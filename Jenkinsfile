pipeline {
    agent any
    
    tools {
        maven 'Maven'
        jdk 'JDK17'
    }
    environment {
        DOCKER_IMAGE = "product-catalog"
        DOCKER_TAG = "${BUILD_NUMBER}"
        DOCKER_NETWORK = "product-catalog-app_app-network"
    }
    
    stages {
        stage('Checkout') {
            steps {
                checkout scm
                sh 'chmod +x mvnw'
            }
        }
        
        stage('Build') {
            steps {
                sh './mvnw clean package -DskipTests'
            }
        }
        
        stage('Test') {
            steps {
                sh './mvnw test'
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
                        docker network create ${DOCKER_NETWORK} || true
                        docker stop product-catalog-app || true
                        docker rm product-catalog-app || true
                        
                        docker run -d \\
                            --name product-catalog-app \\
                            --network ${DOCKER_NETWORK} \\
                            -p 8082:8080 \\
                            -e SPRING_DATASOURCE_URL=jdbc:mariadb://db:3306/product_manage \\
                            -e SPRING_DATASOURCE_USERNAME=root \\
                            -e SPRING_DATASOURCE_PASSWORD=root \\
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
