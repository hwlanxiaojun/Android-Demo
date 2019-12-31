package com.example.h.myapplication;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class ShowAPPActivity extends AppCompatActivity {
    private ListView listView;
    private TextView tv_title;
    private DBUtils dbUtils;
    private Cursor cursor;
    private String salary;
    private String code;
    private Button button;
    MySimpleCursorDapater adapter;

    private int[] icons = {R.drawable.jd,R.drawable.qq,R.drawable.tm,R.drawable.uc,R.drawable.wx,R.drawable.xl};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_app);
        listView=findViewById(R.id.lv);
        tv_title=findViewById(R.id.tv_title);
        button = findViewById(R.id.btn_exit);
        dbUtils = new DBUtils(this);

        final Intent intent = getIntent();
        code = intent.getStringExtra("app_select");

        if(code.endsWith("01"))
        {
            tv_title.setText("所有应用耗电状态");
            //查询出所有app的信息获取Cursor对象
            cursor = dbUtils.queryall();
        }
        else if (code.endsWith("02"))
        {
            tv_title.setText("正常模式应用耗电状态");
            cursor = dbUtils.querynormal();
        }
        else
        {
            tv_title.setText("节电模式应用耗电状态");
            cursor = dbUtils.querysavepower();
        }

        String from[] = {"name", "scale", "status"};
        int to[] = {R.id.item_tv_name, R.id.item_tv_scale, R.id.item_tv_status};

        adapter = new MySimpleCursorDapater(this, R.layout.list_item, cursor, from, to, SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        listView.setAdapter(adapter);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent();
                setResult(1,intent1);
                finish();
            }
        });
    }
    public class MySimpleCursorDapater extends SimpleCursorAdapter {
        private String  sql;
        private DBUtils db;
        private Cursor c;

        public MySimpleCursorDapater(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
            super(context, layout, c, from, to, flags);
            this.c=c;
            db=new DBUtils(context);
        }

        @Override
        public void bindView(final View view, final Context context, Cursor cursor) {

            final ImageView item_image=(ImageView) view.findViewById(R.id.item_image);

            final Integer imageId=Integer.valueOf(cursor.getString(1));
            final String name=cursor.getString(2);
            salary=cursor.getString(4);

            item_image.setBackgroundResource(icons[imageId]);
            super.bindView(view, context, cursor);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(ShowAPPActivity.this);//单选框
                    builder.setTitle("请选择状态");

                    AlertDialog.Builder builder1 = builder.setSingleChoiceItems(new String[]{"节电模式", "正常模式"}, -1,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which) {
                                        case 0:
                                            salary="节电";
                                            break;
                                        case 1:
                                            salary="正常";
                                            break;
                                    }
                                }
                            }
                    );
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            db.update(name,salary);
                            if(code.endsWith("01"))
                            {
                                //查询出所有app的信息获取Cursor对象
                                c = dbUtils.queryall();
                            }
                            else if (code.endsWith("02"))
                            {
                                c = dbUtils.querynormal();
                            }
                            else
                            {
                                c = dbUtils.querysavepower();
                            }
                            MySimpleCursorDapater.this.swapCursor(c);
                        }
                    });
                    AlertDialog alertDialog = builder.show();
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent1 = new Intent();
        setResult(1,intent1);
    }
}
