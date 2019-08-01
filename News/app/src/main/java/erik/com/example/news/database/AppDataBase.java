package erik.com.example.news.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import erik.com.example.news.database.dao.NewsDao;
import erik.com.example.news.database.entities.NewsDb;

@Database(entities = {NewsDb.class}, version = 1, exportSchema = false)
public abstract class AppDataBase extends RoomDatabase {


    private static final String APP_DATABASE_NAME = "news-db";

    private static AppDataBase instance;

    public abstract NewsDao newsDao();

    /**
     * Gets news database (designed using Room architecture).
     *
     * @param context For accessing application context
     * @return NewsDb database
     */
    public static AppDataBase getAppDatabase(final Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    AppDataBase.class, APP_DATABASE_NAME)
                    .allowMainThreadQueries()
                    .build();
        }
        return instance;
    }
}
