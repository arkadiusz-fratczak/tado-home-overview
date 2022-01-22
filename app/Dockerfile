FROM adoptopenjdk:11-jre-hotspot

MAINTAINER Arkadiusz Fratczak<arkadiusz.ef@gmail.com>

RUN groupadd -g 499 app \
    && useradd -u 499 -g 499 -s /bin/false -M -d /opt/app app \
    && mkdir -p /opt/app \
    && chown -R app:app /opt/app

WORKDIR /opt/app
USER app

ARG JAR_FILE
ADD target/${JAR_FILE} /opt/app/home-overview.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/opt/app/home-overview.jar"]