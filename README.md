# Virtual Wallet

## Project Description

Virtual Wallet is a web application that enables users to conveniently manage their budget. Users can send and receive money, add funds to their Virtual Wallet, manage their profile, and more. The project includes a set of core requirements and optional features to enhance the user experience.

## Features

### Functional Requirements

- **Entities**: Users, Credit/Debit Cards, Personal/Joined Wallets
- **Public Part**: Registration, Login, View Features
- **Private Part**: Manage Profile, Manage Cards, Manage Wallets, Make Transfers and Transactions, View Transaction/Transfer History
- **Administrative Part**: Admin Dashboard, User Management, Transaction/Transfer Monitoring

### Additional Features


- Large Transaction Verification
- Joint Virtual Wallets
- Transaction Confirmation
- Multiple Virtual Wallets
- Multiple Cards

## Technical Requirements

### General

- Object-Oriented Programming Principles
- Follow KISS, SOLID, DRY Principles
- REST API Design Best Practices
- Tiered Project Structure
- Unit Test Code Coverage (80%)
- Proper Exception Handling
- Git Version Control
- Proper Commit Messages

### Database

- Relational Database
- Normalize Database Structure
- Database Creation and Seeding Scripts

### REST API

- CRUD Operations for Users
- Credit/Debit Card Management
- Transaction Handling
- Search and Filter Functionality
- Swagger Documentation

## Getting Started
### Setting Up the Project
1. Clone the repository: `git clone <repository_url>`
2. **Clone dummy-api repository**: `git clone <dummy_api_repository_url>`
3. Open IntelliJ IDEA. 
4. **Import the virtual_wallet and dummy-api Projects:**
- Open IntelliJ IDEA.
- From the main menu, select File > Open.
- Navigate to the directory where you cloned the project repository.
- Select the build.gradle file and click Open.
5. **Install Dependencies:**
- Wait for IntelliJ IDEA to index and sync the project.
- Gradle will automatically download and install the project dependencies.

### Running the Application
1. **Run the Application:**
- Open the src/main/java directory.
- Find the main application file (e.g., Application.java).
- Right-click on the main file.
- Select Run <MainClassName> to start the application.
2. **Access the Application:**
- Once the application is running, you can access it through a web browser.
- Open a web browser and navigate to the appropriate URL (e.g., http://localhost:8080).

3. **Running the Dummy API**
- Open another instance of IntelliJ IDEA.
4.**Import the Project:**
- Follow the same steps as above to import the Dummy API project into IntelliJ IDEA. 
5. **Run the Dummy API:**
- Find the main file or entry point of the Dummy API.
- Right-click on the main file.
- Select Run <MainClassName> to start the Dummy API.

6. **Access the Dummy API:**
- Once the Dummy API is running, it will provide endpoints that your main application can interact with.

### Additional Notes
- Make sure to replace <repository_url> and <dummy_api_repository_url> with the actual URLs of your repositories.
- If your Gradle project has specific configuration requirements or additional setup steps, ensure to follow those as well.
- If you encounter any issues during setup or running the application, refer to the project's documentation or seek assistance from project contributors.


## Documentation

- [Swagger Documentation](http://localhost:8080/swagger-ui/index.html#/)

## Contributors

- Lyubima Malkocheva [@LyubimaMalkocheva](https://github.com/LyubimaMalkocheva)
- Reneta Marinova [@RMarinova](https://github.com/RMarinova)
- Teodor Kehayov [@TeodorK0907](https://github.com/TeodorK0907)

### Reference Documentation

For further reference, please consider the following sections:

* [Official Gradle documentation](https://docs.gradle.org)
* [Spring Boot Gradle Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/3.2.2/gradle-plugin/reference/html/)
* [Create an OCI image](https://docs.spring.io/spring-boot/docs/3.2.2/gradle-plugin/reference/html/#build-image)
* [Spring Boot DevTools](https://docs.spring.io/spring-boot/docs/3.2.2/reference/htmlsingle/index.html#using.devtools)
* [Spring Web](https://docs.spring.io/spring-boot/docs/3.2.2/reference/htmlsingle/index.html#web)

### Guides

The following guides illustrate how to use some features concretely:

* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/rest/)


###### [Distributed By: Andromeda Team](https://github.com/Andromeda-LRT)
