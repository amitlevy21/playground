FROM openjdk:8-alpine

# Required for starting application up.
RUN apk update && apk add bash

RUN mkdir -p /opt/app
ENV PROJECT_HOME /opt/app

COPY . $PROJECT_HOME
WORKDIR $PROJECT_HOME

RUN ./mvnw install -DskipTests

CMD ["java", "-Dspring.data.mongodb.uri=mongodb://springboot-mongo:27017/springmongo-demo","-Djava.security.egd=file:/dev/./urandom","-jar","./playground-0.0.1-SNAPSHOT.jar"]