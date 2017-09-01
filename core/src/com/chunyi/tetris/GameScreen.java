package com.chunyi.tetris;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.Timer;

public class GameScreen implements Screen {

    //game reference
    final TetrisGame game;

    //TEXTURE
    private static final Texture[] TEXTURE_TETROMINOES = new Texture[]{
            new Texture(Gdx.files.internal("sprite/puzzle-pack-ii/PNG/Tiles black/tileBlack_27.png")),
            new Texture(Gdx.files.internal("sprite/puzzle-pack-ii/PNG/Tiles blue/tileBlue_27.png")),
            new Texture(Gdx.files.internal("sprite/puzzle-pack-ii/PNG/Tiles green/tileGreen_27.png")),
            new Texture(Gdx.files.internal("sprite/puzzle-pack-ii/PNG/Tiles orange/tileOrange_26.png")),
            new Texture(Gdx.files.internal("sprite/puzzle-pack-ii/PNG/Tiles pink/tilePink_27.png")),
            new Texture(Gdx.files.internal("sprite/puzzle-pack-ii/PNG/Tiles red/tileRed_27.png")),
            new Texture(Gdx.files.internal("sprite/puzzle-pack-ii/PNG/Tiles yellow/tileYellow_27.png"))};

    //ROTATION : [Block Type 7][Rotation Number 4][Piece Number 4][X/Y 2]
    private static final Integer [][][][] PATTERN_TETROMINOES = new Integer [][][][]{
            //TYPE 1 - I
            {
                    //ROTATION 1
                    {{-1,0},{0,0},{1,0},{2,0}},
                    //ROTATION 2
                    {{1,1},{1,0},{1,-1},{1,-2}},
                    //ROTATION 3
                    {{-1,-1},{0,-1},{1,-1},{2,-1}},
                    //ROTATION 4
                    {{0,1},{0,0},{0,-1},{0,-2}}},
            //TYPE 2 - J
            {
                    //ROTATION 1
                    {{-1,1},{-1,0},{0,0},{1,0}},
                    //ROTATION 2
                    {{1,1},{0,1},{0,0},{0,-1}},
                    //ROTATION 3
                    {{1,-1},{1,0},{0,0},{-1,0}},
                    //ROTATION 4
                    {{-1,-1},{0,-1},{0,0},{0,1}}},
            //TYPE 3 - L
            {
                    //ROTATION 1
                    {{-1,0},{0,0},{1,0},{1,1}},
                    //ROTATION 2
                    {{0,1},{0,0},{0,-1},{1,-1}},
                    //ROTATION 3
                    {{-1,0},{0,0},{1,0},{-1,-1}},
                    //ROTATION 4
                    {{0,1},{0,0},{0,-1},{-1,1}}},
            //TYPE 4 - O
            {
                    //ROTATION 1
                    {{0,0},{1,0},{0,1},{1,1}},
                    //ROTATION 2
                    {{0,0},{1,0},{0,1},{1,1}},
                    //ROTATION 3
                    {{0,0},{1,0},{0,1},{1,1}},
                    //ROTATION 4
                    {{0,0},{1,0},{0,1},{1,1}}},
            //TYPE 5 - S
            {
                    //ROTATION 1
                    {{0,0},{-1,0},{0,1},{1,1}},
                    //ROTATION 2
                    {{0,0},{1,0},{0,1},{1,-1}},
                    //ROTATION 3
                    {{0,0},{1,0},{0,-1},{-1,-1}},
                    //ROTATION 4
                    {{0,0},{-1,0},{0,-1},{-1,1}}},
            //TYPE 6 - T
            {
                    //ROTATION 1
                    {{0,0},{-1,0},{1,0},{0,1}},
                    //ROTATION 2
                    {{0,0},{0,-1},{1,0},{0,1}},
                    //ROTATION 3
                    {{0,0},{-1,0},{1,0},{0,-1}},
                    //ROTATION 4
                    {{0,0},{-1,0},{0,-1},{0,1}}},
            //TYPE 7 - Z
            {
                    //ROTATION 1
                    {{0,0},{-1,1},{0,1},{1,0}},
                    //ROTATION 2
                    {{0,0},{1,1},{1,0},{0,-1}},
                    //ROTATION 3
                    {{0,0},{-1,0},{0,-1},{1,-1}},
                    //ROTATION 4
                    {{0,0},{0,1},{-1,0},{-1,-1}}}
    };

    //STAGE ELEMENTS
    private Stage stage;
    private Image background;
    private TextField input_Type, input_Rotation, input_X, input_Y;
    private TextField.TextFieldStyle inputStyle_Type, inputStyle_Rotation, inputStyle_X, inputStyle_Y;
    private TextButton spawnBtn,lockBtn;


    private Float varRowHeight, varColWidth;
    private Float[] coordRow, coordCol;

    private Image [] activeTetromino = new Image[4];
    private Boolean hasActiveTetromino = false;
    private Integer activeType, activeRotation;

    private float timer = 0.0f;


    public GameScreen(final TetrisGame game) {
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

        initializeParameters();



        NinePatch btnPatch = new NinePatch(new Texture(Gdx.files.internal("sprite/uipack_fixed/PNG/grey_button06.png")),10,10,10,10);

        inputStyle_Type = new TextField.TextFieldStyle(game.normalFont1, Color.BLACK,
                new Image(new Texture(Gdx.files.internal("sprite/uipack_fixed/PNG/blue_tick.png"))).getDrawable(),
                new Image(new Texture(Gdx.files.internal("sprite/puzzlepack/png/selectorA.png"))).getDrawable(),
                new NinePatchDrawable(btnPatch));

        input_Type = new TextField("1", inputStyle_Type);
        input_Type.setHeight(game.normalFont1.getLineHeight()*2f);
        input_Type.setPosition(background.getX(), background.getY()-input_Type.getHeight()*1.1f);
        input_Type.setMaxLength(1);
        input_Type.setWidth((new GlyphLayout(game.normalFont1, "555")).width);

        input_Rotation = new TextField("1", inputStyle_Type);
        input_Rotation.setHeight(game.normalFont1.getLineHeight()*2f);
        input_Rotation.setPosition(input_Type.getX() + input_Type.getWidth(), background.getY()-input_Type.getHeight()*1.1f);
        input_Rotation.setMaxLength(1);
        input_Rotation.setWidth((new GlyphLayout(game.normalFont1, "555")).width);

        input_X = new TextField("5", inputStyle_Type);
        input_X.setHeight(game.normalFont1.getLineHeight()*2f);
        input_X.setPosition(input_Rotation.getX() + input_Rotation.getWidth(), background.getY()-input_Type.getHeight()*1.1f);
        input_X.setMaxLength(2);
        input_X.setWidth((new GlyphLayout(game.normalFont1, "1234")).width);

        input_Y = new TextField("15", inputStyle_Type);
        input_Y.setHeight(game.normalFont1.getLineHeight()*2f);
        input_Y.setPosition(input_X.getX() + input_X.getWidth(), background.getY()-input_Type.getHeight()*1.1f);
        input_Y.setMaxLength(2);
        input_Y.setWidth((new GlyphLayout(game.normalFont1, "1234")).width);


        NinePatch upPatch = new NinePatch(new Texture(Gdx.files.internal("sprite/uipack_fixed/PNG/blue_button" + "09.png")),10,10,10,10);
        NinePatchDrawable upPatchDrawable = new NinePatchDrawable(upPatch);
        NinePatch downPatch = new NinePatch(new Texture(Gdx.files.internal("sprite/uipack_fixed/PNG/blue_button" + "10.png")),10,10,10,10);
        NinePatchDrawable downPatchDrawable = new NinePatchDrawable(downPatch);
        NinePatch overPatch = new NinePatch(new Texture(Gdx.files.internal("sprite/uipack_fixed/PNG/blue_button" + "11.png")),10,10,10,10);
        NinePatchDrawable overPatchDrawable = new NinePatchDrawable(overPatch);

        TextButton.TextButtonStyle mainMenuBtnStyle = new TextButton.TextButtonStyle();
        mainMenuBtnStyle.up = upPatchDrawable;
        mainMenuBtnStyle.down = downPatchDrawable;
        mainMenuBtnStyle.over = overPatchDrawable;
        mainMenuBtnStyle.font =  game.normalFont1;

        spawnBtn = new TextButton("R", mainMenuBtnStyle);
        spawnBtn.setWidth((new GlyphLayout(game.normalFont1, "12345")).width);
        spawnBtn.setHeight(game.normalFont1.getLineHeight()*2f);
        spawnBtn.setPosition(input_Y.getX() + input_Y.getWidth(), background.getY()-input_Type.getHeight()*1.1f);
        stage.addActor(spawnBtn);
        spawnBtn.addListener(new InputListener(){

            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                rotateBlock(true);
            }

        });

        lockBtn = new TextButton("L", mainMenuBtnStyle);
        lockBtn.setWidth((new GlyphLayout(game.normalFont1, "12345")).width);
        lockBtn.setHeight(game.normalFont1.getLineHeight()*2f);
        lockBtn.setPosition(spawnBtn.getX() + spawnBtn.getWidth(), background.getY()-input_Type.getHeight()*1.1f);
        stage.addActor(lockBtn);
        lockBtn.addListener(new InputListener(){

            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                lockTetromino();
            }

        });


        stage.addActor(input_Type);
        stage.addActor(input_Rotation);
        stage.addActor(input_X);
        stage.addActor(input_Y);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


        //TICK
        timer += delta;
        if(timer >= 1) {
            timer = 0.0f;

            //DROP CURRENT BLOCK or MAKE NEW BLOCK
            if(hasActiveTetromino) {
                dropBlockSoft();
            } else {
                spawnBlockTEMP();
            }

            //VALIDATE LOCK CONDITION


        }

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

    private void spawnBlock(Integer type, Integer rotation, Texture texture, Integer spawnCol, Integer spawnRow){
        if(hasActiveTetromino){
            return;
        }
        for(Integer pieceIteration = 0; pieceIteration < 4; pieceIteration++){
            Image tetromino = new Image(texture);
            tetromino.setSize(varColWidth, varRowHeight);
            tetromino.setX(coordCol[spawnCol + PATTERN_TETROMINOES[type][rotation][pieceIteration][0]]);
            tetromino.setY(coordRow[spawnRow + PATTERN_TETROMINOES[type][rotation][pieceIteration][1]]);
            activeTetromino [pieceIteration] = tetromino;
            stage.addActor(activeTetromino[pieceIteration]);
        }
        activeType = type;
        activeRotation = rotation;
        hasActiveTetromino = true;
    }

    private void spawnBlockTEMP(){
        spawnBlock(Integer.parseInt(input_Type.getText())-1,
                Integer.parseInt(input_Rotation.getText())-1,
                TEXTURE_TETROMINOES[Integer.parseInt(input_Type.getText())-1],
                Integer.parseInt(input_X.getText()),
                Integer.parseInt(input_Y.getText()));
    }

    private void dropBlockSoft(){
        if(!hasActiveTetromino){
            return;
        }
        for(Image block : activeTetromino){
            block.setPosition(block.getX(), block.getY()-varRowHeight);
        }
    }

    private void rotateBlock(boolean clockwise){
        if(!hasActiveTetromino){
            return;
        }
        Integer[][] currentPattern = PATTERN_TETROMINOES[activeType][activeRotation];
        Integer nextRotation = activeRotation;
        if(clockwise){
            nextRotation ++;
            if (nextRotation > 3){
                nextRotation = 0;
            }
        } else {
            nextRotation --;
            if(nextRotation < 0){
                nextRotation = 3;
            }
        }

        Integer[][] nextPattern = PATTERN_TETROMINOES[activeType][nextRotation];

        for(Integer pieceIteration = 0; pieceIteration <= 3; pieceIteration ++){
            activeTetromino[pieceIteration].setPosition(
                    activeTetromino[pieceIteration].getX()+(nextPattern[pieceIteration][0] - currentPattern[pieceIteration][0])*varColWidth,
                    activeTetromino[pieceIteration].getY()+(nextPattern[pieceIteration][1] - currentPattern[pieceIteration][1])*varRowHeight);

            Gdx.app.log("X" + pieceIteration, activeTetromino[pieceIteration].getX()+(nextPattern[pieceIteration][0] - currentPattern[pieceIteration][0])*varColWidth + "");
            Gdx.app.log("Y" + pieceIteration, activeTetromino[pieceIteration].getY()+(nextPattern[pieceIteration][1] - currentPattern[pieceIteration][1])*varRowHeight + "");
        }

        activeRotation = nextRotation;
    }

    private void lockTetromino(){
        activeTetromino = new Image[4];
        hasActiveTetromino = false;
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
    }

}