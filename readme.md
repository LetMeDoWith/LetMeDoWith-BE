# LetMeDoWith BE

## 버전 정보

- OpenJDK 19.0.2
- Gradle 8.5
- Docker 25.0.2
- Docker Compose version v2.24.3-desktop.1

Docker 내에 MariaDB와 Redis가 포함되어 있습니다.

## 실행 방법

실행 전 꼭 17버전 이상의 JDK가 설치되어있는지 확인

```text
>>> java --version
openjdk 19.0.2 2023-01-17
OpenJDK Runtime Environment (build 19.0.2+7-44)
OpenJDK 64-Bit Server VM (build 19.0.2+7-44, mixed mode, sharing)
```

### 1. Github remote repository에서 소스코드 clone

```text
>>> git clone https://github.com/LetMeDoWith/LetMeDoWith-BE.git
```

### 2. Docker 구성

`docker/local` 디렉토리에서 아래의 명령어 실행

```text
>>> docker compose up -d
```

이후 MariaDB와 Redis가 실행됨을 확인한다.

```text
>>> docker ps
CONTAINER ID   IMAGE          COMMAND                   CREATED        STATUS          PORTS                               NAMES
6e19182c8390   mysql:latest   "docker-entrypoint.s…"   3 months ago   Up 42 seconds   0.0.0.0:3306->3306/tcp, 33060/tcp   matchmyimage-be-mysql-db-1
0d53db30cd17   redis:latest   "docker-entrypoint.s…"   3 months ago   Up 42 seconds   0.0.0.0:6379->6379/tcp              matchmyimage-be-redis-server-1
```

### 3. 의존성 설치, 빌드, 테스트

#### Intellij 사용시

1. 프로젝트를 열고, IDE가 자동으로 의존성을 설치하고 인덱싱할때 까지 대기한다.
2. 이후 Gradle task중 `build` -> `build` 선택.

#### Intellij 미 사용시

프로젝트 루트 디렉토리에서 아래 명령어 실행

```text
>>> ./gradlew build
```

### 4. 실행

프로젝트 루트 디렉토리에서 아래 명령어 실행

```text
>>> ./gradlew bootrun
```
