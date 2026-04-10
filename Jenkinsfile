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

        stage('Build And Test') {
            steps {
                script {
                    if (isUnix()) {
                        sh 'chmod +x mvnw'
                        sh './mvnw clean verify'
                    } else {
                        bat 'mvnw.cmd clean verify'
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

        stage('Archive') {
            steps {
                archiveArtifacts artifacts: 'target/*.war', fingerprint: true
            }
        }
    }

    post {
        always {
            junit testResults: 'target/surefire-reports/*.xml', allowEmptyResults: true
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
