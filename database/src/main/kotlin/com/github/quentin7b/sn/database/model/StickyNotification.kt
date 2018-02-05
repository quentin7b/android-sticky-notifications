package com.github.quentin7b.sn.database.model

import android.os.Parcel
import android.os.Parcelable

import com.j256.ormlite.field.DataType
import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.misc.BaseDaoEnabled

import java.util.Date

/**
 * Model class for the notification
 *
 * @see com.j256.ormlite.misc.BaseDaoEnabled
 */
// https://youtrack.jetbrains.com/issue/KT-19300 ASAP
class StickyNotification : BaseDaoEnabled<StickyNotification, Int>, Comparable<StickyNotification>, Parcelable {

    @DatabaseField(generatedId = true)
    var id: Int = 0
    @DatabaseField
    var title: String = ""
    @DatabaseField
    var content: String = ""
    @DatabaseField(dataType = DataType.ENUM_INTEGER)
    var defcon: Defcon = Defcon.NORMAL
    @DatabaseField
    var isNotification: Boolean = false
    @DatabaseField(dataType = DataType.DATE_STRING, format = "yyyy-MM-dd HH:mm:ss")
    var deadLine: Date? = null

    constructor() {
        title = ""
        content = ""
        defcon = Defcon.NORMAL
        isNotification = true
        deadLine = null
    }

    constructor(stickyNotification: StickyNotification) : this(stickyNotification.title, stickyNotification.content, stickyNotification.defcon, stickyNotification.isNotification, stickyNotification.deadLine) {}

    constructor(title: String, content: String, defcon: Defcon, isNotification: Boolean, deadLine: Date?) {
        this.title = title
        this.content = content
        this.defcon = defcon
        this.isNotification = isNotification
        this.deadLine = deadLine
    }

    private constructor(source: Parcel) : super() {
        this.id = source.readInt()
        this.title = source.readString()
        this.content = source.readString()
        this.defcon = Defcon.from(source.readInt())
        this.isNotification = source.readInt() == 1
        val parcelDate = source.readLong()
        if (parcelDate != -1L) {
            this.deadLine = Date(parcelDate)
        }
    }

    override fun compareTo(other: StickyNotification): Int {
        return other.defcon.describe() - defcon.describe()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(id)
        dest.writeString(title)
        dest.writeString(content)
        dest.writeInt(defcon.describe())
        dest.writeInt(if (isNotification) 1 else 0)
        dest.writeLong(if (deadLine != null) deadLine!!.time else -1L)
    }

    /**
     * Level of the notification
     */
    enum class Defcon {
        USELESS, NORMAL, IMPORTANT, ULTRA;

        fun describe(): Int {
            return when (this) {
                USELESS -> 0
                NORMAL -> 1
                IMPORTANT -> 2
                ULTRA -> 3
            }
        }

        companion object {

            fun from(description: Int): Defcon {
                return when (description) {
                    0 -> USELESS
                    1 -> NORMAL
                    2 -> IMPORTANT
                    3 -> ULTRA
                    else -> NORMAL
                }
            }
        }
    }

    companion object CREATOR : Parcelable.Creator<StickyNotification> {
        override fun createFromParcel(source: Parcel): StickyNotification {
            return StickyNotification(source)
        }

        override fun newArray(size: Int): Array<StickyNotification?> {
            return arrayOfNulls(size)
        }
    }
}
