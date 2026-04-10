pipeline {
    agent any

    options {
        disableConcurrentBuilds()
        buildDiscarder(logRotator(numToKeepStr: '10'))
    }

    environment {
        MAVEN_OPTS = '-Dmaven.test.failure.ignore=false'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build Backend') {
            steps {
                script {
                    if (isUnix()) {
                        sh 'chmod +x mvnw'
                        sh './mvnw clean package -DskipTests'
                    } else {
                        bat 'mvnw.cmd clean package -DskipTests'
                    }
                }
            }
        }

        stage('Test Backend') {
            steps {
                script {
                    if (isUnix()) {
                        sh './mvnw test'
                    } else {
                        bat 'mvnw.cmd test'
                    }
                }
            }
        }

        stage('Sonar Analysis') {
            environment {
                scannerHome = tool 'SONAR_SCANNER'
            }
            steps {
                withSonarQubeEnv('SONAR_LOCAL') {
                    script {
                        if (isUnix()) {
                            sh """
                                ${scannerHome}/bin/sonar-scanner \
                                -Dsonar.projectKey=DeployBack \
                                -Dsonar.host.url=http://localhost:9000 \
                                -Dsonar.login=sqp_d5fc32535b760afa0bca42f96d54f5f07ac1d6f8 \
                                -Dsonar.java.binaries=target/classes \
                                -Dsonar.sources=src/main/java \
                                -Dsonar.tests=src/test/java \
                                -Dsonar.java.test.binaries=target/test-classes \
                                -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml \
                                -Dsonar.qualitygate.wait=true \
                                -Dsonar.qualitygate.timeout=300 \
                                -Dsonar.coverage.exclusions=**/.mvn/**,**/src/test/**,**/model/**,**Application.java
                            """
                        } else {
                            bat """
                                \"${scannerHome}\\bin\\sonar-scanner.bat\" ^
                                -Dsonar.projectKey=DeployBack ^
                                -Dsonar.host.url=http://localhost:9000 ^
                                -Dsonar.login=sqp_d5fc32535b760afa0bca42f96d54f5f07ac1d6f8 ^
                                -Dsonar.java.binaries=target/classes ^
                                -Dsonar.sources=src/main/java ^
                                -Dsonar.tests=src/test/java ^
                                -Dsonar.java.test.binaries=target/test-classes ^
                                -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml ^
                                -Dsonar.qualitygate.wait=true ^
                                -Dsonar.qualitygate.timeout=300 ^
                                -Dsonar.coverage.exclusions=**/.mvn/**,**/src/test/**,**/model/**,**Application.java
                            """
                        }
                    }
                }
            }
        }

        stage('Deploy Backend') {
            steps {
                deploy adapters: [
                    tomcat8(
                        credentialsId: 'TomcatLogin',
                        path: '',
                        url: 'http://localhost:8001/'
                    )
                ],
                contextPath: 'tasks-backend',
                war: 'target/tasks-backend.war'
            }
        }

        stage('Build Frontend') {
            steps {
                dir('tasks-frontend') {
                    git branch: 'master', url: 'https://github.com/stampini81/tasks-frontend'
                    script {
                        if (isUnix()) {
                            sh 'chmod +x mvnw'
                            sh './mvnw clean package -DskipTests'
                        } else {
                            bat 'mvnw.cmd clean package -DskipTests'
                        }
                    }
                }
            }
        }

        stage('Deploy Frontend') {
            steps {
                dir('tasks-frontend') {
                    deploy adapters: [
                        tomcat8(
                            credentialsId: 'TomcatLogin',
                            path: '',
                            url: 'http://localhost:8001/'
                        )
                    ],
                    contextPath: 'tasks',
                    war: 'target/tasks.war'
                }
            }
        }

        stage('Build API Tests') {
            steps {
                dir('tasks-api-test') {
                    git branch: 'master', url: 'https://github.com/stampini81/tasks-api-test'
                    script {
                        if (isUnix()) {
                            sh 'mvn test-compile'
                        } else {
                            bat 'mvn test-compile'
                        }
                    }
                }
            }
        }

        stage('API Tests') {
            steps {
                dir('tasks-api-test') {
                    script {
                        if (isUnix()) {
                            sh 'mvn test -Dtest=APITest'
                        } else {
                            bat 'mvn test -Dtest=APITest'
                        }
                    }
                }
            }
        }

        stage('Build Functional Tests') {
            steps {
                dir('tasks-functional-tests') {
                    git branch: 'master', url: 'https://github.com/stampini81/tasks-functional-tests'
                    script {
                        if (isUnix()) {
                            sh 'mvn test-compile'
                        } else {
                            bat 'mvn test-compile'
                        }
                    }
                }
            }
        }

        stage('Functional Tests') {
            steps {
                dir('tasks-functional-tests') {
                    script {
                        if (isUnix()) {
                            sh 'mvn test -Dselenium.grid.url=http://localhost:4444 -Dselenium.browser=chrome -Dtasks.frontend.url=http://host.docker.internal:8001/tasks/'
                        } else {
                            bat 'mvn test "-Dselenium.grid.url=http://localhost:4444" "-Dselenium.browser=chrome" "-Dtasks.frontend.url=http://host.docker.internal:8001/tasks/"'
                        }
                    }
                }
            }
        }

        stage('Deploy Prod') {
            steps {
                bat 'docker-compose down'
                bat 'set FRONTEND_PORT=8002&& docker-compose build'
                bat 'set FRONTEND_PORT=8002&& docker-compose up -d'
            }
        }

        stage('Archive') {
            steps {
                archiveArtifacts artifacts: 'target/*.war', fingerprint: true
            }
        }
    }

    post {
        always {
            junit testResults: 'target/surefire-reports/*.xml, tasks-api-test/target/surefire-reports/*.xml, tasks-functional-tests/target/surefire-reports/*.xml', allowEmptyResults: true
            publishHTML(target: [
                allowMissing: true,
                alwaysLinkToLastBuild: true,
                keepAll: true,
                reportDir: 'target/site/jacoco',
                reportFiles: 'index.html',
                reportName: 'JaCoCo Report'
            ])
            chuckNorris()
        }
        success {
            echo 'Pipeline executado com sucesso.'
        }
        failure {
            echo 'Pipeline falhou. Verifique os logs e os relatorios publicados.'
        }
    }
}
