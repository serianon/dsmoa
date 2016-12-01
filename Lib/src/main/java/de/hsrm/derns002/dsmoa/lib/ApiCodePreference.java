package de.hsrm.derns002.dsmoa.lib;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.preference.Preference;
import android.util.AttributeSet;

public class ApiCodePreference extends Preference {

    public ApiCodePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        int versionCode;
        final PackageManager packageManager = context.getPackageManager();
        if (packageManager != null) {
            try {
                PackageInfo packageInfo = packageManager.getPackageInfo("de.hsrm.derns002.dsmoa.lib", 0);
                versionCode = packageInfo.versionCode;
            } catch (PackageManager.NameNotFoundException e) {
                versionCode = -1;
            }
            setSummary("API " + versionCode);
        }
    }

}
