# Backlog — EcoCiclo Back-End

Registro resumido das tarefas por sprint e de quem é responsável por cada uma.

> **A fonte da verdade é o GitHub.** Cada linha aqui corresponde a uma issue. Ao abrir a issue, preencha a coluna **Issue** com o número (`#12`). Ao atribuir alguém, atualize a coluna **Responsável** com o usuário do GitHub (`@usuario`) e também o campo *Assignees* da issue. O processo completo está em [PROCESSO_DE_TRABALHO.md](PROCESSO_DE_TRABALHO.md).

**Legenda de status:** ⬜ A fazer · 🟨 Em andamento · 🟦 Em revisão · ✅ Concluído · ⛔ Bloqueado

---

## Equipe

| Membro | Usuário GitHub |
|---|---|
| _nome_ | `@Ianqrzz` |
| _nome_ | `@` |
| _nome_ | `@` |
| _nome_ | `@` |

---

## Sprint 1 — 27/10/2026 a 08/11/2026

**Foco:** Setup, Tratamento de Exceções e Segurança Crítica.

### Tarefas

| ID | Tarefa | Tipo de manutenção | Gravidade | Depende de | Responsável | Issue | Status |
|---|---|---|---|---|---|---|---|
| S1-01 | Configurar o GitHub: labels, Milestone da Sprint 1, quadro do Projects, template de PR e proteção da `main` | — (setup) | — | — | | [#2](https://github.com/Ianqrzz/EcoCiclo-BackEnd-2026.2/issues/2) | 🟨 |
| S1-02 | Registrar baseline: tag `v0.0-baseline` e relatório inicial do SonarCloud em `docs/qualidade/baseline.md` | — (setup) | — | S1-01 | | [#3](https://github.com/Ianqrzz/EcoCiclo-BackEnd-2026.2/issues/3) | ⬜ |
| S1-03 | Criar workflow de CI (build, testes, JaCoCo, SonarCloud) | preventiva | média | S1-01 | | [#4](https://github.com/Ianqrzz/EcoCiclo-BackEnd-2026.2/issues/4) | ⬜ |
| S1-04 | Migrar Spring Boot 3.5.4 → 4.x e ajustar o que quebrar (Security 7, Jackson 3, starters) + escrever ADR 0001 | adaptativa | alta | S1-02 | | [#5](https://github.com/Ianqrzz/EcoCiclo-BackEnd-2026.2/issues/5) | ⬜ |
| S1-05 | Corrigir expiração do JWT: usar `Instant.now()` (UTC) em vez de `LocalDateTime` + offset `-03:00` fixo; mover duração para `application.properties` | corretiva | alta | S1-04 | | [#6](https://github.com/Ianqrzz/EcoCiclo-BackEnd-2026.2/issues/6) | ⬜ |
| S1-06 | Remover chave JWT padrão do código e exigir segredo forte (≥ 32 bytes) via variável de ambiente | corretiva | alta | S1-04 | | [#7](https://github.com/Ianqrzz/EcoCiclo-BackEnd-2026.2/issues/7) | ⬜ |
| S1-07 | Reativar o `SecurityFilter` e trocar `anyRequest().permitAll()` por `authenticated()`, liberando só login e cadastro | corretiva | crítica | S1-05 | | [#8](https://github.com/Ianqrzz/EcoCiclo-BackEnd-2026.2/issues/8) | ⬜ |
| S1-08 | Restringir rotas por perfil (`ADMINISTRADOR`, `COLETOR`, `DOADOR`) conforme regras de negócio | preventiva | alta | S1-07 | | [#9](https://github.com/Ianqrzz/EcoCiclo-BackEnd-2026.2/issues/9) | ⬜ |
| S1-09 | Criar exceções de domínio (`RecursoNaoEncontradoException`, `RegraNegocioException`) e o `@RestControllerAdvice` global com resposta de erro padronizada | perfectiva | média | S1-04 | | [#10](https://github.com/Ianqrzz/EcoCiclo-BackEnd-2026.2/issues/10) | ⬜ |
| S1-10 | Substituir os `throw new RuntimeException` e `orElseThrow()` sem argumento dos services pelas exceções de domínio | perfectiva | média | S1-09 | | [#11](https://github.com/Ianqrzz/EcoCiclo-BackEnd-2026.2/issues/11) | ⬜ |
| S1-11 | Tratar 401/403 do Spring Security em JSON no mesmo padrão do handler global; remover o `try/catch` do login | perfectiva | média | S1-07, S1-09 | | [#12](https://github.com/Ianqrzz/EcoCiclo-BackEnd-2026.2/issues/12) | ⬜ |
| S1-12 | Testes automatizados: expiração do token, rotas protegidas (401/403) e códigos HTTP do handler (404/400) | preventiva | média | S1-05, S1-08, S1-10 | | [#13](https://github.com/Ianqrzz/EcoCiclo-BackEnd-2026.2/issues/13) | ⬜ |
| S1-13 | Escrever relatório da sprint (`docs/sprints/sprint-01.md`) e criar Release `v0.1.0` | — (documentação) | — | todas | | [#14](https://github.com/Ianqrzz/EcoCiclo-BackEnd-2026.2/issues/14) | ⬜ |

### Ordem de execução sugerida

```
S1-01 ──▶ S1-02 ──▶ S1-04 (migração) ──┬──▶ S1-05 ─┬─▶ S1-07 ──▶ S1-08 ──┐
   │                                   ├──▶ S1-06  │                     ├──▶ S1-12 ──▶ S1-13
   └──▶ S1-03 (CI)                     └──▶ S1-09 ──┴──▶ S1-10 / S1-11 ───┘
```

- **S1-01 e S1-03** podem ser feitas em paralelo por pessoas diferentes.
- Depois da migração (**S1-04**), três frentes podem andar ao mesmo tempo: **JWT** (S1-05/06), **Exceções** (S1-09/10) e, em seguida, **Rotas** (S1-07/08).
- Para evitar conflitos de merge, quem pegar **S1-07/S1-08** e quem pegar **S1-11** devem combinar, pois ambos alteram `SecurityConfig.java`.

### Sugestão de divisão (4 pessoas)

| Frente | Tarefas |
|---|---|
| Setup e Qualidade | S1-01, S1-02, S1-03, S1-13 |
| Migração | S1-04 |
| Segurança | S1-05, S1-06, S1-07, S1-08 |
| Exceções e Testes | S1-09, S1-10, S1-11, S1-12 |

---

## Próximas sprints

_Adicionar aqui as tarefas das próximas sprints conforme o planejamento estratégico, seguindo o mesmo formato._

## Itens sem sprint definida

Achados registrados fora do escopo atual. Devem virar issue com a label `precisa-revisao`.

| ID | Tarefa | Tipo de manutenção | Gravidade | Responsável | Issue | Status |
|---|---|---|---|---|---|---|
| BL-01 | Conferir senha do admin em `data.sql`: o comentário diz `Admin@123`, mas o hash aparenta ser de outra senha | corretiva | média | | [#15](https://github.com/Ianqrzz/EcoCiclo-BackEnd-2026.2/issues/15) | ⬜ |
| BL-02 | Adicionar Bean Validation (`@NotBlank`, `@Email` etc.) nos DTOs de request e `@Valid` nos controllers | preventiva | média | | [#16](https://github.com/Ianqrzz/EcoCiclo-BackEnd-2026.2/issues/16) | ⬜ |
| BL-03 | Corrigir `recuperarToken` do `SecurityFilter` para aceitar apenas cabeçalho no formato `Bearer <token>` | corretiva | baixa | | [#17](https://github.com/Ianqrzz/EcoCiclo-BackEnd-2026.2/issues/17) | ⬜ |
