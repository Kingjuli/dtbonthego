#!/bin/bash

# Display banner
echo "======================================================================"
echo "          DTB ON THE GO DEPLOYMENT SCRIPT                  "
echo "======================================================================"
echo

# Check for Docker and Docker Compose
echo "Checking prerequisites..."
if ! command -v docker &> /dev/null; then
    echo "Docker is not installed. Please install Docker first."
    exit 1
fi

if ! command -v docker-compose &> /dev/null; then
    echo "Docker Compose is not installed. Please install Docker Compose first."
    exit 1
fi

echo "✅ Prerequisites satisfied"
echo

# Offer to create a .env file
if [ ! -f .env ]; then
    echo "No .env file found. Would you like to create one with default values? (y/n)"
    read -r create_env
    
    if [[ "$create_env" =~ ^[Yy]$ ]]; then
        cat > .env << EOL
# Database Configuration
POSTGRES_USER=postgres
POSTGRES_PASSWORD=pgpassword123
POSTGRES_DB=dtbonthego

# JWT Configuration
JWT_SECRET=M2NmYTc2ZWYxNDkzN2MxYzBlYTUxOWY4ZmMwNTdhODBmY2QwNGE3NDIwZjhlOGI3OGQ4NmNjMmRjZTE2MzdiZA==
JWT_EXPIRATION=86400000
# Kafka Configuration
KAFKA_BOOTSTRAP_SERVERS=kafka:29092

# Service URLs
PROFILE_SERVICE_URL=http://profile-service:8081/api/v1
STORE_OF_VALUE_URL=http://store-of-value-service:8082/api/v1

# Notification Configuration
EMAIL_SENDER=noreply@dtbonthego.com
SMS_SENDER=DTB-ON-THE-GO
EOL
        echo "✅ Created .env file with default values"
    else
        echo "Proceeding without a .env file. Using default values from docker-compose.yml"
    fi
    echo
fi

# Ask if they want to build the Docker images
echo "Would you like to build the Docker images? (y/n)"
read -r build_images

if [[ "$build_images" =~ ^[Yy]$ ]]; then
    echo "Building Docker images..."
    
    # Make the build script executable and run it
    chmod +x build-images.sh
    ./build-images.sh
    
    echo "✅ Docker images built successfully"
    echo
fi

# Ask if they want to start from scratch
echo "Would you like to remove existing containers and volumes before starting? (y/n)"
read -r clean_start

if [[ "$clean_start" =~ ^[Yy]$ ]]; then
    echo "Stopping and removing existing containers and volumes..."
    docker-compose -f docker-compose.yml down -v
    echo "✅ Cleaned up existing deployment"
    echo
fi

# Start the services
echo "Starting services..."
docker-compose -f docker-compose.yml up -d

if [ $? -eq 0 ]; then
    echo "✅ Services started successfully!"
    echo
    
    echo "======================================================================"
    echo "                    DEPLOYMENT COMPLETE                               "
    echo "======================================================================"
    echo
    echo "Your DTB On The Go banking platform is now running!"
    echo
    echo "Access the services at:"
    echo "- Profile Service:        http://localhost:8081/api/v1"
    echo "- Store of Value Service: http://localhost:8082/api/v1"
    echo "- Payment Service:        http://localhost:8083/api/v1"
    echo "- Events Service:         http://localhost:8084/api/v1"
    echo
    echo "Health endpoints:"
    echo "- Profile Service:        http://localhost:9091/actuator/health"
    echo "- Store of Value Service: http://localhost:9095/actuator/health"
    echo "- Payment Service:        http://localhost:9093/actuator/health"
    echo "- Events Service:         http://localhost:9094/actuator/health"
    echo
    echo "To view logs, use: docker-compose -f docker-compose.yml logs -f [service-name]"
    echo "To stop services, use: docker-compose -f docker-compose.yml down"
    echo
else
    echo "❌ Failed to start services. Please check the logs for details."
    exit 1
fi