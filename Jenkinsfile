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
            script {
                node('built-in') {  // Specify a node explicitly
                    cleanWs()
                    sh '''
                        if command -v docker &> /dev/null; then
                            docker system prune -f || true
                        fi
                    '''
                }
            }
        }
        success {
            script {
                node('built-in') {  // Specify a node explicitly
                    echo 'Pipeline completed successfully!'
                    emailext (
                        subject: "Pipeline Success: ${currentBuild.fullDisplayName}",
                        body: "The pipeline completed successfully.",
                        to: '${DEFAULT_RECIPIENTS}'
                    )
                }
            }
        }
        failure {
            script {
                node('built-in') {  // Specify a node explicitly
                    echo 'Pipeline failed!'
                    emailext (
                        subject: "Pipeline Failed: ${currentBuild.fullDisplayName}",
                        body: "The pipeline failed. Please check the Jenkins logs.",
                        to: '${DEFAULT_RECIPIENTS}'
                    )
                }
            }
        }
    }
}
