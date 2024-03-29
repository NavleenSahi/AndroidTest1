package com.example.navleenkaur.androidtest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.Random;

public class GameEngine<Distance_FromWall> extends SurfaceView implements Runnable {
    private static final int CAT_STEPS = 1000;
    private final String TAG = "SPARROW";

    // game thread variables
    private Thread gameThread = null;
    private volatile boolean gameIsRunning;

    // drawing variables
    private Canvas canvas;
    private Paint paintbrush;
    private SurfaceHolder holder;

    // Screen resolution varaibles
    private int screenWidth;
    private int screenHeight;

    // VISIBLE GAME PLAY AREA
    // These variables are set in the constructor
    int VISIBLE_LEFT;
    int VISIBLE_TOP;
    int VISIBLE_RIGHT;
    int VISIBLE_BOTTOM;

    // SPRITES
    Square bullet;
    int SQUARE_WIDTH = 100;

    final int CAT_SPEED = 20;
    final int CAGE_SPEED = 20;
    final int SPARROW_SPEED = 10;

    Square enemy;

    Sprite player;
    Sprite sparrow;
    Sprite cat;
    Sprite cage;

    ArrayList<Square> bullets = new ArrayList<Square>();

    // GAME STATS
    int score = 0;
    Distance_FromWall = 10;

    public GameEngine(Context context, int screenW, int screenH) {
        super(context);

        // intialize the drawing variables
        this.holder = this.getHolder();
        this.paintbrush = new Paint();

        // set screen height and width
        this.screenWidth = screenW;
        this.screenHeight = screenH;

        this.bullet = new Square(context, 100, 600, SQUARE_WIDTH);


        // setup visible game play area variables
        this.VISIBLE_LEFT = 20;
        this.VISIBLE_TOP = 10;
        this.VISIBLE_RIGHT = this.screenWidth - 20;
        this.VISIBLE_BOTTOM = (int) (this.screenHeight * 0.8);


        // initalize sprites
        this.player = new Sprite(this.getContext(), 100, 700, R.drawable.player64);
        this.sparrow = new Sprite(this.getContext(), 500, 200, R.drawable.bird64);
        this.cat = new Sprite(this.getContext(), 1500, 700, R.drawable.cat64);
    }


    @Override
    public void run() {
        while (gameIsRunning == true) {
            updateGame();    // updating positions of stuff
            redrawSprites(); // drawing the stuff
            controlFPS();
        }
    }



    // Game Loop methods


    boolean movingLeft = true;
    boolean movingRight = true;
    final int DISTANCE_RIGHT = 50;

    public void updatePosition() {

    }

    public void updateGame() {


        //cat movement
        if(movingRight == true) {
            this.cat.setxPosition(this.cat.getxPosition() - CAT_SPEED);
        }

        else if (movingRight == false) {
            this.cat.setxPosition(this.cat.getxPosition() + CAT_SPEED);
        }

        // cat hitbox movement
         this.cat.getHitbox().left = this.cat.getxPosition();
         this.cat.getHitbox().top = this.cat.getyPosition();
         this.cat.getHitbox().right = this.cat.getxPosition() + this.cat.getImage().getWidth();
         this.cat.getHitbox().bottom = this.cat.getyPosition() + this.cat.getImage().getHeight();

        if(movingRight == true) {
            this.sparrow.setxPosition(this.sparrow.getxPosition() - SPARROW_SPEED);
        }

        else if (movingRight == false) {
            Random random = new Random();
        }

        // cat hitbox movement
        this.sparrow.getHitbox().left = this.sparrow.getxPosition();
        this.sparrow.getHitbox().top = this.sparrow.getyPosition();
        this.sparrow.getHitbox().right = this.sparrow.getxPosition() + this.sparrow.getImage().getWidth();
        this.sparrow.getHitbox().bottom = this.sparrow.getyPosition() + this.sparrow.getImage().getHeight();

        if (movingLeft == true) {
            this.cat.setxPosition(this.cat.getxPosition() - CAT_SPEED);
            this.cat.hitbox.left = this.cat.hitbox.left - CAT_SPEED;
            this.cat.hitbox.right = this.cat.hitbox.right - CAT_SPEED;
        } else {


            this.cat.setxPosition(this.cat.getxPosition() + CAT_SPEED);
            this.cat.hitbox.left = this.cat.hitbox.left + CAT_SPEED;
            this.cat.hitbox.right = this.cat.hitbox.right + CAT_SPEED;

        }
        if (this.cat.getxPosition()< this.VISIBLE_LEFT + Distance_FromWall)
        {
            movingLeft = false;
        }
        if(this.cat.getxPosition()> this.VISIBLE_RIGHT - Distance_FromWall )
        {
            movingLeft = true;
        }

    }


    public void outputVisibleArea() {
        Log.d(TAG, "DEBUG: The visible area of the screen is:");
        Log.d(TAG, "DEBUG: Maximum w,h = " + this.screenWidth +  "," + this.screenHeight);
        Log.d(TAG, "DEBUG: Visible w,h =" + VISIBLE_RIGHT + "," + VISIBLE_BOTTOM);
        Log.d(TAG, "-------------------------------------");
    }



    public void redrawSprites() {
        if (holder.getSurface().isValid()) {

            // initialize the canvas
            canvas = holder.lockCanvas();
            // --------------------------------

            // set the game's background color
            canvas.drawColor(Color.argb(255,255,255,255));


            // setup stroke style and width
            paintbrush.setStyle(Paint.Style.FILL);
            paintbrush.setStrokeWidth(8);

            // --------------------------------------------------------
            // draw boundaries of the visible space of app
            // --------------------------------------------------------
            paintbrush.setStyle(Paint.Style.STROKE);
            paintbrush.setColor(Color.argb(255, 0, 128, 0));

            canvas.drawRect(VISIBLE_LEFT, VISIBLE_TOP, VISIBLE_RIGHT, VISIBLE_BOTTOM, paintbrush);
            this.outputVisibleArea();

            // --------------------------------------------------------
            // draw player and sparrow
            // --------------------------------------------------------

            // 1. player
            canvas.drawBitmap(this.player.getImage(), this.player.getxPosition(), this.player.getyPosition(), paintbrush);

            // 2. sparrow
            canvas.drawBitmap(this.sparrow.getImage(), this.sparrow.getxPosition(), this.sparrow.getyPosition(), paintbrush);

            //3. cat
            canvas.drawBitmap(this.cat.getImage(), this.cat.getxPosition(), this.cat.getyPosition(), paintbrush);

            // 4. cage
            canvas.drawRect(1700, 150, 1500, 50, paintbrush);

            // --------------------------------------------------------
            // draw hitbox on player
            // --------------------------------------------------------
            Rect r = player.getHitbox();
            paintbrush.setStyle(Paint.Style.STROKE);
            canvas.drawRect(r, paintbrush);

            // hitbox on cat
            Rect r1 = cat.getHitbox();
            paintbrush.setStyle(Paint.Style.STROKE);
            canvas.drawRect(r1, paintbrush);

            // hitbox on sparrow
            Rect r2 = sparrow.getHitbox();
            paintbrush.setStyle(Paint.Style.STROKE);
            canvas.drawRect(r2, paintbrush);



            // --------------------------------------------------------
            // draw hitbox on player
            // --------------------------------------------------------
            paintbrush.setTextSize(60);
            paintbrush.setStrokeWidth(5);
            String screenInfo = "Screen size: (" + this.screenWidth + "," + this.screenHeight + ")";
            canvas.drawText(screenInfo, 10, 100, paintbrush);

            // --------------------------------
            holder.unlockCanvasAndPost(canvas);
        }

    }

    public void controlFPS() {
        try {
            gameThread.sleep(17);
        }
        catch (InterruptedException e) {

        }
    }


    // Deal with user input
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_UP:
                break;
            case MotionEvent.ACTION_DOWN:
                break;
        }
        return true;
    }

    // Game status - pause & resume
    public void pauseGame() {
        gameIsRunning = false;
        try {
            gameThread.join();
        }
        catch (InterruptedException e) {

        }
    }
    public void  resumeGame() {
        gameIsRunning = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

}