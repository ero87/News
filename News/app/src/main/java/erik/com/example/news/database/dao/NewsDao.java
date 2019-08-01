package erik.com.example.news.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import erik.com.example.news.database.entities.NewsDb;

@Dao
public interface NewsDao {
    @Insert
    void insert(NewsDb... person);

    @Update
    void update(NewsDb... person);

    @Delete
    void delete(NewsDb... person);

    @Query("Select * FROM news")
    NewsDb[] loadAll();

    @Query("SELECT * FROM news WHERE id = :newsId ")
    NewsDb loadNewsById(final int newsId);

    @Query("SELECT id FROM news WHERE news_title = :newsTitle ")
    int getNewsIdByTitle(final String newsTitle);
}
