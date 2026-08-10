pipeline {
	environment {
		GODOT_VERSION = '4.3'
		BLENDER_VERSION = '4'
		PROJECT_LOCATION = '3d/global_illumination'
	}
	agent none
	options {
		disableConcurrentBuilds()
		disableResume()
	}
	stages {
		stage('Linux') {
			agent { label 'linux' }
			steps { script { importBlenderAsset() } }
		}
		stage('Windows') {
			agent { label 'windows' }
			steps { script { importBlenderAsset() } }
		}
	}
}

def importBlenderAsset() {
	def volumes = isUnix()
		? '-v godot-binaries:/godot/binaries -v godot-templates:/godot/export_templates -v blender:/blender'
		: '-v godot-binaries:C:/godot/binaries -v godot-templates:C:/godot/export_templates -v blender:C:/blender'
	docker.image('faulo/godot').inside(volumes) {
		dir(env.PROJECT_LOCATION) {
			fileOperations([folderDeleteOperation('.godot')])
			callShell 'godot --headless --verbose --quit --editor --import'
			def imported = findFiles(glob: '.godot/imported/cube.blend-*.scn')
			if (imported.length != 1) {
				error "Expected one imported cube.blend scene, found ${imported.length}"
			}
			echo "Blender import created ${imported[0].path}"
		}
	}
}
