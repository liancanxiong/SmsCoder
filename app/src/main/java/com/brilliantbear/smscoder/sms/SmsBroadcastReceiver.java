package com.brilliantbear.smscoder.sms;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.support.v7.app.NotificationCompat;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.brilliantbear.smscoder.ClipService;
import com.brilliantbear.smscoder.R;
import com.brilliantbear.smscoder.utils.ClipUtils;

public class SmsBroadcastReceiver extends BroadcastReceiver {

    public static final int NOTIFICATION_ID = 0;
    public static final String KEY_SENDER = "sender";
    public static final String KEY_CODE = "code";

    public SmsBroadcastReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        Resources res = context.getResources();

        if (!sp.getBoolean(res.getString(R.string.key_switch), true)) {
            return;
        }

        Object[] pdus = (Object[]) intent.getExtras().get("pdus");

        for (Object p : pdus) {
            byte[] sms = (byte[]) p;

            SmsMessage message = SmsMessage.createFromPdu(sms);

            String content = message.getMessageBody();
            String number = message.getOriginatingAddress();

            SMSCode.SMSInfo smsInfo = SMSCode.findSMSCode(content);

            if (smsInfo != null) {
                Log.d("TAG", "收到" + smsInfo.sender + "验证码：" + smsInfo.code);

                String way = sp.getString(res.getString(R.string.key_way), "0");
                if (TextUtils.equals(way, res.getString(R.string.copy_value))) {

                    //toast
                    Toast.makeText(context, "收到" + smsInfo.sender + "验证码：" + smsInfo.code, Toast.LENGTH_LONG).show();
                    ClipUtils.setText(context, "SmsCode", smsInfo.code);
                } else {
                    Intent clipIntent = new Intent(context, ClipService.class);
                    clipIntent.putExtra(KEY_SENDER, smsInfo.sender);
                    clipIntent.putExtra(KEY_CODE, smsInfo.code);
                    PendingIntent pendingIntent = PendingIntent.getService(context, 0, clipIntent, 0);
                    NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
                    Notification notification = mBuilder.setContentTitle(context.getResources().getString(R.string.app_name))
                            .setContentText("收到" + smsInfo.sender + "验证码:" + smsInfo.code + "(点击复制到剪贴板)")
                            .setWhen(System.currentTimeMillis())
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setOngoing(false)
                            .setContentIntent(pendingIntent)
                            .build();
                    notification.flags |= Notification.FLAG_AUTO_CANCEL;
                    mNotificationManager.notify(NOTIFICATION_ID, notification);
                }
            }
        }
    }
}
