pipeline {
    agent any
    
    tools {
        maven 'Maven'
        jdk 'JDK17'
    }
    
    environment {
        // Static values for consistency
        APP_NAME = "product-catalog"
        APP_VERSION = "1.0.0"
        APP_PORT = "8080"
        
        // Database configuration
        DB_NAME = "product_manage"
        DB_USER = "root"
        DB_PASSWORD = "root"
        DB_PORT = "3306"
        
        // Docker configuration
        DOCKER_IMAGE = "product-catalog"
        DOCKER_TAG = "latest"
        DOCKER_NETWORK = "product-network"
    }
    
    stages {
        stage('Build') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }
        
        stage('Build Docker Image') {
            steps {
                sh "docker build -t ${DOCKER_IMAGE}:${DOCKER_TAG} ."
            }
        }
        
        stage('Deploy') {
            steps {
                // Create network if not exists (quietly)
                sh 'docker network create ${DOCKER_NETWORK} 2>/dev/null || true'
                
                // Clean up existing containers (quietly)
                sh '''
                    docker container rm -f ${APP_NAME} ${DB_NAME} 2>/dev/null || true
                '''
                
                // Start MariaDB container
                sh '''
                    docker run -d \
                        --name ${DB_NAME} \
                        --network ${DOCKER_NETWORK} \
                        -p ${DB_PORT}:3306 \
                        -e MARIADB_ROOT_PASSWORD=${DB_PASSWORD} \
                        -e MARIADB_DATABASE=${DB_NAME} \
                        -e MARIADB_USER=${DB_USER} \
                        -e MARIADB_PASSWORD=${DB_PASSWORD} \
                        mariadb:10.6
                '''
                
                // Wait for MariaDB to be ready
                sh 'sleep 30'
                
                // Start Spring Boot application
                sh '''
                    docker run -d \
                        --name ${APP_NAME} \
                        --network ${DOCKER_NETWORK} \
                        -p ${APP_PORT}:8080 \
                        -e SPRING_PROFILES_ACTIVE=prod \
                        -e SPRING_DATASOURCE_URL=jdbc:mariadb://${DB_NAME}:${DB_PORT}/${DB_NAME} \
                        -e SPRING_DATASOURCE_USERNAME=${DB_USER} \
                        -e SPRING_DATASOURCE_PASSWORD=${DB_PASSWORD} \
                        -e SPRING_JPA_HIBERNATE_DDL_AUTO=update \
                        -e SPRING_JPA_SHOW_SQL=false \
                        ${DOCKER_IMAGE}:${DOCKER_TAG}
                '''
            }
        }
    }
    
    post {
        success {
            echo "Application deployed successfully at http://localhost:${APP_PORT}"
        }
        failure {
            echo 'Deployment failed!'
        }
        always {
            deleteDir()
        }
    }
}