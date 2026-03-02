#!/bin/bash

docker run --name valkey -p 6379:6379 -d --restart=always valkey/valkey

docker network create mongoCluster

docker run -d --restart always -p 27017:27017 --name mongo1 --network mongoCluster mongo:8.2.3-noble mongod --replSet myReplicaSet --bind_ip localhost,mongo1

docker run -d --restart always -p 27018:27018 --name mongo2 --network mongoCluster mongo:8.2.3-noble mongod --port 27018 --replSet myReplicaSet --bind_ip localhost,mongo2

docker run -d --restart always -p 27019:27019 --name mongo3 --network mongoCluster mongo:8.2.3-noble mongod --port 27019 --replSet myReplicaSet --bind_ip localhost,mongo3


docker exec -it mongo1 mongosh --eval "rs.initiate({
 _id: \"myReplicaSet\",
 members: [
   {_id: 0, host: \"mongo1\"},
   {_id: 1, host: \"mongo2:27018\"},
   {_id: 2, host: \"mongo3:27019\"}
 ]
})"


docker exec -it mongo1 mongosh  --eval "rs.status()"

docker exec -it mongo1 mongosh --host "myReplicaSet/mongo1:27017,mongo2:27018,mongo3:27019" --eval "use app" --eval "db.createUser({'user': 'userAdmin', 'pwd': 'zX6hX5jMjw', 'roles': [{'role':'userAdmin','db':'app'}]})"

docker exec -it mongo1 mongosh --host "myReplicaSet/mongo1:27017,mongo2:27018,mongo3:27019" --eval "use app" --eval "db.createUser({'user': 'appUser', 'pwd': 'P3qUejzbMw', 'roles': [{'role':'readWrite','db':'app'}]})"
