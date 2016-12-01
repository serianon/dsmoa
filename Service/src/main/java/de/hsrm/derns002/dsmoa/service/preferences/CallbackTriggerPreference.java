package de.hsrm.derns002.dsmoa.service.preferences;

import android.content.Context;
import android.preference.Preference;
import android.support.design.widget.Snackbar;
import android.util.AttributeSet;
import android.view.View;

import org.greenrobot.eventbus.EventBus;

import de.hsrm.derns002.dsmoa.service.R;
import de.hsrm.derns002.dsmoa.service.event.CallbackDebugModeEvent;

public class CallbackTriggerPreference extends Preference {

    private View mView;

    public CallbackTriggerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        mView = view;
    }

    @Override
    protected void onClick() {
        super.onClick();
        if (EventBus.getDefault().hasSubscriberForEvent(CallbackDebugModeEvent.class)) {
            EventBus.getDefault().post(new CallbackDebugModeEvent(getSharedPreferences()));
        } else {
            Snackbar.make(mView, R.string.snack_err_servicenotrunning,
                    Snackbar.LENGTH_LONG).show();
        }

    }

}
