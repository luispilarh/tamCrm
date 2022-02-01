# CRM
[![build](https://github.com/luispih/tamCrm/actions/workflows/gradle.yml/badge.svg)](https://github.com/luispih/tamCrm/actions/workflows/gradle.yml)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=luispih_tamCrm&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=luispih_tamCrm)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=luispih_tamCrm&metric=reliability_rating)](https://sonarcloud.io/summary/new_code?id=luispih_tamCrm)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=luispih_tamCrm&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=luispih_tamCrm)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=luispih_tamCrm&metric=coverage)](https://sonarcloud.io/summary/new_code?id=luispih_tamCrm)

## Prerequisites
- [docker](https://docs.docker.com/engine/install/) 
- [docker-compose](https://docs.docker.com/compose/install/)
## Quick start

To test it, you have to follow simple steps:
- Check out the project
  
       git clone https://github.com/luispih/tamCrm.git
- Config
  - Add your Github user as an admin in the user's table. You can add a new insert in file *src/main/resources/db/migration/R__createUsers.sql*
  
        INSERT INTO crmuser ( username, email, admin)   VALUES ( 'userName', 'mail', true)  ON CONFLICT DO NOTHING
  - Configure your environment, can copy file **example.env**, rename to **.env** and fill the properties
    
        DOMAIN_NAME=XXX.mailgun.org
        API_KEY=XXX
        CLIENT_ID=XXX
        CLIENT_SECRET=XXX
    Client_id and client_secret are oauth2 parameters, you can create it following these [instructions](https://docs.github.com/en/developers/apps/building-oauth-apps/creating-an-oauth-app).
 
    Domain_name and api_key are mailgun parameter. [Mailgun](https://www.mailgun.com/email-api/)
- Run
    
      docker-compose up --profile all
- Go to http://localhost:8080/swagger-ui/index.htm

## Tools and frameworks used
- Spring boot
- Intellij
- Gradle
- Minio
- Postgres
- Lombok
- Mustache
...

### Personal workflow

This command upper server minio and postgres 
    
    docker-compose up --profile dev

Run de app using gradle with local profile

    gradle bootRun --args='--spring.profiles.active="local"
    
### CI
  Created 2 workflows in github actions
  1. **build**, build and exec test and report to sonarcloud.io
  2. **coverage report**, add report about coverage to every new pull request
