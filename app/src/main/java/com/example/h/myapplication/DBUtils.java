package com.example.h.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


/**
 * Created by H on 2019/12/28.
 */

public class DBUtils {

    private  SQLiteDatabase db;
    private String sql;
    private Cursor c;

    public DBUtils(Context context) {
        db = context.openOrCreateDatabase("android.db",context.MODE_PRIVATE,null);
        createTableApp();
    }
    private void createTableApp(){
        sql="CREATE TABLE IF NOT EXISTS APP (id INTEGER PRIMARY KEY AUTOINCREMENT,img VARCHAR2(20) ,name VARCHAR2(20) ,scale VARCHAR2(20) ,status VARCHAR2(20))";
        try{
            db.rawQuery("SELECT id _id,img,name,scale,status FROM APP",null);
        }catch(Exception e){
            Log.i("Info","not exit");
            db.execSQL(sql);
            insert("0","京東","85","正常");
            insert("1","QQ","98","正常");
            insert("2","天猫","52","正常");
            insert("3","UC浏览器","75","正常");
            insert("4","微信","65","正常");
            insert("5","新浪","77","正常");
        }
    }
    public Cursor queryall(){
        sql="SELECT id _id,img,name,scale,status FROM APP";
        c=db.rawQuery(sql,null);
        return  c;
    } //查询app信息

    public Cursor querynormal(){
        sql="SELECT id _id,img,name,scale,status FROM APP where status='正常'";
        c=db.rawQuery(sql,null);
        return  c;
    } //查询正常模式的app信息

    public Cursor querysavepower(){
        sql="SELECT id _id,img,name,scale,status FROM APP where status='节电'";
        c=db.rawQuery(sql,null);
        return  c;
    } //查询节电模式的app信息

    public Integer querysavacount(){
        Integer count=0;
        sql="SELECT count(*) FROM APP where status='节电'";
        Cursor cursor=db.rawQuery(sql,null);
        if(cursor.getCount()>0)
        {
            cursor.moveToFirst();
            do{
                count = cursor.getInt(0);
            }while (cursor.moveToNext());
        }
        return  count;
    } //查询节电模式的app数量


    public Cursor deleteall(){
        sql="DELETE APP";
        c=db.rawQuery(sql,null);
        return  c;
    } //查询app信息

    //保存数据
    public void insert(String app_img,String app_name,String app_scale,String app_status){
        ContentValues values=new ContentValues();
        values.put("img",app_img);
        values.put("name",app_name);
        values.put("scale",app_scale);
        values.put("status",app_status);
        db.insert("APP",null,values);
    }

    //修改数据
    public void update(String name,String app_status){
        ContentValues values = new ContentValues();
        values.put("status",app_status);
        db.update("APP",values,"name=?",new String[]{name});
    }
    public void updateAll(String app_status){
        ContentValues values = new ContentValues();
        values.put("status",app_status);
        db.update("APP",values,null,null);
    }
}
