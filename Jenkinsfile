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
    
    options {
        buildDiscarder(logRotator(numToKeepStr: '5'))
    }
    
    stages {
        stage('Build') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }
        
        stage('Unit Tests') {
            steps {
                sh 'mvn test -Dspring.profiles.active=test'
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                }
            }
        }
        
        stage('Package') {
            steps {
                sh 'mvn package -DskipTests'
            }
        }
        
        stage('Build Docker Image') {
            steps {
                script {
                    docker.build("${DOCKER_IMAGE}:${DOCKER_TAG}")
                }
            }
        }
        
        stage('Deploy') {
            steps {
                script {
                    // Create network if it doesn't exist
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
                            mariadb:latest || true
                        
                        # Wait for MariaDB to be ready
                        sleep 15
                        
                        # Run application
                        docker run -d \
                            --name ${APP_NAME} \
                            --network app-network \
                            -p 8082:8080 \
                            -e SPRING_PROFILES_ACTIVE=prod \
                            -e SPRING_DATASOURCE_URL=jdbc:mariadb://db:3306/${DB_NAME} \
                            -e SPRING_DATASOURCE_USERNAME=root \
                            -e SPRING_DATASOURCE_PASSWORD=root \
                            ${DOCKER_IMAGE}:${DOCKER_TAG}
                    '''
                }
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
