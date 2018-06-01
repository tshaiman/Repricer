FROM openjdk:8-jre-alpine
COPY target/repricer.jar repricer.jar
EXPOSE 8080
ENTRYPOINT ["java" , "-jar" , "repricer.jar"]