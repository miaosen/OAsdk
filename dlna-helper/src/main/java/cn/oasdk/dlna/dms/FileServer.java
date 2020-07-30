package cn.oasdk.dlna.dms;

import android.os.Handler;
import android.os.Message;

import org.fourthline.cling.support.model.Res;
import org.fourthline.cling.support.model.item.ImageItem;
import org.fourthline.cling.support.model.item.Item;
import org.seamless.util.MimeType;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import cn.oaui.L;
import cn.oaui.data.Row;
import cn.oaui.utils.AppUtils;
import cn.oaui.utils.FileUtils;
import cn.oaui.utils.SPUtils;

/**
 * @author zengmiaosen
 * @email 1510809124@qq.com
 * @CreateDate 2019-10-23  15:52
 * @Descrition
 */

public class FileServer {

    //static LinkedList<String> files=new LinkedList<>();

    public static final String CACHE_FILE_NAME = "file_cache";

    public static final String CACHE_FILE_PATH = FileUtils.getSDCardPath() + "/" + AppUtils.getAppName() + "/" + "file_cache";

    public static final String CACHE_ALL_FILE_KEY = "all_file_list";

    public static LinkedList<String> files = new LinkedList<>();

    public static LinkedList<Row> rowsVideo = new LinkedList<>();
    public static String[] VIDEO_PREFIX = new String[]{
            "mp4", "flv", "avi", "3gp", "webm", "ts", "ogv", "m3u8", "asf",
            "wmv", "rm", "rmvb", "mov", "mkv","f4v","mpg","mpeg",
            "mpeg1","mpeg2","xvid","dvd","vcd","vob","divx"
    };


    public static LinkedList<Row> rowsDocumnet = new LinkedList<>();
    public static String[] DOCUMNET_PREFIX = new String[]{
            "docx", "doc", "xls", "xlsx", "pdf", "ppt"
    };


    public static LinkedList<Row> rowsImage = new LinkedList<>();
    public static String[] IMAGE_PREFIX = new String[]{
            "jpg", "png", "jpeg", "gif", "bmp"
    };
    public static LinkedList<Row> rowsMusic = new LinkedList<>();
    public static String[] MUSIC_PREFIX = new String[]{
            "mp3", "wma", "amr", "ape"
    };

    public static void scanFile(String path) {

        long startTime = System.currentTimeMillis();
        scanFile(new File(path));

        long endTime = System.currentTimeMillis();
        L.i("============scanFile===========" + "程序运行时间：" + (endTime - startTime) + "ms");
        L.i("============scanFile===========" + files.size());
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
        for (int i = 0; i < fs.length; i++) {
            File file1 = fs[i];
            //RowObject row=new RowObject();
            //row.put("filePath", file1.getAbsolutePath());
            if (file1.isDirectory()) {
                scanFile(file1);
            }
            files.add(file1.getAbsolutePath());
        }
    }

    public static void scanFile(File file, Handler handler) {
        File[] fs = file.listFiles();
        for (int i = 0; i < fs.length; i++) {
            File file1 = fs[i];
            //RowObject row=new RowObject();
            //row.put("filePath", file1.getAbsolutePath());
            //if(!file1.getName().startsWith(".")){
            //&&!file1.getName().equals("Android")
            if (file1.isDirectory()) {
                scanFile(file1, handler);
            }
            //files.add(file1.getAbsolutePath());
            if (isPrefixOf(file1.getAbsolutePath(), IMAGE_PREFIX)) {
                Row row = getBaseRow(file1, i);
                rowsImage.add(row);
            } else if (isPrefixOf(file1.getAbsolutePath(), DOCUMNET_PREFIX)) {
                Row row = getBaseRow(file1, i);
                rowsDocumnet.add(row);
            } else if (isPrefixOf(file1.getAbsolutePath(), VIDEO_PREFIX)) {
                Row row = getBaseRow(file1, i);
                rowsVideo.add(row);
            } else if (isPrefixOf(file1.getAbsolutePath(), MUSIC_PREFIX)) {
                Row row = getBaseRow(file1, i);
                rowsMusic.add(row);
            }
            //}
        }
        Message message = new Message();
        message.obj = file.getAbsolutePath();
        handler.sendMessage(message);
    }

    private static Row getBaseRow(File file1, int i) {
        Row row = new Row();
        row.put("_id", i);
        row.put("name", file1.getName());
        row.put("isHidden", file1.isHidden());
        row.put("title", file1.getName());
        row.put("_display_name", file1.getName());
        row.put("_size", file1.length());
        row.put("filePath", file1.getAbsolutePath());
        return row;
    }

    public static void scanFile(final Handler handler) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                files.clear();

                File file = new File(FileUtils.getSDCardPath());
                //scanByThreadPool(handler);
                clearData();
                scanFile(file, handler);
                SPUtils.clear("rowsImage");
                SPUtils.clear("rowsDocumnet");
                SPUtils.clear("rowsVideo");
                SPUtils.clear("rowsMusic");
                SPUtils.saveRows("rowsImage", "rowsImage", rowsImage);
                SPUtils.saveRows("rowsDocumnet", "rowsDocumnet", rowsDocumnet);
                SPUtils.saveRows("rowsVideo", "rowsVideo", rowsVideo);
                SPUtils.saveRows("rowsMusic", "rowsMusic", rowsMusic);
                handler.sendEmptyMessage(1);
            }


        }).start();
    }

    public static void clearData() {
        rowsImage.clear();
        rowsDocumnet.clear();
        rowsVideo.clear();
        rowsMusic.clear();
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


    public static void initFileFromCache(final Handler handler) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                LinkedList<Row>  rowsI = SPUtils.getRows("rowsImage","rowsImage");
                LinkedList<Row> rowsD = SPUtils.getRows("rowsDocumnet","rowsDocumnet");
                LinkedList<Row> rowsV = SPUtils.getRows("rowsVideo","rowsVideo");
                LinkedList<Row> rowsM = SPUtils.getRows("rowsMusic","rowsMusic");
                if (rowsI == null &&rowsD == null &&rowsV == null &&rowsM == null) {
                    FileServer.scanFile(handler);
                } else {
                    rowsImage.clear();
                    rowsDocumnet.clear();
                    rowsMusic.clear();
                    rowsVideo.clear();
                    if(rowsI!=null){
                        rowsImage.addAll(rowsI);
                    }
                    if(rowsD!=null){
                        rowsDocumnet.addAll(rowsD);
                    }
                    if(rowsV!=null){
                        rowsVideo.addAll(rowsV);
                    }
                    if(rowsM!=null){
                        rowsMusic.addAll(rowsM);
                    }
                    handler.sendEmptyMessage(1);
                    L.i("============setListView==========="+FileServer.rowsImage.size());
                    L.i("============setListView==========="+FileServer.rowsDocumnet.size());
                    L.i("============setListView==========="+FileServer.rowsVideo.size());
                    L.i("============setListView==========="+FileServer.rowsMusic.size());
                }
            }
        }).start();
    }

    /**
     * 通过后缀名筛选文件
     *
     * @param FILE_TYPE
     */

    public static void getByPrefix(LinkedList<Row> rows, String[] FILE_TYPE) {
        rows.clear();
        L.i("============getByPrefix===========" + files.size());
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < files.size(); i++) {
            //RowObject row = files.get(i);
            //String filePath=row.getString("filePath");
            String filePath = files.get(i);
            if (isPrefixOf(filePath, FILE_TYPE)) {
                //RowObject row = new RowObject();
                //row.put("_id", i);
                //row.put("type", "document");
                //File file1 = new File(filePath);
                //row.put("name", file1.getName());
                //row.put("isHidden", file1.isHidden());
                //row.put("title", file1.getName());
                //row.put("_display_name", file1.getName());
                //row.put("_size", file1.length());
                //row.put("filePath", filePath);
                //rows.add(row);
            }
        }
        long endTime = System.currentTimeMillis();
        L.i("============scanFile===========" + "程序运行时间：" + (endTime - startTime) + "ms");
    }


    /**
     * 匹配后缀名
     *
     * @param filePath
     * @param FILE_TYPE
     * @return
     */
    public static boolean isPrefixOf(String filePath, String[] FILE_TYPE) {
        //boolean isDocumnet = false;
        for (int i = 0; i < FILE_TYPE.length; i++) {
            String s = "." + FILE_TYPE[i];
            if (filePath.contains(s) && filePath.endsWith(s)) {
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


    /**
     * 根据当前文件路径列出所有文件夹和文件
     *
     * @param rows
     * @param curFilePath
     */
    public static LinkedList<Row> sortRows(List<Row> rows, String curFilePath) {
        Row tempRowDir = new Row();
        Row tempRowFile = new Row();
        for (int i = 0; i < rows.size(); i++) {
            Row row = rows.get(i);
            String filePath = row.getString("filePath");
            if (filePath.startsWith(curFilePath)) {
                //RowObject row = StringUtils.deepCopyObject(row);
                String nextFileName = filePath.substring(curFilePath.length(), filePath.length());
                if (nextFileName.indexOf("/") > 0) {
                    nextFileName = nextFileName.substring(0, nextFileName.indexOf("/"));
                    row.put("name", nextFileName);
                    //单个文件大小
                    Long size = row.getLong("_size");
                    if (size == null) {
                        size = 0l;
                    }
                    if (tempRowDir.getRow(nextFileName) != null &&
                            tempRowDir.getRow(nextFileName).getLong("file_size") != null) {
                        Long aLong = tempRowDir.getRow(nextFileName).getLong("file_size");
                        size = size + aLong;
                    }
                    if (tempRowDir.getRow(nextFileName) != null) {
                        int sum = tempRowDir.getRow(nextFileName).getInteger("sum");
                        sum = sum + 1;
                        row.put("sum", sum);
                    } else {
                        row.put("sum", 1);
                    }

                    //文件夹中的文件大小
                    row.put("file_size", size);
                    row.put("isDir", true);
                    tempRowDir.put(nextFileName, row);
                } else {
                    row.remove("sum");
                    row.put("isDir", false);
                    row.put("name", row.getString("_display_name"));
                    tempRowFile.put(row.getString("_id"), row);
                }
            }
        }
        LinkedList<Row> tempRowsFile = new LinkedList<>();
        for (Object row : tempRowDir.values()) {
            tempRowsFile.add((Row) row);
        }
        L.i("============sortRows===========" + tempRowFile);
        for (Object row : tempRowFile.values()) {

            ((Row) row).remove("sum");
            tempRowsFile.add((Row) row);
        }
        return tempRowsFile;
    }

    public static Item fileToItem(String filePath){
        return fileToItem(new File(filePath));
    }


    public static Item fileToItem(File file){
        //if ("png".endsWith(file.getName())||"jpeg".endsWith(file.getName())||"jpg".endsWith(file.getName())) {
        String absolutePath = file.getAbsolutePath();
        String id=MediaServer.serverInflat.size()+"";
        String url = "http:/" + MediaServer.getAddress() + "/"
                + id;
        MediaServer.serverInflat.put(id,absolutePath);
        String mimeType = FileUtils.getMIMEType(absolutePath);
        String[] split = mimeType.split("/");
        Res res = new Res(new MimeType(split[0],split[1]), (long) 0, url);
        ImageItem imageItem = new ImageItem(id,"4", file.getName(),
                "", res);
        return imageItem;
        //}
        //return null;
    }


}
