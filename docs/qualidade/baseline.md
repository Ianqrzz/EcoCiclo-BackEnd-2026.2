# Baseline de Qualidade — `v0.0-baseline`

Este documento registra o estado do código **antes de qualquer correção** da Sprint 1. As análises das sprints seguintes são comparadas com estes números.

| Item | Valor |
|---|---|
| **Data da análise** | 19/11/2026, 19:44 (horário de Brasília) |
| **Tag** | `v0.0-baseline` |
| **Commit analisado** | `9593f11` (merge do PR #18: só documentação, sem alteração de código desde `cf17c79`) |
| **Ferramenta** | SonarQube Cloud |
| **Painel do projeto** | https://sonarcloud.io/project/overview?id=Ianqrzz_EcoCiclo-BackEnd-2026.2 |
| **Quality Profile (Java)** | Sonar way comprehensive (670 regras ativas) |
| **Quality Gate** | Sonar way (padrão) |
| **Responsável** | `@Ianqrzz` |
| **Issue** | #3 (S1-02) |

---

## 1. Métricas gerais (Overall Code)

| Métrica | Nome antigo | Quantidade | Nota (A–E) |
|---|---|---|---|
| **Reliability** | Bugs (0 no modelo antigo, ver seção 5) | 7 | A |
| **Security** | Vulnerabilities | 5 | D |
| **Maintainability** | Code Smells | 53 | A |
| **Security Hotspots** | — | 0 (ver seção 5) | — |
| **Total de issues** | — | **58** | — |
| **Dívida técnica (Maintainability)** | Technical Debt | 1d 3h (683 min) | — |
| **Esforço total estimado (todas as issues)** | — | ~3d 3h (1.648 min) | — |
| **Duplicação** | — | **0,0%** | — |
| **Cobertura de testes** | — | **0,0%** (não medida) | — |
| **Linhas de código** | — | **2.289** | — |
| **Quality Gate** | — | **Not computed** (primeira análise) | — |

> O SonarQube Cloud considera um dia de trabalho como 8 horas.

### Issues por severidade

| Severidade | Quantidade |
|---|---|
| Blocker | 0 |
| High | 7 |
| Medium | 46 |
| Low | 1 |
| Info | 4 |
| **Total** | **58** |

---

## 2. Principais achados do SonarQube Cloud

As 58 issues do SonarQube Cloud estão agrupadas abaixo por **regra** (10 regras), ordenadas da maior para a menor severidade. Todo achado está vinculado a uma issue do GitHub. Os dados foram extraídos da API pública do SonarQube Cloud (`api/issues/search`).

| # | Regra / problema | Qualidade | Severidade | Qtd. | Arquivo:linha | Issue no GitHub |
|---|---|---|---|---|---|---|
| 1 | `java:S4502`: proteção CSRF desabilitada | Security | HIGH | 1 | `SecurityConfig.java:30` | Relacionado à #8: seguro em API stateless com JWT no cabeçalho; será marcado como *Accepted* após a #8 |
| 2 | `java:S1192`: mensagem de erro repetida em vez de constante | Maintainability | HIGH | 3 | `AgendamentoService.java:69` ("Agendamento não encontrado", 3×); `RecompensaService.java:57` ("Recompensa não encontrada com o ID: ", 4×); `UsuarioService.java:105` ("Usuário não encontrado.", 4×) | #11 |
| 3 | `java:S1452`: retorno com tipo genérico `ResponseEntity<?>` | Maintainability | HIGH | 2 | `UsuarioController.java:32` (login), `:69` (`/me`) | #12  |
| 4 | `java:S6809`: método `@Transactional` chamado via `this` | Maintainability | HIGH | 1 | `ResgateService.java:58` | #23 |
| 5 | `java:S112`: exceção genérica (`RuntimeException`) | Maintainability | MEDIUM | 25 | `RecompensaService` (12), `AvaliacaoService` (7), `AgendamentoService` (3), `EnderecoService` (2), `UsuarioService` (1) | #11 |
| 6 | `java:S5122`: CORS liberado para qualquer origem (`@CrossOrigin("*")`) | Security | MEDIUM | 4 | `AgendamentoController.java:19`, `AssociacaoController.java:18`, `AvaliacaoController.java:17`, `ResgateController.java:16` | #20 |
| 7 | `java:S6813`: injeção de dependência por campo (`@Autowired`) | Maintainability + Reliability | MEDIUM | 3 | `SecurityFilter.java:20`, `:23`; `AutenticacaoService.java:13` | #21 |
| 8 | `java:S6204`: `collect(Collectors.toList())` em vez de `Stream.toList()` | Maintainability | MEDIUM | 14 | 8 services (ver #22) | #22 |
| 9 | `java:S6201`: `instanceof` seguido de cast | Maintainability | LOW | 1 | `UsuarioController.java:73` (`/me`) | #12  |
| 10 | `java:S8688`: `.now()` sem fuso horário (`ZoneId`/`Clock`) | Reliability + Maintainability | INFO | 4 | `TokenService.java:50`; `AgendamentoService.java:57`, `AvaliacaoService.java:39`, `RecompensaService.java:127` | #6 (TokenService) + #19 (demais) |
| | **Total** | | | **58** | | |

### Resumo por issue do GitHub

| Issue | Regras do Sonar cobertas | Ocorrências |
|---|---|---|
| #11 | `S112`, `S1192` | 28 |
| #22 | `S6204` | 14 |
| #20 | `S5122` | 4 |
| #19 | `S8688` (exceto TokenService) | 3 |
| #12 | `S1452`, `S6201` | 3 |
| #21 | `S6813` | 3 |
| #6 | `S8688` (TokenService) | 1 |
| #8 | `S4502` | 1 |
| #23 | `S6809` | 1 |
| **Total** | | **58** |

Resolver a **#11** sozinha elimina quase metade das issues do Sonar (28 de 58).

---

## 3. Achados da análise manual

Problemas identificados na leitura do código, comparados com o que o SonarQube Cloud detectou:

| Achado | Arquivo | Gravidade | Detectado pelo Sonar? | Issue |
|---|---|---|---|---|
| Todas as rotas públicas (`anyRequest().permitAll()`) e `SecurityFilter` desativado | `SecurityConfig.java` | Crítica | ❌ Não | #8 |
| Rotas sem restrição por perfil | `SecurityConfig.java` | Alta | ❌ Não | #9 |
| Expiração do JWT com fuso `-03:00` fixo (token de 5h no Docker) | `TokenService.java:50` | Alta | ⚠️ Parcial: só como aviso `INFO` (`S8688`), sem apontar o impacto de segurança | #6 |
| Chave JWT padrão no código-fonte | `TokenService.java:19` | Alta | ❌ Não | #7 |
| Spring Boot 3.5.4 fora do suporte OSS | `pom.xml:11` | Alta | ❌ Não | #5 |
| Sem `@RestControllerAdvice`: toda exceção vira HTTP 500 | — | Média | ❌ Não | #10 |
| 25 `RuntimeException` genéricas e `orElseThrow()` sem argumento | services | Média | ✅ Sim (`S112`, 25 ocorrências; `S1192`) | #11 |
| 401/403 sem padrão e `try/catch` genérico no login | `UsuarioController.java:33` | Média | ⚠️ Parcial (`S1452`, `S6201`) | #12 |
| Sem CI e sem medição de cobertura | — | Média | ❌ Não (cobertura aparece apenas como 0,0%) | #4 |
| Apenas 1 teste (`contextLoads`) | `src/test` | Média | ❌ Não | #13 |
| Senha do admin no `data.sql` não confere com o comentário | `data.sql` | Média | ❌ Não (arquivo não analisado) | #15 |
| DTOs sem Bean Validation | DTOs de request | Média | ❌ Não | #16 |
| `recuperarToken` não valida o prefixo `Bearer` | `SecurityFilter.java` | Baixa | ❌ Não | #17 |
| **Projeto não compila em JDK 23+** (Lombok sem `annotationProcessorPaths`) | `pom.xml` | Média | ❌ Não | #24 |

### Achados que só o SonarQube Cloud encontrou

| Achado | Regra | Issue |
|---|---|---|
| `@CrossOrigin("*")` nos controllers sobrepõe a política de CORS | `S5122` | #20 |
| Datas gravadas com `LocalDateTime.now()` ficam 3h adiantadas no Docker | `S8688` | #19 |
| Método `@Transactional` chamado via `this` | `S6809` | #23 |
| Injeção de dependência por campo | `S6813` | #21 |
| `Collectors.toList()` em vez de `Stream.toList()` | `S6204` | #22 |

### Conclusão da comparação

As duas análises se complementam. O SonarQube Cloud foi eficaz em **padrões de código** (exceções genéricas, duplicação de literais, uso de APIs), mas **não detectou os problemas de segurança mais graves**, que dependem de entender a regra de negócio e a configuração (rotas liberadas, chave JWT no código, expiração do token). Também não analisa arquivos fora do código Java (seção 5). Por outro lado, encontrou 5 problemas que passaram despercebidos na leitura manual.

---

## 4. Como a análise foi executada

Executado na pasta `ecociclo/`, com o código na tag `v0.0-baseline` e o token em `$env:SONAR_TOKEN`:

```powershell
.\mvnw.cmd clean verify org.sonarsource.scanner.maven:sonar-maven-plugin:sonar -DskipTests "-Dmaven.compiler.proc=full" "-Dsonar.host.url=https://sonarcloud.io" "-Dsonar.organization=ianqrzz" "-Dsonar.projectKey=Ianqrzz_EcoCiclo-BackEnd-2026.2"
```

| Parâmetro | Motivo |
|---|---|
| `org.sonarsource.scanner.maven:sonar-maven-plugin:sonar` | O plugin do Sonar não está declarado no `pom.xml`; chamado pelo nome completo para não alterar o código da baseline |
| `-DskipTests` | O único teste (`contextLoads`) depende do PostgreSQL em execução |
| `-Dmaven.compiler.proc=full` | A partir do JDK 23 o processamento de anotações não é implícito; sem isso o Lombok não roda e o projeto não compila |
| Automatic Analysis desligada | Conflita com a análise via Maven e é menos precisa para Java |

**Ambiente:** Windows 11 · JDK 25 (Oracle) · Maven 3.9.16 (wrapper) · Spring Boot 3.5.4.

---

## 5. Observações e limitações

- **Arquivos analisados:** 67 arquivos Java de código-fonte, 1 arquivo de teste e o `pom.xml`. **Ficaram fora da análise:** `Dockerfile`, `docker-compose.yml`, `application.properties`, `data.sql`, `.env.example` e os contratos `.ts` em `resources/contracts`. Por isso problemas nesses arquivos (como a senha do admin, #15) não aparecem no Sonar.
- **Contagem por qualidade:** 7 issues (`S6813` e `S8688`) afetam duas qualidades ao mesmo tempo e entram nas duas contagens. Por isso a soma Security (5) + Reliability (7) + Maintainability (53) = 65 é maior que o total de 58 issues.
- **Reliability × Bugs:** no modelo antigo de classificação do Sonar o projeto tem **0 bugs**. As 7 issues de Reliability são *code smells* com impacto em confiabilidade no modelo atual (Software Qualities).
- **Security Hotspots = 0:** o SonarQube Cloud passou a reportar os antigos hotspots como issues de Security. Os achados `S4502` (CSRF) e `S5122` (CORS) têm a tag `former-hotspot` e estão contabilizados nas 5 issues de Security.
- **Dívida técnica:** 1d 3h refere-se apenas a Maintainability. O esforço estimado para resolver todas as 58 issues é de ~3d 3h.
- **Cobertura 0,0%:** o projeto não tem JaCoCo configurado e os testes não foram executados. O valor reflete a ausência de medição, não necessariamente a ausência total de testes. Será tratado na #4.
- **Quality Gate não calculado:** é a primeira análise do projeto. A partir da próxima, o Gate passa a avaliar o código novo.
- **Duplicação 0,0%:** valor medido pelo SonarQube Cloud no código analisado.

---

## 6. Metas para a próxima análise (fim da Sprint 1)

> Proposta inicial — validar com a equipe.

| Métrica | Baseline | Meta | Issues responsáveis |
|---|---|---|---|
| Nota de Security | D | A | #8, #20 |
| Issues `S112` (exceção genérica) | 25 | 0 | #11 |
| Total de issues | 58 | ≤ 25 | #11, #12, #20 |
| Cobertura de testes | não medida | medida no CI | #4, #13 |
| Quality Gate | Not computed | Passed | — |
| Achados críticos/altos da análise manual | 5 | 0 | #5, #6, #7, #8, #9 |

---

## 7. Evidências



- Painel geral (Overview)
  ![Painel Geral](docs/img/img.png)
  ![Painel Geral 2](docs/img/img_1.png)
- Measures: Reliability, Security, Maintainability
![Security Measure](docs/img/img_2.png)
  ![Reliability Measure](docs/img/img_3.png)
  ![Maintainability Measure](docs/img/img_4.png)
- Lista de issues filtrada por severidade Blocker/High
  ![Issues](docs/img/img_5.png)


