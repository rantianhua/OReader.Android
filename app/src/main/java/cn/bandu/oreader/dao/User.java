package cn.bandu.oreader.dao;

/**
 * Created by yangmingfu on 14/12/23.
 */
public class User {
    private long id;
    private long uid;
    private String name;
    private String avatar;


    public User() {

    }

    public User(Long id, Long uid, String name, String avatar) {
        this.id = id;
        this.uid = uid;
        this.name = name;
        this.avatar = avatar;
    }

    public long getId() {
        return id;
    }

    public long getUid() {
        return uid;
    }

    public String getName() {
        return name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
