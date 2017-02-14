package com.github.quentin7b.sn.database.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.misc.BaseDaoEnabled;

/**
 * Model class for the notification
 *
 * @see com.j256.ormlite.misc.BaseDaoEnabled (auto save/update)
 */
public class StickyNotification
        extends BaseDaoEnabled<StickyNotification, Integer>
        implements Comparable<StickyNotification>, Parcelable {

    public static final Parcelable.Creator<StickyNotification> CREATOR = new Parcelable.Creator<StickyNotification>() {
        @Override
        public StickyNotification createFromParcel(Parcel source) {
            return new StickyNotification(source);
        }

        @Override
        public StickyNotification[] newArray(int size) {
            return new StickyNotification[size];
        }
    };

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
        isNotification = true;
    }

    public StickyNotification(StickyNotification stickyNotification) {
        this(stickyNotification.title, stickyNotification.content, stickyNotification.defcon, stickyNotification.isNotification());
    }

    public StickyNotification(final String title, final String content, final Defcon defcon, final boolean isNotification) {
        this.title = title;
        this.content = content;
        this.defcon = defcon;
        this.isNotification = isNotification;
    }

    private StickyNotification(Parcel source) {
        this(
                source.readString(),
                source.readString(),
                Defcon.from(source.readInt()),
                source.readInt() == 1
        );
        setId(source.readInt());
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
        return defcon.compareTo(another.defcon);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(content);
        dest.writeInt(defcon.describe());
        dest.writeInt(isNotification ? 1 : 0);
        dest.writeInt(id);
    }

    /**
     * Level of the notification
     */
    public enum Defcon {
        USELESS, NORMAL, IMPORTANT, ULTRA;

        public static Defcon from(int description) {
            switch (description) {
                case 0:
                    return USELESS;
                case 1:
                    return NORMAL;
                case 2:
                    return IMPORTANT;
                case 3:
                    return ULTRA;
                default:
                    return null;
            }
        }

        public int describe() {
            switch (this) {
                case USELESS:
                    return 0;
                case NORMAL:
                    return 1;
                case IMPORTANT:
                    return 2;
                case ULTRA:
                    return 3;
                default:
                    return -1;
            }
        }
    }
}
