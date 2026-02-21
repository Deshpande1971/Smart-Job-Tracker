# Database Schema Management Strategy

In the **Smart Job Tracker** project, we use a "Code-First" approach for database schema management using **Spring Data JPA** and **Hibernate**.

## How are schemas created?
We do NOT write manual `CREATE TABLE` scripts during development. Instead, the Java **Entities** (classes marked with `@Entity`) define the structure of the database.

### The Mechanism: `ddl-auto`
In our `application.properties`, we use:
```properties
spring.jpa.hibernate.ddl-auto=update
```
- **`update`**: Hibernate compares the Java entities with the existing database schema. If a table or column is missing, it creates it automatically. If an entity changes, it updates the table.
- **This ensures that your database is always in sync with your code.**

## Developer Responsibilities

### 1. Defining the Database
As a developer, you only need to create the database once in **pgAdmin** (e.g., `smart_job_db`).
Once the database exists and the credentials in `application.properties` are correct, Hibernate handles the rest.

### 2. Creating Tables
To create a new table, simply create a new Java class in the `com.jobtracker.entity` package and annotate it with:
- `@Entity`: Tells Hibernate this is a table.
- `@Table(name = "...")`: (Optional) Specifies the table name.
- `@Id`: Defines the Primary Key.

### 3. Verification
You can confirm the existence of your tables by:
1.  Opening **pgAdmin**.
2.  Navigating to `Databases` -> `smart_job_db` -> `Schemas` -> `public` -> `Tables`.
3.  Refreshing the list after the application starts.

## Example: User Schema (Phase 2)
When we start Phase 2, Hibernate will automatically generate this SQL for you:
```sql
CREATE TABLE users (
    id UUID PRIMARY KEY,
    name VARCHAR(255),
    email VARCHAR(255) UNIQUE,
    password VARCHAR(255),
    role VARCHAR(50)
);
```

### Why use this approach?
- **Speed**: No need to maintain separate SQL scripts.
- **Type Safety**: The database structure is strictly tied to your Java models.
- **Portability**: Hibernate generates the correct SQL dialect for PostgreSQL automatically.

> [!NOTE]
> In a production environment, we would eventually switch to a tool like **Liquibase** or **Flyway** for more controlled migrations, but for the MVP / Development phase, `ddl-auto=update` is the best practice.
