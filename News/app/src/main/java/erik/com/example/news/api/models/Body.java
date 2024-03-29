package erik.com.example.news.api.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Body {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("bodyHtml")
    @Expose
    private String bodyHtml;
    @SerializedName("bodyTextSummary")
    @Expose
    private String bodyTextSummary;
    @SerializedName("attributes")
    @Expose
    private Attributes_ attributes;
    @SerializedName("published")
    @Expose
    private Boolean published;
    @SerializedName("createdDate")
    @Expose
    private String createdDate;
    @SerializedName("firstPublishedDate")
    @Expose
    private String firstPublishedDate;
    @SerializedName("publishedDate")
    @Expose
    private String publishedDate;
    @SerializedName("lastModifiedDate")
    @Expose
    private String lastModifiedDate;
    @SerializedName("contributors")
    @Expose
    private List<Object> contributors = null;
    @SerializedName("elements")
    @Expose
    private List<Element_> elements = null;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBodyHtml() {
        return bodyHtml;
    }

    public void setBodyHtml(String bodyHtml) {
        this.bodyHtml = bodyHtml;
    }

    public String getBodyTextSummary() {
        return bodyTextSummary;
    }

    public void setBodyTextSummary(String bodyTextSummary) {
        this.bodyTextSummary = bodyTextSummary;
    }

    public Attributes_ getAttributes() {
        return attributes;
    }

    public void setAttributes(Attributes_ attributes) {
        this.attributes = attributes;
    }

    public Boolean getPublished() {
        return published;
    }

    public void setPublished(Boolean published) {
        this.published = published;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getFirstPublishedDate() {
        return firstPublishedDate;
    }

    public void setFirstPublishedDate(String firstPublishedDate) {
        this.firstPublishedDate = firstPublishedDate;
    }

    public String getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(String publishedDate) {
        this.publishedDate = publishedDate;
    }

    public String getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(String lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public List<Object> getContributors() {
        return contributors;
    }

    public void setContributors(List<Object> contributors) {
        this.contributors = contributors;
    }

    public List<Element_> getElements() {
        return elements;
    }

    public void setElements(List<Element_> elements) {
        this.elements = elements;
    }

}
