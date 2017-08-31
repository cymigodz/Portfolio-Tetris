package com.chunyi.tetris;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

public class Game extends ApplicationAdapter {

	//constants

	//Preference File Names
	private static final String PREFERENCE_CONFIG_GAME		= "gameConfig";
	private static final String PREFERENCE_CONFIG_DESKTOP 	= "desktopConfig";
	private static final String PREFERENCE_CONFIG_ANDROID 	= "androidConfig";

	//Preference Config Names
	private static final String NAME_INITIALIZED = "INITIALIZED";
	private static final String NAME_DIFFICULTY_SPEED = "DIFFICULTY_SPEED";
	private static final String NAME_DIFFICULTY_INFINITY = "DIFFICULTY_INFINITY";
	private static final String NAME_DIFFICULTY_GRAVITY = "DIFFICULTY_GRAVITY";

	private static final String NAME_AUDIO_FX_VOLUME = "AUDIO_FX_VOLUME";
	private static final String NAME_AUDIO_MUSIC_VOLUME = "AUDIO_MUSIC_VOLUME";

	private static final String NAME_DISPLAY_WIDTH = "DISPLAY_WIDTH";
	private static final String NAME_DISPLAY_HEIGHT = "DISPLAY_HEIGHT";
	private static final String NAME_DISPLAY_FULLSCREEN = "DISPLAY_FULLSCREEN";

	private static final String NAME_CONTROL_MOVE_LEFT = "CONTROL_MOVE_LEFT";
	private static final String NAME_CONTROL_MOVE_RIGHT = "CONTROL_MOVE_RIGHT";
	private static final String NAME_CONTROL_ROTATE_COUNTER = "CONTROL_ROTATE_COUNTER";
	private static final String NAME_CONTROL_ROTATE_CLOCK = "CONTROL_ROTATE_CLOCK";
	private static final String NAME_CONTROL_DROP_SOFT = "CONTROL_DROP_SOFT";
	private static final String NAME_CONTROL_DROP_HARD = "CONTROL_DROP_HARD";
	private static final String NAME_CONTROL_HOLD = "CONTROL_HOLD";

	//The frequency of controls values being called is too high to constantly take from
	// preference object.
	private String CONTROL_MOVE_LEFT;
	private String CONTROL_MOVE_RIGHT;
	private String CONTROL_ROTATE_COUNTER;
	private String CONTROL_ROTATE_CLOCK;
	private String CONTROL_DROP_SOFT;
	private String CONTROL_DROP_HARD;
	private String CONTROL_HOLD	;

	//The frequency of display size value is high too, to anchor display elements. TBC
	private Integer DISPLAY_WIDTH;
	private Integer DISPLAY_HEIGHT;

	//Camera used to lock down our render size regardless of device size.
	private OrthographicCamera camera;

	//TEST//TEST//TEST//TEST//TEST//TEST//TEST//TEST//TEST//TEST
	private BitmapFont font, titleFont;
	private SpriteBatch batch;
	GlyphLayout titleLayout;


	TextButton startBtn,settingBtn,exitBtn;

	private static final String STRING_TITLE_NAME = "TETRIS";



	@Override
	public void create () {

		camera = new OrthographicCamera();
		camera.setToOrtho(false, 600, 800);

		//Initialize Configs If Game is run for the first time
		Preferences gameConfig = getGameConfig();
		Preferences platformConfig = getPlatformConfig();
		initConfig(gameConfig, platformConfig);

		//Adjust configs according to the preferences
		if(isDesktop()){
			applyDesktopConfig();
			DISPLAY_WIDTH = platformConfig.getInteger(NAME_DISPLAY_WIDTH);
			DISPLAY_HEIGHT = platformConfig.getInteger(NAME_DISPLAY_HEIGHT);

		}


////TEST///TEST//TEST//TEST//TEST//TEST//TEST//TEST//TEST//TEST//TEST
		font = new BitmapFont();

		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("font/8bit_wonder/8-bit-wonder.ttf"));
		FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
		parameter.size = 60;
		titleFont = generator.generateFont(parameter); // font size 12 pixels
		generator.dispose(); // don't forget to dispose to avoid memory leaks!

		titleLayout = new GlyphLayout(titleFont, STRING_TITLE_NAME);

		batch = new SpriteBatch();

	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		camera.update();
		batch.begin();
		font.draw(batch, Integer.toString(Gdx.graphics.getFramesPerSecond()), 10,DISPLAY_HEIGHT-10);
		titleFont.draw(batch, STRING_TITLE_NAME, (DISPLAY_WIDTH/2) - (titleLayout.width/2), DISPLAY_HEIGHT/4*3);
		batch.end();
	}
	
	@Override
	public void dispose () {
	batch.dispose();
	}


	//Hard coded initialization of configurations
	private static void initConfig(Preferences gameConfig, Preferences platformConfig){

		if(!gameConfig.contains(NAME_INITIALIZED)){
			Gdx.app.log("Config", "Initializing Game Config");
			gameConfig.putInteger(NAME_DIFFICULTY_SPEED, 1);
			gameConfig.putBoolean(NAME_DIFFICULTY_INFINITY, false);
			gameConfig.putBoolean(NAME_DIFFICULTY_GRAVITY, false);

			gameConfig.putInteger(NAME_AUDIO_FX_VOLUME, 50);
			gameConfig.putInteger(NAME_AUDIO_MUSIC_VOLUME, 50);

			gameConfig.putBoolean(NAME_INITIALIZED, true);
			gameConfig.flush();
		}

		if(!platformConfig.contains(NAME_INITIALIZED)){
			if(isDesktop()) {
				Gdx.app.log("Config", "Initializing Desktop Config");
				platformConfig.putInteger(NAME_DISPLAY_WIDTH, 600);
				platformConfig.putInteger(NAME_DISPLAY_HEIGHT, 800);
				platformConfig.putBoolean(NAME_DISPLAY_FULLSCREEN, false);

				platformConfig.putString(NAME_CONTROL_MOVE_LEFT, "LEFT");
				platformConfig.putString(NAME_CONTROL_MOVE_RIGHT, "RIGHT");
				platformConfig.putString(NAME_CONTROL_ROTATE_COUNTER, "Q");
				platformConfig.putString(NAME_CONTROL_ROTATE_CLOCK, "UP");
				platformConfig.putString(NAME_CONTROL_DROP_SOFT, "DOWN");
				platformConfig.putString(NAME_CONTROL_DROP_HARD, "SPACE");
				platformConfig.putString(NAME_CONTROL_HOLD, "W");

				platformConfig.putBoolean(NAME_INITIALIZED, true);
				platformConfig.flush();
			} else if(isAndroid()){


			}
		}
	}

	//True if current run time platform is DESKTOP
	private static boolean isDesktop(){
		if(Gdx.app.getType() == Application.ApplicationType.Desktop){
			return true;
		} else {
			return false;
		}
	}
	//True if current run time platform is ANDROID
	private static boolean isAndroid(){
		if(Gdx.app.getType() == Application.ApplicationType.Android){
			return true;
		} else {
			return false;
		}
	}

	private static void applyDesktopConfig(){

		if(!isDesktop()){
			return;
		}

		Preferences platformConfig = getPlatformConfig();


		if(platformConfig.getBoolean(NAME_DISPLAY_FULLSCREEN)){
			Graphics.Monitor currMonitor = Gdx.graphics.getMonitor();
			Graphics.DisplayMode displayMode = Gdx.graphics.getDisplayMode(currMonitor);
			if(!Gdx.graphics.setFullscreenMode(displayMode)) {
				// switching to full-screen mode failed
			}
		} else {
			Gdx.graphics.setWindowedMode(platformConfig.getInteger(NAME_DISPLAY_WIDTH), platformConfig.getInteger(NAME_DISPLAY_HEIGHT));
		}
	}

	//Getters
	private static Preferences getGameConfig(){
		return Gdx.app.getPreferences(PREFERENCE_CONFIG_GAME);
	}
	private static Preferences getPlatformConfig(){
		if(isDesktop()) {
			return Gdx.app.getPreferences(PREFERENCE_CONFIG_DESKTOP);
		} else if (isAndroid()) {
			return Gdx.app.getPreferences(PREFERENCE_CONFIG_ANDROID);
		} else {
			return null;
		}
	}

	//Setters
	private static void setGameConfig(Integer speed,
									   Boolean infinity,
									   Boolean gravity,
									   Integer fxVol,
									   Integer musicVol) {

		Preferences gameConfig = getGameConfig();

		gameConfig.putInteger(NAME_DIFFICULTY_SPEED, speed);
		gameConfig.putBoolean(NAME_DIFFICULTY_INFINITY, infinity);
		gameConfig.putBoolean(NAME_DIFFICULTY_GRAVITY, gravity);

		gameConfig.putInteger(NAME_AUDIO_FX_VOLUME, fxVol);
		gameConfig.putInteger(NAME_AUDIO_MUSIC_VOLUME, musicVol);

		gameConfig.flush();
	}

	private static void setDesktopConfig(Integer width,
										  Integer height,
										  Boolean fullscreen,
										  String moveL,
										  String moveR,
										  String rotateL,
										  String rotateR,
										  String dropSoft,
										  String dropHard,
										  String hold){
		Preferences platformConfig = getPlatformConfig();

		platformConfig.putInteger(NAME_DISPLAY_WIDTH, width);
		platformConfig.putInteger(NAME_DISPLAY_HEIGHT, height);
		platformConfig.putBoolean(NAME_DISPLAY_FULLSCREEN, fullscreen);

		platformConfig.putString(NAME_CONTROL_MOVE_LEFT, moveL);
		platformConfig.putString(NAME_CONTROL_MOVE_RIGHT, moveR);
		platformConfig.putString(NAME_CONTROL_ROTATE_COUNTER, rotateL);
		platformConfig.putString(NAME_CONTROL_ROTATE_CLOCK, rotateR);
		platformConfig.putString(NAME_CONTROL_DROP_SOFT, dropSoft);
		platformConfig.putString(NAME_CONTROL_DROP_HARD, dropHard);
		platformConfig.putString(NAME_CONTROL_HOLD, hold);

		platformConfig.flush();
	}
}


