package com.demo.jerryyin.notificationlistenerdemo.service;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import com.demo.jerryyin.notificationlistenerdemo.activity.MainActivity;

/**
 * Created by JerryYin on 4/5/17.
 * 删除通知时会回调onNotificationRemoved, 新增通知或是更新时会回调onNotificationPosted

 cancelAllNotifications() ：删除系统中所有 可被清除 的通知；
 cancelNotification(String pkg, String tag, int id) ：删除具体某一个通知；
 getActiveNotifications() ：返回当前系统所有通知到StatusBarNotification[]的列表；
 onNotificationPosted(StatusBarNotification sbn) ：当系统收到新的通知后出发回调；
 onNotificationRemoved(StatusBarNotification sbn) ：当系统通知被删掉后出发回调；
 StatusBarNotification类详解

 StatusBarNotification，多进程传递对象，所有通知信息都会在这个类中通过Binder传递过来.
 内部几个重要的方法如下：

 getId()：返回通知对应的id；
 getNotification()：返回通知对象；
 getPackageName()：返回通知对应的包名；
 getPostTime()：返回通知发起的时间；
 getTag()：返回通知的Tag，如果没有设置返回null；
 isClearable()：返回该通知是否可被清楚，FLAG_ONGOING_EVENT、FLAG_NO_CLEAR；
 isOngoing()：检查该通知的flag是否为FLAG_ONGOING_EVENT；
 */

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class NotificationMonitor extends NotificationListenerService {


    private static final String TAG = "NotificationMonitor";
    public final static String COMMAND = "com.demo.jerryyin.COMMAND_NOTIFICATION_LISTENER_SERVICE";
    public final static String COMMAND_EXTRA = "command";
    public final static String CANCEL_ALL = "clearall";
    public final static String GET_LIST = "list";
    public final static String WEIXIN = "com.tencent.mm";

    private StatusBarNotification mBarNotification;
    private NLServiceReceiver mServiceReceiver;


    @Override
    public void onCreate() {
        super.onCreate();
        mServiceReceiver = new NLServiceReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(COMMAND);
        registerReceiver(mServiceReceiver, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mServiceReceiver);
    }

    @Override
    public void onListenerConnected() {
        super.onListenerConnected();
        Log.i(TAG, "onListenerConnected");
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        super.onNotificationPosted(sbn);
        Log.i(TAG, "ID :" + sbn.getId() + "\t" + sbn.getNotification().tickerText + "\t" + sbn.getPackageName());

        Notification notification = sbn.getNotification();
        Intent i = new Intent(MainActivity.UPDATE);
        i.putExtra(MainActivity.EVENT, "接受 :" + sbn.getPackageName() + "\n");
        i.putExtras(notification.extras);
        i.putExtra(MainActivity.VIEW_S, notification.contentView);
        i.putExtra(MainActivity.View_L, notification.bigContentView);
        sendBroadcast(i);

        onBounReveive(sbn);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void onBounReveive(StatusBarNotification sbn) {
        Notification notification = sbn.getNotification();
        String pkg = sbn.getPackageName();
        if (!pkg.equals(WEIXIN)) return;

        String content = notification.extras.getString(Notification.EXTRA_TEXT);
        if (content.contains("[微信红包]")) {
            final PendingIntent pendingIntent = notification.contentIntent;
            try {
                pendingIntent.send();
            } catch (PendingIntent.CanceledException e) {
            }
        }
    }


    @Override
    public StatusBarNotification[] getActiveNotifications() {
        return super.getActiveNotifications();
    }


    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        Log.i(TAG, "ID :" + sbn.getId() + "\t" + sbn.getNotification().tickerText + "\t" + sbn.getPackageName());
        Intent i = new Intent(MainActivity.UPDATE);
        i.putExtra(MainActivity.EVENT, "移除 :" + sbn.getPackageName() + "\n");
        Notification notification = sbn.getNotification();
        i.putExtras(notification.extras);
        i.putExtra(MainActivity.VIEW_S, notification.contentView);
        i.putExtra(MainActivity.View_L, notification.bigContentView);

        sendBroadcast(i);
    }



    public class NLServiceReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String command = intent.getStringExtra(COMMAND_EXTRA);
            Log.e(TAG, "Command receive:" + command);

            if (command.equals(CANCEL_ALL)) {
                NotificationMonitor.this.cancelAllNotifications();
            } else if (command.equals(GET_LIST)) {
                int i = 1;
                for (StatusBarNotification sbn : NotificationMonitor.this.getActiveNotifications()) {
                    Intent i2 = new Intent(MainActivity.UPDATE);
                    i2.putExtra(MainActivity.EVENT, i + " " + sbn.getPackageName() + "\n");
                    Notification notification = sbn.getNotification();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        i2.putExtras(notification.extras);
                    }
                    i2.putExtra(MainActivity.VIEW_S, notification.contentView);
                    i2.putExtra(MainActivity.View_L, notification.bigContentView);

                    sendBroadcast(i2);
                    i++;
                }
            }

        }
    }
}
