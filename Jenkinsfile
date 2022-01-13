@Library("lab3") _
pipeline {
    agent {
        kubernetes {
            yaml '''
apiVersion: v1
kind: Pod
spec:
  containers:
  - name: build
    image: 'maven:3.8.3-jdk-11'
    command:
    - cat
    tty: true
    '''
        defaultContainer 'build'
        }
    }
    
    triggers {
        eventTrigger jmespathQuery("labs[*].lab")
    }
  
    stages {
        stage ('Enable unit testing when payload object value is true') {
            when {
                allOf {
                    triggeredBy 'EventTriggerCause';    
                    equals (expected: 'true', actual: getTriggerCauseEvent.getTriggerCauseEvent())
                }
            }
            steps {
                echo 'Kicking off unit tests'
            }  
        }
        stage ('Disable unit testing when payload object value is false') {
            when {
                allOf {
                    triggeredBy 'EventTriggerCause';
                    equals (expected: 'false', actual: getTriggerCauseEvent.getTriggerCauseEvent())
                }
            }
            steps {
                echo 'User disabled unit testing'
            }
        }
        stage ('declare properties file') {
            steps {
                script {
                    def d_values = [
                    'default.value1':'1',
                    'default.value2':'2',
                    ]
                    def properties = [:]

                    properties = readProperties(defaults: d_values, file: 'build.properties')
                }
            }
        }    
        stage ('buildStart Time Stage') {
            when {
                equals (expected: 'true', actual: {$properties["buildStart"]})
            }
            steps {
                buildStart ()
            }
        }
        stage ('build') {
            when {
                equals (expected: 'true', actual: {$properties["mvnbuild"]})
            }
            steps {
                sh 'mvn -B -DskipTests clean package'
            }
        }
        stage('Test') {
            when {
                equals (expected: 'true', actual: {$properties["mvntest"]})
            }
            steps {
                sh 'mvn test'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }
        stage ('Deploy') {
            when {
                equals (expected: 'true', actual: {$properties["mvndeploy"]})
            }
            steps {
                sh './scripts/deliver.sh'
                }
        }
        stage ('buildEnd Time Stage') {
            when {
                equals (expected: 'true', actual: {$properties["buildEnd"]})
            }
            steps {
                buildEnd ()
            }
        }
    }
        post {
            success {
                buildResultsEmail("Successful")                
            }
            
            failure {
                buildResultsEmail("Failure")
            }       
            
    }
}