
# Starling Bank Round-Up Feature

his project is a java application which is designed to implement a round up feature for Starling Bank customers. 
As outlined we need to take all the transactions in a given week and round them up to the nearest
pound. For example with spending of £4.35, £5.20 and £0.87, the round-up would be £1.58.
This amount should then be transferred into a savings goal using the Starling Bank API.
---

## Table of Contents

1. [Features](#features)
2. [Technologies Used](#technologies-used)
3. [Setup and Configuration](#setup-and-configuration)
4. [Endpoints](#endpoints)
5. [Usage](#usage)
6. [Code Structure](#code-structure)
7. [How It Works](#how-it-works)

---

## Features

- Fetch all user accounts.
- Calculate the round up amount for weekly transactions.
- Create and reuse existing savings goal.
- Add round up amounts to savings goals.
- RESTful APIs for account, transaction, and round-up management.

---

## Technologies Used

- **Java 21**
- **Spring Boot**
- **Gradle**
- **Gson**
- **REST API**
---

## Setup and Configuration

1. **Clone the Repository**:
   ```bash
   git clone https://github.com/Rojan-s/starling-tech-test.git
   ```

2. **Set Up Environment Variables**:
    - Add your Starling Bank API access token in the `application.properties` file, generate transactions on the account:
      ```properties
      access.token=YOUR_STARLING_ACCESS_TOKEN
      ```

3. **Build the Project**:
   ```bash
   gradlew build
   ```

4. **Run the Application**:
   ```bash
   gradlew bootRun
   ```

5. **Access the Application**:
    - The application runs on `http://localhost:8080`.

---

## Endpoints

### Account Endpoints
- **GET /api/accounts**
    - Retrieves all user accounts.

### Round-Up Endpoints
- **GET /api/roundup/value?startDate={ddMMyyyy}**
    - Calculates the round up amount for a specific week.

- **GET /api/roundup/perform?startDate={ddMMyyyy}**
    - Performs the round up operation and transfers the amount to the savings goal.

### Transaction Endpoints
- **GET /api/transactions?startDate={ddMMyyyy}**
    - Retrieves all weekly transactions starting from a given date.

---

## Usage

### Example API Calls

1. **Get All Accounts**:
   ```bash
   curl -X GET http://localhost:8080/api/accounts
   ```
![plot](img/account.png)

2. **Calculate Round-Up**:
   ```bash
   curl -X GET "http://localhost:8080/api/roundup/value?startDate=01012025"
   ```
![plot](img/view.png)

3. **Perform Round-Up**:
   ```bash
   curl -X GET "http://localhost:8080/api/roundup/perform?startDate=01012025"
   ```
![plot](img/perform.png)

---

## Code Structure

### Controllers
- **AccountController**: Handles account-related operations.
- **RoundUpController**: Manages round-up calculations and savings goal operations.
- **TransactionController**: Handles transaction-related requests.

### Services
- **AccountsService**: Fetches and manages account details.
- **TransactionService**: Retrieves and processes transactions.
- **RoundUpService**: Calculates round-up amounts.
- **SavingGoalService**: Manages savings goals.

### Utilities
- **Constants**: Contains application constants.
- **HttpUtils**: Provides utility methods for creating HTTP headers.

### Components
- **Account**: Represents account details.
- **Transactions**: Represents transaction data.

---

## How It Works

1. **Fetch Accounts**:
    - The application retrieves all user accounts via the `/api/accounts` endpoint.

2. **Calculate Round-Up**:
    - Transactions are fetched for a specific week filtering for outgoing payments in GBP that are settled and not an internal transfer.
    - The round-up amount is calculated by rounding each transaction up to the nearest pound using a list containing the transactions.

3. **Perform Round-Up**:
    - The calculated amount is added to a designated savings goal.
    - If the savings goal does not exist, it is automatically created.
    - It reuses the same name saving goal if it exists.

---

## Assumptions Made
1. Only outgoing transactions (`direction: OUT`) are considered for the round-up. Transactions must have a status of `SETTLED` to be included in the calculation. Transactions with a source of `INTERNAL_TRANSFER` are excluded.
2. Only transactions in GBP (`currency: GBP`) are processed for the round-up feature.
3. The primary account (`accountType: PRIMARY`) is used for transaction retrieval and round-up operations.
4. A "week" is defined as a 7-day period starting from the provided `startDate`.
5. The user is able to input any start date as they wish.

## Testing
The testing for this project was  conducted through self-testing by interacting with the REST API endpoints. This involved sending API requests to the 
endpoints using tools such as Postman to verify the behavior of key functionalities like calculating round-ups,
retrieving transactions, and savings goals.

In addition, a limited set of unit tests were implemented for the `RoundUpService` and 
`TransactionService` using JUnit and Mockito.
These tests focused on validating the round-up calculation logic and simulating key scenarios such as:
- Calculating round-ups for valid transactions.
- Handling large transactions
- Handling empty transaction lists or transactions that are already rounded.
- Simulating realistic flows like fetching transactions and verifying total round-ups.
