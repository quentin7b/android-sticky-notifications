package com.github.quentin7b.sn.ui.view

import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.support.v7.app.AlertDialog
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.widget.LinearLayout
import com.github.quentin7b.sn.R
import com.github.quentin7b.sn.Tool
import com.github.quentin7b.sn.database.model.StickyNotification
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import kotlinx.android.synthetic.main.view_note_full.view.*
import kotlinx.android.synthetic.main.view_note_list_item.view.*
import java.text.SimpleDateFormat
import java.util.*


class StickyNoteFullView : LinearLayout {

    private var date: Date? = null
    private val dateFormat: SimpleDateFormat = SimpleDateFormat(context.getString(R.string.long_date_format),
            Tool.getLocale(context))

    var notification: StickyNotification?
        get() {
            val title = if (note_title_et !== null)
                note_title_et?.text?.toString()
            else
                ""

            return StickyNotification(
                    title,
                    note_content_et?.text!!.toString(),
                    level_btn?.defcon,
                    notification_cb!!.isChecked,
                    date
            )
        }
        set(notification) {
            note_title_et?.setText(notification!!.title)
            note_title_et?.setSelection(notification?.title!!.length)

            level_btn?.defcon = notification!!.defcon

            note_content_et?.setText(notification.content)
            note_content_et?.setSelection(notification.content!!.length)

            notification_cb?.isChecked = notification.isNotification

            if (notification.deadLine !== null) {
                note_content_tv?.text = dateFormat.format(notification.deadLine)
            }
        }

    companion object EXTRA {
        const val SUPER = "superState"
        const val TITLE = "titleState"
        const val CONTENT = "contentState"
        const val DEFCON = "defconState"
        const val NOTIFICATION = "notificationState"
        const val DATE = "dateState"
    }

    constructor(context: Context) : super(context) {
        initLayout()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initLayout()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initLayout()
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        initLayout()
    }

    private fun initLayout() {
        val context = context
        orientation = LinearLayout.VERTICAL
        LayoutInflater.from(getContext()).inflate(R.layout.view_note_full, this, true)
        isSaveEnabled = true

        level_btn?.setOnClickListener { onDefconClick() }
        note_content_tv?.setOnClickListener { onDateClick() }
    }

    override fun onSaveInstanceState(): Parcelable? {
        val instanceState = Bundle()
        instanceState.putParcelable(EXTRA.SUPER, super.onSaveInstanceState())
        instanceState.putString(EXTRA.TITLE, note_title_et?.text.toString())
        instanceState.putString(EXTRA.CONTENT, note_content_et?.text.toString())
        instanceState.putBoolean(EXTRA.NOTIFICATION, notification_cb!!.isChecked)
        instanceState.putInt(EXTRA.DEFCON, level_btn?.defcon!!.describe())
        instanceState.putLong(EXTRA.DATE, if (date != null) date!!.time else -1)
        return super.onSaveInstanceState()
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is Bundle) {
            val bundle = state as Bundle?
            val notification = StickyNotification(
                    bundle!!.getString(EXTRA.TITLE, ""),
                    bundle.getString(EXTRA.CONTENT, ""),
                    StickyNotification.Defcon.from(bundle.getInt(EXTRA.DEFCON, StickyNotification.Defcon.NORMAL.describe())),
                    bundle.getBoolean(EXTRA.NOTIFICATION, true),
                    Date()
            )
            val dateLong = bundle.getLong(EXTRA.DATE)
            if (dateLong != -1L) {
                notification.deadLine = Date(dateLong)
            }
            this.notification = notification
            super.onRestoreInstanceState(bundle.getParcelable(EXTRA.SUPER))
        } else {
            super.onRestoreInstanceState(state)
        }
    }

    private fun onDefconClick() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_color_picker, null)
        val alertDialog = AlertDialog.Builder(context)
                .setView(dialogView)
                .create()

        val uselessBtn = dialogView.findViewById<View>(R.id.useless_btn) as DefconImageButton
        val normalBtn = dialogView.findViewById<View>(R.id.normal_btn) as DefconImageButton
        val importantBtn = dialogView.findViewById<View>(R.id.important_btn) as DefconImageButton
        val ultraBtn = dialogView.findViewById<View>(R.id.ultra_btn) as DefconImageButton

        uselessBtn.defcon = StickyNotification.Defcon.USELESS
        normalBtn.defcon = StickyNotification.Defcon.NORMAL
        importantBtn.defcon = StickyNotification.Defcon.IMPORTANT
        ultraBtn.defcon = StickyNotification.Defcon.ULTRA

        val colorClickListener = OnClickListener { v ->
            val imageButton = v as DefconImageButton
            level_btn.defcon = imageButton.defcon
            alertDialog.dismiss()
        }

        uselessBtn.setOnClickListener(colorClickListener)
        normalBtn.setOnClickListener(colorClickListener)
        importantBtn.setOnClickListener(colorClickListener)
        ultraBtn.setOnClickListener(colorClickListener)

        alertDialog.setCancelable(true)
        alertDialog.show()
    }

    private fun onDateClick() {
        val calendar = Calendar.getInstance()
        val currentDate = date
        if (currentDate != null) {
            calendar.time = currentDate
        }
        val dpd = DatePickerDialog.newInstance(
                { _, year, monthOfYear, dayOfMonth ->
                    val setCalendar = Calendar.getInstance()
                    setCalendar.set(Calendar.YEAR, year)
                    setCalendar.set(Calendar.MONTH, monthOfYear)
                    setCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    date = setCalendar.time
                    note_date_tv.text = dateFormat.format(date)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        )
        dpd.show((context as Activity).fragmentManager, "Datepickerdialog")
    }

}
