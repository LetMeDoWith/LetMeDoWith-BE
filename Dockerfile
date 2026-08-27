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

ENV TZ=Asia/Seoul
ENV JAVA_TOOL_OPTIONS="-Duser.timezone=Asia/Seoul -XX:+UseContainerSupport -Xmx1024m"

# Sentry OpenTelemetry Agent 다운로드
# ⚠️ 버전은 build.gradle의 Sentry SDK 버전과 반드시 동일하게 맞춰야 함 (버전 불일치 시 8.6.0+ 부터는 init에서 예외 발생)
ARG SENTRY_AGENT_VERSION=8.49.0
RUN wget -O sentry-opentelemetry-agent.jar \
    https://repo1.maven.org/maven2/io/sentry/sentry-opentelemetry-agent/${SENTRY_AGENT_VERSION}/sentry-opentelemetry-agent-${SENTRY_AGENT_VERSION}.jar

COPY --from=builder /project/build/libs/*SNAPSHOT.jar LetMeDoWith.jar

EXPOSE 8080

ENTRYPOINT ["java", "-javaagent:/app/sentry-opentelemetry-agent.jar", "-jar", "LetMeDoWith.jar"]