package com.brilliantbear.smscoder;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.brilliantbear.smscoder.sms.SmsBroadcastReceiver;

public class ClipService extends Service {
    public ClipService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("TAG", "create");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String sender = intent.getStringExtra(SmsBroadcastReceiver.KEY_SENDER);
        String code = intent.getStringExtra(SmsBroadcastReceiver.KEY_CODE);
        Log.d("TAG", "sender:" + sender + " code:" + code);

        stopSelf();
        return super.onStartCommand(intent, flags, startId);
    }

}
