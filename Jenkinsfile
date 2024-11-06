pipeline {
    agent any
    
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                sh 'echo Building the project...'
                sh 'python3 -m venv venv'  // Crear entorno virtual
                sh '. venv/bin/activate && pip install -r requirements.txt'  // Instalar dependencias
    }
            }
        }

        stage('Test') {
            steps {
                sh 'echo Running tests...'
                // Comando para correr los tests (e.g., pytest para Python o mvn test para Java)
                // Agrega aquí un script para generar reportes de cobertura (e.g., pycobertura)
            }
            post {
                always {
                    junit '**/test-reports/*.xml' // Reemplaza con la ruta de tus reportes de tests
                    cobertura coberturaReportFile: '**/coverage.xml' // Reemplaza según el nombre de tu archivo de cobertura
                }
            }
        }

        stage('Package') {
            steps {
                sh 'echo Packaging the project...'
                // Aquí puedes agregar empaquetado, como docker o zip
            }
        }

        stage('Deploy') {
            steps {
                sh 'echo Deploying to cloud...'
                // Aquí debes incluir el script de despliegue, según la nube que uses
            }
        }
    }

    post {
        success {
            emailext to: 'your_email@example.com',
                     subject: 'Pipeline Succeeded',
                     body: 'The Jenkins pipeline has succeeded.'
        }
        failure {
            emailext to: 'your_email@example.com',
                     subject: 'Pipeline Failed',
                     body: 'The Jenkins pipeline has failed. Please check the details in Jenkins.'
        }
    }
}