FROM openjdk:8-jre-alpine
EXPOSE 8080

COPY .docker/pogsserver/application-database-config.yml /app/application-database-config.yml
COPY ./src/main/resources/plugins /app/plugins
COPY .docker/pogsserver/setupRightLocalhostChange.sh /app/setupRightLocalhostChange.sh
RUN ["chmod", "+x", "/app/setupRightLocalhostChange.sh"]
RUN ["sh","/app/setupRightLocalhostChange.sh"]

#[LOCAL START]If running local copy enable this line and disable SERVER.
#COPY .docker/pogsserver/pogs-0.0.1-SNAPSHOT.jar /app/pogs.jar
#[LOCAL END]

#If running on server setup get latest relase from github
#[SERVER START]
RUN apk update && apk add --no-cache curl
COPY .docker/pogsserver/downloadLatestBinaryFromGithubReleases.sh /app/downloadLatestBinaryFromGithubReleases.sh
RUN ["chmod", "+x", "/app/downloadLatestBinaryFromGithubReleases.sh"]
RUN ["sh","/app/downloadLatestBinaryFromGithubReleases.sh"]
#[SERVER END]

WORKDIR /app
CMD exec java -jar pogs.jar