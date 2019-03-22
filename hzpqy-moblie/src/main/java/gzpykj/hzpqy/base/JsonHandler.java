package gzpykj.hzpqy.base;

import com.oaui.data.RowObject;
import com.oaui.utils.JsonUtils;

import java.io.Serializable;

/**
 * @author zengmiaosen
 * @email 1510809124@qq.com
 * @CreateDate 2018-06-14  16:39
 * @Descrition
 */

public class JsonHandler implements Serializable {

    String context;

    RowObject rowObject;

    String message;

    public JsonHandler(String context){
         this.context=context;
    }

    public boolean isSuccess(){
        boolean success=false;
        if(getAsRow()!=null){
           String su= getAsRow().getString("success");
            if("true".equals(su)){
                success=true;

            }
        }
        return success;
    }


    public RowObject getAsRow(){
       if(rowObject==null&&JsonUtils.isCanToRow(context)) {
            rowObject = JsonUtils.jsonToRow(context);
        }
        return rowObject;
    }



    public String getMessage(){
        if(getAsRow()!=null){
           message= getAsRow().getString("message");
        }
        return message;
    }


}
