FROM openjdk:8-jre-alpine
EXPOSE 8080
#COPY .docker/pogsserver/pogs-0.0.1-SNAPSHOT.jar /app/pogs.jar
COPY .docker/pogsserver/application-database-config.yml /app/application-database-config.yml
COPY ./src/main/resources/plugins /app/plugins
COPY .docker/pogsserver/setupRightLocalhostChange.sh /app/setupRightLocalhostChange.sh
RUN ["chmod", "+x", "/app/setupRightLocalhostChange.sh"]
RUN ["sh","/app/setupRightLocalhostChange.sh"]

RUN apk update && apk add --no-cache curl

COPY .docker/pogsserver/downloadLatestBinaryFromGithubReleases.sh /app/downloadLatestBinaryFromGithubReleases.sh
RUN ["chmod", "+x", "/app/downloadLatestBinaryFromGithubReleases.sh"]
RUN ["sh","/app/downloadLatestBinaryFromGithubReleases.sh"]

WORKDIR /app
CMD exec java -jar pogs.jar