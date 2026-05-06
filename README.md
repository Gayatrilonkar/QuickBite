# QuickBite

QuickBite is a Spring Boot REST API for a simple food delivery backend. It uses Spring Web, Spring Data JPA, Hibernate, Lombok, and MySQL.

## Tech Stack

| Layer | Technology |
| --- | --- |
| Language | Java 17 |
| Framework | Spring Boot |
| Persistence | Spring Data JPA / Hibernate |
| Database | MySQL |
| Build | Maven Wrapper |

## Project Structure

```text
src/main/java/com/sit/qb/
|-- QuickBiteApplication.java
|-- controller/
|   |-- CustomerController.java
|   |-- DeliveryAgentController.java
|   |-- OrderController.java
|   `-- RestaurantController.java
|-- dtos/
|   |-- MenuQuantity.java
|   `-- OrderRequestDto.java
|-- entity/
|   |-- Customer.java
|   |-- DeliveryAgent.java
|   |-- MenuItem.java
|   |-- Order.java
|   |-- OrderItem.java
|   `-- Restaurant.java
|-- enums/
|   `-- OrderStatus.java
|-- repository/
`-- service/
```

## Configuration

The application uses `src/main/resources/application.properties` for local MySQL configuration:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/quickbite_db?createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=root
spring.jpa.hibernate.ddl-auto=update
server.port=8080
```

Update the datasource username and password if your local MySQL credentials are different.

## API Endpoints

### Customers

| Method | Endpoint | Description |
| --- | --- | --- |
| POST | `/api/customers` | Register a customer |
| GET | `/api/customers` | List all customers |
| GET | `/api/customers/{id}` | Get a customer by ID |
| GET | `/api/customers/byname/{name}` | Get a customer by name |
| GET | `/api/customers/{email}/{phone}` | Get a customer by email and phone |
| DELETE | `/api/customers/{id}` | Delete a customer |

### Restaurants

| Method | Endpoint | Description |
| --- | --- | --- |
| POST | `/api/restaurants` | Register a restaurant |
| GET | `/api/restaurants` | List all restaurants |
| GET | `/api/restaurants/{id}` | Get a restaurant by ID |
| DELETE | `/api/restaurants/{id}` | Delete a restaurant |
| POST | `/api/restaurants/{id}/menu` | Add a menu item to a restaurant |

### Delivery Agents

| Method | Endpoint | Description |
| --- | --- | --- |
| POST | `/api/agents` | Register a delivery agent |

### Orders

| Method | Endpoint | Description |
| --- | --- | --- |
| POST | `/api/orders` | Place an order |

Sample order request:

```json
{
  "customerId": 1,
  "items": [
    {
      "menuItemId": 2,
      "quantity": 2
    }
  ]
}
```

## Run Locally

```bash
./mvnw spring-boot:run
```

On Windows:

```bat
mvnw.cmd spring-boot:run
```

The API runs at `http://localhost:8080`.

## Test

```bash
./mvnw test
```

On Windows:

```bat
mvnw.cmd test
```
