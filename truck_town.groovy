pipeline {
    agent {
        label 'windows'
    }
    environment {
        GODOT_IMAGE = 'barichello/godot-ci'
        GODOT_VERSION = '4.3'
        PROJECT_LOCATION = '3d/truck_town'
        BUILD_NAME = 'Truck Town'
		BUILD_DIR = 'build/windows'
    }
	options {
		disableConcurrentBuilds()
		disableResume()
	}
    stages {
        stage('Setup') {
            steps {
                script {
                    dir(env.PROJECT_LOCATION) {
                        stage('Import Assets') {
                            catchError(buildResult: 'UNSTABLE', stageResult: 'UNSTABLE') {
                                callShell "godot --headless --verbose --quit --editor --import"
                            }
                        }
                        stage('Build: Windows') {
                            catchError(buildResult: 'UNSTABLE', stageResult: 'UNSTABLE') {
                                callShell "godot --headless --verbose --quit --export-debug \"Windows Desktop\" \"${BUILD_DIR}/${BUILD_NAME}.exe\""
                            }
                            
                            zip(zipFile: "${BUILD_NAME}.zip", dir: "${BUILD_DIR}", archive: true)
                        }
                    }
                }
            }
        }
    }
}