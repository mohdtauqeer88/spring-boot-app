# Order Management Service

A Spring Boot order management microservice with REST, JPA, H2, validation, actuator, OpenAPI, and RFC 7807 error handling.

## Run Locally

```bash
mvn clean spring-boot:run


OpenAPI / Swagger
OpenAPI UI:

http://localhost:8080/swagger-ui.html
API docs:

http://localhost:8080/v3/api-docs
H2 Console
H2 console:

http://localhost:8080/h2-console
JDBC URL:

jdbc:h2:mem:orders
Actuator
Health endpoint:

http://localhost:8080/actuator/health