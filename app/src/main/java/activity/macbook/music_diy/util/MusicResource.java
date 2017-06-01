package activity.macbook.music_diy.util;

import java.io.Serializable;

/**
 * Created by Macbook on 2017/5/26.
 */

public class MusicResource implements Serializable{

    private static final long serialVersionUID = -6536286569192349370L;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    private String name;
    private String author;
    private String path;
    private long duration;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }
}
