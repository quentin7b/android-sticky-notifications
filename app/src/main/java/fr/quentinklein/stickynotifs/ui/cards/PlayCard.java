package fr.quentinklein.stickynotifs.ui.cards;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.PopupMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.fima.cardsui.objects.RecyclableCard;

import fr.quentinklein.stickynotifs.R;

/**
 * Created by quentin on 20/07/2014.
 * Copied from Github with cards ui
 */
public class PlayCard extends RecyclableCard {

    private PlayCardMenuListener listener;
    private Context context;

    public PlayCard(Context context, String titlePlay, String description, String color,
                    String titleColor, Boolean hasOverflow, Boolean isClickable, PlayCardMenuListener listener) {
        super(titlePlay, description, color, titleColor, hasOverflow,
                isClickable);
        this.listener = listener;
        this.context = context;
    }

    public PlayCard(String titlePlay, String description, String color,
                    String titleColor, Boolean hasOverflow, Boolean isClickable) {
        super(titlePlay, description, color, titleColor, hasOverflow,
                isClickable);
    }

    @Override
    protected int getCardLayoutId() {
        return R.layout.card_play;
    }

    @Override
    protected void applyTo(View convertView) {
        ((TextView) convertView.findViewById(R.id.title)).setText(titlePlay);
        ((TextView) convertView.findViewById(R.id.title)).setTextColor(Color
                .parseColor(titleColor));
        ((TextView) convertView.findViewById(R.id.description))
                .setText(description);
        ((ImageView) convertView.findViewById(R.id.stripe))
                .setBackgroundColor(Color.parseColor(color));

        if (isClickable) {
            convertView.findViewById(R.id.contentLayout).setBackgroundResource(R.drawable.selectable_background_cardbank);
        }

        convertView.findViewById(R.id.overflow).setVisibility(hasOverflow ? View.VISIBLE : View.GONE);
        if (hasOverflow) {
            // Popup menu on the overflow button : display special menu on the card
            convertView.findViewById(R.id.overflow).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popup = new PopupMenu(context, v);
                    MenuInflater inflater = popup.getMenuInflater();
                    inflater.inflate(R.menu.card_action, popup.getMenu());
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            switch (menuItem.getItemId()) {
                                case R.id.action_edit:
                                    listener.editClicked();
                                    return true;
                                case R.id.action_delete:
                                    listener.deleteClicked();
                                    return true;
                                default:
                                    return false;
                            }
                        }
                    });
                    popup.show();
                }
            });
        }
    }

    /**
     * Listener for overflow menu on a card
     */
    public static interface PlayCardMenuListener {
        public void editClicked();

        public void deleteClicked();
    }
}
