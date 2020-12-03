package com.kevin.prodapp.ui.main;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.kevin.prodapp.R;
import com.kevin.prodapp.utils.DialogSelectItemUtil;
import com.kevin.prodapp.utils.SharePManager;
import com.kevin.prodapp.utils.SysApplication;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class MainActivity extends BaseActivity implements ViewPager.OnPageChangeListener  {

    private DialogSelectItemUtil projectDialog;
    private String selectProjectId = "";
    private TextView tv_pop;
    private ViewPager viewPager;
    private MainFragment fragment1 = new MainFragment();
    private TaskFragment fragment2 = new TaskFragment();
    private SettingFragment fragment3 = new SettingFragment();
    BottomNavigationView navigation;
    private SharePManager sPManger;
    private static final int TIMER = 999;
    //    public MyTimeTask task;
    private boolean isPause = false;
    private  TextView textView;
    private   Button button;
    private TextView resultTv;

//    private String host = "tcp://111.229.209.104:61613";
    private String host = "tcp://192.168.137.1:61613";
    private String userName = "admin";
    private String passWord = "password";
    private int i = 1;

    private Handler handler;

    private MqttClient client;

    private String myTopic = "test/topic";

    private MqttConnectOptions options;

    private ScheduledExecutorService scheduler;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            viewPager.setCurrentItem(item.getOrder());
            resetToDefaultIcon();//重置到默认不选中图片
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    item.setIcon(R.drawable.bottom_view_item_home);
                    return true;
                case R.id.navigation_dashboard:
                    item.setIcon(R.drawable.bottom_view_item_task);
                    return true;
                case R.id.navigation_notifications:
                    item.setIcon(R.drawable.bottom_view_item_setting);
                    return true;
            }
            return false;
        }
    };

    private void resetToDefaultIcon() {
        MenuItem home = navigation.getMenu().findItem(R.id.navigation_home);
        home.setIcon(R.drawable.bottom_view_item_home);
        MenuItem task = navigation.getMenu().findItem(R.id.navigation_dashboard);
        task.setIcon(R.drawable.bottom_view_item_task);
        MenuItem setting = navigation.getMenu().findItem(R.id.navigation_notifications);
        setting.setIcon(R.drawable.bottom_view_item_setting);
    }

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        resultTv = (TextView) findViewById(R.id.textMQTT);

        init();

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 1) {
                    Toast.makeText(MainActivity.this, (String) msg.obj,
                            Toast.LENGTH_SHORT).show();
                    System.out.println("接受的信息:"+msg.obj);
//                    NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//                    Notification notification = new Notification(R.drawable.icon, "Mqtt即时推送", System.currentTimeMillis());
//                    notification.contentView = new RemoteViews(getPackageName(), R.layout.activity_notification);
//                    notification.contentView.setTextViewText(R.id.tv_desc, (String) msg.obj);
//                    notification.defaults = Notification.DEFAULT_SOUND;
//                    notification.flags = Notification.FLAG_AUTO_CANCEL;
//                    manager.notify(i++, notification);

                    String id = "my_channel_01";
                    String name="channelName";
                    NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    Notification notification = null;
                    //安卓版本问题导致通知栏不显示
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        NotificationChannel mChannel = new NotificationChannel(id, name, NotificationManager.IMPORTANCE_LOW);
                       // Toast.makeText(MainActivity.this, mChannel.toString(), Toast.LENGTH_SHORT).show();
//                        Log.i(TAG, mChannel.toString());
                        notificationManager.createNotificationChannel(mChannel);
                        notification = new Notification.Builder(MainActivity.this)
                                .setChannelId(id)
                                .setContentTitle("MQTT即时推送")
                                .setContentText((String) msg.obj)
                                .setSmallIcon(R.drawable.icon).build();
                    } else {
                        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(MainActivity.this)
                                .setContentTitle("MQTT即时推送")
                                .setContentText((String) msg.obj)
                                .setSmallIcon(R.drawable.icon)
                                .setOngoing(true);
//                                .setChannel(id);//无效
                        notification = notificationBuilder.build();
                    }
                    notificationManager.notify(i++, notification);



                } else if (msg.what == 2) {
                    System.out.println("连接成功");

                    Toast.makeText(MainActivity.this, "连接成功", Toast.LENGTH_SHORT).show();
                    try {
                        client.subscribe(myTopic, 1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (msg.what == 3) {
                    Toast.makeText(MainActivity.this, "连接失败，系统正在重连", Toast.LENGTH_SHORT).show();
                    System.out.println("连接失败，系统正在重连");
                }
            }
        };

        startReconnect();


        //退出
        SysApplication.getInstance().addActivity(this);

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        //添加viewPager事件监听
        viewPager.addOnPageChangeListener(this);
        final Intent mainActivity = getIntent();
        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {

                switch (position) {
                    case 0:
                        Bundle bundle = new Bundle();
                        bundle.putString("menuelist",  mainActivity.getStringExtra("menuelist"));
                        bundle.putString("username", mainActivity.getStringExtra("username"));
                        fragment1.setArguments(bundle);
                        return fragment1;
                    case 1:
//                        Bundle bundle1 = new Bundle();
//                        bundle1.putSerializable("taskNums", mainActivity.getSerializableExtra("taskNumsList"));
//                        bundle1.putString("username", mainActivity.getStringExtra("username"));
//                        bundle1.putString("userid", mainActivity.getStringExtra("userid"));
//                        fragment2.setArguments(bundle1);
                        return fragment2;
                    case 2:
                        return fragment3;
                    default:
                        return null;
                }
            }

            @Override
            public int getCount() {
                return 3;
            }
        });
        sPManger = new SharePManager(this, SharePManager.USER_FILE_NAME);
        sPManger.putBoolean("IsClose", false);
        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setItemIconTintList(null);
        startTimer();





    }

    private void startReconnect() {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
                if (!client.isConnected()) {
                    connect();
                }
            }
        }, 0 * 1000, 10 * 1000, TimeUnit.MILLISECONDS);
    }

    private void init() {
        try {
            //host为主机名，test为clientid即连接MQTT的客户端ID，一般以客户端唯一标识符表示，MemoryPersistence设置clientid的保存形式，默认为以内存保存
            client = new MqttClient(host, "test",
                    new MemoryPersistence());
            //MQTT的连接设置
            options = new MqttConnectOptions();
            //设置是否清空session,这里如果设置为false表示服务器会保留客户端的连接记录，这里设置为true表示每次连接到服务器都以新的身份连接
            options.setCleanSession(true);
            //设置连接的用户名
            options.setUserName(userName);
            //设置连接的密码
            options.setPassword(passWord.toCharArray());
            // 设置超时时间 单位为秒
            options.setConnectionTimeout(10);
            // 设置会话心跳时间 单位为秒 服务器会每隔1.5*20秒的时间向客户端发送个消息判断客户端是否在线，但这个方法并没有重连的机制
            options.setKeepAliveInterval(20);
            //设置回调
            client.setCallback(new MqttCallback() {

                @Override
                public void connectionLost(Throwable cause) {
                    //连接丢失后，一般在这里面进行重连
                    System.out.println("connectionLost----------");
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    //publish后会执行到这里
                    System.out.println("deliveryComplete---------"
                            + token.isComplete());
                }

                @Override
                public void messageArrived(String topicName, MqttMessage message)
                        throws Exception {
                    //subscribe后得到的消息会执行到这里面
                    System.out.println("messageArrived----------");
                    Message msg = new Message();
                    msg.what = 1;
                    msg.obj = topicName + "---" + message.toString();
                    handler.sendMessage(msg);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void connect() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    client.connect(options);
                    Message msg = new Message();
                    msg.what = 2;
                    handler.sendMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                    Message msg = new Message();
                    msg.what = 3;
                    handler.sendMessage(msg);
                }
            }
        }).start();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (client != null && keyCode == KeyEvent.KEYCODE_BACK) {
            try {
                client.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return super.onKeyDown(keyCode, event);
    }
//
    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            scheduler.shutdown();
            client.disconnect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
    //
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
