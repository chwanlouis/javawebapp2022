# Java WebApp project

This microservice project make use of finance data api to generate financial metrics. Financial data provider is [Alphavantage]('https://www.alphavantage.co/'). 

## 1. Installation

### Step 1: Install Docker

Please install docker for your own OS and execute the following command to create network bridge

```bash
docker network create -d bridge mongo-network            # create bridge for containers
docker network ls                                        # view bridge network
```

Create mongodb container and database setting
```bash
docker run -p 127.0.0.1:27017:27017 --network mongo-network --name mongo_db -d mongo
                                                         # create container with mongodb image
```

Now your mongodb container is named as "mongo_db". Execute the following command in mongodb console
```
use admin;

db.createUser({
    user: "rootuser",
    pwd: "rootpass",
    roles: [{ role: "userAdminAnyDatabase", db: "admin" }]
})                                                       # create admin user

db.auth("rootuser", "rootpass")                          # verify user
```

Create mongo-express container (optional)
```bash
docker run --name mongo_express --network mongo-network -e ME_CONFIG_MONGODB_SERVER=mongo_db
  -e ME_CONFIG_MONGODB_ADMINUSERNAME=rootuser -e ME_CONFIG_MONGODB_ADMINPASSWORD=rootpass
  -p 127.0.0.1:8081:8081 -d mongo-express:latest         # create container with mongo-express image
```

### Step 2: Build Docker Image and container configuration

Using the following command to build the docker image
```bash
docker build -t [image name] .                           # create container with mongo-express image
docker build -t javawebapp .                             # use javawebapp as image name
```

Create a container with the build java image
```bash
docker run -p 127.0.0.1:9000:9000 --network mongo-network --name [java app container name]
-d [image name] --dns 1.1.1.1 --server.port=[your port] --spring.data.mongodb.host=[db host name]
docker run -p 127.0.0.1:9000:9000 --network mongo-network --name javaapp
-d javawebapp --dns 1.1.1.1 --server.port=9000
--spring.data.mongodb.host=mongo_db                      # example
```

## 2. Usage

### I) 

## 3. Test Case

calling daily time series data
i) wrong symbol
ii) correct symbol, 

## 4. Software Design

## 5. Enhancement in the future

scheduler, optimising API (5 API requests per minute and 500 requests per day)
pre loading major index constituents as symbol recommender

## 6. Personal comment