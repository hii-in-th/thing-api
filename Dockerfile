FROM openjdk:8-jre-slim
RUN apt-get update; \
    apt-get install -y curl; \
    curl -L -O https://artifacts.elastic.co/downloads/beats/filebeat/filebeat-7.3.0-amd64.deb; \
    dpkg -i filebeat-7.3.0-amd64.deb; \
    apt-get remove -y curl; \
    apt-get autoremove; \
    apt-get purge -y --auto-remove -o APT::AutoRemove::RecommendsImportant=false; \
    apt-get autoclean;

COPY build/bin/thing-api.jar /home/hii/thing-api.jar
COPY filebeat.yml /etc/filebeat/filebeat.yml
WORKDIR /home/hii/
CMD service filebeat start; \
    java -Dfile.encoding=UTF-8 -jar thing-api.jar
