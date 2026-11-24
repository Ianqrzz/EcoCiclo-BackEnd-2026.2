$ErrorActionPreference = 'Stop'
# Inicia a API, confere os acessos e executa a varredura do ZAP.
$moduleRoot = (Resolve-Path (Join-Path $PSScriptRoot '..\..')).Path
$repoRoot = (Resolve-Path (Join-Path $moduleRoot '..')).Path
$compose = Join-Path $repoRoot 'docker-compose.staging.yml'
$reports = Join-Path $PSScriptRoot 'reports'
New-Item -ItemType Directory -Force -Path $reports | Out-Null

docker compose -f $compose up -d --build staging-db app
if ($LASTEXITCODE -ne 0) { throw 'Falha ao iniciar o ambiente staging.' }

$port = if ($env:STAGING_APP_PORT) { $env:STAGING_APP_PORT } else { '8081' }
$healthUrl = "http://localhost:$port/actuator/health"
$healthy = $false
for ($attempt = 0; $attempt -lt 60; $attempt++) {
    try {
        $health = Invoke-RestMethod -Uri $healthUrl -TimeoutSec 3
        if ($health.status -eq 'UP') { $healthy = $true; break }
    } catch { }
    Start-Sleep -Seconds 3
}
if (-not $healthy) { throw "API de staging não ficou saudável em $healthUrl" }

& (Join-Path $PSScriptRoot 'run-bola-checks.ps1') -BaseUrl "http://localhost:$port"

docker compose -f $compose --profile dast run --rm zap
$scanExitCode = $LASTEXITCODE
if ($scanExitCode -eq 3) { throw 'OWASP ZAP falhou ao executar. Consulte a saída acima.' }
if (-not (Test-Path -LiteralPath (Join-Path $reports 'zap-report.json'))) {
    throw "OWASP ZAP não gerou zap-report.json (código $scanExitCode). Consulte a saída acima."
}

$report = Get-Content -Raw (Join-Path $reports 'zap-report.json') | ConvertFrom-Json
$alerts = @($report.site | ForEach-Object { $_.alerts })
$mediumOrHigher = @($alerts | Where-Object { [int]$_.riskcode -ge 2 })
if ($scanExitCode -eq 1 -or $mediumOrHigher.Count -gt 0) {
    throw "OWASP ZAP encontrou FAIL ou risco médio/alto. Consulte $reports"
}
if ($scanExitCode -notin @(0, 2)) {
    throw "OWASP ZAP terminou com código inesperado $scanExitCode. Consulte a saída acima."
}
if ($alerts.Count -gt 0) {
    $lowAlertTypes = @($alerts | Where-Object { [int]$_.riskcode -eq 1 })
    $infoAlertTypes = @($alerts | Where-Object { [int]$_.riskcode -eq 0 })
    Write-Warning "No medium/high findings; $($lowAlertTypes.Count) low and $($infoAlertTypes.Count) informational alert type(s) remain in the report."
}
Write-Host "Scan completed without medium/high findings. Reports: $reports"
