FROM maven:3-jdk-8

COPY . /app

WORKDIR /app

RUN mvn clean install

# keep container alive
#ENTRYPOINT [ "tail", "-f", "/etc/hosts" ] 