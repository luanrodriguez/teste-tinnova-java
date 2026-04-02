FROM maven:3.9.6-amazoncorretto-21-debian
RUN mkdir /app
WORKDIR /app
COPY target/*.jar /app/app.jar
CMD ["java","-jar","/app/app.jar"]