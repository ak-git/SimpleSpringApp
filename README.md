# Simple SpringBoot App with Docker support

## Build docker image

`docker build -t a002k/simple-spring-app:latest .`

## Run docker image

`docker run --rm -p 8080:8080 a002k/simple-spring-app`

## Run docker image as docker-compose

up: `docker-compose up`

down: `docker-compose down`

## Use as

[localhost:8080/controller/players/](localhost:8080/controller/players/)

[localhost:8080/controller/players/1c6cefa4-9622-40c7-8d5f-6223c5f4aa0d](localhost:8080/controller/players/1c6cefa4-9622-40c7-8d5f-6223c5f4aa0d)

[localhost:8080/controller/players/history/1c6cefa4-9622-40c7-8d5f-6223c5f4aa0d](localhost:8080/controller/players/history/1c6cefa4-9622-40c7-8d5f-6223c5f4aa0d)

### List all players

`$ curl localhost:8080/controller/players/`

### Create new player

`$ curl -X POST localhost:8080/controller/players/ -H "Content-type:application/json" -d {\"lastName\":\"Doo\"}`

### Update player

`$ curl -X PUT localhost:8080/controller/players/ddba8655-4c9b-4760-81c0-e32448866550 -H "Content-type:application/json" -d {\"lastName\":\"Doo2\"}`

### Find player by id

`$ curl localhost:8080/controller/players/ddba8655-4c9b-4760-81c0-e32448866550`

### History for player by id

`$ curl localhost:8080/controller/players/history/ddba8655-4c9b-4760-81c0-e32448866550`

### Delete player by id

`curl -X DELETE localhost:8080/controller/players/ddba8655-4c9b-4760-81c0-e32448866550`

[![Codacy Badge](https://api.codacy.com/project/badge/Grade/53ba623ba8c3475693088b60067b1a7b)](https://app.codacy.com/gh/ak-git/SimpleSpringApp?utm_source=github.com&utm_medium=referral&utm_content=ak-git/SimpleSpringApp&utm_campaign=Badge_Grade_Settings)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=ak-git_SimpleSpringApp&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=ak-git_SimpleSpringApp)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=ak-git_SimpleSpringApp&metric=reliability_rating)](https://sonarcloud.io/summary/new_code?id=ak-git_SimpleSpringApp)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=ak-git_SimpleSpringApp&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=ak-git_SimpleSpringApp)

[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=ak-git_SimpleSpringApp&metric=bugs)](https://sonarcloud.io/summary/new_code?id=ak-git_SimpleSpringApp)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=ak-git_SimpleSpringApp&metric=code_smells)](https://sonarcloud.io/summary/new_code?id=ak-git_SimpleSpringApp)
[![Duplicated Lines (%)](https://sonarcloud.io/api/project_badges/measure?project=ak-git_SimpleSpringApp&metric=duplicated_lines_density)](https://sonarcloud.io/summary/new_code?id=ak-git_SimpleSpringApp)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=ak-git_SimpleSpringApp&metric=vulnerabilities)](https://sonarcloud.io/summary/new_code?id=ak-git_SimpleSpringApp)