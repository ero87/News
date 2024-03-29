package erik.com.example.news.api.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Blocks {

    @SerializedName("main")
    @Expose
    private Main main;
    @SerializedName("body")
    @Expose
    private List<Body> body = null;
    @SerializedName("totalBodyBlocks")
    @Expose
    private Integer totalBodyBlocks;

    public Main getMain() {
        return main;
    }

    public void setMain(Main main) {
        this.main = main;
    }

    public List<Body> getBody() {
        return body;
    }

    public void setBody(List<Body> body) {
        this.body = body;
    }

    public Integer getTotalBodyBlocks() {
        return totalBodyBlocks;
    }

    public void setTotalBodyBlocks(Integer totalBodyBlocks) {
        this.totalBodyBlocks = totalBodyBlocks;
    }

}
