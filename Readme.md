# CRM
[![build](https://github.com/luispih/tamCrm/actions/workflows/gradle.yml/badge.svg)](https://github.com/luispih/tamCrm/actions/workflows/gradle.yml)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=luispih_tamCrm&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=luispih_tamCrm)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=luispih_tamCrm&metric=reliability_rating)](https://sonarcloud.io/summary/new_code?id=luispih_tamCrm)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=luispih_tamCrm&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=luispih_tamCrm)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=luispih_tamCrm&metric=coverage)](https://sonarcloud.io/summary/new_code?id=luispih_tamCrm)

## Requirements
- docker 
- docker-compose
## Quick start

For to test it, you have to follow simple steps:
- Checkout the proyect 
  
       git clone https://github.com/luispih/tamCrm.git
- Add your gitHub's user as admin to table user. You can add new insert in file src/main/resources/db/migration/R__createUsers.sql
  
      INSERT INTO crmuser ( username, email, admin)   VALUES ( 'userName', 'mail', true)  ON CONFLICT DO NOTHING
- Add your 
- Run
    
      docker-compose up --profile all
- Go to http://localhost:8080/swagger-ui/index.html

