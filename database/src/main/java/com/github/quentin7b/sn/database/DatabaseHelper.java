package com.github.quentin7b.sn.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import com.github.quentin7b.sn.database.model.StickyNotification;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    // name of the database file for your application -- change to something appropriate for your app
    private static final String DATABASE_NAME = "stickynotes.db";
    // any time you make changes to your database objects, you may have to increase the database version
    private static final int DATABASE_VERSION = 1;
    // dao for notifications
    private StickyDao database;


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION, R.raw.ormlite_config);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            // Create table
            TableUtils.createTable(connectionSource, StickyNotification.class);
        } catch (SQLException e) {
            Log.e("Database", "Can't create database", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        // For now do nothing
    }

    /**
     * Get the Sticky Database
     *
     * @return the sticky database
     */
    public StickyDao getDatabase() {
        if (database == null) {
            database = new StickyDao();
        }
        return database;
    }

    public class StickyDao {
        private Dao<StickyNotification, Integer> dao;

        StickyDao() {
            if (dao == null) {
                try {
                    dao = getDao(StickyNotification.class);
                } catch (SQLException e) {
                    Log.e("Database", "Can't get DAO", e);
                    dao = null;
                }
            }
        }

        public synchronized List<StickyNotification> getAll() {
            try {
                return dao.queryForAll();
            } catch (Exception e) {
                Log.e("Database", "Can't get all notifications", e);
                return Collections.emptyList();
            }
        }

        public synchronized StickyNotification save(StickyNotification stickyNotification) {
            try {
                if (stickyNotification.getId() > 0) {
                    dao.update(stickyNotification);
                } else {
                    int newId = dao.create(stickyNotification);
                    stickyNotification.setId(newId);
                }
                return stickyNotification;
            } catch (Exception e) {
                Log.e("Database", "Can't save notification", e);
                return stickyNotification;
            }
        }

        public synchronized void delete(StickyNotification notification) {
            try {
                dao.delete(notification);
            } catch (Exception e) {
                Log.e("Database", "Can't delete notification", e);
            }
        }

    }


}