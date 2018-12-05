# playground.2019A.Sheena
An academic class project, building an infrastructure using JAVA and Spring framework

## How to integrate your code into master branch
1. Open a new branch with a name describing the feature you are adding.
2. Write the code.
3. Write tests for your code.
4. Once all tests pass, open a pull request to the 'integrate' branch and merge the changes.
5. Devops will integrate the code into master branch, making sure it runs on Docker.



## How to deploy using Docker
1. Clone this repo.
2. Install docker, we recommend using Linux: https://docs.docker.com/install/#supported-platforms
2. cd with terminal to project folder.
3. run:
```shell
~/github/playground.2019A.Sheena$ docker build -t playground . && docker run --rm -it playground
```
4. (Optional) To connect to container you may use:
```shell
~/github/playground.2019A.Sheena$ docker ps # tells you what is the name of the container
~/github/playground.2019A.Sheena$ docker exec -it loving_heisenberg /bin/bash #by Name
```
5. Done!

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
