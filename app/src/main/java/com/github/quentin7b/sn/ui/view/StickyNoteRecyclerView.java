package com.github.quentin7b.sn.ui.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.quentin7b.sn.ColorHelper;
import com.github.quentin7b.sn.R;
import com.github.quentin7b.sn.database.model.StickyNotification;

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

    public void setNoteSelectionListener(NoteSelectedListener listener) {
        this.adapter.setNoteSelectionListener(listener);
    }

    public void setNotificationsList(List<StickyNotification> notificationsList) {
        this.adapter.setNotificationList(notificationsList);
    }

    public interface NoteSelectedListener {
        void onNoteSelected(StickyNotification note);
    }

    private interface NoteSelectedListenerProvider {
        NoteSelectedListener getListener();
    }

    private static class StickyAdapter
            extends RecyclerView.Adapter<StickyAdapter.Holder>
            implements NoteSelectedListenerProvider {

        private List<StickyNotification> notificationList;
        private NoteSelectedListener noteSelectionListener;

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
            holder.bind(notificationList.get(position), this);
        }

        @Override
        public int getItemCount() {
            return notificationList.size();
        }

        void setNotificationList(List<StickyNotification> stickyNotifications) {
            this.notificationList.clear();
            this.notificationList.addAll(stickyNotifications);
            notifyDataSetChanged();
        }

        void setNoteSelectionListener(NoteSelectedListener noteSelectionListener) {
            this.noteSelectionListener = noteSelectionListener;
        }

        @Override
        public NoteSelectedListener getListener() {
            return noteSelectionListener;
        }

        static class Holder extends RecyclerView.ViewHolder {

            private View rootView;
            private AppCompatTextView titleTextView;
            private AppCompatTextView contentTextView;
            private LabelImageView colorIv;
            private AppCompatImageView isNotificationIv;

            Holder(View itemView) {
                super(itemView);
                this.rootView = itemView;
                this.titleTextView = ButterKnife.findById(itemView, R.id.note_title_tv);
                this.contentTextView = ButterKnife.findById(itemView, R.id.note_description_tv);
                this.colorIv = ButterKnife.findById(itemView, R.id.note_color);
                this.isNotificationIv = ButterKnife.findById(itemView, R.id.note_notif_iv);
            }

            void bind(final StickyNotification stickyNotification,
                      final NoteSelectedListenerProvider listenerProvider) {
                this.titleTextView.setText(stickyNotification.getTitle());
                this.contentTextView.setText(stickyNotification.getContent());
                this.rootView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listenerProvider.getListener().onNoteSelected(stickyNotification);
                    }
                });

                this.colorIv.setColorRes(ColorHelper.getDefconColor(stickyNotification.getDefcon()));

                if (!stickyNotification.isNotification()) {
                    this.isNotificationIv.setImageDrawable(null);
                } else {
                    this.isNotificationIv.setImageResource(R.drawable.ic_attach_file_24dp);
                }
            }
        }
    }
}
