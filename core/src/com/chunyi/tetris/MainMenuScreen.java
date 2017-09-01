package com.chunyi.tetris;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class MainMenuScreen implements Screen {

    //Variables
    final TetrisGame game;

    private static final String TITLE_NAME = "TETRIS";
    private static final String FILE_PATH_BUTTON_BLUE = "sprite/uipack_fixed/PNG/blue_button";

    private TextButton startBtn, settingBtn, exitBtn;
    private TextButton.TextButtonStyle mainMenuBtnStyle;
    private Image background;
    private Label title;
    private Label.LabelStyle titleStyle;

    private Stage stage;

    public MainMenuScreen(final TetrisGame game) {
        this.game = game;


        //STAGE
        stage = new Stage(new ScreenViewport(), game.spritebatch);
        Gdx.input.setInputProcessor(stage);


        //BACKGROUND
        NinePatch bgPatch = new NinePatch(new Texture(Gdx.files.internal("sprite/uipack_fixed/PNG/grey_panel.png")),10,10,10,10);
        background = new Image(new NinePatchDrawable(bgPatch));
        background.setHeight(game.DISPLAY_HEIGHT/10*9);
        background.setWidth(background.getHeight()/15*8);
        background.setPosition((game.DISPLAY_WIDTH/2) - (background.getWidth()/2), game.DISPLAY_HEIGHT/2 - background.getHeight()/2);
        stage.addActor(background);

        //TITLE
        titleStyle = new Label.LabelStyle(game.titleFont, Color.BLACK);
        title = new Label("TETRIS",titleStyle);
        title.setPosition((game.DISPLAY_WIDTH/2) - (title.getWidth()/2), game.DISPLAY_HEIGHT/4*3);
        stage.addActor(title);

        //BUTTON STYLE FOR MAIN MENU
        NinePatch upPatch = new NinePatch(new Texture(Gdx.files.internal(FILE_PATH_BUTTON_BLUE + "09.png")),10,10,10,10);
        NinePatchDrawable upPatchDrawable = new NinePatchDrawable(upPatch);
        NinePatch downPatch = new NinePatch(new Texture(Gdx.files.internal(FILE_PATH_BUTTON_BLUE + "10.png")),10,10,10,10);
        NinePatchDrawable downPatchDrawable = new NinePatchDrawable(downPatch);
        NinePatch overPatch = new NinePatch(new Texture(Gdx.files.internal(FILE_PATH_BUTTON_BLUE + "11.png")),10,10,10,10);
        NinePatchDrawable overPatchDrawable = new NinePatchDrawable(overPatch);

        mainMenuBtnStyle = new TextButton.TextButtonStyle();
        mainMenuBtnStyle.up = upPatchDrawable;
        mainMenuBtnStyle.down = downPatchDrawable;
        mainMenuBtnStyle.over = overPatchDrawable;
        mainMenuBtnStyle.font =  game.normalFont1;

        //START BUTTON
        startBtn = new TextButton("Start", mainMenuBtnStyle);
        startBtn.setWidth(game.DISPLAY_WIDTH/2);
        startBtn.setHeight(game.DISPLAY_HEIGHT/15);
        startBtn.setPosition(game.DISPLAY_WIDTH/2-startBtn.getWidth()/2, game.DISPLAY_HEIGHT/2-100);
        stage.addActor(startBtn);
        startBtn.addListener(new InputListener(){

            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                Gdx.app.log("DEBUG", "Start new game");
                return true;
            }

            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                game.setScreen(new GameScreen(game));
                dispose();
            }

        });

        //SETTING BUTTON
        settingBtn = new TextButton("Setting", mainMenuBtnStyle);
        settingBtn.setWidth(game.DISPLAY_WIDTH/2);
        settingBtn.setHeight(game.DISPLAY_HEIGHT/15);
        settingBtn.setPosition(game.DISPLAY_WIDTH/2-settingBtn.getWidth()/2, game.DISPLAY_HEIGHT/2 - settingBtn.getHeight()*2-100);
        stage.addActor(settingBtn);
        settingBtn.addListener(new InputListener(){

            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                Gdx.app.log("DEBUG", "Setting");
                return true;
            }

            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                game.setScreen(new SettingScreen(game));
                dispose();
            }

        });

        //EXIT BUTTON
        exitBtn = new TextButton("Exit", mainMenuBtnStyle);
        exitBtn.setWidth(game.DISPLAY_WIDTH/2);
        exitBtn.setHeight(game.DISPLAY_HEIGHT/15);
        exitBtn.setPosition(game.DISPLAY_WIDTH/2-exitBtn.getWidth()/2, game.DISPLAY_HEIGHT/2 - settingBtn.getHeight()*4-100);
        stage.addActor(exitBtn);
        exitBtn.addListener(new InputListener(){

            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                Gdx.app.log("DEBUG", "Exit");
                return true;
            }

            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                Gdx.app.exit();
                dispose();
            }

        });

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void show() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

}