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
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Slider.SliderStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
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
    private MazeRunner orb;
	
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
		orb = new MazeRunner();
		stage.addActor(orb);
		addOrbSlider();
		addVsyncSlider();
		System.out.println(Gdx.graphics.getWidth());
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
//	
//	public void delay() {
//        currentTime = TimeUtils.nanoTime();
//        deltaTime += currentTime - previousTime;
//        while (deltaTime < 1000000000 / targetFps) {
//            previousTime = currentTime;
//            long diff = (long) (1000000000 / targetFps - deltaTime);
//            if (diff / 1000000 > 1) {
//                try {
//                    Thread.sleep(diff / 1000000 - 1);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//            currentTime = TimeUtils.nanoTime();
//            deltaTime += currentTime - previousTime;
//            previousTime = currentTime;
//        }
//        deltaTime -= 1000000000 / targetFps;
//    }
	
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
				frametimeBufferSize = (int) slider.getValue() * 10;
			}
		});
		stage.addActor(l);
		stage.addActor(slider);
	}
	
	private void addOrbSlider(){
		SliderStyle style = new SliderStyle();
		style.knob = AssetFactory.getTextureDrawable("slider");
		style.background = AssetFactory.getTextureDrawable("sliderbg");
		style.knobBefore = AssetFactory.getTextureDrawable("sliderfilled");
		final Slider slider = new Slider(0, 10, 0.01f, false, style);
		slider.setValue(0.4f);
		slider.setBounds(100, 100, 500, 20);
		slider.setDisabled(false);
		final Label l = new Label("Orb Speed: 0.4", new LabelStyle(AssetFactory.getFont("normal", 20, 0), Color.SKY));
		l.setBounds(400, 70, 0, 0);
		slider.addListener(new ChangeListener() {
			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				orb.setSpeed(slider.getValue());
				l.setText("Orb Speed: " + String.format("%.2f", slider.getValue()));
			}
		});
		stage.addActor(l);
		stage.addActor(slider);
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
		final Label l = new Label("Fps: 0", new LabelStyle(AssetFactory.getFont("normal", 50, 0), Color.YELLOW));
		l.addAction(new Action() {
			
			@Override
			public boolean act(float delta) {
				l.setText("Fps: " + Gdx.graphics.getFramesPerSecond() + "  |  "+ + (int)(1f / (currentTotalFrametime / frametimeBufferSize)));
				//l.setText("Fps: " + (int)(1f / (currentTotalFrametime / frametimeBufferSize)));
				return false;
			}
		});
		l.setAlignment(Align.topLeft);
		l.setBounds(20, 1080, 0, 0);
		stage.addActor(l);
	}
}
