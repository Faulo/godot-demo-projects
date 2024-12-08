FROM barichello/godot-ci:4.3

RUN apt update && \
	apt install -y blender

RUN echo "filesystem/import/blender/blender_path = \"/usr/bin/blender\"" >> ~/.config/godot/editor_settings-4.3.tres
