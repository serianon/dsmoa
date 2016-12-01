package de.hsrm.derns002.dsmoa.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {

    private static final String TAG = "BootReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive: finished boot has been broadcasted");
        context.startService(new Intent(context, DsmService.class));
    }

}