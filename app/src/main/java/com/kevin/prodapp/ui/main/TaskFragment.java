package com.kevin.prodapp.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.kevin.prodapp.R;


public class TaskFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {


    private static final int SENSOR_SHAKE=10;
//    private SharePManager sPManger;
    private TextView tv_simple;
    private SwipeRefreshLayout srl_simple;


    private int[] imageRes={
//            R.drawable.lichang,
//            R.drawable.entrance,
//            R.drawable.internalappchange,
//            R.drawable.qingjia,
//            R.drawable.resourcestatistics,
//            R.drawable.toupiaoshenpi,
//            R.drawable.crowdview,
//            R.drawable.banbenshenhe,
//            R.drawable.entranceapproval
    };
    public TaskFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle bundle = getArguments();
//        String username = (String) bundle.get("username");
//        String userid=(String) bundle.get("userid");
//        sPManger = new SharePManager(this.getContext(),SharePManager.USER_FILE_NAME);//获取本地数据库信息
//        String usersp = sPManger.getString("usersp");
//        Map<String, String> header=new HashMap<String, String>();
//        header.put("cookie",sPManger.getString("cookie"));
//        header.put("jksessionid",sPManger.getString("jksessionid"));
//        ResultData taskNums=new UserService().getTaskNums(userid,username,usersp,header,getActivity());
//        HashMap<String, List> taskList = new HashMap<>();
//        if(!taskNums.getRet_data().equals(""))
//        taskList = (HashMap<String, List>) taskNums.getRet_data();
        View view = inflater.inflate(R.layout.fragment_task, container, false);
        //加载九宫格
//        ListView listView=(ListView)view.findViewById(R.id.taskitem);
//        view.setFocusable(false);
//        tv_simple = (TextView) view.findViewById(R.id.tv_simple);
//        srl_simple = (SwipeRefreshLayout) view.findViewById(R.id.srl_simple);
//        srl_simple.setOnRefreshListener(this);
//        srl_simple.setColorSchemeResources(
//                R.color.red, R.color.orange, R.color.green, R.color.colorPrimary);
//        //旧版v4包中无下面三个方法
//        int length =imageRes.length;
//        //生成动态数组，并且转入数据
//        ArrayList<HashMap<String, Object>> lstImageItem = new ArrayList<HashMap<String, Object>>();
//        for (Map.Entry<String, List> entry:taskList.entrySet()) {
//            HashMap<String, Object> map = new HashMap<String, Object>();
//            switch (entry.getKey().toString()){
//                case "离场审核":map.put("ItemImage", imageRes[0]);//添加图像资源的ID
//                    map.put("ItemText", entry.getKey().toString());//按序号做ItemText
//                    map.put("ItemCount", entry.getValue().get(0));
//                    lstImageItem.add(map);break;
//                case "入场审核":  map.put("ItemImage", imageRes[1]);//添加图像资源的ID
//                    map.put("ItemText", entry.getKey().toString());//按序号做ItemText
//                    map.put("ItemCount", entry.getValue().get(0));
//                    lstImageItem.add(map);break;
//                case "内部应用变更":  map.put("ItemImage", imageRes[2]);//添加图像资源的ID
//                    map.put("ItemText", entry.getKey().toString());//按序号做ItemText
//                    map.put("ItemCount", entry.getValue().get(0));
//                    lstImageItem.add(map);break;
//                case"请假审核":  map.put("ItemImage", imageRes[3]);//添加图像资源的ID
//                    map.put("ItemText", entry.getKey().toString());//按序号做ItemText
//                    map.put("ItemCount", entry.getValue().get(0));
//                    lstImageItem.add(map);break;
//                case"版本审核":  map.put("ItemImage", imageRes[4]);//添加图像资源的ID
//                    map.put("ItemText", entry.getKey().toString());//按序号做ItemText
//                    map.put("ItemCount", entry.getValue().get(0));
//                    lstImageItem.add(map);break;
//                case "投票":  map.put("ItemImage", imageRes[5]);//添加图像资源的ID
//                    map.put("ItemText", entry.getKey().toString());//按序号做ItemText
//                    map.put("ItemCount", entry.getValue().get(0));
//                    lstImageItem.add(map);break;
//                case "变更测试评估":  map.put("ItemImage", imageRes[6]);//添加图像资源的ID
//                    map.put("ItemText", entry.getKey().toString());//按序号做ItemText
//                    map.put("ItemCount", entry.getValue().get(0));
//                    lstImageItem.add(map);break;
//                case "待办测试评估":  map.put("ItemImage", imageRes[7]);//添加图像资源的ID
//                    map.put("ItemText", entry.getKey().toString());//按序号做ItemText
//                    map.put("ItemCount", entry.getValue().get(0));
//                    lstImageItem.add(map);break;
//                case "工时审核":  map.put("ItemImage", imageRes[8]);//添加图像资源的ID
//                    map.put("ItemText", entry.getKey().toString());//按序号做ItemText
//                    map.put("ItemCount", entry.getValue().get(0));
//                    lstImageItem.add(map);break;
//                default:continue;
//            }
//        }
//        //生成适配器的ImageItem 与动态数组的元素相对应
//        SimpleAdapter saImageItems = new SimpleAdapter(getActivity(),
//                lstImageItem,//数据来源
//                R.layout.item_task,//item的XML实现
//                //动态数组与ImageItem对应的子项
//                new String[]{"ItemImage", "ItemText","ItemCount"},
//                //ImageItem的XML文件里面的一个ImageView,两个TextView ID
//                new int[]{R.id.itemtaskimage, R.id.itemtasktext,R.id.itemtaskcount});
//        //添加并且显示
//        listView.setAdapter(saImageItems);
//        //添加消息处理
//        HashMap<String, List> finalTaskList = taskList;
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent webview = new Intent(getActivity(), WebViewActivity.class);
//                String url=new PropertyUtils(getActivity()).getProperty("CONTROL");
//                ListView listView = (ListView)parent;
//                HashMap<String, String> map = (HashMap<String, String>) listView.getItemAtPosition(position);
//                Bundle bundle=new Bundle();
//                bundle.putString("ServerUrl", url+"/AppPlus/"+ finalTaskList.get(map.get("ItemText")).get(1));
//                bundle.putString("PageName",map.get("ItemText"));
//                bundle.putString("UserInfo",username);
//                webview.putExtras(bundle);
//                startActivity(webview);
//            }
//        });
//        setListViewHeightBasedOnChildren(listView);
//        srl_simple.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                ResultData taskNums=new UserService().getTaskNums(userid,username,usersp,header,getActivity());
//                ArrayList<HashMap<String, Object>> lstImageItem1 = new ArrayList<HashMap<String, Object>>();
//                if(!taskNums.getRet_code().equals("-1")){
//                    HashMap<String, List> taskList = (HashMap<String, List>) taskNums.getRet_data();
//                    for (Map.Entry<String, List> entry:taskList.entrySet()) {
//                        HashMap<String, Object> map = new HashMap<String, Object>();
//                        switch (entry.getKey().toString()){
//                            case "离场审核":map.put("ItemImage", imageRes[0]);//添加图像资源的ID
//                                map.put("ItemText", entry.getKey().toString());//按序号做ItemText
//                                map.put("ItemCount", entry.getValue().get(0));
//                                lstImageItem1.add(map);break;
//                            case "入场审核":  map.put("ItemImage", imageRes[1]);//添加图像资源的ID
//                                map.put("ItemText", entry.getKey().toString());//按序号做ItemText
//                                map.put("ItemCount", entry.getValue().get(0));
//                                lstImageItem1.add(map);break;
//                            case "内部应用变更":  map.put("ItemImage", imageRes[2]);//添加图像资源的ID
//                                map.put("ItemText", entry.getKey().toString());//按序号做ItemText
//                                map.put("ItemCount", entry.getValue().get(0));
//                                lstImageItem1.add(map);break;
//                            case"请假审核":  map.put("ItemImage", imageRes[3]);//添加图像资源的ID
//                                map.put("ItemText", entry.getKey().toString());//按序号做ItemText
//                                map.put("ItemCount", entry.getValue().get(0));
//                                lstImageItem1.add(map);break;
//                            case"版本审核":  map.put("ItemImage", imageRes[4]);//添加图像资源的ID
//                                map.put("ItemText", entry.getKey().toString());//按序号做ItemText
//                                map.put("ItemCount", entry.getValue().get(0));
//                                lstImageItem1.add(map);break;
//                            case "投票":  map.put("ItemImage", imageRes[5]);//添加图像资源的ID
//                                map.put("ItemText", entry.getKey().toString());//按序号做ItemText
//                                map.put("ItemCount", entry.getValue().get(0));
//                                lstImageItem1.add(map);break;
//                            case "变更测试评估":  map.put("ItemImage", imageRes[6]);//添加图像资源的ID
//                                map.put("ItemText", entry.getKey().toString());//按序号做ItemText
//                                map.put("ItemCount", entry.getValue().get(0));
//                                lstImageItem1.add(map);break;
//                            case "待办测试评估":  map.put("ItemImage", imageRes[7]);//添加图像资源的ID
//                                map.put("ItemText", entry.getKey().toString());//按序号做ItemText
//                                map.put("ItemCount", entry.getValue().get(0));
//                                lstImageItem1.add(map);break;
//                            case "工时审核":  map.put("ItemImage", imageRes[8]);//添加图像资源的ID+
//                                map.put("ItemText", entry.getKey().toString());//按序号做ItemText
//                                map.put("ItemCount", entry.getValue().get(0));
//                                lstImageItem1.add(map);break;
//                            default:continue;
//                        }
//                    }
//                }else{
//                    lstImageItem1 = lstImageItem;
//                }
//                ArrayList<HashMap<String, Object>> finalLstImageItem = lstImageItem1;
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        if(!taskNums.getRet_code().equals("-1")){
//                            //生成适配器的ImageItem 与动态数组的元素相对应
//                            SimpleAdapter saImageItems = new SimpleAdapter(getActivity(),
//                                    finalLstImageItem,//数据来源
//                                    R.layout.item_task,//item的XML实现
//                                    //动态数组与ImageItem对应的子项
//                                    new String[]{"ItemImage", "ItemText","ItemCount"},
//                                    //ImageItem的XML文件里面的一个ImageView,两个TextView ID
//                                    new int[]{R.id.itemtaskimage, R.id.itemtasktext,R.id.itemtaskcount});
//                            //添加并且显示
//                            listView.setAdapter(saImageItems);
//                            setListViewHeightBasedOnChildren(listView);
//                            Toast.makeText(getContext(),"数据更新成功", Toast.LENGTH_SHORT).show();
//                        }else{
//                            Toast.makeText(getContext(),"数据更新失败", Toast.LENGTH_SHORT).show();
//                        }
//                        srl_simple.setRefreshing(false);
//                    }
//                }, 1000);
//            }
//        });
        return view;
    }

    public void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        ((ViewGroup.MarginLayoutParams) params).setMargins(10, 10, 10, 10); // 可删除
        listView.setLayoutParams(params);
    }

    @Override
    public void onRefresh() {

    }
}
