FROM openjdk:17-alpine
ENV _JAVA_OPTIONS="-Xms128m -Xmx1024m -Dfile.encoding=utf-8"
ENV TZ=Europe/Minsk

RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
ARG APP_PATH="/app"
ARG JAR_FILE="./target/app.jar"
COPY ${JAR_FILE} ${APP_PATH}/app.jar
WORKDIR "${APP_PATH}"

EXPOSE 8000
ENTRYPOINT ["java","-jar", "app.jar"]