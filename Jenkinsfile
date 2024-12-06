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
        DOCKER_NETWORK = "product-catalog-network"
    }
    
    stages {
        stage('Build') {
            steps {
                // Clean and package the application, skipping tests
                sh 'mvn clean package -DskipTests'
            }
        }
        
        stage('Build Docker Image') {
            steps {
                // Build the Docker image
                sh "docker build -t ${DOCKER_IMAGE}:${DOCKER_TAG} ."
            }
        }
        
        stage('Deploy') {
            steps {
                // Ensure Docker network exists
                sh '''
                    docker network inspect ${DOCKER_NETWORK} >/dev/null 2>&1 || \
                    docker network create ${DOCKER_NETWORK}
                '''
                
                // Stop and remove existing containers
                sh '''
                    docker stop ${APP_NAME} || true
                    docker rm ${APP_NAME} || true
                    docker stop ${DB_NAME} || true
                    docker rm ${DB_NAME} || true
                '''
                
                // Start MySQL container
                sh '''
                    docker run -d \
                        --name ${DB_NAME} \
                        --network ${DOCKER_NETWORK} \
                        -e MYSQL_ROOT_PASSWORD=root \
                        -e MYSQL_DATABASE=${DB_NAME} \
                        mysql:8.0
                '''
                
                // Wait for MySQL to be ready
                sh 'sleep 30'
                
                // Start application container
                sh '''
                    docker run -d \
                        --name ${APP_NAME} \
                        --network ${DOCKER_NETWORK} \
                        -p 8080:8080 \
                        -e SPRING_PROFILES_ACTIVE=prod \
                        -e SPRING_DATASOURCE_URL=jdbc:mysql://${DB_NAME}:3306/${DB_NAME} \
                        -e SPRING_DATASOURCE_USERNAME=root \
                        -e SPRING_DATASOURCE_PASSWORD=root \
                        -e SPRING_JPA_HIBERNATE_DDL_AUTO=update \
                        ${DOCKER_IMAGE}:${DOCKER_TAG}
                '''
            }
        }
    }
    
    post {
        always {
            // Clean up workspace
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
