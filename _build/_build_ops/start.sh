#!/bin/bash

cd "$(dirname "$0")"

# Build and start the app containers
cd ../_build_z-one-company-main-starter
docker build -t z-one-company-app .
docker run -d --name app-dev -p 8888:8888 -e SPRING_PROFILES_ACTIVE=dev z-one-company-app

# Start nginx
cd "$(dirname "$0")"
docker-compose up -d

echo "Done!"
echo "Access:"
echo "  - Dev: http://one.company.dev/"
echo "  - Prod: http://one.company.prod/"
