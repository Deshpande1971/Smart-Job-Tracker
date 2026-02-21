# How to Run Java & Spring Boot Applications

This guide provides simple commands to run your code from the terminal.

## 1. Running a Single Java File
If you have a simple standalone Java file (e.g., `Main.java`):

```powershell
# Compile and run in one step (Java 11+)
java Main.java

# OR the traditional way:
javac Main.java    # Produces Main.class
java Main          # Runs the class
```

---

## 2. Running a Spring Boot Project (Maven)
Spring Boot projects are usually managed by Maven. Navigate to your project root (where `pom.xml` is) and run:

### Using Maven Directly
```powershell
mvn spring-boot:run
```

### Using the Maven Wrapper (Recommended)
The wrapper ensures you use the correct Maven version even if it's not installed on the system.
```powershell
./mvnw spring-boot:run
```

---

## 3. Running in VS Code
Since you have the **Spring Boot Extension Pack** installed:

1. **Spring Boot Dashboard**: Look for the "Spring Boot Dashboard" icon in the left sidebar. Click the "Play" button next to your project name.
2. **Main Class**: Open the file containing the `@SpringBootApplication` annotation (usually `JobTrackerApplication.java`) and click the **Run** button above the `main` method.

---

## 4. Useful Maven Commands
- **Build the project**: `mvn clean install`
- **Run tests**: `mvn test`
- **Package into a JAR**: `mvn clean package` (The JAR will be in the `target/` folder).
