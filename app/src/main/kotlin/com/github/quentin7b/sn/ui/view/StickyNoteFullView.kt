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
import android.view.ViewAnimationUtils
import android.widget.LinearLayout

import com.github.quentin7b.sn.ColorHelper
import com.github.quentin7b.sn.R
import com.github.quentin7b.sn.Tool
import com.github.quentin7b.sn.database.model.StickyNotification
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import kotlinx.android.synthetic.main.view_note_full.view.*

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date


class StickyNoteFullView : LinearLayout {

    private var date: Date? = null
    private var dateFormat: SimpleDateFormat? = null

    var notification: StickyNotification?
        get() {
            val title = if (note_title_et !== null)
                note_title_et?.text?.toString()
            else
                ""

            return StickyNotification(
                    title,
                    note_content_et?.text!!.toString(),
                    ColorHelper.getColorDefcon(level_btn?.colorResIdentifier),
                    notification_cb!!.isChecked,
                    date
            )
        }
        set(notification) {
            note_title_et?.setText(notification!!.title)
            note_title_et?.setSelection(notification?.title!!.length)

            level_btn?.colorResIdentifier = ColorHelper.getDefconColor(notification!!.defcon)

            note_content_et?.setText(notification.content)
            note_content_et?.setSelection(notification.content!!.length)

            notification_cb?.isChecked = notification.isNotification

            if (notification.deadLine !== null) {
                note_content_tv?.text = dateFormat?.format(notification.deadLine)
            }
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
        dateFormat = SimpleDateFormat(context.getString(R.string.long_date_format),
                Tool.getLocale(context))

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
        instanceState.putInt(EXTRA.DEFCON, ColorHelper.getColorDefcon(level_btn?.colorResIdentifier).describe())
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

        addRevealAnimation(alertDialog, dialogView)

        val uselessBtn = dialogView.findViewById<View>(R.id.useless_btn) as LabelImageButton
        val normalBtn = dialogView.findViewById<View>(R.id.normal_btn) as LabelImageButton
        val importantBtn = dialogView.findViewById<View>(R.id.important_btn) as LabelImageButton
        val ultraBtn = dialogView.findViewById<View>(R.id.ultra_btn) as LabelImageButton

        uselessBtn.colorResIdentifier = ColorHelper.getDefconColor(StickyNotification.Defcon.USELESS)
        normalBtn.colorResIdentifier = ColorHelper.getDefconColor(StickyNotification.Defcon.NORMAL)
        importantBtn.colorResIdentifier = ColorHelper.getDefconColor(StickyNotification.Defcon.IMPORTANT)
        ultraBtn.colorResIdentifier = ColorHelper.getDefconColor(StickyNotification.Defcon.ULTRA)

        val colorClickListener = OnClickListener { v ->
            val imageButton = v as LabelImageButton
            level_btn.colorResIdentifier = imageButton.colorResIdentifier
            alertDialog.dismiss()
        }

        uselessBtn.setOnClickListener(colorClickListener)
        normalBtn.setOnClickListener(colorClickListener)
        importantBtn.setOnClickListener(colorClickListener)
        ultraBtn.setOnClickListener(colorClickListener)

        alertDialog.setCancelable(true)
        alertDialog.show()
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun addRevealAnimation(alertDialog: AlertDialog, dialogView: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            alertDialog.setOnShowListener {
                val view = dialogView.findViewById<View>(R.id.reveal_view)
                val w = view.width
                val h = view.height
                val maxRadius = Math.sqrt((w * w / 2 + h * h / 2).toDouble()).toFloat()
                val revealAnimator = ViewAnimationUtils.createCircularReveal(view,
                        w, h / 2, 0f, maxRadius)
                view.visibility = View.VISIBLE
                revealAnimator.start()
            }
        }
    }

    private fun onDateClick() {
        val calendar = Calendar.getInstance()
        val currentDate = date
        if (currentDate != null) {
            calendar.time = currentDate
        }
        val dpd = DatePickerDialog.newInstance(
                { view, year, monthOfYear, dayOfMonth ->
                    val setCalendar = Calendar.getInstance()
                    setCalendar.set(Calendar.YEAR, year)
                    setCalendar.set(Calendar.MONTH, monthOfYear)
                    setCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    date = setCalendar.time
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        )
        dpd.show((context as Activity).fragmentManager, "Datepickerdialog")
    }

    private object EXTRA {
        val SUPER = "superState"
        val TITLE = "titleState"
        val CONTENT = "contentState"
        val DEFCON = "defconState"
        val NOTIFICATION = "notificationState"
        val DATE = "dateState"
    }

}
