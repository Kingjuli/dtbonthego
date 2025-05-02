#!/bin/bash

echo "======================================================================"
echo "          DTB-ON-THE-GO DOCKER IMAGE BUILD SCRIPT                     "
echo "======================================================================"
echo

# Build each service using Maven and then build Docker images manually
echo "Building services and Docker images..."

# Build Profile Service
echo "Building profile-service..."
cd profile-service
mvn clean package -DskipTests
docker build -t dtbonthego/profile-service:latest -f Dockerfile .
cd ..
echo "✅ profile-service image built successfully"
echo

# Build Store of Value Service
echo "Building store-of-value-service..."
cd store-of-value-service
mvn clean package -DskipTests
docker build -t dtbonthego/store-of-value-service:latest -f Dockerfile .
cd ..
echo "✅ store-of-value-service image built successfully"
echo

# Build Payment Service
echo "Building payment-service..."
cd payment-service
mvn clean package -DskipTests
docker build -t dtbonthego/payment-service:latest -f Dockerfile .
cd ..
echo "✅ payment-service image built successfully"
echo

# Build Events Service
echo "Building events-service..."
cd events-service
mvn clean package -DskipTests
docker build -t dtbonthego/events-service:latest -f Dockerfile .
cd ..
echo "✅ events-service image built successfully"
echo

echo "======================================================================"
echo "                 ALL IMAGES BUILT SUCCESSFULLY                         "
echo "======================================================================"
echo 
echo "Images built:"
docker images dtbonthego/*
echo
echo "You can now run the services using:"
echo "docker-compose -f docker-compose.yml up -d"
echo