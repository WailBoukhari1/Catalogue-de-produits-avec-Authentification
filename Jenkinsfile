pipeline {
    agent any
    
    environment {
        DOCKER_IMAGE = "product-catalog"
        DOCKER_TAG = "${BUILD_NUMBER}"
        DOCKER_NETWORK = "product-catalog-app_app-network"
        DB_USERNAME = credentials('db-username')
        DB_PASSWORD = credentials('db-password')
    }
    
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        
        stage('Build and Test') {
            steps {
                script {
                    // Use Maven Docker image to build and test
                    docker.image('maven:3.9.5-eclipse-temurin-17-alpine').inside {
                        sh '''
                            mvn clean package
                        '''
                    }
                }
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
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
        
        stage('Deploy') {
            steps {
                script {
                    sh """
                        docker stop product-catalog-app || true
                        docker rm product-catalog-app || true
                        
                        docker run -d \\
                            --name product-catalog-app \\
                            --network ${DOCKER_NETWORK} \\
                            -p 8082:8080 \\
                            -e SPRING_DATASOURCE_URL=jdbc:mariadb://db:3306/product_manage \\
                            -e SPRING_DATASOURCE_USERNAME=${DB_USERNAME} \\
                            -e SPRING_DATASOURCE_PASSWORD=${DB_PASSWORD} \\
                            -e SPRING_PROFILES_ACTIVE=prod \\
                            ${DOCKER_IMAGE}:${DOCKER_TAG}
                            
                        docker image prune -f
                    """
                }
            }
        }
    }
    
    post {
        always {
            node {
                deleteDir()
            }
        }
        success {
            echo 'Pipeline completed successfully!'
        }
        failure {
            echo 'Pipeline failed!'
        }
    }
}