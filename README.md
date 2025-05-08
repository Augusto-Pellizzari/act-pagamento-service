# Pagamento Service

Microserviço responsável por **processar pagamentos** dos pedidos, persistir cada transação no PostgreSQL, consumir eventos `PedidoCriado` do RabbitMQ e publicar eventos `PagamentoConfirmado` ou `PagamentoRecusado` para os demais serviços.

---

## Tecnologias

* Java 17
* Spring Boot 3.4.5
* Spring Data JDBC
* RabbitMQ (`spring-boot-starter-amqp`)
* PostgreSQL
* Swagger / OpenAPI 3.1 (springdoc-openapi-starter)
* Maven

---

## Pré‑requisitos

* JDK 17
* Maven
* RabbitMQ (p.ex. via docker‑compose)
* PostgreSQL (p.ex. via docker‑compose)

---

## Como rodar localmente

1. **Clone** este repositório

   ```bash
   git clone https://github.com/Augusto-Pellizzari/act-pagamento-service.git
   cd act-pagamento-service
   ```

2. **Configure as variáveis de ambiente** (ou `application.yml`):

   ```properties
   # Banco de dados
   SPRING_DATASOURCE_URL=jdbc:postgresql://<host>:5432/loja
   SPRING_DATASOURCE_USERNAME=postgres
   SPRING_DATASOURCE_PASSWORD=123456

   # RabbitMQ
   SPRING_RABBITMQ_HOST=rabbitmq
   SPRING_RABBITMQ_PORT=5672
   SPRING_RABBITMQ_USERNAME=guest
   SPRING_RABBITMQ_PASSWORD=guest
   ```

3. **Gere o JAR** com Maven

   ```bash
   mvn clean package -DskipTests
   ```

4. **Execute** o serviço

   ```bash
   java -jar target/loja-online-pagamento-be-0.0.1-SNAPSHOT.jar
   ```

   (Por padrão na porta **8081**; altere `server.port` se necessário.)

5. **Ou** suba um contêiner Docker:

   ```dockerfile
   # Dockerfile (já incluso no projeto)
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

| Recurso            | URL default                                                                    |
| ------------------ | ------------------------------------------------------------------------------ |
| UI interativa      | [http://localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html) |
| Especificação JSON | [http://localhost:8081/v3/api-docs](http://localhost:8081/v3/api-docs)         |

> Dica: configure `springdoc.swagger-ui.path=/swagger-ui` para acessar via `/swagger-ui`.

---

## Endpoints Principais

### Confirmar / Recusar pagamento

```
POST /api/pagamentos/confirmar
Content-Type: application/json

{
  "pedidoId": 1,
  "status": "CONFIRMADO"   // ou "RECUSADO"
}
```

**Resposta 200 OK** – sem corpo (HTTP 204 também seria aceitável).

---

## Fluxo de mensageria

1. **Consumo**: este serviço escuta a fila `pedido.criado.queue`.
2. Ao receber um `PedidoCriadoEvent` ele grava o pagamento como **PENDENTE**.
3. **Publicação**: após confirmação manual ou automática, publica `PagamentoConfirmadoEvent` (ou `RECUSADO`) no exchange `pagamento.confirmado.exchange` com routing‑key `pagamento.confirmado`.
4. O **pedido-service** consome esse evento e atualiza o status do pedido.

---

## Tratamento de erros

* Exceções de domínio geram `CustomException.ServiceException` com `ErrorCode` adequado.
* Falhas de banco retornam `CustomException.RepositoryException`.
* Problemas de broker lançam `CustomException.BrokerException`.
* `@RestControllerAdvice` converte todas em JSON padronizado.

---

## Executando tudo com Docker Compose

Use o repositório de infraestrutura:

```bash
cd D:\Projetos\VISUAL-SPRING
git clone https://github.com/Augusto-Pellizzari/infrastructure-docker-compose.git docker-compose
cd docker-compose

docker-compose up -d --build
```

Isso iniciará PostgreSQL, RabbitMQ, **pedido-service (8080)** e **pagamento-service (8081)** já configurados.

---

## Teste rápido

1. **Criar pedido**

   ```bash
   curl -X POST http://localhost:8080/api/pedidos \
        -H "Content-Type: application/json" \
        -d '{"cliente":"José da Silva"}'
   ```
2. **Confirmar pagamento**

   ```bash
   curl -X POST http://localhost:8081/api/pagamentos/confirmar \
        -H "Content-Type: application/json" \
        -d '{"pedidoId":1,"status":"CONFIRMADO"}'
   ```
3. **Verificar pedido**

   ```bash
   curl http://localhost:8080/api/pedidos
   ```

   O status deve estar atualizado para `PAGO` (ou equivalente).

---

## Observações

* O serviço armazena `correlationId` para evitar processamento duplicado de mensagens.
* `RetryInterceptor` no listener RabbitMQ garante retentativas e DLQ.
* Toda data/hora é salva em **UTC** (`OffsetDateTime`).
