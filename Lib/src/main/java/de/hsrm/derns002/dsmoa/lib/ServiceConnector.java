package de.hsrm.derns002.dsmoa.lib;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

/**
 * Holds the connection to the service. It abstracts from the underlying logic of IPC via AIDL.
 * Initialize an instance and use {@link #connect()} and {@link #disconnect()} for the
 * service-connection. Invoke methods on the service by getting the {@link IService}-reference via
 * {@link #getService()}. Callbacks are handled using the {@link ISimpleServiceListener}. Note that
 * all callback methods will be posted on the handler you provide in the constructor
 */
public class ServiceConnector {

    public static final String DSMSERVICE_PACKAGE_NAME = "de.hsrm.derns002.dsmoa.service";
    public static final String DSMSERVICE_FULL_CLASS_NAME = "de.hsrm.derns002.dsmoa.service.DsmService";

    private static final String TAG = "ServiceConnector";

    private Context mContext;
    private Handler mHandler;
    private IService mService;
    private ISimpleServiceListener mSimpleServiceListener;

    /**
     * Instantiates a ServiceConnector
     *
     * @param context The context of the app
     * @param handler The handler where the callbacks shall be posted on. If you want the callbacks
     *                to be running on the same thread initializing this ServiceConnector, a simple
     *                new Handler() as a parameter will do
     * @param listener The listener which receives the callbacks from the service. All callbacks
     *                 will be run on the thread belonging to your handler
     */
    public ServiceConnector(Context context, Handler handler, ISimpleServiceListener listener) {
        mContext = context;
        mHandler = handler;
        mSimpleServiceListener = listener;
    }

    /**
     * Connects to the service, that is, it starts the service (if it is not running yet), and then
     * binds to that service. Then it will try to register the callback-listener as well, but since
     * this happens in an asynchronous manner, the callback
     * {@link ISimpleServiceListener#onConnected()} will be fired to inform you when the
     * registering worked and everything is fine.
     *
     * The method should be embedded in the lifecycle of the calling activity or service, using
     * {@link #disconnect()} correspondingly.
     *
     * @throws RemoteException If the connection to the service fails
     */
    public void connect() throws RemoteException {
        // binding
        Log.d(TAG, "binding to dsmservice");
        Intent intent = new Intent();
        intent.setClassName(DSMSERVICE_PACKAGE_NAME, DSMSERVICE_FULL_CLASS_NAME);
        mContext.startService(intent);
        boolean bound = mContext.bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
        if (!bound) {
            Log.w(TAG, "couldn't bind to dsmservice");
            throw new RemoteException("couldn't bind to dsmservice");
        }
        // registering the listener happens after async callback from service in
        // this serviceconnections onServiceConnected-callback
    }

    /**
     * Disconnects from the service by unregistering the listener first, and then unbinding.
     *
     * The method should be embedded in the lifecycle of the calling activity or service, using
     * {@link #connect()} correspondingly.
     *
     * @throws RemoteException If the connection to the service fails
     */
    public void disconnect() throws RemoteException {
        Log.d(TAG, "unregistering listener from dsmservice");
        mService.unregisterListener(mServiceListener);
        Log.d(TAG, "unbinding from dsmservice");
        mContext.unbindService(mServiceConnection);
    }

    /**
     * Returns the reference to the service for direct method calls
     *
     * @return A reference to the service for direct method calls
     */
    public IService getService() {
        return mService;
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected from dsmservice");
            mService = IService.Stub.asInterface(service);
            try {
                Log.d(TAG, "registering listener at dsmservice");
                mService.registerListener(mServiceListener);
                Log.d(TAG, "registered listener at dsmservice, connection completed");
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mSimpleServiceListener.onConnected();
                    }
                });
            } catch (RemoteException e) {
                onServiceDisconnected(null);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.e(TAG, "onServiceDisconnected from dsmservice");
            mService = null;
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mSimpleServiceListener.onDisconnected();
                }
            });
        }

    };

    private final IServiceListener.Stub mServiceListener = new IServiceListener.Stub() {

        public void _onLabeledClusterEnter(final String clusterName) {
            Log.d(TAG, "_onLabeledClusterEnter received, calling service connector...");
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mSimpleServiceListener.onLabeledClusterEnter(clusterName);
                }
            });
        }

        public void _onLabeledClusterExit(final String clusterName) {
            Log.d(TAG, "_onLabeledClusterExit received, calling service connector...");
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mSimpleServiceListener.onLabeledClusterExit(clusterName);
                }
            });
        }

        public void _onUnlabeledClusterEnter(final int clusterId) {
            Log.d(TAG, "_onUnlabeledClusterEnter received, calling service connector...");
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mSimpleServiceListener.onUnlabeledClusterEnter(clusterId);
                }
            });
        }

        public void _onUnlabeledClusterExit(final int clusterId) {
            Log.d(TAG, "_onUnlabeledClusterExit received, calling service connector...");
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mSimpleServiceListener.onUnlabeledClusterExit(clusterId);
                }
            });
        }

        public void _onPredictionChange(final String clusterName) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mSimpleServiceListener.onPredictionChange(clusterName);
                }
            });
        }

    };

}
