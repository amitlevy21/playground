# Playground
An academic class project, A general-purpose infrastructure using Java and Spring framework.

## How to integrate your code into master branch
1. Open a new branch with a name describing the feature you are adding.
2. Write the code.
3. Write tests for your code.
4. Once all tests pass, open a pull request to the 'integrate' branch and merge the changes.
5. Devops will integrate the code into master branch, making sure it runs on Docker.



## How to deploy using Docker
1. Install docker, we recommend using Linux: https://docs.docker.com/install/#supported-platforms
2. Install docker-compose https://docs.docker.com/compose/install/
3. Download the [docker-compose.deploy.yml](https://github.com/amitlevy21/playground.2019A.Sheena/blob/docker_deployment/docker-compose.deploy.yml) file
4. run:
```shell
~$ docker-compose -f docker-compose.deploy.yml up
```
5. (Optional) To connect to container you may use (useful for development):
```shell
~$ docker exec -it playground /bin/bash # connect to playground container
~$ docker exec -it playground-mongo /bin/bash # connect to mongo container
```
6. Done!

## How to deploy using Maven
1. Install JDK - 1.8.0_191
2. Install JRE - 1.8.0_191
3. Install Apache Maven - https://maven.apache.org/download.cgi
4. Clone this repo.
5. cd with terminal to project folder.
6. run:
```shell
~/github/playground.2019A.Sheena$ mvn clean install
```
7. Done!
