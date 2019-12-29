package cn.oaui.utils;

import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;

import cn.oaui.UIGlobal;

/**
 * @author zms
 * @Created by com.gzpykj.com
 * @Date 2016-3-21
 * @Descrition 文件操作工具类
 */
public class FileUtils {


    /**
     * 根据传入文件的绝对路径，如果文件不存在则创建文件夹和一个文件
     *
     * @param AbsolutePath 内存卡上的文件路径，如："/MyUtils/dom4j"
     * @return
     */
    public static File createFile(String AbsolutePath) {
        String path = AbsolutePath.substring(0, AbsolutePath.lastIndexOf("/"));
        String name = AbsolutePath.substring(AbsolutePath.lastIndexOf("/") + 1, AbsolutePath.length());
        return createFile(path, name);
    }


    /**
     * 获取文件夹
     *
     * @param path
     * @return 不存在则创建一个
     */
    public static boolean createDir(String path) {
        if (!validate(path)) {
            return false;
        }
        File dirFile = new File(path);
        return dirFile.mkdirs();
    }

    /**
     * 根据传入文件的路径和名称获取文件对象，如果文件不存在则创建文件夹和一个文件
     *
     * @param dir  内存卡上的文件路径，如："/MyUtils/dom4j"
     * @param name 文件名称
     * @return
     */
    public static File createFile(String dir, String name) {
        // 判断是否存在sd卡
        if (!isSDCardEnable()) {// 如果不存在,
            return null;
        } else {
            // 判断目录是否存在，不存在则创建该目录
            createDir(dir);
            // 文件是否创建成功
            boolean isFileCreateSuccess = false;
            // 判断文件是否存在，不存在则创建该文件
            File file = new File(dir, name);
            try {
                isFileCreateSuccess = file.createNewFile();// 创建文件
            } catch (IOException e) {
                e.printStackTrace();
            }
            // 返回文件对象
            if (isFileCreateSuccess) {
                return file;
            } else {
                return null;
            }
        }
    }

    public static File getOrCreateFile(String path) {
        String dirPath = path.substring(0, path.lastIndexOf("/"));
        String name = path.substring(path.lastIndexOf("/"), path.length());
        return getOrCreateFile(dirPath, name);

    }

    public static File getOrCreateFile(String dir, String name) {
        File dirFile = new File(dir);
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }
        File file = new File(dir + "/" + name);
        if (!file.exists()) {
            try {
                boolean newFile = file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }


    /**
     * 校验路径是否有问题
     *
     * @param path
     * @return
     */
    public static boolean validate(String path) {
        boolean success = false;
        try {
            new File(path);
            success = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return success;
    }

    /**
     * 检查内存卡是否挂载
     *
     * @return
     */
    public static boolean isSDCardEnable() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    /**
     * 获取SD卡路径
     *
     * @return
     */
    public static String getSDCardPath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath()
                + File.separator;
    }

    /**
     * 获取SD卡的剩余容量 单位byte
     *
     * @return
     */
    public static long getSDCardAllSize() {
        if (isSDCardEnable()) {
            StatFs stat = new StatFs(getSDCardPath());
            // 获取空闲的数据块的数量
            long availableBlocks = (long) stat.getAvailableBlocks() - 4;
            // 获取单个数据块的大小（byte）
            long freeBlocks = stat.getAvailableBlocks();
            return freeBlocks * availableBlocks;
        }
        return 0;
    }

    /**
     * 获取指定路径所在空间的剩余可用容量字节数，单位byte
     *
     * @param filePath
     * @return 容量字节 SDCard可用空间，内部存储可用空间
     */
    public static long getFreeBytes(String filePath) {
        // 如果是sd卡的下的路径，则获取sd卡可用容量
        if (filePath.startsWith(getSDCardPath())) {
            filePath = getSDCardPath();
        } else {// 如果是内部存储的路径，则获取内存存储的可用容量
            filePath = Environment.getDataDirectory().getAbsolutePath();
        }
        StatFs stat = new StatFs(filePath);
        long availableBlocks = (long) stat.getAvailableBlocks() - 4;
        return stat.getBlockSize() * availableBlocks;
    }

    /**
     * 获取系统存储路径
     *
     * @return
     */
    public static String getRootDirectoryPath() {
        return Environment.getRootDirectory().getAbsolutePath();
    }

    /**
     * 获取系统存储路径
     *
     * @return
     */
    public static String getAppDirPath() {
        String dirPath = FileUtils.getSDCardPath() + AppUtils.getAppName();
        File file = new File(dirPath);
        if (!file.exists()) {
            file.mkdirs();
        }
        return dirPath;
    }

    /**
     * =========================Assets文件夹操作======================
     */

    /**
     * 工具Assets的文件名称获取InputStream
     *
     * @param filepath
     * @return
     */
    public static InputStream readFromAssets(String filepath) {
        try {
            InputStream is = UIGlobal.getApplication().getAssets()
                    .open(filepath);
            return is;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 转换文件大小
     *
     * @param fileS
     * @return
     */
    public static String formetFileSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "K";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "M";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "G";
        }
        return fileSizeString;
    }


    /**
     * 判断是否存在改路径
     *
     * @return
     */
    public static boolean isEixtPath(String path) {
        File file = new File(path);
        if (file.exists()) {
            return true;
        }
        return false;
    }

    /**
     * 复制单个文件
     *
     * @param oldPath String 原文件路径 如：c:/fqf.txt
     * @param newPath String 复制后路径 如：f:/fqf.txt
     * @return boolean
     */
    public static void copyFile(String oldPath, String newPath) {
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) { // 文件存在时
                InputStream inStream = new FileInputStream(oldPath); // 读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                int length;
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; // 字节数 文件大小
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();

        }

    }


    /**
     * 写文本
     *
     * @param text
     * @param filePath
     */
    public static boolean writeFile(String text, String filePath) {
        boolean success = false;
        try {
            File file = FileUtils.createFile(filePath);
            // 先清空内容再写入
            FileOutputStream fos = new FileOutputStream(file);

            byte[] buffer = text.getBytes();
            fos.write(buffer);
            fos.close();
            success = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            success = false;
        } catch (IOException e) {
            e.printStackTrace();
            success = false;
        } catch (Exception e) {
            e.printStackTrace();
            success = false;
        }
        return success;
    }


    /**
     * 读取文本文件
     *
     * @param file
     * @return
     */
    public static String readFile(File file) {
        String result = null;
        try {
            if (file.exists()) {
                FileInputStream is = new FileInputStream(file);
                byte[] b = new byte[is.available()];
                is.read(b);
                result = new String(b);
                // 关闭流
                is.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    public static String toString(InputStream inputStream) {
        try {
            byte[] bytes = new byte[4096];
            bytes = new byte[inputStream.available()];
            inputStream.read(bytes);
            String str = new String(bytes);
            return str;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getFoldName(String path) {

        String sdPath = Environment.getExternalStorageDirectory().toString();
        String foldPath = null;
        if (null != sdPath && path.contains(sdPath)) {
            if (!TextUtils.isEmpty(sdPath)) {
                int beginIndex = path.indexOf("/", sdPath.length()) + 1;
                int lastIndex = path.indexOf("/", sdPath.length() + 1);
                if (lastIndex == -1) {
                    lastIndex = path.length() - 1;
                }
                //L.i("============getFoldName==========="+path);
                foldPath = path.substring(beginIndex, lastIndex);
                if (foldPath.contains(".")) {
                    foldPath = path.substring(beginIndex + 1, lastIndex);
                }
            }
        }
        return foldPath;
    }

    /**
     * 获取文件类型
     * @param fName
     * @return
     */
    public static String getMIMEType(String fName) {
        String type="*/*";
        //获取后缀名前的分隔符"."在fName中的位置。
        int dotIndex = fName.lastIndexOf(".");
        if(dotIndex < 0){
            return type;
        }
        /* 获取文件的后缀名*/
        String end=fName.substring(dotIndex,fName.length()).toLowerCase();
        if(end=="")return type;
        //在MIME和文件类型的匹配表中找到对应的MIME类型。
        for(int i=0;i<MIME_MapTable.length;i++){ //MIME_MapTable??在这里你一定有疑问，这个MIME_MapTable是什么？
            if(end.equals(MIME_MapTable[i][0]))
                type = MIME_MapTable[i][1];
        }
        return type;
    }
    public static final String[][] MIME_MapTable = {
            // {后缀名，MIME类型}
            { ".3gp", "video/3gpp" },
            { ".apk", "application/vnd.android.package-archive" },
            { ".asf", "video/x-ms-asf" },
            { ".avi", "video/x-msvideo" },
            { ".bin", "application/octet-stream" },
            { ".bmp", "image/bmp" },
            { ".c", "text/plain" },
            { ".class", "application/octet-stream" },
            { ".conf", "text/plain" },
            { ".cpp", "text/plain" },
            { ".doc", "application/msword" },
            { ".docx",
                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document" },
            { ".xls", "application/vnd.ms-excel" },
            { ".xlsx",
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" },
            { ".exe", "application/octet-stream" },
            { ".gif", "image/gif" },
            { ".gtar", "application/x-gtar" },
            { ".gz", "application/x-gzip" },
            { ".h", "text/plain" },
            { ".htm", "text/html" },
            { ".html", "text/html" },
            { ".jar", "application/java-archive" },
            { ".java", "text/plain" },
            { ".jpeg", "image/jpeg" },
            { ".jpg", "image/jpeg" },
            { ".js", "application/x-javascript" },
            { ".log", "text/plain" },
            { ".m3u", "audio/x-mpegurl" },
            { ".m4a", "audio/mp4a-latm" },
            { ".m4b", "audio/mp4a-latm" },
            { ".m4p", "audio/mp4a-latm" },
            { ".m4u", "video/vnd.mpegurl" },
            { ".m4v", "video/x-m4v" },
            { ".mov", "video/quicktime" },
            { ".mp2", "audio/x-mpeg" },
            { ".mp3", "audio/x-mpeg" },
            { ".mp4", "video/mp4" },
            { ".mpc", "application/vnd.mpohun.certificate" },
            { ".mpe", "video/mpeg" },
            { ".mpeg", "video/mpeg" },
            { ".mpg", "video/mpeg" },
            { ".mpg4", "video/mp4" },
            { ".mpga", "audio/mpeg" },
            { ".msg", "application/vnd.ms-outlook" },
            { ".ogg", "audio/ogg" },
            { ".pdf", "application/pdf" },
            { ".png", "image/png" },
            { ".pps", "application/vnd.ms-powerpoint" },
            { ".ppt", "application/vnd.ms-powerpoint" },
            { ".pptx",
                    "application/vnd.openxmlformats-officedocument.presentationml.presentation" },
            { ".prop", "text/plain" }, { ".rc", "text/plain" },
            { ".rmvb", "audio/x-pn-realaudio" }, { ".rtf", "application/rtf" },
            { ".sh", "text/plain" }, { ".tar", "application/x-tar" },
            { ".tgz", "application/x-compressed" }, { ".txt", "text/plain" },
            { ".wav", "audio/x-wav" }, { ".wma", "audio/x-ms-wma" },
            { ".wmv", "audio/x-ms-wmv" },
            { ".wps", "application/vnd.ms-works" }, { ".xml", "text/plain" },
            { ".z", "application/x-compress" },
            { ".zip", "application/x-zip-compressed" },
            { ".amr", "audio/*" },{ ".flac", "audio/*" },{ ".ape", "audio/*" }, { "", "*/*" } };


}
