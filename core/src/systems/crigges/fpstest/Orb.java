package systems.crigges.fpstest;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Slider.SliderStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public abstract class Orb extends Actor{	
	protected Image img;
	protected float scale;
	protected float speed;
	protected Slider speedSlider;
	protected Label speedSliderLabel;
	protected Slider scaleSlider;
	protected Label scaleSliderLabel;
	
	public Orb() {
		img = new Image(AssetFactory.getTexture("runner"));
		addSliders();
		setOrbScale(getInitialScale());
		speed = getInitialSpeed();
	}
	
	protected abstract float getInitialScale();
	
	protected abstract float getMinScale();
	
	protected abstract float getMaxScale();
	
	protected abstract float getInitialSpeed();
	
	protected abstract float getMinSpeed();
	
	protected abstract float getMaxSpeed();
	
	@Override
	protected void setStage(Stage stage) {
		if (stage != null) {
			stage.addActor(img);
			stage.addActor(speedSlider);
			stage.addActor(speedSliderLabel);
			stage.addActor(scaleSlider);
			stage.addActor(scaleSliderLabel);
		}
		super.setStage(stage);
	}
	
	@Override
	public void act(float delta) {
		img.rotateBy(250f * delta);
		super.act(delta);
	}
	
	protected void setOrbScale(float newBounds){
		scale = newBounds;
		img.setWidth(newBounds);
		img.setHeight(newBounds);
		img.setOrigin(newBounds / 2, newBounds / 2);
	}
	
	private void addSliders(){
		SliderStyle style = new SliderStyle();
		style.knob = AssetFactory.getTextureDrawable("slider");
		style.background = AssetFactory.getTextureDrawable("sliderbg");
		style.knobBefore = AssetFactory.getTextureDrawable("sliderfilled");
		speedSlider = new Slider(getMinSpeed(), getMaxSpeed(), 0.01f, false, style);
		speedSlider.setValue(getInitialSpeed());
		speedSlider.setBounds(100, 50, 500, 20);
		speedSlider.setDisabled(false);
		speedSliderLabel = new Label("Orb Speed: " + getInitialSpeed(), new LabelStyle(AssetFactory.getFont("normal", 20, 0), Color.SKY));
		speedSliderLabel.setBounds(400, 40, 0, 0);
		speedSlider.addListener(new ChangeListener() {
			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				speed = speedSlider.getValue();
				speedSliderLabel.setText("Orb Speed: " + String.format("%.2f", speedSlider.getValue()));
			}
		});
		scaleSlider = new Slider(getMinScale(), getMaxScale(), 0.01f, false, style);
		scaleSlider.setValue(getInitialScale());
		scaleSlider.setBounds(100, 110, 500, 20);
		scaleSlider.setDisabled(false);
		scaleSliderLabel = new Label("Orb Scale: " + getInitialScale(), new LabelStyle(AssetFactory.getFont("normal", 20, 0), Color.SKY));
		scaleSliderLabel.setBounds(400, 100, 0, 0);
		scaleSlider.addListener(new ChangeListener() {
			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				setOrbScale(scaleSlider.getValue());
				scaleSliderLabel.setText("Orb Scale: " + String.format("%.1f", scaleSlider.getValue()));
			}
		});
	}
	
	@Override
	public boolean remove() {
		img.remove();
		speedSlider.remove();
		speedSliderLabel.remove();
		scaleSlider.remove();
		scaleSliderLabel.remove();
		return super.remove();
	}
}
