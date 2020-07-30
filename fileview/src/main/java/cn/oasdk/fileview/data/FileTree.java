package cn.oasdk.fileview.data;

import java.io.File;

import cn.oaui.L;
import cn.oaui.data.Row;

public class FileTree {

    public static Row scanFile(String path) {
        long startTime = System.currentTimeMillis();
        Row row = new Row();


        scanFile(new File(path), row);
        L.i("======scanFile===== "+ row.getRow("MIUI").getString("size"));
        L.i("======scanFile===== "+ row.getLayerData("MIUI.size"));
        long endTime = System.currentTimeMillis();
        L.i("============scanFile===========" + "程序运行时间：" + (endTime - startTime) + "ms");
//        L.i("============scanFile===========" + files.size());
//        ViewUtils.toast("程序运行时间：" + (endTime - startTime) + "ms,文件数量："+ files.size());
        //long startTime1 = System.currentTimeMillis();
        return row;
    }

    public static void scanFile(File file, Row row) {
        File[] fs = file.listFiles();
        //.nomedia处理
        long size=0;
        for (int i = 0; i < fs.length; i++) {
            File file1 = fs[i];
//            if (!file1.getName().startsWith(".")) {
//            &&!file1.getName().equals("Android")
                if (file1.isDirectory()
//                        && !file1.getName().startsWith(".")
//                        && !hasNoMediaFile(file1)
                ) {
                    Row rowNext=new Row();
                    row.put(file1.getName(),rowNext);
                    scanFile(file1, rowNext);
                    size=size+rowNext.getLong("size");
                }
//            }
            size=size+file1.length();
        }
        row.put("name",file.getName());
        row.put("size",size);
//        row.put("size2", FileUtils.formetFileSize(size));
    }


    /**
     * 文件是否包含.nomedia文件
     *
     * @param file1
     * @return
     */
    public static boolean isIngore(File file1) {
        return file1.getName().startsWith(".")
                || hasNoMediaFile(file1);
    }

    /**
     * 文件是否包含.nomedia文件
     *
     * @param file1
     * @return
     */
    public static boolean hasNoMediaFile(File file1) {
        File file = new File(file1.getPath(), ".nomedia");
        return file.exists();
    }
}
