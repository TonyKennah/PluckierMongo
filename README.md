[![Build & Test Status](https://github.com/TonyKennah/PluckierMongo/actions/workflows/maven.yml/badge.svg)](https://github.com/TonyKennah/PluckierMongo/actions/workflows/maven.yml)

# Pluckier-Mongo Database Library

This is a Java library for managing user data in a MongoDB database. It provides a simple repository pattern for creating, reading, updating, and deleting user information.  It is designed to be used by other applications that need to interact with the Pluckier user database for Creation, Update, Payments, Logins, Forgotten Passwords (standard user operations).

## Installation

This library is hosted on GitHub Packages. To use it in your Maven project, you need to add the dependency to your `pom.xml` and configure the repository.

### 1. Add the Dependency

Add the following to your `pom.xml`'s `<dependencies>` section:

```xml
<dependency>
    <groupId>co.uk.pluckier.mongo</groupId>
    <artifactId>pluckier-mongo</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

### 2. Configure the Repository

Since the package is not on Maven Central, you also need to tell Maven where to find it by adding the following to your `pom.xml`:

```xml
<repositories>
    <repository>
        <id>github</id>
        <name>GitHub TonyKennah Apache Maven Packages</name>
        <url>https://maven.pkg.github.com/TonyKennah/pluckiermongo</url>
    </repository>
</repositories>
```

## Core Components

*   **`Repo` interface**: Defines the contract for all user data operations. It is `AutoCloseable` to ensure proper resource management.
*   **`UserRepo` class**: The default implementation of the `Repo` interface, handling all communication with MongoDB.
*   **Model classes**: POJOs like `User`, `Login`, and `Forgot` that map directly to MongoDB documents.

## Configuration

The library is configured via a `db.properties` file located in the `src/main/resources` directory. This file must contain the connection details for your MongoDB instance.

**`src/main/resources/db.properties`**
```properties
db.connectionString=mongodb+srv://<user>:<password>@<host>/?retryWrites=true&w=majority
db.database=<database>
db.collection=users
db.collection1=forgot
db.collection2=logins
```

## Usage

To use the library, get an instance of the repository and call its methods. The `UserRepo` handles the database connection lifecycle.

### Getting a Repository Instance

The easiest way to get an instance is to use the default factory method, which reads from `db.properties`.

```java
import uk.co.pluckier.mongo.Repo;
import uk.co.pluckier.mongo.UserRepo;

// It's recommended to use a try-with-resources block to ensure the database connection is always closed.
try (Repo userRepo = UserRepo.getDefaultInstance()) {
    // Use the repo here
} catch (Exception e) {
    // Handle exceptions
}
```

### Example: Fetching a User

```java
try (Repo userRepo = UserRepo.getDefaultInstance()) {
    User user = userRepo.get("some_username");
    if (user != null) {
        System.out.println("Found user: " + user.getEmail());
    } else {
        System.out.println("User not found.");
    }
}
```

## Testing

This project uses 
[JUnit 5](https://junit.org/junit5/) 
for unit testing and 
[Mockito](https://site.mockito.org/) 
for mocking database dependencies.

This approach allows the `UserRepo` logic to be tested in isolation without needing a real or in-memory database. The tests simulate the behavior of the MongoDB driver, making them extremely fast and reliable.

### Running Tests

You can run the full test suite using Maven:

```sh
mvn clean test
```

## Packaging

Automatically packaged and deplpoyed to github packages via github actions.
