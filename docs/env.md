# docker env
Docker Compose + Dockerfile + Maven Wrapper

docker compose will generally manage all services,network and volume;

1. APP (DOCKERFILE) - JDK17,Maven,Quarkus,CIB Seven,BPMN..
2. Database - Postgresql,volume...
3. Message - ActiveMQ + JMS Broker

## System env : Dockerfile + Docker Compose
+ Linux、JDK 17、Maven、PostgreSQL、ActiveMQ Classic
## Java framework env: pom.xml + Maven (As long as JDK17 + pom.xml)
+ Quarkus、CIB seven、Hibernate、JDBC、Camel、Esper

we get 2 envs;
+ Dockerfile.dev：dev env, keep JDK、Maven、Quarkus Dev Mode
+ Dockerfile：official real runtime env，after build the app only run the artefact, no need Maven


Dockerfile.dev	define Java 17、Maven 3.9 and Quarkus env
compose.yaml	define how to build,start container,and the port reflexion,code mount..

the directory:
```shell
insurance-claims-bpm/
│
├── Dockerfile.dev
├── Dockerfile
├── compose.yaml
├── .dockerignore
├── .env.example
│
├── pom.xml
├── mvnw
├── .mvn/
│
└── src/
    ├── main/
    │   ├── java/
    │   └── resources/
    │       ├── application.properties
    │       └── bpmn/
    │           └── coverage-check.bpmn
    └── test/
```



# finally
Run: `docker compose up --build`
use another terminal and run the test: 
```sh
curl -s -X POST http://localhost:8080/claims \
  -H "Content-Type: application/json" \
  -d '{
    "policyStart": "2026-01-01",
    "policyEnd": "2026-12-31",
    "treatmentDate": "2026-06-15"
  }'
```
