package cn.oasdk.webview;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import cn.oasdk.base.BaseAct;
import cn.oasdk.fileview.R;
import cn.oaui.L;
import cn.oaui.ResourceHold;
import cn.oaui.annotation.InjectReader;
import cn.oaui.annotation.ViewInject;
import cn.oaui.data.DoubleCache;
import cn.oaui.data.Row;
import cn.oaui.utils.ViewUtils;
import cn.oaui.view.dialog.extra.TipDialog;
import cn.oaui.view.dialog.extra.WindowTipDialog;
import cn.oaui.view.listview.BaseFillAdapter;

/**
 * @author zengmiaosen
 * @email 1510809124@qq.com
 * @CreateDate 2020-07-23  10:51
 * @Descrition
 */
public class WebCollectAct extends BaseAct {

    public final static String FILE_COLLECTION = "file_collection";

    public final static int CODE_COLLECTION = 1;

    public final static String KEY_ISCHECKED = "isChecked";

    @ViewInject
    ListView listView;
    public ListAdapter listAdapter;
    LinkedList<Row> rows;
    @ViewInject
    LinearLayout ln_back;

    boolean isEditMode = false;

    @ViewInject
    LinearLayout ln_edit_tail, ln_delete;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.collect_act);
        InjectReader.injectAllFields(this);
        ViewUtils.finishByClick(ln_back);
        rows = DoubleCache.getRows(FILE_COLLECTION);
        if (rows != null) {
            listAdapter = new ListAdapter(context, rows, R.layout.collect_view_item);
            listAdapter.setOnItemClickListener(new BaseFillAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View convertView, Row row, int position, BaseFillAdapter.ViewHolder holder) {
                    L.i("============onItemClick===========" + isEditMode);
                    if (isEditMode) {
                        CheckBox cb = (CheckBox) holder.views.get("cb");
                        Boolean checked = !row.getBoolean(KEY_ISCHECKED);
                        row.put(KEY_ISCHECKED, checked);
                        L.i("============onItemClick===========" + row);
                        cb.setChecked(checked);
                    } else {
                        Intent intent = new Intent();
                        intent.putExtra("url", row.getString("url"));
                        setResult(CODE_COLLECTION, intent);
                        finish();
                    }

                }
            });
            listView.setAdapter(listAdapter);
        }
        ln_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TipDialog tipDialog = new TipDialog(context);
                tipDialog.setTitle(ResourceHold.getString(R.string.tip));
                tipDialog.setOnSureListener(new WindowTipDialog.OnSureListener() {
                    @Override
                    public void onSure(DialogInterface dialog) {
                        deleteChecked();
                        dialog.dismiss();
                    }
                });
                tipDialog.setText("是否删除选中的书签？");
                tipDialog.show();
            }
        });
    }

    private void deleteChecked() {
        Iterator<Row> it = rows.iterator();
        while (it.hasNext()) {
            Row row = it.next();
            if (row.getBoolean(KEY_ISCHECKED)) {
                it.remove();
            }
        }
        saveCollections(rows);
        listAdapter.notifyDataSetChanged();
    }


    @Override
    protected void onStart() {
        super.onStart();
        //rows = DoubleCache.getRows(FILE_COLLECTION);
        //listAdapter.notifyDataSetChanged();
        //L.i("============onStart==========="+rows);
    }

    public class ListAdapter extends BaseFillAdapter {
        public ListAdapter(Context context, LinkedList<Row> rows, int layout) {
            super(context, rows, layout);
        }

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void setItem(final View convertView, final Row row, final int position, final ViewHolder holder) {
            CheckBox cb = (CheckBox) holder.views.get("cb");
            cb.setChecked(row.getBoolean(KEY_ISCHECKED));
            if (isEditMode) {
                cb.setVisibility(View.VISIBLE);
            } else {
                cb.setVisibility(View.GONE);
            }
            convertView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    cb.setChecked(true);
                    row.put(KEY_ISCHECKED, true);
                    openEditMode();
                    return true;
                }
            });
        }
    }

    private void openEditMode() {
        isEditMode = true;
        listAdapter.notifyDataSetChanged();
        ln_edit_tail.setVisibility(View.VISIBLE);
    }


    public static void saveCollection(Row row) {
        LinkedList<Row> rows = DoubleCache.getRows(FILE_COLLECTION);
        rows.add(0, row);
        DoubleCache.saveRows(FILE_COLLECTION, rows);
    }

    public static void saveCollections(List<Row> rows) {
        DoubleCache.saveRows(FILE_COLLECTION, rows);
    }

    public static LinkedList<Row> getCollection() {
        return DoubleCache.getRows(FILE_COLLECTION);
    }

    @Override
    public void onBackPressed() {
        if (isEditMode) {
            closeEditMode();
        } else {
            super.onBackPressed();
        }
    }

    private void closeEditMode() {
        isEditMode = false;
        ln_edit_tail.setVisibility(View.GONE);
        listAdapter.notifyDataSetChanged();
    }
}
