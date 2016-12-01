package de.hsrm.derns002.dsmoa.service.preferences;

import android.content.Context;
import android.preference.DialogPreference;
import android.support.design.widget.Snackbar;
import android.util.AttributeSet;
import android.view.View;

import org.greenrobot.eventbus.EventBus;

import de.hsrm.derns002.dsmoa.service.R;
import de.hsrm.derns002.dsmoa.service.event.DsmCommand;
import de.hsrm.derns002.dsmoa.service.event.DsmCommandEvent;

public class DsmCommandPreference extends DialogPreference {

    private View mView;

    public DsmCommandPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setDialogTitle(getTitle());
        setDialogMessage(R.string.dialog_resetmodel);
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        mView = view;
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);
        if (positiveResult) {
            if (EventBus.getDefault().hasSubscriberForEvent(DsmCommandEvent.class)) {
                EventBus.getDefault().post(new DsmCommandEvent(DsmCommand.RESET_CLUSTERER));
            } else {
                Snackbar.make(mView, R.string.snack_err_servicenotrunning,
                        Snackbar.LENGTH_LONG).show();
            }
        }
    }

}
