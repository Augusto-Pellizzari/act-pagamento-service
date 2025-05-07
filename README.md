# Pagamento Service

Microserviço responsável por processar pagamentos de pedidos, persistir status no PostgreSQL e publicar eventos de confirmação ou recusa no RabbitMQ.

---

## Tecnologias

- Java 17  
- Spring Boot 3.4.5  
- Spring Data JDBC
- RabbitMQ (`spring-boot-starter-amqp`)  
- PostgreSQL  
- Maven  

---

## Pré-requisitos

- JDK 17  
- Maven  
- RabbitMQ (p.ex. docker-compose)  
- PostgreSQL (p.ex. docker-compose)  

---

## Como rodar localmente

1. Clone este repositório  
   git clone https://github.com/Augusto-Pellizzari/act-pagamento-service.git  
   cd act-pagamento-service

2. Ajuste as variáveis de ambiente (ou application.yml):

SPRING_DATASOURCE_URL=jdbc:postgresql://<host>:5432/loja  
SPRING_DATASOURCE_USERNAME=postgres  
SPRING_DATASOURCE_PASSWORD=123456  

SPRING_RABBITMQ_HOST=rabbitmq  
SPRING_RABBITMQ_PORT=5672  
SPRING_RABBITMQ_USERNAME=guest  
SPRING_RABBITMQ_PASSWORD=guest

3. Gere o JAR via Maven  
   mvn clean package -DskipTests

4. Execute a aplicação  
   java -jar target/loja-online-pagamento-be-0.0.1-SNAPSHOT.jar

5. Ou, use o mesmo Dockerfile do pedido-service, ajustando apenas o nome do JAR na etapa de cópia.

---

## Endpoints

### 1. Confirmar pagamento

POST /api/pagamentos/confirmar  
Content-Type: application/json

{
  "pedidoId": 1,
  "status": "CONFIRMADO"
}

**status:** "CONFIRMADO" ou "RECUSADO"  
**Resposta:** 200 OK (sem body)

---

## Consumo de eventos

- Consome PedidoCriadoEvent da fila `pedido.criado.queue`  
- Gera um registro PENDENTE para o pagamento  
- Publica PagamentoConfirmadoEvent no exchange `pagamento.confirmado.exchange`

---

## Tratamento de erros

- Valida `correlationId` recebido no cabeçalho AMQP  
- Gera BusinessException / ServiceException em falhas do broker ou de lógica de pagamento  
- Handler global (@RestControllerAdvice) padroniza o JSON de erro

---

## Observações

Integre com o repositório de orquestração:  
https://github.com/Augusto-Pellizzari/infrastructure-docker-compose

Use testes de integração com RabbitMQ em memória e PostgreSQL no Docker
