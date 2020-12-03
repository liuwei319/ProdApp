package com.kevin.prodapp.ui.list;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.kevin.prodapp.R;
import com.kevin.prodapp.utils.HttpsUrlConnection;
import com.kevin.prodapp.utils.SharePManager;
import com.kevin.prodapp.utils.SysApplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;


public class MoreInfoActivity extends Activity {
    private List<HashMap<String, Object>> list;
    private EditText etText;
    private ImageView ivDeleteText;
    private JSONArray dataArray;
    private ProgressDialog dialog;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Bundle bunde;
    private String path1;
    private String path;
    private String params1;
    private Map<String, String> map;
    private JSONObject body;
    private MyAdapter adapter;
    private ListView listview;
    private String xtStatus ="";
    private  ArrayList<String> listXtstatus=new ArrayList<>();;
    private SharePManager sPManger;
    /**
     * 列表的数据源
     */
    private List<String> listData;
    /**
     * 记录选中item的下标
     */
    private List<Integer> checkedIndexList = new ArrayList<>();
    /**
     * 保存每个item中的checkbox
     */
    private List<CheckBox> checkBoxList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.more_info);
        sPManger = new SharePManager(this, SharePManager.USER_FILE_NAME);//获取本地数据库信息

//        initListData();
//退出
        SysApplication.getInstance().addActivity(this);
        etText = findViewById(R.id.etSearch);
//        ivDeleteText = findViewById(R.id.ivDeleteText);
        bunde = this.getIntent().getExtras();
        String id = new String(bunde.getString("project_id"));
        String titleName = new String(bunde.getString("titleName"));

        TextView title = findViewById(R.id.txt_title);
        title.setText(titleName);
        String projectId = id.substring(0, id.indexOf("|"));
        listview = (ListView) findViewById(R.id.defectList);

        List<Map<String, String>> defectList = new ArrayList<>();
        try {
            Properties props = new Properties();
            props.load(getApplicationContext().getAssets().open("config.properties"));
            String serverurl = props.getProperty("servers_url");;
            //第一次从配置文件中获取ip,修改过ip之后从缓存获取ip
            String sPMangerIP=sPManger.getString("servers_url");
            if(sPMangerIP!=null&&!"".equals(sPMangerIP)){
                serverurl=sPMangerIP;
            }
            path = serverurl + "/getDefectsList";
            SharePManager sharePManager = new SharePManager(MoreInfoActivity.this, SharePManager.USER_FILE_NAME);
            String cookie = sharePManager.getString("cookie");
            String jksessionid = sharePManager.getString("jksessionid");
            map = new HashMap<>();
            map.put("cookie", cookie);
            map.put("jksessionid", jksessionid);
            body = new JSONObject();
            body.put("value", projectId);

            params1 = String.valueOf(body);
            path1 = serverurl + "/setProjectSession";
            final HttpsUrlConnection[] httpsUrlConnection = {new HttpsUrlConnection()};
            httpsUrlConnection[0].post(path1, params1, map, MoreInfoActivity.this);

            String params = String.valueOf(body);
            httpsUrlConnection[0] = new HttpsUrlConnection();
            String result = httpsUrlConnection[0].post(path, params, map, MoreInfoActivity.this);
            JSONObject object = new JSONObject(result);
            if (object.get("code").toString().equals("0")) {
                JSONArray data = new JSONArray(object.getString("data"));
                dataArray = data;

                for (int i = 0; i <data.length() ; i++) {
                    listXtstatus.add(i,data.getJSONObject(i).getString("xtStatus"));
                }

                setData(data);
            }
//            list = new ArrayList<HashMap<String, Object>>();
            listData = new ArrayList<String>();

            for (int i = 0; i < list.size(); i++) {
                listData.add(dataArray.getJSONObject(i).get("xtSid").toString());
            }
            adapter = new MyAdapter(this);




            listview.setAdapter(adapter);


            listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @SuppressWarnings("unused")
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1,
                                        int position, long id) {
                    final ProgressDialog dialog = ProgressDialog.show(MoreInfoActivity.this, "", "正在加载...");
                    new Thread() {
                        public void run() {
                            try {
                                sleep(1000);
                            } catch (Exception e) {
                                Log.e("tag", e.getMessage());
                            }
                            dialog.dismiss();
                        }
                    }.start();
                    String data = list.get(position).get("data").toString();
                    try {
                        Properties props = new Properties();
                        props.load(MoreInfoActivity.this.getAssets().open("config.properties"));
                        String serverUrl = props.getProperty("servers_url");;
                        //第一次从配置文件中获取ip,修改过ip之后从缓存获取ip
                        String sPMangerIP=sPManger.getString("servers_url");
                        if(sPMangerIP!=null&&!"".equals(sPMangerIP)){
                            serverUrl=sPMangerIP;
                        }
                        String path1 = serverUrl + "/getformdata";
                        SharePManager sharePManager = new SharePManager(MoreInfoActivity.this, SharePManager.USER_FILE_NAME);
                        String cookie = sharePManager.getString("cookie");
                        String jksessionid = sharePManager.getString("jksessionid");
                        Map<String, String> map = new HashMap<>();
                        map.put("cookie", cookie);
                        map.put("jksessionid", jksessionid);
                        String param = data;
                        JSONObject json = new JSONObject(data);
                        String xtSid = json.getString("xtSid");
                        HttpsUrlConnection httpsUrlConnection = new HttpsUrlConnection();
                        String result1 = httpsUrlConnection.post(path1, xtSid, map, MoreInfoActivity.this);
                        JSONObject jsonObject = new JSONObject(result1);
                        JSONObject jsonObject1 = new JSONObject(jsonObject.get("data").toString());
                        JSONObject jsonObject2 = new JSONObject(jsonObject1.getString("dataItem"));
                        String drUser = jsonObject2.getString("DRespUser");
                        xtStatus = json.getString("xtStatus");
                        if (xtStatus.equals("3901")) {//新建状态
                            Intent intent = new Intent();
                            intent.putExtra("dpUser", drUser);
                            Bundle bundle = new Bundle();
                            bundle.putString("data", data);
                            intent.putExtras(bundle);
                            intent.setClass(MoreInfoActivity.this, DefectDetailActivity.class);
                            startActivity(intent);
                        } else if (xtStatus.equals("3902")) {//分配
                            Intent intent = new Intent();
                            intent.putExtra("dpUser", drUser);
                            Bundle bundle = new Bundle();
                            bundle.putString("data", data);
                            intent.putExtras(bundle);
                            intent.setClass(MoreInfoActivity.this, Defect2Activity.class);
                            startActivity(intent);
                        } else if (xtStatus.equals("3903")) {//打开
                            Intent intent = new Intent();
                            intent.putExtra("dpUser", drUser);
                            Bundle bundle = new Bundle();
                            bundle.putString("data", data);
                            intent.putExtras(bundle);
                            intent.setClass(MoreInfoActivity.this, Defect3Activity.class);
                            startActivity(intent);
                        } else if (xtStatus.equals("3904")) {//xiufu
                            Intent intent = new Intent();
                            intent.putExtra("dpUser", drUser);
                            Bundle bundle = new Bundle();
                            bundle.putString("data", data);
                            intent.putExtras(bundle);
                            intent.setClass(MoreInfoActivity.this, Defect4Activity.class);
                            startActivity(intent);
                        } else if (xtStatus.equals("3908")) {//拒绝
                            Intent intent = new Intent();
                            intent.putExtra("dpUser", drUser);
                            Bundle bundle = new Bundle();
                            bundle.putString("data", data);
                            intent.putExtras(bundle);
                            intent.setClass(MoreInfoActivity.this, Defect5Activity.class);
                            startActivity(intent);
                        } else if (xtStatus.equals("3905")) {//重开
                            Intent intent = new Intent();
                            intent.putExtra("dpUser", drUser);
                            Bundle bundle = new Bundle();
                            bundle.putString("data", data);
                            intent.putExtras(bundle);
                            intent.setClass(MoreInfoActivity.this, Defect3Activity.class);
                            startActivity(intent);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            //下拉刷新
//            swipeRefreshLayout = findViewById(R.id.swswsw);//初始化下拉刷新控件，
//            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//                @Override
//                public void onRefresh() {
//                    httpsUrlConnection[0].post(path1, params1, map, MoreInfoActivity.this);
//                    String params = String.valueOf(body);
//                    httpsUrlConnection[0] = new HttpsUrlConnection();
//                    String result = httpsUrlConnection[0].post(path, params, map, MoreInfoActivity.this);
//                    JSONObject object = null;
//                    try {
//                        object = new JSONObject(result);
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                    try {
//                        if (object.get("code").toString().equals("0")) {
//                            JSONArray data = new JSONArray(object.getString("data"));
//                            dataArray = data;
//                            setData(data);
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//
//
//                    Toast.makeText(MoreInfoActivity.this, "刷新", Toast.LENGTH_SHORT).show();//刷新时要做的事情
//                    swipeRefreshLayout.setRefreshing(false);//刷新完成
//                }
//            });

            etText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    // TODO Auto-generated method stub
                    //这个应该是在改变的时候会做的动作吧，具体还没用到过。

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    // TODO Auto-generated method stub
                    //这个应该是在改变之前会做的动作吧，具体还没用到过
                }

                @Override
                public void afterTextChanged(Editable s) {
                    String ss = s.toString();


                    if (s.length() == 0) {
//                        ivDeleteText.setVisibility(View.GONE);
                        setData(dataArray);
                    }else if(ss.equals("新建")){
//                        ivDeleteText.setVisibility(View.VISIBLE);
                        JSONArray array = new JSONArray();
                        for (int i = 0; i < dataArray.length(); i++) {
                            try {
                                if(dataArray.getJSONObject(i).getInt("xtStatus")==3901)array.put(dataArray.get(i));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        setData(array);
                        listview.setAdapter(adapter);

                    }else if(ss.equals("已分配")){
//                        ivDeleteText.setVisibility(View.VISIBLE);
                        JSONArray array = new JSONArray();
                        for (int i = 0; i < dataArray.length(); i++) {
                            try {
                                if(dataArray.getJSONObject(i).getInt("xtStatus")==3902)array.put(dataArray.get(i));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        setData(array);
                        listview.setAdapter(adapter);
                    }else if(ss.equals("已打开")){
//                        ivDeleteText.setVisibility(View.VISIBLE);
                        JSONArray array = new JSONArray();
                        for (int i = 0; i < dataArray.length(); i++) {
                            try {
                                if(dataArray.getJSONObject(i).getInt("xtStatus")==3903)array.put(dataArray.get(i));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        setData(array);
                        listview.setAdapter(adapter);
                    } else if(ss.equals("已修复")){
//                        ivDeleteText.setVisibility(View.VISIBLE);
                        JSONArray array = new JSONArray();
                        for (int i = 0; i < dataArray.length(); i++) {
                            try {
                                if(dataArray.getJSONObject(i).getInt("xtStatus")==3904)array.put(dataArray.get(i));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        setData(array);
                        listview.setAdapter(adapter);
                    }else if(ss.equals("拒绝")){
//                        ivDeleteText.setVisibility(View.VISIBLE);
                        JSONArray array = new JSONArray();
                        for (int i = 0; i < dataArray.length(); i++) {
                            try {
                                if(dataArray.getJSONObject(i).getInt("xtStatus")==3908)array.put(dataArray.get(i));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        setData(array);
                        listview.setAdapter(adapter);
                    }else if(ss.equals("重新打开")){
//                        ivDeleteText.setVisibility(View.VISIBLE);
                        JSONArray array = new JSONArray();
                        for (int i = 0; i < dataArray.length(); i++) {
                            try {
                                if(dataArray.getJSONObject(i).getInt("xtStatus")==3905)array.put(dataArray.get(i));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        setData(array);
                        listview.setAdapter(adapter);
                    }else{
//                        ivDeleteText.setVisibility(View.VISIBLE);
                        JSONArray array = new JSONArray();
                        for (int i = 0; i < dataArray.length(); i++) {

                            try {
                                String jsonStrng = dataArray.getString(i);
                                JSONObject object = dataArray.getJSONObject(i);
                                String deNo = object.getString("DSno");
                                String deDesc = object.getString("DSummary");
                                String title = deNo + " " + deDesc;
                                if (title.indexOf(ss) != -1) {
                                    array.put(dataArray.get(i));
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        setData(array);
                        listview.setAdapter(adapter);
                    }
                }
            });
        } catch (Exception e) {
            e.toString();
        }


        //新增缺陷
        Button defectadd = findViewById(R.id.add_btn1);
        defectadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtras(bunde);
                intent.setClass(MoreInfoActivity.this, DefectAddActivity.class);
                startActivity(intent);
            }
        });

    }

    /**
     * 初始化列表的数据源
     */
    public void initListData() {
        //静态赋值
        listData = new ArrayList<String>();
        for (int i = 0; i < list.size(); i++) {
            listData.add(list.get(i).toString());
        }
    }


    private void setData(JSONArray array) {
        list = new ArrayList<>();
        HashMap<String, Object> hashMap;

        try {
            for (int i = 0; i < array.length(); i++) {

                hashMap = new HashMap<String, Object>();
                String jsonStrng = array.getString(i);
                JSONObject object = array.getJSONObject(i);
                hashMap.put("id", object.getString("xtSid"));
                hashMap.put("name", object.getString("DSummary"));
                hashMap.put("dno", object.get("DSno"));
                hashMap.put("data", jsonStrng);
                list.add(hashMap);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //返回时刷新
    @Override
    protected void onRestart() {
        super.onRestart();
        Intent intent = getIntent();
        overridePendingTransition(0, 0);
        finish();
        overridePendingTransition(0, 0);
        startActivity(intent);
    }

//    public void click_deleteButton(View view) {
//    }

    class MyAdapter extends BaseAdapter {
        private LayoutInflater inflater;

        public MyAdapter(Context context) {
            inflater = LayoutInflater.from(context);
//            inflater = (LayoutInflater) context
//                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = inflater.inflate(R.layout.list_child2, null);
                Typeface typeface = Typeface.createFromAsset(getAssets(), "fontawesome-webfont.ttf");
//            convertView = inflater.inflate(R.layout.list_child, null, false);
                HashMap<String, Object> hashMap = new HashMap<String, Object>();
                hashMap = list.get(position);
                viewHolder.tv3 = convertView.findViewById(R.id.enter_id);
                viewHolder.tv1 = (TextView) convertView.findViewById(R.id.list_child_tv1);
                //根据不同的缺陷状态显示
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) viewHolder.tv1.getLayoutParams();
                JSONObject jo= null;
                try {
                    jo = new JSONObject(list.get(position).get("data").toString());
                    if((int)jo.get("xtStatus")==3901){
                        viewHolder.tv3.setText("[新建]");
                        lp.setMargins( 55,  0,  0,  0);
                        viewHolder.tv1.setText(hashMap.get("dno").toString() + "  " + hashMap.get("name"));
                    }else if((int)jo.get("xtStatus")==3902){
                        viewHolder.tv3.setText("[已分配]");
                       lp.setMargins( 30,  0,  0,  0);
                        viewHolder.tv1.setText(hashMap.get("dno").toString() + "  " + hashMap.get("name"));
                    }else if((int)jo.get("xtStatus")==3903){
                        viewHolder.tv3.setText("[已打开]");
                        lp.setMargins( 30,  0,  0,  0);
                        viewHolder.tv1.setText(hashMap.get("dno").toString() + "  " + hashMap.get("name"));
                    }else if((int)jo.get("xtStatus")==3904){
                        viewHolder.tv3.setText("[已修复]");
                        lp.setMargins( 30,  0,  0,  0);
                        viewHolder.tv1.setText(hashMap.get("dno").toString() + "  " + hashMap.get("name"));
                    }else if((int)jo.get("xtStatus")==3905){
                        viewHolder.tv3.setText("[重新打开]");
                        lp.setMargins( 10,  0,  0,  0);
                        viewHolder.tv1.setText(hashMap.get("dno").toString() + "  " + hashMap.get("name"));
                    }else if((int)jo.get("xtStatus")==3908){
                        viewHolder.tv3.setText("[拒绝]");
                        lp.setMargins( 55,  0,  0,  0);
                        viewHolder.tv1.setText(hashMap.get("dno").toString() + "  " + hashMap.get("name"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }




//                viewHolder.tv3.setTypeface(typeface);

                viewHolder.tv2 = (TextView) convertView.findViewById(R.id.list_child_tv2);
//                viewHolder.tv1.setText(hashMap.get("dno").toString() + "  " + hashMap.get("name"));


                viewHolder.tv2.setText(hashMap.get("id").toString());

                viewHolder.tv2.setLayoutParams(lp);
                viewHolder.tv2.setVisibility(View.GONE);

//                viewHolder.tv = (TextView) convertView.findViewById(R.id.textview1);
                viewHolder.checkbox = (CheckBox) convertView.findViewById(R.id.checkbox);

                //将item中的checkbox放到checkBoxList中
                checkBoxList.add((CheckBox) convertView.findViewById(R.id.checkbox));
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();

            }

//            viewHolder.tv.setText(listData.get(position));
            viewHolder.checkbox.setOnCheckedChangeListener(new CheckBoxListener(position));
            return convertView;


        }

        class ViewHolder {
            private TextView tv3;
            private TextView tv1;
            private TextView tv2;
            private TextView tv;
            private CheckBox checkbox;
        }
    }

    private void buildDialog() {
        dialog = new ProgressDialog(MoreInfoActivity.this);
        dialog.setTitle("加载中...");
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    }

    /**
     * checkbox的监听器
     */
    class CheckBoxListener implements CompoundButton.OnCheckedChangeListener {
        /**
         * 列表item的下标位置
         */
        int position;

        public CheckBoxListener(int position) {
            this.position = position;
        }

        @Override
        public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
            if (isChecked) {
                checkedIndexList.add(position);
            } else {
                checkedIndexList.remove((Integer) position);
            }
        }
    }


    /**
     * 删除按钮的点击事件
     */

    public void click_deleteButton(View v) throws IOException, JSONException {
        if(checkedIndexList.size()==0){
            Toast.makeText(getApplicationContext(), "请至少选择一项", Toast.LENGTH_SHORT).show();
        }else {
            new AlertDialog.Builder(MoreInfoActivity.this).setTitle("确认删除吗？")
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

//                        List<String> delXtSidList = new ArrayList<>();
                            //先将checkedIndexList中的元素从大到小排列,否则可能会出现错位删除或下标溢出的错误
                            checkedIndexList = sortCheckedIndexList(checkedIndexList);
                            for (int i = 0; i < checkedIndexList.size(); i++) {
                                //需要强转为int,才会删除对应下标的数据,否则默认删除与括号中对象相同的数据
                                String delXtSid = listData.get((int) checkedIndexList.get(i));
//                            delXtSidList.add(delXtSid);
                                listData.remove((int) checkedIndexList.get(i));

                               // checkBoxList.remove((int) checkedIndexList.get(i));
                                //删除云平台

                                Properties props = new Properties();
                                try {
                                    props.load(getApplicationContext().getAssets().open("config.properties"));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                String serverurl = props.getProperty("servers_url");;
                                //第一次从配置文件中获取ip,修改过ip之后从缓存获取ip
                                String sPMangerIP=sPManger.getString("servers_url");
                                if(sPMangerIP!=null&&!"".equals(sPMangerIP)){
                                    serverurl=sPMangerIP;
                                }
                                path = serverurl + "/getDefectsList";
                                SharePManager sharePManager = new SharePManager(MoreInfoActivity.this, SharePManager.USER_FILE_NAME);
                                String cookie = sharePManager.getString("cookie");
                                String jksessionid = sharePManager.getString("jksessionid");
                                map = new HashMap<>();
                                map.put("cookie", cookie);
                                map.put("jksessionid", jksessionid);
                                body = new JSONObject();
                                try {
                                    body.put("value", delXtSid);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                String params2 = String.valueOf(body);
                                String path2 = serverurl + "/deleteDefects";
                                final HttpsUrlConnection[] httpsUrlConnection = {new HttpsUrlConnection()};
                                httpsUrlConnection[0].post(path2, params2, map, MoreInfoActivity.this);

                            }

                            //更新数据源
                            adapter.notifyDataSetChanged();
                            //清空checkedIndexList,避免影响下一次删除
                            checkedIndexList.clear();

                            try {
                                HttpsUrlConnection httpsUrlConnection = new HttpsUrlConnection();
                                String result = httpsUrlConnection.post(path, params1, map, MoreInfoActivity.this);
                                JSONObject object = new JSONObject(result);
                                if (object.get("code").toString().equals("0")) {
                                    JSONArray data = new JSONArray(object.getString("data"));
                                    dataArray = data;
                                    setData(data);
                                }
                                listData = new ArrayList<String>();
                                for (int i = 0; i < list.size(); i++) {
                                    listData.add(dataArray.getJSONObject(i).get("xtSid").toString());
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            adapter = new MyAdapter(MoreInfoActivity.this);
                            listview.setAdapter(adapter);

                        }
                    }).setNegativeButton("返回", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // 点击“返回”后的操作,这里不设置没有任何操作
                }
            }).show();
        }
    }

    /**
     * 取消按钮的点击事件
     */
//    public void click_cancelButton(View v) {
//        for (int i = 0; i < checkBoxList.size(); i++) {
//            //将已选的设置成未选状态
//            checkBoxList.get(i).setChecked(false);
//            //将checkbox设置为不可见
//            checkBoxList.get(i).setVisibility(View.INVISIBLE);
//        }
//    }

    /**
     * 对checkedIndexList中的数据进行从大到小排序
     */
    public List<Integer> sortCheckedIndexList(List<Integer> list) {
        int[] ass = new int[list.size()];//辅助数组
        for (int i = 0; i < list.size(); i++) {
            ass[i] = list.get(i);
        }
        Arrays.sort(ass);
        list.clear();
        for (int i = ass.length - 1; i >= 0; i--) {
            list.add(ass[i]);
        }
        return list;
    }
}

