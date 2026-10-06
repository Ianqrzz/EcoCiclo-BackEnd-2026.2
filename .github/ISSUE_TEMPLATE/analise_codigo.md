---
name: "Relato de Qualidade ou Segurança"
about: "Utilize este modelo para reportar vulnerabilidades, débitos técnicos ou falhas de arquitetura."
title: "[ANÁLISE] "
labels: "precisa-revisao"
assignees: ""
---

### 📌 Tipo de Achado
- [ ] **Qualidade / Débito Técnico** (Ex: Má prática, falha de arquitetura, falta de testes, código duplicado)
- [ ] **Vulnerabilidade de Segurança** (Ex: Falha no Spring Security, JWT, SQL Injection, credencial exposta)

### 📍 Localização no Código
- **Arquivo / Classe:** `src/main/java/br/com/ifba/ecociclo/.../NomeDaClasse.java`
- **Método / Linhas:** ex: `autenticarUsuario()` — Linhas 35 a 42

### 📝 Descrição do Problema
Descreva com clareza o problema encontrado no código ou no comportamento da aplicação.

### ⚠️ Impacto e Gravidade
- [ ] **Baixa:** Não afeta a execução nem expõe dados (ex: violação de padrão de código, log desnecessário).
- [ ] **Média:** Afeta legibilidade, manutenção ou pode gerar erros sob certas condições.
- [ ] **Alta:** Falha de arquitetura relevante ou vulnerabilidade que expõe dados/recursos sensíveis.
- [ ] **Crítica:** Vulnerabilidade explorável imediata (ex: bypass de autenticação, injeção de código, chave privada no repositório).

### 🧪 Como Reproduzir / Evidência
*(Opcional - útil para a equipe de testes/DAST)*
Descreva os passos para ver o erro acontecer ou cole aqui o retorno do SonarQube / OWASP ZAP.

### 💡 Proposta de Melhoria / Correção
Explique como esse problema deve ser resolvido no relatório final ou apresente a sugestão de código corrigido.

```java
// Cole aqui o trecho de código como deveria ficar (se aplicável)