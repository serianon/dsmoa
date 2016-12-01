package de.hsrm.derns002.dsmoa.service;

import android.app.Application;

import com.karumi.dexter.Dexter;

public class ServiceApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Dexter.initialize(this);
    }

}
