package cn.bandu.oreader.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import cn.bandu.oreader.dao.Cate;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table CATE.
*/
public class CateDao extends AbstractDao<Cate, Long> {

    public static final String TABLENAME = "CATE";

    /**
     * Properties of entity Cate.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Sid = new Property(0, long.class, "sid", true, "SID");
        public final static Property Name = new Property(1, String.class, "name", false, "NAME");
        public final static Property Date = new Property(2, long.class, "date", false, "DATE");
        public final static Property Sort = new Property(3, int.class, "sort", false, "SORT");
    };


    public CateDao(DaoConfig config) {
        super(config);
    }
    
    public CateDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'CATE' (" + //
                "'SID' INTEGER PRIMARY KEY ASC NOT NULL ," + // 0: sid
                "'NAME' TEXT NOT NULL ," + // 1: name
                "'DATE' INTEGER NOT NULL ," + // 2: date
                "'SORT' INTEGER NOT NULL );"); // 3: sort
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'CATE'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, Cate entity) {
        stmt.clearBindings();
        stmt.bindLong(1, entity.getSid());
        stmt.bindString(2, entity.getName());
        stmt.bindLong(3, entity.getDate());
        stmt.bindLong(4, entity.getSort());
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public Cate readEntity(Cursor cursor, int offset) {
        Cate entity = new Cate( //
            cursor.getLong(offset + 0), // sid
            cursor.getString(offset + 1), // name
            cursor.getLong(offset + 2), // date
            cursor.getInt(offset + 3) // sort
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, Cate entity, int offset) {
        entity.setSid(cursor.getLong(offset + 0));
        entity.setName(cursor.getString(offset + 1));
        entity.setDate(cursor.getLong(offset + 2));
        entity.setSort(cursor.getInt(offset + 3));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(Cate entity, long rowId) {
        entity.setSid(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(Cate entity) {
        if(entity != null) {
            return entity.getSid();
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
