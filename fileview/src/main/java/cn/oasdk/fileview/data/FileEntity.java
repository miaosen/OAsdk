package cn.oasdk.fileview.data;

import java.io.Serializable;


public class FileEntity implements Serializable {


    public String id;//id
    public String fileName;//文件名称
    public String dir;//所在目录
    public Integer dirCount;//
    public Integer fileCount;//
    public String path;//文件路径
    public String createTime;//创建时间
    public String suffix;//文件后缀名
    public Boolean isDir = false;//是否文件夹
    public Long fileSize = 0l;//文件大小
    public String fileSizeText;//文件大小(转换)
    public String fileType;//文件类型
    public String extra;//额外信息

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getDir() {
        return dir;
    }

    public void setDir(Boolean dir) {
        isDir = dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }


    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileSizeText() {
        return fileSizeText;
    }

    public void setFileSizeText(String fileSizeText) {
        this.fileSizeText = fileSizeText;
    }

    public Integer getDirCount() {
        return dirCount;
    }

    public void setDirCount(Integer dirCount) {
        this.dirCount = dirCount;
    }

    public Integer getFileCount() {
        return fileCount;
    }

    public void setFileCount(Integer fileCount) {
        this.fileCount = fileCount;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }
}
