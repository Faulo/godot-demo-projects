withEnv([
        'GODOT_VERSION=4.3',
        'PROJECT_LOCATION=3d/truck_town',
        'BUILD_NAME=Truck Town',
        'BUILD_DIR=.builds',
        'STEAM_CREDENTIALS=',
        'DISCORD_WEBHOOK=',
        'DISCORD_PING_USER=',
        'DISCORD_PING_IF='
]) {
    evaluate(readTrusted('.jenkins/pipeline.groovy')).start()
}
