package com.kevin.prodapp.ui.list;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.kevin.prodapp.R;
import com.kevin.prodapp.entity.Grad;
import com.kevin.prodapp.entity.Item;
import com.kevin.prodapp.entity.Province;
import com.kevin.prodapp.entity.TestCaseCatalog;
import com.kevin.prodapp.entity.TestCaseItem;
import com.kevin.prodapp.entity.TestCaseProvince;
import com.kevin.prodapp.utils.HttpsUrlConnection;
import com.kevin.prodapp.utils.SharePManager;
import com.kevin.prodapp.utils.SysApplication;
import com.kevin.prodapp.view.DoubleListFilterView;
import com.kevin.prodapp.view.ExpandMenuView;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class TestCaseListActivity extends Activity {

    /**
     * 列表原数据
     */
    private List<Item> allItems;
    /**
     * 可扩展的条件筛选菜单组合控件
     */
    private ExpandMenuView expandTabView;
    /**
     * 筛选条件视图集合
     */
    private ArrayList<View> mViewArray;
    private BaseAdapter adapter;
    /**
     * 城市筛选条件数据
     */
    private List<Province> allCitys;

    private List<TestCaseProvince> allCatagys;

    private List<TestCaseCatalog> allChildCatagys;
    /**
     * 等级筛选条件数据
     */
    private List<Grad> grads;

    /**
     * 筛选后的数据
     */
    private List<Item> items;
    private List<TestCaseItem> testCaseItems;

    private ArrayList<String> superItemDatas;

    //访问地址
    private String serverUrl;
    private String cookie;
    private String jksessionid;
    private String prjId;
    private String titleName;
    private ProgressDialog dialog;
    private SharePManager sPManger;
    // 筛选条件
    private String cityName = null;
    private String gradId = null;
    private int sort = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.test_case_list);
        //退出
        SysApplication.getInstance().addActivity(this);
        sPManger = new SharePManager(this, SharePManager.USER_FILE_NAME);//获取本地数据库信息

            Bundle bunde = this.getIntent().getExtras();
            prjId = new String(bunde.getString("project_id"));
            titleName = new String(bunde.getString("titleName"));
            FloatingActionButton button = findViewById(R.id.serch_testcase);
//        Button button = findViewById(R.id.serch_testcase);
            try {
                Properties props = new Properties();
                props.load(getApplicationContext().getAssets().open("config.properties"));
                serverUrl = props.getProperty("servers_url");;
                //第一次从配置文件中获取ip,修改过ip之后从缓存获取ip
                String sPMangerIP=sPManger.getString("servers_url");
                if(sPMangerIP!=null&&!"".equals(sPMangerIP)){
                    serverUrl=sPMangerIP;
                }
                SharePManager sharePManager = new SharePManager(TestCaseListActivity.this, SharePManager.USER_FILE_NAME);
                cookie = sharePManager.getString("cookie");
                jksessionid = sharePManager.getString("jksessionid");


            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent();
                    intent.putExtra("prjSid", prjId);
                    intent.putExtra("titleName", titleName);
                    intent.setClass(TestCaseListActivity.this, TestCaseSerchActivity.class);
                    startActivity(intent);
                }
            });



            initView();
            initData();}
            catch (Exception e) {
            e.toString();
        }

        }

        /**
         * 初始化数据
         */
        private void initData () {

            allItems = new ArrayList<Item>();
//        allItems.addAll(JSONObject.parseArray(JSONData, Item.class));
//        allItems.addAll()
            testCaseItems = new ArrayList<TestCaseItem>();
            items.addAll(allItems);
            adapter.notifyDataSetChanged();

            allCitys = new ArrayList<Province>();

            allCatagys = new ArrayList<TestCaseProvince>();
            //访问接口
            try {
                TextView title = findViewById(R.id.txt_title);
                title.setText(titleName);
                String projectId = prjId.substring(0, prjId.indexOf("|"));
                Map<String, String> map = new HashMap<>();
                map.put("cookie", cookie);
                map.put("jksessionid", jksessionid);
                org.json.JSONObject body = new org.json.JSONObject();
                body.put("value", projectId);

                String params1 = String.valueOf(body);
                final String path1 = serverUrl + "/setProjectSession";
                HttpsUrlConnection httpsUrlConnection = new HttpsUrlConnection();
                String result1 = httpsUrlConnection.post(path1, params1, map, TestCaseListActivity.this);

                final String path2 = serverUrl + "/getAppSystemId";
                String result2 = httpsUrlConnection.post(path2, "", map, TestCaseListActivity.this);
                org.json.JSONObject object2 = new org.json.JSONObject(result2);
                String data = object2.getString("data");
                JSONArray jsonArray = new JSONArray(data);
                //第一次获取一级目录id
                final String pathid = serverUrl + "/getTreeNodesId";
                String result4 = httpsUrlConnection.post(pathid, "", map, TestCaseListActivity.this);
                org.json.JSONObject obj=new org.json.JSONObject(result4);

                JSONArray jsonId =new org.json.JSONArray(obj.get("data").toString());
                for (int i = 0; i <jsonId.length() ; i++) {
                    jsonId.getJSONObject(i).getString("id");
                }
//                String nodeid=jsonId.get("data").getString("id");


                body = new org.json.JSONObject();
                body.put("value", jsonArray.getJSONObject(0).getString("id"));

                String params = String.valueOf(body);
                final String path3 = serverUrl + "/getTreeNodes";
                String result = httpsUrlConnection.post(path3, params, map, TestCaseListActivity.this);
                org.json.JSONObject object = new org.json.JSONObject(result4);
                String JSONTerm1 = object.getString("data");
                allCatagys.addAll(JSONObject.parseArray(JSONTerm1, TestCaseProvince.class));
                superItemDatas = new ArrayList<String>();

                //二级目录参数列表
                SparseArray<List<String>> children = new SparseArray<List<String>>();
                allChildCatagys = new ArrayList<>();




                // 提取并设置数据
                for (int i = 0; i < allCatagys.size(); i++) {
                    superItemDatas.add(allCatagys.get(i).getText());
                    String id = allCatagys.get(i).getId();
                    body = new org.json.JSONObject();
                    body.put("value", id);
                    String param = String.valueOf(body);
                    String path4 = serverUrl + "/getTreeNodes";
                    String result3 = httpsUrlConnection.post(path4, param, map, TestCaseListActivity.this);
                    org.json.JSONObject object1 = new org.json.JSONObject(result3);
//                JSONObject object1= FAST.parseObject(result3,null);
                    String jsonData = object1.getString("data");
                    allChildCatagys.addAll(JSONObject.parseArray(jsonData, TestCaseCatalog.class));
                    List<String> items = new ArrayList<>();
//                FAST.parseArray(jsonData,null);
                    JSONArray array = new JSONArray(jsonData);
//                List<Map<String, Object>> list= FAST.parseObjectListKeyMaps(jsonData);
                    if (array.length() > 0) {
                        for (int j = 0; j < array.length(); j++) {
                            org.json.JSONObject object3 = array.getJSONObject(j);
//                       if( list.get(j).get("icon").toString().contains("folder")){
//                           items.add(list.get(j).get("text").toString());
//                       }
                            if (object3.getString("icon").contains("folder")) {
                                items.add(object3.getString("text"));
                            }
                        }
                    }
                    children.put(i, items);
                }


                final DoubleListFilterView cityFilterView = new DoubleListFilterView(this, titleName, superItemDatas, children, 0, 0);
                cityFilterView.setOnSelectListener(new DoubleListFilterView.OnSelectListener() {

                    @Override
                    public void getValue(String showText, int superPosition, int position, boolean isFather) {
                        refreshScreen(cityFilterView, showText, superPosition, position, isFather);
                    }
                });

                //添加条件筛选控件到数据集合中
                mViewArray = new ArrayList<View>();
                mViewArray.add(cityFilterView);
//        mViewArray.add(gradFilterView);
//        mViewArray.add(sortFilterView);

                ArrayList<String> mTextArray = new ArrayList<String>();
                mTextArray.add("目录筛选");
//        mTextArray.add("等级筛选");
//        mTextArray.add("排序筛选");

                //给组合控件设置数据
                expandTabView.setValue(mTextArray, mViewArray);

                //处理组合控件按钮点击事件
                expandTabView.setOnButtonClickListener(new ExpandMenuView.OnButtonClickListener() {

                    @Override
                    public void onClick(int selectPosition, boolean isChecked) {
                        // TODO Auto-generated method stub
                    }
                });
            } catch (Exception e) {
                e.toString();
            }

        }

        private void initView () {


            expandTabView = (ExpandMenuView) findViewById(R.id.expandTabView);
            ListView listView = (ListView) findViewById(R.id.testcase_listview);
            items = new ArrayList<Item>();
            testCaseItems = new ArrayList<>();
            adapter = new MyAdapter(this);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @SuppressWarnings("unused")
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1,
                                        int position, long id) {
                    TestCaseItem caseItem = testCaseItems.get(position);
                    String caseCatlogid = caseItem.getId();
                    String caseids[] = caseCatlogid.split("\\|");
                    Intent intent = new Intent();
                    intent.setClass(TestCaseListActivity.this, TestCaseDetailActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("caseid", caseids[1]);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });

        }

        /**
         * 更新筛选条件
         *
         * @param view
         * @param showText
         * @param superPosition
         * @param pos           选中的位置
         */
        private void refreshScreen (View view, String showText,int superPosition, int pos,
        boolean isFather){
            expandTabView.closeView();
            int position = getPositon(view);
            String id = "";
            if (position >= 0)
                expandTabView.setTitle(showText, position);

            testCaseItems.clear();
            items.clear();

            if (isFather) {
                for (int i = 0; i < allCatagys.size(); i++) {
                    String text = allCatagys.get(i).getText();
                    if (showText.equals(text)) {
                        id = allCatagys.get(i).getId();
                    }
                }
            } else {
                for (int i = 0; i < allChildCatagys.size(); i++) {
                    String text = allChildCatagys.get(i).getText();
                    if (showText.equals(text)) {
                        id = allChildCatagys.get(i).getId();
                    }
                }
            }
            if (!id.equals("")) {
                try {
                    SharePManager sharePManager = new SharePManager(TestCaseListActivity.this, SharePManager.USER_FILE_NAME);
                    String cookie = sharePManager.getString("cookie");
                    String jksessionid = sharePManager.getString("jksessionid");
                    Map<String, String> map = new HashMap<>();
                    map.put("cookie", cookie);
                    map.put("jksessionid", jksessionid);
                    org.json.JSONObject body = new org.json.JSONObject();
                    body.put("value", id);

                    String params = String.valueOf(body);
                    final String path = serverUrl + "/getTreeNodes";
                    HttpsUrlConnection httpsUrlConnection = new HttpsUrlConnection();
                    String result = httpsUrlConnection.post(path, params, map, TestCaseListActivity.this);
                    org.json.JSONObject object = new org.json.JSONObject(result);
                    testCaseItems = JSONObject.parseArray(object.getString("data"), TestCaseItem.class);

                } catch (Exception e) {
                    e.toString();
                }

            }

            adapter.notifyDataSetChanged();
        }

        /**
         * 获取当前点击的位置
         *
         * @param tView
         * @return
         */
        private int getPositon (View tView){
            for (int i = 0; i < mViewArray.size(); i++) {
                if (mViewArray.get(i) == tView)
                    return i;
            }

            return -1;
        }


        class MyAdapter extends BaseAdapter {
            private LayoutInflater inflater;

            public MyAdapter(Context context) {
                inflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            }

            @Override
            public int getCount() {
                return testCaseItems.size();
            }

            @Override
            public Object getItem(int position) {
                return testCaseItems.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                Typeface typeface = Typeface.createFromAsset(getAssets(), "fontawesome-webfont.ttf");
                convertView = inflater.inflate(R.layout.test_case_list_child, null, false);
                TestCaseItem testCaseItem = testCaseItems.get(position);
                MyAdapter.ViewHold viewHold = new MyAdapter.ViewHold();
                viewHold.tv3 = convertView.findViewById(R.id.caseflag_id);
                viewHold.tv3.setText(R.string.fa_list_ol);
                viewHold.tv3.setTypeface(typeface);

                viewHold.tv1 = (TextView) convertView.findViewById(R.id.testcase_list_child_tv1);
                viewHold.tv2 = (TextView) convertView.findViewById(R.id.testcase_list_child_tv2);
                viewHold.tv1.setText(testCaseItem.getText());
                viewHold.tv2.setText(testCaseItem.getId());
                viewHold.tv2.setVisibility(View.GONE);
                return convertView;
            }

            class ViewHold {
                private TextView tv3;
                private TextView tv1;
                private TextView tv2;
            }
        }
    }

