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
        stage('Clean Workspace') {
            steps {
                deleteDir()
                checkout scm
            }
        }

        // stage('Unit Tests') {
        //     steps {
        //         sh 'mvn clean test'
        //     }
        //     post {
        //         always {
        //             junit '**/target/surefire-reports/*.xml'
        //         }
        //     }
        // }

        stage('Build') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
            post {
                success {
                    archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
                }
            }
        }
        
        stage('Build Docker Image') {
            steps {
                script {
                    docker.build("${DOCKER_IMAGE}:${DOCKER_TAG}")
                }
            }
        }
        
        stage('Deploy to Development') {
            when {
                branch 'develop'
            }
            steps {
                script {
                    // Create network if not exists
                    sh "docker network create ${DOCKER_NETWORK} || true"
                    
                    // Stop and remove existing containers
                    sh """
                        docker container rm -f ${APP_NAME} ${DB_NAME} || true
                    """
                    
                    // Start MariaDB container
                    sh """
                        docker run -d \
                            --name ${DB_NAME} \
                            --network ${DOCKER_NETWORK} \
                            -p ${DB_PORT}:3306 \
                            -e MARIADB_ROOT_PASSWORD=${DB_PASSWORD} \
                            -e MARIADB_DATABASE=${DB_NAME} \
                            -e MARIADB_USER=${DB_USER} \
                            -e MARIADB_PASSWORD=${DB_PASSWORD} \
                            mariadb:10.6
                    """
                    
                    // Wait for MariaDB to be ready
                    sh 'sleep 30'
                    
                    // Start application container
                    sh """
                        docker run -d \
                            --name ${APP_NAME} \
                            --network ${DOCKER_NETWORK} \
                            -p ${APP_PORT}:8080 \
                            -e SPRING_PROFILES_ACTIVE=dev \
                            -e SPRING_DATASOURCE_URL=jdbc:mariadb://${DB_NAME}:3306/${DB_NAME} \
                            -e SPRING_DATASOURCE_USERNAME=${DB_USER} \
                            -e SPRING_DATASOURCE_PASSWORD=${DB_PASSWORD} \
                            ${DOCKER_IMAGE}:${DOCKER_TAG}
                    """
                }
            }
        }
        
        stage('Deploy to Production') {
            when {
                branch 'main'
            }
            steps {
                echo 'Deploying to production...'
            }
        }
    }
    
    post {
        success {
            echo """
                Application deployed successfully!
                Access the application at: http://localhost:${APP_PORT}
                Swagger UI: http://localhost:${APP_PORT}/swagger-ui/index.html
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
