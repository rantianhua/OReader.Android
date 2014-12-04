package cn.bandu.oreader.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import cn.bandu.oreader.dao.Fav;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table FAV.
*/
public class FavDao extends AbstractDao<Fav, Long> {

    public static final String TABLENAME = "FAV";

    /**
     * Properties of entity Fav.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Sid = new Property(1, long.class, "sid", false, "SID");
        public final static Property Title = new Property(2, String.class, "title", false, "TITLE");
        public final static Property Description = new Property(3, String.class, "description", false, "DESCRIPTION");
        public final static Property Date = new Property(4, String.class, "date", false, "DATE");
        public final static Property WebUrl = new Property(5, String.class, "webUrl", false, "WEB_URL");
        public final static Property Image0 = new Property(6, String.class, "image0", false, "IMAGE0");
        public final static Property Image1 = new Property(7, String.class, "image1", false, "IMAGE1");
        public final static Property Image2 = new Property(8, String.class, "image2", false, "IMAGE2");
        public final static Property Model = new Property(9, Integer.class, "model", false, "MODEL");
    };


    public FavDao(DaoConfig config) {
        super(config);
    }
    
    public FavDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'FAV' (" + //
                "'_id' INTEGER PRIMARY KEY ," + // 0: id
                "'SID' INTEGER NOT NULL ," + // 1: sid
                "'TITLE' TEXT NOT NULL ," + // 2: title
                "'DESCRIPTION' TEXT," + // 3: description
                "'DATE' TEXT," + // 4: date
                "'WEB_URL' TEXT NOT NULL ," + // 5: webUrl
                "'IMAGE0' TEXT," + // 6: image0
                "'IMAGE1' TEXT," + // 7: image1
                "'IMAGE2' TEXT," + // 8: image2
                "'MODEL' INTEGER);"); // 9: model
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'FAV'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, Fav entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindLong(2, entity.getSid());
        stmt.bindString(3, entity.getTitle());
 
        String description = entity.getDescription();
        if (description != null) {
            stmt.bindString(4, description);
        }
 
        String date = entity.getDate();
        if (date != null) {
            stmt.bindString(5, date);
        }
        stmt.bindString(6, entity.getWebUrl());
 
        String image0 = entity.getImage0();
        if (image0 != null) {
            stmt.bindString(7, image0);
        }
 
        String image1 = entity.getImage1();
        if (image1 != null) {
            stmt.bindString(8, image1);
        }
 
        String image2 = entity.getImage2();
        if (image2 != null) {
            stmt.bindString(9, image2);
        }
 
        Integer model = entity.getModel();
        if (model != null) {
            stmt.bindLong(10, model);
        }
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public Fav readEntity(Cursor cursor, int offset) {
        Fav entity = new Fav( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getLong(offset + 1), // sid
            cursor.getString(offset + 2), // title
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // description
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // date
            cursor.getString(offset + 5), // webUrl
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // image0
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // image1
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // image2
            cursor.isNull(offset + 9) ? null : cursor.getInt(offset + 9) // model
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, Fav entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setSid(cursor.getLong(offset + 1));
        entity.setTitle(cursor.getString(offset + 2));
        entity.setDescription(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setDate(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setWebUrl(cursor.getString(offset + 5));
        entity.setImage0(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setImage1(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setImage2(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setModel(cursor.isNull(offset + 9) ? null : cursor.getInt(offset + 9));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(Fav entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(Fav entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
}
