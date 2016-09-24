package com.hjp.mobilesafe;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.MediumTest;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.hjp.mobilesafe.utils.AppInfoProvider;

import org.junit.Test;
import org.junit.runner.RunWith;


import java.util.List;

import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@MediumTest
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentationTest {
    private String TAG="AppTest";
    private Context appContext;

    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.hjp.mobilesafe", appContext.getPackageName());

    }
    public void test(){
        List<AppInfoProvider.AppInfo> allAppInfos = AppInfoProvider.getAllAppInfos(appContext);
        for (AppInfoProvider.AppInfo ai:allAppInfos){
            Log.i(TAG, "test: "+ai.toString());
        }
    }
}