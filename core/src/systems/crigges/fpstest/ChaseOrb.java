package systems.crigges.fpstest;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Slider.SliderStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Timer;

public class ChaseOrb extends Orb{
	private static final int minX = 50; 
	private static final int minY = 200;
	private static final int maxX = 1920 - 150; 
	private static final int maxY = 1080 - 150;
	private static final float startTargetDelay = 0.3f;
	private static final float startFriction = 0.8f;
	private static final float startDuration = 60f;
	
	private float targetX = 1500;
	private float targetY = 800;
	private Random gen = new Random();
	private boolean move = false;
	private Label statusLabel;
	private Label startLabel;
	private float accuracy = 0;
	private float velX = 0;
	private float velY = 0;
	private float friction = startFriction;
	private float newTargetDelay = startTargetDelay;
	private float duration = startDuration;
	private float total = 0;
	private Timer targetTimer = null;
	private Timer endTimer = null;
	private Slider newTargetSlider;
	private Slider frictionSlider;
	private Slider durationSlider;
	private Label newTargetSliderLabel;
	private Label frictionSliderLabel;
	private Label durationSliderLabel;
	private TextButton resetButton;
	
	@Override
	protected float getInitialScale() {
		return 80;
	}

	@Override
	protected float getMinScale() {
		return 5;
	}

	@Override
	protected float getMaxScale() {
		return 200;
	}

	@Override
	protected float getInitialSpeed() {
		return 1800f;
	}

	@Override
	protected float getMinSpeed() {
		return 1;
	}

	@Override
	protected float getMaxSpeed() {
		return 10000f;
	}
	
	public ChaseOrb() {
		super();
		addStatusLabel();
		addStartLabel();
		addAimSlider();
		addResetButton();
		img.setBounds(1920 / 2 - scale / 2, 1080 / 2 - scale / 2, scale, scale);
		img.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if(!move){
					accuracy = 0;
					total = 0;
					move = true;
					startLabel.setText("");
					targetTimer = new Timer();
					targetTimer.scheduleTask(new GenerateTargetTask(), newTargetDelay, newTargetDelay);
					targetTimer.start();
					endTimer = new Timer();
					endTimer.scheduleTask(new ResetTask(), duration);
					endTimer.start();
					super.clicked(event, x, y);
				}
			}
		});
	}
	
	private class ResetTask extends Timer.Task{

		@Override
		public void run() {
			reset();
		}
		
	}
	
	private class GenerateTargetTask extends Timer.Task{

		@Override
		public void run() {
			generateNewTarget();
		}
		
	}
	
	@Override
	protected void setOrbScale(float newBounds) {
		super.setOrbScale(newBounds);
		if(!move){
			img.setBounds(1920 / 2 - scale / 2, 1080 / 2 - scale / 2, scale, scale);
		}
	}
	
	@Override
	protected void setStage(Stage stage) {
		if (stage != null) {
			stage.addActor(statusLabel);
			stage.addActor(startLabel);
			stage.addActor(frictionSlider);
			stage.addActor(frictionSliderLabel);
			stage.addActor(newTargetSlider);
			stage.addActor(newTargetSliderLabel);
			stage.addActor(durationSlider);
			stage.addActor(durationSliderLabel);
			stage.addActor(resetButton);
		}
		super.setStage(stage);
	}
	
	public void generateNewTarget(){
		targetX = gen.nextFloat() * (maxX - minX) + minX;
		targetY = gen.nextFloat() * (maxY - minY) + minY;
	}
	
	@Override
	public void act(float delta) {
		if(move){
			rangeCheck(delta);
			float deltaX = targetX - img.getX();
			float deltaY = targetY - img.getY();
			float deltaLength = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);
			float deltaNorX = deltaX / deltaLength;
			float deltaNorY = deltaY / deltaLength;
			float dx = speed * deltaNorX * delta;
			float dy = speed * deltaNorY * delta;
			float fric = (float) Math.pow(1 - friction, delta);
			velX *= fric; 
			velY *= fric; 
			velX += dx;
			velY += dy;
			img.setPosition(img.getX() + velX * delta, img.getY() + velY * delta);
			img.rotateBy(300f * delta);
			super.act(delta);
		}
	}
	
	private void rangeCheck(float delta) {
		Stage s = img.getStage();
		Vector2 v = s.screenToStageCoordinates(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
		float deltaX = v.x - (img.getX() + scale / 2);
		float deltaY = v.y - (img.getY() + scale / 2);
		float deltaLength = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);
		if(deltaLength < scale * 0.49){
			accuracy += delta;
		}
		total+= delta;
		statusLabel.setText("Avg Accuracy: " +  String.format("%.1f", accuracy / total * 100) + "%");
	}
	
	private void addAimSlider(){
		SliderStyle style = new SliderStyle();
		style.knob = AssetFactory.getTextureDrawable("slider");
		style.background = AssetFactory.getTextureDrawable("sliderbg");
		style.knobBefore = AssetFactory.getTextureDrawable("sliderfilled");
		frictionSlider = new Slider(0.5f, 1f, 0.001f, false, style);
		frictionSlider.setValue(startFriction);
		frictionSlider.setBounds(100, 170, 500, 20);
		frictionSlider.setDisabled(false);
		frictionSliderLabel = new Label("Orb Friction: " + startFriction, new LabelStyle(AssetFactory.getFont("normal", 20, 0), Color.SKY));
		frictionSliderLabel.setBounds(400, 160, 0, 0);
		frictionSlider.addListener(new ChangeListener() {
			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				friction = frictionSlider.getValue();
				frictionSliderLabel.setText("Orb Friction: " + String.format("%.3f", frictionSlider.getValue()));
			}
		});
		newTargetSlider = new Slider(0.05f, 2.f, 0.01f, false, style);
		newTargetSlider.setValue(startTargetDelay);
		newTargetSlider.setBounds(100, 230, 500, 20);
		newTargetSlider.setDisabled(false);
		newTargetSliderLabel = new Label("New Target Delay: " + startTargetDelay, new LabelStyle(AssetFactory.getFont("normal", 20, 0), Color.SKY));
		newTargetSliderLabel.setBounds(400, 220, 0, 0);
		newTargetSlider.addListener(new ChangeListener() {
			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				newTargetDelay = newTargetSlider.getValue();
				newTargetSliderLabel.setText("New Target Delay: " + String.format("%.2f", newTargetSlider.getValue()));
			}
		});
		durationSlider = new Slider(10, 120, 1, false, style);
		durationSlider.setValue(startDuration);
		durationSlider.setBounds(100, 290, 500, 20);
		durationSlider.setDisabled(false);
		durationSliderLabel = new Label("Test Duration: " + startDuration +  "sec", new LabelStyle(AssetFactory.getFont("normal", 20, 0), Color.SKY));
		durationSliderLabel.setBounds(400, 280, 0, 0);
		durationSlider.addListener(new ChangeListener() {
			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				duration = durationSlider.getValue();
				durationSliderLabel.setText("Test Duration: " + String.format("%.0f", durationSlider.getValue()) + "sec");
			}
		});
	}

	public void setSpeed(float speed){
		this.speed = speed;
	}
	
	private void reset(){
		move = false;
		if(endTimer != null){
			endTimer.clear();
			endTimer.stop();
		}
		if(targetTimer != null){
			targetTimer.clear();
			targetTimer.stop();
		}
		startLabel.setText("Click to Start!");
		img.setBounds(1920 / 2 - scale / 2, 1080 / 2 - scale / 2, scale, scale);
	}
	
	private void addStatusLabel(){
		statusLabel = new Label("Avg Accuracy: 0.0%", new LabelStyle(AssetFactory.getFont("normal", 30, 0), Color.YELLOW));
		statusLabel.setBounds(1500, 60, 0, 0);
	}
	
	private void addResetButton(){
		TextButtonStyle style = new TextButtonStyle();
		style.font = AssetFactory.getFont("normal", 20, 0);
		style.fontColor = Color.BLACK;
		style.up = AssetFactory.getTextureDrawable("button");
		resetButton = new TextButton("Reset", style);
		resetButton.setBounds(1650, 1000, 250, 80);
		resetButton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				reset();
				super.clicked(event, x, y);
			}
		});
	}
	
	private void addStartLabel(){
		startLabel = new Label("Click to Start", new LabelStyle(AssetFactory.getFont("normal", 25, 0), Color.SKY));
		startLabel.setAlignment(Align.center);
		startLabel.setBounds(1920 / 2, 1080 / 2 - 110, 0, 0);
	}
	
	@Override
	public boolean remove() {
		statusLabel.remove();
		startLabel.remove();
		frictionSlider.remove();
		durationSlider.remove();
		newTargetSlider.remove();
		newTargetSliderLabel.remove();
		frictionSliderLabel.remove();
		durationSliderLabel.remove();
		resetButton.remove();
		return super.remove();
	}
}
