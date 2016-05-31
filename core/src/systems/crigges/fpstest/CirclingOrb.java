package systems.crigges.fpstest;

import java.util.ArrayList;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class CirclingOrb extends Orb{	
	private float angle = 0;
	private float radius = 300;
	
	@Override
	protected float getInitialScale() {
		return 50;
	}

	@Override
	protected float getMinScale() {
		return 5;
	}

	@Override
	protected float getMaxScale() {
		return 500;
	}

	@Override
	protected float getInitialSpeed() {
		return 0.4f;
	}

	@Override
	protected float getMinSpeed() {
		return 0;
	}

	@Override
	protected float getMaxSpeed() {
		return 20f;
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
	
	@Override
	public boolean remove() {
		img.remove();
		return super.remove();
	}
}
