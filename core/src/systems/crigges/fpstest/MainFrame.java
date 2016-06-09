package systems.crigges.fpstest;

import java.nio.Buffer;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Slider.SliderStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Queue;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.StretchViewport;

public class MainFrame extends ApplicationAdapter {
	private static final int viewportWidth = 1920;
	private static final int viewportHeight = 1080;
	
	private Stage stage;
	private OrthographicCamera camera;
	private StretchViewport viewport;
	private Queue<Float> frametimes = new Queue<Float>();
	private float currentTotalFrametime = 0f;
	private int frametimeBufferSize = 140;
	private int targetFps = 60;
    private long previousTime = TimeUtils.nanoTime();
    private Actor currentTestOrb;
	
	@Override
	public void create () {
		AssetFactory.loadAllRessources();
		camera = new OrthographicCamera();
		camera.zoom = 1f;
		camera.position.set(viewportWidth / 2, viewportHeight / 2, camera.position.z);
		viewport = new StretchViewport(viewportWidth, viewportHeight, camera);
		stage = new Stage(viewport);
		addFpsCounter();
		addFrameSlider();
		addOptionButtons();
		addVsyncSlider();
		Gdx.input.setInputProcessor(stage);
		Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode(Gdx.graphics.getMonitor()));
	}
	

	


	@Override
	public void render() {
		float delta = Gdx.graphics.getDeltaTime();
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stage.act(delta);
		stage.draw();
		calcFrametimes(delta);
		sleep(targetFps);
	}
	
	public void sleep(int fps) {
	    if(fps>0){
	      long diff = TimeUtils.nanoTime() - previousTime;
	      long targetDelay = 1000000000/fps;
	      if (diff < targetDelay) {
	        try{
	        	long target = targetDelay - diff;
	        	long millis = target / 1000000;
	        	long nanos = target % 1000000;
	            Thread.sleep(millis, (int) nanos);
	          } catch (InterruptedException e) {}
	        }   
	      previousTime = TimeUtils.nanoTime();
	    }
	}
	
	@Override
	public void resize(int width, int height) {
		viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		super.resize(width, height);
	}
	
	private void calcFrametimes(float delta){
		frametimes.addFirst(delta);
		currentTotalFrametime += delta;
		while(frametimes.size > frametimeBufferSize){
			currentTotalFrametime -= frametimes.removeLast();
		}
	}
	
	private void addFrameSlider(){
		SliderStyle style = new SliderStyle();
		style.knob = AssetFactory.getTextureDrawable("slider");
		style.background = AssetFactory.getTextureDrawable("sliderbg");
		style.knobBefore = AssetFactory.getTextureDrawable("sliderfilled");
		final Slider slider = new Slider(10, 500, 1, false, style);
		slider.setValue(60);
		slider.setBounds(700, 100, 500, 20);
		slider.setDisabled(false);
		final Label l = new Label("Target Fps: 60", new LabelStyle(AssetFactory.getFont("normal", 20, 0), Color.SKY));
		l.setBounds(1000, 70, 0, 0);
		slider.addListener(new ChangeListener() {
			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				targetFps = (int) slider.getValue();
				l.setText("Target Fps: " + targetFps);
				frametimeBufferSize = (int) slider.getValue();
			}
		});
		stage.addActor(l);
		stage.addActor(slider);
	}
	
	private void addOptionButtons() {
		TextButtonStyle style = new TextButtonStyle();
		style.font = AssetFactory.getFont("normal", 20, 0);
		style.fontColor = Color.BLACK;
		style.up = AssetFactory.getTextureDrawable("button");
		TextButton but = new TextButton("Orb Testing", style);
		but.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if(currentTestOrb == null){
					currentTestOrb = new CirclingOrb();
					stage.addActor(currentTestOrb);
				}else if(!(currentTestOrb instanceof CirclingOrb)){
					currentTestOrb.remove();
					currentTestOrb = new CirclingOrb();
					stage.addActor(currentTestOrb);
				}
			}
		});
		but.setBounds(600, 1000, 250, 80);
		stage.addActor(but);
		but = new TextButton("Reaction Time Testing", style);
		but.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if(currentTestOrb == null){
					currentTestOrb = new ReactionTest();
					stage.addActor(currentTestOrb);
				}else if(!(currentTestOrb instanceof ReactionTest)){
					currentTestOrb.remove();
					currentTestOrb = new ReactionTest();
					stage.addActor(currentTestOrb);
				}
			}
		});
		but.setBounds(840, 1000, 250, 80);
		stage.addActor(but);
		but = new TextButton("Aim Testing", style);
		but.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if(currentTestOrb == null){
					currentTestOrb = new ChaseOrb();
					stage.addActor(currentTestOrb);
				}else if(!(currentTestOrb instanceof ChaseOrb)){
					currentTestOrb.remove();
					currentTestOrb = new ChaseOrb();
					stage.addActor(currentTestOrb);
				}
			}
		});
		but.setBounds(1080, 1000, 250, 80);
		stage.addActor(but);
	}
	
	
	private void addVsyncSlider(){
		SliderStyle style = new SliderStyle();
		style.knob = AssetFactory.getTextureDrawable("slider");
		style.background = AssetFactory.getTextureDrawable("sliderbg");
		style.knobBefore = AssetFactory.getTextureDrawable("sliderfilled");
		final Slider slider = new Slider(0, 1, 1, false, style);
		slider.setValue(0);
		slider.setBounds(1300, 100, 35, 20);
		slider.setDisabled(false);
		final Label l = new Label("vSync disabeld", new LabelStyle(AssetFactory.getFont("normal", 20, 0), Color.SKY));
		l.setBounds(1250, 70, 0, 0);
		slider.addListener(new ChangeListener() {
			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if(slider.getValue() > 0.5f){
					l.setText("vSync enabeld");
					Gdx.graphics.setVSync(true);
				}else{
					l.setText("vSync disabeld");
					Gdx.graphics.setVSync(false);
				}
				
			}
		});
		stage.addActor(l);
		stage.addActor(slider);
	}
	
	private void addFpsCounter(){
		final Label l = new Label("Fps: 0", new LabelStyle(AssetFactory.getFont("normal", 40, 0), Color.YELLOW));
		l.addAction(new Action() {
			
			@Override
			public boolean act(float delta) {
				l.setText("Fps: " + Gdx.graphics.getFramesPerSecond() + "  |  "+ + (int)(1f / (currentTotalFrametime / frametimeBufferSize)) + "\n@" + Gdx.graphics.getDisplayMode(Gdx.graphics.getMonitor()).refreshRate + "Hz");
				//l.setText("Fps: " + (int)(1f / (currentTotalFrametime / frametimeBufferSize)));
				return false;
			}
		});
		l.setAlignment(Align.topLeft);
		l.setBounds(20, 1080, 0, 0);
		stage.addActor(l);
	}
}
