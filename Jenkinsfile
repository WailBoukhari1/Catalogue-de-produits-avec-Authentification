pipeline {
    agent {
        docker {
            image 'maven:3.9.5-eclipse-temurin-17-alpine'
            args '-v /var/run/docker.sock:/var/run/docker.sock -v $HOME/.m2:/root/.m2'
        }
    }
    
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
                sh '''
                    mvn clean verify \
                        -Dspring.profiles.active=test \
                        -Dspring.datasource.url=jdbc:h2:mem:testdb \
                        -Dspring.datasource.username=sa \
                        -Dspring.datasource.password=
                '''
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
        
        stage('Security Scan') {
            steps {
                sh 'mvn dependency-check:check'
            }
            post {
                always {
                    dependencyCheckPublisher pattern: '**/dependency-check-report.xml'
                }
            }
        }
        
        stage('Build Docker Image') {
            steps {
                script {
                    sh '''
                        apk add --no-cache docker-cli
                        docker build -t ${DOCKER_IMAGE}:${DOCKER_TAG} \
                            --build-arg JAR_FILE=target/*.jar \
                            --build-arg DB_USERNAME=${DB_CREDS_USR} \
                            --build-arg DB_PASSWORD=${DB_CREDS_PSW} \
                            .
                    '''
                }
            }
        }
        
        stage('Deploy') {
            steps {
                script {
                    sh """
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
                            
                        docker image prune -f
                    """
                }
            }
        }
    }
    
    post {
        always {
            cleanWs()
            sh 'docker system prune -f'
        }
        success {
            echo 'Pipeline completed successfully!'
            // Add notification steps here (email, Slack, etc.)
        }
        failure {
            echo 'Pipeline failed!'
            // Add notification steps here (email, Slack, etc.)
        }
    }
}