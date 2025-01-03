# Account Management Microservice (AccountMS)

## Description

This microservice is responsible for the creation, query, and management of bank accounts associated with customers. It allows creating accounts, making deposits and withdrawals, and retrieving information about existing accounts.

## Endpoints

### Bank Account Management

- **POST /accounts**: Creates a new bank account for a customer.
- **GET /accounts/{id}**: Retrieves the details of a bank account by its ID.
- **PUT /accounts/{accountId}/depositar**: Makes a deposit into a bank account.
- **PUT /accounts/{accountsId}/retirar**: Makes a withdrawal from a bank account.
- **DELETE /accounts/{id}**: Delete a bank account by ID.

### Interaction with TransactionMS

- **GET /byAccountNumber/{accountNumber}**: Retrieves account details by account number received from **TransactionMS**.
- **POST /tDeposit**: Receives deposit information from **TransactionMS** and processes the deposit in the specified account.
- **POST /tWithdrawal**: Receives withdrawal information from **TransactionMS** and processes the withdrawal in the specified account.
  
## Business Rules

- The initial balance of a bank account must be greater than 0.
- Withdrawals cannot leave the balance of a savings account negative.
- Checking accounts can have an overdraft of up to -500.
- **Account Type**: A customer must specify the type of account when opening a new account (Savings or Checking).
- **Customer Validation**: When opening a new bank account, the system must verify that the customer exists.
- **Unique Account Number**: The account number must be unique and automatically generated by the system (either through a sequence or a random identifier).

## Account Creation Logic

To ensure that a bank account can only be created for an existing customer, the **AccountMS** uses **RestTemplate** to interact with the **CustomerMS** microservice. When an account creation request is made, **AccountMS** first sends a request to **CustomerMS** to verify if the customer exists.

- If the customer does not exist, the account creation is blocked, and no account will be created.
- If the customer exists, the system proceeds to create the account and automatically generates a unique account number.

This approach ensures that accounts are only created for valid, existing customers, maintaining data integrity and preventing the creation of accounts for non-existent customers.

## Technologies used

- **Programming Language**: Java 17
- **Framework**: Spring Boot
- **Database**: MySQL
- **ORM (Object-Relational Mapping)**: JPA (Java Persistence API)
- **Helper Libraries**: Lombok
- **Testing**:
  - **JUnit** (Unit Testing)
  - **Mockito** (Framework for mocking in unit tests)
- **Code Coverage**: JaCoCo
- **Code Style**: Checkstyle
- **API Documentation**: OpenAPI (Swagger)

## Postman Collection

You can find the Postman collection for testing the **AccountMS** API at the following link:

[CustomerMS Postman Collection](https://www.postman.com/yulyschr/test-api-accountms/overview)

This collection includes all the endpoints and examples for testing the API.

## Database Setup

To set up the database for the project, you can use the provided SQL script:

- **[Download core_banking_system2.sql](https://github.com/yulychr/PF-NTT-DATA-AccountMS/blob/main/docs/script%20core_banking_system2.sql)**: This script contains the necessary database schema and initial data for the core banking system.

You can run this script to create the required tables and populate the database with initial data for the system to function correctly.

### How to Run the Script
1. Download the script by clicking the link above.
2. Access your database management tool (MySQL).
3. Open the SQL script.
4. Execute the script in your database to set up the schema.

This will create the necessary database structure and ensure that the system has the required data to operate.

## DIAGRAMs

### Sequence diagram

![DIAGRAMA DE SECUENCIA DE -- CUENTAS -- FINAL drawio](https://github.com/user-attachments/assets/64dc8650-c7c6-4b5a-aefa-8e33d292c21a)

## Documentation

You can find additional documentation for the project in the following documents:

- [Analysis of SOLID Principles and Design Patterns](https://github.com/yulychr/PF-NTT-DATA-AccountMS/blob/main/docs/Analis%20de%20principios%20solid%20y%20patrones%20de%20dise%C3%B1o.docx)
- [Unit Testing and CheckStyle](https://github.com/yulychr/PF-NTT-DATA-AccountMS/blob/main/docs/Pruebas%20unitarias.docx)

Please refer to these documents for a detailed analysis of the SOLID principles, design patterns used, unit testing practices, and code style checks.
