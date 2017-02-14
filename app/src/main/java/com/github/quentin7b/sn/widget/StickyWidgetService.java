package com.github.quentin7b.sn.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

import java.sql.SQLException;

/**
 */
public class StickyWidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        try {
            return new StickyWidgetRemoteViewsFactory(getApplicationContext());
        } catch (SQLException e) {
            return null;
        }
    }
}