package erik.com.example.news.database.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "news")
public class NewsDb {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "news_title")
    public String newsTitle;

    @ColumnInfo(name = "news_category")
    public String newsCategory;

    @ColumnInfo (name = "news_description")
    public String newsDescription;

    @ColumnInfo(name = "news_image", typeAffinity = ColumnInfo.BLOB)
    private byte[] newsImage;

    public String getNewsTitle() {
        return newsTitle;
    }

    public String getNewsCategory() {
        return newsCategory;
    }

    public String getNewsDescription() {
        return newsDescription;
    }

    public byte[] getNewsImage() {
        return newsImage;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setNewsTitle(String newsTitle) {
        this.newsTitle = newsTitle;
    }

    public void setNewsCategory(String newsCategory) {
        this.newsCategory = newsCategory;
    }

    public void setNewsDescription(String newsDescription) {
        this.newsDescription = newsDescription;
    }

    public void setNewsImage(byte[] newsImage) {
        this.newsImage = newsImage;
    }
}


