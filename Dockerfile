# Base image
FROM bellsoft/liberica-openjdk-alpine:17


# JAR 파일 복사
ARG JAR_FILE=./build/libs/LetMeDoWith-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} LetMeDoWith.jar

# 타임존 & JVM 옵션 (GC 로그, 힙덤프, JFR)
ENV TZ=Asia/Seoul
ENV JAVA_TOOL_OPTIONS="\
 -Duser.timezone=Asia/Seoul \
 -XX:+UseContainerSupport \
 -XX:MaxRAMPercentage=60 -XX:InitialRAMPercentage=30 \
 -Xlog:gc*:file=/var/log/app/gc-%t.log:time,level,tags,uptime:filecount=5,filesize=20m \
 -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/var/log/app/heap.hprof \
 -XX:StartFlightRecording=filename=/var/log/app/app.jfr,settings=profile,maxage=2d,maxsize=256m,dumponexit=true \
"

# 로그 디렉토리 생성
RUN mkdir -p /var/log/app

# 포트 오픈
EXPOSE 8080

# 실행
ENTRYPOINT ["java", "-jar", "LetMeDoWith.jar"]

# 컨테이너 실행 예시:
# docker run -d --name app \
#   --memory=700m --memory-swap=700m \
#   -p 8080:8080 \
#   -v /var/log/app:/var/log/app \
#   your-image:tag