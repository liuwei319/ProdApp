package com.kevin.prodapp.ui.list;

import android.content.Context;
import android.os.Bundle;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.kevin.prodapp.R;
import com.kevin.prodapp.entity.TestCase;
import com.kevin.prodapp.utils.HttpsUrlConnection;
import com.kevin.prodapp.utils.SharePManager;
import com.kevin.prodapp.utils.SysApplication;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;

public class TestCaseDetailActivity extends AppCompatActivity {

    private String caseId;
    private String serverUrl;
    private String cookie;
    private String jksessionid;
    private TestCase testCase;
    private JSONObject jsonData;
    private List<Map> caselist;
    private ListView listView;
    private String titleName;
    private SharePManager sPManger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_case_detail);
        sPManger = new SharePManager(this, SharePManager.USER_FILE_NAME);//获取本地数据库信息

        //退出
        SysApplication.getInstance().addActivity(this);
        Bundle bundle = this.getIntent().getExtras();
        caseId = bundle.getString("caseid");
        listView = findViewById(R.id.testcase_detail_listview);
        caselist = new ArrayList<>();
        try {
            Properties props = new Properties();
            props.load(getApplicationContext().getAssets().open("config.properties"));
            serverUrl = props.getProperty("servers_url");
            //第一次从配置文件中获取ip,修改过ip之后从缓存获取ip
            String sPMangerIP=sPManger.getString("servers_url");
            if(sPMangerIP!=null&&!"".equals(sPMangerIP)){
                serverUrl=sPMangerIP;
            }
            SharePManager sharePManager = new SharePManager(TestCaseDetailActivity.this, SharePManager.USER_FILE_NAME);
            cookie = sharePManager.getString("cookie");
            jksessionid = sharePManager.getString("jksessionid");

            Map<String, String> map = new HashMap<>();
            map.put("cookie", cookie);
            map.put("jksessionid", jksessionid);
            org.json.JSONObject body = new org.json.JSONObject();
            body.put("value", caseId);
            String path = serverUrl + "/getTestCaseDetail";
            HttpsUrlConnection httpsUrlConnection = new HttpsUrlConnection();
            String params = String.valueOf(body);

            String result = httpsUrlConnection.post(path, params, map, TestCaseDetailActivity.this);
            org.json.JSONObject object = new org.json.JSONObject(result);
            String data = object.getString("data");
            jsonData = new JSONObject(data);
            titleName = jsonData.getString("tcSno");
            TextView title = findViewById(R.id.txt_title);
            title.setText(titleName);
            ininData(jsonData);


            //得到案例步骤
            path = serverUrl + "/getTestCaseStep";
            String resuletStep = httpsUrlConnection.post(path, params, map, TestCaseDetailActivity.this);
            org.json.JSONObject objectStep = new org.json.JSONObject(resuletStep);
            String stepData = objectStep.getString("data");
            Map<String, String> stepTitleMap = new HashMap<>();
            stepTitleMap.put("orderNo", "序号");
            stepTitleMap.put("summary", "概述");
            stepTitleMap.put("datareq", "数据要求");
            stepTitleMap.put("ExpectedResult", "期望结果");
            stepTitleMap.put("OperationName", "操作与参数");
            caselist.add(stepTitleMap);

            JSONArray steparray = new JSONArray(stepData);
            JSONArray jsonArray = new JSONArray(stepData);
            for (int i = 0; i < jsonArray.length(); i++) {
                Map<String, String> stepMap = new HashMap<>();
                String orderNo = jsonArray.getJSONObject(i).get("tcsSort").toString();
                String summary = jsonArray.getJSONObject(i).get("tcsSummary").toString();
                String OperationName = jsonArray.getJSONObject(i).get("tcsOperationName").toString();
                String tcsExpectation = jsonArray.getJSONObject(i).get("tcsExpectation").toString();
                String tcsDataReq = jsonArray.getJSONObject(i).get("tcsDataReq").toString();
//                steparray = new JSONArray(jsonArray.getJSONObject(i).get("stepData").toString());
//                String datareq = "";
//                String ExpectedResult = "";
//                for (int j = 0; j < steparray.length(); j++) {
//                    //数据要求
//                    if (steparray.getJSONObject(j).get("tcdPname").equals("Datareq")) {
//                        datareq = steparray.getJSONObject(j).get("tcdValue").toString();
//                        stepMap.put("datareq", datareq);
//                    }
//                    //期望结果
//                    if (steparray.getJSONObject(j).get("tcdPname").equals("ExpectedResult")) {
//                        ExpectedResult = steparray.getJSONObject(j).get("tcdValue").toString();
//                        stepMap.put("ExpectedResult", ExpectedResult);
//                    }
//                }
                stepMap.put("orderNo", orderNo);
                stepMap.put("datareq", tcsDataReq);
                stepMap.put("summary", summary);
                stepMap.put("ExpectedResult", tcsExpectation);
                stepMap.put("OperationName", OperationName);
                caselist.add(stepMap);
            }


            final MyAdapter1 adapter = new MyAdapter1(TestCaseDetailActivity.this);
            listView.setAdapter(adapter);
        } catch (Exception e) {
            e.toString();
        }
    }


    class MyAdapter1 extends BaseAdapter {
        private LayoutInflater inflater;

        public MyAdapter1(Context context) {
            inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return caselist.size();
        }

        @Override
        public Object getItem(int position) {
            return caselist.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            Map<String, String> data = caselist.get(position);
            if (data.size() < 5) {
                convertView = inflater.inflate(R.layout.test_case_detail_child, null, false);

//            String key = list.get(position);
                MyAdapter1.ViewHold viewHold = new MyAdapter1.ViewHold();
                viewHold.tv1 = (TextView) convertView.findViewById(R.id.detail_child_tv1);
                viewHold.tv2 = (TextView) convertView.findViewById(R.id.detail_child_tv2);

                viewHold.tv1.setText(data.get("title"));
                viewHold.tv2.setText(data.get("value"));


            } else {

                convertView = inflater.inflate(R.layout.test_case_step_child, null, false);
//            String key = list.get(position);
                MyAdapter1.ViewHold viewHold = new MyAdapter1.ViewHold();
                viewHold.steptv1 = (TextView) convertView.findViewById(R.id.text1);
                viewHold.steptv2 = (TextView) convertView.findViewById(R.id.text2);
                viewHold.steptv3 = (TextView) convertView.findViewById(R.id.text3);
                viewHold.steptv4 = (TextView) convertView.findViewById(R.id.text4);
                viewHold.steptv5 = (TextView) convertView.findViewById(R.id.text5);
                viewHold.steptv1.setText(data.get("orderNo"));
                viewHold.steptv2.setText(data.get("summary"));
                viewHold.steptv3.setText(data.get("datareq"));
                viewHold.steptv4.setText(data.get("ExpectedResult"));
                viewHold.steptv5.setText(data.get("OperationName"));
                if (position == 28) {
                    //中文加粗

                    viewHold.steptv1.setTextSize((float) 12.5);
                    viewHold.steptv2.setTextSize((float) 12.5);
                    viewHold.steptv3.setTextSize((float) 12.5);
                    viewHold.steptv4.setTextSize((float) 12.5);
                    viewHold.steptv5.setTextSize((float) 12.5);
                    viewHold.steptv1.setSingleLine(true);
                    viewHold.steptv2.setSingleLine(true);
                    viewHold.steptv3.setSingleLine(true);
                    viewHold.steptv4.setSingleLine(true);
                    viewHold.steptv5.setSingleLine(true);
                    TextPaint paint1 = viewHold.steptv1.getPaint();
                    TextPaint paint2 = viewHold.steptv2.getPaint();
                    TextPaint paint3 = viewHold.steptv3.getPaint();
                    TextPaint paint4 = viewHold.steptv4.getPaint();
                    TextPaint paint5 = viewHold.steptv5.getPaint();
                    paint1.setFakeBoldText(true);
                    paint2.setFakeBoldText(true);
                    paint3.setFakeBoldText(true);
                    paint4.setFakeBoldText(true);
                    paint5.setFakeBoldText(true);
                }
            }

//            TestCase testCase = testCases.get(position);


//            viewHold.tv2.setVisibility(View.GONE);
            return convertView;
        }

        class ViewHold {
            private TextView tv1;
            private TextView tv2;
            private TextView steptv1;
            private TextView steptv2;
            private TextView steptv3;
            private TextView steptv4;
            private TextView steptv5;
            private TextView title1;
        }
    }


    private void ininData(JSONObject testCase) {
        Map<Integer, Map<String, String>> sortMap = new HashMap<>();
        Iterator<String> keys = testCase.keys();
        try {
            while (keys.hasNext()) {
                String keyName = keys.next();
                Map<String, String> temp = new HashMap<>();
                String value = "";
                switch (keyName) {
                    case "tcSno":
                        temp.put("title", "编号:");
                        temp.put("value", testCase.getString(keyName));
                        sortMap.put(0, temp);
                        break;
                    case "tcName":
                        value = testCase.getString(keyName);
                        value = value.replaceAll("\\[", "&#091;");
                        value = value.replaceAll("\\]", "&#093;");
                        temp.put("title", "名称:");
                        temp.put("value", value);
                        sortMap.put(1, temp);
                        break;
                    case "tcStructType":
                        value = testCase.getString(keyName);
                        if (value.equals("2501")) {
                            value = "单一业务案例";
                        } else if (value.equals("2502")) {
                            value = "复合业务案例";
                        }
                        temp.put("title", "结构类型:");
                        temp.put("value", value);
                        sortMap.put(2, temp);
                        break;
                    case "tcType":
                        value = testCase.getString(keyName);
                        if (value.equals("2601")) {
                            value = "手工测试案例";
                        } else if (value.equals("2606")) {
                            value = "自动化案例(内置)";
                        } else if (value.equals("2607")) {
                            value = "自动化案例(接口)";
                        } else if (value.equals("2699")) {
                            value = "自动生成案例";
                        }
                        temp.put("title", "类型:");
                        temp.put("value", value);
                        sortMap.put(3, temp);
                        break;
                    case "tcPnType":
                        value = testCase.getString(keyName);
                        if (value.equals("2301")) {
                            value = "正案例";
                        } else if (value.equals("2302")) {
                            value = "反案例";
                        } else if (value.equals("2303")) {
                            value = "界面检查案例";
                        }
                        temp.put("title", "正反案例类型:");
                        temp.put("value", value);
                        sortMap.put(4, temp);
                        break;
                    case "tcStatus":
                        value = getStatus(testCase.getString(keyName));
                        temp.put("title", "类型:");
                        temp.put("value", value);
                        sortMap.put(5, temp);
                        break;
                    case "tcProduct":
                        if (testCase.getString(keyName).equals("null") || "".equals(testCase.getString(keyName))) {
                            value = "";
                        } else {
                            value = testCase.getString(keyName);
                        }
                        temp.put("title", "产品:");
                        temp.put("value", value);
                        sortMap.put(6, temp);
                        break;
                    case "tcFeature":
                        if (testCase.getString(keyName).equals("null") || "".equals(testCase.getString(keyName))) {
                            value = "";
                        } else {
                            value = testCase.getString(keyName);
                        }
                        temp.put("title", "功能点:");
                        temp.put("value", value);
                        sortMap.put(7, temp);
                        break;
                    case "tcBusiness":
                        if (testCase.getString(keyName).equals("null") || "".equals(testCase.getString(keyName))) {
                            value = "";
                        } else {
                            value = testCase.getString(keyName);
                        }
                        temp.put("title", "业务:");
                        temp.put("value", value);
                        sortMap.put(8, temp);
                        break;
                    case "tcTradeCode":
                        if (testCase.getString(keyName).equals("null") || "".equals(testCase.getString(keyName))) {
                            value = "";
                        } else {
                            value = testCase.getString(keyName);
                        }
                        temp.put("title", "交易码:");
                        temp.put("value", value);
                        sortMap.put(9, temp);
                        break;
                    case "tcTradeName":
                        if (testCase.getString(keyName).equals("null") || "".equals(testCase.getString(keyName))) {
                            value = "";
                        } else {
                            value = testCase.getString(keyName);
                        }
                        temp.put("title", "交易名:");
                        temp.put("value", value);
                        sortMap.put(10, temp);
                        break;
                    case "tcImportance":
                        value = testCase.getString(keyName);
                        if (value.equals("4901")) {
                            value = "重要";
                        } else if (value.equals("4902")) {
                            value = "一般";
                        } else if (value.equals("4903")) {
                            value = "不重要";
                        }
                        temp.put("title", "重要性:");
                        temp.put("value", value);
                        sortMap.put(11, temp);
                        break;
                    case "tcStability":
                        value = testCase.getString(keyName);
                        if (value.equals("5001")) {
                            value = "稳定";
                        } else if (value.equals("5002")) {
                            value = "一般";
                        } else if (value.equals("5003")) {
                            value = "不稳定";
                        }
                        temp.put("title", "稳定性:");
                        temp.put("value", value);
                        sortMap.put(12, temp);
                        break;
                    case "tcPriority":
                        value = testCase.getString(keyName);
                        if (value.equals("3401")) {
                            value = "高";
                        } else if (value.equals("3402")) {
                            value = "中";
                        } else if (value.equals("3403")) {
                            value = "低";
                        } else {
                            value = "";
                        }
                        temp.put("title", "优先级:");
                        temp.put("value", value);
                        sortMap.put(13, temp);
                        break;
                    case "tcMsgMode":
                        value = testCase.getString(keyName);
                        if (value.equals("9801")) {
                            value = "树节点方式";
                        } else if (value.equals("9802")) {
                            value = "原报文方式";
                        } else {
                            value = "";
                        }
                        temp.put("title", "接口案例编辑方式:");
                        temp.put("value", value);
                        sortMap.put(14, temp);
                        break;
                    case "tcReqAgree":
                        value = testCase.getString(keyName);
                        if (value.equals("9701")) {
                            value = "HTTP";
                        } else if (value.equals("9702")) {
                            value = "TCP";
                        } else {
                            value = "";
                        }
                        temp.put("title", "接口协议:");
                        temp.put("value", value);
                        sortMap.put(15, temp);
                        break;
                    case "tcReqType":
                        value = testCase.getString(keyName);
                        if (value.equals("9601")) {
                            value = "XML";
                        } else if (value.equals("9602")) {
                            value = "JSON";
                        } else if (value.equals("9603")) {
                            value = "STR";
                        } else {
                            value = "";
                        }
                        temp.put("title", "接口数据类型:");
                        temp.put("value", value);
                        sortMap.put(16, temp);
                        break;
                    case "tcReqUrl":
                        if (testCase.getString(keyName).equals("null") || "".equals(testCase.getString(keyName))) {
                            value = "";
                        } else {
                            value = testCase.getString(keyName);
                        }
                        temp.put("title", "请求地址:");
                        temp.put("value", value);
                        sortMap.put(17, temp);
                        break;
                    case "tcCreator":
                        if (testCase.getString(keyName).equals("null") || "".equals(testCase.getString(keyName))) {
                            value = "";
                        } else {
                            value = testCase.getString(keyName);
                        }
                        temp.put("title", "创建人:");
                        temp.put("value", value);
                        sortMap.put(18, temp);
                        break;
                    case "tcCreateTime":
                        if (testCase.getString(keyName).equals("null") || "".equals(testCase.getString(keyName))) {
                            value = "";
                        } else {
                            value = testCase.getString(keyName);
                            TimeZone.setDefault(TimeZone.getTimeZone("GMT+8"));
                            Date date = new Date();
                            date.setTime(Long.parseLong(value));
                            value = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
                        }

                        temp.put("title", "创建时间:");
                        temp.put("value", value);
                        sortMap.put(19, temp);
                        break;
                    case "tcPathTxt":
                        if (testCase.getString(keyName).equals("null") || "".equals(testCase.getString(keyName))) {
                            value = "";
                        } else {
                            value = testCase.getString(keyName);
                        }
                        temp.put("title", "模块路径:");
                        temp.put("value", value);
                        sortMap.put(20, temp);
                        break;
                    case "tcInput":
                        if (testCase.getString(keyName).equals("null") || "".equals(testCase.getString(keyName))) {
                            value = "";
                        } else {
                            value = testCase.getString(keyName);
                        }
                        temp.put("title", "输入:");
                        temp.put("value", value);
                        sortMap.put(21, temp);
                        break;
                    case "tcOutput":
                        if (testCase.getString(keyName).equals("null") || "".equals(testCase.getString(keyName))) {
                            value = "";
                        } else {
                            value = testCase.getString(keyName);
                        }
                        temp.put("title", "输出:");
                        temp.put("value", value);
                        sortMap.put(22, temp);
                        break;
                    case "tcDesc":
                        if (testCase.getString(keyName).equals("null") || "".equals(testCase.getString(keyName))) {
                            value = "";
                        } else {
                            value = testCase.getString(keyName);
                        }
                        temp.put("title", "描述:");
                        temp.put("value", value);
                        sortMap.put(23, temp);
                        break;
                    case "tcSummary":
                        if (testCase.getString(keyName).equals("null") || "".equals(testCase.getString(keyName))) {
                            value = "";
                        } else {
                            value = testCase.getString(keyName);
                        }
                        temp.put("title", "概述:");
                        temp.put("value", value);
                        sortMap.put(24, temp);
                        break;
                    case "tcTestPurpose":
                        if (testCase.getString(keyName).equals("null") || "".equals(testCase.getString(keyName))) {
                            value = "";
                        } else {
                            value = testCase.getString(keyName);
                        }
                        temp.put("title", "测试目的:");
                        temp.put("value", value);
                        sortMap.put(25, temp);
                        break;
                    case "tcExpectedResult":
                        if (testCase.getString(keyName).equals("null") || "".equals(testCase.getString(keyName))) {
                            value = "";
                        } else {
                            value = testCase.getString(keyName);
                        }
                        temp.put("title", "预期结果:");
                        temp.put("value", value);
                        sortMap.put(26, temp);
                        break;
                    case "tcDataDemand":
                        if (testCase.getString(keyName).equals("null") || "".equals(testCase.getString(keyName))) {
                            value = "";
                        } else {
                            value = testCase.getString(keyName);
                        }
                        temp.put("title", "数据要求:");
                        temp.put("value", value);
                        sortMap.put(27, temp);
                        break;
                    default:
                        break;
                }
            }

            for (int i = 0; i < 28; i++) {
                caselist.add(sortMap.get(i));
            }

        } catch (Exception e) {
            e.toString();
        }


    }

    private String getStatus(String status) {
        String value = "";
        switch (status) {
            case "2401":
                value = "编辑";
                break;
            case "2402":
                value = "评审中";
                break;
            case "2403":
                value = "已评审";
                break;
            case "2404":
                value = "已执行";
                break;
            case "2405":
                value = "已签入";
                break;
            case "2406":
                value = "已签出";
                break;
            case "2407":
                value = "已修改";
                break;
            case "2408":
                value = "已废弃";
                break;
            default:
                break;
        }
        return value;
    }

}
