# POGS
The Platform for Online Group Studies (POGS) is a tool to study collective learning.

## Development install procedure

1 Install all dependencies
 
 - Install Java 8 JRE
 - Install mysql-server
 - Install nodejs (used for sass) version v9.11.1
 
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
