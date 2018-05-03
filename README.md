# POGS
The Platform for Online Group Studies (POGS) is a tool to study collective learning.

##Development install procedure

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

4 Go to the root directory and run

```
mvn clean compile package install 
```

5 Go to Intellij and use RUN the app named PlatformForOnlineGroupStudiesApplication