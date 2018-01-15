package com.github.quentin7b.sn.widget

import android.content.Intent
import android.widget.RemoteViewsService

import java.sql.SQLException

class StickyWidgetService : RemoteViewsService() {

    override fun onGetViewFactory(intent: Intent): RemoteViewsService.RemoteViewsFactory {
        return StickyWidgetRemoteViewsFactory(applicationContext)
    }
}