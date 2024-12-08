pipeline {
    agent none
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
        stage('Linux Host') {
			agent {
				label 'linux'
			}
			environment {
				BUILD_HOST = 'Linux'
			}
            steps {
                script {
                    dir(env.PROJECT_LOCATION) {		
						docker.image("$GODOT_IMAGE:$GODOT_VERSION").inside() {
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
								
								zip(zipFile: "${BUILD_HOST} - ${BUILD_NAME} - ${BUILD_PLATFORM}.zip", dir: "${BUILD_DIR}/${BUILD_PLATFORM}", archive: true, overwrite: true)
							}
							stage('Build: Linux') {
								env.BUILD_PLATFORM = "Linux"
								
								fileOperations([folderCreateOperation("${BUILD_DIR}/${BUILD_PLATFORM}")])
							
								catchError(buildResult: 'UNSTABLE', stageResult: 'UNSTABLE') {
									callShell "godot --headless --verbose --quit --export-debug \"${BUILD_PLATFORM}\" \"${BUILD_DIR}/${BUILD_PLATFORM}/${BUILD_NAME}.exe\""
								}
								
								zip(zipFile: "${BUILD_HOST} - ${BUILD_NAME} - ${BUILD_PLATFORM}.zip", dir: "${BUILD_DIR}/${BUILD_PLATFORM}", archive: true, overwrite: true)
							}
							stage('Build: MacOS') {
								env.BUILD_PLATFORM = "macOS"
								
								fileOperations([folderCreateOperation("${BUILD_DIR}/${BUILD_PLATFORM}")])
							
								catchError(buildResult: 'UNSTABLE', stageResult: 'UNSTABLE') {
									callShell "godot --headless --verbose --quit --export-debug \"${BUILD_PLATFORM}\" \"${BUILD_DIR}/${BUILD_PLATFORM}/${BUILD_NAME}.exe\""
								}
								
								zip(zipFile: "${BUILD_HOST} - ${BUILD_NAME} - ${BUILD_PLATFORM}.zip", dir: "${BUILD_DIR}/${BUILD_PLATFORM}", archive: true, overwrite: true)
							}
							stage('Build: WebGL') {
								env.BUILD_PLATFORM = "Web"
								
								fileOperations([folderCreateOperation("${BUILD_DIR}/${BUILD_PLATFORM}")])
							
								catchError(buildResult: 'UNSTABLE', stageResult: 'UNSTABLE') {
									callShell "godot --headless --verbose --quit --export-debug \"${BUILD_PLATFORM}\" \"${BUILD_DIR}/${BUILD_PLATFORM}/${BUILD_NAME}.exe\""
								}
								
								zip(zipFile: "${BUILD_HOST} - ${BUILD_NAME} - ${BUILD_PLATFORM}.zip", dir: "${BUILD_DIR}/${BUILD_PLATFORM}", archive: true, overwrite: true)
							}
						}
					}
                }
            }
		}
        stage('Windows Host') {
			agent {
				label 'windows'
			}
			environment {
				BUILD_HOST = 'Windows'
			}
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
                            
                            zip(zipFile: "${BUILD_HOST} - ${BUILD_NAME} - ${BUILD_PLATFORM}.zip", dir: "${BUILD_DIR}/${BUILD_PLATFORM}", archive: true, overwrite: true)
                        }
                        stage('Build: Linux') {
							env.BUILD_PLATFORM = "Linux"
							
							fileOperations([folderCreateOperation("${BUILD_DIR}/${BUILD_PLATFORM}")])
						
                            catchError(buildResult: 'UNSTABLE', stageResult: 'UNSTABLE') {
                                callShell "godot --headless --verbose --quit --export-debug \"${BUILD_PLATFORM}\" \"${BUILD_DIR}/${BUILD_PLATFORM}/${BUILD_NAME}.exe\""
                            }
                            
                            zip(zipFile: "${BUILD_HOST} - ${BUILD_NAME} - ${BUILD_PLATFORM}.zip", dir: "${BUILD_DIR}/${BUILD_PLATFORM}", archive: true, overwrite: true)
                        }
                        stage('Build: MacOS') {
							env.BUILD_PLATFORM = "macOS"
							
							fileOperations([folderCreateOperation("${BUILD_DIR}/${BUILD_PLATFORM}")])
						
                            catchError(buildResult: 'UNSTABLE', stageResult: 'UNSTABLE') {
                                callShell "godot --headless --verbose --quit --export-debug \"${BUILD_PLATFORM}\" \"${BUILD_DIR}/${BUILD_PLATFORM}/${BUILD_NAME}.exe\""
                            }
                            
                            zip(zipFile: "${BUILD_HOST} - ${BUILD_NAME} - ${BUILD_PLATFORM}.zip", dir: "${BUILD_DIR}/${BUILD_PLATFORM}", archive: true, overwrite: true)
                        }
                        stage('Build: WebGL') {
							env.BUILD_PLATFORM = "Web"
							
							fileOperations([folderCreateOperation("${BUILD_DIR}/${BUILD_PLATFORM}")])
						
                            catchError(buildResult: 'UNSTABLE', stageResult: 'UNSTABLE') {
                                callShell "godot --headless --verbose --quit --export-debug \"${BUILD_PLATFORM}\" \"${BUILD_DIR}/${BUILD_PLATFORM}/${BUILD_NAME}.exe\""
                            }
                            
                            zip(zipFile: "${BUILD_HOST} - ${BUILD_NAME} - ${BUILD_PLATFORM}.zip", dir: "${BUILD_DIR}/${BUILD_PLATFORM}", archive: true, overwrite: true)
                        }
                    }
                }
            }
        }
    }
}