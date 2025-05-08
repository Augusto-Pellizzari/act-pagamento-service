# Pedido Service

Microserviço responsável por receber a criação de pedidos, persistir no PostgreSQL e publicar eventos de `PedidoCriado` no RabbitMQ.

---

## Tecnologias

* Java 17
* Spring Boot 3.4.5
* Spring Data JDBC
* RabbitMQ (`spring-boot-starter-amqp`)
* PostgreSQL
* Swagger / OpenAPI 3.1 (springdoc‑openapi‑starter)
* Maven

---

## Pré‑requisitos

* JDK 17
* Maven
* RabbitMQ (p.ex. docker‑compose)
* PostgreSQL (p.ex. docker‑compose)

---

## Como rodar localmente

1. **Clone** este repositório

   ```bash
   git clone https://github.com/Augusto-Pellizzari/act-pedido-service.git
   cd act-pedido-service
   ```

2. **Ajuste as variáveis de ambiente** (ou `application.yml`):

   ```properties
   SPRING_DATASOURCE_URL=jdbc:postgresql://<host>:5432/loja
   SPRING_DATASOURCE_USERNAME=postgres
   SPRING_DATASOURCE_PASSWORD=123456

   SPRING_RABBITMQ_HOST=rabbitmq
   SPRING_RABBITMQ_PORT=5672
   SPRING_RABBITMQ_USERNAME=guest
   SPRING_RABBITMQ_PASSWORD=guest
   ```

3. **Gere o JAR** via Maven

   ```bash
   mvn clean package -DskipTests
   ```

4. **Execute** a aplicação

   ```bash
   java -jar target/loja-online-pedido-be-0.0.1-SNAPSHOT.jar
   ```

5. **Ou** crie um contêiner Docker:

   ```dockerfile
   # Dockerfile (já incluso neste projeto)
   FROM maven:3.9.4-eclipse-temurin-17 AS builder
   WORKDIR /build
   COPY pom.xml .
   COPY src ./src
   RUN mvn clean package -DskipTests

   FROM eclipse-temurin:17-jre
   WORKDIR /app
   COPY --from=builder /build/target/*-SNAPSHOT.jar app.jar
   ENTRYPOINT ["java","-jar","app.jar"]
   ```

---

## Documentação Swagger

Depois de subir o serviço localmente você pode explorar a API pelo Swagger:

| Recurso            | URL default                                                                    |
| ------------------ | ------------------------------------------------------------------------------ |
| UI interativa      | [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html) |
| Especificação JSON | [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)         |

> Dica: caso queira acessar a UI apenas com `/swagger-ui`, defina `springdoc.swagger-ui.path=/swagger-ui` no `application.properties`.

### Importar no Postman / Insomnia

1. Acesse `<host>/v3/api-docs` e salve o JSON.
2. No Postman clique em **Import → Raw text / File** e escolha o JSON para gerar automaticamente a coleção.

---

## Endpoints

### 1. Criar pedido

```
POST /api/pedidos
Content-Type: application/json

{
  "cliente": "Nome do Cliente"
}
```

**Resposta 200 OK**

```json
{
  "id": 1,
  "cliente": "Nome do Cliente",
  "status": "AGUARDANDO_PAGAMENTO",
  "dataCriacao": "2025-05-07T12:34:56"
}
```

### 2. Listar todos

```
GET /api/pedidos
```

**Resposta 200 OK**

```json
[
  {
    "id": 1,
    "cliente": "Nome do Cliente",
    "status": "AGUARDANDO_PAGAMENTO",
    "dataCriacao": "2025-05-07T12:34:56"
  }
]
```

### 3. Atualizar status

```
PUT /api/pedidos/{id}/status?status={StatusPedido}
```

`StatusPedido` pode ser `AGUARDANDO_PAGAMENTO`, `PAGO`, `RECUSADO`, etc.

**Resposta 200 OK** — pedido atualizado.

---

## Fluxo de mensageria

1. Ao criar um pedido, o serviço publica um **PedidoCriadoEvent** no exchange `pedido.criado.exchange`.
2. O **pagamento-service** consome esse evento e processa o pagamento.
3. Após confirmação ou recusa, o pagamento-service publica **PagamentoConfirmadoEvent** no exchange `pagamento.confirmado.exchange`.
4. Este serviço consome o evento e atualiza o status do pedido.

---

## Tratamento de erros

* Lança `BusinessException` para erros de domínio.
* `@RestControllerAdvice` global transforma exceções em payload JSON padronizado.

---

## Observações

Para testes locais recomendo usar o docker‑compose:
[https://github.com/Augusto-Pellizzari/infrastructure-docker-compose](https://github.com/Augusto-Pellizzari/infrastructure-docker-compose)
