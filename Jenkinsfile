pipeline {
    agent any
    
    tools {
        maven 'Maven'
        jdk 'JDK17'
    }
    
    environment {
        DOCKER_IMAGE = "product-catalog"
        DOCKER_TAG = "${BUILD_NUMBER}"
        APP_NAME = "product-catalog"
        DB_NAME = "product_manage"
        DB_PASSWORD = "root"
        DOCKER_NETWORK = "product-catalog-network"
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
                // Create network if not exists
                sh '''
                    docker network inspect ${DOCKER_NETWORK} >/dev/null 2>&1 || \
                    docker network create ${DOCKER_NETWORK}
                '''
                
                // Clean up existing containers
                sh '''
                    docker stop ${APP_NAME} || true
                    docker rm ${APP_NAME} || true
                    docker stop ${DB_NAME} || true
                    docker rm ${DB_NAME} || true
                '''
                
                // Start MariaDB container
                sh '''
                    docker run -d \
                        --name ${DB_NAME} \
                        --network ${DOCKER_NETWORK} \
                        -e MARIADB_ROOT_PASSWORD=${DB_PASSWORD} \
                        -e MARIADB_DATABASE=${DB_NAME} \
                        mariadb:latest
                '''
                
                // Wait for MariaDB to be ready
                sh 'sleep 30'
                
                // Start Spring Boot application
                sh '''
                    docker run -d \
                        --name ${APP_NAME} \
                        --network ${DOCKER_NETWORK} \
                        -p 8080:8080 \
                        -e SPRING_PROFILES_ACTIVE=prod \
                        -e SPRING_DATASOURCE_URL=jdbc:mariadb://${DB_NAME}:3306/${DB_NAME} \
                        -e SPRING_DATASOURCE_USERNAME=root \
                        -e SPRING_DATASOURCE_PASSWORD=${DB_PASSWORD} \
                        -e SPRING_JPA_HIBERNATE_DDL_AUTO=update \
                        ${DOCKER_IMAGE}:${DOCKER_TAG}
                '''
            }
        }
    }
    
    post {
        always {
            deleteDir()
        }
        success {
            echo 'Pipeline completed successfully!'
        }
        failure {
            echo 'Pipeline failed!'
        }
    }
}
