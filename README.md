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

## User management

### List all users

`$ curl localhost:8080/controller/persons/ -c cookie.txt`

### Create new user

Find **XSRF-TOKEN** in `cookie.txt`

`$ curl -X POST localhost:8080/controller/persons/
-H "Content-type:application/json"
-H "Cookie: XSRF-TOKEN=8a2cecc9-a7ea-4ef5-9b19-8caea71c68cc" -H "X-XSRF-TOKEN:8a2cecc9-a7ea-4ef5-9b19-8caea71c68cc" -u admin:password
-d "Doo"`

### Delete user

`curl -X DELETE localhost:8080/controller/persons/5670ee20-d8ac-3075-b402-9a8b4839a454
-H "Cookie: XSRF-TOKEN=fabe1658-6e15-4cbf-b0bd-538bafad6cd8" -H "X-XSRF-TOKEN:fabe1658-6e15-4cbf-b0bd-538bafad6cd8" -u admin:password`

## Player management

### List all players

`$ curl localhost:8080/controller/players/ -u user:password -c cookie.txt`

### Create new player

Find **XSRF-TOKEN** in `cookie.txt`

`$ curl -X POST localhost:8080/controller/players/
-H "Content-type:application/json"
-H "Cookie: XSRF-TOKEN=f9923651-c287-4f19-a1d0-6bbad646ff01" -H "X-XSRF-TOKEN:f9923651-c287-4f19-a1d0-6bbad646ff01" -u user:password
-d {\"lastName\":\"Doo\"}`

### Update player

`$ curl -X PUT localhost:8080/controller/players/ddba8655-4c9b-4760-81c0-e32448866550 -H "Content-type:application/json"
-H "Cookie: XSRF-TOKEN=f9923651-c287-4f19-a1d0-6bbad646ff01" -H "X-XSRF-TOKEN:f9923651-c287-4f19-a1d0-6bbad646ff01" -u user:password
-d {\"lastName\":\"Doo2\"}`

### Find player by id

`$ curl localhost:8080/controller/players/ddba8655-4c9b-4760-81c0-e32448866550 -u user:password`

### History for player by id

`$ curl localhost:8080/controller/players/history/ddba8655-4c9b-4760-81c0-e32448866550 -u admin:password`

### Delete player by id

`curl -X DELETE localhost:8080/controller/players/ddba8655-4c9b-4760-81c0-e32448866550
-H "Cookie: XSRF-TOKEN=f9923651-c287-4f19-a1d0-6bbad646ff01" -H "X-XSRF-TOKEN:f9923651-c287-4f19-a1d0-6bbad646ff01" -u user:password`

[![Codacy Badge](https://api.codacy.com/project/badge/Grade/53ba623ba8c3475693088b60067b1a7b)](https://app.codacy.com/gh/ak-git/SimpleSpringApp?utm_source=github.com&utm_medium=referral&utm_content=ak-git/SimpleSpringApp&utm_campaign=Badge_Grade_Settings)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=ak-git_SimpleSpringApp&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=ak-git_SimpleSpringApp)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=ak-git_SimpleSpringApp&metric=reliability_rating)](https://sonarcloud.io/summary/new_code?id=ak-git_SimpleSpringApp)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=ak-git_SimpleSpringApp&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=ak-git_SimpleSpringApp)

[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=ak-git_SimpleSpringApp&metric=bugs)](https://sonarcloud.io/summary/new_code?id=ak-git_SimpleSpringApp)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=ak-git_SimpleSpringApp&metric=code_smells)](https://sonarcloud.io/summary/new_code?id=ak-git_SimpleSpringApp)
[![Duplicated Lines (%)](https://sonarcloud.io/api/project_badges/measure?project=ak-git_SimpleSpringApp&metric=duplicated_lines_density)](https://sonarcloud.io/summary/new_code?id=ak-git_SimpleSpringApp)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=ak-git_SimpleSpringApp&metric=vulnerabilities)](https://sonarcloud.io/summary/new_code?id=ak-git_SimpleSpringApp)