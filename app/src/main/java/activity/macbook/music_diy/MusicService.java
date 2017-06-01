package activity.macbook.music_diy;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.io.IOException;

import activity.macbook.music_diy.util.MusicResource;

/**
 * Created by Macbook on 2017/5/30.
 */

public class MusicService extends Service {
    private int newmusic;
    private MusicResource music;
    private MediaPlayer player=new MediaPlayer();
    private int state=0x11;//0x11:为第一次播放歌曲，0x12：暂停，0x13:继续播放
    private int curposition,duration;//当前音乐时间，时长
    @Override
    public void onCreate() {
        MyBroadcastReceiver receiver=new MyBroadcastReceiver();
        IntentFilter filter =new IntentFilter("com.Xuli.Service");
        registerReceiver(receiver,filter);
        //监听播放完成事件
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Intent intent=new Intent("com.Xuli.Activity");
                intent.putExtra("Over",true);
                sendBroadcast(intent);
                curposition=0;
                duration=0;
            }
        });
        super.onCreate();
    }
    public class MyBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            newmusic=intent.getIntExtra("newmusic",-1);//接收是否播放的是歌曲
            if(newmusic!=-1){
                music= (MusicResource) intent.getSerializableExtra("music");//获得歌曲对象
                if(music!=null){
                    playmusic(music);
                    state=0x12;
                }
            }
            int isplay=intent.getIntExtra("isplay",-1);
            if (isplay!=-1){
                switch (state){
                    //第一次播放歌曲
                    case 0x11:
                        music= (MusicResource) intent.getSerializableExtra("music");//获得歌曲对象
                        playmusic(music);
                        state=0x12;
                        break;
                    //暂停
                    case 0x12:
                        player.pause();
                        state=0x13;
                        break;
                    //继续播放
                    case 0x13:
                        player.start();
                        state=0x12;
                        break;

                }
            }
            int progress=intent.getIntExtra("progress",-1);
            if (progress!=-1){
                curposition= (int) (((progress*1.0)/100)*duration);//把当前歌曲位置转换成毫秒
                player.seekTo(curposition);
            }
            Intent intent2 = new Intent("com.Xuli.Activity");
            intent2.putExtra("state",state);
            sendBroadcast(intent2);//把当前状态发送给activity
        }
    }
    public void playmusic(MusicResource resource){
        if (player!=null){
            //停止播放
            player.stop();
            player.reset();
            try {
                //获得歌曲路径
                player.setDataSource(resource.getPath());
                //准备
                player.prepare();
                //播放
                player.start();
                duration = player.getDuration();//获得当前歌曲时长
                new Thread(){
                    @Override
                    public void run() {
                        while (curposition<duration){
                            try {
                                sleep(1000);
                                curposition = player.getCurrentPosition();//获得当前音乐时间
                                Intent intent=new Intent("com.Xuli.Activity");
                                intent.putExtra("curposition",curposition);
                                intent.putExtra("duration",duration);
                                sendBroadcast(intent);//把当前音乐的时间和当前音乐时长发送给Activity
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void unregisterReceiver(BroadcastReceiver receiver) {
        super.unregisterReceiver(receiver);
        unregisterReceiver(receiver);
    }
}
