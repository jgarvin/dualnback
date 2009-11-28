package org.dualnback.android;

import android.test.ActivityInstrumentationTestCase2;

/**
 * This is a simple framework for a test of an Application.  See
 * {@link android.test.ApplicationTestCase ApplicationTestCase} for more information on
 * how to write and extend Application tests.
 * <p/>
 * To run this test, you can type:
 * adb shell am instrument -w \
 * -e class org.dualnback.android.DualNBackTest \
 * org.dualnback.android.tests/android.test.InstrumentationTestRunner
 */
public class DualNBackTest extends ActivityInstrumentationTestCase2<DualNBack> {

    public DualNBackTest() {
        super("org.dualnback.android", DualNBack.class);
    }

}
