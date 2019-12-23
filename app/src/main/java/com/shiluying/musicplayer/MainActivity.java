package com.shiluying.musicplayer;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextSwitcher;
import android.widget.TextView;

import com.shiluying.musicplayer.Music.MusicContent;
import com.shiluying.musicplayer.database.DBHelper;
import com.shiluying.musicplayer.database.SQLHelper;
import com.shiluying.musicplayer.enity.Record;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        ListFragment.OnListFragmentInteractionListener,
        ContentFragment.OnContentFragmentInteractionListener,
        LocalFragment.OnLocalFragmentInteractionListener,
        Runnable,
        SeekBar.OnSeekBarChangeListener
        {
    private ImageView playingPre,playingPlay,playingNext,playingType;
    private boolean play=false,isOrder=true;
    private MediaService mediaService;
    private Integer listid;
    private TextSwitcher mSwitcher;
    private SeekBar mSeekBar;
    private String[] localMusicName;
    private ArrayList<Record> playList;
    private ArrayList<Record> musicList;
    private int musicIndex=0;
    private Thread thread;
    private String musicNameText;
    //页面上方
    private Button musiclist,localmusic,addlist;
    private TextView musicText;

    private AlertDialog.Builder builder;
    //Fragment
    private ListFragment listFragment;
    private ContentFragment contentFragment;
    private LocalFragment localFragment;
    //数据库
    SQLiteDatabase db;
    SQLHelper sqlHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //视图
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initButton();
        //媒体
        mediaService = new MediaService(this);
        //初始化数据库
        sqlHelper  = new SQLHelper();
        db=new DBHelper(this).getWritableDatabase();
        localMusicName=mediaService.musicList;//获取本地歌曲信息
        initMusicName();//将本地歌曲信息传入数据库,创建本地歌单
        initView();//初始化视图
        musicList= sqlHelper.queryLoacMusic(db);
        playList=musicList;
        mSeekBar.setOnSeekBarChangeListener(this);

    }
    //初次创建表的时候执行,获取本地音乐信息
    private void initMusicName(){
        ArrayList<Record> test=sqlHelper.queryLoacMusic(db);
        if(test.size()==0){
            Record item= sqlHelper.insertMusicListName(db,"本地音乐");
            Log.i("ooo",item.getMusicListId()+"--"+item.getMusicListName());
            for(int i=0;i<localMusicName.length;i++){
                Record record= sqlHelper.insertMusicName(db,localMusicName[i]);
                Log.i("ooo",record.getMusicId()+"--"+record.getMusicName());
                sqlHelper.insertMusic(db,item.getMusicListId(),record.getMusicId());
            }
        }
    }
    private void initView(){
        musicText=findViewById(R.id.musicname);//正在播放歌曲名称
        //滑动条部分
        mSeekBar = (SeekBar) findViewById(R.id.music_seek_bar);
//        mSeekBar.setOnSeekBarChangeListener(this);
        mSwitcher = (TextSwitcher) findViewById(R.id.text_switcher);
        mSwitcher.setInAnimation(this, android.R.anim.fade_in);
        mSwitcher.setOutAnimation(this, android.R.anim.fade_out);
        showListFragment();
    }
    private void initButton(){
        localmusic = findViewById(R.id.localmusic);
        musiclist = findViewById(R.id.musiclist);
        addlist = findViewById(R.id.addmusiclist);
        playingPre = (ImageView) findViewById(R.id.playing_pre);
        playingPlay = (ImageView) findViewById(R.id.playing_play);
        playingNext = (ImageView) findViewById(R.id.playing_next);
        playingType = (ImageView) findViewById(R.id.playing_type);
        localmusic.setOnClickListener(this);
        musiclist.setOnClickListener(this);
        addlist.setOnClickListener(this);
        playingPre.setOnClickListener(this);
        playingPlay.setOnClickListener(this);
        playingNext.setOnClickListener(this);
        playingType.setOnClickListener(this);
    }
    public void showListFragment(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        listFragment=new ListFragment();
        Bundle arguments = new Bundle();
        ArrayList<Integer> listid=new ArrayList<Integer>();
        ArrayList<String> listname=new ArrayList<String>();
        ArrayList<Record> musicListName=sqlHelper.queryMusicListName(db);
        for(Record record : musicListName){
            listid.add(record.getMusicListId());
            listname.add(record.getMusicListName());
        }
        arguments.putIntegerArrayList("ListId",listid );
        arguments.putStringArrayList("ListName",listname );
        listFragment.setArguments(arguments);
        FragmentTransaction transaction = fragmentManager.beginTransaction();//开启一个事务
        transaction.replace(R.id.content_main, listFragment);//添加fragment
        transaction.commit();
    }
    public void showLocalFragment(){
        ArrayList<Record> list=new ArrayList<>();
        list=sqlHelper.queryLoacMusic(db);
        localFragment=new LocalFragment();
        ArrayList<Integer> musicid=new ArrayList<Integer>();
        ArrayList<String> musicname=new ArrayList<String>();
        for(Record record : list){
            musicid.add(record.getMusicId());
            musicname.add(record.getMusicName());
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        Bundle arguments = new Bundle();
        arguments.putIntegerArrayList("MusicId",musicid );
        arguments.putStringArrayList("MusicName",musicname );
        localFragment.setArguments(arguments);
        FragmentTransaction transaction = fragmentManager.beginTransaction();//开启一个事务
        transaction.replace(R.id.content_main, localFragment);//添加fragment
        transaction.commit();
    }
    private void showContentFragment(Integer id){
        musicList= sqlHelper.queryMusicList(db,id);
        playList=musicList;
        if(isOrder){

        }else{
            RandomPlaying();
        }


        ArrayList<Integer> musicid=new ArrayList<Integer>();
        ArrayList<String> musicname=new ArrayList<String>();
        for(Record record : musicList){
            musicid.add(record.getMusicId());
            musicname.add(record.getMusicName());
        }
        contentFragment=new ContentFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        Bundle arguments = new Bundle();
        arguments.putIntegerArrayList("MusicId",musicid );
        arguments.putStringArrayList("MusicName",musicname );
        contentFragment.setArguments(arguments);
        FragmentTransaction transaction = fragmentManager.beginTransaction();//开启一个事务
        transaction.replace(R.id.content_main, contentFragment);//添加fragment
//        transaction.addToBackStack(null);
        transaction.commit();
    }
    @Override
    //本地歌曲点击事件，点击添加到歌单
    public void OnLocalFragmentInteractionListener(MusicContent.MusicItem mItem) {
        showAddDialog(mItem.id);
    }
    @Override
    //歌单列表的点击事件，转到contentfragment
    public void onListFragmentInteraction(MusicContent.MusicItem item) {
        showListOption(item.id);
        listid=item.id;

    }
    @Override
    //歌单详情点击事件，点击进行播放
    public void OnContentFragmentInteractionListener(MusicContent.MusicItem mItem) {
        showContentOption(mItem.id,mItem.content);
    }
    private void showListOption(final Integer listid){
        builder = new AlertDialog.Builder(this)
                .setTitle("请选择操作")
                .setNeutralButton("删除歌单", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        sqlHelper.deleteMusicList(db,listid);
                        showListFragment();
                    }
                    })
                .setPositiveButton("查看歌曲", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        showContentFragment(listid);
                    }
                }
                );
        builder.create().show();
    }
    private void showContentOption(final Integer musicid, final String musicname){
        builder = new AlertDialog.Builder(this)
                .setTitle("请选择操作")
                .setNeutralButton("删除歌曲", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        sqlHelper.deleteMusic(db,listid,musicid);
                        showContentFragment(listid);
                    }
                })
                .setPositiveButton("播放", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int j) {
                                mediaService.loadMusic(musicname);
                                musicIndex=0;
                                for(int i=0;i<playList.size();i++){
                                    if(musicid.equals(playList.get(i).getMusicId())){
                                        musicIndex=i;
                                        break;
                                    }
                                }
                                musicNameText=playList.get(musicIndex).getMusicName();


                                StartPlaying();
                            }
                        }
                );
        builder.create().show();
    }
    private void showAddListDialog(){
        final View layout = View.inflate(this, R.layout.addmusiclist,
                null);
        ArrayList<Record> musiclist=sqlHelper.queryLoacMusic(db);
        List<String> list = new ArrayList<>();
        for (Record record:musiclist)
        {
            list.add(record.getMusicId()+":"+record.getMusicName());
        }
        Spinner musicName = (Spinner) layout.findViewById(R.id.music_name);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,list);
        musicName.setAdapter(dataAdapter);
        builder = new AlertDialog.Builder(this)
                .setTitle("添加歌单")
                .setView(layout)
                .setPositiveButton("添加", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        EditText ListName = (EditText) layout.findViewById(R.id.music_list_name);
                        Spinner musicName=(Spinner)layout.findViewById(R.id.music_name);
                        String select = musicName.getSelectedItem().toString();
                        String[] data=select.split(":");
                        Integer id= Integer.valueOf(data[0]);
                        String listname=ListName.getText().toString();
                        Record item= sqlHelper.insertMusicListName(db,listname);
                        sqlHelper.insertMusic(db,item.getMusicListId(),id);
                    }
                });
        builder.create().show();
    }
    private void showAddDialog(final Integer musicid) {
        final View layout = View.inflate(this, R.layout.addmusic,
                null);
        ArrayList<Record> musiclist=sqlHelper.queryMusicListName(db);
        List<String> list = new ArrayList<>();
        for (Record record:musiclist)
        {
            list.add(record.getMusicListId()+":"+record.getMusicListName());
        }
        Spinner ListName = (Spinner) layout.findViewById(R.id.list_name);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,list);
        ListName.setAdapter(dataAdapter);
        builder = new AlertDialog.Builder(this)
                .setTitle("添加到歌单")
                .setView(layout)
                .setPositiveButton("添加", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Spinner ListName = (Spinner) layout.findViewById(R.id.list_name);
                        String select = ListName.getSelectedItem().toString();
                        String[] data=select.split(":");
                        Integer id= Integer.valueOf(data[0]);
                        Log.i("GG",id+"=="+musicid);
                        sqlHelper.insertMusic(db,id,musicid);
                    }
                });
        builder.create().show();
    }
    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.playing_play:
                if(play){
                    StopPlaying();
                }else{
                    StartPlaying();
                }
                break;
            case R.id.playing_pre:
                PrePlaying();
                break;
            case R.id.playing_next:
                NextPlaying();
                break;
            case R.id.playing_type:
                if(isOrder){
                    RandomPlaying();
                }else{
                    OrderPlaying();
                }
                break;
            case R.id.localmusic:
                showLocalFragment();
                break;
            case R.id.musiclist:
                showListFragment();
                break;
            case R.id.addmusiclist:
                showAddListDialog();
                break;
        }
    }
    public void StartPlaying(){
        play=true;
        mediaService.startMusic();
        thread = new Thread(this);
        thread.start();
        musicText.setText(musicNameText);
        playingPlay.setImageResource(R.drawable.pause);


    }
    public void StopPlaying(){
        mediaService.pauseMusic();
        play=false;
        playingPlay.setImageResource(R.drawable.start);
    }
    public void NextPlaying(){
        if(musicIndex==playList.size()-1){
            musicIndex=0;
        }
        else{
            musicIndex++;
        }
        try{
            mediaService.loadMusic(playList.get(musicIndex).getMusicName());
        }catch (Exception e){
            NextPlaying();
        }

        musicNameText=playList.get(musicIndex).getMusicName();
        musicText.setText(musicNameText);
        StartPlaying();
    }
    public void PrePlaying(){
        if(musicIndex==0){
            musicIndex=playList.size()-1;
        }
        else{
            musicIndex--;
        }
        try{
            mediaService.loadMusic(playList.get(musicIndex).getMusicName());
        }catch (Exception e){
            PrePlaying();
        }

        musicNameText=playList.get(musicIndex).getMusicName();
        musicText.setText(musicNameText);
        StartPlaying();
    }
    public void OrderPlaying(){
        playList=musicList;
        isOrder=true;
        playingType.setImageResource(R.drawable.order);
    }
    public void RandomPlaying(){
        ArrayList<Record> temp=new ArrayList<>();
        for(Record record:musicList){
            temp.add(record);
        }
        Collections.shuffle(temp);
        playList=temp;
        isOrder=false;
        playingType.setImageResource(R.drawable.random);
    }

    @Override
    public void run() {
        play = true;
        try {
            while (play) {
                if (mediaService != null) {
                    final int musicDuration = mediaService.getMusicDuration();
                    final int position = mediaService.getPosition();
                    final Date dateTotal = new Date(musicDuration);
                    final SimpleDateFormat sb = new SimpleDateFormat("mm:ss");
                    mSeekBar.setMax((int) musicDuration);
                    mSeekBar.setProgress((int) position);
                    mSwitcher.post(
                            new Runnable() {
                                @Override
                                public void run() {
                                    Date date = new Date(position);
                                    String time = sb.format(date) + "/" + sb.format(dateTotal);
                                    mSwitcher.setCurrentText(time);
                                    if(position==musicDuration){
//                                        StopPlaying();

                                        NextPlaying();
                                    }
                                }
                            }
                    );
                }
                Thread.sleep(500);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaService.setPosition(seekBar.getProgress());
            }
        }
