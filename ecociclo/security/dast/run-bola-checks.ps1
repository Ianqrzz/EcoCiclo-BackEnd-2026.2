param(
    [string]$BaseUrl = 'http://localhost:8081'
)

$ErrorActionPreference = 'Stop'
$moduleRoot = (Resolve-Path (Join-Path $PSScriptRoot '..\..')).Path
$repoRoot = (Resolve-Path (Join-Path $moduleRoot '..')).Path
$compose = Join-Path $repoRoot 'docker-compose.staging.yml'
$reports = Join-Path $PSScriptRoot 'reports'
New-Item -ItemType Directory -Force -Path $reports | Out-Null
$checks = [System.Collections.Generic.List[object]]::new()
$userA = $null
$userB = $null
$tokenA = $null
$tokenB = $null
$appointmentId = $null
$donationId = $null
$cleanupFailed = $false

function Invoke-Api {
    param(
        [Parameter(Mandatory)][string]$Method,
        [Parameter(Mandatory)][string]$Path,
        [string]$Token,
        [object]$Body
    )

    $headers = @{}
    if ($Token) { $headers.Authorization = "Bearer $Token" }
    $request = @{
        Uri = "$BaseUrl$Path"
        Method = $Method
        Headers = $headers
        TimeoutSec = 15
        UseBasicParsing = $true
        ErrorAction = 'Stop'
    }
    if ($null -ne $Body) {
        $request.ContentType = 'application/json'
        $request.Body = ConvertTo-Json -InputObject $Body -Depth 10 -Compress
    }

    try {
        $response = Invoke-WebRequest @request
        $status = [int]$response.StatusCode
        $content = [string]$response.Content
    } catch {
        $response = $_.Exception.Response
        if ($null -eq $response) { throw }
        $status = [int]$response.StatusCode
        $content = [string]$_.ErrorDetails.Message
        if (-not $content -and $response -isnot [System.Net.Http.HttpResponseMessage]) {
            try {
                $stream = $response.GetResponseStream()
                if ($stream) {
                    $reader = [System.IO.StreamReader]::new($stream)
                    try { $content = $reader.ReadToEnd() } finally { $reader.Dispose() }
                }
            } catch { }
        }
    }

    $json = $null
    if ($content) {
        try { $json = ConvertFrom-Json -InputObject $content -ErrorAction Stop } catch { }
    }
    return [pscustomobject]@{ Status = $status; Json = $json; Content = $content }
}

function Assert-Status {
    param([string]$Name, [object]$Response, [int]$Expected)
    $passed = $Response.Status -eq $Expected
    $checks.Add([pscustomobject]@{ name = $Name; expected = $Expected; actual = $Response.Status; passed = $passed })
    if (-not $passed) {
        throw "${Name}: expected HTTP $Expected, got $($Response.Status). Body: $($Response.Content)"
    }
}

function New-TestUser {
    param([string]$Name, [string]$Email, [string]$Password)
    $cpf = [guid]::NewGuid().ToString('N').Substring(0, 11)
    $created = Invoke-Api -Method POST -Path '/api/usuarios' -Body @{
        nome = $Name; cpf = $cpf; telefone = '0000000000'; email = $Email
        senha = $Password; perfil = 'DOADOR'
    }
    Assert-Status "Register $Name" $created 201
    $login = Invoke-Api -Method POST -Path '/api/usuarios/login' -Body @{ email = $Email; senha = $Password }
    Assert-Status "Login $Name" $login 200
    if (-not $login.Json.token) { throw "Login $Name did not return a token." }
    return [pscustomobject]@{ Id = [string]$created.Json.id; Token = [string]$login.Json.token; Name = $Name }
}

try {
    # Cria usuários temporários para testar acesso entre contas.
    $nonce = [guid]::NewGuid().ToString('N')
    $password = "Bola-Test-$nonce!"
    $oversizedRegistration = Invoke-Api POST '/api/usuarios' -Body @{
        nome = 'N' * 121; cpf = [guid]::NewGuid().ToString('N').Substring(0, 11)
        telefone = '0000000000'; email = "oversized-$nonce@example.invalid"
        senha = $password; perfil = 'ADMINISTRADOR'
    }
    Assert-Status 'Reject oversized registration text' $oversizedRegistration 400
    $sqliLogin = Invoke-Api POST '/api/usuarios/login' -Body @{
        email = "' OR '1'='1' --"; senha = $password
    }
    Assert-Status 'Reject SQL injection login payload' $sqliLogin 401

    $userA = New-TestUser "DAST BOLA A $nonce" "bola-a-$nonce@example.invalid" $password
    $userB = New-TestUser "DAST BOLA B $nonce" "bola-b-$nonce@example.invalid" $password
    $tokenA = $userA.Token
    $tokenB = $userB.Token

    Assert-Status 'Owner can read own profile' (Invoke-Api GET "/api/usuarios/$($userB.Id)" $tokenB) 200
    Assert-Status 'Other user cannot read profile' (Invoke-Api GET "/api/usuarios/$($userB.Id)" $tokenA) 403
    Assert-Status 'Other user cannot update profile' (Invoke-Api PUT "/api/usuarios/$($userB.Id)" $tokenA @{ nome = 'BOLA tamper'; telefone = '1111111111' }) 403
    Assert-Status 'Other user cannot delete profile' (Invoke-Api DELETE "/api/usuarios/$($userB.Id)" $tokenA) 403
    Assert-Status 'User listing is admin-only' (Invoke-Api GET '/api/usuarios' $tokenA) 403

    $addressBody = @{ logradouro = 'Rua de Teste DAST'; bairro = 'Centro'; cidade = 'Irece'; estado = 'BA'; cep = '44900000'; usuarioId = $userB.Id }
    Assert-Status 'Other user cannot create address for B' (Invoke-Api POST '/api/enderecos' $tokenA $addressBody) 403
    $address = Invoke-Api POST '/api/enderecos' $tokenB $addressBody
    Assert-Status 'Owner can create address' $address 201
    $addressId = [string]$address.Json.id
    if ([string]::IsNullOrWhiteSpace($addressId)) { throw "Address creation returned no id. Body: $($address.Content)" }
    if ([string]$address.Json.usuarioId -ne [string]$userB.Id) {
        throw "Address owner mismatch: created for $($userB.Id), response says $($address.Json.usuarioId). Body: $($address.Content)"
    }
    Assert-Status 'Other user cannot read address' (Invoke-Api GET "/api/enderecos/$addressId" $tokenA) 403
    Assert-Status 'Other user cannot update address' (Invoke-Api PUT "/api/enderecos/$addressId" $tokenA @{ logradouro = 'BOLA tamper' }) 403
    Assert-Status 'Other user cannot delete address' (Invoke-Api DELETE "/api/enderecos/$addressId" $tokenA) 403
    Assert-Status 'Owner can read own address' (Invoke-Api GET "/api/enderecos/$addressId" $tokenB) 200
    $oversizedAddress = @{
        logradouro = 'R' * 181; bairro = 'Centro'; cidade = 'Irece'; estado = 'BA'
        cep = '44900000'; usuarioId = $userA.Id
    }
    Assert-Status 'Reject oversized address text' (Invoke-Api POST '/api/enderecos' $tokenA $oversizedAddress) 400

    $appointmentBody = @{
        doadorId = $userB.Id
        enderecoId = $addressId
        dataColeta = (Get-Date).AddDays(1).ToString('yyyy-MM-ddTHH:mm:ss')
        observacoes = 'Registro temporario do teste DAST BOLA'
        doacao = @{ nome = 'Doacao DAST'; quantidade = 1; imagem = ''; peso = 1.0 }
    }
    $appointment = Invoke-Api POST '/api/agendamentos' $tokenB $appointmentBody
    Assert-Status 'Owner can create appointment' $appointment 200
    $appointmentId = [string]$appointment.Json.id
    $donationId = [string]$appointment.Json.doacao.id
    Assert-Status 'Other user cannot cancel appointment' (Invoke-Api PUT "/api/agendamentos/$appointmentId/cancelar" $tokenA) 403
    Assert-Status 'Other user cannot read appointment donation' (Invoke-Api GET "/api/doacoes/$donationId" $tokenA) 403
    Assert-Status 'Owner can read own appointment donation' (Invoke-Api GET "/api/doacoes/$donationId" $tokenB) 200
    Assert-Status 'Owner can cancel own appointment' (Invoke-Api PUT "/api/agendamentos/$appointmentId/cancelar" $tokenB) 204

    $fakeRewardId = [guid]::NewGuid().ToString()
    Assert-Status 'Cannot redeem using another donor ID' (Invoke-Api POST "/api/recompensas/$fakeRewardId/resgatar" $tokenA @{ doadorId = $userB.Id }) 403
    Assert-Status 'Cannot list another donor resgates' (Invoke-Api GET "/api/resgates?doadorId=$($userB.Id)" $tokenA) 403
    Assert-Status 'Regular user cannot create rewards' (Invoke-Api POST '/api/recompensas' $tokenA @{ nome = 'DAST'; quantidade = 1; imagem = ''; descricao = 'test'; custoPontos = 1 }) 403
    Assert-Status 'Public registration cannot create admin' (Invoke-Api POST '/api/usuarios' $tokenA @{ nome = 'DAST Admin'; cpf = [guid]::NewGuid().ToString('N').Substring(0, 11); email = "admin-$nonce@example.invalid"; senha = $password; perfil = 'ADMINISTRADOR' }) 403

} finally {
    # Remove os dados temporários mesmo se algum teste falhar.
    if ($appointmentId -and $donationId) {
        $dockerCommand = Get-Command docker -ErrorAction SilentlyContinue
        $dockerPath = if ($dockerCommand) { $dockerCommand.Source } else { Join-Path $env:LOCALAPPDATA 'Programs\DockerDesktop\resources\bin\docker.exe' }
        if (Test-Path -LiteralPath $dockerPath) {
            $sql = "DELETE FROM tb_agendamentos WHERE id = '$appointmentId'; DELETE FROM tb_doacoes WHERE id = '$donationId';"
            $dbUser = if ($env:STAGING_DB_USER) { $env:STAGING_DB_USER } else { 'ecociclo_staging' }
            $dbName = if ($env:STAGING_DB_NAME) { $env:STAGING_DB_NAME } else { 'ecociclo_staging' }
            & $dockerPath compose -f $compose exec -T staging-db psql -U $dbUser -d $dbName -v ON_ERROR_STOP=1 -c $sql | Out-Null
            if ($LASTEXITCODE -ne 0) { $cleanupFailed = $true }
        } else {
            $cleanupFailed = $true
        }
    }

    foreach ($user in @($userB, $userA)) {
        if ($user -and $user.Token) {
            try { $null = Invoke-Api DELETE "/api/usuarios/$($user.Id)" $user.Token } catch { $cleanupFailed = $true }
        }
    }

    if ($cleanupFailed) {
        Write-Warning 'Could not fully clean the disposable BOLA test data from staging.'
    }
    $reportPath = Join-Path $reports 'bola-report.json'
    $checks | ConvertTo-Json -Depth 5 | Set-Content -Encoding utf8 $reportPath
}

if ($checks.Count -eq 0 -or @($checks | Where-Object { -not $_.passed }).Count -gt 0) {
    throw "BOLA checks failed. Review $reportPath"
}
Write-Host "BOLA checks passed: $($checks.Count). Report: $reportPath"
