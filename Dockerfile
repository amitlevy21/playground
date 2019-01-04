FROM maven:3.6-jdk-8-alpine as maven

# copy the project files
COPY ./pom.xml ./pom.xml

# build all dependencies
RUN mvn dependency:go-offline -B

# copy src files
COPY ./src ./src

# build for release
RUN mvn install -DskipTests

# our final base image
FROM openjdk:8u181-jre-alpine

# Required for starting application up.
RUN apk update && apk add bash

# copy over the built artifact from the maven image
COPY --from=maven target/playground-0.0.1-SNAPSHOT.jar ./

# start the server
CMD ["java", "-Dspring.data.mongodb.uri=mongodb://playground-mongo:27017/test","-Djava.security.egd=file:/dev/./urandom","-jar","./playground-0.0.1-SNAPSHOT.jar"]
