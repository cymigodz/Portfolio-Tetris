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

public class GameScreen implements Screen {

    //Variables
    final TetrisGame game;

    private static final String FILE_PATH_TETRIS_BLOCK_A = "sprite/puzzle-pack-ii/PNG/Tiles black/tileBlack_27.png";
    private static final String FILE_PATH_TETRIS_BLOCK_B = "sprite/puzzle-pack-ii/PNG/Tiles blue/tileBlue_27.png";
    private static final String FILE_PATH_TETRIS_BLOCK_C = "sprite/puzzle-pack-ii/PNG/Tiles green/tileGreen_27.png";
    private static final String FILE_PATH_TETRIS_BLOCK_D = "sprite/puzzle-pack-ii/PNG/Tiles orange/tileOrange_26.png";
    private static final String FILE_PATH_TETRIS_BLOCK_E = "sprite/puzzle-pack-ii/PNG/Tiles pink/tilePink_27.png";
    private static final String FILE_PATH_TETRIS_BLOCK_F = "sprite/puzzle-pack-ii/PNG/Tiles red/tileRed_27.png";
    private static final String FILE_PATH_TETRIS_BLOCK_G = "sprite/puzzle-pack-ii/PNG/Tiles yellow/tileYellow_27.png";
    private Stage stage;
    private Image background;

    private Image gridA, gridB, gridC, gridD, gridE, gridF, gridG;

    //Hardcode Rotation Information
    private static final Integer[][] PATTERN_BLOCK_A_X = new Integer [][]{
            {-1, 0, 1, 2},
            {1, 1, 1, 1},
            {-1, 0, 1, 2},
            {0, 0, 0, 0}};
    private static final Integer[][] PATTERN_BLOCK_A_Y = new Integer [][]{
            {0, 0, 0, 0},
            {1, 0, -1, -2},
            {-1, -1, -1, -1},
            {1, 0, -1, -2}};
    private static final Integer[][] PATTERN_BLOCK_B_X = new Integer [][]{
            {-1, -1, 0, 1},
            {},{},{}};
    private static final Integer[][] PATTERN_BLOCK_B_Y = new Integer [][]{
            {1, 0, 0, 0}
            ,{},{},{}};
    private static final Integer[][] PATTERN_BLOCK_C_X = new Integer [][]{
            {-1, 0, 1, 1}
            ,{},{},{}};
    private static final Integer[][] PATTERN_BLOCK_C_Y = new Integer [][]{
            {0, 0, 0, 1}
            ,{},{},{}};
    private static final Integer[][] PATTERN_BLOCK_D_X = new Integer [][]{
            {0, 1, 0, 1}
            ,{},{},{}};
    private static final Integer[][] PATTERN_BLOCK_D_Y = new Integer [][]{
            {0, 0, 1, 1}
            ,{},{},{}};
    private static final Integer[][] PATTERN_BLOCK_E_X = new Integer [][]{
            {-1, 0, 0, 1}
            ,{},{},{}};
    private static final Integer[][] PATTERN_BLOCK_E_Y = new Integer [][]{
            {0, 0, 1, 1}
            ,{},{},{}};
    private static final Integer[][] PATTERN_BLOCK_F_X = new Integer [][]{
            {-1, 0, 0, 1}
            ,{},{},{}};
    private static final Integer[][] PATTERN_BLOCK_F_Y = new Integer [][]{
            {0, 0, 1, 0}
            ,{},{},{}};
    private static final Integer[][] PATTERN_BLOCK_G_X = new Integer [][]{
            {-1, 0, 0, 1}
            ,{},{},{}};
    private static final Integer[][] PATTERN_BLOCK_G_Y = new Integer [][]{
            {1, 1, 0, 0}
            ,{},{},{}};

    private Float varRowHeight, varColWidth;
    private Float[] coordRow, coordCol;


    public GameScreen(final TetrisGame game) {
        this.game = game;

        //STAGE
        stage = new Stage(new ScreenViewport(), game.spritebatch);
        Gdx.input.setInputProcessor(stage);

        //BACKGROUND
        NinePatch bgPatch = new NinePatch(new Texture(Gdx.files.internal("sprite/uipack_fixed/PNG/grey_panel.png")),10,10,10,10);
        background = new Image(new NinePatchDrawable(bgPatch));
        background.setWidth(game.DISPLAY_WIDTH/5*3);
        background.setHeight(game.DISPLAY_HEIGHT/10*9);
        background.setPosition((game.DISPLAY_WIDTH/2) - (background.getWidth()/2), game.DISPLAY_HEIGHT/2 - background.getHeight()/2);
        stage.addActor(background);

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

    private void spawnBlock(Integer[][] block, Image blockImage, Integer rotation, Integer spawnCol, Integer spawnRow){

        for(Integer pieceIteration = 0; pieceIteration < 4; pieceIteration++){
            Image tetromino = new Image(blockImage.getDrawable());
            tetromino.setSize(varColWidth, varRowHeight);
            tetromino.setX(coordCol[spawnCol + block[rotation][pieceIteration]]);
            tetromino.setY(coordRow[spawnRow + block[rotation][pieceIteration]]);
            stage.addActor(tetromino);
        }
    }

    private void initializeParameters(){
        //ROW PREPERATION
        coordRow = new Float[20];
        Float startRow = background.getY() + background.getHeight()*0.01f;
        varRowHeight = (background.getHeight()-background.getHeight()*0.02f)/20;
        for(Integer i = 0; i < 20; i ++){
            coordRow[i] = startRow;
            startRow += varRowHeight;
        }

        //COL PREPERATION
        coordCol = new Float[10];
        Float startCol = (background.getX() + background.getWidth()*0.02f);
        varColWidth = (background.getWidth()-background.getWidth()*0.04f)/10;
        for(Integer i = 0; i < 10; i ++){
            coordCol[i] = startCol;
            startCol += varColWidth;
        }

        //GRID TEXTURE PREPERATION
        gridA = new Image(new Texture(Gdx.files.internal(FILE_PATH_TETRIS_BLOCK_A)));
        gridB = new Image(new Texture(Gdx.files.internal(FILE_PATH_TETRIS_BLOCK_B)));
        gridC = new Image(new Texture(Gdx.files.internal(FILE_PATH_TETRIS_BLOCK_C)));
        gridD = new Image(new Texture(Gdx.files.internal(FILE_PATH_TETRIS_BLOCK_D)));
        gridE = new Image(new Texture(Gdx.files.internal(FILE_PATH_TETRIS_BLOCK_E)));
        gridF = new Image(new Texture(Gdx.files.internal(FILE_PATH_TETRIS_BLOCK_F)));
        gridG = new Image(new Texture(Gdx.files.internal(FILE_PATH_TETRIS_BLOCK_G)));

        //GRID PATTERN PREPERATION
        Integer[][] patternBlockA_X = new Integer[] []{
                {-1, 0, 1, 2},
                {1, 1, 1, 1},
                {-1, 0, 1, 2},
                {0, 0, 0, 0}};
        Integer[][] patternBlockA_Y = new Integer[][]{
                {0, 0, 0, 0},
                {1, 0, -1, -2},
                {-1, -1, -1, -1},
                {1, 0, -1, -2}};

        Integer[] patternBlockB_X = new Integer[] {-1, -1, 0, 1};
        Integer[] patternBlockB_Y = new Integer[] {1, 0, 0, 0};

        Integer[] patternBlockC_X = new Integer[] {-1, 0, 1, 1};
        Integer[] patternBlockC_Y = new Integer[] {0, 0, 0, 1};

        Integer[] patternBlockD_X = new Integer[] {0, 1, 0, 1};
        Integer[] patternBlockD_Y = new Integer[] {0, 0, 1, 1};

        Integer[] patternBlockE_X = new Integer[] {-1, 0, 0, 1};
        Integer[] patternBlockE_Y = new Integer[] {0, 0, 1, 1};

        Integer[] patternBlockF_X = new Integer[] {-1, 0, 0, 1};
        Integer[] patternBlockF_Y = new Integer[] {0, 0, 1, 0};

        Integer[] patternBlockG_X = new Integer[] {-1, 0, 0, 1};
        Integer[] patternBlockG_Y = new Integer[] {1, 1, 0, 0};
    }

}