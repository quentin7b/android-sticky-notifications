package fr.quentinklein.stickynotifs.model.database;

import com.j256.ormlite.android.apptools.OrmLiteConfigUtil;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by quentin on 19/07/2014.
 * Database helper class used to manage the creation and upgrading of your database. This class also usually provides
 * the DAOs used by the other classes.
 */
public class DatabaseConfigUtil extends OrmLiteConfigUtil {

    public static void main(String[] args) throws SQLException, IOException {
        writeConfigFile("ormlite_config.txt");
    }
}