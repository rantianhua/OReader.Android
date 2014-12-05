package cn.bandu.oreader.dao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table FAV.
 */
public class Fav implements java.io.Serializable {

    private long sid;
    /** Not-null value. */
    private String title;
    private String description;
    private String date;
    /** Not-null value. */
    private String webUrl;
    private String image0;
    private String image1;
    private String image2;
    private Integer model;
    private long cateid;
    /** Not-null value. */
    private String cateName;
    private long createTime;

    public Fav() {
    }

    public Fav(long sid) {
        this.sid = sid;
    }

    public Fav(long sid, String title, String description, String date, String webUrl, String image0, String image1, String image2, Integer model, long cateid, String cateName, long createTime) {
        this.sid = sid;
        this.title = title;
        this.description = description;
        this.date = date;
        this.webUrl = webUrl;
        this.image0 = image0;
        this.image1 = image1;
        this.image2 = image2;
        this.model = model;
        this.cateid = cateid;
        this.cateName = cateName;
        this.createTime = createTime;
    }

    public long getSid() {
        return sid;
    }

    public void setSid(long sid) {
        this.sid = sid;
    }

    /** Not-null value. */
    public String getTitle() {
        return title;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    /** Not-null value. */
    public String getWebUrl() {
        return webUrl;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }

    public String getImage0() {
        return image0;
    }

    public void setImage0(String image0) {
        this.image0 = image0;
    }

    public String getImage1() {
        return image1;
    }

    public void setImage1(String image1) {
        this.image1 = image1;
    }

    public String getImage2() {
        return image2;
    }

    public void setImage2(String image2) {
        this.image2 = image2;
    }

    public Integer getModel() {
        return model;
    }

    public void setModel(Integer model) {
        this.model = model;
    }

    public long getCateid() {
        return cateid;
    }

    public void setCateid(long cateid) {
        this.cateid = cateid;
    }

    /** Not-null value. */
    public String getCateName() {
        return cateName;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setCateName(String cateName) {
        this.cateName = cateName;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

}
