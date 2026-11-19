# Processo de Trabalho — EcoCiclo Back-End

Este documento define **como a equipe registra e executa toda manutenção e análise de qualidade** do código. Todo o trabalho é centralizado no **GitHub**. Nada é feito "por fora": se não tem issue, não existe; se não passou por Pull Request, não entra na `main`.

---

## 1. Visão geral: a cadeia de rastreabilidade

Cada mudança no código percorre a mesma cadeia. Assim, qualquer pessoa da equipe (ou o professor) consegue partir de uma linha de código e chegar ao motivo dela, ou partir de um problema e chegar à solução.

```
 ACHADO            REGISTRO           EXECUÇÃO            REVISÃO              ENTREGA
───────────     ──────────────     ──────────────     ─────────────────     ─────────────
Análise do  ──▶  Issue no      ──▶  Branch própria ──▶  Pull Request     ──▶  Merge na main
código           GitHub             + commits           + CI + revisão        + Release da
(manual ou       (template,         padronizados        de 1 colega           sprint
 ferramenta)      labels,
                  responsável)
```

| Etapa | Onde fica | Quem faz | Resultado |
|---|---|---|---|
| 1. Achado | Análise manual, SonarCloud, testes | Qualquer membro | Problema identificado |
| 2. Registro | **Issue** | Quem encontrou | Problema documentado, classificado e atribuído |
| 3. Planejamento | **GitHub Projects** + **Milestone** | Equipe (na reunião da sprint) | Issue entra numa sprint com responsável |
| 4. Execução | **Branch** + **commits** | Responsável da issue | Código alterado |
| 5. Revisão | **Pull Request** | Responsável abre, 1 colega revisa | Mudança aprovada e verificada |
| 6. Entrega | **Merge** + **Release** | Responsável faz o merge; equipe fecha a sprint | Issue fechada, histórico registrado |

---

## 2. Ferramentas do GitHub que usamos

| Ferramenta | Para quê |
|---|---|
| **Issues** | Registrar cada problema, melhoria ou tarefa. É a unidade de trabalho. |
| **Labels** | Classificar a issue (tipo de manutenção, gravidade, área). |
| **Milestones** | Uma por sprint. Agrupa as issues e mostra o progresso. |
| **GitHub Projects** | Quadro (kanban) da equipe. Substitui o Trello. |
| **Pull Requests** | Registrar a solução, a evidência e a revisão. |
| **Actions (CI)** | Rodar build, testes e análise de qualidade automaticamente em cada PR. |
| **Releases / Tags** | Marcar o estado do código no fim de cada sprint. |
| **Pasta `docs/`** | Documentação permanente: processo, backlog, decisões, relatórios de sprint. |

---

## 3. Etapa 1 e 2 — Registrar o achado (Issue)

1. Antes de abrir, procure se já existe issue sobre o mesmo problema.
2. Abra a issue usando o template **"Relato de Qualidade ou Segurança"** (`.github/ISSUE_TEMPLATE/analise_codigo.md`).
3. Preencha **todos** os campos: tipo, localização (arquivo e linhas), descrição, gravidade, evidência e proposta de correção.
4. Aplique as labels (seção 4).
5. **Atribua um responsável** (`Assignees`) — ou deixe sem responsável e leve para a reunião da sprint.
6. Vincule à Milestone da sprint, se já estiver planejada.

> **Regra:** uma issue = um problema. Se a análise encontrar três problemas, abra três issues.

### Título da issue

Use o prefixo do template e seja específico:

- ✅ `[ANÁLISE] Expiração do JWT usa fuso -03:00 fixo e gera token de 5h no Docker`
- ❌ `[ANÁLISE] Problema no token`

---

## 4. Labels

Toda issue deve ter **uma label de cada grupo**.

### Tipo de manutenção (ISO/IEC 14764)

| Label | Quando usar | Exemplo |
|---|---|---|
| `manutencao:corretiva` | Corrige um defeito existente | Bug na expiração do JWT |
| `manutencao:adaptativa` | Adapta o sistema a mudança de ambiente/tecnologia | Migração do Spring Boot |
| `manutencao:perfectiva` | Melhora qualidade, legibilidade ou desempenho sem mudar comportamento esperado | Padronizar respostas de erro |
| `manutencao:preventiva` | Evita problemas futuros | Restringir rotas, adicionar testes |

### Gravidade (igual ao template da issue)

`gravidade:baixa` · `gravidade:media` · `gravidade:alta` · `gravidade:critica`

### Área

`area:seguranca` · `area:arquitetura` · `area:testes` · `area:infra` · `area:documentacao`

### Status auxiliar

- `precisa-revisao` — issue recém-aberta, ainda não validada pela equipe (já vem do template).
- `bloqueada` — depende de outra issue. Escreva no comentário qual (`Depende de #12`).

---

## 5. Etapa 3 — Planejamento (Projects + Milestone)

- Cada sprint tem uma **Milestone** com data de término (ex.: `Sprint 1 — 27/10 a 08/11`).
- O quadro do **GitHub Projects** tem as colunas:

| Coluna | Significado |
|---|---|
| **Backlog** | Issue registrada, ainda sem sprint |
| **A Fazer** | Planejada para a sprint atual, com responsável |
| **Em Andamento** | Responsável já criou a branch e está trabalhando |
| **Em Revisão** | PR aberto aguardando revisão |
| **Concluído** | PR aprovado e mergeado (issue fechada automaticamente) |

- Na reunião de início da sprint, a equipe move issues do Backlog para "A Fazer" e **cada uma recebe um responsável**.
- O registro resumido das atribuições fica em [`docs/BACKLOG.md`](BACKLOG.md), mas **a fonte da verdade é a issue no GitHub**.

---

## 6. Etapa 4 — Execução (Branch e Commits)

### Branch

Sempre a partir da `main` atualizada:

```bash
git checkout main
git pull
git checkout -b <tipo>/<numero-da-issue>-<descricao-curta>
```

| Prefixo | Uso |
|---|---|
| `fix/` | Manutenção corretiva |
| `feat/` | Nova funcionalidade |
| `refactor/` | Manutenção perfectiva |
| `chore/` | Infra, dependências, configuração (inclui migração) |
| `test/` | Apenas testes |
| `docs/` | Apenas documentação |

Exemplos: `fix/12-jwt-expiracao-utc`, `chore/10-migracao-spring-boot`, `refactor/14-global-exception-handler`.

### Commits — padrão Conventional Commits

```
<tipo>: <descrição no imperativo>  (#numero-da-issue)
```

Exemplos:

```
fix: calcula expiração do JWT com Instant em UTC (#12)
chore: atualiza Spring Boot para 4.x (#10)
refactor: substitui RuntimeException por exceções de domínio (#14)
test: adiciona teste de expiração do token (#12)
```

- Commits pequenos e com um propósito só.
- **Nunca** commite `.env`, senhas ou chaves.
- **Nunca** faça push direto na `main`.

---

## 7. Etapa 5 — Revisão (Pull Request)

1. Abra o PR da sua branch para a `main`.
2. Preencha o template de PR (seção 7.1).
3. Na descrição, escreva `Closes #<numero>` — a issue fecha sozinha no merge.
4. Peça revisão de **pelo menos 1 colega** (`Reviewers`).
5. Aguarde o CI ficar verde.
6. Responda os comentários do revisor com novos commits (não apague o histórico).
7. Após aprovação + CI verde, o **autor** faz o merge (preferência: *Squash and merge*).

### 7.1 Conteúdo obrigatório do PR

```markdown
## Issue
Closes #

## Problema
O que estava errado e qual o impacto.

## Causa
Por que acontecia (causa raiz, não o sintoma).

## Solução
O que foi alterado e por quê desta forma.

## Evidência (antes × depois)
- Teste automatizado adicionado/alterado:
- Print/resultado do Postman:
- Métrica do SonarCloud (se aplicável):

## Riscos e impacto
O que pode quebrar? Algum outro módulo afetado?

## Checklist
- [ ] Build e testes passando localmente
- [ ] Testes novos cobrindo a mudança
- [ ] Sem segredos/credenciais no código
- [ ] Documentação atualizada (se necessário)
```

### 7.2 O que o revisor verifica

- A mudança resolve **exatamente** o que a issue descreve (nem mais, nem menos).
- Existe evidência de que funciona (teste ou print).
- O código segue o padrão do projeto e não introduz novos problemas.
- Não há credenciais, `System.out.println` esquecido ou código comentado.

> O revisor **comenta no PR**, não no WhatsApp. Toda discussão técnica precisa ficar registrada.

---

## 8. Etapa 6 — Entrega (Merge e Release)

- Após o merge, apague a branch.
- A issue é fechada automaticamente e o card vai para **Concluído**.
- No fim da sprint, a equipe:
  1. Cria uma **tag e Release** (ex.: `v0.1.0 — Sprint 1`) com o resumo das issues entregues.
  2. Escreve o relatório `docs/sprints/sprint-XX.md` (seção 10).
  3. Move issues não concluídas para a próxima Milestone, com comentário explicando o motivo.

---

## 9. Análise de qualidade automática (CI)

Todo PR dispara o workflow do GitHub Actions, que executa:

| Verificação | Ferramenta | Bloqueia o merge? |
|---|---|---|
| Build e testes | Maven (`./mvnw verify`) | Sim |
| Cobertura de testes | JaCoCo | Não (apenas registra) |
| Bugs, vulnerabilidades e code smells | SonarCloud | Sim, se piorar o Quality Gate |
| Vulnerabilidades em dependências | Dependabot / OWASP Dependency-Check | Não (gera issue/alerta) |

### Baseline

Antes da primeira correção, registramos o estado original do código:

- Tag `v0.0-baseline` no commit atual da `main`.
- Relatório inicial do SonarCloud salvo em `docs/qualidade/baseline.md`.

Toda melhoria de qualidade é comparada com esse ponto de partida.

---

## 10. Documentação permanente (`docs/`)

```
docs/
├── PROCESSO_DE_TRABALHO.md   ← este documento
├── BACKLOG.md                ← resumo das tarefas e responsáveis por sprint
├── adr/                      ← decisões técnicas (Architecture Decision Records)
│   └── 0001-migracao-spring-boot-4.md
├── qualidade/
│   └── baseline.md           ← métricas iniciais
└── sprints/
    └── sprint-01.md          ← relatório de cada sprint
```

### ADR — quando escrever

Sempre que a equipe tomar uma decisão técnica com alternativas (ex.: versão do Spring Boot, formato de resposta de erro). Modelo curto:

```markdown
# ADR 0001 — <título>
**Data:** dd/mm/aaaa  **Status:** aceita
## Contexto
## Decisão
## Alternativas consideradas
## Consequências
```

### Relatório de sprint — conteúdo

- Período e Milestone.
- Issues planejadas × entregues (com links).
- Métricas antes × depois (SonarCloud, cobertura).
- Problemas encontrados e o que fica para a próxima sprint.

---

## 11. Regras de ouro

1. **Sem issue, sem trabalho.** Toda mudança começa por uma issue.
2. **Sem PR, sem merge.** Ninguém faz push direto na `main`.
3. **Toda issue tem um dono.** Issue sem responsável não entra na sprint.
4. **Toda discussão técnica fica no GitHub**, em comentários da issue ou do PR.
5. **Toda correção tem evidência.** Teste automatizado sempre que possível.
6. **Uma issue, uma branch, um PR.**
