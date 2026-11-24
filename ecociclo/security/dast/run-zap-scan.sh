#!/usr/bin/env bash
set -euo pipefail
# Inicia a API e executa a varredura do ZAP.
module_root="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
repo_root="$(cd "$module_root/.." && pwd)"
compose="$repo_root/docker-compose.staging.yml"
reports="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)/reports"
mkdir -p "$reports"

docker compose -f "$compose" up -d --build staging-db app
port="${STAGING_APP_PORT:-8081}"
healthy=false
for _ in $(seq 1 60); do
  if curl --fail --silent "http://localhost:${port}/actuator/health" | grep -q '"status":"UP"'; then
    healthy=true
    break
  fi
  sleep 3
done
if [ "$healthy" != true ]; then
  echo "API staging não ficou saudável na porta $port" >&2
  exit 1
fi

if docker compose -f "$compose" --profile dast run --rm zap; then
  scan_exit_code=0
else
  scan_exit_code=$?
fi
if [ "$scan_exit_code" -eq 3 ]; then
  echo "OWASP ZAP falhou ao executar. Consulte a saída acima." >&2
  exit 3
fi
report="$reports/zap-report.json"
if [ ! -f "$report" ]; then
  echo "OWASP ZAP não gerou zap-report.json (código $scan_exit_code)." >&2
  exit 3
fi
if [ "$scan_exit_code" -eq 1 ]; then
  echo "OWASP ZAP encontrou achados configurados como FAIL. Consulte $reports" >&2
  exit 1
fi
if grep -Eq '"riskcode"[[:space:]]*:[[:space:]]*"[23]"' "$report"; then
  echo "OWASP ZAP encontrou risco médio/alto. Consulte $reports" >&2
  exit 1
fi
if [ "$scan_exit_code" -ne 0 ] && [ "$scan_exit_code" -ne 2 ]; then
  echo "OWASP ZAP terminou com código inesperado $scan_exit_code." >&2
  exit "$scan_exit_code"
fi
if grep -q '"riskcode"' "$report"; then
  echo "Nenhum achado médio/alto; alertas baixo/informativos permanecem no relatório." >&2
fi
echo "Varredura sem achados médios/altos. Relatórios em: $reports"
