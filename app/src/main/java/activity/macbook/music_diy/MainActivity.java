package activity.macbook.music_diy;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import activity.macbook.music_diy.adapter.MusicAdapter;
import activity.macbook.music_diy.util.MusicResource;
import activity.macbook.music_diy.util.MusicUtil;

public class MainActivity extends AppCompatActivity {
    private ListView listView;
    private ImageButton btn_top,btn_play,btn_next,imageButton;
    private SeekBar SBar;
    private TextView time;
    private MusicAdapter adapter;
    private List<MusicResource> oList;
    private Context oContext;
    private MusicResource music;
    private int index=0;
    private int state=0x11;
    private int flag=0;//0为列表循环，1：为单曲循环 2：随机播放
    private SharedPreferences sha;
    private SharedPreferences.Editor oEditor;

        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.activity_main);
            init();//初始化
            oList = MusicUtil.getMusicData(oContext);
            adapter = new MusicAdapter(oList,oContext);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(onItemClickListener);
            btn_top.setOnClickListener(onClickListener);
            btn_next.setOnClickListener(onClickListener);
            btn_play.setOnClickListener(onClickListener);
            imageButton.setOnClickListener(onClickListener);
            //adapter.notifyDataSetChanged();
            //Utility.setListViewHeightBasedOnChildren(listView);
            seekbarchange();
    }
    public class MyBroadcastActivity extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            state=intent.getIntExtra("state",-1);
            switch (state){
                case 0x11:
                    btn_play.setImageResource(R.drawable.play_button);
                    break;
                case 0x12:
                    btn_play.setImageResource(R.drawable.pause_button);
                    break;
                case 0x13:
                    btn_play.setImageResource(R.drawable.play_button);
                    break;
            }
            int duration=intent.getIntExtra("duration",-1);
            int curposition=intent.getIntExtra("curposition",-1);
            if (curposition!=-1){
                SBar.setProgress((int) ((curposition*1.0)/duration*100));//为拖动条设置当前播放进度
                time.setText(inittime(curposition, duration));//显示时间
            }
            boolean isover=intent.getBooleanExtra("Over",false);
            if (isover==true){
                Intent intent1=new Intent("com.Xuli.Service");
                if(flag==0){//列表循环
                    if(index==oList.size()-1){
                        index=0;
                    }else{
                        index++;
                    }
                    music = oList.get(index);
                    intent1.putExtra("newmusic",1);
                    intent1.putExtra("music",music);
                    sendBroadcast(intent1);
                }else if(flag==1){
                    music = oList.get(index);
                    intent1.putExtra("newmusic",1);
                    intent1.putExtra("music",music);
                    sendBroadcast(intent1);
                }else{//随机播放
                    index = (int) (Math.random() * oList.size());
                    music = oList.get(index);
                    intent1.putExtra("newmusic",1);
                    intent1.putExtra("music",music);
                    sendBroadcast(intent1);
                }
                oEditor.putInt("index",index);
                oEditor.commit();
            }
        }
    }
    //ListItem监听器
    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            index=position;//获得当前下标
            music=oList.get(position);//获得当前选中位置
            Intent intent=new Intent("com.Xuli.Service");
            intent.putExtra("music",music);
            intent.putExtra("newmusic",1);
            sendBroadcast(intent);//发送广播到Service（服务）
        }
    };
    //上一曲下一曲监听器
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent=new Intent("com.Xuli.Service");
            switch (v.getId()){
                //上一曲按钮
                case R.id.btn_top:
                    if (index==0){//如果为第一首歌曲，当点击上一曲按钮后
                        index=oList.size()-1;//当前的下标为最后一首歌曲下标
                    }else{
                        index--;
                    }
                    music = oList.get(index);
                    intent.putExtra("newmusic",1);
                    intent.putExtra("music",music);
                    break;
                //暂停播放按钮
                case R.id.btn_paly:
                    if(music==null){//如果当前没有播放歌曲
                        music=oList.get(index);//播放第一首歌曲
                        intent.putExtra("music",music);
                    }
                    intent.putExtra("isplay",1);
                    break;
                //下一曲按钮
                case R.id.btn_bottom:
                    if(index==oList.size()-1){
                        index=0;
                    }else{
                        index++;
                    }
                    music = oList.get(index);
                    intent.putExtra("newmusic",1);
                    intent.putExtra("music",music);
                    break;
                //播放模式
                case R.id.xuanze_img:
                    flag++;
                    if (flag>2){
                        flag=0;
                    }
                    if(flag==0){
                        imageButton.setImageResource(R.drawable.sunxu);
                        Toast.makeText(oContext,"列表循环",Toast.LENGTH_SHORT).show();
                    }else if(flag==1){
                        imageButton.setImageResource(R.drawable.danqu);
                        Toast.makeText(oContext,"单曲循环",Toast.LENGTH_SHORT).show();
                    }else{
                        imageButton.setImageResource(R.drawable.suiji);
                        Toast.makeText(oContext,"随机循环",Toast.LENGTH_SHORT).show();
                    }
                    break;


            }
            sendBroadcast(intent);
        }
    };
    private void seekbarchange(){
        SBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            //当拖动条停止后调用该方法
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Intent intent=new Intent("com.Xuli.Service");
                intent.putExtra("progress",seekBar.getProgress());//获取当前拖动条位置
                sendBroadcast(intent);
            }

            //当开始拖动拖动条调用该方法
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            //当拖动条正在拖动动用该方法
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }
        });
    }
    /*
    把毫秒转换成分钟和秒
     */
    private String inittime(int cur,int dur){
        int cur_fen = cur/1000/60;//分
        int cur_miao=cur/1000%60;//秒
        int dur_fen=dur/1000/60;
        int dur_miao=dur/1000%60;
        //          2:30/4:58
        return getT(cur_fen) + ":" + getT(cur_miao) + "/" + getT(dur_fen) + ":" + getT(dur_miao);
    }
    //时间转换
    private String getT(int time){
        if (time<10){
            return "0"+time;
        }else{
            return time+"";
        }
    }
    /*控件初始化*/
    private void init(){
        listView = (ListView) findViewById(R.id.list);
        btn_top = (ImageButton) findViewById(R.id.btn_top);
        btn_play =(ImageButton) findViewById(R.id.btn_paly);
        btn_next = (ImageButton)findViewById(R.id.btn_bottom);
        SBar = (SeekBar) findViewById(R.id.seekBar);
        time = (TextView) findViewById(R.id.time);
        imageButton = (ImageButton) findViewById(R.id.xuanze_img);
        oContext = MainActivity.this;
        /*
        注册广播
         */
        MyBroadcastActivity receiver = new MyBroadcastActivity();
        IntentFilter filter = new IntentFilter("com.Xuli.Activity");
        registerReceiver(receiver,filter);
        //启动服务
        Intent intent=new Intent(oContext,MusicService.class);
        startService(intent);
        sha = getSharedPreferences("Data",0);
        oEditor = sha.edit();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            oEditor.putInt("zhuangtai",state);
            oEditor.putInt("index",index);
            oEditor.commit();
            Intent intent=new Intent(Intent.ACTION_MAIN);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
            return  true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE,0,1,"退出");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case 0:
                AlertDialog.Builder builder = new AlertDialog.Builder(oContext);
                builder.setTitle("提示");
                builder.setMessage("您确定要退出吗？");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent=new Intent(oContext,MusicService.class);
                        stopService(intent);
                        oEditor.clear();
                        System.exit(0);
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //注销广播
    @Override
    public void unregisterReceiver(BroadcastReceiver receiver) {
        super.unregisterReceiver(receiver);
        unregisterReceiver(receiver);
    }
}
