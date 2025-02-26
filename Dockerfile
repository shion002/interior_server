FROM eclipse-temurin:21-jdk

# JAVA_HOME을 설정
ENV JAVA_HOME=/opt/java/openjdk
ENV PATH="${JAVA_HOME}/bin:${PATH}"

WORKDIR /app

# 빌드된 JAR 파일 복사
COPY build/libs/interior-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 10000

# JAR 파일 실행
CMD ["java", "-jar", "app.jar"]
