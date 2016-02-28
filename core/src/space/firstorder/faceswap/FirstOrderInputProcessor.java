package space.firstorder.faceswap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import sun.rmi.runtime.Log;

public class FirstOrderInputProcessor extends CameraInputController {
    final Runnable runnable;

    public FirstOrderInputProcessor(Camera c, Runnable runnable) {
        super(c);
        this.runnable = runnable;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
//        int height = Gdx.graphics.getHeight();
//        int width = Gdx.graphics.getWidth();
//
//        int yInverted = height - screenY;
        Gdx.app.log("Example", "touch started at (" + screenX + ", " + screenY + ")");

        if (screenX < 250 && screenY < 250) {
            if (runnable != null) {

                // call the changeColorsAction runnable interface, implemented in Main
                runnable.run();
                return true;
            }
        }

        // otherwise, just do camera input controls
        return super.touchDown(screenX, screenY, pointer, button);
    }
}
