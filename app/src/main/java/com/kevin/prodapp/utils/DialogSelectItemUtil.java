package com.kevin.prodapp.utils;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import com.kevin.prodapp.R;
import com.kevin.prodapp.interfaces.SortPopCallBack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by JQ on 2017/8/6.
 */

public class DialogSelectItemUtil {
    private Context context = null;
    private Dialog shongHuoDialog;
    private List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
    private SortPopCallBack callback = null;

    private View view = null;
    private LayoutInflater inflater = null;
    private ListView lv_pop;
    private SimpleAdapter adapter;

    public DialogSelectItemUtil(Context mContext, SortPopCallBack callback, List<Map<String, Object>> pData) {
        this.context = mContext;
        this.callback = callback;
        init();
        setData(pData);
    }

    @SuppressLint("WrongConstant")
    private void init() {
        inflater = (LayoutInflater) context.getSystemService("layout_inflater");
        view = inflater.inflate(R.layout.dialog_select_list, null);
        lv_pop = (ListView) view.findViewById(R.id.lv_pop);
        lv_pop.setOnItemClickListener(itemClick);

        adapter = new SimpleAdapter(context, data,
                R.layout.dialog_select_item, new String[] { "title" },
                new int[] { R.id.tv1 });
        lv_pop.setAdapter(adapter);
    }

    public void showDialog() {
        if(shongHuoDialog == null) {
            DisplayMetrics dm = context.getResources().getDisplayMetrics();
            shongHuoDialog = new Dialog(context, R.style.custom_dialog);// 创建自定义样式dialog
            shongHuoDialog.setCancelable(true);// 不可以用“返回键”取消
            shongHuoDialog.setContentView(view, new LinearLayout.LayoutParams(
                    dm.widthPixels * 4 / 5,
                    LinearLayout.LayoutParams.MATCH_PARENT));// 设置布局
        }
        if(!shongHuoDialog.isShowing()) {
            shongHuoDialog.show();
        }
    }

    public void dismissDialog() {
        if(shongHuoDialog!=null && shongHuoDialog.isShowing()) {
            shongHuoDialog.dismiss();
        }
    }

    private void setData(List<Map<String, Object>> pData) {
        data.clear();
        if(pData!=null) {
            data.addAll(pData);
        }
        if(data.size() > 6) {
            LinearLayout.LayoutParams lp= (LinearLayout.LayoutParams)lv_pop.getLayoutParams();
            lp.height = dip2px(context, 6 * 40);
            lv_pop.setLayoutParams(lp);
        }
        adapter.notifyDataSetChanged();
    }

    /** 把dip——>px */
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    private AdapterView.OnItemClickListener itemClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                long arg3) {
            // TODO Auto-generated method stub
            if(callback!=null) {
                Map<String,Object> map = data.get((int)arg3);
                callback.itemClick((int)arg3,map);
            }
        }
    };
}
