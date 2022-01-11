def call(int buildnumber) {
    if (buildNumber % 2 == 0) {
        pipeline {
            agent any
            stages {
                stage('Even stage') {
                    steps {
                        echo "The build number is even"
                    }
                }
            }
    }   else {
            pipeline {
                agent any
                stages {
                    stage('Odd stage') {
                        steps {
                            echo "The build number is odd"
                        }
                    }
                }
            }
        }
    }
}