package cn.bandu.oreader.dao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table CATE.
 */
public class Cate implements java.io.Serializable {

    private long sid;
    /** Not-null value. */
    private String name;

    public Cate() {
    }

    public Cate(long sid) {
        this.sid = sid;
    }

    public Cate(long sid, String name) {
        this.sid = sid;
        this.name = name;
    }

    public long getSid() {
        return sid;
    }

    public void setSid(long sid) {
        this.sid = sid;
    }

    /** Not-null value. */
    public String getName() {
        return name;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setName(String name) {
        this.name = name;
    }

}
