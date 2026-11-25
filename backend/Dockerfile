FROM maven:3.9.11-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY backend/pom.xml ./pom.xml
COPY backend/src ./src
RUN mvn --batch-mode --no-transfer-progress -DskipTests package

FROM eclipse-temurin:21-jre
RUN apt-get update \
    && apt-get install -y --no-install-recommends tesseract-ocr curl \
    && mkdir -p /app/tessdata \
    && curl -fsSL https://raw.githubusercontent.com/tesseract-ocr/tessdata_best/main/eng.traineddata -o /app/tessdata/eng.traineddata \
    && curl -fsSL https://raw.githubusercontent.com/tesseract-ocr/tessdata_best/main/por.traineddata -o /app/tessdata/por.traineddata \
    && rm -rf /var/lib/apt/lists/*
ENV TESSDATA_PREFIX=/app/tessdata
WORKDIR /app
COPY --from=build /workspace/target/estante-api-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
