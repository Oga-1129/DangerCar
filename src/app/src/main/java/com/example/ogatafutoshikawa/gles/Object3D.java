package com.example.ogatafutoshikawa.gles;

import java.util.ArrayList;
import android.opengl.Matrix;

//3Dオブジェクト
public class Object3D {
    public Figure  figure;                     //フィギュア
    public Vector3 position=new Vector3();     //位置
    public Vector3 rotate  =new Vector3();     //回転
    public Vector3 scale   =new Vector3(1,1,1);//拡大縮小
    public ArrayList<Object3D> childs=new ArrayList<Object3D>();//子

    //描画
    public void draw(int check) {
        GLES.glPushMatrix();
        Matrix.translateM(GLES.mMatrix,0,position.x,position.y,position.z);
        Matrix.rotateM(GLES.mMatrix,0,rotate.z,0,0,1);
        Matrix.rotateM(GLES.mMatrix,0,rotate.y,0,1,0);
        Matrix.rotateM(GLES.mMatrix,0,rotate.x,1,0,0);
        Matrix.scaleM(GLES.mMatrix,0,scale.x,scale.y,scale.z);
        GLES.updateMatrix();
        figure.draw(check);
        for (int i=0;i<childs.size();i++) childs.get(i).draw(check);
        GLES.glPopMatrix();
    }
}