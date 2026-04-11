FROM maven:3.8.8-eclipse-temurin-8 AS tasks-backend-build
WORKDIR /workspace/backend

COPY pom.xml pom.xml
COPY src/ src/

RUN mvn clean package -DskipTests

FROM tasks-backend-build AS tasks-backend-tests
RUN mvn test

FROM maven:3.8.8-eclipse-temurin-8 AS tasks-frontend-build
WORKDIR /workspace/frontend

COPY tasks-frontend/pom.xml pom.xml
COPY tasks-frontend/src/ src/

RUN mvn clean package -DskipTests

FROM maven:3.8.8-eclipse-temurin-8 AS tasks-api-build
WORKDIR /workspace/api-test

COPY tasks-api-test/pom.xml pom.xml
COPY tasks-api-test/src/ src/

RUN mvn test-compile

FROM tasks-api-build AS tasks-api-tests
# These tests call http://localhost:8001/tasks-backend and need the app plus PostgreSQL running.
# Keep the stage in the Dockerfile so the pipeline has a dedicated target, but execute it in CI/runtime.
RUN echo "API tests must run after the containerized app and database are available."

FROM maven:3.8.8-eclipse-temurin-8 AS tasks-functional-build
WORKDIR /workspace/functional-test

COPY tasks-functional-tests/pom.xml pom.xml
COPY tasks-functional-tests/src/ src/

RUN mvn test-compile

FROM tasks-functional-build AS tasks-functional-tests
RUN echo "Functional tests must run with the frontend app and Selenium Grid available."

FROM tomcat:8.5.50-jdk8-openjdk AS tasks-backend-runtime

ENV DATABASE_HOST=db \
    DATABASE_PORT=5432 \
    DATABASE_USER=postgres \
    DATABASE_PASSWD=password \
    DATABASE_UPDATE=none

RUN rm -rf /usr/local/tomcat/webapps/*
COPY --from=tasks-backend-build /workspace/backend/target/tasks-backend.war /usr/local/tomcat/webapps/tasks-backend.war

EXPOSE 8080

FROM tomcat:8.5.50-jdk8-openjdk AS tasks-frontend-runtime

ENV BACKEND_HOST=backend \
    BACKEND_PORT=8080 \
    APP_VERSION=docker-prod

RUN rm -rf /usr/local/tomcat/webapps/*
COPY --from=tasks-frontend-build /workspace/frontend/target/tasks.war /usr/local/tomcat/webapps/tasks.war

EXPOSE 8080

FROM tomcat:8.5.50-jdk8-openjdk AS runtime

ARG WAR_FILE=target/tasks-backend.war
ARG CONTEXT=tasks-backend

RUN rm -rf /usr/local/tomcat/webapps/*
COPY ${WAR_FILE} /usr/local/tomcat/webapps/${CONTEXT}.war

EXPOSE 8080
