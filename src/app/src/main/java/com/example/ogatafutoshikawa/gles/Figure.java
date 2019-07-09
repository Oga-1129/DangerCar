package com.example.ogatafutoshikawa.gles;

import java.util.ArrayList;
import java.util.HashMap;

//フィギュア
public class Figure {
    public HashMap<String, Material> materials;//マテリアル群
    public ArrayList<Mesh>          meshs;    //メッシュ群

    //描画
    public void draw(int check) {
        for (Mesh mesh:meshs) mesh.draw(check);
    }
}
