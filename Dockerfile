FROM openjdk:17-jdk-alpine
#LABEL authors="deosi"

EXPOSE 8081

COPY ./target/cloudstorage-0.0.1-SNAPSHOT.jar myapp.jar

CMD ["java","-jar","myapp.jar"]