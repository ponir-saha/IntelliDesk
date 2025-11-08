FROM eclipse-temurin:17-jre-alpine
VOLUME /tmp
RUN mkdir -p /app/uploads
COPY target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
EXPOSE 8083
