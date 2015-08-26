package com.mbientlab.metawear.app;

import android.content.Context;

import com.kiwiwearables.kiwilib.EndUser;
import com.kiwiwearables.kiwilib.Kiwi;
import com.kiwiwearables.kiwilib.KiwiCallback;
import com.kiwiwearables.kiwilib.KiwiConfiguration;
import com.kiwiwearables.kiwilib.LoggingOptions;
import com.kiwiwearables.kiwilib.SensorUnits;

/**
 * Created by afzal on 15-07-15.
 */
public class KiwiHelper {

    public static Kiwi mKiwi;

    public static Kiwi getInstance(Context context, String vendor, EndUser user, KiwiCallback kiwiCallback) {
        if (mKiwi == null) {
            mKiwi = Kiwi.with(context, new KiwiConfiguration.Builder().useStagingServer().build());
            mKiwi.setSensorUnits(SensorUnits.G_AND_DPS);
            mKiwi.setWebSocketOption(LoggingOptions.LOG_DISABLED);
            mKiwi.setDebugging(true);
        }

        mKiwi.initApp(vendor, "mbientlab@kiwi@ai", "12345678", user, kiwiCallback);
        return mKiwi;
    }
}
