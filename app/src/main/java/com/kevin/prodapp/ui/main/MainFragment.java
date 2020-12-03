package com.kevin.prodapp.ui.main;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.kevin.prodapp.R;
import com.kevin.prodapp.entity.TFunctionMenu;
import com.kevin.prodapp.interfaces.SortPopCallBack;
import com.kevin.prodapp.ui.list.SecondActivity;
import com.kevin.prodapp.utils.DialogSelectItemUtil;
import com.kevin.prodapp.utils.HttpsUrlConnection;
import com.kevin.prodapp.utils.SharePManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;


public class MainFragment extends Fragment {
    private ViewPager viewPager;
    private int[] imageResIds;
    private DialogSelectItemUtil projectDialog;
    private ArrayList<ImageView> imageViewList;
    private LinearLayout ll_point_container;
    private int previousSelectedPosition = 0;
    private TFunctionMenu tFunctionMenu;
    private String menuelist;
    private ProgressDialog dialog;
    public static SharePManager sharePManager;
    private String serverUrl;
    private String result;
    //private FragmentManager manager;
    public static List<Map<String, Object>> projectData = new ArrayList<Map<String, Object>>();
    boolean isRunning = false;
    private int imageRescloudicon =  R.drawable.cloudicon;
    private int imageRestextprojecticon =  R.drawable.textprojecticon;
    private JSONArray jsonArray=new JSONArray();
    private String title;
    private int[] imageRes = {
            R.drawable.cloudicon,
            R.drawable.textprojecticon,
//            R.drawable.versionsicon,
//            R.drawable.textmeasureicon,
//            R.drawable.textchange,
//            R.drawable.crowdview
    };


    public MainFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        sharePManager = new SharePManager(getActivity(), SharePManager.USER_FILE_NAME);

        try {
            Properties props = new Properties();
            props.load(getActivity().getAssets().open("config.properties"));
            serverUrl =props.getProperty("servers_url");;
            //第一次从配置文件中获取ip,修改过ip之后从缓存获取ip
            String sPMangerIP=sharePManager.getString("servers_url");
            if(sPMangerIP!=null&&!"".equals(sPMangerIP)){
                serverUrl=sPMangerIP;
            }
            final String path = serverUrl + "/getTestProjectTreeView";
            SharePManager sharePManager = new SharePManager(getActivity(), SharePManager.USER_FILE_NAME);
            String cookie = sharePManager.getString("cookie");
            String jksessionid = sharePManager.getString("jksessionid");

            Map<String, String> map = new HashMap<>();
            map.put("cookie", cookie);
            map.put("jksessionid", jksessionid);
            HttpsUrlConnection httpsUrlConnection = new HttpsUrlConnection();
            result = httpsUrlConnection.post(path, "", map, getActivity());
        } catch (Exception e) {
            e.toString();
        }

    }

    /**
     * 初始化要显示的数据
     */
    private void initData() {
        // 图片资源id数组
        imageResIds = new int[]{R.drawable.shenzhou, R.drawable.shenzhou1, R.drawable.shenzhou2, R.drawable.shenzhou3, R.drawable.shenzhou4};
        // 初始化要展示的5个ImageView
        imageViewList = new ArrayList<ImageView>();
        ImageView imageView;
        View pointView;
        LinearLayout.LayoutParams layoutParams;
        for (int i = 0; i < imageResIds.length; i++) {
            // 初始化要显示的图片对象
            imageView = new ImageView(getActivity());
            imageView.setBackgroundResource(imageResIds[i]);
            imageViewList.add(imageView);

            // 加小白点, 指示器
            pointView = new View(getActivity());
            pointView.setBackgroundResource(R.drawable.circlepoint);
            layoutParams = new LinearLayout.LayoutParams(5, 5);
            if (i != 0)
                layoutParams.leftMargin = 10;
            // 设置默认所有都不可用
            pointView.setEnabled(false);
            ll_point_container.addView(pointView, layoutParams);
        }
    }

    private void initAdapter() {
        ll_point_container.getChildAt(0).setEnabled(true);
        previousSelectedPosition = 0;
        // 设置适配器
        viewPager.setAdapter(new MyAdapter());

        // 默认设置到中间的某个位置
        int pos = Integer.MAX_VALUE / 2 - (Integer.MAX_VALUE / 2 % imageViewList.size());
        // 2147483647 / 2 = 1073741823 - (1073741823 % 5)
        viewPager.setCurrentItem(5000000); // 设置到某个位置
    }

    class MyAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }

        // 3. 指定复用的判断逻辑, 固定写法
        @Override
        public boolean isViewFromObject(View view, Object object) {
//          System.out.println("isViewFromObject: "+(view == object));
            // 当划到新的条目, 又返回来, view是否可以被复用.
            // 返回判断规则
            return view == object;
        }

        // 1. 返回要显示的条目内容, 创建条目
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            // container: 容器: ViewPager
            // position: 当前要显示条目的位置 0 -> 4
            int newPosition = position % imageViewList.size();

            ImageView imageView = imageViewList.get(newPosition);
            // a. 把View对象添加到container中
            container.addView(imageView);
            // b. 把View对象返回给框架, 适配器
            return imageView; // 必须重写, 否则报异常
        }

        // 2. 销毁条目
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_blank, container, false);
        buildDialog();
        //加载Banner
        // 初始化布局 View视图
        viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        //获取MainActivity的值
        final Bundle bundle = getArguments();
        String username = bundle.getString("username");
        menuelist = bundle.getString("menuelist");

//        List<TFunctionMenu> menuelist = (List<TFunctionMenu>) bundle.getSerializable("menuelist");
//        int length=menuelist.size();

        final String[] titlename = new String[]{"测试案例", "测试缺陷"};
//        for(int i=0;i< length;i++){
//            titlename[i]=menuelist.get(i).getTitle();
//        }
//        Map<String, List<TFunctionMenu>> tflist=new HashMap<>();
//        for (TFunctionMenu memueEntity:menuelist){
//            tflist.put(memueEntity.getTitle(),memueEntity.getData());
//        }
        ll_point_container = (LinearLayout) view.findViewById(R.id.ll_point_container);
        // Model数据
        initData();
        // Controller 控制器
        initAdapter();
        // 开启轮询
        new Thread() {
            public void run() {
                isRunning = true;
                while (isRunning) {
                    try {
                        Thread.sleep(8000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                            }
                        });
                    }
                }
            }

            ;
        }.start();
        //加载九宫格
        GridView gridView = (GridView) view.findViewById(R.id.gridView);
        //生成动态数组，并且转入数据
        final ArrayList<HashMap<String, Object>> lstImageItem = new ArrayList<HashMap<String, Object>>();

        try {
             jsonArray = new JSONArray(menuelist);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(jsonArray.length()==2) {
            for (int i = 0; i < jsonArray.length(); i++) {
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("ItemImage", imageRes[i]);//添加图像资源的ID
                map.put("ItemText", titlename[i]);//按序号做ItemText
                lstImageItem.add(map);
            }
        }else {
            for (int i = 0; i < jsonArray.length(); i++) {
                HashMap<String, Object> map = new HashMap<String, Object>();
                if (menuelist.contains("测试案例")) {
                    title=titlename[0];
                    map.put("ItemImage", imageRes[0]);
                    map.put("ItemText",  titlename[0]);//按序号做ItemText
                }
                if (menuelist.contains("测试缺陷")) {
                    title=titlename[1];
                    map.put("ItemImage", imageRes[1]);
                    map.put("ItemText",  titlename[1]);//按序号做ItemText
                }
                //添加图像资源的ID

                lstImageItem.add(map);
            }
        }


        //生成适配器的ImageItem 与动态数组的元素相对应
        SimpleAdapter saImageItems = new SimpleAdapter(getActivity(),
                lstImageItem,//数据来源
                R.layout.item_home,//item的XML实现
                //动态数组与ImageItem对应的子项
                new String[]{"ItemImage", "ItemText"},
                //ImageItem的XML文件里面的一个ImageView,两个TextView ID
                new int[]{R.id.img_shoukuan, R.id.txt_shoukuan});
        //添加并且显示
        gridView.setAdapter(saImageItems);

        //添加消息处理
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               //正在加载
                dialog=ProgressDialog.show(getActivity(), "", "正在加载...");
                new Thread() {
                    public void run() {
                        try{
                            sleep(300);
                        } catch (Exception e) {
                            Log.e("tag", e.getMessage());
                        }
                        dialog.dismiss();
                    }
                }.start();

                if(jsonArray.length()==2){
                    title= titlename[position];
                }
                 String sysName = sharePManager.getString("sysName");

                try {
                    JSONObject object = new JSONObject(result);
                    Map<String,String> map = new HashMap<>();
                    if (object.get("code").toString().equals("0")) {
                        JSONArray data = new JSONArray(object.getString("data"));
                        for (int i = 0; i < data.length(); i++) {
                            Map<String, Object> prjMap = new HashMap<>();

                            JSONObject prjObject = data.getJSONObject(i);
                            prjMap.put("title", prjObject.getString("text"));
                            prjMap.put("value", prjObject.getString("items"));
                            prjMap.put("titleName", title);
                            prjMap.put("id", prjObject.getString("id"));
                            projectData.add(prjMap);
//                            projectData.add(prjMap);
                        }

                        for (int i = 0; i < data.length(); i++) {
                            JSONObject prjObject = data.getJSONObject(i);
                            if (prjObject.getString("text").equals(sysName)) {
                                map.put("title", prjObject.getString("text"));
                                map.put("value", prjObject.getString("items"));
                                map.put("titleName", title);
                                map.put("id", prjObject.getString("id"));
                                break;
                            }
                        }
                    }


                    if (menuelist!=null) {
                        if (sysName != null) {
                            if(map.size()==0){
                                Toast.makeText(getContext(), "当前无项目", Toast.LENGTH_SHORT).show();
                            }
                            Intent intent = new Intent();
                            intent.putExtra("PROJECT_LIST", map.get("value").toString());
                            intent.putExtra("titleName", map.get("title").toString());
                            intent.putExtra("id", map.get("id").toString());
                            intent.putExtra("titbun", map.get("titleName").toString());
                            intent.setClass(getActivity(), SecondActivity.class);

                            startActivity(intent);
                        } else {
                            dialog.show();
                            showProjectDialog(title);
                        }

                    } else {
//                        Toast.makeText(getContext(), "您没有权限查看,请先配置权限!", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.toString();
                }


//                if (title.equals("测试缺陷") || title.equals("测试案例")) {
//                    showProjectDialog(title);
//                }
            }
        });
        return view;
    }

    /**
     * 选择列表弹出框
     */
    private void showProjectDialog(String titleName) {
//        final ProgressBar loadingProgressBar = findViewById(R.id.login_progress);
//        if (projectDialog == null) {
        //    final List<Map<String, Object>> projectData = new ArrayList<Map<String, Object>>();
        //数据超过6条就自动滚动
//            String[] titleStrs = new String[]{"谷歌","印象笔记","网易","豆瓣电台","有道词典","知乎"};
//                try {
//                    JSONObject object = new JSONObject(result);
//                    if (object.get("code").toString().equals("0")) {
//                        JSONArray data = new JSONArray(object.getString("data"));
//                        for (int i = 0; i < data.length(); i++) {
//                            Map<String, Object> prjMap = new HashMap<>();
//
//                            JSONObject prjObject = data.getJSONObject(i);
//                            prjMap.put("title", prjObject.getString("text"));
//                            prjMap.put("value", prjObject.getString("items"));
//                            prjMap.put("titleName",titleName);
//                            prjMap.put("id", prjObject.getString("id"));
//                            projectData.add(prjMap);
////                            projectData.add(prjMap);
//                        }
        projectDialog = new DialogSelectItemUtil(getActivity(), projectClick, projectData);
        projectDialog.showDialog();
        dialog.dismiss();
//                    }
//                } catch (Exception e) {
//                    e.toString();
//                }

//        }

    }

    private SortPopCallBack projectClick = new SortPopCallBack() {
        @Override
        public void itemClick(int position, Map<String, Object> map) {
            // TODO Auto-generated method stub
            projectDialog.dismissDialog();
            if (map != null) {
                Toast.makeText(getActivity(), (String) map.get("title"), Toast.LENGTH_SHORT).show();
                String titleName = map.get("title").toString();
                //sharePManager.putString("sysName", titleName);
                String titbun = map.get("titleName").toString();
                Intent intent = new Intent();
                intent.putExtra("PROJECT_LIST", map.get("value").toString());
                intent.putExtra("titleName", map.get("title").toString());
                intent.putExtra("id", map.get("id").toString());
                intent.putExtra("titbun", titbun);
                intent.setClass(getActivity(), SecondActivity.class);


//                projectDialog = null;
                startActivity(intent);
            }
        }
    };

    private void buildDialog() {
        dialog = new ProgressDialog(getActivity());
        dialog.setTitle("加载中...");
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    }
}
