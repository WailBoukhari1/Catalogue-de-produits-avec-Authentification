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
        APP_PORT = "8086"
        
        // Database configuration
        DB_NAME = "product_manage"
        DB_USER = "root"
        DB_PASSWORD = "root"
        DB_PORT = "3306"
        
        // Docker configuration
        DOCKER_IMAGE = "product-catalog"
        DOCKER_TAG = "latest"
        DOCKER_NETWORK = "product-network"
        
        // Added new environment variables
        MAVEN_OPTS = '-Dhttps.protocols=TLSv1.2'
        SPRING_PROFILES_ACTIVE = 'dev'
    }
    
    stages {
        stage('Verify Tools') {
            steps {
                sh '''
                    java -version
                    mvn -version
                '''
            }
        }

        stage('Clean') {
            steps {
                script {
                    writeFile file: 'settings.xml', text: '''<?xml version="1.0" encoding="UTF-8"?>
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0
                            https://maven.apache.org/xsd/settings-1.0.0.xsd">
    <mirrors>
        <mirror>
            <id>central-https</id>
            <name>Maven Central</name>
            <url>https://repo.maven.apache.org/maven2</url>
            <mirrorOf>central</mirrorOf>
        </mirror>
    </mirrors>
</settings>'''
                    
                    // Update pom.xml with clean plugin version
                    writeFile file: 'pom.xml', text: readFile('pom.xml').replaceAll(
                        '</properties>',
                        '''    <maven-clean-plugin.version>3.1.0</maven-clean-plugin.version>
                        </properties>'''
                    )
                    
                    sh 'mvn -s settings.xml clean'
                }
            }
        }

        stage('Unit Tests') {
            steps {
                sh 'mvn -s settings.xml test -Dspring.profiles.active=dev'
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                }
            }
        }

        stage('Build') {
            steps {
                sh 'mvn -s settings.xml package -DskipTests -Dspring.profiles.active=dev'
            }
            post {
                success {
                    archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
                }
            }
        }
        
        stage('Deploy') {
            steps {
                script {
                    sh '''
                        echo "Arrêt du conteneur existant s'il existe"
                        docker stop ${APP_NAME} || true
                        docker rm ${APP_NAME} || true
                        
                        echo "Construction de l'image Docker"
                        docker build -t ${APP_NAME}:latest .
                        
                        echo "Démarrage du nouveau conteneur"
                        docker run -d \
                            --name ${APP_NAME} \
                            -p ${APP_PORT}:8086 \
                            -e SPRING_PROFILES_ACTIVE=${SPRING_PROFILES_ACTIVE} \
                            ${APP_NAME}:latest
                    '''
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
