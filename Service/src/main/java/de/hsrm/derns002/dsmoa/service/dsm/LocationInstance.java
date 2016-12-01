package de.hsrm.derns002.dsmoa.service.dsm;

import android.location.Location;

import java.util.ArrayList;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

public class LocationInstance {

    private static final String TAG = "LocationInstance";

    private static final String LAT_ATTR = "latitude";
    private static final String LONG_ATTR = "longitude";
    private static final String CLASSVALUE_ATTR = "classValue";

    private Attribute mLatitudeAttr;
    private Attribute mLongitudeAttr;
    private Attribute mClassValueAttr;
    private Instances mInstances;

    private Instance mCurrentInstance;

    public LocationInstance() {
        mLatitudeAttr = new Attribute(LAT_ATTR);
        mLongitudeAttr = new Attribute(LONG_ATTR);
        mClassValueAttr = new Attribute(CLASSVALUE_ATTR);

        ArrayList<Attribute> attributeList = new ArrayList<>();
        attributeList.add(mLatitudeAttr);
        attributeList.add(mLongitudeAttr);
        attributeList.add(mClassValueAttr);
        mInstances = new Instances(TAG, attributeList, 0);

        // reuseable
        mCurrentInstance = new DenseInstance(mInstances.numAttributes());
    }

    public void setInstance(Location location) {
        mInstances.clear();
        mCurrentInstance.setValue(mLatitudeAttr, location.getLatitude());
        mCurrentInstance.setValue(mLongitudeAttr, location.getLongitude());
        mInstances.add(mCurrentInstance);
        mCurrentInstance.setDataset(mInstances);
    }

    public Instance getInstance() {
        return mCurrentInstance;
    }

}
