package systems.crigges.fpstest;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class AssetFactory {
	private static Map<String, Texture> textures = new HashMap<String, Texture>();
	private static Map<String, FreeTypeFontGenerator> fonts = new HashMap<String, FreeTypeFontGenerator>();
	private static Map<String, Animation> animations = new HashMap<String, Animation>();
	private static Map<String, Music> musics = new HashMap<String, Music>();
	private static Map<String, Sound> sounds = new HashMap<String, Sound>();
		
	public static void loadAllRessources(){
		addFont("normal", "normal.otf");
		addTexture("default", "default.png");
		addTexture("slider", "slider.png");
		addTexture("sliderbg", "sliderbg.png");
		addTexture("sliderfilled", "sliderfilled.png");
		addTexture("runner", "orb1.png");
		addTexture("button", "warning_message.png");
	}
	
	public static Drawable getTextureDrawable(String name){
		return new TextureRegionDrawable(new TextureRegion(AssetFactory.getTexture(name)));
	}

	private static void addTexture(String title, String name) {
		Texture t = new Texture("textures/" + name);
		t.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		textures.put(title, t);
	}
	
	public static Texture getTexture(String title)	{
		return textures.get(title);
	}
	
	private static void addFont(String title, String path)	{
		FreeTypeFontGenerator gen = new FreeTypeFontGenerator(Gdx.files.internal("fonts/" + path));
		System.out.println(gen);
		fonts.put(title, gen);
	}
	

	public static BitmapFont getFont(String title, int size, int shaddowOffset)	{
		FreeTypeFontGenerator gen = fonts.get(title);
		FreeTypeFontParameter param = new FreeTypeFontParameter();
		param.magFilter = TextureFilter.Linear;
		param.minFilter = TextureFilter.Linear;
		param.shadowColor = new Color(0, 0, 0, 1);
		param.shadowOffsetX = shaddowOffset;
		param.shadowOffsetY = shaddowOffset;
		param.size = size;
		BitmapFont font = gen.generateFont(param);
		return font;
	}
	
	private static void addMusic(String title, Music music){
		musics.put(title, music);
	}
	
	public static Music getMusic(String title){
		return musics.get(title);
	}
	
	private static void addSound(String title, Sound sound){
		sounds.put(title, sound);
	}
	
	public static Sound getSound(String title){
		return sounds.get(title);
	}
	
	private static void addAnimation(String title, Animation anim){
		animations.put(title, anim);
	}
	
	public static Animation getAnimation(String title){
		return animations.get(title);
	}

}
