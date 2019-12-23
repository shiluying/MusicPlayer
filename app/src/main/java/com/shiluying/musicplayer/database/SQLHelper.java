package com.shiluying.musicplayer.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.shiluying.musicplayer.enity.Record;

import java.util.ArrayList;
import java.util.List;

public class SQLHelper {
    //查询歌单名称和歌单id
    public ArrayList queryMusicListName(SQLiteDatabase db){
        ArrayList data=new ArrayList();
        Cursor cursor= db.query(DBHelper.MUSICLISTNAME_TABLE_NAME, DBHelper.MUSICLISTNAME_TABLE_COLUMNS, null,null, null, null, null);
        while (cursor != null && cursor.moveToNext()) {
            Record record = new Record();
            record.musicListName(cursor.getInt(0), cursor.getString(cursor.getColumnIndex(DBHelper.RECORD_MUSICLIST_NAME)));
            data.add(record);
            Log.i("QUERY", "queryData record = " + record.getMusicListName());
        }
        cursor.close();
        return data;
    }
    public ArrayList<Record> queryLoacMusic(SQLiteDatabase db){
        ArrayList data=new ArrayList();
        Cursor cursor= db.query(DBHelper.MUSICLISTNAME_TABLE_NAME, DBHelper.MUSICLISTNAME_TABLE_COLUMNS, DBHelper.RECORD_MUSICLIST_NAME+"=?",new String[]{"本地音乐"}, null, null, null);
        while (cursor != null && cursor.moveToNext()) {
            Integer id=cursor.getInt(0);
            data=queryMusicList(db,id);
        }
        cursor.close();
        return data;
    }
    //查询歌曲id对应的歌曲
    public String queryMusic(SQLiteDatabase db,Integer musicid){
        Cursor cursor= db.query(DBHelper.MUSICNAME_TABLE_NAME, DBHelper.MUSICNAME_TABLE_COLUMNS, null,null, null, null, null);
        String musicname="";
        while (cursor != null && cursor.moveToNext()) {
           if(musicid.equals(cursor.getInt(0))){
               musicname=cursor.getString(cursor.getColumnIndex(DBHelper.RECORD_MUSIC_NAME));
           }
        }
        cursor.close();
        return musicname;
    }
    //查找某歌单下所有歌曲
    public ArrayList queryMusicList(SQLiteDatabase db,Integer listid){
        Log.i("MUSICLIST",listid+"");
        ArrayList record=new ArrayList();
        Cursor cursor= db.query(DBHelper.MUSICLIST_TABLE_NAME, DBHelper.MUSICLIST_TABLE_COLUMNS, null,null, null, null, null);
        while (cursor != null && cursor.moveToNext()) {
            Integer selectlistid=cursor.getInt(0);
            if(selectlistid.equals(listid)) {
                Record data=new Record();
                Integer musicid = cursor.getInt(1);
                String musicname = queryMusic(db, musicid);
                data.musicName(musicid,musicname);
                record.add(data);
                Log.i("QUERY", "queryData record = " + data.toString());
            }
        }
        cursor.close();
        return record;
    }
    //查询歌曲名称和歌曲id
    public ArrayList queryMusicName(SQLiteDatabase db){
        ArrayList data=new ArrayList();
        Cursor cursor= db.query(DBHelper.MUSICNAME_TABLE_NAME, DBHelper.MUSICNAME_TABLE_COLUMNS, null,null, null, null, null);
        while (cursor != null && cursor.moveToNext()) {
            Record record = new Record();
            record.musicName(cursor.getColumnIndex(DBHelper.RECORD_MUSIC_ID), cursor.getString(cursor.getColumnIndex(DBHelper.RECORD_MUSIC_NAME)));
            data.add(record);
            Log.i("QUERY", "queryData record = " + record.getMusicName()+record.getMusicId());
        }
        cursor.close();
        return data;
    }
    private Integer queryLastRecordId(SQLiteDatabase db,String tablename){
        Cursor cursor = db.rawQuery("select last_insert_rowid() from "+tablename, null);
        Integer selectId=-1;
        if (cursor.moveToFirst())
            selectId = cursor.getInt(0);
        cursor.close();
        return selectId;
    }
    //添加歌单
    public Record insertMusicListName(SQLiteDatabase db, String MusicListName){
        ContentValues contentValues = new ContentValues();
        contentValues.put("musiclistname",MusicListName);
        db.insertWithOnConflict(DBHelper.MUSICLISTNAME_TABLE_NAME,null,contentValues,SQLiteDatabase.CONFLICT_IGNORE);
        Integer id=queryLastRecordId(db,DBHelper.MUSICLISTNAME_TABLE_NAME);
        Record record=new Record();
        record.musicListName(id,MusicListName);
        Log.i("TEST","insert music list name==>"+id+":"+MusicListName);
        return record;
    }
    //将歌曲添加到指定歌单
    public void insertMusic(SQLiteDatabase db,Integer MusicListId,Integer MusicId){
        Log.i("MUSICLIST",MusicListId+"");
        ContentValues contentValues = new ContentValues();
        contentValues.put("musiclistid",MusicListId);
        contentValues.put("musicid",MusicId);
        db.insertWithOnConflict(DBHelper.MUSICLIST_TABLE_NAME,null,contentValues,SQLiteDatabase.CONFLICT_IGNORE);
        Log.i("TEST","insert music"+MusicListId+"==>"+MusicId);
    }
    //添加歌曲
    public Record insertMusicName(SQLiteDatabase db,String MusicName){
        ContentValues contentValues = new ContentValues();
        contentValues.put("musicname",MusicName);
        db.insertWithOnConflict(DBHelper.MUSICNAME_TABLE_NAME,null,contentValues,SQLiteDatabase.CONFLICT_IGNORE);;
        Integer id=queryLastRecordId(db,DBHelper.MUSICNAME_TABLE_NAME);
        Record record=new Record();
        record.musicName(id,MusicName);
        Log.i("TEST","insert music name==>"+id+":"+MusicName);
        return record;
    }
    public void deleteMusicList(SQLiteDatabase db,Integer MusicListId){
        db.delete(DBHelper.MUSICLIST_TABLE_NAME,"musiclistid=?", new String[]{String.valueOf(MusicListId)});
        db.delete(DBHelper.MUSICLISTNAME_TABLE_NAME,"musiclistid=?", new String[]{String.valueOf(MusicListId)});
    }
    public void deleteMusic(SQLiteDatabase db,Integer MusicListId,Integer MusicId){
        db.delete(DBHelper.MUSICLIST_TABLE_NAME,"musiclistid=? and musicid=?", new String[]{String.valueOf(MusicListId),String.valueOf(MusicId)});
    }

}
