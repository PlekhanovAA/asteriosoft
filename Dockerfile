FROM maven:3.8.6-openjdk-18 AS builder
COPY . /asteriosoftTest
WORKDIR /asteriosoftTest
RUN mvn install "-X" "-Dmaven.test.skip=true"

FROM openjdk:18-slim
ENV TZ=Europe/Moscow
RUN echo "Europe/Moscow" > /etc/timezone
COPY --from=builder asteriosoftTest/target/asteriosoftTest.jar asteriosoftTest.jar
ENTRYPOINT ["java","-jar","/asteriosoftTest.jar"]
EXPOSE 8080