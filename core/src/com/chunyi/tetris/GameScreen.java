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

import java.util.Random;

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
    //TICK CONTROL VARIABLE
    private Float timer;
    private Float rotateTimer;

    //DEVELOPING STAGE ELEMENTS
    private TextField input_Type, input_Rotation, input_X, input_Y;
    private TextField.TextFieldStyle inputStyle_Type, inputStyle_Rotation, inputStyle_X, inputStyle_Y;
    private TextButton rotateRBtn,rotateLBtn, lockBtn,resetBtn,leftBtn,rightBtn,dropBtn;


    private Float varRowHeight, varColWidth;

    //Exact coordinates for each grid
    private Float[] coordRow, coordCol;
    //Holds gameBoard array idex for current active tetromino
    private Integer [][] activeTetromino = new Integer[][]{{0,0},{0,0},{0,0},{0,0}};
    //Holds characteristics for active tetromino
    private Integer activeType, activeRotation;
    private Integer nextSpawnType, nextSpawnX = 4, nextSpawnY = 18;
    //Holds image/actor for all tetrominoes on the board
    private Image [][] gameBoard = new Image[10][20];


    //FLAGS
    private Boolean hasActiveTetromino = false;
    private Boolean isGameOver = false;


    public GameScreen(final TetrisGame game) {
        this.game = game;

        initializeScene();
        initializeDevelopScene();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //TICK
        timer += delta;
        rotateTimer +=delta;
        if(timer >= 1) {
            timer = 0.0f;

            if(isGameOver){

            } else if(hasActiveTetromino) { //DROP CURRENT BLOCK or MAKE NEW BLOCK if not game over yet
                naturalDrop();
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
        if(hasActiveTetromino || isGameOver){
            return;
        }

        //Validate Gameover
        for(Integer i = 0; i < 4; i++) {
            Image tetromino = new Image(texture);



            Integer col = spawnCol + PATTERN_TETROMINOES[type][rotation][i][0];
            Integer row = spawnRow + PATTERN_TETROMINOES[type][rotation][i][1];

            //VALIDATE if the spawning slot already has a block
            if (gameBoard[col][row] != null) {
                isGameOver = true;
                Gdx.app.log("Debug:", "Game Over");
                break;
            }
        }

        if(isGameOver){
            return;
        }

        for(Integer i = 0; i < 4; i++){
            Image tetromino = new Image(texture);
            Integer col = spawnCol + PATTERN_TETROMINOES[type][rotation][i][0];
            Integer row = spawnRow + PATTERN_TETROMINOES[type][rotation][i][1];


            activeTetromino[i][0] = col;
            activeTetromino[i][1] = row;

            tetromino.setSize(varColWidth, varRowHeight);

            tetromino.setX(coordCol[col]);
            tetromino.setY(coordRow[row]);

            gameBoard[col][row] = tetromino;

            stage.addActor( gameBoard[col][row]);
        }

        activeType = type;
        activeRotation = rotation;
        hasActiveTetromino = true;

        Random random = new Random();
        nextSpawnType = random.nextInt(7) + 1;
        input_Type.setText(nextSpawnType+"");
    }

    private void spawnBlockTEMP(){
        spawnBlock(Integer.parseInt(input_Type.getText())-1,
                Integer.parseInt(input_Rotation.getText())-1,
                TEXTURE_TETROMINOES[Integer.parseInt(input_Type.getText())-1],
                Integer.parseInt(input_X.getText()),
                Integer.parseInt(input_Y.getText()));
    }

    private Image getActiveTetrominoImage(Integer pieceIteration) {
        return gameBoard[activeTetromino[pieceIteration][0]][activeTetromino[pieceIteration][1]];
    }

    private void naturalDrop(){
        if(!hasActiveTetromino){
            return;
        }

        //Validate Drop
        //Logic:
        //For every single block in the tetromino, check if there is a block below
            //Don't have, it pass and remain the canDrop flag as it is
            //Have, Match that block against all 4 blocks of the active one
                    //Does not match any of the 4, flag canDrop as false
                    //Matched any piece in the 4 and it pass, remain the canDrop flag as it is
        Boolean canDrop = true;
        for(Integer i = 0; i < 4; i ++){

            //If already at bottom row, cant drop;
            if(activeTetromino[i][1]-1 < 0){
                canDrop = false;
                break;
            }
            Image blockBelow = gameBoard[activeTetromino[i][0]][activeTetromino[i][1]-1];
            if(blockBelow != null){
                Boolean isOwnBlock = false;
                for(Integer i2 = 0; i2 < 4; i2 ++) {
                    if((activeTetromino[i][0]) == (activeTetromino[i2][0]) &&
                            (activeTetromino[i][1]-1) == (activeTetromino[i2][1])){
                        isOwnBlock = true;
                    }
                }
                if(!isOwnBlock) {
                    canDrop = false;
                    break;
                }
            } else {
                canDrop = true;
            }
            if(!canDrop){
                break;
            }
        }


        if(canDrop){
            Image[] holder = new Image[4];
            for(Integer i = 0; i < 4; i ++){
                holder[i] = getActiveTetrominoImage(i);

//                holder[i].setPosition(holder[i].getX(), coordRow[activeTetromino[i][1] - 1 + PATTERN_TETROMINOES[activeType][activeRotation][i][1]]);
                holder[i].setPosition(holder[i].getX(), coordRow[activeTetromino[i][1] - 1]);
                gameBoard[activeTetromino[i][0]][activeTetromino[i][1]] = null;

            }

            for (Integer i = 0; i < 4; i ++){

                gameBoard[activeTetromino[i][0]][activeTetromino[i][1]-1] = holder[i];
                activeTetromino[i][1] --;
            }
        } else {
            lockTetromino();
        }

    }

    private void dropBlockHard(){
        do{
            naturalDrop();
        } while(hasActiveTetromino);
    }

    private void dropBlockSoft(){

    }

    private void rotateBlock(boolean clockwise){
        if(!hasActiveTetromino){
            return;
        }

        //Reset rotation integer if it gets out of bound
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

        //Patterns
        Integer[][] currentPattern, nextPattern;
        currentPattern = PATTERN_TETROMINOES[activeType][activeRotation];
        nextPattern = PATTERN_TETROMINOES[activeType][nextRotation];



        Image [] holder = new Image[4]; //temp holder for the images
        Integer [] xOffsetHolder = new Integer[4]; //temp holder for the x grid offset, so no need calculate again when updating gameBoard and activetetromino
        Integer [] yOffsetHolder = new Integer[4]; //same as ^^ but fot y grid offset

        //Backup tetromino reference
        //Calculate Offset
        for(Integer i = 0; i < 4; i ++) {
            holder[i] = getActiveTetrominoImage(i); //Saving reference into temp holder, as it will be removed from gameBoard
            xOffsetHolder[i] = nextPattern[i][0] - currentPattern[i][0]; //Calculate offset in x
            yOffsetHolder[i] = nextPattern[i][1] - currentPattern[i][1]; //Calculate offset in y
        }


        Boolean canRotate = true;
        Integer xOffsetToRotate = 0, yOffsetToRotate = 0; //How much does the piece need to be adjusted AFTER rotation

        for(Integer i = 0; i < 4; i ++) {
            Integer newX, newY;
            newX = activeTetromino[i][0] + xOffsetHolder[i];
            newY = activeTetromino[i][1] + yOffsetHolder[i];

            //Check If out of bound
            //Out of bound alone should never deny a rotate
            if(newX < 0){
                xOffsetToRotate = newX*(-1);
            } else if(newX > 9){
                xOffsetToRotate += newX - 9;
            }

            if(newY < 0){
                yOffsetToRotate = newY*(-1);
            } else if(newY > 19){
                yOffsetToRotate += newY - 19;
            }

            //Check collision based on post-oob-check coordinate
            newX += xOffsetToRotate;
            newY += yOffsetToRotate;
            //Check If Collide into other pieces
            Image targetBlock = gameBoard[newX][newY];
            if(targetBlock != null){
                Boolean isOwnBlock = false;
                for(Integer i2 = 0; i2 < 4; i2 ++) {
                    if(newX == (activeTetromino[i2][0]) && newY == (activeTetromino[i2][1])){
                        isOwnBlock = true;
                    }
                }
                if(!isOwnBlock) {
                    canRotate = false;
                    break;
                }
            }
            if(!canRotate){
                break;
            }
        }

        if(!canRotate){
            return;
        }


        //Move image to new position
        //Remove old location in gameBoard
        for(Integer i = 0; i < 4; i ++){
            holder[i].setPosition( //Setting position
                    holder[i].getX()+xOffsetHolder[i]*varColWidth, //calculate difference for x gird, then multiply by the grid size
                    holder[i].getY()+yOffsetHolder[i]*varRowHeight); //calculate difference for y gird, then multiply by the grid size

            gameBoard[activeTetromino[i][0]][activeTetromino[i][1]] = null; //removing the reference in the old gameBoard slot
        }

        //Update gameBoard and activeTetromino
        //Necessary to do in seperate loop because, if a tetromino has a block above/below each other, moving the upper
        //one down will overwrite the lower one, and its lost.
        for(Integer i = 0; i <= 3; i ++){
            gameBoard[activeTetromino[i][0]+xOffsetHolder[i]][activeTetromino[i][1]+yOffsetHolder[i]] = holder[i];
            activeTetromino[i][0] += xOffsetHolder[i];
            activeTetromino[i][1] += yOffsetHolder[i];
        }

        activeRotation = nextRotation;
        rotateTimer = 0.0f;
    }

    private void lockTetromino(){
        Gdx.app.log("Debug", rotateTimer + "");
        if(rotateTimer < 1.0f){
            return;
        }
        activeTetromino = new Integer[][]{
                {0,0},{0,0},{0,0},{0,0}
        };
        hasActiveTetromino = false;
        Gdx.app.log("Debug:","LOCK!");
    }

    private void initializeDevelopScene(){
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

        input_X = new TextField(nextSpawnX+"", inputStyle_Type);
        input_X.setHeight(game.normalFont1.getLineHeight()*2f);
        input_X.setPosition(input_Rotation.getX() + input_Rotation.getWidth(), background.getY()-input_Type.getHeight()*1.1f);
        input_X.setMaxLength(2);
        input_X.setWidth((new GlyphLayout(game.normalFont1, "1234")).width);

        input_Y = new TextField(nextSpawnY+"", inputStyle_Type);
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

        rotateRBtn = new TextButton("R", mainMenuBtnStyle);
        rotateRBtn.setWidth((new GlyphLayout(game.normalFont1, "123")).width);
        rotateRBtn.setHeight(game.normalFont1.getLineHeight()*2f);
        rotateRBtn.setPosition(input_Y.getX() + input_Y.getWidth(), background.getY()-input_Type.getHeight()*1.1f);
        stage.addActor(rotateRBtn);
        rotateRBtn.addListener(new InputListener(){

            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                Gdx.app.log("Input", "Game Screen : Rotate button pressed");
                return true;
            }

            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                rotateBlock(true);
            }

        });

        lockBtn = new TextButton("L", mainMenuBtnStyle);
        lockBtn.setWidth((new GlyphLayout(game.normalFont1, "123")).width);
        lockBtn.setHeight(game.normalFont1.getLineHeight()*2f);
        lockBtn.setPosition(rotateRBtn.getX() + rotateRBtn.getWidth(), background.getY()-input_Type.getHeight()*1.1f);
        stage.addActor(lockBtn);
        lockBtn.addListener(new InputListener(){

            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                Gdx.app.log("Input", "Game Screen : Lock button pressed");
                return true;
            }

            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                lockTetromino();
            }

        });

        resetBtn = new TextButton("Rs", mainMenuBtnStyle);
        resetBtn.setWidth((new GlyphLayout(game.normalFont1, "123")).width);
        resetBtn.setHeight(game.normalFont1.getLineHeight()*2f);
        resetBtn.setPosition(lockBtn.getX() + lockBtn.getWidth(), background.getY()-input_Type.getHeight()*1.1f);
        stage.addActor(resetBtn);
        resetBtn.addListener(new InputListener(){

            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                Gdx.app.log("Input", "Game Screen : Reset button pressed");
                return true;
            }

            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                resetGame();
            }

        });

        dropBtn = new TextButton("D", mainMenuBtnStyle);
        dropBtn.setWidth((new GlyphLayout(game.normalFont1, "123")).width);
        dropBtn.setHeight(game.normalFont1.getLineHeight()*2f);
        dropBtn.setPosition(resetBtn.getX() + resetBtn.getWidth(), background.getY()-input_Type.getHeight()*1.1f);
        stage.addActor(dropBtn);
        dropBtn.addListener(new InputListener(){

            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                Gdx.app.log("Input", "Game Screen : Drop button pressed");
                return true;
            }

            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                dropBlockHard();
            }

        });



        stage.addActor(input_Type);
        stage.addActor(input_Rotation);
        stage.addActor(input_X);
        stage.addActor(input_Y);
    }

    private void initializeScene(){

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

        //GAMEBOARD
        for(Integer i = 0; i < 10; i ++){
            for(Integer z = 0; z < 20; z ++){
                gameBoard[i][z] = null;
            }
        }

        timer = 0.0f;
        rotateTimer = 0.0f;
    }

    private void resetGame(){
        stage.dispose();
        initializeScene();
        initializeDevelopScene();
        activeTetromino = new Integer[][]{{0,0},{0,0},{0,0},{0,0}};
        gameBoard = new Image[10][20];
        hasActiveTetromino = false;
        isGameOver = false;
    }

}