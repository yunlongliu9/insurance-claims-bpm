# Insurance Claims BPM

## 1. What does this project do?
This project checks insurance claims
- **CIB seven** runs the BPMN process.
- **Quarkus** runs the Java code.
- **PostgreSQL** stores process data.
- **Docker Compose** starts the services.
## 2. System Architecture
```

                       User / API
                           │ POST /claims
                           │
┌────────────── QUARKUS :8081 ────────────────┐
│                          │                  │
│                          ▼                  │
│               ┌───────────────────┐         │
│               │ ClaimResource     │         │
│               └─────────┬─────────┘         │
│                         │                   │
│                         ▼                   │
│               ┌───────────────────┐         │
│               │ CibSevenClient    ├─────────┼─────────────┐
│               └───────────────────┘         │ REST        │
│                                             │             │(start BPMN)
│  ┌──────────────────────┐                   │             │
│  │ CoverageResource     │◄──────────────────┼───────┐     │
│  │                      ├───────────────────┼─────┐ │     │
│  └──────────┬───────────┘                   │     │ │     │
│             │                               │     │ │     │
│             ▼                               │     │ │     │
│  ┌──────────────────────┐                   │     │ │     │
│  │ CoverageService      │                   │     │ │     │
│  └──────────────────────┘                   │     │ │     │
│                                             │     │ │     │
└─────────────────────────────────────────────┘     │ │     │
                                                    │ │     │
┌────────────── CIB seven :8080 ──────────────┐     │ │     │
│                                             │     │ │     │
│  ┌──────────────────────┐                   │     │ │     │
│  │ REST API             │◄──────────────────┼─────┼─┼─────┘
│  └──────────┬───────────┘                   │     │ │
│             │                               │     │ │
│             ▼                               │     │ │
│  ┌──────────────────────┐                   │     │ │
│  │ BPMN Engine          │                   │     │ │
│  │ coverage-check.bpmn  │                   │     │ │
│  └──────────┬───────────┘                   │     │ │
│             │                               │     │ │(valid date is resourcs)
│             ▼                               │     │ │
│  ┌──────────────────────┐                   │     │ │(check date valid?)
│  │ HTTP Connector       │◄──────────────────┼─────┘ │
│  │                      ├───────────────────┼───────┘(REST API)
│  └──────────────────────┘                   │
│                                             │
│  BPMN Engine ───────── JDBC ────────────────┼───────┐
└─────────────────────────────────────────────┘       │
                                                      │
                               ┌──────────────────────▼──┐
                               │ PostgreSQL :5432        │
                               │ Process / History Data  │
                               └─────────────────────────┘
   
```
## 3. Project Files

```
insurance-claims-bpm/
|
|-- compose.yaml
|-- Dockerfile.dev
|-- pom.xml
|-- config/
|   `-- bpm-platform.xml
|
`-- src/
    |-- main/
    |   |-- java/org/example/insurance/
    |   |   |-- client/    (cib seven client)
    |   |   |   `-- CibSevenClient.java
    |   |   `-- server/     (cib seven server)
    |   |       |-- ClaimResource.java
    |   |       |-- CoverageResource.java
    |   |       `-- CoverageService.java
    |   |
    |   `-- resources/
    |       |-- application.properties
    |       `-- bpmn/
    |           |-- coverage-check.bpmn
    |           `-- insurance_process_mod.bpmn
    |
    `-- test/java/org/example/insurance/
        `-- CoverageServiceTest.java
```

### Java Files

| File                           | What does it do?                     |
| ------------------------------ | ------------------------------------ |
| `client/CibSevenClient.java`   | Calls the CIB seven REST API.        |
| `server/ClaimResource.java`    | Starts a BPMN process.               |
| `server/CoverageResource.java` | Receives the HTTP request from BPMN. |
| `server/CoverageService.java`  | Checks insurance dates.              |
| `CoverageServiceTest.java`     | Tests the date check.                |
1. CoverageService - add new business? code here.
2. CoverageResource - provide http API for CIB 7

### BPMN Files
==CIB 7 do not need any java code, just modify the bpmn file to controll the proces.==
+ bpmn: add new process or task (must code to realize first)

| File                         | What does it do?              |
| ---------------------------- | ----------------------------- |
| `coverage-check.bpmn`        | Checks the insurance period.  |
| `insurance_process_mod.bpmn` | Main insurance process model. |

### Config Files

| File                     | What does it do?                          |
| ------------------------ | ----------------------------------------- |
| `compose.yaml`           | Starts Docker containers.                 |
| `Dockerfile.dev`         | Builds the Quarkus development container. |
| `pom.xml`                | Manages Java libraries.                   |
| `application.properties` | Sets Quarkus and database options.        |
| `bpm-platform.xml`       | Sets up the CIB seven engine.             |

## 4. How to Start
Run this command in the project folder:

```
docker compose up -d
```
Services:
- CIB seven: `http://localhost:8080`
- Quarkus: `http://localhost:8081`
 Background Services
- PostgreSQL: `localhost:5432`
## 5. How to Add a New BPMN Process
### Step 1 — Create a BPMN File
Go to:
+ `src/main/resources/bpmn/`
Create a new file:
+ `my-process.bpmn`
Use a BPMN editor to add tasks, gateways and events.Give the process a unique ID, for example:
`myProcess`
### Step 2 — Add Java Business Logic
Go to:       `src/main/java/org/example/insurance/server/`
Create:      `MyService.java`
This file does the business work.
### Step 3 — Add a REST API
In the same `server/` folder, create:       `MyResource.java`
This file receives HTTP requests from CIB seven and calls `MyService` Example:
```java
@Path("/my/check")
public class MyResource {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String check() {
        return "OK";
    }
}
```
Add the needed Java imports.
### Step 4 — Connect BPMN to Java
In your BPMN file, add a **Service Task** with an HTTP Connector. Example settings:
```
Connector ID: http-connector
URL:          http://app:8081/my/check
Method:       GET
```
Use `app` as the host name inside Docker. Map the HTTP result to a BPMN variable if needed.
### Step 5 — Deploy the BPMN File
Run:
```bash
curl -s -X POST localhost:8080/engine-rest/deployment/create \
  -F "deployment-name=my-process" \
  -F "data=@src/main/resources/bpmn/my-process.bpmn"
```
Change `my-process` to your file name.
### Step 6 — Start the Process
Run:
```bash
curl -X POST localhost:8080/engine-rest/process-definition/key/myProcess/start \
  -H "Content-Type: application/json" \
  -d '{"variables":{}}'
```
Use your BPMN process ID.
For a new user-facing API, add a new method in `ClaimResource.java` or create another Resource.
## 6. How to Update a BPMN Process
1. Open the `.bpmn` file.
2. Edit and save it.
3. Run the deployment command again.
4. CIB seven creates a new process version.
**Important:** Saving a BPMN file does not deploy it.
## 7. How to Test
Run Java tests:
```bash
./mvnw test -q
```
Start the existing coverage process:
```bash
curl -X POST localhost:8081/claims \
  -H "Content-Type: application/json" \
  -d '{"policyStart":"2026-01-01","policyEnd":"2026-12-31","treatmentDate":"2026-06-15"}'
```
The response should show:
+ "ended": true`
This means the process has ended. Check the process history to see which path was used.
## 8. Team Rules
- **BPMN:** Process steps and decisions.
- **Service:** Java business logic.
- **Resource:** HTTP API.
- **Client:** Calls CIB seven.
- **Tests:** Check business rules.
- **No External Task Workers.**
- **Do not add Java code inside BPMN.**
- **Deploy BPMN again after each BPMN change.**