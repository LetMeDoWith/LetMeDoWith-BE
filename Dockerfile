FROM bellsoft/liberica-openjdk-alpine:17

ARG JAR_FILE=./build/libs/LetMeDoWith-0.0.1-SNAPSHOT.jar

COPY ${JAR_FILE} LetMeDoWith.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "-Duser.timezone=Asia/Seoul", "LetMeDoWith.jar"]