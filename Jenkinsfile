pipeline {
    agent any
    
    environment {
        DOCKER_IMAGE = "product-catalog"
        DOCKER_TAG = "${BUILD_NUMBER}"
        DOCKER_NETWORK = "app-network"
        DB_CREDS = credentials('db-credentials')
    }
    
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        
        stage('Build and Test') {
            steps {
                sh 'mvn clean verify -Dspring.profiles.active=test'
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                    jacoco(
                        execPattern: '**/target/jacoco.exec',
                        classPattern: '**/target/classes',
                        sourcePattern: '**/src/main/java'
                    )
                }
            }
        }
        
        stage('Build Docker Image') {
            steps {
                script {
                    sh """
                        docker build -t ${DOCKER_IMAGE}:${DOCKER_TAG} .
                    """
                }
            }
        }
        
        stage('Deploy') {
            steps {
                script {
                    sh """
                        docker network create ${DOCKER_NETWORK} || true
                        
                        docker stop ${DOCKER_IMAGE} || true
                        docker rm ${DOCKER_IMAGE} || true
                        
                        docker run -d \\
                            --name ${DOCKER_IMAGE} \\
                            --network ${DOCKER_NETWORK} \\
                            -p 8082:8080 \\
                            -e SPRING_PROFILES_ACTIVE=prod \\
                            -e SPRING_DATASOURCE_URL=jdbc:mariadb://db:3306/product_manage \\
                            -e SPRING_DATASOURCE_USERNAME=${DB_CREDS_USR} \\
                            -e SPRING_DATASOURCE_PASSWORD=${DB_CREDS_PSW} \\
                            ${DOCKER_IMAGE}:${DOCKER_TAG}
                    """
                }
            }
        }
    }
    
    post {
        always {
            cleanWs()
        }
        success {
            echo 'Pipeline completed successfully!'
        }
        failure {
            echo 'Pipeline failed!'
        }
    }
}
