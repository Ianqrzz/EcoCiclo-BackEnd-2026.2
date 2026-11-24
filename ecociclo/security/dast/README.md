# OWASP ZAP DAST em Staging

O ambiente usa `docker-compose.staging.yml` e um volume PostgreSQL independente (`staging_postgres_data`). Ele não reutiliza o banco do `docker-compose.yml`. A especificação OpenAPI mínima em `openapi.yaml` mapeia os principais pontos de entrada porque o projeto ainda não publica `/v3/api-docs`.

## Windows (PowerShell)

Pré-requisitos: Docker Desktop iniciado. Na raiz do repositório:

```powershell
$env:STAGING_DB_PASSWORD = 'escolha-uma-senha-local'
$env:STAGING_JWT_SECRET = 'troque-por-um-segredo-local-com-mais-de-32-caracteres'
docker compose -f .\docker-compose.staging.yml up -d --build staging-db app
.\ecociclo\security\dast\run-zap-scan.ps1
```

O script espera `/actuator/health` ficar `UP`, executa verificações autenticadas com duas contas descartáveis (incluindo autorização BOLA, limites de texto e uma tentativa de SQL Injection no login) e depois executa `zap-api-scan.py`. O cadastro de contas fica fora da especificação ativa do ZAP para evitar que o scanner crie usuários persistentes; os casos de autorização e validação do cadastro são exercitados pelo pré-teste e os registros são removidos. Os resultados vão para `bola-report.json`; o ZAP grava HTML e JSON. As regras XSS refletido/persistente e SQLi listadas em `zap-rules.tsv` são `FAIL`; a regra 40026 de XSS baseado no DOM fica `IGNORE` porque analisa execução no navegador cliente, fora do escopo desta especificação REST. Os demais alertas permanecem visíveis como `WARN`. O script lê os códigos de risco no JSON e falha em `FAIL` ou risco médio/alto, permitindo alertas baixos sem esconder esses achados do relatório. O parâmetro `-l WARN` controla a exibição, não o nível de risco. Relatórios são ignorados pelo Git.

Para desligar preservando o volume:

```powershell
docker compose -f .\docker-compose.staging.yml down
```

Para apagar também os dados isolados de staging:

```powershell
docker compose -f .\docker-compose.staging.yml down -v
```

## Linux/Bash

Na raiz do repositório:

```bash
export STAGING_DB_PASSWORD='escolha-uma-senha-local'
export STAGING_JWT_SECRET='troque-por-um-segredo-local-com-mais-de-32-caracteres'
docker compose -f docker-compose.staging.yml up -d --build staging-db app
bash ecociclo/security/dast/run-zap-scan.sh
```

O ZAP roda na mesma rede Docker e ataca `app:8080`; a espera local usa a porta publicada (padrão `8081`). Para incluir novos endpoints, atualize `openapi.yaml`. A regra 40026 é ignorada pelo escopo do scanner; outras exceções só devem ser adicionadas após revisar evidência concreta.

## Limitações conhecidas

- O scan ativo do ZAP não usa contexto JWT, mas o pré-teste BOLA executa verificações autenticadas com duas contas temporárias em perfis, endereços, agendamentos, doações, recompensas e resgates. As contas e os registros sintéticos do agendamento são removidos ao final.
- DAST não prova ausência de todas as falhas BOLA, SQLi ou XSS. Revise os relatórios e complemente com testes manuais de fluxos específicos antes de declarar o KPI de zero vulnerabilidades.
- Não coloque credenciais reais em variáveis, especificação OpenAPI, relatórios ou issues.
