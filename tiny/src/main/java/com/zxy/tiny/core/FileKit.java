package com.zxy.tiny.core;

import android.os.Environment;
import android.text.TextUtils;

import com.zxy.tiny.Tiny;
import com.zxy.tiny.common.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * Created by zhengxiaoyong on 2017/3/13.
 */
public final class FileKit {

    private static final String DEFAULT_FILE_COMPRESS_DIRECTORY_NAME = "tiny";

    private static final Random RANDOM = new Random();

    private static final ThreadLocal<DateFormat> FILE_SUFFIX_DATE_FORMAT_THREAD_LOCAL = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        }
    };

    public static File generateCompressOutfileFormatJPEG(String prefix) {
        String suffix = getDateFormat().format(new Date(System.currentTimeMillis()));
        int seed = RANDOM.nextInt(1000);
        return new File(getDefaultFileCompressDirectory(), prefix + "-" + seed + "-" + suffix + ".jpg");
    }

    public static File generateCompressOutfileFormatPNG(String prefix) {
        String suffix = getDateFormat().format(new Date(System.currentTimeMillis()));
        int seed = RANDOM.nextInt(1000);
        return new File(getDefaultFileCompressDirectory(), prefix + "-" + seed + "-" + suffix + ".png");
    }

    public static File generateCompressOutfileFormatJPEG(String directory, String prefix) {
        if (TextUtils.isEmpty(directory))
            return generateCompressOutfileFormatJPEG(prefix);
        String suffix = getDateFormat().format(new Date(System.currentTimeMillis()));
        int seed = RANDOM.nextInt(1000);
        File parent;
        try {
            parent = new File(directory);
            if (parent.exists() && parent.isFile()) {
                parent = getDefaultFileCompressDirectory();
            } else {
                parent.mkdirs();
            }
        } catch (Exception e) {
            parent = getDefaultFileCompressDirectory();
        }
        return new File(parent, prefix + "-" + seed + "-" + suffix + ".jpg");
    }

    public static File generateCompressOutfileFormatPNG(String directory, String prefix) {
        if (TextUtils.isEmpty(directory))
            return generateCompressOutfileFormatPNG(prefix);
        String suffix = getDateFormat().format(new Date(System.currentTimeMillis()));
        int seed = RANDOM.nextInt(1000);
        File parent;
        try {
            parent = new File(directory);
            if (parent.exists() && parent.isFile()) {
                parent = getDefaultFileCompressDirectory();
            } else {
                parent.mkdirs();
            }
        } catch (Exception e) {
            parent = getDefaultFileCompressDirectory();
        }
        return new File(parent, prefix + "-" + seed + "-" + suffix + ".png");
    }

    public static File getDefaultFileCompressDirectory() {
        File file = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            file = Tiny.getInstance().getApplication().getExternalFilesDir(null);
        }
        file = file == null ? Tiny.getInstance().getApplication().getFilesDir() : file;

        file = new File(file.getParentFile(), DEFAULT_FILE_COMPRESS_DIRECTORY_NAME);
        if (!file.exists())
            file.mkdirs();
        return file;
    }

    public static long getSizeInBytes(String filePath) {
        if (TextUtils.isEmpty(filePath))
            return 0L;
        return getSizeInBytes(new File(filePath));
    }

    public static long getSizeInBytes(File file) {
        if (file == null || !file.exists() || !file.isFile())
            return 0L;
        return file.length();
    }

    public static long getSizeInBytes(InputStream is) {
        if (is == null)
            return 0L;
        try {
            return is.available();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0L;
    }

    public static DateFormat getDateFormat() {
        return FILE_SUFFIX_DATE_FORMAT_THREAD_LOCAL.get();
    }

    public static File[] wrap(String[] filePaths) {
        if (filePaths == null || filePaths.length == 0)
            return null;
        File[] files = new File[filePaths.length];
        for (int i = 0; i < filePaths.length; i++) {
            String filePath = filePaths[i];
            files[i] = TextUtils.isEmpty(filePath) ? new File("") : new File(filePath);
        }
        return files;
    }

    public static boolean clearDirectory(File dir) {
        if (dir == null || !dir.isDirectory() || !dir.exists())
            return false;
        File[] files = dir.listFiles();
        int length = files.length;
        for (int i = 0; i < length; i++) {
            File file = files[i];
            if (file == null)
                continue;
            if (file.isFile() && file.exists()) {
                boolean result = file.delete();
                Logger.e(file.getName() + (result ? " delete success!" : " delete failed!"));
                continue;
            }
            if (file.isDirectory() && file.exists()) {
                clearDirectory(file);
            }
        }
        return true;
    }

    /**
     * 保存输入流到指定文件
     *
     * @param is   输入流
     * @param file 指定文件
     */
    public static void toFile(InputStream is, File file) throws IOException {
        File parentFile = file.getParentFile();
        if (!parentFile.exists() && !parentFile.mkdirs()) {
            throw new FileNotFoundException("File cannot create");
        }
        FileOutputStream fos = new FileOutputStream(file);
        int len;
        byte[] b = new byte[1024];
        while ((len = is.read(b)) != -1) {
            fos.write(b, 0, len);
        }
        fos.flush();
        fos.close();
        is.close();
    }
}
