package com.example.ogatafutoshikawa.gles;

//メッシュ
public class Mesh {
    public VertexBuffer vertexBuffer;//頂点バッファ
    public IndexBuffer  indexBuffer; //インデックスバッファ
    public Material     material;    //マテリアル

    //描画
    public void draw(int check) {
        material.bind();
        vertexBuffer.bind();
        indexBuffer.draw(check);
        vertexBuffer.unbind();
        material.unbind();
    }
}
