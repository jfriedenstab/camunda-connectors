FROM eclipse-temurin:17.0.8_7-jre

RUN mkdir /opt/app
COPY target/*-with-dependencies.jar /opt/app/

# Using the start script from the base connector runtime image
COPY start.sh /start.sh
RUN chmod +x start.sh

# Create an unprivileged user / group and switch to that user
RUN addgroup --gid 1003 connectors && useradd -g connectors -u 1003 -M connectors
USER connectors:connectors

ENTRYPOINT ["/start.sh"]
