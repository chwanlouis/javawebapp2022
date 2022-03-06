docker network create -d bridge mongo-network
docker network ls

docker run -p 127.0.0.1:27017:27017 --network mongo-network --name mongo_db -d mongo
docker run --name mongo_express --network mongo-network -e ME_CONFIG_MONGODB_SERVER=mongo_db -e ME_CONFIG_BASICAUTH_USERNAME=admin -e ME_CONFIG_BASICAUTH_PASSWORD=123456 -e ME_CONFIG_MONGODB_ADMINUSERNAME=admin -e ME_CONFIG_MONGODB_ADMINPASSWORD=123456 -p 8081:8081 -d mongo-express:latest 


docker run --name mongo_express --network mongo-network -e ME_CONFIG_MONGODB_SERVER=mongo_db -e ME_CONFIG_MONGODB_ADMINUSERNAME=rootuser -e ME_CONFIG_MONGODB_ADMINPASSWORD=rootpass -p 127.0.0.1:8081:8081 -d mongo-express:latest