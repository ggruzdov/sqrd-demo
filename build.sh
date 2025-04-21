#!/bin/bash

./mvnw clean package -DskipTests=true &&
docker build --no-cache -t ggruzdov/sqrd-demo:1.0 .