pipeline {
    agent {
        label 'windows'
    }
    environment {
        GODOT_IMAGE = 'barichello/godot-ci'
        GODOT_VERSION = '4.3'
        PROJECT_LOCATION = '3d/truck_town'
        BUILD_NAME = 'Truck Town'
		BUILD_DIR = 'build'
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
							env.BUILD_PLATFORM = "Windows Desktop"
							
							fileOperations([folderCreateOperation("${BUILD_DIR}/${BUILD_PLATFORM}")])
						
                            catchError(buildResult: 'UNSTABLE', stageResult: 'UNSTABLE') {
                                callShell "godot --headless --verbose --quit --export-debug \"${BUILD_PLATFORM}\" \"${BUILD_DIR}/${BUILD_PLATFORM}/${BUILD_NAME}.exe\""
                            }
                            
                            zip(zipFile: "${BUILD_NAME} - ${BUILD_PLATFORM}.zip", dir: "${BUILD_DIR}/${BUILD_PLATFORM}", archive: true)
                        }
                        stage('Build: Linux') {
							env.BUILD_PLATFORM = "Linux"
							
							fileOperations([folderCreateOperation("${BUILD_DIR}/${BUILD_PLATFORM}")])
						
                            catchError(buildResult: 'UNSTABLE', stageResult: 'UNSTABLE') {
                                callShell "godot --headless --verbose --quit --export-debug \"${BUILD_PLATFORM}\" \"${BUILD_DIR}/${BUILD_PLATFORM}/${BUILD_NAME}.exe\""
                            }
                            
                            zip(zipFile: "${BUILD_NAME} - ${BUILD_PLATFORM}.zip", dir: "${BUILD_DIR}/${BUILD_PLATFORM}", archive: true)
                        }
                    }
                }
            }
        }
    }
}