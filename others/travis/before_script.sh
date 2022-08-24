#!/bin/bash

echo "Configuring maven options"
echo "MAVEN_OPTS='-Xmx1536m -XX:+TieredCompilation -XX:TieredStopAtLevel=1'" > ~/.mavenrc

echo "Running create database script"
mysql --version
mysql -e 'CREATE DATABASE IF NOT EXISTS pogs;'

