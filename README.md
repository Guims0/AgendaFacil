# Agenda Fácil — Sistema de Agendamentos

Uma API REST desenvolvida em Java e Spring Boot para o gerenciamento de agendamentos e repasses financeiros entre Clientes e Estabelecimentos de serviços. Totalmente documentada via Swagger.

---

## 🛠️ Especificações Técnicas e Arquitetura

*   **Linguagem:** Java
*   **Framework:** Spring Boot
*   **Segurança:** Spring Security + JWT + Stateless Session
*   **Controle de Acesso:** Autorização (`@EnableMethodSecurity` + `@PreAuthorize`)
*   **Banco de Dados:** MySQL com persistência via Spring Data JPA
*   **Documentação:** Swagger UI integrada
*   **Testes Automatizados:** JUnit 5, Mockito e Spring Test

---

## 🔒 Regras de Negócio Implementadas

1.  **Prevenção de Conflitos :** Bloqueio de novos agendamentos caso o estabelecimento já possua uma reserva ativa no mesmo horário.
2.  **Validação de Expediente:** Rejeição de agendamentos que ocorram fora dos dias e horários de funcionamento configurados pelo estabelecimento.
3.  **Divisão Financeira e Taxação:** Pagamentos processados via carteira digital debitam o valor integral do cliente, retêm automaticamente 5% de taxa da plataforma e repassam o valor líquido (95%) ao estabelecimento.
4.  **Estorno Garantido:** Em caso de cancelamento permitido, o financeiro executa a operação inversa : devolve 100% ao cliente e debita o valor líquido da carteira do estabelecimento.
5.  **Delete:** Clientes e estabelecimentos são inativados logicamente, preservando a integridade do histórico financeiro e impedindo o acesso à API.

---

## 🧪 Cobertura de Testes Unitários

A estabilidade do sistema é validada por uma suíte de testes unitários automatizados desenvolvida com **JUnit 5** e **Mockito**.

*   **`AgendamentoServiceTest`:** Valida a trava de segurança contra overbooking, a checagem do quadro de expedientes, o bloqueio de cancelamento por usuários mal-intencionados e a injeção automatizada de status dependendo do papel do usuário logado.
*   **`PagamentoServiceTest`:** Garante a precisão matemática do financeiro. Valida a divisão de valores com a retenção da taxa de 5%, o bloqueio de agendamentos online por saldo insuficiente e o fluxo de estorno entre carteiras.
*   **`ClienteServiceTest`:** Assegura a integridade das restrições de e-mail e CPF únicos no banco de dados e valida as travas do Delete, impedindo a inativação de contas com agendamentos pendentes.

---

## 📑 Documentação dos Endpoints

Todas as rotas exceto login são restritas exigindo a inclusão do cabeçalho de autorização: `Authorization: Bearer <TOKEN_JWT>`.

### 1. Módulo de Autenticação

#### Efetuar Login
*   **Método:** `POST`
*   **Rota:** `/login`
*   **Headers:** `Nenhum`
*   **Requisição (Body):**
```json
{
  "email": "cliente@email.com",
  "senha": "senhaSegura123"
}
```
*   **Resposta (HTTP 200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

---

### 2. Módulo de Clientes

#### Cadastrar Cliente
*   **Método:** `POST`
*   **Rota:** `/clientes`
*   **Headers:** `Nenhum`
*   **Requisição (Body):**
```json
{
  "nome": "Albert Silva",
  "email": "Albert@email.com",
  "senha": "senhaCripto123",
  "cpf": "12345678901",
  "telefone": "21999999999"
}
```

#### Atualizar Perfil do Cliente
*   **Método:** `PUT`
*   **Rota:** `/clientes`
*   **Headers:** `Authorization: Bearer <TOKEN_CLIENTE>`
*   **Requisição (Body):**
```json
{
  "nome": "Albert De Silva",
  "email": "albert.novo@email.com",
  "senha": "novaSenhaExemplo",
  "telefone": "21988888888"
}
```

#### Inativar Conta do Cliente (Soft Delete)
*   **Método:** `DELETE`
*   **Rota:** `/clientes`
*   **Headers:** `Authorization: Bearer <TOKEN_CLIENTE>`
*   **Requisição (Body):** `Nenhum`
*   **Resposta (HTTP 204 No Content)**

---

### 3. Módulo de Estabelecimentos

#### Cadastrar Estabelecimento
*   **Método:** `POST`
*   **Rota:** `/estabelecimentos`
*   **Headers:** `Nenhum`
*   **Requisição (Body):**
```json
{
  "nome": "Barbearia Premium",
  "email": "contato@barbearia.com",
  "senha": "senhaEstab123",
  "cnpj": "12345678000195",
  "intervaloAtendimentoMinutos": 30,
  "descricaoEspecialidade": "Cabelo e Barba",
  "aprovacaoAutomatica": true
}
```

#### Atualizar Estabelecimento
*   **Método:** `PUT`
*   **Rota:** `/estabelecimentos`
*   **Headers:** `Authorization: Bearer <TOKEN_ESTABELECIMENTO>`
*   **Requisição (Body):**
```json
{
  "nome": "Barbearia Premium LTDA",
  "email": "gerencia@barbearia.com",
  "senha": "novaSenhaEstab",
  "descricaoEspecialidade": "Cabelo, Barba e Estética",
  "intervaloAtendimentoMinutos": 45,
  "aprovacaoAutomatica": false
}
```

#### Inativar Conta do Estabelecimento (Soft Delete)
*   **Método:** `DELETE`
*   **Rota:** `/estabelecimentos`
*   **Headers:** `Authorization: Bearer <TOKEN_ESTABELECIMENTO>`
*   **Requisição (Body):** `Nenhum`
*   **Resposta (HTTP 204 No Content)**

---

### 4. Módulo de Serviços

#### Cadastrar Serviço
*   **Método:** `POST`
*   **Rota:** `/servicos`
*   **Headers:** `Authorization: Bearer <TOKEN_ESTABELECIMENTO>`
*   **Requisição (Body):**
```json
{
  "nome": "Corte Degradê",
  "descricao": "Corte moderno com acabamento em navalha",
  "preco": 50.0
}
```

#### Listar Serviços por Estabelecimento
*   **Método:** `GET`
*   **Rota:** `/servicos/estabelecimento/{idEstabelecimento}`
*   **Headers:** `Authorization: Bearer <TOKEN>`
*   **Requisição (Body):** `Nenhum`
*   **Resposta (HTTP 200 OK):**
```json
[
  {
    "id": 1,
    "nome": "Corte Degradê",
    "descricao": "Corte moderno com acabamento em navalha",
    "preco": 50.0
  }
]
```

#### Atualizar Serviço
*   **Método:** `PUT`
*   **Rota:** `/servicos/{id}`
*   **Headers:** `Authorization: Bearer <TOKEN_ESTABELECIMENTO>`
*   **Requisição (Body):**
```json
{
  "nome": "Corte Degradê Premium",
  "descricao": "Corte moderno com lavagem inclusa",
  "preco": 60.0
}
```

#### Deletar Serviço
*   **Método:** `DELETE`
*   **Rota:** `/servicos/{id}`
*   **Headers:** `Authorization: Bearer <TOKEN_ESTABELECIMENTO>`
*   **Requisição (Body):** `Nenhum`
*   **Resposta (HTTP 204 No Content)**

---

### 5. Módulo de Agendamentos

#### Solicitar Agendamento (Via App do Cliente)
*   **Método:** `POST`
*   **Rota:** `/agendamentos`
*   **Headers:** `Authorization: Bearer <TOKEN_CLIENTE>`
*   **Requisição (Body):**
```json
{
  "estabelecimentoId": 1,
  "servicoId": 1,
  "dataHora": "2026-06-15T14:30:00",
  "formaPagamento": "CARTEIRA"
}
```

#### Solicitar Agendamento (Via Balcão do Estabelecimento)
*   **Método:** `POST`
*   **Rota:** `/agendamentos`
*   **Headers:** `Authorization: Bearer <TOKEN_ESTABELECIMENTO>`
*   **Requisição (Body):**
```json
{
  "clienteId": 4,
  "servicoId": 1,
  "dataHora": "2026-06-15T16:00:00",
  "formaPagamento": "LOCAL"
}
```

#### Listar Agendamentos do Cliente Logado
*   **Método:** `GET`
*   **Rota:** `/agendamentos/meus`
*   **Headers:** `Authorization: Bearer <TOKEN_CLIENTE>`
*   **Requisição (Body):** `Nenhum`
*   **Resposta (HTTP 200 OK):**
```json
[
  {
    "idAgendamento": 12,
    "dataHora": "2026-06-15T14:30:00",
    "estabelecimentoNome": "Barbearia Premium",
    "servicoNome": "Corte Degradê",
    "preco": 50.0,
    "statusAgendamento": "CONFIRMADO",
    "statusPagamento": "PAGO"
  }
]
```

#### Listar Agenda do Estabelecimento Logado
*   **Método:** `GET`
*   **Rota:** `/agendamentos/estabelecimento`
*   **Headers:** `Authorization: Bearer <TOKEN_ESTABELECIMENTO>`
*   **Requisição (Body):** `Nenhum`
*   **Resposta (HTTP 200 OK):**
```json
[
  {
    "idAgendamento": 12,
    "dataHora": "2026-06-15T14:30:00",
    "clienteNome": "Albert Silva",
    "clienteTelefone": "21999999999",
    "servicoNome": "Corte Degradê",
    "preco": 50.0,
    "statusAgendamento": "CONFIRMADO",
    "statusPagamento": "PAGO"
  }
]
```

*Nota: Para qualquer listagem vazia, o retorno da API será uma lista vazia `[]` com status HTTP 200 OK.*

#### Confirmar Agendamento Pendente
*   **Método:** `PUT`
*   **Rota:** `/agendamentos/{id}/confirmar`
*   **Headers:** `Authorization: Bearer <TOKEN_ESTABELECIMENTO>`
*   **Requisição (Body):** `Nenhum`
*   **Resposta (HTTP 200 OK)**

#### Registrar Pagamento Manual (No Local)
*   **Método:** `PUT`
*   **Rota:** `/agendamentos/{id}/registrar-pagamento`
*   **Headers:** `Authorization: Bearer <TOKEN_ESTABELECIMENTO>`
*   **Requisição (Body):** `Nenhum`
*   **Resposta (HTTP 200 OK)**

#### Cancelar Agendamento
*   **Método:** `PUT`
*   **Rota:** `/agendamentos/{id}/cancelar`
*   **Headers:** `Authorization: Bearer <TOKEN>`
*   **Requisição (Body):** `Nenhum`
*   **Resposta (HTTP 200 OK)**

---

### 6. Módulo de Carteira Digital

#### Consultar Saldo
*   **Método:** `GET`
*   **Rota:** `/carteiras/saldo`
*   **Headers:** `Authorization: Bearer <TOKEN>`
*   **Requisição (Body):** `Nenhum`
*   **Resposta (HTTP 200 OK):** `150.00`

#### Realizar Depósito
*   **Método:** `POST`
*   **Rota:** `/carteiras/deposito`
*   **Headers:** `Authorization: Bearer <TOKEN>`
*   **Requisição (Body):**
```json
{
  "valor": 100.0
}
```

---

## 🖥️ Interface Interativa (Swagger UI)

A API possui integração com o **Springdoc OpenAPI**. Tudo documentados acima podem ser validados e executados visualmente em tempo real.

Para acessar o painel com a aplicação rodando localmente, utilize a URL:
`http://localhost:8080/swagger-ui/index.html`

---
