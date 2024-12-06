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
        
        stage('Build and Unit Tests') {
            steps {
                sh '''
                    mvn clean test \
                        -Dspring.profiles.active=test \
                        -Dmaven.test.failure.ignore=true \
                        -Dsurefire.useFile=false
                '''
            }
            post {
                always {
                    junit(
                        allowEmptyResults: true,
                        keepLongStdio: true,
                        testResults: '**/target/surefire-reports/*.xml',
                        skipMarkingBuildUnstable: true
                    )
                    
                    jacoco(
                        execPattern: '**/target/jacoco.exec',
                        classPattern: '**/target/classes',
                        sourcePattern: '**/src/main/java'
                    )
                }
            }
        }
        
        stage('Integration Tests') {
            steps {
                sh '''
                    mvn verify -Dspring.profiles.active=test \
                        -DskipUnitTests=true \
                        -Dfailsafe.rerunFailingTestsCount=2
                '''
            }
            post {
                always {
                    junit(
                        allowEmptyResults: true,
                        keepLongStdio: true,
                        testResults: '**/target/failsafe-reports/*.xml'
                    )
                }
            }
        }
        
        stage('Package') {
            steps {
                sh 'mvn package -DskipTests -Dspring.profiles.active=prod'
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            }
        }
        
        stage('Build Docker Image') {
            steps {
                sh '''
                    docker build -t ${DOCKER_IMAGE}:${DOCKER_TAG} \
                        --build-arg PROFILE=prod \
                        --build-arg DB_URL=jdbc:mariadb://db:3306/${DB_NAME} \
                        --build-arg DB_USERNAME=root \
                        --build-arg DB_PASSWORD=root \
                        .
                    
                    docker tag ${DOCKER_IMAGE}:${DOCKER_TAG} ${DOCKER_IMAGE}:latest
                '''
            }
        }
        
        stage('Deploy') {
            steps {
                sh '''
                    # Create network if it doesn't exist
                    docker network create app-network || true
                    
                    # Stop and remove existing containers
                    docker stop ${APP_NAME} || true
                    docker rm ${APP_NAME} || true
                    
                    # Run MariaDB if not running
                    docker run -d \
                        --name db \
                        --network app-network \
                        -e MYSQL_ROOT_PASSWORD=root \
                        -e MYSQL_DATABASE=${DB_NAME} \
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
