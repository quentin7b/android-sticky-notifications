package com.github.quentin7b.sn.ui.view

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.quentin7b.sn.R
import com.github.quentin7b.sn.Tool
import com.github.quentin7b.sn.database.model.StickyNotification
import kotlinx.android.synthetic.main.view_note_list_item.view.*
import java.text.SimpleDateFormat
import java.util.*


class StickyNoteRecyclerView : RecyclerView {

    private var adapter: StickyAdapter = StickyAdapter()

    constructor(context: Context) : super(context) {
        initLayout()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initLayout()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        initLayout()
    }

    private fun initLayout() {
        val manager = LinearLayoutManager(context)
        manager.orientation = LinearLayoutManager.VERTICAL
        super.setLayoutManager(manager)
        val divider = DividerItemDecoration(context, manager.orientation)
        divider.setDrawable(ContextCompat.getDrawable(context, R.drawable.divider_grey)!!)
        super.addItemDecoration(divider)
        setAdapter(adapter)
    }

    fun setNoteListener(listener: NoteListener) {
        adapter.setNoteSelectionListener(listener)
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int) {
                listener.onNoteSwiped(adapter.getItemAt(viewHolder.adapterPosition))
            }
        }).attachToRecyclerView(this)
    }

    fun setNotificationsList(notificationsList: List<StickyNotification>) {
        this.adapter.setNotificationList(notificationsList)
    }

    interface NoteListener {
        fun onNoteSelected(note: StickyNotification)

        fun onNoteSwiped(note: StickyNotification)
    }

    private interface NoteSelectedListenerProvider {
        val listener: NoteListener?
    }

    private class StickyAdapter internal constructor() : RecyclerView.Adapter<StickyAdapter.Holder>(), NoteSelectedListenerProvider {
        override fun getItemCount(): Int {
            return notificationList.size
        }

        private val notificationList: MutableList<StickyNotification>
        override var listener: NoteListener? = null
            private set

        init {
            this.notificationList = ArrayList(0)
            listener = null
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
            return Holder(
                    LayoutInflater
                            .from(parent.context)
                            .inflate(R.layout.view_note_list_item, parent, false)
            )
        }

        override fun onBindViewHolder(holder: Holder, position: Int) {
            holder.bind(getItemAt(position), this)
        }

        internal fun getItemAt(position: Int): StickyNotification {
            return notificationList[position]
        }

        internal fun setNotificationList(stickyNotifications: List<StickyNotification>) {
            this.notificationList.clear()
            this.notificationList.addAll(stickyNotifications)
            notifyDataSetChanged()
        }

        internal fun setNoteSelectionListener(noteSelectionListener: NoteListener) {
            this.listener = noteSelectionListener
        }

        internal class Holder(rootView: View) : RecyclerView.ViewHolder(rootView) {

            private val dateFormat: SimpleDateFormat = SimpleDateFormat(rootView.context.getString(R.string.date_format),
                    Tool.getLocale(rootView.context))

            fun bind(stickyNotification: StickyNotification,
                     listenerProvider: NoteSelectedListenerProvider) {
                itemView.setOnClickListener { listenerProvider.listener!!.onNoteSelected(stickyNotification) }

                itemView.note_title_tv.text = stickyNotification.title

                itemView.note_color.defcon = stickyNotification.defcon

                if (!stickyNotification.isNotification) {
                    itemView.note_notif_iv.setImageDrawable(null)
                } else {
                    itemView.note_notif_iv.setImageResource(R.drawable.ic_attach_file_24dp)
                }

                if (stickyNotification.content.isEmpty()) {
                    itemView.note_description_tv.visibility = GONE
                } else {
                    itemView.note_description_tv.visibility = VISIBLE
                    itemView.note_description_tv.text = stickyNotification.content
                }

                if (stickyNotification.deadLine == null) {
                    itemView.note_date_tv.visibility = GONE
                } else {
                    itemView.note_date_tv.visibility = VISIBLE
                    itemView.note_date_tv.text = dateFormat.format(stickyNotification.deadLine)
                }
            }
        }
    }
}
