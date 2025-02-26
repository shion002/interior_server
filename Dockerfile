# Java 21을 포함한 이미지 사용
FROM eclipse-temurin:21-jdk

# JAVA_HOME 환경변수 설정
ENV JAVA_HOME=/opt/java/openjdk
ENV PATH="${JAVA_HOME}/bin:${PATH}"

# Gradle과 필요한 도구 설치
RUN apt-get update && apt-get install -y gradle

# 작업 디렉토리 설정
WORKDIR /app

# Gradle wrapper와 필요한 파일들을 복사
COPY gradlew /app/gradlew
COPY gradle /app/gradle
COPY build.gradle /app/build.gradle
COPY settings.gradle /app/settings.gradle
COPY src /app/src

# 권한 설정 (Windows에서 복사한 경우 실행 권한이 없을 수 있음)
RUN chmod +x /app/gradlew

# Gradle 빌드
RUN ./gradlew build

# 빌드된 JAR 파일을 복사
COPY build/libs/interior-0.0.1-SNAPSHOT.jar app.jar

# 컨테이너에서 열어둘 포트
EXPOSE 10000

# JAR 파일 실행 명령
CMD ["java", "-jar", "app.jar"]
