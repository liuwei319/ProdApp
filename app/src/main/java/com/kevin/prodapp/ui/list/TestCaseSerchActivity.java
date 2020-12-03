package com.kevin.prodapp.ui.list;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.kevin.prodapp.R;
import com.kevin.prodapp.utils.HttpsUrlConnection;
import com.kevin.prodapp.utils.SharePManager;
import com.kevin.prodapp.utils.SysApplication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class TestCaseSerchActivity extends AppCompatActivity {

    private String prjSid;
    private String serverUrl;
    private String cookie;
    private String jksessionid;
    private List<String> testCases=new ArrayList<>();
    private ListView listview;
    private String text;
    private String titleName;
    private List<String> listId=new ArrayList<>();
    private SharePManager sPManger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_case_serch);
        //退出
        SysApplication.getInstance().addActivity(this);
        sPManger = new SharePManager(this, SharePManager.USER_FILE_NAME);//获取本地数据库信息

        Intent intentData = getIntent();
        String dataStringExtra = intentData.getStringExtra("prjSid");
        titleName = intentData.getStringExtra("titleName");
        TextView title = findViewById(R.id.txt_title);
        title.setText(titleName);
        prjSid = dataStringExtra.substring(0, dataStringExtra.indexOf("|"));
        listview = (ListView) findViewById(R.id.case_serch_list);
        Button button = findViewById(R.id.serch_testcase);
        final EditText serchEditText = findViewById(R.id.casenameornum);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text = serchEditText.getText().toString();
                try {
                    Properties props = new Properties();
                    props.load(getApplicationContext().getAssets().open("config.properties"));
                    serverUrl = props.getProperty("servers_url");;
                    //第一次从配置文件中获取ip,修改过ip之后从缓存获取ip
                    String sPMangerIP=sPManger.getString("servers_url");
                    if(sPMangerIP!=null&&!"".equals(sPMangerIP)){
                        serverUrl=sPMangerIP;
                    }
                    SharePManager sharePManager = new SharePManager(TestCaseSerchActivity.this, SharePManager.USER_FILE_NAME);
                    cookie = sharePManager.getString("cookie");
                    jksessionid = sharePManager.getString("jksessionid");

                    Map<String, String> map = new HashMap<>();
                    map.put("cookie", cookie);
                    map.put("jksessionid", jksessionid);
                    org.json.JSONObject body = new org.json.JSONObject();
                   // body.put("value", prjSid);
                    body.put("text", text);
                    String path = serverUrl + "/searchNodes";
                    HttpsUrlConnection httpsUrlConnection = new HttpsUrlConnection();
                    String params = String.valueOf(body);
                    String result = httpsUrlConnection.post(path, params, map, TestCaseSerchActivity.this);
                    org.json.JSONObject object = new org.json.JSONObject(result);
                    String data = object.getString("data");
                    org.json.JSONArray jsonArray=new  org.json.JSONArray(data);

                    for (int i = 0; i < jsonArray.length(); i++) {
                       String id= jsonArray.getJSONObject(i).getString("id");
                       id=id.replace("confId|","");
                       id=id.replace("|parentId|1","");
                        listId.add(i,id);

                        String text=jsonArray.getJSONObject(i).getString("text");
                        testCases.add(i,text);
                    }


                   // testCases = JSONObject.parseArray(data, TestCase.class);
                    final MyAdapter adapter = new MyAdapter(TestCaseSerchActivity.this);
                    listview.setAdapter(adapter);
                    listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @SuppressWarnings("unused")
                        @Override
                        public void onItemClick(AdapterView<?> arg0, View arg1,
                                                int position, long id) {
                          //  TestCase tcase = testCases.get(position);
                            Intent intent = new Intent();
                            intent.setClass(TestCaseSerchActivity.this, TestCaseDetailActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("caseid",listId.get(position));
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }
                    });
                } catch (Exception e) {
                    e.toString();
                }
            }
        });




    }

    class MyAdapter extends BaseAdapter {
        private LayoutInflater inflater;

        public MyAdapter(Context context) {
            inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return testCases.size();
        }

        @Override
        public Object getItem(int position) {
            return testCases.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Typeface typeface = Typeface.createFromAsset(getAssets(), "fontawesome-webfont.ttf");
            convertView = inflater.inflate(R.layout.list_child, null, false);
           // TestCase testCase = testCases.get(position);
            MyAdapter.ViewHold viewHold = new MyAdapter.ViewHold();
            viewHold.tv3 = convertView.findViewById(R.id.enter_id);
            viewHold.tv3.setText(R.string.fa_list_ol);
            viewHold.tv3.setTypeface(typeface);
            viewHold.tv1 = (TextView) convertView.findViewById(R.id.list_child_tv1);
            viewHold.tv2 = (TextView) convertView.findViewById(R.id.list_child_tv2);
//            viewHold.tv1.setText(testCase.getTcName() + "[" + testCase.getTcSno() + "]");
//            viewHold.tv2.setText(testCase.getTcSid());
            viewHold.tv1.setText(testCases.get(position));
            viewHold.tv2.setText("");
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
