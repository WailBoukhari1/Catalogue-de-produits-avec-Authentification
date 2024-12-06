pipeline {
    agent any
    
    tools {
        maven 'Maven'
        jdk 'JDK17'
    }
    
    options {
        buildDiscarder(logRotator(numToKeepStr: '5'))
    }
    
    stages {
        stage('Checkout') {
            steps {
                deleteDir()
                checkout scm
            }
        }
        
        stage('Build and Test') {
            steps {
                sh '''
                    mvn clean test \
                        -Dmaven.test.failure.ignore=true \
                        -Dsurefire.useFile=false
                    
                    # Ensure test results directory exists
                    mkdir -p target/surefire-reports/
                '''
            }
            post {
                always {
                    junit(
                        allowEmptyResults: true,
                        keepLongStdio: true,
                        testResults: '**/target/surefire-reports/*.xml',
                        skipMarkingBuildUnstable: true,
                        skipPublishingChecks: true
                    )
                }
            }
        }
        
        stage('Build Docker Image') {
            steps {
                sh '''
                    docker build -t product-catalog:${BUILD_NUMBER} .
                    docker tag product-catalog:${BUILD_NUMBER} product-catalog:latest
                '''
            }
        }
        
        stage('Deploy') {
            steps {
                sh '''
                    docker network create jenkins-network || true
                    docker stop product-catalog || true
                    docker rm product-catalog || true
                    
                    docker run -d \
                        --name product-catalog \
                        --network jenkins-network \
                        -p 8082:8080 \
                        -e SPRING_DATASOURCE_URL=jdbc:mariadb://jenkins-db:3306/product_manage \
                        -e SPRING_DATASOURCE_USERNAME=root \
                        -e SPRING_DATASOURCE_PASSWORD=root \
                        -e SPRING_PROFILES_ACTIVE=prod \
                        product-catalog:${BUILD_NUMBER}
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
