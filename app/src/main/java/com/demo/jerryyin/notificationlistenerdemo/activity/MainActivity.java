package com.demo.jerryyin.notificationlistenerdemo.activity;

import android.annotation.TargetApi;
import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.demo.jerryyin.notificationlistenerdemo.R;
import com.demo.jerryyin.notificationlistenerdemo.service.NotificationMonitor;
import com.demo.jerryyin.notificationlistenerdemo.util.NotificationUtil;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public final static String UPDATE = "com.baidu.notifymgr.NOTIFICATION_LISTENER_EXAMPLE";
    public final static String EVENT = "notification_event";
    final static String ICON_S = "small_icon";
    public final static String VIEW_S = "view_small";
    public final static String View_L = "view_large";

    private Button mBtnStartService, mBtnStart, mBtnStop, mBtnClean, mBtnCur;

    private TextView mInfoTex;
    private NotificationReceiver mNotificationReceiver;
    private ListView mListView;
    private List<NTBean> mInfoList;
    private InfoListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

       FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        setupViews();
        initdata();
    }

    private void setupViews() {
        mBtnStartService = (Button) findViewById(R.id.btn_start_service);
        mBtnStart = (Button) findViewById(R.id.btn_start);
        mBtnStop = (Button) findViewById(R.id.btn_stop);
        mBtnClean = (Button) findViewById(R.id.btn_clean);
        mBtnCur = (Button) findViewById(R.id.btn_cur);
        mBtnStartService.setOnClickListener(this);
        mBtnStart.setOnClickListener(this);
        mBtnStop.setOnClickListener(this);
        mBtnClean.setOnClickListener(this);
        mBtnCur.setOnClickListener(this);
        mListView = (ListView) findViewById(R.id.listview);
    }

    private void initdata() {
        mNotificationReceiver = new NotificationReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(UPDATE);
        registerReceiver(mNotificationReceiver, filter);

        mInfoList = new ArrayList<>();
        mAdapter = new InfoListAdapter(this, mInfoList);
        mListView.setAdapter(mAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_start_service:

                break;

            case R.id.btn_start:
                if (!NotificationUtil.isEnabled(this)) {
//                    “android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS”
                    Intent i = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
                    startActivity(i);
                }else {
                    Toast.makeText(this, "已开启服务权限", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.btn_stop:
                Intent intent = new Intent(this, NotificationMonitor.class);
                stopService(intent);

                break;

            case R.id.btn_clean:
                Intent intent1 = new Intent(NotificationMonitor.COMMAND);
                intent1.putExtra(NotificationMonitor.COMMAND_EXTRA, NotificationMonitor.CANCEL_ALL);
                sendBroadcast(intent1);
                break;

            case R.id.btn_cur:
                Intent i4 = new Intent(NotificationMonitor.COMMAND);
                i4.putExtra(NotificationMonitor.COMMAND_EXTRA, NotificationMonitor.GET_LIST);
                sendBroadcast(i4);
//                v.setEnabled(false);
                break;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mNotificationReceiver);
    }


    class NotificationReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String temp = mInfoList.size() + "：" + intent.getStringExtra(EVENT);
            NTBean bean = new NTBean();
            bean.info = temp;

            Bundle budle = intent.getExtras();
            bean.title = budle.getString(Notification.EXTRA_TITLE);
            bean.text = budle.getString(Notification.EXTRA_TEXT);
            bean.subText = budle.getString(Notification.EXTRA_SUB_TEXT);
            bean.largeIcon = budle.getParcelable(Notification.EXTRA_LARGE_ICON);
            Drawable icon = budle.getParcelable(Notification.EXTRA_SMALL_ICON);
            bean.smallIcon = icon;

            bean.viewS = budle.getParcelable(VIEW_S);
            bean.viewL = budle.getParcelable(View_L);

            mInfoList.add(bean);
            Log.i("changxing", "receive:" + temp + "\n" + budle);
            mAdapter.notifyDataSetChanged();
        }

    }


    class NTBean {
        String info;
        String title;
        String text;
        String subText;
        Drawable smallIcon;
        Bitmap largeIcon;
        RemoteViews viewS, viewL;
    }

    public class InfoListAdapter extends BaseAdapter {
        private List<NTBean> nInfoList;
        private LayoutInflater nInflater;
        private Context nContext;

        public InfoListAdapter(Context cxt, List<NTBean> source) {
            nInfoList = source;
            nInflater = LayoutInflater.from(cxt);
            nContext = cxt;
        }

        @Override
        public int getCount() {
            return nInfoList.size();
        }

        @Override
        public Object getItem(int position) {
            return nInfoList.get(position);
        }

        @TargetApi(Build.VERSION_CODES.M)
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = nInflater.inflate(R.layout.list_item, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.title = (TextView) convertView.findViewById(R.id.title);
                viewHolder.notiTitel = (TextView) convertView.findViewById(R.id.noti_title);
                viewHolder.notiText = (TextView) convertView.findViewById(R.id.noti_text);
                viewHolder.notiSubText = (TextView) convertView.findViewById(R.id.noti_sub_text);
                viewHolder.smallIcon = (ImageView) convertView.findViewById(R.id.noti_small_icon);
                viewHolder.largeIcon = (ImageView) convertView.findViewById(R.id.noti_large_icon);
                viewHolder.contentView = (ViewGroup) convertView.findViewById(R.id.noti_content_view);
                viewHolder.bigContentView = (ViewGroup) convertView.findViewById(R.id.noti_bit_content_view);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            NTBean bean = nInfoList.get(position);
            viewHolder.title.setText(bean.info);
            viewHolder.notiTitel.setText(bean.title);
            viewHolder.notiSubText.setText(bean.subText);
            viewHolder.notiText.setText(bean.text);
            viewHolder.largeIcon.setImageBitmap(bean.largeIcon);
            viewHolder.smallIcon.setImageDrawable(bean.smallIcon);//high api level
            viewHolder.contentView.removeAllViews();
            viewHolder.bigContentView.removeAllViews();

            if (bean.viewS != null) {
                View view = bean.viewS.apply(nContext, viewHolder.contentView);
                viewHolder.contentView.addView(view);
            }
            if (bean.viewL != null) {
                View view = bean.viewL.apply(nContext, viewHolder.bigContentView);
                viewHolder.bigContentView.addView(view);
            }
            return convertView;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

    }

    public static class ViewHolder {
        public TextView title, notiTitel, notiText, notiSubText;
        public ImageView smallIcon, largeIcon;
        public ViewGroup contentView, bigContentView;
    }
}
