package fr.quentinklein.stickynotifs.widget;

import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViewsService;

import java.sql.SQLException;

/**
 * Created by quentin on 04/08/2014.
 */
public class StickyWidgetService extends RemoteViewsService {


    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        try {
            Log.i(StickyWidgetService.class.getSimpleName(), "Widget Factory creation");
            return (new StickyWidgetRemoteViewsFactory(this.getApplicationContext()));
        } catch (SQLException e) {
            Log.e(StickyWidgetService.class.getSimpleName(), "Can't create factory", e);
            return null;
        }
    }
}