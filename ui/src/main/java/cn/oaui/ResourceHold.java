package cn.oaui;


import android.graphics.drawable.Drawable;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @Created by com.gzpykj.com
 * @author zms
 * @Date 2016-3-8
 * @Descrition 资源缓存处理模块，通过反射获取R文件的资源文件(gen文件夹下可以看到),并静态化处理
 */
public class ResourceHold {

	private final String TAG =getClass().getSimpleName();

	enum MapType{
		TYPE_ID2NAME,TYPE_NAME2ID
	}



	/**
	 * key为资源类型，value为存放一个key为资源字段名称和一个vaule为资源id的值 的map
	 * 
	 */
	public static Map<String, Map<String, Integer>> name2idResounceMap=new LinkedHashMap<String, Map<String, Integer>>();

	/**
	 * key为资源类型，value和楼上相反,为存放一个key为资源id的值和一个vaule为资源id的值对应字段名称 的map
	 * (和上面的nameResounceMap相反)
	 */
	public static Map<String, Map<Integer, String>> id2nameResounceMap=new LinkedHashMap<String, Map<Integer, String>>();


	private static String packageName = UIGlobal.getApplication()
			.getPackageName();

	public static int getLayoutByName(String name) {
		return getResounceByName("layout", name);
	}

	/**
	 *  通过id的字段名称获取资源id
	 * @param name
	 * @return
	 */
	public static int getIdByName(String name) {
		return getResounceByName("id", name);
	}

	public static int getDrawableByName(String name) {
		return getResounceByName("drawable", name);
	}

	public static int getRawByName(String name) {
		return getResounceByName("raw", name);
	}

	public static int getColorByName(String name) {
		return getResounceByName("color", name);
	}

	public static String getStringByName(String name) {
		return UIGlobal.getApplication().getResources()
				.getString(getResounceByName("string", name));
	}

	/**
	 * 通过名称获取资源ID的字段名称
	 * @param resType
	 * @param name
	 * @return
	 */
	public static int getResounceByName(String resType, String name) {
		int id = 0;
		if (name2idResounceMap == null) {
			name2idResounceMap = new HashMap<String, Map<String, Integer>>();
			// getClassResounce(resType);
		}
		Map<String, Integer> resounce = getResounce(resType, MapType.TYPE_NAME2ID);
		if(resounce==null||resounce.get(name)==null){
			id=0;
		}else{
			id = resounce.get(name);
		}
		//L.i("strName="+name+"====="+id);
		return id;
	}


	/**
	 * 通过资源类型来获取名称
	 *
	 * @param resType
	 * @param name
	 * @return
	 */
	public static String getNameByResounce(String resType, int name) {
		String strName=null;
		Map<Integer, String> resounce = getResounce(resType, MapType.TYPE_ID2NAME);
		if(resounce==null||resounce.get(name)==null){
			strName=null;
		}else{
			strName = resounce.get(name);
		}

		return strName;
	}

	/**
	 * 通过资源类型获取类文件
	 * 
	 * @param restype
	 *            资源类型
	 * @return 类文件
	 */
	public static Map getResounce(String restype, MapType mapType) {
		if (mapType.equals(MapType.TYPE_NAME2ID)&&name2idResounceMap.get(restype) != null) {
			//L.i("strName====TYPE_NAME2ID");
			return name2idResounceMap.get(restype);
		}else if(mapType.equals(MapType.TYPE_ID2NAME)&&id2nameResounceMap.get(restype)!= null) {
			//L.i("strName====TYPE_NAME2ID");
			return id2nameResounceMap.get(restype);
		} else {
			Class<?> r = null;
			try {
				r = Class.forName(packageName + ".R");
				Class<?>[] classes = r.getClasses();
				Class<?> desireClass = null;
				for (int i = 0; i < classes.length; i++) {
					if (classes[i].getName().split("\\$")[1].equals(restype)) {
						desireClass = classes[i];
						break;
					}
				}
				if (desireClass != null) {
					Field fields[] = desireClass.getDeclaredFields();
					Map<String, Integer> name2idfieldMap = new HashMap<String, Integer>();
					Map<Integer, String> id2namefieldMap = new HashMap<Integer, String>();
					//TODO  fields的下标  eclipse从0开始，studio从1开始
					for (int i = 0; i < fields.length; i++) {
						Field field = fields[i];
						String name = field.getName();
						if(!name.startsWith("$")){
							int value = field.getInt(desireClass);
							name2idfieldMap.put(name,
									value);
							id2namefieldMap.put(value,
									name);
						}
					}
					name2idResounceMap.put(restype, name2idfieldMap);
					id2nameResounceMap.put(restype, id2namefieldMap);
					if(mapType.equals(MapType.TYPE_ID2NAME)){
						return id2nameResounceMap.get(restype);
					}else{
						return name2idResounceMap.get(restype);
					}
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			}catch (Exception e) {
				e.printStackTrace();

			}
			return null;
		}

	}


	/**
	 * 通过id来获取字段名称
	 * 
	 * @param value
	 * @return
	 */
	public static String getNameById(int value) {
		return getNameByResounce("id", value);
	}

	/**
	 * 通过布局id来获取字段名称
	 * 
	 * @param value
	 * @return
	 */
	public static String getNameByLayout(int value) {
		return getNameByResounce("layout", value);
	}

	public static String getNameByDrawable(int value) {
		return getNameByResounce("drawable", value);
	}

	public static String getNameByRaw(int value) {
		return getNameByResounce("raw", value);
	}

	public static String getNameByColor(int value) {
		return getNameByResounce("color", value);
	}

	public static String getNameByString(int value) {
		return getNameByResounce("string", value);
	}
	public static int getColor(int colorId) {
		return UIGlobal.getApplication().getResources().getColor(colorId);
	}

	public static String getString(int stringId) {
		return UIGlobal.getApplication().getResources().getString(stringId);
	}

	public static Drawable getDrawable(int drawableId) {
		return UIGlobal.getApplication().getResources().getDrawable(drawableId);
	}


}
