pipeline {
	environment {
		GODOT_VERSION = '4.3'
		PROJECT_LOCATION = '2d/platformer'
		BUILD_NAME = 'Platformer'
		BUILD_DIR = '.builds'
		STEAM_CREDENTIALS = ''
		DISCORD_WEBHOOK = ''
		DISCORD_PING_USER = ''
		DISCORD_PING_IF = ''
	}
	agent none
	options {
		disableConcurrentBuilds()
		disableResume()
	}
	stages {
		stage('Linux') {
			agent { label 'linux && docker' }
			steps { script { runBuildInImage() } }
		}
		stage('Windows') {
			agent { label 'windows && docker' }
			steps { script { runBuildInImage() } }
		}
	}
	post {
		always {
			script {
				report()
			}
		}
	}
}

def runBuildInImage() {
	def unix = isUnix()
	env.BUILD_PRESET_WINDOWS = 'Windows Desktop'
	env.BUILD_PRESET_LINUX = unix ? 'Linux' : ''
	env.BUILD_PRESET_MAC = unix ? 'macOS' : ''
	env.BUILD_PRESET_WEBGL = unix ? 'Web' : ''
	env.STEAM_ID = ''
	env.STEAM_DEPOT_WINDOWS = ''
	env.STEAM_DEPOT_LINUX = ''
	env.STEAM_DEPOT_MAC = ''
	env.STEAM_BRANCH = env.BRANCH_NAME

	def volumes = unix
		? '-v godot-binaries:/godot/binaries -v godot-templates:/godot/export_templates -v blender:/blender'
		: '-v godot-binaries:C:/godot/binaries -v godot-templates:C:/godot/export_templates -v blender:C:/blender'
	def image = docker.image('faulo/godot')
	image.pull()
	image.inside(volumes) {
		build()
	}
}

def build() {
	dir(env.PROJECT_LOCATION) {
		def builds = "$WORKSPACE/$PROJECT_LOCATION/$BUILD_DIR"

		def depots = ''

		stage('Import Assets') {
			catchError(buildResult: 'UNSTABLE', stageResult: 'UNSTABLE') {
				callShell "godot --headless --verbose --quit --editor --import"
			}
		}

		if (env.BUILD_PRESET_WINDOWS) {
			stage('Build: Windows') {
				env.BUILD_PLATFORM = env.BUILD_PRESET_WINDOWS

				fileOperations([folderCreateOperation("${builds}/${BUILD_PLATFORM}")])

				catchError(buildResult: 'UNSTABLE', stageResult: 'UNSTABLE') {
					callShell "godot --headless --verbose --quit --export-debug \"${BUILD_PLATFORM}\" \"${builds}/${BUILD_PLATFORM}/${BUILD_NAME}.exe\""
				}

				zip(zipFile: "${BUILD_NAME} - ${BUILD_PLATFORM}.zip", dir: "${builds}/${BUILD_PLATFORM}", archive: true, overwrite: true)

				depots += "$STEAM_DEPOT_WINDOWS=\"${BUILD_PLATFORM}\" "
			}
		}

		if (env.BUILD_PRESET_LINUX) {
			stage('Build: Linux') {
				env.BUILD_PLATFORM = env.BUILD_PRESET_LINUX

				fileOperations([folderCreateOperation("${builds}/${BUILD_PLATFORM}")])

				catchError(buildResult: 'UNSTABLE', stageResult: 'UNSTABLE') {
					callShell "godot --headless --verbose --quit --export-debug \"${BUILD_PLATFORM}\" \"${builds}/${BUILD_PLATFORM}/${BUILD_NAME}.x86_64\""
				}

				zip(zipFile: "${BUILD_NAME} - ${BUILD_PLATFORM}.zip", dir: "${builds}/${BUILD_PLATFORM}", archive: true, overwrite: true)


				depots += "$STEAM_DEPOT_LINUX=\"${BUILD_PLATFORM}\" "
			}
		}

		if (env.BUILD_PRESET_MAC) {
			stage('Build: MacOS') {
				env.BUILD_PLATFORM = env.BUILD_PRESET_MAC

				fileOperations([folderCreateOperation("${builds}/${BUILD_PLATFORM}")])

				catchError(buildResult: 'UNSTABLE', stageResult: 'UNSTABLE') {
					callShell "godot --headless --verbose --quit --export-debug \"${BUILD_PLATFORM}\" \"${builds}/${BUILD_PLATFORM}/${BUILD_NAME} - ${BUILD_PLATFORM}.zip\""
				}

				archiveArtifacts(artifacts: "$BUILD_DIR/${BUILD_PLATFORM}/*.zip", fingerprint: true)

				depots += "$STEAM_DEPOT_MAC=\"${BUILD_PLATFORM}\" "
			}
		}

		if (env.BUILD_PRESET_WEBGL) {
			stage('Build: WebGL') {
				env.BUILD_PLATFORM = env.BUILD_PRESET_WEBGL

				fileOperations([folderCreateOperation("${builds}/${BUILD_PLATFORM}")])

				catchError(buildResult: 'UNSTABLE', stageResult: 'UNSTABLE') {
					callShell "godot --headless --verbose --quit --export-debug \"${BUILD_PLATFORM}\" \"${builds}/${BUILD_PLATFORM}/${BUILD_NAME}.html\""
				}

				zip(zipFile: "${BUILD_NAME} - ${BUILD_PLATFORM}.zip", dir: "${builds}/${BUILD_PLATFORM}", archive: true, overwrite: true)

				publishHTML([
					allowMissing: false,
					alwaysLinkToLastBuild: false,
					keepAll: false,
					reportDir: "$BUILD_DIR/${BUILD_PLATFORM}",
					reportFiles: "${BUILD_NAME}.html",
					reportName: "WebGL Build",
					reportTitles: '',
					useWrapperFileDirectly: true
				])
			}
		}

		if (env.STEAM_ID && depots) {
			stage('Deploy to: Steam') {
				callUnity "steam-buildfile '${builds}' '${builds}' ${STEAM_ID} ${depots} $STEAM_BRANCH", "${builds}/deploy-steam.vdf"
				withCredentials([
					usernamePassword(credentialsId: env.STEAM_CREDENTIALS, usernameVariable: 'STEAM_CREDS_USR', passwordVariable: 'STEAM_CREDS_PSW')
				]) {
					callShell "steamcmd +login $STEAM_CREDS_USR $STEAM_CREDS_PSW +run_app_build '${builds}/deploy-steam.vdf' +quit"
				}
			}
		}
	}
}

def report() {
	if (env.DISCORD_WEBHOOK) {
		def description = "${currentBuild.currentResult}\r\n"

		def error = currentBuild.rawBuild.execution.causeOfFailure
		if (error) {
			description += "Cause of failure:\r\n"
			description += "${error}\r\n"
		}

		if (currentBuild.resultIsWorseOrEqualTo(env.DISCORD_PING_IF ? env.DISCORD_PING_IF : 'FAILURE')) {
			description += "Help!\r\n"
			if (env.$DISCORD_PING_USER) {
				description += "<@$DISCORD_PING_USER>\r\n"
			}
		}

		def footer = ""

		footer += "Changes:\r\n"
		def hasChanges = false
		for (changeLogSet in currentBuild.changeSets) {
			for (entry in changeLogSet.items) {
				footer += "- ${entry.msg}\r\n"
				hasChanges = true
			}
		}

		if (!hasChanges) {
			footer += "No changes detected.\r\n"
		}

		if (currentBuild.rawBuild.culprits.size() > 0) {
			footer += "\r\n"
			footer += "Culprits:\r\n"
			for (culprit in currentBuild.rawBuild.culprits) {
				footer += "- ${culprit.displayName}\r\n"
			}
		}

		discordSend description: description, footer: footer, link: env.BUILD_URL, result: currentBuild.currentResult, title: env.JOB_NAME, webhookURL: env.DISCORD_WEBHOOK
	}
}
