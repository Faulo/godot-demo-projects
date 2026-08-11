withEnv([
        'GODOT_VERSION=4.3',
        'BLENDER_VERSION=4',
        'PROJECT_LOCATION=3d/squash_the_creeps_blender',
        'BUILD_NAME=Squash the Creeps - Blender',
        'BUILD_DIR=.builds',
        'STEAM_CREDENTIALS=',
        'DISCORD_WEBHOOK=',
        'DISCORD_PING_USER=',
        'DISCORD_PING_IF='
]) {
    evaluate(readTrusted('.jenkins/pipeline.groovy')).start()
}
