package space.firstorder.faceswap;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.model.NodePart;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;

public class Main extends ApplicationAdapter {
	public SpriteBatch sprites;
	public Environment environment;

	public PerspectiveCamera cam;
	public DirectionalLight lightSource;

	public ModelBatch modelBatch;
	public ModelInstance cube;

	private AssetManager assets;

	private boolean loading;

	private final DeviceCameraControl deviceCameraControl;

	public enum Mode {
		normal,
		prepare,
		preview
	}

	private Mode mode = Mode.normal;

	public Main(DeviceCameraControl cameraControl) {
		this.deviceCameraControl = cameraControl;
	}

	@Override
	public void create() {
		DefaultShader.Config config = new DefaultShader.Config();
		config.numDirectionalLights = 1;
		config.numPointLights = 0;
		modelBatch = new ModelBatch();

		sprites = new SpriteBatch();
		assets = new AssetManager();

		assets.load("data/texture.jpg", Texture.class);
		assets.load("data/cube.obj", Model.class);
		loading = true;

		lightSource = new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f);
		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1));
		environment.add(lightSource);

		cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		cam.position.set(10f, 10f, 10f);
		cam.lookAt(0, 0, 0);
		cam.near = 1f;
		cam.far = 300f;
		cam.update();

        // set the input processor to work with our custom input:
        //  clicking the image in the lower right should change the colors of the helmets
        //  bonus points: implement your own GestureDetector and an input processor based on it
		Gdx.input.setInputProcessor(new space.firstorder.faceswap.FirstOrderInputProcessor(cam, null));
	}

	private void doneLoading() {
		cube = new ModelInstance(assets.get("data/cube.obj", Model.class));
		cube.materials.get(0).set(ColorAttribute.createDiffuse(0.5f, 0.5f, 0.5f, 1));

		loading = false;
	}

	@Override
	public void render() {
		if (loading && assets.update())
			doneLoading();

		//Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		if (mode == Mode.normal) {
			mode = Mode.prepare;
			if (deviceCameraControl != null) {
				deviceCameraControl.prepareCameraAsync();
			}
		} else if (mode == Mode.prepare) {
			Gdx.gl.glClearColor(1.0f, 0.0f, 0.0f, 0.5f);
			if (deviceCameraControl != null) {
				if (deviceCameraControl.isReady()) {
					deviceCameraControl.startPreviewAsync();
					mode = Mode.preview;
				}
			}
		} else if (mode == Mode.preview) {
			Gdx.gl.glClearColor(1.0f, 0.0f, 0.0f, 1.0f);
		}

		cam.update();

		modelBatch.begin(cam);
		if (cube != null)
			modelBatch.render(cube, environment);
		modelBatch.end();

		sprites.begin();
		// draw sprites
		sprites.end();
	}

	@Override
	public void dispose () {
		modelBatch.dispose();
		sprites.dispose();
	}
}