package fr.quentinklein.stickynotifs.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.fima.cardsui.objects.CardStack;
import com.fima.cardsui.views.CardUI;
import com.j256.ormlite.dao.Dao;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.OrmLiteDao;
import org.androidannotations.annotations.ViewById;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import fr.quentinklein.stickynotifs.NotificationHelper;
import fr.quentinklein.stickynotifs.R;
import fr.quentinklein.stickynotifs.model.StickyNotification;
import fr.quentinklein.stickynotifs.model.database.DatabaseHelper;
import fr.quentinklein.stickynotifs.ui.cards.PlayCard;
import fr.quentinklein.stickynotifs.ui.listeners.HideNoteListener;
import fr.quentinklein.stickynotifs.ui.listeners.NoteChanedListener;

/**
 * Created by quentin on 20/07/2014.
 */
@EFragment(R.layout.fragment_list_notes)
public class NotesListFragment extends Fragment {
    @OrmLiteDao(helper = DatabaseHelper.class, model = StickyNotification.class)
    Dao<StickyNotification, Integer> stickyNotificationDao;

    @ViewById(R.id.cardsview)
    CardUI cardsView;

    @Bean
    NotificationHelper notificationHelper;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        refreshNotesList();
    }

    public void refreshNotesList() {
        List<StickyNotification> stickyNotifications = new ArrayList<StickyNotification>(0);
        try {
            stickyNotifications = stickyNotificationDao.queryForAll();
        } catch (SQLException e) {
            Log.e(NotesListFragment.class.getSimpleName(), "Errow while requesting notes", e);
        }
        if (stickyNotifications.size() == 0) {
            // hide detail
            if (getActivity() instanceof HideNoteListener) {
                ((HideNoteListener) getActivity()).hideNote();
            }
        }
        List<StickyNotification> urgentNotifications = NotificationHelper.getDefconsNotifications(stickyNotifications, StickyNotification.Defcon.ULTRA);
        List<StickyNotification> importantNotifications = NotificationHelper.getDefconsNotifications(stickyNotifications, StickyNotification.Defcon.IMPORTANT);
        List<StickyNotification> normalNotifications = NotificationHelper.getDefconsNotifications(stickyNotifications, StickyNotification.Defcon.NORMAL);
        List<StickyNotification> uselessNotifications = NotificationHelper.getDefconsNotifications(stickyNotifications, StickyNotification.Defcon.USELESS);

        cardsView.clearCards();

        fillStackNotifications(urgentNotifications, getString(R.string.ultra));
        fillStackNotifications(importantNotifications, getString(R.string.important));
        fillStackNotifications(normalNotifications, getString(R.string.normal));
        fillStackNotifications(uselessNotifications, getString(R.string.useless));

        cardsView.setSwipeable(false);
        cardsView.refresh();
    }

    private void fillStackNotifications(List<StickyNotification> notifications, String stackName) {
        if (notifications != null && notifications.size() > 0) {
            CardStack stack = new CardStack(stackName);
            cardsView.addStack(stack);
            for (final StickyNotification stickyNotification : notifications) {
                boolean twoPart = false;
                if (getActivity() instanceof TwoPartProvider) {
                    twoPart = ((TwoPartProvider) getActivity()).isTwoPartMode();
                }
                PlayCard card;
                if (!twoPart) {
                    card = new PlayCard(getActivity(), stickyNotification.getTitle(),
                            stickyNotification.getContent(), stickyNotification.getHexColor(),
                            stickyNotification.getHexColor(), true, false, new PlayCard.PlayCardMenuListener() {
                        @Override
                        public void editClicked() {
                            if (getActivity() instanceof NoteChanedListener) {
                                ((NoteChanedListener) getActivity()).noteSelected(stickyNotification.getId());
                            }
                        }

                        @Override
                        public void deleteClicked() {
                            try {
                                stickyNotification.delete();
                                Toast.makeText(getActivity(), R.string.note_deleted, Toast.LENGTH_SHORT).show();
                                notificationHelper.hideAll();
                                NotesListFragment.this.refreshNotesList();
                            } catch (SQLException e) {
                                Log.e(NoteFragment.class.getSimpleName(), "Error while deleting note", e);
                                Toast.makeText(getActivity(), R.string.note_deleted_error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    );
                } else {
                    card = new PlayCard(stickyNotification.getTitle(),
                            stickyNotification.getContent(), stickyNotification.getHexColor(),
                            stickyNotification.getHexColor(), false, true);
                    card.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (getActivity() instanceof NoteChanedListener) {
                                ((NoteChanedListener) getActivity()).noteSelected(stickyNotification.getId());
                            }
                        }
                    });
                }
                cardsView.addCard(card, true);
                if (stickyNotification.isNotification()) {
                    notificationHelper.showNotification(stickyNotification.getId());
                } else {
                    notificationHelper.hideNotification(stickyNotification.getId());
                }
            }
        }
    }

    public static interface TwoPartProvider {
        public boolean isTwoPartMode();
    }
}
