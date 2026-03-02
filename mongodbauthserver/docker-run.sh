#!/bin/bash

docker run -p 9000:9000 -v "$(pwd)"/jwttest2.pkcs12:/workspace/jwttest2.pkcs12  --name mongodbauthserver --env-file mongotest.env -d docker.io/library/mongodbauthserver:0.0.1-SNAPSHOT

docker network connect mongoCluster mongodbauthserver

docker restart mongodbauthserver