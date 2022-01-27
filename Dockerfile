FROM amazoncorretto:11-alpine-jdk as builder
# If a script to run is created in a Windows machine the line endings may cause errors when being executed so we need to convert them for a unix environment
RUN apk update && apk add dos2unix

WORKDIR /usr/app

# Download gradle dependencies (before building the app to improve performance)
COPY build.gradle settings.gradle gradlew ./
COPY gradle ./gradle
RUN dos2unix gradlew
RUN chmod +x gradlew
RUN ./gradlew build || true

# Copy all the source code
ADD . .
RUN dos2unix gradlew
RUN chmod +x gradlew

ARG TEST=false
RUN if [ "$TEST" = "true" ] ; then \
        ./gradlew clean build  ; \
    else \
        echo 'Skipping test.'; \
        ./gradlew clean build -x test ; \
    fi

FROM amazoncorretto:11-alpine-jdk

RUN apk update && apk add curl

WORKDIR /usr/app

COPY --from=builder /usr/app/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT java -jar app.jar