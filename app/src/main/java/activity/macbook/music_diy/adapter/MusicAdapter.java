package activity.macbook.music_diy.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

import activity.macbook.music_diy.R;
import activity.macbook.music_diy.util.MusicResource;

/**
 * Created by Macbook on 2017/5/26.
 */

public class MusicAdapter extends BaseAdapter {
    private List<MusicResource> oList;
    private Context oContext;
    private LayoutInflater oInflater;

    public MusicAdapter(List<MusicResource> oList, Context oContext) {
        this.oList = oList;
        this.oContext = oContext;
        this.oInflater=LayoutInflater.from(oContext);
    }

    public int getCount() {
        return oList.size();
    }

    public Object getItem(int position) {
        return oList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View view, ViewGroup parent) {
        ViewHoulder oHoulder = null;
        if(view==null){
            oHoulder=new ViewHoulder();
            view = oInflater.inflate(R.layout.list_item,null);
            oHoulder.img = (ImageView) view.findViewById(R.id.mu_img);
            oHoulder.name = (TextView) view.findViewById(R.id.mu_name);
            oHoulder.author = (TextView) view.findViewById(R.id.mu_author);
            oHoulder.duration = (TextView) view.findViewById(R.id.mu_time);
            view.setTag(oHoulder);
        }else{
            oHoulder= (ViewHoulder) view.getTag();
        }
        oHoulder.img.setBackgroundResource(R.drawable.play_button);
        oHoulder.name.setText(oList.get(position).getName());
        oHoulder.author.setText(oList.get(position).getAuthor());
        oHoulder.duration.setText( getTime(oList.get(position).getDuration()));
        return view;
    }

    private String getTime(long time){
        SimpleDateFormat formats=new SimpleDateFormat("mm:ss");
        String times = formats.format(time);
        return times;
    }

    class ViewHoulder{
        ImageView img;
        TextView name;
        TextView author;
        TextView duration;
    }
}
