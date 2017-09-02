package com.chunyi.tetris;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
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
    private static final int [][][][] PATTERN_TETROMINOES = new int [][][][]{
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
    private Float softDropTimer;
    private Float newPieceTimer;
    private Float keyRepeatTimer;
    private Float autoRepeatTimer;

    //DEVELOPING STAGE ELEMENTS
    private TextField input_Type, input_Rotation, input_X, input_Y;
    private TextField.TextFieldStyle inputStyle_Type, inputStyle_Rotation, inputStyle_X, inputStyle_Y;
    private TextButton rotateRBtn,rotateLBtn, lockBtn,resetBtn,leftBtn,rightBtn,dropBtn;


    private Float varRowHeight, varColWidth;

    //Exact coordinates for each grid
    private Float[] coordRow, coordCol;
    //Holds gameBoard array idex for current active tetromino
    private int [][] activeTetromino = new int[][]{{0,0},{0,0},{0,0},{0,0}};
    //Holds characteristics for active tetromino
    private int activeType, activeRotation;
    private int nextSpawnType;
    //Holds image/actor for all tetrominoes on the board
    private Image [][] gameBoard = new Image[10][20];
    private Image [] ghostTetromino = null;

    //FLAGS
    private Boolean hasActiveTetromino = false;
    private Boolean isGameOver = false;
    private Boolean isSoftDropping = false;


    public GameScreen(final TetrisGame game) {
        this.game = game;

        initializeScene();
        //initializeDevelopScene();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //TICK
        timer += delta;
        rotateTimer +=delta;
        keyRepeatTimer += delta;
        softDropTimer += delta;
        newPieceTimer += delta;
        autoRepeatTimer += delta;

        inputHandler();


        //60Tick
        if(timer >= 1f) {
            timer -=1f;
            if(isGameOver){

            } else if(hasActiveTetromino) { //DROP CURRENT BLOCK or MAKE NEW BLOCK if not game over yet
                if(!isSoftDropping) {
                    if (dropTetrominoNatural()) {
                        lockTetromino(false);
                    }
                }
            }
        }

        //60 Tick but in seperate timer
        if(!hasActiveTetromino && newPieceTimer >= 0.7f && !isGameOver){
            newPieceTimer = 0.0f;
            spawnTetromino();
        }

        //Real time to follow player input
        handleGhostTetromino();

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



    private void spawnTetromino(){
        if(hasActiveTetromino || isGameOver){
            return;
        }

        int spawnCol, spawnRow = 18;
        if(randNextSpawnType() == 0){
            spawnCol = 3;
        } else {
            spawnCol = 4;
        }

        //Validate Gameover
        for(int i = 0; i < 4; i++) {
            Image tetromino = new Image(TEXTURE_TETROMINOES[nextSpawnType]);
            int col = spawnCol + PATTERN_TETROMINOES[nextSpawnType][1][i][0];
            int row = spawnRow + PATTERN_TETROMINOES[nextSpawnType][1][i][1];

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

        for(int i = 0; i < 4; i++){
            Image tetromino = new Image(TEXTURE_TETROMINOES[nextSpawnType]);
            int col = spawnCol + PATTERN_TETROMINOES[nextSpawnType][1][i][0];
            int row = spawnRow + PATTERN_TETROMINOES[nextSpawnType][1][i][1];


            activeTetromino[i][0] = col;
            activeTetromino[i][1] = row;

            tetromino.setSize(varColWidth, varRowHeight);

            tetromino.setX(coordCol[col]);
            tetromino.setY(coordRow[row]);

            gameBoard[col][row] = tetromino;

            stage.addActor( gameBoard[col][row]);
        }

        activeType = nextSpawnType;
        activeRotation = 1;
        hasActiveTetromino = true;
    }

    private Integer randNextSpawnType(){
        Random random = new Random();
        nextSpawnType = random.nextInt(7);
        return nextSpawnType;
    }

    private Image getActiveTetrominoImage(int pieceIteration) {
        return gameBoard[activeTetromino[pieceIteration][0]][activeTetromino[pieceIteration][1]];
    }

    //return true if lockTetromino should be called after
    private Boolean dropTetrominoNatural(){
        if(!hasActiveTetromino || isGameOver){
            return false;
        }

        //Validate Drop
        //Logic:
        //For every single block in the tetromino, check if there is a block below
            //Don't have, it pass and remain the canDrop flag as it is
            //Have, Match that block against all 4 blocks of the active one
                    //Does not match any of the 4, flag canDrop as false
                    //Matched any piece in the 4 and it pass, remain the canDrop flag as it is
        Boolean canDrop = true;
        for(int i = 0; i < 4; i ++){

            //If already at bottom row, cant drop;
            if(activeTetromino[i][1]-1 < 0){
                canDrop = false;
                break;
            }
            Image blockBelow = gameBoard[activeTetromino[i][0]][activeTetromino[i][1]-1];
            if(blockBelow != null){
                Boolean isOwnBlock = false;
                for(int i2 = 0; i2 < 4; i2 ++) {
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
            for(int i = 0; i < 4; i ++){
                holder[i] = getActiveTetrominoImage(i);
                holder[i].setPosition(holder[i].getX(), coordRow[activeTetromino[i][1] - 1]);
                gameBoard[activeTetromino[i][0]][activeTetromino[i][1]] = null;

            }

            for (int i = 0; i < 4; i ++){

                gameBoard[activeTetromino[i][0]][activeTetromino[i][1]-1] = holder[i];
                activeTetromino[i][1] --;
            }
            return false;
        } else {
            return true;
        }

    }

    private void dropTetrominoHard(){
        do{
            if(dropTetrominoNatural()){
                lockTetromino(true);
            };
        } while(hasActiveTetromino);

    }

    private void dropTetrominoSoft(){
//        do{
//            dropTetrominoNatural();
//        } while(hasActiveTetromino);

        if(softDropTimer > 0.03f) {

            //Gdx.app.log("softdrop", softDropTimer + "");
            softDropTimer -= 0.03f;
            if(dropTetrominoNatural()){
                lockTetromino(false);
            };
        }
    }


    private void handleGhostTetromino(){
        if(!hasActiveTetromino){
            if(ghostTetromino!= null){
                for(int i = 0; i < 4; i++){
                    ghostTetromino[i].remove();
                }
                ghostTetromino = null;
            }
            return;
        }

        if(ghostTetromino == null) {
            ghostTetromino = new Image[4];
            for (int i = 0; i < 4; i++) {
                ghostTetromino[i] = new Image(new Texture(Gdx.files.internal("sprite/sokobanpack/PNG/Default size/Environment/environment_06.png")));
                ghostTetromino[i].setSize(varColWidth, varRowHeight);
                ghostTetromino[i].setX(getActiveTetrominoImage(i).getX());
                ghostTetromino[i].setY(getActiveTetrominoImage(i).getY());
                stage.addActor(ghostTetromino[i]);
            }
        }
        Image blockBelow;
        boolean canDrop = true;
        int yOffsetGhost = 0;
        for(int y = 19; y >= 0; y --){

            for(int i = 0; i < 4; i ++){
                if(activeTetromino[i][1] + yOffsetGhost - 1 < 0){
                    canDrop = false;
                    break;
                }
                blockBelow = gameBoard[activeTetromino[i][0]][activeTetromino[i][1] + yOffsetGhost - 1];
                if(blockBelow != null){

                    Boolean isOwnBlock = false;
                    for(int i2 = 0; i2 < 4; i2 ++) {
                        if((activeTetromino[i][0]) == (activeTetromino[i2][0]) &&
                                (activeTetromino[i][1]-1) == (activeTetromino[i2][1])){
                            isOwnBlock = true;
                        }
                    }

                    if(!isOwnBlock) {
                        canDrop = false;
                        break;
                    }
                }
            }

            if(canDrop){
                yOffsetGhost --;
            }
        }
        for(int i = 0; i < 4; i ++){
            ghostTetromino[i].setX(getActiveTetrominoImage(i).getX());
            ghostTetromino[i].setY(coordRow[activeTetromino[i][1] + yOffsetGhost]);
        }


    }

    private void rotateTetromino(boolean clockwise){
        if(!hasActiveTetromino || isGameOver){
            return;
        }

        //Reset rotation integer if it gets out of bound
        int nextRotation = activeRotation;
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
        int[][] currentPattern, nextPattern;
        currentPattern = PATTERN_TETROMINOES[activeType][activeRotation];
        nextPattern = PATTERN_TETROMINOES[activeType][nextRotation];



        Image [] holder = new Image[4]; //temp holder for the images
        int [] xOffsetHolder = new int[4]; //temp holder for the x grid offset, so no need calculate again when updating gameBoard and activetetromino
        int [] yOffsetHolder = new int[4]; //same as ^^ but fot y grid offset

        //Backup tetromino reference
        //Calculate Offset
        for(int i = 0; i < 4; i ++) {
            holder[i] = getActiveTetrominoImage(i); //Saving reference into temp holder, as it will be removed from gameBoard
            xOffsetHolder[i] = nextPattern[i][0] - currentPattern[i][0]; //Calculate offset in x
            yOffsetHolder[i] = nextPattern[i][1] - currentPattern[i][1]; //Calculate offset in y
        }


        Boolean canRotate = true;
        int xOffsetToRotate = 0, yOffsetToRotate = 0; //How much does the piece need to be adjusted AFTER rotation

        for(int i = 0; i < 4; i ++) {
            int newX, newY;
            newX = activeTetromino[i][0] + xOffsetHolder[i];
            newY = activeTetromino[i][1] + yOffsetHolder[i];



            //Check If out of bound
            //Out of bound alone should never deny a rotate
            if (newX < 0) {
                if(xOffsetToRotate < newX * (-1)){
                    xOffsetToRotate = newX *(-1);
                }
            } else if (newX > 9) {
                if(xOffsetToRotate > 9 - newX) {
                    xOffsetToRotate = 9 - newX ;
                }
            }

            if (newY < 0) {
                if(yOffsetToRotate < newY * (-1)){
                    yOffsetToRotate = newY *(-1);
                }
            } else if (newY > 19) {
                if(yOffsetToRotate > 19 - newY) {
                    yOffsetToRotate = 19 - newY;
                }
            }
        }

        for(int i = 0; i < 4; i ++) {
            int newX, newY;
            newX = activeTetromino[i][0] + xOffsetHolder[i];
            newY = activeTetromino[i][1] + yOffsetHolder[i];

            //Check collision based on post-oob-check coordinate
            newX += xOffsetToRotate;
            newY += yOffsetToRotate;


            //Check If Collide into other pieces
            Image targetBlock = gameBoard[newX][newY];
            if(targetBlock != null){
                Boolean isOwnBlock = false;
                for(int i2 = 0; i2 < 4; i2 ++) {
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
        for(int i = 0; i < 4; i ++){

            xOffsetHolder[i] += xOffsetToRotate;
            yOffsetHolder[i] += yOffsetToRotate;

            holder[i].setPosition( //Setting position
                    holder[i].getX()+xOffsetHolder[i]*varColWidth, //calculate difference for x gird, then multiply by the grid size
                    holder[i].getY()+yOffsetHolder[i]*varRowHeight); //calculate difference for y gird, then multiply by the grid size

            gameBoard[activeTetromino[i][0]][activeTetromino[i][1]] = null; //removing the reference in the old gameBoard slot
        }

        //Update gameBoard and activeTetromino
        //Necessary to do in seperate loop because, if a tetromino has a block above/below each other, moving the upper
        //one down will overwrite the lower one, and its lost.
        for(int i = 0; i < 4; i ++){
            gameBoard[activeTetromino[i][0]+xOffsetHolder[i]]
                    [activeTetromino[i][1]+yOffsetHolder[i]] = holder[i];
            activeTetromino[i][0] += xOffsetHolder[i];
            activeTetromino[i][1] += yOffsetHolder[i];
        }

        activeRotation = nextRotation;
        rotateTimer = 0.0f;
    }

    private void moveTetromino(Boolean left){
        if(!hasActiveTetromino || isGameOver){
            return;
        }

        Boolean canMove = true;

        //OOB CHECK
        if(left){
            for(int i = 0; i < 4; i ++){
                if(activeTetromino[i][0]<=0){
                    canMove = false;
                    break;
                }
            }
        } else {
            for(int i = 0; i < 4; i ++){
                if(activeTetromino[i][0]>=9){
                    canMove = false;
                    break;
                }
            }
        }

        if(!canMove){
            return;
        }

        //COLLISION CHECK
        Image holder[] = new Image[4];
        int nowX,nowY,newX;
        int xOffset;
        if(left){
            xOffset = -1;
        } else {
            xOffset = 1;
        }

        for(int i = 0; i < 4; i ++){
            holder[i] = getActiveTetrominoImage(i);
            nowX = activeTetromino[i][0];
            nowY = activeTetromino[i][1];
            newX = nowX + xOffset;

            Image targetBlock = gameBoard[newX][nowY];
            //If destination is occupied
            if(targetBlock!=null){
                //verify if occupied slot belongs to active tetromino
                boolean isOwnBlock = false;
                for(int i2 = 0; i2 < 4; i2++){
                    if(newX == activeTetromino[i2][0] && nowY == activeTetromino[i2][1]){
                        isOwnBlock = true;
                        break;
                    }
                }
                if(!isOwnBlock){
                    canMove = false;
                    break;
                }
            }
        }

        if(canMove){
            for(int i = 0; i < 4; i ++){
                nowX = activeTetromino[i][0];
                nowY = activeTetromino[i][1];
                newX = nowX + xOffset;
                holder[i].setPosition(coordCol[newX], holder[i].getY());
                gameBoard[nowX][nowY] = null;
            }
            for (int i = 0; i < 4; i ++){
                nowX = activeTetromino[i][0];
                nowY = activeTetromino[i][1];
                newX = nowX + xOffset;
                gameBoard[newX][nowY] = holder[i];
                activeTetromino[i][0] += xOffset;
            }
        }

        //UPDATE gameBoard AND activeTetromino


    }

    private void lockTetromino(Boolean force){
        if(rotateTimer < 1.0f && force == false){
            return;
        }
        activeTetromino = new int[][]{
                {0,0},{0,0},{0,0},{0,0}
        };
        hasActiveTetromino = false;
        newPieceTimer = 0.0f;
        //VALIDATE Line Clear
        validateLineClear();
        Gdx.app.log("Debug:","LOCK!");
    }


//    private void initializeDevelopScene(){
//        NinePatch btnPatch = new NinePatch(new Texture(Gdx.files.internal("sprite/uipack_fixed/PNG/grey_button06.png")),10,10,10,10);
//
//        inputStyle_Type = new TextField.TextFieldStyle(game.normalFont1, Color.BLACK,
//                new Image(new Texture(Gdx.files.internal("sprite/uipack_fixed/PNG/blue_tick.png"))).getDrawable(),
//                new Image(new Texture(Gdx.files.internal("sprite/puzzlepack/png/selectorA.png"))).getDrawable(),
//                new NinePatchDrawable(btnPatch));
//
//        input_Type = new TextField("1", inputStyle_Type);
//        input_Type.setHeight(game.normalFont1.getLineHeight()*2f);
//        input_Type.setPosition(background.getX(), background.getY()-input_Type.getHeight()*1.1f);
//        input_Type.setMaxLength(1);
//        input_Type.setWidth((new GlyphLayout(game.normalFont1, "555")).width);
//
//        input_Rotation = new TextField("1", inputStyle_Type);
//        input_Rotation.setHeight(game.normalFont1.getLineHeight()*2f);
//        input_Rotation.setPosition(input_Type.getX() + input_Type.getWidth(), background.getY()-input_Type.getHeight()*1.1f);
//        input_Rotation.setMaxLength(1);
//        input_Rotation.setWidth((new GlyphLayout(game.normalFont1, "555")).width);
//
//        input_X = new TextField(nextSpawnX+"", inputStyle_Type);
//        input_X.setHeight(game.normalFont1.getLineHeight()*2f);
//        input_X.setPosition(input_Rotation.getX() + input_Rotation.getWidth(), background.getY()-input_Type.getHeight()*1.1f);
//        input_X.setMaxLength(2);
//        input_X.setWidth((new GlyphLayout(game.normalFont1, "1234")).width);
//
//        input_Y = new TextField(nextSpawnY+"", inputStyle_Type);
//        input_Y.setHeight(game.normalFont1.getLineHeight()*2f);
//        input_Y.setPosition(input_X.getX() + input_X.getWidth(), background.getY()-input_Type.getHeight()*1.1f);
//        input_Y.setMaxLength(2);
//        input_Y.setWidth((new GlyphLayout(game.normalFont1, "1234")).width);
//
//
//        NinePatch upPatch = new NinePatch(new Texture(Gdx.files.internal("sprite/uipack_fixed/PNG/blue_button" + "09.png")),10,10,10,10);
//        NinePatchDrawable upPatchDrawable = new NinePatchDrawable(upPatch);
//        NinePatch downPatch = new NinePatch(new Texture(Gdx.files.internal("sprite/uipack_fixed/PNG/blue_button" + "10.png")),10,10,10,10);
//        NinePatchDrawable downPatchDrawable = new NinePatchDrawable(downPatch);
//        NinePatch overPatch = new NinePatch(new Texture(Gdx.files.internal("sprite/uipack_fixed/PNG/blue_button" + "11.png")),10,10,10,10);
//        NinePatchDrawable overPatchDrawable = new NinePatchDrawable(overPatch);
//
//        TextButton.TextButtonStyle mainMenuBtnStyle = new TextButton.TextButtonStyle();
//        mainMenuBtnStyle.up = upPatchDrawable;
//        mainMenuBtnStyle.down = downPatchDrawable;
//        mainMenuBtnStyle.over = overPatchDrawable;
//        mainMenuBtnStyle.font =  game.normalFont1;
//
//        rotateRBtn = new TextButton("R", mainMenuBtnStyle);
//        rotateRBtn.setWidth((new GlyphLayout(game.normalFont1, "123")).width);
//        rotateRBtn.setHeight(game.normalFont1.getLineHeight()*2f);
//        rotateRBtn.setPosition(input_Y.getX() + input_Y.getWidth(), background.getY()-input_Type.getHeight()*1.1f);
//        stage.addActor(rotateRBtn);
//        rotateRBtn.addListener(new InputListener(){
//
//            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
//                Gdx.app.log("Input", "Game Screen : Rotate button pressed");
//                return true;
//            }
//
//            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
//                //rotateTetromino(true);
//            }
//
//        });
//
//        lockBtn = new TextButton("L", mainMenuBtnStyle);
//        lockBtn.setWidth((new GlyphLayout(game.normalFont1, "123")).width);
//        lockBtn.setHeight(game.normalFont1.getLineHeight()*2f);
//        lockBtn.setPosition(rotateRBtn.getX() + rotateRBtn.getWidth(), background.getY()-input_Type.getHeight()*1.1f);
//        stage.addActor(lockBtn);
//        lockBtn.addListener(new InputListener(){
//
//            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
//                Gdx.app.log("Input", "Game Screen : Lock button pressed");
//                return true;
//            }
//
//            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
//                lockTetromino(true);
//            }
//
//        });
//
//        resetBtn = new TextButton("Rs", mainMenuBtnStyle);
//        resetBtn.setWidth((new GlyphLayout(game.normalFont1, "123")).width);
//        resetBtn.setHeight(game.normalFont1.getLineHeight()*2f);
//        resetBtn.setPosition(lockBtn.getX() + lockBtn.getWidth(), background.getY()-input_Type.getHeight()*1.1f);
//        stage.addActor(resetBtn);
//        resetBtn.addListener(new InputListener(){
//
//            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
//                Gdx.app.log("Input", "Game Screen : Reset button pressed");
//                return true;
//            }
//
//            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
//                resetGame();
//            }
//
//        });
//
//        dropBtn = new TextButton("D", mainMenuBtnStyle);
//        dropBtn.setWidth((new GlyphLayout(game.normalFont1, "123")).width);
//        dropBtn.setHeight(game.normalFont1.getLineHeight()*2f);
//        dropBtn.setPosition(resetBtn.getX() + resetBtn.getWidth(), background.getY()-input_Type.getHeight()*1.1f);
//        stage.addActor(dropBtn);
//        dropBtn.addListener(new InputListener(){
//
//            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
//                Gdx.app.log("Input", "Game Screen : Drop button pressed");
//                return true;
//            }
//
//            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
//                //dropTetrominoHard();
//            }
//
//        });
//
//        leftBtn = new TextButton("l", mainMenuBtnStyle);
//        leftBtn.setWidth((new GlyphLayout(game.normalFont1, "123")).width);
//        leftBtn.setHeight(game.normalFont1.getLineHeight()*2f);
//        leftBtn.setPosition(dropBtn.getX() + dropBtn.getWidth(), background.getY()-input_Type.getHeight()*1.1f);
//        stage.addActor(leftBtn);
//        leftBtn.addListener(new InputListener(){
//
//            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
//                Gdx.app.log("Input", "Game Screen : Left button pressed");
//                return true;
//            }
//
//            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
//                //moveTetromino(true);
//            }
//
//        });
//
//        rightBtn = new TextButton("r", mainMenuBtnStyle);
//        rightBtn.setWidth((new GlyphLayout(game.normalFont1, "123")).width);
//        rightBtn.setHeight(game.normalFont1.getLineHeight()*2f);
//        rightBtn.setPosition(leftBtn.getX() + leftBtn.getWidth(), background.getY()-input_Type.getHeight()*1.1f);
//        stage.addActor(rightBtn);
//        rightBtn.addListener(new InputListener(){
//
//            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
//                Gdx.app.log("Input", "Game Screen : Right button pressed");
//                return true;
//            }
//
//            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
//                //moveTetromino(false);
//            }
//
//        });
//
//
//
//        stage.addActor(input_Type);
//        stage.addActor(input_Rotation);
//        stage.addActor(input_X);
//        stage.addActor(input_Y);
//    }

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
        for(int i = 0; i < 20; i ++){
            coordRow[i] = startRow;
            startRow += varRowHeight;
        }

        //COL PREPERATION
        coordCol = new Float[10];
        Float startCol = (background.getX() + background.getWidth()*0.02f);
        varColWidth = (background.getWidth()-background.getWidth()*0.04f)/10;
        for(int i = 0; i < 10; i ++){
            coordCol[i] = startCol;
            startCol += varColWidth;
        }

        //GAMEBOARD
        for(int i = 0; i < 10; i ++){
            for(int i2 = 0; i2 < 20; i2 ++){
                gameBoard[i][i2] = null;
            }
        }

        timer = 0.0f;
        rotateTimer = 0.0f;
        keyRepeatTimer = 0.0f;
        softDropTimer = 0.0f;
        newPieceTimer = 0.0f;
        autoRepeatTimer = 0.0f;
    }

    private void resetGame(){
        stage.dispose();
        initializeScene();
        //initializeDevelopScene();
        activeTetromino = new int[][]{{0,0},{0,0},{0,0},{0,0}};
        gameBoard = new Image[10][20];
        hasActiveTetromino = false;
        isGameOver = false;
    }

    private void inputHandler() {

        if (Gdx.input.isKeyJustPressed(Keys.LEFT) && hasActiveTetromino && !isGameOver) {
            //Gdx.app.log("Input - Keyboard : ", "Left arrow key");

            moveTetromino(true);
            keyRepeatTimer = 0.0f;
            //return;

        }

        if (Gdx.input.isKeyJustPressed(Keys.RIGHT) && hasActiveTetromino && !isGameOver) {
            //Gdx.app.log("Input - Keyboard : ", "Right arrow key");

            moveTetromino(false);
            keyRepeatTimer = 0.0f;
            //return;

        }

        if (Gdx.input.isKeyPressed(Keys.LEFT) && hasActiveTetromino && !isGameOver) {
            //Gdx.app.log("Input - Keyboard : ", "Left arrow key");
            if((keyRepeatTimer > 0.5f || isSoftDropping) && autoRepeatTimer > 0.03f)  {
                autoRepeatTimer = 0.0f;
                moveTetromino(true);
            }
            //return;

        } else if (Gdx.input.isKeyPressed(Keys.RIGHT) && hasActiveTetromino && !isGameOver) {
            //Gdx.app.log("Input - Keyboard : ", "Right arrow key");

            if((keyRepeatTimer > 0.5f || isSoftDropping) && autoRepeatTimer > 0.03f)  {
                autoRepeatTimer = 0.0f;
                moveTetromino(false);
            }
            //return;

        } else {
            autoRepeatTimer = 0.0f;
            keyRepeatTimer = 0.0f;
        }

        if (Gdx.input.isKeyJustPressed(Keys.UP) && hasActiveTetromino && !isGameOver) {
            //Gdx.app.log("Input - Keyboard : ", "Up arrow key");
            rotateTetromino(true);
            //return;
        }

        if (Gdx.input.isKeyPressed(Keys.DOWN) && hasActiveTetromino && !isGameOver) {
            //Gdx.app.log("Input - Keyboard : ", "Down arrow key");
            if(!isSoftDropping) {
                softDropTimer = 0.0f;
                isSoftDropping = true;
            }
            dropTetrominoSoft();
            //return;
        } else{
            isSoftDropping = false;
        }


        if (Gdx.input.isKeyJustPressed(Keys.SPACE) && hasActiveTetromino && !isGameOver) {
            //Gdx.app.log("Input - Keyboard : ", "Space bar key");
            dropTetrominoHard();
            //return;
        }

//        boolean touch0 = Gdx.input.isTouched(0);
//        boolean touch1 = Gdx.input.isTouched(1);
//        boolean right = (touch0 && (x0 > 80 && x0 < 128)) || (touch1 && (x1 > 80 && x1 < 128));
//        boolean down = (touch0 && (y0 < 60)) || (touch1 && (y1 < 60));
//        boolean up = (touch0 && (y0 > 80 && x0 < 128)) || (touch1 && (y1 > 80 && y1 < 128));
//
//        if (state == CONTROLLED) {
//            if (Gdx.input.isKeyPressed(Keys.A)) {
//                accel.x = -ACCELERATION;
//            } else if (Gdx.input.isKeyPressed(Keys.D) || right) {
//                accel.x = ACCELERATION;
//            } else {
//                accel.x = 0;
//            }
//
//            if (Gdx.input.isKeyPressed(Keys.W) || up) {
//                accel.y = ACCELERATION;
//            } else if (Gdx.input.isKeyPressed(Keys.S) || down) {
//                accel.y = -ACCELERATION;
//            } else {
//                accel.y = 0;
//            }
//
//            if (touch0) {
//                if (dpadRect.contains(x0, y0)) {
//                    float x = (x0 - 64) / 64;
//                    float y = (y0 - 64) / 64;
//                    float len = (float)Math.sqrt(x * x + y * y);
//                    if (len != 0) {
//                        x /= len;
//                        y /= len;
//                    } else {
//                        x = 0;
//                        y = 0;
//                    }
//                    vel.x = x * MAX_VELOCITY;
//                    vel.y = y * MAX_VELOCITY;
//                } else {
//                    accel.x = 0;
//                    accel.y = 0;
//                }
//           }
    }

    private void validateLineClear(){

        boolean [] isLineCleared = new boolean [20];
        int linesCleared = 0;
        //CLEARING
        for(int y = 0; y < 20; y ++){
            boolean isLineFull = true;
            for(int x = 0; x < 10; x ++){
                if(gameBoard[x][y] == null){
                    isLineFull = false;
                    break;
                }
            }
            if(isLineFull){
                for(int x = 0; x < 10; x ++) {
                    gameBoard[x][y].remove();
                    gameBoard[x][y] = null;
                }
                isLineCleared[y] = true;
            } else {
                isLineCleared[y] = false;
            }
        }

        //DROPPING
        for(int y = 0; y < 20; y ++){
            if(isLineCleared[y] || linesCleared > 0){
                if(isLineCleared[y]){
                    linesCleared ++;
                } else {
                    for(int x = 0; x < 10; x ++){
                        if(gameBoard[x][y] != null){
                            gameBoard[x][y].setY (coordRow[y-linesCleared]);
                            gameBoard[x][y-linesCleared] = gameBoard[x][y];
                            gameBoard[x][y] = null;
                        }
                    }
                }

            }
        }

    }

}