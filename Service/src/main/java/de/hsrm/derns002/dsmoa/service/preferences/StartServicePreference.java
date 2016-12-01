package de.hsrm.derns002.dsmoa.service.preferences;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.preference.CheckBoxPreference;
import android.util.AttributeSet;
import android.view.View;

import de.hsrm.derns002.dsmoa.lib.ServiceConnector;
import de.hsrm.derns002.dsmoa.service.DsmService;
import de.hsrm.derns002.dsmoa.service.R;

public class StartServicePreference extends CheckBoxPreference {

    private static final boolean DISABLE_PREF_WHEN_RUNNING = true;

    public StartServicePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        refreshStatus();
    }

    @Override
    protected void onClick() {
        super.onClick();
        Intent intent = new Intent();
        intent.setClassName(ServiceConnector.DSMSERVICE_PACKAGE_NAME,
                ServiceConnector.DSMSERVICE_FULL_CLASS_NAME);
        getContext().startService(intent);
        refreshStatus();
    }

    private void refreshStatus() {
        if (isDsmServiceRunning()) {
            setChecked(true);
            if (DISABLE_PREF_WHEN_RUNNING) setEnabled(false);
            setSummary(getContext().getResources().getString(R.string.pref_dsmrunning));
        } else {
            setChecked(false);
            if (DISABLE_PREF_WHEN_RUNNING) setEnabled(true);
            setSummary(getContext().getResources().getString(R.string.pref_dsmnotrunning));
        }
    }

    private boolean isDsmServiceRunning() {
        ActivityManager manager = (ActivityManager) getContext().getSystemService(
                Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo rsi : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (DsmService.class.getName().equals(rsi.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}
