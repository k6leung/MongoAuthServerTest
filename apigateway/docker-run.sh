#!/bin/bash

docker run -p 8080:8080 --name oauth2testApiGateway --env-file apigateway.env -d docker.io/library/apigateway:0.0.1-SNAPSHOT
