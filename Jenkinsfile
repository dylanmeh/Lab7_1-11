@Library("lab3") _

def d_values = [
    'default.value1':'1',
    'default.value2':'2',
]

def properties = [:]

podTemplate {
    node(POD_LABEL) {
        checkout scm 
        properties = readProperties(defaults: d, file: 'build.properties')
    }
}

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
        stage ('read properties file') {

        }
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
        
        stage ('buildStart Time Stage') {
            when {
                equals (expected: 'a', actual: {$properties["default.value1"]})
            }
            steps {
                buildStart ()
            }
        }
        stage ('build') {
            steps {
                sh 'mvn -B -DskipTests clean package'
            }
        }
        stage('Test') {
            steps {
                sh 'mvn test'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }
        stage ('deploy') {
            steps {
                sh './scripts/deliver.sh'
                }
        }
        stage ('buildEnd Time Stage') {
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