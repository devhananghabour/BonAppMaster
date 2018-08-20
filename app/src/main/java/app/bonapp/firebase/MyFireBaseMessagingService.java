package app.bonapp.firebase;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.app.bonapp.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import java.util.Map;
import java.util.Random;

import app.bonapp.activities.HomeActivity;
import app.bonapp.activities.LoginActivity;
import app.bonapp.activities.OrderDetailActivity;
import app.bonapp.activities.RestaurantDetailActivity;
import app.bonapp.constants.Constants;
import app.bonapp.serivces.SoundService;
import app.bonapp.utils.AppSharedPrefs;

public class MyFireBaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FireBase";
    private int NOTIFICATION_ID = 999;

    private String notificationMsg, notificationTitle;
    private int notificationType;
    private String createdAt, recipeId;
    private int requestID = 111;
    private JSONObject messageObj;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e(TAG, "==========================Push NotificationFragment Received========================");
        Log.e(TAG, "Message data payload: " + remoteMessage.getData());
        sendNotification(remoteMessage.getData());

    }


    //This method is only generating push notification
    private void sendNotification(Map<String, String> dataMap) {
        //notificationMsg = mainObject.getString("alert");
        notificationMsg = dataMap.get("notification_message");
        //messageObj = mainObject.getJSONObject("message");
        notificationTitle = dataMap.get("title");
        String orderId="";
        if (dataMap.containsKey("order_id")) {
            orderId = dataMap.get("order_id");
        }
        String type=dataMap.get("type");        //type: 1 for order 2 for admin random push 3 for new deal
        String merchantId="",customerName="";
        if (dataMap.containsKey("merchant_id")) {
            merchantId = dataMap.get("merchant_id");
        }
        if (dataMap.containsKey("customer_name")){
            customerName=dataMap.get("customer_name");
        }
        handleNotification(type,orderId,merchantId,customerName);


    }

    //method to get notificationManager
    public NotificationManager getNotificationManager() {
        return (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    /**
     * @return small icon with transparent bg for post lollipop
     */
    private int getSmallIcon() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return R.drawable.ic_notification;            //transparent
        } else {
            return R.mipmap.ic_launcher;
        }
    }

    /**
     * method to handle notification
     *
     * @param eventType
     */
    private void handleNotification(String eventType,String orderId,String merchantId,String customerName) {
        String channelId = "channel-01";
        switch (eventType) {
            case "1":
                Intent notificationIntent;
                startService(new Intent(this, SoundService.class));
                if (AppSharedPrefs.getInstance(getApplicationContext()).getBoolean(AppSharedPrefs.PREF_KEY.IS_LOGIN,false)) {
                    notificationIntent = new Intent(this, OrderDetailActivity.class);
                    notificationIntent.putExtra(Constants.ORDER_ID,orderId);
                    notificationIntent.putExtra(Constants.IS_FROM_NOTIFICATION,true);
                    notificationIntent.putExtra(Constants.CUSTOMER_NAME,customerName);

                } else {
                    notificationIntent = new Intent(this, LoginActivity.class);
                }
                TaskStackBuilder tsp = TaskStackBuilder.create(this);
                tsp.addParentStack(HomeActivity.class);
                tsp.addNextIntent(notificationIntent);
                //orderPlacedIntent.putExtra("type",notificationType);
                notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                PendingIntent contentIntent = tsp.getPendingIntent(requestID, PendingIntent.FLAG_UPDATE_CURRENT);
                //orderPlacedIntent.setData((Uri.parse("mystring" + requestID)));
                Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                Bitmap bm = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);


                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    int importance = NotificationManager.IMPORTANCE_DEFAULT;
                    String channelName = "Channel Name";

                    NotificationChannel mChannel = new NotificationChannel(
                            channelId, channelName, importance);
                    getNotificationManager().createNotificationChannel(mChannel);
                }


                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                        this).setSmallIcon(getSmallIcon())
                        .setLargeIcon(bm)
                        .setContentTitle(notificationTitle)
                        .setAutoCancel(true)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(notificationMsg))
                        .setContentText(notificationMsg)
                        .setSound(soundUri).setChannelId(channelId);
                mBuilder.setContentIntent(contentIntent);
                getNotificationManager().notify(new Random().nextInt(NOTIFICATION_ID) + 1, mBuilder.build());

                break;
            case "2":
                Intent homeIntent;
                homeIntent = new Intent(this, HomeActivity.class);
                TaskStackBuilder tsp1 = TaskStackBuilder.create(this);
                tsp1.addParentStack(HomeActivity.class);
                tsp1.addNextIntent(homeIntent);
                homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                PendingIntent contentIntent1 = tsp1.getPendingIntent(requestID, PendingIntent.FLAG_UPDATE_CURRENT);
                Uri soundUri1 = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                Bitmap bm1 = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    int importance = NotificationManager.IMPORTANCE_DEFAULT;
                    String channelName = "Channel Name";

                    NotificationChannel mChannel = new NotificationChannel(
                            channelId, channelName, importance);
                    getNotificationManager().createNotificationChannel(mChannel);
                }


                NotificationCompat.Builder mBuilder1 = new NotificationCompat.Builder(
                        this).setSmallIcon(getSmallIcon())
                        .setLargeIcon(bm1)
                        .setContentTitle(notificationTitle)
                        .setAutoCancel(true)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(notificationMsg))
                        .setContentText(notificationMsg)
                        .setSound(soundUri1)
                        .setChannelId(channelId);
                mBuilder1.setContentIntent(contentIntent1);
                getNotificationManager().notify(new Random().nextInt(NOTIFICATION_ID) + 1, mBuilder1.build());

                break;
            case "3":
                Intent merchantIntent;
                merchantIntent = new Intent(this, RestaurantDetailActivity.class);
                merchantIntent.putExtra(Constants.MERCHANT_ID_KEY,merchantId);
                merchantIntent.putExtra(Constants.IS_FROM_PUSH,true);
                TaskStackBuilder tsp2 = TaskStackBuilder.create(this);
                tsp2.addParentStack(RestaurantDetailActivity.class);
                tsp2.addNextIntent(merchantIntent);
                merchantIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                PendingIntent contentIntent2 = tsp2.getPendingIntent(requestID, PendingIntent.FLAG_UPDATE_CURRENT);
                Uri soundUri2 = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                Bitmap bm2 = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    int importance = NotificationManager.IMPORTANCE_DEFAULT;

                    String channelName = "Channel Name";

                    NotificationChannel mChannel = new NotificationChannel(
                            channelId, channelName, importance);
                    getNotificationManager().createNotificationChannel(mChannel);
                }

                NotificationCompat.Builder mBuilder2 = new NotificationCompat.Builder(
                        this).setSmallIcon(getSmallIcon())
                        .setLargeIcon(bm2)
                        .setContentTitle(notificationTitle)
                        .setAutoCancel(true)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(notificationMsg))
                        .setContentText(notificationMsg)
                        .setSound(soundUri2)
                        .setChannelId(channelId);
                mBuilder2.setContentIntent(contentIntent2);
                getNotificationManager().notify(new Random().nextInt(NOTIFICATION_ID) + 1, mBuilder2.build());


                break;
            case "4":
                refreshOrderDetailsScreen();
                Intent notificationIntent4;
                if (AppSharedPrefs.getInstance(getApplicationContext()).getBoolean(AppSharedPrefs.PREF_KEY.IS_LOGIN,false)) {
                    notificationIntent4 = new Intent(this, OrderDetailActivity.class);
                    notificationIntent4.putExtra(Constants.ORDER_ID,orderId);
                    notificationIntent4.putExtra(Constants.IS_FROM_NOTIFICATION,true);
                    notificationIntent4.putExtra(Constants.CUSTOMER_NAME,customerName);

                } else {
                    notificationIntent4 = new Intent(this, LoginActivity.class);
                }
                TaskStackBuilder tsp4 = TaskStackBuilder.create(this);
                tsp4.addParentStack(HomeActivity.class);
                tsp4.addNextIntent(notificationIntent4);
                //orderPlacedIntent.putExtra("type",notificationType);
                notificationIntent4.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                PendingIntent contentIntent4 = tsp4.getPendingIntent(requestID, PendingIntent.FLAG_UPDATE_CURRENT);
                //orderPlacedIntent.setData((Uri.parse("mystring" + requestID)));
                Uri soundUri4 = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                Bitmap bm4 = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);


                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    int importance = NotificationManager.IMPORTANCE_DEFAULT;
                    String channelName = "Channel Name";

                    NotificationChannel mChannel = new NotificationChannel(
                            channelId, channelName, importance);
                    getNotificationManager().createNotificationChannel(mChannel);
                }


                NotificationCompat.Builder mBuilder4 = new NotificationCompat.Builder(
                        this).setSmallIcon(getSmallIcon())
                        .setLargeIcon(bm4)
                        .setContentTitle(notificationTitle)
                        .setAutoCancel(true)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(notificationMsg))
                        .setContentText(notificationMsg)
                        .setSound(soundUri4).setChannelId(channelId);
                mBuilder4.setContentIntent(contentIntent4);
                getNotificationManager().notify(new Random().nextInt(NOTIFICATION_ID) + 1, mBuilder4.build());
                break;

        }
    }

    private void refreshOrderDetailsScreen(){
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(OrderDetailActivity.REFRESH_ORDER_STATUS));
    }

}

