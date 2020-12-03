package com.kevin.prodapp.ui.list;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.kevin.prodapp.R;
import com.kevin.prodapp.ui.main.MainFragment;
import com.kevin.prodapp.utils.HttpsUrlConnection;
import com.kevin.prodapp.utils.SharePManager;
import com.kevin.prodapp.utils.SysApplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class SecondActivity extends Activity {
    private List<HashMap<String, Object>> list;
    private ListView listview;
    private ProgressDialog dialog;
    private Spinner spinner;
    private List<HashMap<String,Object>> data_list;
    private ArrayAdapter<String> arr_adapter;
    private String serverUrl ;
    private SharePManager sPManger;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        //退出
        SysApplication.getInstance().addActivity(this);
        spinner = (Spinner) findViewById(R.id.spinner);
        data_list = new ArrayList<>();
        sPManger = new SharePManager(this, SharePManager.USER_FILE_NAME);//获取本地数据库信息
        //1、添加数据源，就是下拉菜单选项
        try {
            Properties props = new Properties();
            props.load(SecondActivity.this.getAssets().open("config.properties"));
            serverUrl = props.getProperty("servers_url");;
            //第一次从配置文件中获取ip,修改过ip之后从缓存获取ip
            String sPMangerIP=sPManger.getString("servers_url");
            if(sPMangerIP!=null&&!"".equals(sPMangerIP)){
                serverUrl=sPMangerIP;
            }
            getdata();

            List<String> list1=new ArrayList<>();

            final JSONArray arr=new JSONArray(data_list);
            for (int i = 0; i <data_list.size() ; i++) {
                try {
                    list1.add(arr.getJSONObject(i).get("title").toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            Intent intent = getIntent();
            String title1 = intent.getStringExtra("titleName");
            //第一次选项目时保证下拉列表第一个的值相等
            String temp="";
            for (int i = 0; i <list1.size() ; i++) {
                while (list1.get(i).equals(title1)){
                    //将值等于title的元素和第一个元素替换
                        temp=list1.get(0);
                        list1.set(0,list1.get(i));
                        list1.set(i,temp);
                        break;
                }
            }

            //2、未下来列表定义一个数组适配器
            arr_adapter=new ArrayAdapter(this,android.R.layout.simple_spinner_dropdown_item,list1);
            //3、为适配器设置下拉菜单的样式
            arr_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            //4、将适配器配置到下拉列表上
            spinner.setAdapter(arr_adapter);
            //5、给下拉菜单设置监听事件
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    //dialog=ProgressDialog.show(SecondActivity.this, "", "正在加载...");
                    String text = spinner.getItemAtPosition(position).toString();
                    //将项目名加入缓存
                    MainFragment.sharePManager.putString("sysName",text);

                    Toast.makeText(SecondActivity.this, text, Toast.LENGTH_SHORT).show();


                    String path = serverUrl + "/getTestProjectTreeView";
                    SharePManager sharePManager = new SharePManager(SecondActivity.this, SharePManager.USER_FILE_NAME);
                    String cookie = sharePManager.getString("cookie");
                    String jksessionid = sharePManager.getString("jksessionid");

                    Map<String, String> map = new HashMap<>();
                    map.put("cookie", cookie);
                    map.put("jksessionid", jksessionid);
                    HttpsUrlConnection httpsUrlConnection = new HttpsUrlConnection();
                    String result = httpsUrlConnection.post(path, "", map, SecondActivity.this);
                    String  items="";
                   try {

                       JSONArray jsonArray = new JSONArray(new JSONObject(result).get("data").toString());
                       for (int i = 0; i <jsonArray.length() ; i++) {
                           System.out.println(jsonArray.getJSONObject(i).get("text").toString());
                          if(jsonArray.getJSONObject(i).get("text").toString().equals(text)){
                               items = jsonArray.getJSONObject(i).get("items").toString();
                              break;
                          };
                       }
                       JSONArray itemArray=new JSONArray(items);
                       listview = (ListView) findViewById(R.id.listview1);
                       TextView title = findViewById(R.id.txt_title);
                       String titleName=text;
                       title.setText(titleName);
                        setData(itemArray);


                       final MyAdapter adapter = new MyAdapter(SecondActivity.this);
                       listview.setAdapter(adapter);

                       listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                           @SuppressWarnings("unused")
                           @Override
                           public void onItemClick(AdapterView<?> arg0, View arg1,
                                                   int position, long id) {
                            //正在加载
                               dialog=ProgressDialog.show(SecondActivity.this, "", "正在加载...");
                               new Thread() {
                                   public void run() {
                                       try{
                                           sleep(2000);
                                       } catch (Exception e) {
                                           Log.e("tag", e.getMessage());
                                       }
                                       dialog.dismiss();
                                   }
                               }.start();

                               String ids = list.get(position).get("id").toString();
                               String titleName = list.get(position).get("name").toString();
                               Intent intentBef = getIntent();
                               String titbun = intentBef.getStringExtra("titbun");
                               String appsysId = intentBef.getStringExtra("id");
                               Intent intent = new Intent();
                               Bundle bundle = new Bundle();
                               bundle.putString("project_id", ids);
                               bundle.putString("titleName", titleName);
                               bundle.putString("appsysId", appsysId);
                               intent.putExtras(bundle);
                               if (titbun.equals("测试缺陷")) {
                                   intent.setClass(SecondActivity.this, DefectAddActivity.class);
                                   intent.setClass(SecondActivity.this, MoreInfoActivity.class);

                               }else if(titbun.equals("测试案例")){
                                   intent.setClass(SecondActivity.this, TestCaseListActivity.class);
                               }
                               startActivity(intent);
                           }

                       });

                   }catch (Exception e){
                       e.toString();
                   }
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }

            } );

           // dialog.dismiss();

            listview = (ListView) findViewById(R.id.listview1);
            TextView title = findViewById(R.id.txt_title);
            list = new ArrayList<HashMap<String, Object>>();
             intent = getIntent();
            String items = intent.getStringExtra("PROJECT_LIST");
            String titleName = intent.getStringExtra("titleName");
            title.setText(titleName);
            try {
                JSONArray array = new JSONArray(items);
                setData(array);
            } catch (Exception e) {
                e.toString();
            }

            final MyAdapter adapter = new MyAdapter(this);
            listview.setAdapter(adapter);
//        dialog.dismiss();
            listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @SuppressWarnings("unused")
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1,
                                        int position, long id) {

                    String ids = list.get(position).get("id").toString();
                    String titleName = list.get(position).get("name").toString();
                    Intent intentBef = getIntent();
                    String titbun = intentBef.getStringExtra("titbun");
                    String appsysId = intentBef.getStringExtra("id");
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putString("project_id", ids);
                    bundle.putString("titleName", titleName);
                    bundle.putString("appsysId", appsysId);
                    intent.putExtras(bundle);
                    if (titbun.equals("测试缺陷")) {
                        intent.setClass(SecondActivity.this, MoreInfoActivity.class);
                    }else if(titbun.equals("测试案例")){
                        intent.setClass(SecondActivity.this, TestCaseListActivity.class);
                    }
                    startActivity(intent);
                }
            });
        }catch (Exception e){
            e.toString();
        }

    }

    private void getdata() {
        data_list.clear();

        JSONArray jsonArray = new JSONArray(MainFragment.projectData);
        for (int i = 0; i <MainFragment.projectData.size() ; i++) {
            JSONObject object= null;
            try {
                object = jsonArray.getJSONObject(i);
                HashMap<String,Object> map=new HashMap<>();
                map.put("id",object.getString("id"));
                map.put("title",object.getString("title"));
                data_list.add(map);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        MainFragment.projectData.clear();

    }
    private void setData(JSONArray array) {
        HashMap<String, Object> hashMap;
        //列表选择时清空之前项目内容
        list.clear();
        try {
            for (int i = 0; i < array.length(); i++) {
                hashMap = new HashMap<String, Object>();
                JSONObject object = array.getJSONObject(i);
                hashMap.put("id",object.getString("id"));
                hashMap.put("name",object.getString("text"));
                list.add(hashMap);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    class MyAdapter extends BaseAdapter {
        private LayoutInflater inflater;

        public MyAdapter(Context context) {
            inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
            Typeface typeface = Typeface.createFromAsset(getAssets(),"fontawesome-webfont.ttf");
            convertView = inflater.inflate(R.layout.list_child, null, false);
            HashMap<String, Object> hashMap = new HashMap<String, Object>();
            hashMap = list.get(position);
            ViewHold viewHold = new ViewHold();
            viewHold.tv1 = (TextView) convertView
                    .findViewById(R.id.list_child_tv1);
            viewHold.tv2 = (TextView) convertView
                    .findViewById(R.id.list_child_tv2);
            viewHold.tv3 = convertView.findViewById(R.id.enter_id);
            viewHold.tv3.setText(R.string.fa_gears);
            viewHold.tv3.setTypeface(typeface);
//            viewHold.tv1.setText("����: jack" + hashMap.get("name").toString());
            viewHold.tv1.setText(hashMap.get("name").toString());
            viewHold.tv2.setText(hashMap.get("id").toString());
            viewHold.tv2.setVisibility(View.GONE);
            return convertView;
        }

        class ViewHold {
            private TextView tv1;
            private TextView tv2;
            private TextView tv3;
        }
    }
    private void buildDialog() {
        dialog = new ProgressDialog(SecondActivity.this);
        dialog.setTitle("加载中...");
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    }



}