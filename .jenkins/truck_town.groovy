pipeline {
    agent none
    environment {
        GODOT_VERSION = '4.3'
        PROJECT_LOCATION = '3d/truck_town'
        BUILD_NAME = 'Truck Town'
    }
	options {
		disableConcurrentBuilds()
		disableResume()
	}
    stages {
        stage('Setup Agent') {
			agent {
				dockerfile {
					filename '.jenkins/Dockerfile'					
				}
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
							
							zip(zipFile: "${BUILD_NAME} - ${BUILD_PLATFORM}.zip", dir: ".builds/${BUILD_PLATFORM}", archive: true, overwrite: true)
						}
						stage('Build: Linux') {
							env.BUILD_PLATFORM = "Linux"
							
							fileOperations([folderCreateOperation(".builds/${BUILD_PLATFORM}")])
						
							catchError(buildResult: 'UNSTABLE', stageResult: 'UNSTABLE') {
								callShell "godot --headless --verbose --quit --export-debug \"${BUILD_PLATFORM}\" \".builds/${BUILD_PLATFORM}/${BUILD_NAME}.x86_64\""
							}
							
							zip(zipFile: "${BUILD_NAME} - ${BUILD_PLATFORM}.zip", dir: ".builds/${BUILD_PLATFORM}", archive: true, overwrite: true)
						}
						stage('Build: MacOS') {
							env.BUILD_PLATFORM = "macOS"
							
							fileOperations([folderCreateOperation(".builds/${BUILD_PLATFORM}")])
						
							catchError(buildResult: 'UNSTABLE', stageResult: 'UNSTABLE') {
								callShell "godot --headless --verbose --quit --export-debug \"${BUILD_PLATFORM}\" \".builds/${BUILD_PLATFORM}/${BUILD_NAME} - ${BUILD_PLATFORM}.zip\""
							}
							
							archiveArtifacts artifacts: ".builds/${BUILD_PLATFORM}/*.zip", fingerprint: true
						}
						stage('Build: WebGL') {
							env.BUILD_PLATFORM = "Web"
							
							fileOperations([folderCreateOperation(".builds/${BUILD_PLATFORM}")])
						
							catchError(buildResult: 'UNSTABLE', stageResult: 'UNSTABLE') {
								callShell "godot --headless --verbose --quit --export-debug \"${BUILD_PLATFORM}\" \".builds/${BUILD_PLATFORM}/${BUILD_NAME}.html\""
							}
							
							zip(zipFile: "${BUILD_NAME} - ${BUILD_PLATFORM}.zip", dir: ".builds/${BUILD_PLATFORM}", archive: true, overwrite: true)
							
							publishHTML([
								allowMissing: false,
								alwaysLinkToLastBuild: false,
								keepAll: false,
								reportDir: ".builds/${BUILD_PLATFORM}",
								reportFiles: "${BUILD_NAME}.html",
								reportName: "WebGL Build",
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