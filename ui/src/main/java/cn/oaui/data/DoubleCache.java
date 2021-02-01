package cn.oaui.data;


import java.io.File;
import java.util.LinkedList;
import java.util.List;

import cn.oaui.utils.AppUtils;
import cn.oaui.utils.FileUtils;
import cn.oaui.utils.JsonUtils;
import cn.oaui.utils.SPUtils;

/**
 *  双缓存，文件和应用缓存各存一份，防止误删
 */
public class DoubleCache {

    public static String cacheDir=".cache";
    public static String SP_CACHE_KEY=".cache";


    public static String getCacheDir(){
        String dir = AppUtils.getDefaultDirectory() + "/" + cacheDir;
        File file=new File(dir);
        if(file.isFile()){
            file.delete();
        }
        if(!file.exists()){
            file.mkdirs();
        }
        return dir;
    }

    /**
     * 获取json文本后转rowObject
     * @param fileName
     * @return
     */
    public static Row getRow(String fileName){
        Row rowSp = SPUtils.getRow(fileName, SP_CACHE_KEY);
        String filePath=getCacheDir() + "/" + fileName;
        String text = FileUtils.readFile(new File(filePath));
        Row rowFile = JsonUtils.jsonToRow(text);
       if(rowSp==null&&rowFile!=null){
           SPUtils.saveRow(fileName,SP_CACHE_KEY,rowFile);
            return rowFile;
        }else if(rowSp!=null&&rowFile==null){
           FileUtils.writeFile(text,filePath);
            return rowSp;
        }else if(rowSp!=null&&rowFile!=null){
           return rowSp;
       }else{
            return null;
        }
    }

    /**
     * @param fileName
     * @param value
     * @return
     */
    public static void saveRow(String fileName,  Row value){
        String path=getCacheDir()+"/"+fileName;
        SPUtils.saveRow(fileName,SP_CACHE_KEY,value);
        FileUtils.writeFile(JSONSerializer.toJSONString(value),path);
    }


    /**
     * @param fileName
     * @param value
     * @return
     */
    public static boolean saveRows(String fileName, List<Row> value){
        String path=getCacheDir()+"/"+fileName;
        boolean b = SPUtils.saveRows(fileName, SP_CACHE_KEY, value);
        boolean b1 = FileUtils.writeFile(JSONSerializer.toJSONString(value), path);
        return b&&b1;

    }

    /**
     * @param fileName
     * @param value
     * @return
     */
    public static void addRows(String fileName, List<Row> value){
        String path=getCacheDir()+"/"+fileName;
        LinkedList<Row> rows = getRows(fileName);
        if(rows==null){
            rows=new LinkedList<>();
        }
        rows.addAll(value);
        SPUtils.saveRows(fileName,SP_CACHE_KEY,rows);
        FileUtils.writeFile(JSONSerializer.toJSONString(rows),path);
    }

    /**
     * @param fileName
     * @param value
     * @return
     */
    public static void addRow(String fileName, Row value){
        LinkedList<Row> rows = getRows(fileName);
        if(rows==null){
            rows=new LinkedList<>();
        }
        rows.add(value);
        SPUtils.saveRows(fileName,SP_CACHE_KEY,rows);
        String path=getCacheDir()+"/"+fileName;
        FileUtils.writeFile(JSONSerializer.toJSONString(rows),path);
    }

    /**
     * 获取json文本后转rowObject
     * @param fileName
     * @return
     */
    public static LinkedList<Row> getRows(String fileName){
        LinkedList<Row> rowsSp = SPUtils.getRows(fileName, SP_CACHE_KEY);
        String filePath=getCacheDir() + "/" + fileName;
        String text = FileUtils.readFile(new File(filePath));
        LinkedList<Row> rowsFile =JsonUtils.jsonToRows(text);
        if(rowsSp==null&&rowsFile.size()>0){
            SPUtils.saveRows(fileName,SP_CACHE_KEY,rowsFile);
            return rowsFile;
        }else if(rowsSp!=null&&rowsSp.size()>0&&rowsFile.size()==0){
            FileUtils.writeFile(text,filePath);
            return rowsSp;
        }else if(rowsSp!=null&&rowsFile.size()>0){
            return rowsFile;
        }else{
            return new LinkedList<Row>();
        }
    }

    /**
     * 清空内容
     * @param fileName
     * @return
     */
    public static void clear(String fileName){
       SPUtils.clear(fileName);
       File file=new File(getCacheDir()+"/"+fileName);
       file.delete();
    }

}
