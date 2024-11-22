FROM openjdk:17-jdk
ARG JAR_FILE=build/libs/server-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} dcd-server.jar
ENV TZ=Asia/Seoul
CMD ["java", "-jar", "dcd-server.jar"]