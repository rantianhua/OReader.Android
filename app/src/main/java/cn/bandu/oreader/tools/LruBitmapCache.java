package cn.bandu.oreader.tools;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v4.util.LruCache;
import android.util.Log;

import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.jakewharton.disklrucache.DiskLruCache;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import cn.bandu.oreader.OReaderApplication;

/**
 * Created by yangmingfu on 14/12/2.
 */
public class LruBitmapCache extends LruCache<String, Bitmap> implements ImageCache {

    DiskLruCache diskLruCache;
    String DISK_CACHE_DIR = "image";
    final long DISK_MAX_SIZE = 10 * 1024 * 1024;

    public static int getDefaultLruCacheSize() {
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        Log.e("maxMemory", String.valueOf(Runtime.getRuntime().maxMemory()));
        final int cacheSize = maxMemory / 8;

        return cacheSize;
    }

    public LruBitmapCache() {
        this(getDefaultLruCacheSize());
        File cacheDir = getDiskCacheDir(DISK_CACHE_DIR);
        if(!cacheDir.exists()) {
            cacheDir.mkdir();
        } try {
            diskLruCache = DiskLruCache.open(cacheDir, 1, 1, DISK_MAX_SIZE);
        } catch (IOException e) {
            e.printStackTrace();
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
        String key = hashKeyForDisk(url);
        Bitmap bitmap = get(url);
        if (bitmap == null) {
            Log.e("get from disklru=", url);
            bitmap = getBitmapFromDiskLruCache(key);
            if(bitmap != null) {
                put(url, bitmap);
            }
        }
        Log.e("get bitmap=", String.valueOf(bitmap));
        return bitmap;
    }

    @Override
    public void putBitmap(String url, Bitmap bitmap) {
        put(url, bitmap);
        String key = hashKeyForDisk(url);
        putBitmapToDiskLruCache(key, bitmap);
    }

    /**
     * 获取disk缓存位置
     * @param uniqueName
     * @return
     */
    public File getDiskCacheDir(String uniqueName) {
        String cachePath;
        Log.e("Environment.MEDIA_MOUNTED", Environment.MEDIA_MOUNTED);
        Log.e("Environment.getExternalStorageState", Environment.getExternalStorageState());

        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
            cachePath = String.valueOf(Environment.getExternalStorageDirectory());
        } else {
            cachePath = OReaderApplication.getInstance().getCacheDir().getPath();
        }
        Log.e("cachePath=", cachePath);
        return new File(cachePath + File.separator + uniqueName);
    }

    /**
     * 从disk中获取图片
     * @param key
     * @return
     */
    private Bitmap getBitmapFromDiskLruCache(String key) {
        try {
            DiskLruCache.Snapshot snapshot = diskLruCache.get(key);
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
            DiskLruCache.Editor editor = diskLruCache.edit(key);

            if(editor != null) {
                OutputStream outputStream = editor.newOutputStream(0);
                bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
                editor.commit();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    public String hashKeyForDisk(String key) {
        String cacheKey;
        try {
            final MessageDigest mDigest = MessageDigest.getInstance("MD5");
            mDigest.update(key.getBytes());
            cacheKey = bytesToHexString(mDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            cacheKey = String.valueOf(key.hashCode());
        }
        return cacheKey;
    }

    private String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }
}