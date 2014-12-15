package cn.bandu.oreader.tools;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.util.LruCache;
import android.util.Log;

import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.jakewharton.disklrucache.DiskLruCache;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import cn.bandu.oreader.OReaderApplication;
import cn.bandu.oreader.OReaderConst;

/**
 * Created by yangmingfu on 14/12/2.
 */
public class LruBitmapCache extends LruCache<String, Bitmap> implements ImageCache {



    public static int getDefaultLruCacheSize() {
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        Log.e("maxMemory", String.valueOf(Runtime.getRuntime().maxMemory()));
        final int cacheSize = maxMemory / 8;
        return cacheSize;
    }

    public LruBitmapCache() {
        this(getDefaultLruCacheSize());
        File cacheDir = OReaderApplication.getInstance().getDiskCacheDir(OReaderConst.DISK_IMAGE_CACHE_DIR);
        Log.e("CacheDir=", String.valueOf(cacheDir));
        if(!cacheDir.exists()) {
            cacheDir.mkdir();
        }
    }

    public LruBitmapCache(int sizeInKiloBytes) {
        super(sizeInKiloBytes);
    }

    @Override
    protected int sizeOf(String key, Bitmap value) {
        return value.getRowBytes() * value.getHeight() / 1024;
    }

    @Override
    public Bitmap getBitmap(String url) {
        String key = DataTools.hashKeyForDisk(url);
        Bitmap bitmap = get(url);
        Log.e("lru url bitmap", url + bitmap);
        if (bitmap == null) {
            bitmap = getBitmapFromDiskLruCache(key);
            Log.e("disk url bitmap", url + bitmap);

            if(bitmap != null) {
                put(url, bitmap);
            }
        }
        return bitmap;
    }

    @Override
    public void putBitmap(String url, Bitmap bitmap) {
        put(url, bitmap);
        String key = DataTools.hashKeyForDisk(url);
        putBitmapToDiskLruCache(key, bitmap);
    }

    /**
     * 从disk中获取图片
     * @param key
     * @return
     */
    private Bitmap getBitmapFromDiskLruCache(String key) {
        try {
            DiskLruCache.Snapshot snapshot = OReaderApplication.getInstance().getDiskLruCache(OReaderConst.DISK_IMAGE_CACHE_DIR).get(key);
            if (snapshot!=null) {
                InputStream inputStream = snapshot.getInputStream(0);
                if (inputStream != null) {
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    inputStream.close();
                    return bitmap;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     *
     * @param key
     * @param bitmap
     */
    private void putBitmapToDiskLruCache(String key, Bitmap bitmap) {
        try {
            DiskLruCache.Editor editor = OReaderApplication.getInstance().getDiskLruCache(OReaderConst.DISK_IMAGE_CACHE_DIR).edit(key);
            if(editor != null) {
                OutputStream outputStream = editor.newOutputStream(0);
                if (bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream)) {
                    editor.commit();
                } else {
                    editor.abort();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}