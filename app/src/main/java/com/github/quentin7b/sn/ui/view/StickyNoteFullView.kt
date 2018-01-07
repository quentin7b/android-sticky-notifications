package com.github.quentin7b.sn.ui.view

import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.support.design.widget.TextInputEditText
import android.support.v7.app.AlertDialog
import android.support.v7.widget.AppCompatCheckBox
import android.support.v7.widget.AppCompatTextView
import android.text.TextUtils
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

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date


class StickyNoteFullView : LinearLayout {

    internal var colorRes: Int? = null

    private var dateFormat: SimpleDateFormat? = null

    var title: String?
        get() = if (note_title_et != null) {
            note_title_et!!.text.toString()
        } else {
            null
        }
        set(title) {
            if (note_title_et != null) {
                note_title_et!!.setText(title)
                note_title_et!!.setSelection(title!!.length)
            }
        }

    var defcon: StickyNotification.Defcon?
        get() = ColorHelper.getColorDefcon(colorRes)
        set(defcon) {
            colorRes = ColorHelper.getDefconColor(defcon!!)
            level_btn!!.setColorRes(colorRes!!)
        }

    var content: String
        get() = note_content_et!!.text.toString()
        set(content) {
            note_content_et!!.setText(content)
            note_content_et!!.setSelection(content.length)
        }

    var isNotification: Boolean
        get() = notification_cb!!.isChecked
        set(isNotification) {
            notification_cb!!.isChecked = isNotification
        }

    var date: Date?
        get() = if (!TextUtils.isEmpty(note_content_tv!!.text)) {
            try {
                dateFormat!!.parse(note_content_tv!!.text.toString())
            } catch (e: ParseException) {
                null
            }

        } else {
            null
        }
        set(date) {
            if (date != null) {
                note_content_tv!!.text = dateFormat!!.format(date)
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
        instanceState.putString(EXTRA.TITLE, title)
        instanceState.putString(EXTRA.CONTENT, content)
        instanceState.putBoolean(EXTRA.NOTIFICATION, isNotification)
        instanceState.putInt(EXTRA.DEFCON, defcon!!.describe())
        instanceState.putLong(EXTRA.DATE, if (date != null) date!!.time else -1)
        return super.onSaveInstanceState()
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        var state = state
        if (state is Bundle) {
            val bundle = state as Bundle?
            title = bundle!!.getString(EXTRA.TITLE, "")
            content = bundle.getString(EXTRA.CONTENT, "")
            isNotification = bundle.getBoolean(EXTRA.NOTIFICATION, true)
            defcon = StickyNotification.Defcon.from(bundle.getInt(EXTRA.DEFCON, StickyNotification.Defcon.NORMAL.describe()))
            state = bundle.getParcelable(EXTRA.SUPER)
            val dateLong = bundle.getLong(EXTRA.DATE)
            if (dateLong != -1L) {
                date = Date(dateLong)
            }
        }
        super.onRestoreInstanceState(state)
    }

    internal fun onDefconClick() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_color_picker, null)
        val alertDialog = AlertDialog.Builder(context)
                .setView(dialogView)
                .create()

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
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

        val uselessBtn = dialogView.findViewById<View>(R.id.useless_btn) as LabelImageButton
        val normalBtn = dialogView.findViewById<View>(R.id.normal_btn) as LabelImageButton
        val importantBtn = dialogView.findViewById<View>(R.id.important_btn) as LabelImageButton
        val ultraBtn = dialogView.findViewById<View>(R.id.ultra_btn) as LabelImageButton

        uselessBtn.setColorRes(ColorHelper.getDefconColor(StickyNotification.Defcon.USELESS))
        normalBtn.setColorRes(ColorHelper.getDefconColor(StickyNotification.Defcon.NORMAL))
        importantBtn.setColorRes(ColorHelper.getDefconColor(StickyNotification.Defcon.IMPORTANT))
        ultraBtn.setColorRes(ColorHelper.getDefconColor(StickyNotification.Defcon.ULTRA))

        val colorClickListener = OnClickListener { v ->
            val imageButton = v as LabelImageButton
            this@StickyNoteFullView.colorRes = imageButton.getColorRes()
            alertDialog.dismiss()
        }

        uselessBtn.setOnClickListener(colorClickListener)
        normalBtn.setOnClickListener(colorClickListener)
        importantBtn.setOnClickListener(colorClickListener)
        ultraBtn.setOnClickListener(colorClickListener)

        alertDialog.setOnDismissListener { this@StickyNoteFullView.level_btn!!.setColorRes(this@StickyNoteFullView.colorRes!!) }

        alertDialog.setCancelable(true)
        alertDialog.show()
    }

    internal fun onDateClick() {
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
