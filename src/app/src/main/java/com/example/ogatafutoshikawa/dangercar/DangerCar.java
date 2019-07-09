package com.example.ogatafutoshikawa.dangercar;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

public class DangerCar extends Activity implements Runnable{


    private GLSurfaceView glView;
    private GLRenderer renderer;
    private Thread thread;

    //アクティビティ生成時に呼ばれる
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //GLサーフェイスビュー
        renderer = new GLRenderer(this);
        glView=new GLSurfaceView(this);
        glView.setEGLContextClientVersion(2);
        glView.setRenderer(renderer);
        glView.setOnTouchListener(renderer);
        setContentView(glView);
    }

    //アクティビティレジューム時に呼ばれる
    @Override
    public void onResume(){
        super.onResume();
        glView.onResume();
        thread = new Thread(this);
        thread.start();
    }

    //アクティビティポーズ時に呼ばれる
    @Override
    public void onPause(){
        super.onPause();
        glView.onPause();
        thread=null;
    }

    //スレッドの処理
    public void run(){
        while(thread!=null){
            //定期処理
            renderer.onTick();
            //スリープ
            try{
                Thread.sleep(50);
            }catch(Exception e){
            }
        }
    }
}

