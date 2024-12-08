ARG GODOT_VERSION=4.3

FROM barichello/godot-ci:${GODOT_VERSION}

RUN apt update && \
	apt install -y blender

RUN echo "filesystem/import/blender/blender_path = \"/usr/bin/blender\"" >> ~/.config/godot/editor_settings-${GODOT_VERSION}.tres
