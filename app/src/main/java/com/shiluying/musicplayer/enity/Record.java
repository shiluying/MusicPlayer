package com.shiluying.musicplayer.enity;

public class Record {
    private Integer musicListId;
    private Integer musicId;
    private String musicName;
    private String musicListName;
    public void musicName(Integer musicId,String musicName){
        this.musicId=musicId;
        this.musicName=musicName;
    }
    public void musicListName(Integer musicListId,String musicListName){
        this.musicListId=musicListId;
        this.musicListName=musicListName;
    }
    public void musicList(Integer musicListId,Integer musicId){
        this.musicListId=musicListId;
        this.musicId=musicId;
    }
    public Integer getMusicListId(){
        return musicListId;
    }
    public Integer getMusicId(){
        return musicId;
    }
    public String getMusicName(){
        return musicName;
    }
    public String getMusicListName(){
        return musicListName;
    }
}
