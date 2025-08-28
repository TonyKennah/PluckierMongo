[![Build & Test Status](https://github.com/TonyKennah/PluckierMongo/actions/workflows/maven.yml/badge.svg)](https://github.com/TonyKennah/PluckierMongo/actions/workflows/maven.yml)

# Pluckier-Mongo User Library

This is a Java library for managing user data in a MongoDB database. It provides a simple repository pattern for creating, reading, updating, and deleting user information.

The library is designed to be used by other applications that need to interact with the user database.

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
