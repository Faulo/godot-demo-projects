withEnv([
        'GODOT_VERSION=4.3',
        'BLENDER_VERSION=4',
        'PROJECT_LOCATION=3d/global_illumination',
        'BUILD_NAME=Global Illumination',
        'BUILD_DIR=.builds',
        'STEAM_CREDENTIALS=',
        'DISCORD_WEBHOOK=',
        'DISCORD_PING_USER=',
        'DISCORD_PING_IF='
]) {
    evaluate(readTrusted('.jenkins/pipeline.groovy')).start()
}
