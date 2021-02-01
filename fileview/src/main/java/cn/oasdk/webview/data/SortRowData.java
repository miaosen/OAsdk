package cn.oasdk.webview.data;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

import cn.oaui.data.Row;

/**
 * @author zengmiaosen
 * @email 1510809124@qq.com
 * @CreateDate 2020-08-11  11:29
 * @Descrition
 */
public class SortRowData {

    public static void sortRowByFileSize(LinkedList<Row> rows) {
        Collections.sort(rows, new Comparator<Row>() {
            @Override
            public int compare(Row o1, Row o2) {
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
}
