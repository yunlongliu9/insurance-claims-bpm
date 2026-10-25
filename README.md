# insurance-claims-bpm

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: <https://quarkus.io/>.

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:

```shell script
./mvnw quarkus:dev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at <http://localhost:8080/q/dev/>.

## Packaging and running the application

The application can be packaged using:

```shell script
./mvnw package
```

It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/quarkus-app/lib/` directory.

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:

```shell script
./mvnw package -Dquarkus.package.jar.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar target/*-runner.jar`.

## Creating a native executable

You can create a native executable using:

```shell script
./mvnw package -Dnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using:

```shell script
./mvnw package -Dnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./target/insurance-claims-bpm-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult <https://quarkus.io/guides/maven-tooling>.

# env prepration in mac
+ java 17
	+ brew install --cask temurin@17
	+ /usr/libexec/java_home -V
+ Quarkus
	+ brew install quarkusio/tap/quarkus
+ PostgreSQL：
	+ psql --version
+  ActiveMQ
	+ brew install activemq
+ Apache Camel
	+ mvn dependency:get -Dartifact=org.apache.camel:camel-core:4.14.0
+ Esper
	+ mvn dependency:get -Dartifact=com.espertech:esper-runtime:9.0.0
+ CIB seven by maven
	+ mvn dependency:get -Dartifact=org.cibseven.bpm:cibseven-engine:2.2.0
+ JPA dependency
	+ mvn dependency:get -Dartifact=io.quarkus:quarkus-hibernate-orm:3.40.1
+ PostgreSQL JDBC drive
	+ mvn dependency:get -Dartifact=io.quarkus:quarkus-jdbc-postgresql:3.40.1
+ REST / JSON
	+ mvn dependency:get -Dartifact=io.quarkus:quarkus-rest-jackson:3.40.1
+ JMS（ActiveMQ）
	+ mvn dependency:get -Dartifact=io.quarkiverse.artemis:quarkus-artemis-jms:3.10.0
+ ：Apache Camel Quarkus integration
	+ mvn dependency:get -Dartifact=org.apache.camel.quarkus:camel-quarkus-core:3.27.0
+  Camel Quarkus JMS extension to connect Apache Camel and ActiveMQ 
	+ mvn dependency:get -Dartifact=org.apache.camel.quarkus:camel-quarkus-jms:3.27.0
