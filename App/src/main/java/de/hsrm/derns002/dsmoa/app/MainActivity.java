package de.hsrm.derns002.dsmoa.app;

import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import de.hsrm.derns002.dsmoa.lib.ISimpleServiceListener;
import de.hsrm.derns002.dsmoa.lib.ServiceConnector;

public class MainActivity extends AppCompatActivity implements ISimpleServiceListener {

    private static final String TAG = "MainActivity";
    private ServiceConnector mServiceConnector;
    private TextView mTimeTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mServiceConnector = new ServiceConnector(this, new Handler(), this);
        try {
            mServiceConnector.connect();
        } catch (RemoteException e) {
            Log.e(TAG, "connect failed in app-onCreate");
            // show Dialog or something
        }

        setupButtons();
        mTimeTextView = (TextView) findViewById(R.id.log_textview);
    }

    protected void onDestroy() {
        super.onDestroy();
        // onDestroy actually doesn't get called that often, unlike you'd expect. therefore,
        // the unregistering of the listener isn't happening. The service has to deal with this
        // zombie. You can't use onPause and onStop instead. But actually, you probably don't
        // want to do the connection in an activity, but something like a local service instead,
        // so this so-to-speak-error will be ignored here (DsmService will do fine)
        try {
            mServiceConnector.disconnect();
        } catch (RemoteException e) {
            Log.e(TAG, "disconnect failed in app-onDestroy");
            // show Dialog or something
        }
    }


    @Override
    public void onConnected() {
        appLog("INI", "onConnected");
    }

    @Override
    public void onDisconnected() {
        appLog("INI", "onDisconnected");
    }

    @Override
    public void onLabeledClusterEnter(final String clusterName) {
        appLog("LCI", clusterName);
    }

    @Override
    public void onLabeledClusterExit(final String clusterName) {
        appLog("LCO", clusterName);
    }

    @Override
    public void onUnlabeledClusterEnter(final int clusterId) {
        appLog("UCI", clusterId + "");
    }

    @Override
    public void onUnlabeledClusterExit(final int clusterId) {
        appLog("UCO", clusterId + "");
    }

    @Override
    public void onPredictionChange(final String clusterName) {
        appLog("PC ", clusterName);
    }

    private void appLog(String tag, String message) {
        mTimeTextView.append(String.valueOf(System.currentTimeMillis() / 1000)
                + "/" + tag + ": " + message + "\n");
    }

    private void setupButtons() {
        Button mSetLabelButton = (Button) findViewById(R.id.btn_setlabel);
        if (mSetLabelButton != null) mSetLabelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    appLog("SVC", "setLabelForCurrentCluster: " +
                            mServiceConnector.getService().setLabelForCurrentCluster("test"));
                } catch (RemoteException e) {
                    Log.e(TAG, "direct service call failed");
                    // show Dialog or something
                }
            }
        });
        Button mRemoveLabelButton = (Button) findViewById(R.id.btn_removelabel);
        if (mRemoveLabelButton != null) mRemoveLabelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    appLog("SVC", "removeLabelForCurrentCluster: " +
                            mServiceConnector.getService().removeLabelForCurrentCluster());
                } catch (RemoteException e) {
                    Log.e(TAG, "direct service call failed");
                    // show Dialog or something
                }
            }
        });
        Button mRemoveAllButton = (Button) findViewById(R.id.btn_removeall);
        if (mRemoveAllButton != null) mRemoveAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    appLog("SVC", "removeAllLabels: " +
                            mServiceConnector.getService().removeAllLabels());
                } catch (RemoteException e) {
                    Log.e(TAG, "direct service call failed");
                    // show Dialog or something
                }
            }
        });
    }

}
