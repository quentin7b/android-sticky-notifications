package com.github.quentin7b.sn.ui.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.quentin7b.sn.ColorHelper;
import com.github.quentin7b.sn.R;
import com.github.quentin7b.sn.Tool;
import com.github.quentin7b.sn.database.model.StickyNotification;
import com.github.quentin7b.sn.ui.MainActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

public class StickyNoteRecyclerView extends RecyclerView {

    private StickyAdapter adapter;

    public StickyNoteRecyclerView(Context context) {
        super(context);
        initLayout();
    }

    public StickyNoteRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initLayout();
    }

    public StickyNoteRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initLayout();
    }

    private void initLayout() {
        Context context = getContext();
        LinearLayoutManager manager = new LinearLayoutManager(context);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        super.setLayoutManager(manager);
        DividerItemDecoration divider = new DividerItemDecoration(context, manager.getOrientation());
        divider.setDrawable(ContextCompat.getDrawable(context, R.drawable.divider_grey));
        super.addItemDecoration(divider);
        this.adapter = new StickyAdapter();
        setAdapter(this.adapter);
    }

    public void setNoteListener(final NoteListener listener) {
        this.adapter.setNoteSelectionListener(listener);
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                listener.onNoteSwiped(adapter.getItemAt(viewHolder.getAdapterPosition()));
            }
        }).attachToRecyclerView(this);
    }

    public void setNotificationsList(List<StickyNotification> notificationsList) {
        this.adapter.setNotificationList(notificationsList);
    }

    public interface NoteListener {
        void onNoteSelected(StickyNotification note);

        void onNoteSwiped(StickyNotification note);
    }

    private interface NoteSelectedListenerProvider {
        NoteListener getListener();
    }

    private static class StickyAdapter
            extends RecyclerView.Adapter<StickyAdapter.Holder>
            implements NoteSelectedListenerProvider {

        private List<StickyNotification> notificationList;
        private NoteListener noteSelectionListener;

        StickyAdapter() {
            this.notificationList = new ArrayList<>(0);
            noteSelectionListener = null;
        }

        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new Holder(
                    LayoutInflater
                            .from(parent.getContext())
                            .inflate(R.layout.view_note_list_item, parent, false)
            );
        }

        @Override
        public void onBindViewHolder(Holder holder, int position) {
            holder.bind(getItemAt(position), this);
        }

        @Override
        public int getItemCount() {
            return notificationList.size();
        }

        private StickyNotification getItemAt(int position) {
            return notificationList.get(position);
        }

        void setNotificationList(List<StickyNotification> stickyNotifications) {
            this.notificationList.clear();
            this.notificationList.addAll(stickyNotifications);
            notifyDataSetChanged();
        }

        void setNoteSelectionListener(NoteListener noteSelectionListener) {
            this.noteSelectionListener = noteSelectionListener;
        }

        @Override
        public NoteListener getListener() {
            return noteSelectionListener;
        }

        static class Holder extends RecyclerView.ViewHolder {

            private SimpleDateFormat dateFormat;

            private View rootView;
            private AppCompatTextView titleTextView;
            private AppCompatTextView contentTextView;
            private AppCompatTextView dateTextView;
            private LabelImageView colorIv;
            private AppCompatImageView isNotificationIv;

            Holder(View itemView) {
                super(itemView);
                this.rootView = itemView;
                this.titleTextView = ButterKnife.findById(itemView, R.id.note_title_tv);
                this.contentTextView = ButterKnife.findById(itemView, R.id.note_description_tv);
                this.colorIv = ButterKnife.findById(itemView, R.id.note_color);
                this.isNotificationIv = ButterKnife.findById(itemView, R.id.note_notif_iv);
                this.dateTextView = ButterKnife.findById(itemView, R.id.note_date_tv);


                Context context = itemView.getContext();
                dateFormat = new SimpleDateFormat(context.getString(R.string.date_format),
                        Tool.getLocale(context));
            }

            void bind(final StickyNotification stickyNotification,
                      final NoteSelectedListenerProvider listenerProvider) {
                titleTextView.setText(stickyNotification.getTitle());
                rootView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listenerProvider.getListener().onNoteSelected(stickyNotification);
                    }
                });

                colorIv.setColorRes(ColorHelper.getDefconColor(stickyNotification.getDefcon()));

                if (!stickyNotification.isNotification()) {
                    isNotificationIv.setImageDrawable(null);
                } else {
                    isNotificationIv.setImageResource(R.drawable.ic_attach_file_24dp);
                }

                if (stickyNotification.getContent().isEmpty()) {
                    contentTextView.setVisibility(GONE);
                } else {
                    contentTextView.setVisibility(VISIBLE);
                    contentTextView.setText(stickyNotification.getContent());
                }

                if (stickyNotification.getDeadLine() == null) {
                    dateTextView.setVisibility(GONE);
                } else {
                    dateTextView.setVisibility(VISIBLE);
                    dateTextView.setText(dateFormat.format(stickyNotification.getDeadLine()));
                }
            }
        }
    }
}
