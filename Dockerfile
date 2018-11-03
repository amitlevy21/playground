FROM maven:3-jdk-8

COPY . /app

WORKDIR /app

RUN mvn clean install