package de.hsrm.derns002.dsmoa.service;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;

import de.hsrm.derns002.dsmoa.service.dsm.LocationMiner;
import moa.cluster.Cluster;
import moa.core.AutoExpandVector;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "MapsActivity";
    private static final String MACROCLUSTERS_STATEKEY = "mMacroClusters";
    private static final String MICROCLUSTERS_STATEKEY = "mMicroClusters";
    private GoogleMap mMap;
    private AutoExpandVector<Cluster> mMacroClusters;
    private AutoExpandVector<Cluster> mMicroClusters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().
                findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_maps, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_center_on_macroclusters:
                centerOnMacroClusters();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveClusters(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        restoreClusters(savedInstanceState);
    }

    private void saveClusters(Bundle bundle) {
        Log.d(TAG, "saving clusters");
        bundle.putSerializable(MACROCLUSTERS_STATEKEY, mMacroClusters);
        bundle.putSerializable(MICROCLUSTERS_STATEKEY, mMicroClusters);
    }

    private void restoreClusters(Bundle bundle) {
        Log.d(TAG, "restoring clusters");
        Serializable macroClustersSerializable = bundle.getSerializable(MACROCLUSTERS_STATEKEY);
        if (macroClustersSerializable instanceof  AutoExpandVector) {
            mMacroClusters = (AutoExpandVector<Cluster>) macroClustersSerializable;
        }
        Serializable microClustersSerializable = bundle.getSerializable(MICROCLUSTERS_STATEKEY);
        if (microClustersSerializable instanceof  AutoExpandVector) {
            mMicroClusters = (AutoExpandVector<Cluster>) microClustersSerializable;
        }
    }

    @SuppressWarnings("unused") // will be called by EventBus
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMiningUpdate(LocationMiner locationMiner) {
        if (locationMiner.getLocationClusterer().getMacroClusters() != null) {
            mMacroClusters = locationMiner.getLocationClusterer().getMacroClusters().getClustering();
        }
        if (locationMiner.getLocationClusterer().getMicroClusters() != null) {
            mMicroClusters = locationMiner.getLocationClusterer().getMicroClusters().getClustering();
        }

        showAllClusters();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // initially show germany as center
        LatLng germanyCenter = new LatLng(51.345503, 10.288293);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(germanyCenter, 5.5f));
        showAllClusters();
    }

    private void showAllClusters() {
        mMap.clear();
        showMacroClusters();
        showMicroClusters();
    }

    private void showMacroClusters() {
        if (mMacroClusters == null || mMap == null) return;

        double[] currentCenter;
        LatLng currentCenterLatLng;
        for (int i = 0; i < mMacroClusters.size(); i++) {
            currentCenter = mMacroClusters.get(i).getCenter();
            currentCenterLatLng = new LatLng(currentCenter[0], currentCenter[1]);
            mMap.addMarker(new MarkerOptions()
                    .title("ID = " + String.valueOf(mMacroClusters.get(i).getId()))
                    .position(currentCenterLatLng)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET))
                    .draggable(false));
        }
    }

    private void showMicroClusters() {
        if (mMicroClusters == null || mMap == null) return;

        double[] currentCenter;
        LatLng currentCenterLatLng;
        for (int i = 0; i < mMicroClusters.size(); i++) {
            currentCenter = mMicroClusters.get(i).getCenter();
            currentCenterLatLng = new LatLng(currentCenter[0], currentCenter[1]);
            mMap.addMarker(new MarkerOptions()
                    .title("ID = " + String.valueOf(mMicroClusters.get(i).getId()))
                    .position(currentCenterLatLng)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
                    .alpha(0.4f)
                    .draggable(false));
        }
    }

    private void centerOnMacroClusters() {
        if (mMacroClusters == null || mMap == null || mMacroClusters.size() == 0) return;

        LatLngBounds.Builder boundBuilder = new LatLngBounds.Builder();
        double[] currentCenter;
        LatLng currentCenterLatLng;
        for (int i = 0; i < mMacroClusters.size(); i++) {
            currentCenter = mMacroClusters.get(i).getCenter();
            currentCenterLatLng = new LatLng(currentCenter[0], currentCenter[1]);
            boundBuilder.include(currentCenterLatLng);
        }

        LatLngBounds bounds = boundBuilder.build();
        if (mMacroClusters.size() > 1) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 200));
        } else {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(bounds.getCenter(), 10.0f));
        }
    }

}
