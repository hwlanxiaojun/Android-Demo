package com.example.h.myapplication;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Vibrator;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.h.myapplication.widget.CustomCircleProgressBar;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private CustomCircleProgressBar progressBarOne;
    private CustomCircleProgressBar progressBarTwo;
    private BroadcastReceiver batteryLevelRcvr;
    private IntentFilter batteryLevelFilter;
    private DBUtils dbUtils;

    private int battery_current,battery_next;
    private int count_current,count_next;

    private Button button1,button2,button3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbUtils = new DBUtils(this);

        progressBarOne = (CustomCircleProgressBar) findViewById(R.id.am_progressbar_one);
        progressBarTwo = (CustomCircleProgressBar) findViewById(R.id.am_progressbar_two);

        button1 = findViewById(R.id.btn_1);
        button1.setOnClickListener(this);
        button2 = findViewById(R.id.btn_2);
        button2.setOnClickListener(this);
        button3 = findViewById(R.id.btn_3);
        button3.setOnClickListener(this);

        BatteryManager batteryManager = (BatteryManager)getSystemService(BATTERY_SERVICE);
        battery_current = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);

        progressBarOne.setProgress(battery_current);
        count_current = (int)(dbUtils.querysavacount()/6.0*100);
        progressBarTwo.setProgress(count_current);

        monitorBatteryState();
    }

    private void monitorBatteryState() {
        batteryLevelRcvr = new BroadcastReceiver(){

            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent !=null){
                    String action = intent.getAction();
                    switch (action){
                        case Intent.ACTION_POWER_CONNECTED:
                            Vibrator vibrator_on=(Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);
                            vibrator_on.vibrate(new long[]{0,1000}, -1);
                            Toast.makeText(context,"[电源连接]",Toast.LENGTH_SHORT).show();
                            dbUtils.updateAll("正常");
                            progressBarTwo.setProgress(0);
                            break;
                        case Intent.ACTION_POWER_DISCONNECTED:
                            Vibrator vibrator_off=(Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);
                            vibrator_off.vibrate(new long[]{0,1000},-1);
                            final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);//单选框
                            builder.setTitle("温馨提示：");
                            builder.setMessage("当前电源已断开，是否进入节电模式管理？");
                            builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(MainActivity.this,ShowAPPActivity.class);
                                    intent.putExtra("app_select","02");
                                    startActivity(intent);
                                }
                            });
                            builder.setPositiveButton("取消",null);
                            builder.create().show();
                            Toast.makeText(context,"[电源断开]",Toast.LENGTH_SHORT).show();
                            break;
                        case Intent.ACTION_BATTERY_CHANGED:
                            BatteryManager batteryManager = (BatteryManager)getSystemService(BATTERY_SERVICE);
                            battery_next = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
                            int status = intent.getIntExtra("status", 0);//获取电池状态
                            if (battery_next == 15 && status == BatteryManager.BATTERY_STATUS_DISCHARGING)
                            {
                                dbUtils.updateAll("节电");
                                progressBarTwo.setProgress(100);
                                Toast.makeText(context,"[电量过低，请充电]",Toast.LENGTH_SHORT).show();
                            }
                            if(battery_next == 60 && status == BatteryManager.BATTERY_STATUS_CHARGING)
                            {
                                final AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);//单选框
                                builder1.setTitle("温馨提示：");
                                builder1.setMessage("当前电量恢复正常，是否设置APP恢复为正常模式？");
                                builder1.setNegativeButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(MainActivity.this,ShowAPPActivity.class);
                                        intent.putExtra("app_select","03");
                                        startActivity(intent);
                                    }
                                });
                                builder1.setPositiveButton("取消",null);
                                builder1.create().show();
                                Toast.makeText(context,"[当前电量恢复正常]",Toast.LENGTH_SHORT).show();
                            }
                            if (battery_next != battery_current)
                            {
                                progressBarOne.setProgress(battery_next);
                                battery_current = battery_next;
                            }
                            break;
                    }
                }
            }
        };
        batteryLevelFilter = new IntentFilter();
        batteryLevelFilter.addAction(Intent.ACTION_POWER_CONNECTED);
        batteryLevelFilter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        batteryLevelFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(batteryLevelRcvr,batteryLevelFilter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btn_1:
                Intent intent1 = new Intent(MainActivity.this,ShowAPPActivity.class);
                intent1.putExtra("app_select","01");
                startActivityForResult(intent1,1);
                break;
            case R.id.btn_2:
                Intent intent2 = new Intent(MainActivity.this,ShowAPPActivity.class);
                intent2.putExtra("app_select","02");
                startActivityForResult(intent2,1);
                break;
            case R.id.btn_3:
                Intent intent3 = new Intent(MainActivity.this,ShowAPPActivity.class);
                intent3.putExtra("app_select","03");
                startActivityForResult(intent3,1);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null){
            if (requestCode == 1){
                if (resultCode == 1){
                    count_next = (int)(dbUtils.querysavacount()/6.0*100);
                    if (count_next!=count_current)
                    {
                        progressBarTwo.setProgress(count_next);
                        count_current = count_next;
                    }
                }
            }
        }
    }
}
