package erik.com.example.news.api.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Element {
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("assets")
    @Expose
    private List<Asset> assets = null;
    @SerializedName("imageTypeData")
    @Expose
    private ImageTypeData imageTypeData;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Asset> getAssets() {
        return assets;
    }

    public void setAssets(List<Asset> assets) {
        this.assets = assets;
    }

    public ImageTypeData getImageTypeData() {
        return imageTypeData;
    }

    public void setImageTypeData(ImageTypeData imageTypeData) {
        this.imageTypeData = imageTypeData;
    }
}
