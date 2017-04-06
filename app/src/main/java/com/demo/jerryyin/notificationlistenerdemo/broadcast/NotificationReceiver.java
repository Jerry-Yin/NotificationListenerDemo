//package com.demo.jerryyin.notificationlistenerdemo.broadcast;
//
//import android.app.Notification;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.service.notification.StatusBarNotification;
//import android.util.Log;
//
//import com.demo.jerryyin.notificationlistenerdemo.activity.MainActivity;
//import com.demo.jerryyin.notificationlistenerdemo.service.NotificationMonitor;
//
///**
// * Created by JerryYin on 4/5/17.
// */
//
//public class NotificationReceiver extends BroadcastReceiver {
//
//    public final static String COMMAND = "com.demo.jerryyin.COMMAND_NOTIFICATION_LISTENER_SERVICE";
//    public final static String COMMAND_EXTRA = "command";
//    public final static String CANCEL_ALL = "clearall";
//    public final static String GET_LIST = "list";
//    public final static String WEIXIN = "com.tencent.mm";
//    private static final String TAG = "NotificationReceiver";
//
//    @Override
//        public void onReceive(Context context, Intent intent) {
//            String command = intent.getStringExtra(COMMAND_EXTRA);
//            Log.i(TAG, "Command receive:" + command);
//
//            if (command.equals(CANCEL_ALL)) {
//                context.cancelAllNotifications();
//            } else if (command.equals(GET_LIST)) {
//                int i = 1;
//                for (StatusBarNotification sbn : NLService.this.getActiveNotifications()) {
//                    Intent i2 = new Intent(MainActivity.UPDATE);
//                    i2.putExtra(MainActivity.EVENT, i + " " + sbn.getPackageName() + "\n");
//                    Notification notification = sbn.getNotification();
//                    i2.putExtras(notification.extras);
//                    i2.putExtra(MainActivity.VIEW_S, notification.contentView);
//                    i2.putExtra(MainActivity.View_L, notification.bigContentView);
//
//                    sendBroadcast(i2);
//                    i++;
//                }
//            }
//
//        }
//    }
//}
