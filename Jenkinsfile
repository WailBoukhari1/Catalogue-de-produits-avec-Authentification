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
        
        // SonarQube
        SONAR_PROJECT_KEY = "product-catalog"
        SONAR_SERVER = "SonarQube"
    }
    
    stages {
        stage('Code Quality Check') {
            steps {
                // Run SonarLint analysis
                withSonarQubeEnv(SONAR_SERVER) {
                    sh 'mvn sonar:sonar ' +
                       '-Dsonar.projectKey=${SONAR_PROJECT_KEY} ' +
                       '-Dsonar.java.binaries=target/classes'
                }
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
        
        stage('Integration Tests') {
            steps {
                sh 'mvn verify -Dspring.profiles.active=test -DskipUnitTests'
            }
            post {
                always {
                    junit '**/target/failsafe-reports/*.xml'
                }
            }
        }
        
        stage('Security Scan') {
            steps {
                // OWASP Dependency Check
                sh 'mvn org.owasp:dependency-check-maven:check'
            }
            post {
                always {
                    publishHTML([
                        allowMissing: false,
                        alwaysLinkToLastBuild: true,
                        keepAll: true,
                        reportDir: 'target',
                        reportFiles: 'dependency-check-report.html',
                        reportName: 'OWASP Dependency Check'
                    ])
                }
            }
        }
        
        stage('Build') {
            steps {
                sh 'mvn clean package -DskipTests -Dspring.profiles.active=prod'
            }
        }
        
        stage('Build Docker Image') {
            steps {
                sh "docker build -t ${DOCKER_IMAGE}:${DOCKER_TAG} ."
            }
        }
        
        stage('Deploy') {
            steps {
                // Create network
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
                
                // Deploy application
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
                        -e SPRING_JPA_SHOW_SQL=false \
                        ${DOCKER_IMAGE}:${DOCKER_TAG}
                '''
            }
        }
        
        stage('API Documentation') {
            steps {
                // Generate and publish Swagger documentation
                sh 'mvn springdoc-openapi:generate'
                publishHTML([
                    allowMissing: false,
                    alwaysLinkToLastBuild: true,
                    keepAll: true,
                    reportDir: 'target/swagger-ui',
                    reportFiles: 'index.html',
                    reportName: 'API Documentation'
                ])
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
            - API Documentation: http://localhost:${APP_PORT}/swagger-ui.html
            - Database: jdbc:mariadb://localhost:${DB_PORT}/${DB_NAME}
            =========================================
            """
        }
        failure {
            echo 'Pipeline failed! Check the logs for details.'
        }
        always {
            // Clean workspace
            deleteDir()
            
            // Send notification (example with email)
            emailext (
                subject: "Pipeline ${currentBuild.result}: ${env.JOB_NAME}",
                body: """
                    Pipeline Status: ${currentBuild.result}
                    Job: ${env.JOB_NAME}
                    Build Number: ${env.BUILD_NUMBER}
                    Build URL: ${env.BUILD_URL}
                """,
                recipientProviders: [[$class: 'DevelopersRecipientProvider']]
            )
        }
    }
}
