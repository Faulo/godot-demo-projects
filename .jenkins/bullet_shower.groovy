pipeline {
    agent none
    environment {
        GODOT_VERSION = '4.2'
        PROJECT_LOCATION = '2d/bullet_shower'
        BUILD_NAME = 'Bullet Shower'
    }
	options {
		disableConcurrentBuilds()
		disableResume()
	}
    stages {
        stage('Linux Host') {
			agent {
				dockerfile {
					filename '.jenkins/Dockerfile'					
				}
			}
			environment {
				BUILD_HOST = 'Linux'
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
							
							fileOperations([folderCreateOperation(".builds/${BUILD_PLATFORM}")])
						
							catchError(buildResult: 'UNSTABLE', stageResult: 'UNSTABLE') {
								callShell "godot --headless --verbose --quit --export-debug \"${BUILD_PLATFORM}\" \".builds/${BUILD_PLATFORM}/${BUILD_NAME}.exe\""
							}
							
							zip(zipFile: "${BUILD_HOST} - ${BUILD_NAME} - ${BUILD_PLATFORM}.zip", dir: ".builds/${BUILD_PLATFORM}", archive: true, overwrite: true)
						}
						stage('Build: Linux') {
							env.BUILD_PLATFORM = "Linux"
							
							fileOperations([folderCreateOperation(".builds/${BUILD_PLATFORM}")])
						
							catchError(buildResult: 'UNSTABLE', stageResult: 'UNSTABLE') {
								callShell "godot --headless --verbose --quit --export-debug \"${BUILD_PLATFORM}\" \".builds/${BUILD_PLATFORM}/${BUILD_NAME}\""
							}
							
							zip(zipFile: "${BUILD_HOST} - ${BUILD_NAME} - ${BUILD_PLATFORM}.zip", dir: ".builds/${BUILD_PLATFORM}", archive: true, overwrite: true)
						}
						stage('Build: MacOS') {
							env.BUILD_PLATFORM = "macOS"
							
							fileOperations([folderCreateOperation(".builds/${BUILD_PLATFORM}")])
						
							catchError(buildResult: 'UNSTABLE', stageResult: 'UNSTABLE') {
								callShell "godot --headless --verbose --quit --export-debug \"${BUILD_PLATFORM}\" \".builds/${BUILD_PLATFORM}/${BUILD_NAME}\""
							}
							
							zip(zipFile: "${BUILD_HOST} - ${BUILD_NAME} - ${BUILD_PLATFORM}.zip", dir: ".builds/${BUILD_PLATFORM}", archive: true, overwrite: true)
						}
						stage('Build: WebGL') {
							env.BUILD_PLATFORM = "Web"
							
							fileOperations([folderCreateOperation(".builds/${BUILD_PLATFORM}")])
						
							catchError(buildResult: 'UNSTABLE', stageResult: 'UNSTABLE') {
								callShell "godot --headless --verbose --quit --export-debug \"${BUILD_PLATFORM}\" \".builds/${BUILD_PLATFORM}/${BUILD_NAME}\""
							}
							
							zip(zipFile: "${BUILD_HOST} - ${BUILD_NAME} - ${BUILD_PLATFORM}.zip", dir: ".builds/${BUILD_PLATFORM}", archive: true, overwrite: true)
							
							publishHTML([
								allowMissing: false,
								alwaysLinkToLastBuild: false,
								keepAll: false,
								reportDir: ".builds/${BUILD_PLATFORM}",
								reportFiles: "${BUILD_NAME}.html",
								reportName: '${BUILD_HOST} - WebGL Build',
								reportTitles: '',
								useWrapperFileDirectly: true
							])
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
							
							fileOperations([folderCreateOperation(".builds/${BUILD_PLATFORM}")])
						
                            catchError(buildResult: 'UNSTABLE', stageResult: 'UNSTABLE') {
                                callShell "godot --headless --verbose --quit --export-debug \"${BUILD_PLATFORM}\" \".builds/${BUILD_PLATFORM}/${BUILD_NAME}.exe\""
                            }
                            
                            zip(zipFile: "${BUILD_HOST} - ${BUILD_NAME} - ${BUILD_PLATFORM}.zip", dir: ".builds/${BUILD_PLATFORM}", archive: true, overwrite: true)
                        }
                        stage('Build: Linux') {
							env.BUILD_PLATFORM = "Linux"
							
							fileOperations([folderCreateOperation(".builds/${BUILD_PLATFORM}")])
						
                            catchError(buildResult: 'UNSTABLE', stageResult: 'UNSTABLE') {
                                callShell "godot --headless --verbose --quit --export-debug \"${BUILD_PLATFORM}\" \".builds/${BUILD_PLATFORM}/${BUILD_NAME}\""
                            }
                            
                            zip(zipFile: "${BUILD_HOST} - ${BUILD_NAME} - ${BUILD_PLATFORM}.zip", dir: ".builds/${BUILD_PLATFORM}", archive: true, overwrite: true)
                        }
                        stage('Build: MacOS') {
							env.BUILD_PLATFORM = "macOS"
							
							fileOperations([folderCreateOperation(".builds/${BUILD_PLATFORM}")])
						
                            catchError(buildResult: 'UNSTABLE', stageResult: 'UNSTABLE') {
                                callShell "godot --headless --verbose --quit --export-debug \"${BUILD_PLATFORM}\" \".builds/${BUILD_PLATFORM}/${BUILD_NAME}\""
                            }
                            
                            zip(zipFile: "${BUILD_HOST} - ${BUILD_NAME} - ${BUILD_PLATFORM}.zip", dir: ".builds/${BUILD_PLATFORM}", archive: true, overwrite: true)
                        }
                        stage('Build: WebGL') {
							env.BUILD_PLATFORM = "Web"
							
							fileOperations([folderCreateOperation(".builds/${BUILD_PLATFORM}")])
						
                            catchError(buildResult: 'UNSTABLE', stageResult: 'UNSTABLE') {
                                callShell "godot --headless --verbose --quit --export-debug \"${BUILD_PLATFORM}\" \".builds/${BUILD_PLATFORM}/${BUILD_NAME}\""
                            }
                            
                            zip(zipFile: "${BUILD_HOST} - ${BUILD_NAME} - ${BUILD_PLATFORM}.zip", dir: ".builds/${BUILD_PLATFORM}", archive: true, overwrite: true)
							
							publishHTML([
								allowMissing: false,
								alwaysLinkToLastBuild: false,
								keepAll: false,
								reportDir: ".builds/${BUILD_PLATFORM}",
								reportFiles: "${BUILD_NAME}.html",
								reportName: '${BUILD_HOST} - WebGL Build',
								reportTitles: '',
								useWrapperFileDirectly: true
							])
                        }
                    }
                }
            }
        }
    }
}