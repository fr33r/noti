FROM openjdk:8
LABEL maintainer="Jon Freer"
LABEL maintainerEmail="freerjm@miamioh.edu"

COPY target/noti*.jar noti/noti.jar
COPY src/main/java/configuration/configuration.yml noti/configuration.yml
CMD java -jar noti/noti.jar server noti/configuration.yml
