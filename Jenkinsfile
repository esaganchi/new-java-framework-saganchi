pipeline {
    agent any
    
    parameters {
        choice(
            name: 'TEST_SUITE',
            choices: ['all', 'api', 'ui'],
            description: 'Выберите набор тестов для запуска'
        )
        choice(
            name: 'BROWSER',
            choices: ['chrome', 'firefox'],
            description: 'Выберите браузер для UI тестов'
        )
        booleanParam(
            name: 'GENERATE_ALLURE_REPORT',
            defaultValue: true,
            description: 'Генерировать Allure отчет'
        )
        booleanParam(
            name: 'USE_DOCKER',
            defaultValue: true,
            description: 'Использовать Docker для запуска тестов'
        )
    }
    
    environment {
        JAVA_HOME = '/usr/lib/jvm/java-17-openjdk'
        GRADLE_OPTS = '-Xmx2g -Dorg.gradle.daemon=false'
        SELENIUM_REMOTE_URL = 'http://selenium-hub:4444/wd/hub'
        ALLURE_RESULTS_PATH = 'build/allure-results'
    }
    
    tools {
        jdk 'Java17'
        gradle 'Gradle8'
    }
    
    stages {
        stage('Checkout') {
            steps {
                echo 'Получение кода из репозитория...'
                checkout scm
            }
        }
        
        stage('Gradle Wrapper') {
            steps {
                script {
                    if (isUnix()) {
                        sh 'chmod +x gradlew'
                    }
                }
            }
        }
        
        stage('Clean & Build') {
            steps {
                echo 'Очистка и сборка проекта...'
                script {
                    if (isUnix()) {
                        sh './gradlew clean build -x test'
                    } else {
                        bat 'gradlew.bat clean build -x test'
                    }
                }
            }
        }
        
        stage('Setup Test Environment') {
            when {
                params.USE_DOCKER == true
            }
            steps {
                echo 'Запуск Docker окружения...'
                script {
                    sh '''
                        docker-compose down --volumes --remove-orphans
                        docker-compose up -d selenium-hub chrome firefox
                        sleep 30
                        docker-compose ps
                    '''
                }
            }
        }
        
        stage('Run Tests') {
            steps {
                echo "Запуск тестов: ${params.TEST_SUITE}"
                script {
                    def testCommand = buildTestCommand()
                    
                    try {
                        if (params.USE_DOCKER) {
                            sh """
                                export SELENIUM_REMOTE_URL=${SELENIUM_REMOTE_URL}
                                ${testCommand}
                            """
                        } else {
                            if (isUnix()) {
                                sh testCommand
                            } else {
                                bat testCommand.replace('./gradlew', 'gradlew.bat')
                            }
                        }
                    } catch (Exception e) {
                        currentBuild.result = 'UNSTABLE'
                        echo "Некоторые тесты упали: ${e.getMessage()}"
                    }
                }
            }
        }
        
        stage('Generate Allure Report') {
            when {
                params.GENERATE_ALLURE_REPORT == true
            }
            steps {
                echo 'Генерация Allure отчета...'
                script {
                    if (isUnix()) {
                        sh './gradlew allureReport'
                    } else {
                        bat 'gradlew.bat allureReport'
                    }
                }
            }
        }
        
        stage('Archive Results') {
            steps {
                echo 'Архивирование результатов...'
                
                // Архивирование тестовых отчетов
                publishHTML([
                    allowMissing: false,
                    alwaysLinkToLastBuild: true,
                    keepAll: true,
                    reportDir: 'build/reports/tests/test',
                    reportFiles: 'index.html',
                    reportName: 'Gradle Test Report'
                ])
                
                // Архивирование Allure результатов
                script {
                    if (params.GENERATE_ALLURE_REPORT) {
                        allure([
                            includeProperties: false,
                            jdk: '',
                            properties: [],
                            reportBuildPolicy: 'ALWAYS',
                            results: [[path: env.ALLURE_RESULTS_PATH]]
                        ])
                    }
                }
                
                // Архивирование артефактов
                archiveArtifacts(
                    artifacts: 'build/reports/**/*,build/allure-results/**/*',
                    allowEmptyArchive: true,
                    fingerprint: true
                )
            }
        }
        
        stage('Publish Test Results') {
            steps {
                echo 'Публикация результатов тестов...'
                
                publishTestResults([
                    testResultsPattern: 'build/test-results/test/TEST-*.xml'
                ])
                
                // Уведомления о результатах
                script {
                    def testResults = currentBuild.rawBuild.getAction(hudson.tasks.test.AbstractTestResultAction.class)
                    if (testResults) {
                        def total = testResults.totalCount
                        def failed = testResults.failCount
                        def skipped = testResults.skipCount
                        def passed = total - failed - skipped
                        
                        echo """
                        📊 Результаты тестирования:
                        ✅ Пройдено: ${passed}
                        ❌ Упало: ${failed}
                        ⏭️ Пропущено: ${skipped}
                        📈 Всего: ${total}
                        """
                    }
                }
            }
        }
    }
    
    post {
        always {
            echo 'Очистка ресурсов...'
            script {
                if (params.USE_DOCKER) {
                    sh '''
                        docker-compose logs selenium-hub chrome firefox || true
                        docker-compose down --volumes --remove-orphans || true
                    '''
                }
            }
        }
        
        success {
            echo '✅ Пайплайн выполнен успешно!'
            script {
                if (env.BRANCH_NAME == 'main' || env.BRANCH_NAME == 'master') {
                    echo '🚀 Деплой на продакшен можно запускать'
                }
            }
        }
        
        failure {
            echo '❌ Пайплайн упал!'
            // Здесь можно добавить уведомления в Slack/Teams/Email
        }
        
        unstable {
            echo '⚠️ Пайплайн завершен с предупреждениями'
        }
        
        cleanup {
            echo 'Финальная очистка workspace...'
            cleanWs()
        }
    }
}

def buildTestCommand() {
    def command = './gradlew test --no-daemon'
    
    // Добавляем фильтр по типу тестов
    switch(params.TEST_SUITE) {
        case 'api':
            command += ' --tests "*ApiTests*"'
            break
        case 'ui':
            command += ' --tests "*UiTests*" --tests "*UIPomTests*"'
            break
        case 'all':
        default:
            // Запускаем все тесты
            break
    }
    
    // Добавляем системные свойства
    command += ' -Dselenium.browser=' + params.BROWSER
    
    if (params.USE_DOCKER) {
        command += ' -Dselenium.grid.enabled=true'
    }
    
    return command
}

// Вспомогательная функция для публикации результатов тестов
def publishTestResults(Map args) {
    junit(
        testResults: args.testResultsPattern,
        allowEmptyResults: true,
        skipPublishingChecks: false,
        keepLongStdio: true
    )
}