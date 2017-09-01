package com.chunyi.tetris;

import com.badlogic.gdx.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class SettingScreen implements Screen {

    //Variables
    final TetrisGame game;
    private Stage stage;
    private Image background;

    public SettingScreen(final TetrisGame game) {
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

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //game.camera.update();

        stage.act(delta);
        stage.draw();

        game.spritebatch.begin();

        game.spritebatch.end();


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