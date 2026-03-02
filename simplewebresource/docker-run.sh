#!/bin/bash

docker run -p 8090:8090 --name oauth2testSimpleWebResource --env-file simple-resource.env -d docker.io/library/simplewebresource:0.0.1-SNAPSHOT
