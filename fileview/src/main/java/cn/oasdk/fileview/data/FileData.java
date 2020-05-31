package cn.oasdk.fileview.data;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.Collator;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import cn.oasdk.base.AppContext;
import cn.oasdk.fileview.R;
import cn.oaui.L;
import cn.oaui.ResourceHold;
import cn.oaui.data.RowObject;
import cn.oaui.utils.AppUtils;
import cn.oaui.utils.FileUtils;
import cn.oaui.utils.StringUtils;


/**
 * @author zengmiaosen
 * @email 1510809124@qq.com
 * @CreateDate 2019-10-23  15:52
 * @Descrition
 */

public class FileData {

    //static LinkedList<String> files=new LinkedList<>();

    public static final String CACHE_FILE_NAME = "file_cache";

    public static final String CACHE_FILE_PATH = FileUtils.getSDCardPath() + "/" + AppUtils.getAppName() + "/" + "file_cache";

    public static final String CACHE_ALL_FILE_KEY = "all_file_list";

    public static LinkedList<String> files = new LinkedList<>();

    public static LinkedList<FileEntity> fileEntitysVideo = new LinkedList<>();
    public static String[] VIDEO_SUFFIX = new String[]{
            "mp4", "flv", "avi", "3gp", "webm", "ts", "ogv", "m3u8", "asf",
            "wmv", "rm", "rmvb", "mov", "mkv", "f4v", "mpg", "mpeg",
            "mpeg1", "mpeg2", "xvid", "dvd", "vcd", "vob", "divx"
    };

    public static LinkedList<FileEntity> fileEntitysDocumnet = new LinkedList<>();
    public static String[] DOCUMNET_SUFFIX = new String[]{
            "docx", "doc", "xls", "xlsx", "pdf", "ppt"
    };


    public static LinkedList<FileEntity> fileEntitysImage = new LinkedList<>();
    public static String[] IMAGE_SUFFIX = new String[]{
            "jpg", "png", "jpeg", "bmp","gif","svg","tif","psd"
    };
    public static LinkedList<FileEntity> fileEntitysMusic = new LinkedList<>();
    public static String[] MUSIC_SUFFIX = new String[]{
            "mp3", "wma", "amr", "ape", "flac"
    };
    public static LinkedList<FileEntity> fileEntitysApp = new LinkedList<>();
    public static String[] APP_SUFFIX = new String[]{
            "apk"
    };

    public static LinkedList<FileEntity> fileEntitysRar = new LinkedList<>();
    public static String[] RAR_SUFFIX = new String[]{
            "rar", "zip", "7z"
    };

    public static boolean scanDataComplate = false;

    public static void initData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                FileData.scanFile(FileUtils.getSDCardPath());
                scanDataComplate = true;
            }
        }).start();
    }

    public static void handlerData(final Handler handler) {
        if (scanDataComplate) {
            handler.sendEmptyMessage(1);
        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (!scanDataComplate) {
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    handler.sendEmptyMessage(1);
                }
            }).start();
        }
    }


    public static void scanFile(String path) {
        long startTime = System.currentTimeMillis();
        scanFile(new File(path));
        long endTime = System.currentTimeMillis();
//        L.i("============scanFile===========" + "程序运行时间：" + (endTime - startTime) + "ms");
//        L.i("============scanFile===========" + files.size());
//        ViewUtils.toast("程序运行时间：" + (endTime - startTime) + "ms,文件数量："+ files.size());
        //long startTime1 = System.currentTimeMillis();
        //int i = countMp4(files);
        //long endTime1 = System.currentTimeMillis();
        //L.i("============scanFile==========="+"程序运行时间：" + (endTime1 - startTime1) + "ms");
        //L.i("============scanFile==========="+i);
    }

    public static void scanFile(String path, Handler handler) {
        long startTime = System.currentTimeMillis();
        scanFile(new File(path), handler);
        long endTime = System.currentTimeMillis();
        L.i("============scanFile===========" + "程序运行时间：" + (endTime - startTime) + "ms");
    }


    public static void scanFile(File file) {
        File[] fs = file.listFiles();
        if (fs != null) {
            for (int i = 0; i < fs.length; i++) {
                File file1 = fs[i];
                //FileEntity fileEntity=new FileEntity();
                //fileEntity.put("filePath", file1.getAbsolutePath());
                if (file1.isDirectory()
                        && !isIngore(file1)) {
                    scanFile(file1);
                }
                if (!file1.isDirectory()
                        && !isIngore(file1)) {
                    if (isSuffixOf(file1.getAbsolutePath(), IMAGE_SUFFIX)) {
                        FileEntity fileEntity = getBasefileEntity(file1, "image");
                        fileEntitysImage.add(fileEntity);
                    } else if (isSuffixOf(file1.getAbsolutePath(), DOCUMNET_SUFFIX)) {
                        FileEntity fileEntity = getBasefileEntity(file1, "documnet");
                        fileEntitysDocumnet.add(fileEntity);
                    } else if (isSuffixOf(file1.getAbsolutePath(), VIDEO_SUFFIX)) {
                        FileEntity fileEntity = getBasefileEntity(file1, "video");
                        fileEntitysVideo.add(fileEntity);
                    } else if (isSuffixOf(file1.getAbsolutePath(), MUSIC_SUFFIX)) {
                        FileEntity fileEntity = getBasefileEntity(file1, "music");
                        fileEntitysMusic.add(fileEntity);
                    } else if (isSuffixOf(file1.getAbsolutePath(), APP_SUFFIX)) {
                        FileEntity fileEntity = getBasefileEntity(file1, "app");
                        fileEntitysApp.add(fileEntity);
                    } else if (isSuffixOf(file1.getAbsolutePath(), RAR_SUFFIX)) {
                        FileEntity fileEntity = getBasefileEntity(file1, "rar");
                        fileEntitysRar.add(fileEntity);
                    }
                }
                files.add(file1.getAbsolutePath());
            }
        }
    }

    /**
     * 文件是否包含.nomedia文件
     *
     * @param file1
     * @return
     */
    public static boolean hasNomedieFile(File file1) {
        File file = new File(file1.getPath(), ".nomedia");
        return file.exists();
    }

    /**
     * 文件是否包含.nomedia文件
     *
     * @param file1
     * @return
     */
    public static boolean isIngore(File file1) {
//        return file1.getName().startsWith(".")
//                || hasNomedieFile(file1);
        return false;
    }

    public static void scanFile(File file, Handler handler) {
        File[] fs = file.listFiles();
        //.nomedia处理
        for (int i = 0; i < fs.length; i++) {
            File file1 = fs[i];
            if (!file1.getName().startsWith(".")) {
//            &&!file1.getName().equals("Android")
                if (file1.isDirectory()
                        && !file1.getName().startsWith(".")
                        && !hasNomedieFile(file1)) {
                    scanFile(file1, handler);
                }
                if (isSuffixOf(file1.getAbsolutePath(), IMAGE_SUFFIX)) {
                    FileEntity fileEntity = getBasefileEntity(file1, "image");
                    fileEntitysImage.add(fileEntity);
                } else if (isSuffixOf(file1.getAbsolutePath(), DOCUMNET_SUFFIX)) {
                    FileEntity fileEntity = getBasefileEntity(file1, "documnet");
                    fileEntitysDocumnet.add(fileEntity);
                } else if (isSuffixOf(file1.getAbsolutePath(), VIDEO_SUFFIX)) {
                    FileEntity fileEntity = getBasefileEntity(file1, "video");
                    fileEntitysVideo.add(fileEntity);
                } else if (isSuffixOf(file1.getAbsolutePath(), MUSIC_SUFFIX)) {
                    FileEntity fileEntity = getBasefileEntity(file1, "music");
                    fileEntitysMusic.add(fileEntity);
                }
            }
        }
        Message message = new Message();
        message.obj = file.getAbsolutePath();
        handler.sendMessage(message);
    }



    public static FileEntity getBasefileEntity(File file1, String fileType) {
        FileEntity fileEntity = new FileEntity();
        fileEntity.setId(StringUtils.getUUID());
        fileEntity.setPath(file1.getAbsolutePath());
        fileEntity.setFileName(file1.getName());
        fileEntity.setFileSize(file1.length());
        fileEntity.setFileType(fileType);
        Date date = new Date(file1.lastModified());
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        fileEntity.setCreateTime(formatter.format(date));
        L.i("======getBasefileEntity===== "+ formatter.format(date));
        fileEntity.setFileSizeText(FileUtils.formetFileSize(file1.length()));
        return fileEntity;
    }


    /**
     * @param rows
     */
    public static LinkedList<RowObject> folderRows(LinkedList<RowObject> rows) {
        RowObject rowTemp = new RowObject();
        for (int i = 0; i < rows.size(); i++) {
            RowObject rowObject = rows.get(i);
            String filePath = rowObject.getString("path");
            String dir = rowObject.getString("dir");
            File file = new File(filePath);
            File dirFile = file.getParentFile();
            String dirPath = StringUtils.isNotEmpty(dir) ? dir : dirFile.getAbsolutePath();
            Long fileSize = rowObject.getLong("fileSize");
            if (rowTemp.containsKey(dirPath)) {
                RowObject row = rowTemp.getRow(dirPath);
                row.put("fileCount", row.getInteger("fileCount") + 1);
                long fileSize1 = row.getLong("fileSize") + fileSize;
                row.put("fileSize", fileSize1);
                row.put("fileSizeText", FileUtils.formetFileSize(fileSize1));
                LinkedList<RowObject> rows1 = row.getRows("rows");
                rows1.add(rowObject);
            } else {
                RowObject row = new RowObject();
                LinkedList<RowObject> rows2 = new LinkedList<>();
                rows2.add(rowObject);
                row.put("rows", rows2);
                row.put("fileName", dirFile.getName());
                row.put("path", dirPath);
                row.put("dir", rowObject.getString("dir"));
                row.put("isDir", true);
                row.put("fileSizeText", FileUtils.formetFileSize(fileSize));
                row.put("fileSize", fileSize);
                row.put("fileCount", 1);
                rowTemp.put(dirPath, row);
            }
        }
        LinkedList<RowObject> rows3 = new LinkedList<>();
        for (Object row : rowTemp.values()) {
            RowObject rowObject= (RowObject) row;
            rows3.add(rowObject);
        }
        return rows3;
    }


    public static void clearData() {
        fileEntitysImage.clear();
        fileEntitysDocumnet.clear();
        fileEntitysVideo.clear();
        fileEntitysMusic.clear();
    }

    /**
     * 没有改善
     *
     * @param handler
     */
    public static void scanByThreadPool(final Handler handler) {
        File file = new File(FileUtils.getSDCardPath());
        File[] fs = file.listFiles();
        List<Runnable> runnables = new ArrayList<>();

        for (int i = 0; i < fs.length; i++) {
            final File f = fs[i];
            if (f.isDirectory()) {
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        scanFile(f, handler);
                    }
                };
                runnables.add(runnable);
            } else {
                files.add(f.getAbsolutePath());
            }
        }
        L.i("============scanFile===========" + runnables.size());
        //固定数目线程池(最大线程数目为cpu核心数,多余线程放在等待队列中)
        final ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        for (Runnable runnable : runnables) {
            executorService.submit(runnable);
        }
        executorService.shutdown();
        //等待线程池中的所有线程运行完成
        while (true) {
            if (executorService.isTerminated()) {
                break;
            }
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


//    public static void initFileFromCache(final Handler handler) {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                LinkedList<FileEntity> fileEntitysI = SPUtils.getfileEntitys("fileEntitysImage","fileEntitysImage");
//                LinkedList<FileEntity> fileEntitysD = SPUtils.getfileEntitys("fileEntitysDocumnet","fileEntitysDocumnet");
//                LinkedList<FileEntity> fileEntitysV = SPUtils.getfileEntitys("fileEntitysVideo","fileEntitysVideo");
//                LinkedList<FileEntity> fileEntitysM = SPUtils.getfileEntitys("fileEntitysMusic","fileEntitysMusic");
//                if (fileEntitysI == null &&fileEntitysD == null &&fileEntitysV == null &&fileEntitysM == null) {
//                    FileData.scanFile(handler);
//                } else {
//                    fileEntitysImage.clear();
//                    fileEntitysDocumnet.clear();
//                    fileEntitysMusic.clear();
//                    fileEntitysVideo.clear();
//                    if(fileEntitysI!=null){
//                        fileEntitysImage.addAll(fileEntitysI);
//                    }
//                    if(fileEntitysD!=null){
//                        fileEntitysDocumnet.addAll(fileEntitysD);
//                    }
//                    if(fileEntitysV!=null){
//                        fileEntitysVideo.addAll(fileEntitysV);
//                    }
//                    if(fileEntitysM!=null){
//                        fileEntitysMusic.addAll(fileEntitysM);
//                    }
//                    handler.sendEmptyMessage(1);
//                    L.i("============setListView==========="+FileData.fileEntitysImage.size());
//                    L.i("============setListView==========="+FileData.fileEntitysDocumnet.size());
//                    L.i("============setListView==========="+FileData.fileEntitysVideo.size());
//                    L.i("============setListView==========="+FileData.fileEntitysMusic.size());
//                }
//            }
//        }).start();
//    }


    /**
     * 匹配后缀名
     *
     * @param filePath
     * @param FILE_TYPE
     * @return
     */
    public static boolean isSuffixOf(String filePath, String[] FILE_TYPE) {
        for (int i = 0; i < FILE_TYPE.length; i++) {
            String s = "." + FILE_TYPE[i];
            if (filePath.toLowerCase().contains(s.toLowerCase()) &&filePath.toLowerCase().endsWith(s.toLowerCase())) {//
                return true;
            }
        }
        return false;
    }

    /**
     * charAt效率高于endwith?
     *
     * @param s
     * @param filePath
     * @return
     */
    private static boolean isEndsWith(String s, String filePath) {
        //aa.doc
        if (s.length() >= filePath.length()) {
            return false;
        }
        for (int i = 0; i < s.length(); i++) {
            if (filePath.charAt(filePath.length() - i - 1) != s.charAt(s.length() - i - 1)) {
                return false;
            }
        }
        return true;
    }



    public static String getApk(String packageName) {
        String appDir = null;
        try {
            //通过包名获取程序源文件路径
            appDir = AppContext.getApplication().getPackageManager().getApplicationInfo(packageName, 0).sourceDir;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return appDir;
    }


//    /**
//     * 根据当前文件路径列出所有文件夹和文件
//     *
//     * @param fileEntitys
//     * @param curFilePath
//     */
//    public static LinkedList<FileEntity> sortfileEntitys(List<FileEntity> fileEntitys, String curFilePath) {
//        FileEntity tempfileEntityDir = new FileEntity();
//        FileEntity tempfileEntityFile = new FileEntity();
//        for (int i = 0; i < fileEntitys.size(); i++) {
//            FileEntity fileEntity = fileEntitys.get(i);
//            String filePath = fileEntity.getString("filePath");
//            if (filePath.startsWith(curFilePath)) {
//                //FileEntity fileEntity = StringUtils.deepCopyObject(fileEntity);
//                String nextFileName = filePath.substring(curFilePath.length(), filePath.length());
//                if (nextFileName.indexOf("/") > 0) {
//                    nextFileName = nextFileName.substring(0, nextFileName.indexOf("/"));
//                    fileEntity.put("name", nextFileName);
//                    //单个文件大小
//                    Long size = fileEntity.getLong("_size");
//                    if (size == null) {
//                        size = 0l;
//                    }
//                    if (tempfileEntityDir.getfileEntity(nextFileName) != null &&
//                            tempfileEntityDir.getfileEntity(nextFileName).getLong("file_size") != null) {
//                        Long aLong = tempfileEntityDir.getfileEntity(nextFileName).getLong("file_size");
//                        size = size + aLong;
//                    }
//                    if (tempfileEntityDir.getfileEntity(nextFileName) != null) {
//                        int sum = tempfileEntityDir.getfileEntity(nextFileName).getInteger("sum");
//                        sum = sum + 1;
//                        fileEntity.put("sum", sum);
//                    } else {
//                        fileEntity.put("sum", 1);
//                    }
//                    //文件夹中的文件大小
//                    fileEntity.put("file_size", size);
//                    fileEntity.put("isDir", true);
//                    tempfileEntityDir.put(nextFileName, fileEntity);
//                } else {
//                    fileEntity.remove("sum");
//                    fileEntity.put("isDir", false);
//                    fileEntity.put("name", fileEntity.getString("_display_name"));
//                    tempfileEntityFile.put(fileEntity.getString("_id"), fileEntity);
//                }
//            }
//        }
//        LinkedList<FileEntity> tempfileEntitysFile = new LinkedList<>();
//        for (Object fileEntity : tempfileEntityDir.values()) {
//            tempfileEntitysFile.add((FileEntity) fileEntity);
//        }
//        L.i("============sortfileEntitys===========" + tempfileEntityFile);
//        for (Object fileEntity : tempfileEntityFile.values()) {
//            ((FileEntity) fileEntity).remove("sum");
//            tempfileEntitysFile.add((FileEntity) fileEntity);
//        }
//        return tempfileEntitysFile;
//    }

    public static LinkedList<FileEntity> readFileFromDir(String dir) {
        File file = new File(dir);
        if (!file.exists()) {
            return null;
        }
        File[] files = file.listFiles();
        L.i("======readFileFromDir===== "+file);
        LinkedList<FileEntity> rows = new LinkedList<>();
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                File file1 = files[i];
                FileEntity cl = new FileEntity();
                String absolutePath = file1.getAbsolutePath();
                cl.setPath(absolutePath);
                cl.setFileName(file1.getName());
                if (file1.isDirectory()) {
                    if (!isIngore(file1)) {
                        cl.setDir(true);
                        cl.setFileSizeText(" ");
                        int fileNum = 0, dirNum = 0;
                        File[] files1 = file1.listFiles();
                        for (int j = 0; j < files1.length; j++) {
                            if (files1[j].isDirectory()) {
                                if (!isIngore(files1[j])) {
                                    dirNum = dirNum + 1;
                                }

                            } else {
                                if (!isIngore(files1[j])) {
                                    fileNum = fileNum + 1;
                                }
                            }
                        }
                        cl.setDirCount(dirNum);
                        cl.setFileCount(fileNum);
                        rows.add(cl);
                    }
                } else {
                    if (!isIngore(file1)) {
                        cl.setDir(false);
                        long length = file1.length();
                        cl.setFileSize(length);
                        cl.setFileSizeText(FileUtils.formetFileSize(length));
                        rows.add(cl);
                    }
                }
            }
            sortRow(rows);
        }
        return rows;
    }

    public static void sortRow(LinkedList<FileEntity> rows) {
        final Collator cmp = Collator.getInstance(java.util.Locale.CHINA);
        Collections.sort(rows, new Comparator<FileEntity>() {
            @Override
            public int compare(FileEntity o1, FileEntity o2) {
                Boolean isDir1 = o1.isDir;
                Boolean isDir2 = o2.isDir;
                return cmp.compare(isDir2 + "", isDir1 + "");
            }
        });
    }

    public static void sortRowByFileSize(LinkedList<RowObject> rows) {
        Collections.sort(rows, new Comparator<RowObject>() {
            @Override
            public int compare(RowObject o1, RowObject o2) {
                if (o1.getBoolean("isDir")) {
                    return 1;
                } else if (o2.getBoolean("isDir")) {
                    return 1;
                } else {
                    Long fileSize1 = o1.getLong("fileSize");
                    Long fileSize2 = o2.getLong("fileSize");
                    return fileSize2.compareTo(fileSize1);
                }
            }
        });
    }

    public static void sortRowByFileType(LinkedList<RowObject> rows) {
        Collections.sort(rows, new Comparator<RowObject>() {
            @Override
            public int compare(RowObject o1, RowObject o2) {
                if (o1.getBoolean("isDir")) {
                    return 1;
                } else if (o2.getBoolean("isDir")) {
                    return 1;
                } else {
                    String fileSize1 = o1.getString("fileType") + "";
                    String fileSize2 = o2.getString("fileType") + "";
                    if (StringUtils.isEmpty(fileSize1) && StringUtils.isNotEmpty(fileSize2)) {
                        return 1;
                    } else if (StringUtils.isNotEmpty(fileSize1) && StringUtils.isEmpty(fileSize2)) {
                        return -1;
                    } else {
                        return fileSize1.compareTo(fileSize2);
                    }

                }
            }
        });
    }


    public static void copyFileByRows(final LinkedList<RowObject> rowsChecked, final String curCollectDir, final Handler handler) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < rowsChecked.size(); i++) {
                    RowObject rowObject = rowsChecked.get(i);
                    String path = rowObject.getString("path");
                    Boolean isDir = rowObject.getBoolean("isDir");
                    if (isDir) {
                        FileUtils.copyDirectiory(path, curCollectDir);
                    } else {
                        copyFile(path, curCollectDir, rowObject);
                    }
                }
                handler.sendEmptyMessage(1);
            }
        }).start();

    }

    /**
     * 复制单个文件
     *
     * @param oldPath   String 原文件路径 如：c:/fqf.txt
     * @param targetDir String  如：f:/fqf.txt
     * @return boolean
     */
    public static boolean copyFile(String oldPath, String targetDir, RowObject row) {
        boolean sucess = false;
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            File fileDir = new File(targetDir);
            if (!fileDir.exists()) {
                fileDir.mkdirs();
            }
            String newPath = FileUtils.renameFileIfExit(targetDir + "/" + oldfile.getName());
            row.put("path", newPath);
            File file = new File(newPath);
            row.put("fileName", file.getName());
            if (oldfile.exists()) { // 文件存在时
                InputStream inStream = new FileInputStream(oldPath); // 读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; // 字节数 文件大小
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
            }
            sucess = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sucess;
    }


    public static void moveFileByRows(LinkedList<RowObject> rowsChecked, String targetDir, Handler handler) {
        for (int i = 0; i < rowsChecked.size(); i++) {
            RowObject rowObject = rowsChecked.get(i);
            String path = rowObject.getString("path");
            File file = new File(path);
            if (!targetDir.equals(file.getParent())) {
                Boolean isDir = rowObject.getBoolean("isDir");
                String newPath;
                if (isDir) {
                    newPath = FileUtils.moveDir(path, targetDir);
                } else {
                    newPath = FileUtils.moveFile(path, targetDir);

                }
                rowObject.put("path", newPath);
            }
        }
    }

    public static void deleteFileByRows(final LinkedList<RowObject> rowsChecked, final Handler handler) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < rowsChecked.size(); i++) {
                    RowObject rowObject = rowsChecked.get(i);
                    String path = rowObject.getString("path");
                    Boolean isDir = rowObject.getBoolean("isDir");
                    if (isDir) {
                        boolean b = FileUtils.deleteDirectory(path);
                        L.i("======run===== " + b);
                        if (!b) {
                            Message msg = new Message();
                            msg.obj = ResourceHold.getString(R.string.dir) + ":" + path + " 删除失败";
                            msg.what = -1;
                            handler.sendMessage(msg);
                        }
                    } else {
                        boolean b = FileUtils.deleteFile(path);
                        if (!b) {
                            Message msg = new Message();
                            msg.obj = ResourceHold.getString(R.string.file) + ":" + path + " 删除失败";
                            msg.what = -1;
                            handler.sendMessage(msg);
                        }
                    }
                }
                handler.sendEmptyMessage(1);
            }
        }).start();
    }


    public static void shareFileByRows(Context context, LinkedList<RowObject> rowsChecked) {
        ArrayList<String> files = new ArrayList<String>();
        for (int i = 0; i < rowsChecked.size(); i++) {
            RowObject rowObject = rowsChecked.get(i);
            String path = rowObject.getString("path");
            files.add(path);//分享文件
        }
        AppUtils.shareFileBySystemApp(context, files.get(0));
    }


    public static void scanFile(File file,RowObject rowObject) {
        File[] fs = file.listFiles();
        if (fs != null) {
            for (int i = 0; i < fs.length; i++) {
                File file1 = fs[i];
                if (file1.isDirectory()
                        && !isIngore(file1)) {
//                    RowObject branch=new RowObject();
//                    rowObject.put("branch",branch);
                    scanFile(file1,rowObject);
                }

                if (!file1.isDirectory()
                        && !isIngore(file1)) {
                    if (isSuffixOf(file1.getAbsolutePath(), IMAGE_SUFFIX)) {
                        File[] fsImg ;
                        if(rowObject.get("img")!=null){
                            fsImg= (File[]) rowObject.get("img");
                        }else {
                            fsImg=new File[]{};
                        }
                        fsImg[fsImg.length]=file1;

                    } else if (isSuffixOf(file1.getAbsolutePath(), DOCUMNET_SUFFIX)) {
                        FileEntity fileEntity = getBasefileEntity(file1, "documnet");
                        fileEntitysDocumnet.add(fileEntity);
                    } else if (isSuffixOf(file1.getAbsolutePath(), VIDEO_SUFFIX)) {
                        FileEntity fileEntity = getBasefileEntity(file1, "video");
                        fileEntitysVideo.add(fileEntity);
                    } else if (isSuffixOf(file1.getAbsolutePath(), MUSIC_SUFFIX)) {
                        FileEntity fileEntity = getBasefileEntity(file1, "music");
                        fileEntitysMusic.add(fileEntity);
                    } else if (isSuffixOf(file1.getAbsolutePath(), APP_SUFFIX)) {
                        FileEntity fileEntity = getBasefileEntity(file1, "app");
                        fileEntitysApp.add(fileEntity);
                    } else if (isSuffixOf(file1.getAbsolutePath(), RAR_SUFFIX)) {
                        FileEntity fileEntity = getBasefileEntity(file1, "rar");
                        fileEntitysRar.add(fileEntity);
                    }
                }
                files.add(file1.getAbsolutePath());
            }
            rowObject.put("rows",fs);
        }
    }



}
