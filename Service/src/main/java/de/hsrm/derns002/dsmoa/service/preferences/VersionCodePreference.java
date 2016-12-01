package de.hsrm.derns002.dsmoa.service.preferences;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.preference.Preference;
import android.util.AttributeSet;

public class VersionCodePreference extends Preference {

    public VersionCodePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        int versionCode;
        final PackageManager packageManager = context.getPackageManager();
        if (packageManager != null) {
            try {
                PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
                versionCode = packageInfo.versionCode;
            } catch (PackageManager.NameNotFoundException e) {
                versionCode = -1;
            }
            setSummary(versionCode + "");
        }
    }

}
