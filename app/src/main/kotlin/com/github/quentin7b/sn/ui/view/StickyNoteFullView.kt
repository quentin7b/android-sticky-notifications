package com.github.quentin7b.sn.ui.view

import android.app.Activity
import android.os.Bundle
import android.os.Parcelable
import android.support.design.widget.TextInputEditText
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.github.quentin7b.sn.ColorHelper
import com.github.quentin7b.sn.R
import com.github.quentin7b.sn.Tool
import com.github.quentin7b.sn.database.model.StickyNotification
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.onCheckedChange
import org.jetbrains.anko.sdk25.coroutines.onClick
import java.text.SimpleDateFormat
import java.util.*


class StickyNoteFullView : Fragment() {

    companion object {
        fun newInstance(): StickyNoteFullView {
            return StickyNoteFullView()
        }
    }

    private var ui: StickyNoteFullViewUI? = null
    private var date: Date? = null
    private var dateFormat: SimpleDateFormat? = null

    var notification: StickyNotification?
        get() {
            return with(ui!!) {
                StickyNotification(
                        "",
                        content.text!!.toString(),
                        defcon,
                        isNotification,
                        date
                )
            }
        }
        set(notification) {
            with(ui!!) {
                content.setText(notification?.content)
                content.setSelection(notification?.content!!.length)
                isNotification = notification.isNotification

                if (notification.deadLine !== null) {
                    deadline.text = dateFormat?.format(notification.deadLine)
                }
            }
        }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dateFormat = SimpleDateFormat(context!!.getString(R.string.long_date_format),
                Tool.getLocale(context!!))

        ui = StickyNoteFullViewUI()
        var detailView = ui!!.createView(AnkoContext.create(context!!, this, false))

        ui!!.deadline.setOnClickListener { onDateClick() }

        return detailView
    }

    fun onSaveInstanceState(): Parcelable? {
        val instanceState = Bundle()
        instanceState.putString(EXTRA.TITLE, "")
        instanceState.putString(EXTRA.CONTENT, ui?.content?.text.toString())
        instanceState.putBoolean(EXTRA.NOTIFICATION, ui!!.isNotification)
        instanceState.putInt(EXTRA.DEFCON, ui?.defcon!!.describe())
        instanceState.putLong(EXTRA.DATE, if (date != null) date!!.time else -1)
        return instanceState
    }

    fun onRestoreInstanceState(state: Parcelable?) {
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
        }
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
                    ui!!.deadline.text = dateFormat?.format(date)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        )
        dpd.show((context as Activity).fragmentManager, "Datepickerdialog")
    }

    private object EXTRA {
        val TITLE = "titleState"
        val CONTENT = "contentState"
        val DEFCON = "defconState"
        val NOTIFICATION = "notificationState"
        val DATE = "dateState"
    }

    inner class StickyNoteFullViewUI : AnkoComponent<StickyNoteFullView> {

        lateinit var content: TextInputEditText
        var isNotification: Boolean = false
        lateinit var deadline: TextView
        var defcon: StickyNotification.Defcon = StickyNotification.Defcon.NORMAL

        override fun createView(ui: AnkoContext<StickyNoteFullView>): View {
            return with(ui) {

                verticalLayout {

                    relativeLayout {
                        padding = dip(16)

                        imageView(R.drawable.ic_attach_file_24dp) {
                            id = R.id.noteShowNotificationIcon
                        }.lparams(width = dip(24), height = dip(24)) {
                            alignParentLeft()
                            alignParentStart()

                            centerVertically()
                        }

                        checkBox {
                            onCheckedChange { _, isChecked ->
                                isNotification = isChecked
                            }
                        }.lparams(width = wrapContent, height = wrapContent) {
                            id = R.id.noteShowNotificationCheckBox

                            alignParentRight()
                            alignParentEnd()

                            centerVertically()
                        }

                        textView(R.string.hint_notification) {
                            textSize = 15f
                        }.lparams(width = matchParent, height = wrapContent) {
                            horizontalMargin = dip(32)

                            rightOf(R.id.noteShowNotificationIcon)
                            leftOf(R.id.noteShowNotificationCheckBox)

                            centerVertically()
                        }

                    }.lparams(width = matchParent, height = dip(72))

                    horizontalDivider()

                    relativeLayout {
                        padding = dip(16)

                        imageView(R.drawable.ic_color_lens_24dp) {
                            id = R.id.noteColorIcon
                        }.lparams(width = dip(24), height = dip(24)) {
                            alignParentLeft()
                            alignParentStart()

                            centerVertically()
                        }

                        labelImageButton {
                            id = R.id.noteColorButton
                            colorResIdentifier = ColorHelper.getDefconColor(defcon)
                            onClick {
                                alert {
                                    customView {
                                        linearLayout {

                                            labelImageButton {
                                                colorResIdentifier = R.color.color_useless
                                                padding = dip(5)
                                                onClick {
                                                    defcon = StickyNotification.Defcon.USELESS
                                                }
                                            }

                                            labelImageButton {
                                                colorResIdentifier = R.color.color_normal
                                                padding = dip(5)
                                                onClick {
                                                    defcon = StickyNotification.Defcon.NORMAL
                                                }
                                            }.lparams(weight = 1f)

                                            labelImageButton {
                                                colorResIdentifier = R.color.color_important
                                                padding = dip(5)
                                                onClick {
                                                    defcon = StickyNotification.Defcon.IMPORTANT
                                                }
                                            }.lparams(weight = 1f)


                                            labelImageButton {
                                                colorResIdentifier = R.color.color_ultra
                                                padding = dip(5)
                                                onClick {
                                                    defcon = StickyNotification.Defcon.ULTRA
                                                }
                                            }.lparams(weight = 1f)

                                        }
                                    }
                                }.show()
                            }
                        }.lparams(width = dip(48), height = dip(48)) {
                            alignParentRight()
                            alignParentEnd()

                            centerVertically()
                        }

                        textView(R.string.hint_color) {
                            textSize = 15f
                        }.lparams(width = matchParent, height = wrapContent) {
                            horizontalMargin = dip(32)


                            rightOf(R.id.noteColorIcon)
                            leftOf(R.id.noteColorButton)

                            centerVertically()
                        }

                    }.lparams(width = matchParent, height = dip(72))

                    horizontalDivider()

                    relativeLayout {
                        padding = dip(16)

                        imageView(R.drawable.ic_short_text_24dp) {
                            id = R.id.noteDetailsIcon
                        }.lparams(width = dip(24), height = dip(24)) {
                            alignParentStart()
                            alignParentLeft()

                            centerVertically()
                        }

                        editText {
                            hintResource = R.string.hint_details
                            textColor = R.color.primary_text
                            hintTextColor = R.color.accent
                            textSize = 15f
                        }.lparams(width = matchParent, height = wrapContent) {
                            horizontalMargin = dip(32)

                            rightOf(R.id.noteDetailsIcon)

                            centerVertically()
                        }

                    }.lparams(width = matchParent, height = wrapContent) {
                        minimumHeight = dip(72)
                    }

                    horizontalDivider()

                    relativeLayout {
                        padding = dip(16)

                        imageView(R.drawable.ic_schedule_24dp) {
                            id = R.id.noteDateIcon
                        }.lparams(width = dip(24), height = dip(24)) {
                            alignParentLeft()
                            alignParentStart()

                            centerVertically()

                        }

                        deadline = textView {
                            hintResource = R.string.hint_date
                            textColor = R.color.primary_text
                            textSize = 15f
                        }.lparams(height = wrapContent, width = matchParent) {
                            horizontalMargin = dip(32)

                            rightOf(R.id.noteDateIcon)

                            centerVertically()
                        }

                    }.lparams(width = matchParent, height = dip(72))

                }
            }
        }
    }

}
