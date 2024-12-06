pipeline {
    agent any
    
    tools {
        maven 'Maven'
        jdk 'JDK17'
    }
    
    environment {
        // Application
        APP_NAME = "product-catalog"
        APP_VERSION = "1.0.0"
        APP_PORT = "8080"
        
        // Database
        DB_NAME = "product_manage"
        DB_USER = "root"
        DB_PASSWORD = "root"
        DB_PORT = "3306"
        
        // Docker
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
        
        stage('Test') {
            steps {
                sh 'mvn test -Dspring.profiles.active=test'
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                }
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
                sh 'docker network create ${DOCKER_NETWORK} 2>/dev/null || true'
                
                // Clean up existing containers
                sh '''
                    docker container rm -f ${APP_NAME} ${DB_NAME} 2>/dev/null || true
                '''
                
                // Start MariaDB
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
                
                // Wait for MariaDB
                sh 'sleep 30'
                
                // Start application
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
                        ${DOCKER_IMAGE}:${DOCKER_TAG}
                '''
            }
        }
    }
    
    post {
        success {
            echo """
            =========================================
            Application deployed successfully!
            Access URLs:
            - Application: http://localhost:${APP_PORT}
            - Database: jdbc:mariadb://localhost:${DB_PORT}/${DB_NAME}
            =========================================
            """
        }
        failure {
            echo 'Pipeline failed! Check the logs for details.'
        }
        always {
            deleteDir()
        }
    }
}
