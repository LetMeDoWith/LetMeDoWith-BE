# Stage 1: Build
FROM bellsoft/liberica-openjdk-alpine:17 AS builder

WORKDIR /project

# Gradle 빌드에 필요한 파일들만 먼저 복사 (캐싱 효율화)
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# gradlew 실행 권한 부여
RUN chmod +x gradlew

# 소스 코드 복사
COPY src src

# 프로젝트 빌드 (테스트 제외 원할 경우 -x test 추가 가능하나, 기본 clean bootJar 수행)
RUN ./gradlew clean bootJar

# Stage 2: Run
FROM bellsoft/liberica-openjdk-alpine:17

WORKDIR /app

# 타임존 & JVM 옵션 (GC 로그, 힙덤프, JFR)
ENV TZ=Asia/Seoul
ENV JAVA_TOOL_OPTIONS="\
 -Duser.timezone=Asia/Seoul \
 -XX:+UseContainerSupport \
 -Xmx768m \
"

# 빌드 스테이지에서 생성된 JAR 파일 복사
# bootJar로 생성된 jar는 build/libs에 위치함. 정확한 파일명을 모르더라도 패턴 매칭 사용
COPY --from=builder /project/build/libs/*SNAPSHOT.jar LetMeDoWith.jar

# 포트 오픈
EXPOSE 8080

# 실행
ENTRYPOINT ["java", "-jar", "LetMeDoWith.jar"]
