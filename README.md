
# POGS
The Platform for Online Group Studies (POGS) is a tool to study collective learning.

[![Travis build status](https://img.shields.io/travis/CCI-MIT/POGS.svg)](https://travis-ci.org/CCI-MIT/POGS)


## Simple download and play procedure

1. Install [Docker](https://docs.docker.com/get-docker/)
2. Clone this repository, at the repo's root copy the sample.env file to a local .env file:
```cp sample.env .env``` 
 This file has all the usernames and passwords used in the system. It is advised to change this on production setups.
3. At the repo's root directory run:

```
docker-compose up
```

On startup expect some issues with the node server, as it loads faster than the mysql initial server setup.
It will automatically restart until the mysql 

Open a browser window at :
```http://localhost:8080/initialize```

Select the file found in ```others/T1ShortDemo.zip``` or another POGS generated study configuration dump file.

And click the button ```INITIALIZE```

After confirmation go to:
```http://localhost:8080/admin```

Use the following credentials:

```
User: admin@pogs.info

Password: pogs1234
```
The first time docker will install everything we need to run POGS, it may take a while.
The next executions will be a lot faster. The .mysql/ dir will save database changes between server restarts.

To stop it just hit
```
ctrl + c
```

## Development install procedure

1 Install all dependencies
 
 - Install Java 8 JRE
 - Install mysql-server 5.7
 - Install nodejs (used for sass) version v9.11.1
 
 On macs be sure to use the java 8 home 
``` 
 "export JAVA_HOME=`/usr/libexec/java_home -v 1.8`;java -version"
```

2 On mysql create a new database:

```
CREATE SCHEMA pogs;
```

3 Open file application-database-config.yml
Adjust the username and password to the ones you configured during the mysql database installation
 
```
  username: root
  password: 1234
```
4 Make sure that redis-server is running.
```
$redis-cli ping
PONG
```
if not start it with:
```
$redis-server & 
```

5 Go to the root directory and run

```
mvn clean compile package install 
```

6 Go to Intellij and use RUN the configuration called "POGS (development)". If you don't see that configuration, you can also run the application directly from the `PlatformForOnlineGroupStudiesApplication` class. Make sure you set the active profile to development, which enables hot reloading of static files.

or run as standalone server:

7 Run as standalone server:

In the root directory:
```
java -jar target/pogs-0.0.1-SNAPSHOT.jar
```

If you are setting up the system in a RedHat remember to allow proxy to connect
https://manastri.blogspot.com/2020/07/13-permission-denied-while-connecting.html