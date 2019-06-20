FROM gradle
COPY . /home/hii/src/
RUN     cd /home/hii/src && \
        gradle jar --info && \
        mv ./build/bin/*.jar ..
FROM openjdk:8-jre-slim
COPY --from=0  /home/hii/thing-api.jar /home/hii/thing-api.jar
CMD java -jar /home/hii/thing-api.jar
