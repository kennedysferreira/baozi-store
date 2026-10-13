# Baozi Store – API REST

API REST da loja **Baozi Store** (pãozinho chinês) para controle de clientes, produtos e pedidos.

**Tecnologias:** Java 21 · Spring Boot · Spring Data JPA · MySQL (ou H2) · JSON · Postman

## Estrutura (padrão MVC do Spring)

```
src/main/java/com/baozistore
├── BaoziStoreApplication.java
├── model/          Cliente, Produto, Pedido (entidades JPA) e PedidoRequest (corpo JSON do pedido)
├── repository/     ClienteRepository, ProdutoRepository, PedidoRepository (JpaRepository)
└── controller/     ClienteController, ProdutoController, PedidoController, ErroHandler
```

## Como executar

Pré-requisito: JDK 21 instalado (o Maven vem junto via `mvnw`).

### Com MySQL (padrão)

1. Tenha um MySQL rodando em `localhost:3306`.
2. Usuário/senha padrão: `root` / `root`. Para usar outros, defina as variáveis `DB_USERNAME` e `DB_PASSWORD`
   (ou edite `src/main/resources/application.properties`).
3. O banco `baozi_store` e as tabelas são criados automaticamente.

```bash
./mvnw spring-boot:run          # Linux/Mac
mvnw.cmd spring-boot:run        # Windows
```

### Com H2 (em memória, sem instalar nada)

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=h2
mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=h2
```

Console do H2: http://localhost:8080/h2-console (JDBC URL `jdbc:h2:mem:baozi_store`, usuário `sa`, senha vazia).

A API sobe em **http://localhost:8080**.

## Entidades e campos

| Entidade | Campo          | Tipo       | Observação                              |
|----------|----------------|------------|-----------------------------------------|
| Cliente  | id             | Long       | gerado automaticamente                  |
|          | nome           | String     | obrigatório                             |
|          | clienteDesde   | LocalDate  | formato `AAAA-MM-DD`; se omitido, hoje  |
| Produto  | id             | Long       | gerado automaticamente                  |
|          | nome           | String     | obrigatório                             |
|          | preco          | BigDecimal | obrigatório, ≥ 0                        |
|          | estoque        | Boolean    | disponível em estoque; padrão `true`    |
| Pedido   | id             | Long       | gerado automaticamente                  |
|          | clienteId      | Long       | FK `cliente_id` → cliente               |
|          | produtoId      | Long       | FK `produto_id` → produto               |
|          | quantidade     | Integer    | obrigatório, > 0                        |

Relacionamentos: um Cliente **faz** 0..N Pedidos; um Produto é **vendido em** 0..N Pedidos.

## Endpoints

| Método | URL               | Descrição                 | Sucesso          |
|--------|-------------------|---------------------------|------------------|
| POST   | `/clientes`       | Cria cliente              | 201 Created      |
| GET    | `/clientes`       | Lista todos os clientes   | 200 OK           |
| GET    | `/clientes/{id}`  | Consulta cliente por ID   | 200 OK / 404     |
| PUT    | `/clientes/{id}`  | Atualiza cliente          | 200 OK / 404     |
| DELETE | `/clientes/{id}`  | Apaga cliente             | 204 No Content / 404 / 409 |
| POST   | `/produtos`       | Cria produto              | 201 Created      |
| GET    | `/produtos`       | Lista todos os produtos   | 200 OK           |
| GET    | `/produtos/{id}`  | Consulta produto por ID   | 200 OK / 404     |
| PUT    | `/produtos/{id}`  | Atualiza produto          | 200 OK / 404     |
| DELETE | `/produtos/{id}`  | Apaga produto             | 204 No Content / 404 / 409 |
| POST   | `/pedidos`        | Cria pedido               | 201 Created / 400 |
| GET    | `/pedidos`        | Lista todos os pedidos    | 200 OK           |
| GET    | `/pedidos/{id}`   | Consulta pedido por ID    | 200 OK / 404     |
| PUT    | `/pedidos/{id}`   | Atualiza pedido           | 200 OK / 404     |
| DELETE | `/pedidos/{id}`   | Apaga pedido              | 204 No Content / 404 |

- **409 Conflict**: cliente/produto que ainda possui pedidos não pode ser apagado (apague os pedidos antes).
- **400 Bad Request**: dados inválidos ou `clienteId`/`produtoId` inexistente no pedido.

### Exemplos de JSON

`POST /clientes`
```json
{ "nome": "SeuNome123456", "clienteDesde": "2026-03-01" }
```

`POST /produtos`
```json
{ "nome": "Baozi de Porco", "preco": 7.50, "estoque": true }
```

`POST /pedidos`
```json
{ "clienteId": 1, "produtoId": 1, "quantidade": 12 }
```

Resposta do pedido (já traz os dados do cliente e do produto):
```json
{
  "id": 1,
  "cliente": { "id": 1, "nome": "SeuNome123456", "clienteDesde": "2026-03-01" },
  "produto": { "id": 1, "nome": "Baozi de Porco", "preco": 7.50, "estoque": true },
  "quantidade": 12
}
```

## Postman

Importe `postman/BaoziStore.postman_collection.json` no Postman. A variável `baseUrl` já vem como
`http://localhost:8080`. Troque `SeuNome123456` pelo seu nome + RU antes de tirar os prints.
