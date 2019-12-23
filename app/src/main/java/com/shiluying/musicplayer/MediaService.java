package com.shiluying.musicplayer;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;

public class MediaService extends Service {
    public MediaPlayer mediaPlayer;
    private AssetManager assetManager;
    public String[] musicList;
    public MediaService(Context context) {
        mediaPlayer=new MediaPlayer();
        assetManager=context.getAssets();
        try {
            musicList=assetManager.list("music");
            loadMusic(musicList[0]);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public int getMusicDuration() {
        return mediaPlayer.getDuration();//获取文件的总长度
    }
    public int getPosition() {
        return mediaPlayer.getCurrentPosition();//获取当前播放进度
    }
    public void setPosition (int position) {
        mediaPlayer.seekTo(position);//重新设定播放进度
    }
    public void loadMusic(String musicName){
        try {
            mediaPlayer.stop();
            mediaPlayer.reset();
            AssetFileDescriptor afd = assetManager.openFd("music/" + musicName);
            mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void startMusic(){
        mediaPlayer.start();
    }
    public void pauseMusic(){
        mediaPlayer.pause();
    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
