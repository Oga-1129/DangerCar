package com.example.ogatafutoshikawa.dangercar;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.example.ogatafutoshikawa.gles.GLES;
import com.example.ogatafutoshikawa.gles.Graphics;
import com.example.ogatafutoshikawa.gles.ObjLoader;
import com.example.ogatafutoshikawa.gles.Object3D;
import com.example.ogatafutoshikawa.gles.Texture;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class GLRenderer extends AppCompatActivity
        implements GLSurfaceView.Renderer, View.OnTouchListener{

    private float aspect;                         //アスペクト比
    private final static int
        TITLE = 0,                                //タイトル
        PLAY  = 1;                                //プレイ画面

    private int scene = TITLE;                    //最初の画面
    private int init  = TITLE;                    //最初の画面の初期化

    private float car_x;                          //車のx座標
    private float car_y;                          //車のy座標
    private float car_z;                          //車のz座標

    private float c_x;                            //カメラの視点x座標
    private float c_y;                            //カメラの視点y座標
    private float c_z;                            //カメラの視点z座標

    private float c_cx;                           //カメラの焦点x座標
    private float c_cy;                           //カメラの焦点y座標
    private float c_cz;                           //カメラの焦点z座標

    private float save_x;                         //ボールのx方向に対するベクトル
    private float save_z;                         //ボールのz方向に対するベクトル

    private float speed_ac;                       //加速度

    private int screenH;                          //画面の縦幅
    private int screenW;                          //画面の横幅

    private int controller_x;                     //コントローラーの座標x座標
    private int controller_y;                     //コントローラーの座標y座標
    private int center_0;                         //コントローラーの中心座標
    private int angle;                            //ハンドルの回転角度

    private float down;                           //道路外に出た後の落下
    private int time;                             //落下時間計算用
    private int game_end;                         //ゲームの状態

    //モデル
    private Object3D load1    = new Object3D();   //道路1のモデル
    private Object3D load2    = new Object3D();   //道路2のモデル
    private Object3D load3    = new Object3D();   //道路3のモデル
    private Object3D load4    = new Object3D();   //道路4のモデル
    private Object3D load5    = new Object3D();   //道路5のモデル
    private Object3D load6    = new Object3D();   //道路6のモデル
    private Object3D load7    = new Object3D();   //道路7のモデル
    private Object3D load8    = new Object3D();   //道路8のモデル
    private Object3D load9    = new Object3D();   //道路9のモデル
    private Object3D load10   = new Object3D();   //道路10のモデル
    private Object3D goal     = new Object3D();   //ゴールのモデル

    private Object3D car      = new Object3D();   //車のモデル
    private Object3D gimmick1 = new Object3D();   //コンテナのモデル
    private Object3D gimmick2 = new Object3D();   //岩のモデル

    private Graphics g;
    private boolean game_over;                    //ゲームオーバーしているかの判定
    private boolean Play;                         //ゲーム開始の判定

    private Texture brake;                        //ブレーキアイコンのテクスチャ
    private Texture pedal;                        //ペダルアイコンのテクスチャ
    private Texture handle;                       //ハンドルアイコンのテクスチャ
    private Texture hp_green_ber;                 //HPの緑バーのテクスチャ
    private Texture hp_red_ber;                   //HPの赤バーのテクスチャ
    private Texture heat;                         //ハートアイコンのテクスチャ
    private Texture br_green_ber;                 //ブレーキゲージの緑バーのテクスチャ
    private Texture br_red_ber;                   //ブレーキゲージの赤バーのテクスチャ
    private Texture meter;                        //ハンドルのメーターのテクスチャ
    private Texture clear;                        //「CLEAR」のテクスチャ
    private Texture crash;                        //「CRASH」のテクスチャ
    private Texture outside;                      //「OUTSIDE」のテクスチャ
    private Texture time_up;                      //「TIME_UP」のテクスチャ

    private int dmg;                              //車のダメージ
    private int brake_gage;                       //ブレーキゲージの消費量
    private boolean brake_on;                     //ブレーキ判定
    private float speed_down;                     //ブレーキによるスピードの低下

    private int load_count;
    private boolean goal_pop;                     //ゴールの生成判定
    private boolean gimmick_pop;                  //ギミックの生成判定
    private float speed;                          //車の素の速度
    private int car_angle;                        //車の角度

    private MediaPlayer mp;

    //コンストラクタ
    GLRenderer(Context context) {
        GLES.context=context;
    }

    //サーフェイス生成時に呼ばれる
    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        //プログラムの生成
        GLES.makeProgram();

        //デプステストと光源の有効化
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glUniform1i(GLES.useLightHandle,1);

        //光源色の指定
        GLES20.glUniform4f(GLES.lightAmbientHandle,0.2f,0.2f,0.2f,1.0f);
        GLES20.glUniform4f(GLES.lightDiffuseHandle,0.7f,0.7f,0.7f,1.0f);
        GLES20.glUniform4f(GLES.lightSpecularHandle,0.9f,0.9f,0.9f,1.0f);

        //モデルデータの読み込み(2)
        try {
            load1.figure    = ObjLoader.load("load.obj");
            load2.figure    = ObjLoader.load("load.obj");
            load3.figure    = ObjLoader.load("load.obj");
            load4.figure    = ObjLoader.load("load.obj");
            load5.figure    = ObjLoader.load("load.obj");
            load6.figure    = ObjLoader.load("load.obj");
            load7.figure    = ObjLoader.load("load.obj");
            load8.figure    = ObjLoader.load("load.obj");
            load9.figure    = ObjLoader.load("load.obj");
            load10.figure   = ObjLoader.load("load.obj");
            goal.figure     = ObjLoader.load("goal.obj");

            car.figure      = ObjLoader.load("car.obj");
            gimmick1.figure = ObjLoader.load("wood.obj");
            gimmick2.figure = ObjLoader.load("gimmick_car.obj");

            brake           = Texture.createTextureFromAsset("brake.png");
            pedal           = Texture.createTextureFromAsset("pedal.png");
            handle          = Texture.createTextureFromAsset("handle.png");
            clear           = Texture.createTextureFromAsset("clear.png");
            crash           = Texture.createTextureFromAsset("crash.png");
            outside         = Texture.createTextureFromAsset("outside.png");
            time_up         = Texture.createTextureFromAsset("time_up.png");
            hp_green_ber    = Texture.createTextureFromAsset("green_ber.png");
            hp_red_ber      = Texture.createTextureFromAsset("red_ber.png");
            heat            = Texture.createTextureFromAsset("heat.png");
            br_green_ber    = Texture.createTextureFromAsset("green_ber.png");
            br_red_ber      = Texture.createTextureFromAsset("red_ber.png");
            meter           = Texture.createTextureFromAsset("meter.png");

        } catch (Exception e) {
            android.util.Log.e("debug",e.toString());
            for (StackTraceElement ste:e.getStackTrace()) {
                android.util.Log.e("debug","    "+ste);
            }
        }
    }

    //画面サイズ変更時に呼ばれる
    @Override
    public void onSurfaceChanged(GL10 gl10,int w,int h) {
        //ビューポート変換
        GLES20.glViewport(0,0,w,h);
        aspect  = (float)w/(float)h;
        screenH = h;
        screenW = w;

        g = new Graphics(w,h);
        onTick();
    }

    //毎フレーム描画時に呼ばれる
    @Override
    public void onDrawFrame(GL10 gl10) {
        //画面のクリア
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT |
                GLES20.GL_DEPTH_BUFFER_BIT);

        //デプステストと光源の有効化
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glUniform1i(GLES.useLightHandle, 1);

        //射影変換
        Matrix.setIdentityM(GLES.pMatrix, 0);
        GLES.gluPerspective(GLES.pMatrix,
                45.0f,          //Y方向の画角
                aspect,               //アスペクト比
                0.01f,           //ニアクリップ
                300.0f);          //ファークリップ

        //光源位置の指定
        GLES20.glUniform4f(GLES.lightPosHandle, 5.0f, 5.0f, 5.0f, 1.0f);

        //ビュー変換
        Matrix.setIdentityM(GLES.mMatrix, 0);
        GLES.gluLookAt(GLES.mMatrix,
                c_x, c_y, c_z,                  //カメラの視点
                c_cx, c_cy, c_cz,               //カメラの焦点
                0.0f, 1.0f, 0.0f);//カメラの上方向

        //モデル変換
        Matrix.rotateM(GLES.mMatrix, 0, 0, 0, 1, 0);

        //道路作成
        make_load();

        //ギミックの位置移動
        set_gimmick(gimmick1);
        set_gimmick(gimmick2);

        //ギミックの生成
        if(gimmick_pop) {
            gimmick1.draw(1);
            gimmick2.draw(1);
        }

        //車の描画
        car.draw(1);
        car.position.set(car_x,car_y,car_z);

        if(car.position.x > -10.5 && car.position.x < 8.0) {
            if (scene == PLAY) {
                if(game_end < 1) {
                    if (Play) {
                        if(brake_gage < 400) {
                            if (brake_on) {
                                if (speed_down <= speed_ac) {
                                    speed_down += 0.005f;
                                }
                                brake_gage++;
                            }
                        }else{
                            brake_on = false;
                        }
                        if(!brake_on) {
                            if (brake_gage > 0) {
                                brake_gage--;
                            }
                            if (speed_down > 0.0f) {
                                speed_down -= 0.005f;
                            }
                        }
                        move_z(speed_down, 0);
                    }
                }
                if(game_end == -1) {
                    move_x(angle);
                }

                if(dmg == 400){
                    game_end = 2;
                    game_over = true;
                }

                if(car_z + 5.0f <= goal.position.z && goal_pop){
                    game_end = 0;
                }
            }
        }else{
            if(scene == PLAY) {
                speed = 0;
                move_z(0,1);
                game_end = 1;
                time++;
                down += time * 0.098f;
                car.position.y -= down;
                game_over = true;
            }
        }

        g.init();

        //プレイ画面描画
        if(scene == PLAY){
            g.drawImage(pedal,50,screenH - 250,150,200);

            g.drawImage(heat,45,10,70,70);
            g.drawImage(hp_red_ber,130,22,400,50);
            g.drawImage(hp_green_ber,130,22,400 - dmg,50);

            g.drawImage(brake,30,70,100,100);
            g.drawImage(br_red_ber,130,95,400,50);
            g.drawImage(br_green_ber,130,95,400 - brake_gage,50);

            g.drawImage(meter,screenW - 170,screenH - 410);
            g.drawImage(handle,controller_x,controller_y,100,100);

            switch (game_end){
                case -1:
                    break;

                case 0:
                    g.drawImage(clear,screenW - 1400, screenH - 800,1000,700);
                    gimmick_pop = false;
                    break;

                case 1:
                    g.drawImage(outside, screenW - 1700, screenH - 800,1600,700);
                    break;

                case 2:
                    g.drawImage(crash, screenW - 1400, screenH - 800,1000,700);
                    break;

                case 3:
                    g.drawImage(time_up, screenW - 1400, screenH - 800,1000,700);
                    break;
            }
        }
    }
// && obj.position.z > car.position.z
// && obj.position.x + obj.scale.x > car.position.x
    //- obj.scale.z
    //障害物の当たり判定
    private void hit_gimmick(Object3D obj) {
        if(obj.position.z+10.0f > car.position.z){
            if(obj.position.x < car.position.x && obj.position.x + obj.scale.x > car.position.x){//車の左上が、「障害物の左面より右かつ、障害物の右面より左」の時
                dmg++;
                speed = 0.0f;
                speed_ac = 0.0f;
            }else if(obj.position.x < car.position.x + car.scale.x && obj.position.x+obj.scale.x > car.position.x+car.scale.x) {//車の右上が、「障害物の左面より右かつ、障害物の右面より左」の時
                dmg++;
                speed = 0.0f;
                speed_ac = 0.0f;
            }
        }else{
            speed = 0.1f;
        }
    }

    //地面の描画
    private void make_load() {
        load1.draw(1);
        load2.draw(1);
        load3.draw(1);
        load4.draw(1);
        load5.draw(1);
        load6.draw(1);
        load7.draw(1);
        load8.draw(1);
        load9.draw(1);
        load10.draw(1);
        goal.draw(1);

        if(!game_over) {
            if (car_z + 5.0f <= load1.position.z) {
                load1.position.z -= 100.0f;
                gimmick_pop = true;
            }
            if (car_z + 5.0f <= load2.position.z) {
                load2.position.z -= 100.0f;
            }
            if (car_z + 5.0f <= load3.position.z) {
                load3.position.z -= 100.0f;
            }
            if (car_z + 5.0f <= load4.position.z) {
                load4.position.z -= 100.0f;
            }
            if (car_z + 5.0f <= load5.position.z) {
                load5.position.z -= 100.0f;
            }
            if (car_z + 5.0f <= load6.position.z) {
                load6.position.z -= 100.0f;
            }
            if (car_z + 5.0f <= load7.position.z) {
                load7.position.z -= 100.0f;
            }
            if (car_z + 5.0f <= load8.position.z) {
                load8.position.z -= 100.0f;
            }
            if (car_z + 5.0f <= load9.position.z) {
                load9.position.z -= 100.0f;
            }
            if(load_count != 5) {
                if (car_z + 5.0f <= load10.position.z) {
                    load_count++;
                    load10.position.z -= 100.0f;
                    goal.position.z -= 100.0f;
                }
            }else{
                if (car_z + 5.0f <= load10.position.z) {
                    load_count++;
                    load10.position.z -= 200.0f;
                    goal.position.z -= 100.0f;
                    goal_pop = true;
                }
            }
        }
    }

    //ギミックのセット
    private void set_gimmick(Object3D obj){
        if(obj.position.z + obj.scale.z - 15.0f > car.position.z + car.scale.z) {
            obj.position.z -= 100.0f;
            obj.position.x = car_x;
        }
    }

    //x軸の動き
    private void move_x(int num){
        switch (num) {
            case 0:
                save_x = 0.0f;
                break;
            case 1:
                if(save_x < 0.1f) {
                    save_x += 0.005f;
                }
                if(car_angle < 30){
                    car_angle++;
                }
                break;
            case 2:
                if(save_x > -0.1f) {
                    save_x -= 0.005f;
                }
                if(car_angle > -30){
                    car_angle++;
                }
                break;
        }
        car_x += save_x;
        c_x += save_x;
        c_cx += save_x;
    }

    //z軸の動き
    private void move_z(float down, int out){
        if(game_end == -1) {
            if (speed_ac < 1.0f) {
                speed_ac += 0.01f;
            }
            if(gimmick_pop) {
                hit_gimmick(gimmick1);
                hit_gimmick(gimmick2);
            }
            save_z = speed + speed_ac - down;
            car_z -= save_z;
        }else if(game_end == 0 || game_end == 1){
            if (speed_ac > 0.0f) {
                speed_ac -= 0.01f;
            }else{
                speed_ac = 0.0f;
            }
            save_z = speed_ac;
            car_z -= save_z;
        }
        if(out == 0) {
            c_z -= save_z;
            c_cz -= save_z;
        }
    }

    //定期処理
    public synchronized void onTick(){
        if(g == null)return;
        //初期化
        if(init >= 0){
            scene = init;
            init = -1;
            init_scene();
        }
    }

    //値の初期化
    private void init_scene(){
        car_x = 3.0f;
        car_y = 1.0f;
        car_z = 8.0f;

        c_x = 0.0f;
        c_y = 8.0f;
        c_z = 25.0f;

        c_cx = 0.0f;
        c_cy = 0.8f;
        c_cz = 0.0f;

        save_x = 0.0f;
        save_z = 0.0f;

        speed_ac = 0.0f;

        load1.position.z  = 0.0f;
        load2.position.z  = -10.0f;
        load3.position.z  = -20.0f;
        load4.position.z  = -30.0f;
        load5.position.z  = -40.0f;
        load6.position.z  = -50.0f;
        load7.position.z  = -60.0f;
        load8.position.z  = -70.0f;
        load9.position.z  = -80.0f;
        load10.position.z = -90.0f;
        goal.position.z = -90.0f;

        gimmick1.position.z = -130.0f;
        gimmick2.position.z = -180.0f;

        load1.position.y  = -1.0f;
        load2.position.y  = -1.0f;
        load3.position.y  = -1.0f;
        load4.position.y  = -1.0f;
        load5.position.y  = -1.0f;
        load6.position.y  = -1.0f;
        load7.position.y  = -1.0f;
        load8.position.y  = -1.0f;
        load9.position.y  = -1.0f;
        load10.position.y = -1.0f;
        goal.position.y = -1.0f;

        gimmick1.position.y = -1.0f;
        gimmick2.position.y = -1.0f;
        gimmick1.scale.set(3.0f,3.0f,3.0f);
        gimmick2.scale.set(3.0f,3.0f,3.0f);

        gimmick1.rotate.set(0.0f,90.0f,0.0f);
        gimmick2.rotate.set(0.0f,0.0f,0.0f);
        car.scale.set(1.5f,1.5f,1.5f);

        controller_x = screenW - 178;
        controller_y = screenH - 260;
        center_0 = controller_y;

        load_count = 0;

        game_end = -1;

        time = 0;

        speed = 0.1f;

        angle = 0;

        goal_pop = false;
        gimmick_pop = false;
        game_over = false;
        dmg = 0;
    }

    //タッチイベント処理
    public boolean onTouch(View v, MotionEvent event){
        if(g == null)return false;
        int action = event.getAction();
        if (scene == TITLE) {
            if(action == MotionEvent.ACTION_DOWN) {
                init = PLAY;
                init_scene();
            }
        }
        if (scene == PLAY) {
            int touchX = (int) event.getX();//タッチしたところのx座標
            int touchY = (int) event.getY();//タッチしたところのy座標

            if(game_end == -1) {
                if(action == MotionEvent.ACTION_DOWN){
                    Play = true;
                }
                if((touchX > 50) && (touchX < 200) && (touchY > screenH - 250) && (touchY < screenH - 50)) {
                    switch (action) {
                        case MotionEvent.ACTION_DOWN:
                            brake_on = true;
                            break;

                        case MotionEvent.ACTION_UP:
                            brake_on = false;
                            break;
                    }
                }

                if ((touchX > screenW - 178) && (touchX < screenW - 78) && (touchY > controller_y) && (touchY < controller_y + 100)) {
                    if (action == MotionEvent.ACTION_MOVE) {
                        if (event.getY() > screenH - 390 && event.getY() < screenH - 30) {
                            controller_y = (int) event.getY() - 50;
                            if (center_0 - 10 >= controller_y && center_0 + 10 <= controller_y) {
                                angle = 0;
                            } else if (center_0 - 11 < controller_y) {
                                angle = 1;
                            } else {
                                angle = 2;
                            }
                        }
                    }
                }
            }else{
                if(action == MotionEvent.ACTION_DOWN) {
                    init = PLAY;
                    init_scene();
                    Play = false;
                }
            }
        }
        return true;
    }
}