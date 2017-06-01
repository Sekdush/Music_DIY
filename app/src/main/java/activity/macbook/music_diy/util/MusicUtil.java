package activity.macbook.music_diy.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Macbook on 2017/5/26.
 * 获取手机音乐文件
 */

public class MusicUtil {
    public static List<MusicResource> getMusicData(Context context){
        List<MusicResource> oList=new ArrayList<MusicResource>();
        ContentResolver resolver=context.getContentResolver();
        /*查询手机中的音乐文件*/
        Cursor cursor=resolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,null,null,null,
                MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        while (cursor.moveToNext()){
            MusicResource music=new MusicResource();
            //得到歌曲名
            String name=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
            //得到歌手名
            String author=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
            //得到歌曲文件路径
            String path=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
            //得到歌曲时长
            long duration= Long.parseLong(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)));
            if(author.equals("<unknow>")){
                author="未知艺术家";
            }
            //当歌曲大于20秒，才获得歌曲
            if(duration>20000){
                music.setName(name);
                music.setAuthor(author);
                music.setPath(path);
                music.setDuration(duration);
                oList.add(music);
            }

        }
        return oList;
    }
}
