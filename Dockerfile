FROM openjdk:11-jdk-slim
FROM maven:3.6.3-jdk-11-slim

MAINTAINER Emanuel Almirante, emanuelalmirante@gmail.com

ENV TZ=Europe/Lisbon

COPY src /home/app/src
COPY pom.xml /home/app
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
RUN mvn -f /home/app/pom.xml clean package

ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]

EXPOSE 8080:8080