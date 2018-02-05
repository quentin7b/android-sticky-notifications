package com.github.quentin7b.sn.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.github.quentin7b.sn.database.model.StickyNotification
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper
import com.j256.ormlite.dao.Dao
import com.j256.ormlite.support.ConnectionSource
import com.j256.ormlite.table.TableUtils
import java.sql.SQLException

class DatabaseHelper(context: Context) : OrmLiteSqliteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    // dao for notifications
    var database: StickyDao = StickyDao()

    override fun onCreate(database: SQLiteDatabase, connectionSource: ConnectionSource) {
        try {
            // Create table
            TableUtils.createTable(connectionSource, StickyNotification::class.java)
        } catch (e: SQLException) {
            Log.e("Database", "Can't create database", e)
            throw RuntimeException(e)
        }

    }

    override fun onUpgrade(database: SQLiteDatabase, connectionSource: ConnectionSource, oldVersion: Int, newVersion: Int) {
        try {
            if (oldVersion < 2) {
                database.execSQL("ALTER TABLE `StickyNotification` ADD COLUMN deadLine VARCHAR;")
            }
        } catch (e: Exception) {
            Log.e("Database", "Can't upgrade database", e)
        }

    }

    inner class StickyDao internal constructor() {
        private var dao: Dao<StickyNotification, Int>? = null

        val all: List<StickyNotification>
            @Synchronized get() {
                return try {
                    dao!!.queryForAll()
                } catch (e: Exception) {
                    Log.e("Database", "Can't get all notifications", e)
                    emptyList()
                }

            }

        init {
            if (dao == null) {
                dao = try {
                    getDao(StickyNotification::class.java)
                } catch (e: SQLException) {
                    Log.e("Database", "Can't get DAO", e)
                    null
                }

            }
        }

        @Synchronized
        fun one(id: Int): StickyNotification? {
            return try {
                dao!!.queryForId(id)
            } catch (e: Exception) {
                Log.e("Database", "Can't get all notifications", e)
                null
            }

        }

        @Synchronized
        fun save(stickyNotification: StickyNotification): StickyNotification {
            return try {
                if (stickyNotification.id > 0) {
                    dao!!.update(stickyNotification)
                } else {
                    val newId = dao!!.create(stickyNotification)
                    stickyNotification.id = newId
                }
                stickyNotification
            } catch (e: Exception) {
                Log.e("Database", "Can't save notification", e)
                stickyNotification
            }

        }

        @Synchronized
        fun delete(notification: StickyNotification) {
            try {
                dao!!.delete(notification)
            } catch (e: Exception) {
                Log.e("Database", "Can't delete notification", e)
            }

        }

    }

    companion object {

        // name of the database file for your application -- change to something appropriate for your app
        private val DATABASE_NAME = "stickynotes.db"
        // any time you make changes to your database objects, you may have to increase the database version
        private val DATABASE_VERSION = 2
    }


}