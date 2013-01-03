package com.soomla.tapjoy;

import android.util.Log;
import com.soomla.store.SoomlaApp;
import com.soomla.store.StoreConfig;
import com.soomla.store.StoreInventory;
import com.soomla.store.exceptions.VirtualItemNotFoundException;
import com.tapjoy.*;

import java.util.Hashtable;

/**
 * A Singleton class to handle Tapjoy functionality
 */
public class SoomlaTapjoy implements TapjoyNotifier, TapjoySpendPointsNotifier, TapjoyEarnedPointsNotifier {

    private SoomlaTapjoy() {
        Hashtable<String, String> flags = new Hashtable<String, String>();
        flags.put(TapjoyConnectFlag.ENABLE_LOGGING, "true");

        TapjoyConnect.requestTapjoyConnect(SoomlaApp.getAppContext(), "[YOUR TAPJOY APP ID HERE]", "[YOUR TAPJOY SECRET HERE]", flags);
        TapjoyConnect.getTapjoyConnectInstance().setEarnedPointsNotifier(this);
    }

//    public void shutDown() {
//        TapjoyConnect.getTapjoyConnectInstance().sendShutDownEvent();
//    }

    public void showOfferWall() {
        if (StoreConfig.debug) {
            Log.d(TAG, "Showing offer wall.");
        }

        TapjoyConnect.getTapjoyConnectInstance().showOffers();
    }

    public void update() {
        TapjoyConnect.getTapjoyConnectInstance().getTapPoints(this);
    }

    @Override
    public void getUpdatePoints(String currencyName, int pointTotal) {
        if (StoreConfig.debug) {
            Log.d(TAG, "Got getTapPoints response with pointTotal: " + pointTotal);
        }

        TapjoyConnect.getTapjoyConnectInstance().spendTapPoints(pointTotal, this);
        try {
            StoreInventory.addCurrencyAmount(currencyName, pointTotal);
        } catch (VirtualItemNotFoundException e) {
            Log.e(TAG, "The requested currency was not found when trying to update the balance after the user earned currency units from TapJoy. " +
                    "Did you set the name of the currency on Tapjoy the same of one of your currency itemIds ?");
        }
    }


    @Override
    public void getUpdatePointsFailed(String error) {
        if (StoreConfig.debug) {
            Log.d(TAG, "getTapPoints error: " + error);
        }
    }

    @Override
    public void getSpendPointsResponse(String currencyName, int pointTotal)
    {
        if (StoreConfig.debug) {
            Log.d(TAG, "spent: " + pointTotal + " " + currencyName);
        }
    }

    @Override
    public void getSpendPointsResponseFailed(String s) {
        if (StoreConfig.debug) {
            Log.d(TAG, "getSpendPoints error: " + s);
        }
    }

    @Override
    public void earnedTapPoints(int amount)
    {
        if (StoreConfig.debug) {
            Log.d(TAG, "Earned: " + amount);
        }
    }

    private static SoomlaTapjoy sInstance;
    private static String TAG = "SoomlaTapjoy";

    public static SoomlaTapjoy getInstance() {

        if (sInstance == null) {
            sInstance = new SoomlaTapjoy();
        }

        return sInstance;
    }
}
