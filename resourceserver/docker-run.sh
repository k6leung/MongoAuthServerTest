#!/bin/bash

docker run -p 8081:8081 --name oauth2testResourceServer --env-file resource-server.env -d docker.io/library/resourceserver:0.0.1-SNAPSHOT
