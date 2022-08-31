FROM openjdk:18-jdk-alpine
VOLUME /tmp
COPY target/*.jar app.jar
EXPOSE 7050
ENTRYPOINT ["java","-jar","/app.jar"]
