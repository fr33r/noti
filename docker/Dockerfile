FROM openjdk:8
LABEL maintainer="Jon Freer"
LABEL maintainerEmail="freerjm@miamioh.edu"

COPY target/noti*.jar ~
COPY src/main/java/configuration/configuration.yml ~
CMD java -jar noti*.jar server configuration.yml
