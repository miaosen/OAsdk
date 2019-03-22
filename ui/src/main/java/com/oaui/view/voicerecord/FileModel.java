package com.oaui.view.voicerecord;

import com.oaui.utils.AppUtils;
import com.oaui.utils.DateTimeUtils;
import com.oaui.utils.FileUtils;

import java.io.File;

/**
 * @Created by com.gzpykj.com
 * @author zms
 * @Date 2016-6-6
 * @Descrition 项目文件的输出模型，
 * 默认目录为(AppDir)，可以通过指定fileDir来相对app目录创建新目录,
 * 如果指定路径(customDir),则根据customDir来新建目录
 * 
 * 如果为空，则输出当前系统打印时间,默认每次刷新文件名称，后缀名prefix不为空，则输出prefix+name
 * 
 */
public class FileModel {

	/**
	 * 默认项目名称为基础文件夹
	 */
	public String dfBsDir = FileUtils.getSDCardPath()+ AppUtils.getAppName();
	/**
	 * 相对App目录下的目录路径
	 */
	public String nextDir=null;
	/**
	 *  App基础文件夹
	 */
	public String dir = null;
	/**
	 * 自定义基础文件夹
	 */
	public String customDir = null;
	/**
	 * 文件名称
	 */
	private String name = null;
	/**
	 * 前缀
	 */
	private String prefix = "";
	/**
	 * 后缀
	 */
	private String suffix= "";
	
	/**
	 * 文件对象
	 */
	private File file;
	
	

	public FileModel() {

	}
	
	/**
	 * 获取文件目录
	 * @return
	 */
	public String getDir() {
		if(customDir!=null){
			dir=customDir;
		}else{
			if(dir==null){
				if(nextDir!=null){
					dir=dfBsDir+nextDir;
				}else{
					dir=dfBsDir;
				}
			}
		}
		return dir;
	}


	/**
	 * 获取文件名称
	 * @return
	 */
	public String getName() {
		if (name == null) {
			name = DateTimeUtils.getCurrentTime("yyyyMMddHH:mm:ss");
		}
		return prefix+name+suffix;
	}
	
	/**
	 * 更新文件名称
	 * @return
	 */
	public void updateName() {
		name =DateTimeUtils.getCurrentTime("yyyyMMddHH:mm:ss");
	}
	
	/**
	 * 获取文件路径
	 * @return
	 */
	public String getPath() {
		return getDir()+"/"+getName();
	}
	
	
	
	/**
	 * 获取文件路径
	 * @return
	 */
	public File createFile() {
		File f=new File(getDir(), getName());
		if(f.exists()){
			file=f;
		}else{
			file=FileUtils.getOrCreateFile(getDir(),getName());
		}
		return file;
	}
	

	public void setName(String name) {
		this.name = name;
	}
	

	public String getPrefix() {
		return prefix;
	}


	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getCustomDir() {
		return customDir;
	}

	public void setCustomDir(String customDir) {
		this.customDir = customDir;
	}

	public String getDfBsDir() {
		return dfBsDir;
	}

	public void setDfBsDir(String dfBsDir) {
		this.dfBsDir = dfBsDir;
	}

	
	public void setDir(String dir) {
		this.dir = dir;
	}

	public String getNextDir() {
		return nextDir;
	}

	public void setNextDir(String nextDir) {
		this.nextDir = nextDir;
	}

	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}




}
