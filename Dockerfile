FROM openjdk:8-jre-slim
COPY build/bin/thing-api.jar /home/hii/thing-api.jar
WORKDIR /home/hii/
CMD java -jar thing-api.jar
