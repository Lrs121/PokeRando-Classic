package org.lrsservers.pokerando;

import android.app.Application;
import android.content.res.Resources;

public class ResourceFunctions extends Application {
    private static ResourceFunctions mInstance;
    private static Resources res;


    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        res = getResources();
    }

    public static ResourceFunctions getInstance() {
        return mInstance;
    }

    public static Resources getRes() {
        return res;
    }
}
