package fr.quentinklein.stickynotifs.model;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.misc.BaseDaoEnabled;

/**
 * Created by quentin on 19/07/2014.
 * Model class for the notification
 *
 * @see com.j256.ormlite.misc.BaseDaoEnabled (auto save/update)
 */
public class StickyNotification extends BaseDaoEnabled<StickyNotification, Integer> implements Comparable<StickyNotification> {

    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField
    private String title;
    @DatabaseField
    private String content;
    @DatabaseField(dataType = DataType.ENUM_INTEGER)
    private Defcon defcon;
    @DatabaseField
    private boolean isNotification;

    public StickyNotification() {
        title = "";
        content = "";
        defcon = Defcon.NORMAL;
        isNotification = false;
    }

    public StickyNotification(StickyNotification stickyNotification) {
        setTitle(stickyNotification.title);
        setContent(stickyNotification.content);
        setDefcon(stickyNotification.defcon);
        setNotification(stickyNotification.isNotification());
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isNotification() {
        return isNotification;
    }

    public void setNotification(boolean isNotification) {
        this.isNotification = isNotification;
    }

    public Defcon getDefcon() {
        return defcon;
    }

    public void setDefcon(Defcon defcon) {
        this.defcon = defcon;
    }

    @Override
    public int compareTo(StickyNotification another) {
        return another.defcon.compareTo(defcon);
    }

    /**
     * Level of the notification
     * //TODO improvement : use xml values
     */
    public enum Defcon {
        USELESS, NORMAL, IMPORTANT, ULTRA;
    }
}
