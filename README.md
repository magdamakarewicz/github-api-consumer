# GitHub API Consumer

This project is a Spring Boot application designed to consume the GitHub API and provide information about a user's repositories that are not forks. The application exposes an endpoint to retrieve a list of repositories along with their branches and the latest commit SHA for each branch.

## Features

- List all non-fork repositories for a given GitHub username. 
- For each repository, list all branches with name, owner login and the latest commit SHA.
- Proper error handling for non-existent GitHub users with a 404 response.

## Technologies Used

- Java 21
- Spring Boot 3.3.2
- RESTful API
- Maven
- JUnit and Mockito for testing
- Lombok for boilerplate code reduction

## Prerequisites

- JDK 21 or higher
- Maven 3.8.1 or higher

## Getting Started

### Clone the Repository

```bash
git clone https://github.com/magdamakarewicz/github-api-consumer.git
cd github-api-consumer
```

### Build the Application
```bash
mvn clean install
```

### Run the Application
```bash
mvn spring-boot:run
```
The application will start on the default port 8080.

## API Endpoints

### Get Non-Fork Repositories

#### Request:

```http
GET /api/github/users/{username}/repos
Headers:
Accept: application/json
```

#### Response:

* 200 OK
```json
[
  {
    "name": "repository-name",
    "branches": [
      {
        "name": "branch-name",
        "commit": {
          "sha": "commit-sha"
        }
      }
    ],
    "owner": {
      "login": "owner-login"
    }
  }
]
```
* 404 Not Found
```json
{
  "status": 404,
  "message": "User '{username}' not found"
}
```

## Error Handling
If a non-existent GitHub username is provided, the API will return a 404 response with a message indicating the user was not found.

## Project Structure
* src/main/java: Contains the main application and business logic.
    - config: Configuration classes.
    - controller: REST controllers.
    - dto: Data transfer objects.
    - exception: Custom exceptions and global exception handlers. 
    - repository: Repository classes for API calls.
    - service: Service layer with business logic.
* src/test/java: Contains unit and integration tests.

## Testing
Unit and integration tests are provided to ensure the functionality of the application. To run the tests, use:

```bash
mvn test
```

## References
* [GitHub API v3 Documentation](https://developer.github.com/v3)
* [Spring Boot Documentation](https://spring.io/projects/spring-boot)

## Contact
If you have any questions or suggestions, please feel free to contact me at makarewicz.magdalena@outlook.com.