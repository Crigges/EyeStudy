package systems.crigges.fpstest;

import java.util.ArrayList;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class MazeRunner extends Actor{	
	private Image img;
	private float bounds;
	private float steppedTime = 0;
	private float angle = 0;
	private float radius = 300;
	private float speed = 0.4f;
	
	public MazeRunner() {
		img = new Image(AssetFactory.getTexture("runner"));
		bounds = 100;
		img.setWidth(bounds);
		img.setHeight(bounds);
		img.setOrigin(bounds / 2, bounds / 2);
		img.setPosition(100, 500);
	}
	
	@Override
	protected void setStage(Stage stage) {
		if (stage != null) {
			stage.addActor(img);
		}
		super.setStage(stage);
	}
	
	@Override
	public void act(float delta) {
		float dx = (float) (radius * Math.cos(angle)); 
		float dy = (float) (radius * Math.sin(angle));
		img.setPosition(dx + 500, dy + 500);
		img.rotateBy(300f * delta);
		angle += speed * Math.PI * delta;
		super.act(delta);
	}
	
	public void setSpeed(float speed){
		this.speed = speed;
	}

	public float getTime() {
		return steppedTime;
	}
}
