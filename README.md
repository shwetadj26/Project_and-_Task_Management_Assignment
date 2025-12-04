# Project_and-_Task_Management_Assignment ( Mini Project Management API )

A RESTful API built with Spring Boot for managing projects and tasks. This application includes User Authentication (JWT), Project management, and detailed Task tracking with filtering and sorting capabilities.

## 📋 Table of Contents
- [Tech Stack](#-tech-stack)
- [Database Schema](#-database-schema)
- [Setup & Installation](#-setup--installation)
- [Configuration](#-configuration)
- [API Endpoints](#-api-endpoints)
    - [Authentication](#authentication)
    - [Projects](#projects)
    - [Tasks](#tasks)

## 🛠 Tech Stack
- **Language:** Java 17+
- **Framework:** Spring Boot 3.x
- **Database:** MySQL / PostgreSQL (JPA/Hibernate)
- **Security:** Spring Security & JWT (JSON Web Tokens)
- **Tools:** Maven, Lombok, Jakarta Validation

---

## 🗄 Database Schema

The application uses a relational database structure. Below is the logical schema based on the DTOs and relationships.

### Entities

1.  **Users** (`users`)
    * `id` (PK): Long
    * `username`: String (Unique)
    * `email`: String (Unique)
    * `password`: String (Encrypted)
    * `created_at`: Timestamp

2.  **Projects** (`projects`)
    * `id` (PK): Long
    * `name`: String
    * `description`: Text
    * `user_id` (FK): Links to User
    * `created_at`: Timestamp

3.  **Tasks** (`tasks`)
    * `id` (PK): Long
    * `title`: String
    * `description`: Text
    * `status`: Enum (`PENDING`, `IN_PROGRESS`, `COMPLETED`)
    * `priority`: Enum (`LOW`, `MEDIUM`, `HIGH`)
    * `due_date`: Date
    * `project_id` (FK): Links to Project

---

## 🚀 Setup & Installation

### Prerequisites
* Java Development Kit (JDK) 17 or higher
* Maven
* A SQL Database (MySQL or PostgreSQL) running locally

### Steps

1.  **Clone the Repository**
    ```bash
    git clone [https://github.com/your-username/java-project-management.git](https://github.com/your-username/java-project-management.git)
    cd java-project-management
    ```

2.  **Configure Database**
    Update your `src/main/resources/application.properties` file with your database credentials:
    ```properties
    spring.datasource.url=jdbc:mysql://localhost:3306/project_db
    spring.datasource.username=root
    spring.datasource.password=yourpassword
    spring.jpa.hibernate.ddl-auto=update
    
    # JWT Configuration (Example)
    app.jwtSecret=your_very_strong_secret_key_here
    app.jwtExpirationMs=86400000
    ```

3.  **Build the Project**
    ```bash
    mvn clean install
    ```

4.  **Run the Application**
    ```bash
    mvn spring-boot:run
    ```
    The server will start on `http://localhost:8080`.

---

## 🔗 API Endpoints

**Base URL:** `http://localhost:8080/api`

### Authentication
| Method | Endpoint | Description | Request Body |
| :--- | :--- | :--- | :--- |
| `POST` | `/auth/register` | Register a new user | [UserDTO JSON](#userdto) |
| `POST` | `/auth/login` | Login and get JWT | [AuthRequest JSON](#authrequest) |

### Projects
*Requires `Bearer Token` in Authorization header.*

| Method | Endpoint | Description | Request Body |
| :--- | :--- | :--- | :--- |
| `POST` | `/projects/` | Create a new project | [ProjectDTO JSON](#projectdto) |
| `GET` | `/projects/` | Get all projects | N/A |
| `GET` | `/projects/{id}` | Get project by ID | N/A |
| `PUT` | `/projects/{id}` | Update project details | [ProjectDTO JSON](#projectdto) |
| `DELETE` | `/projects/{id}` | Delete a project | N/A |

### Tasks
*Requires `Bearer Token` in Authorization header.*

| Method | Endpoint | Description | Params / Body |
| :--- | :--- | :--- | :--- |
| `POST` | `/projects/{projectId}/tasks` | Create task in project | [TaskDTO JSON](#taskdto) |
| `GET` | `/projects/{projectId}/tasks` | Get all tasks for project | N/A |
| `GET` | `/projects/{projectId}/tasks/{taskId}` | Get specific task | N/A |
| `PUT` | `/projects/{projectId}/tasks/{taskId}` | Update task | [TaskDTO JSON](#taskdto) |
| `DELETE` | `/projects/{projectId}/tasks/{taskId}` | Delete task | N/A |
| `GET` | `/projects/search` | Search tasks | `?query=keyword` |
| `GET` | `/projects/filter` | Filter & Sort tasks | See parameters below |

**Filter Parameters:**
* `status`: (Optional) e.g., `PENDING`
* `priority`: (Optional) e.g., `HIGH`
* `sortBy`: (Default: `dueDate`) Field to sort by
* `order`: (Default: `asc`) `asc` or `desc`

---
