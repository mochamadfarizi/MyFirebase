package org.online.myfirebase;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.online.myfirebase.activity.seller.DetailProductSeller;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(manifest= Config.NONE)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)

public class TestC2MyFirebase051 extends ViewTest {
    ActivityScenario<DetailProductSeller> scenario;
    private String packageName = "org.online";
    private String targetDevice = "9";
    private int minSDK = 21;
    private String actName = "DetailProductSeller";
    private String layoutName = "activity_detail_product_seller";
    private String backwardComp = "AppCompatActivity";
    //private String packName;
    //ResourceTest rsc;

    @Before
    public void initTest() {
        scenario = ActivityScenario.launch(DetailProductSeller.class);
        scenario.moveToState(Lifecycle.State.CREATED);
    }

    @Test
    public void check_01_ActivityName() { //Check Activity Name (Should be MyActivity)
        scenario.onActivity(activity -> {
            assertEquals("Activity Name is Wrong", actName, activity.getClass().getSimpleName());
        });
    }

    @Test
    public void check_02_LayoutName() { //Check Layout Name (Should be activity_layout)
        scenario.onActivity(activity -> {
            int resId = activity.getResources().getIdentifier(layoutName, "layout", activity.getPackageName());
            assertNotEquals("Layout Name is Wrong", 0, resId);
        });
    }

    @Test
    public void check_03_TextView(){
        scenario.onActivity(activity -> {
            testViewExist("titleProductDetailSeller","TextView", activity);
        });
    }

    @Test
    public void check_04_EditText (){
        scenario.onActivity(activity -> {
            testViewExist("textInputEditTextProductName","EditText", activity);
            testViewExist("textInputEditTextProductPrice","EditText", activity);
        });
    }

    @Test
    public void check_05_Button(){
        scenario.onActivity(activity -> {
            testViewExist("ButtonBack","Button", activity);
        });
    }


    private String getAppName(String packName) {
        String[] list = packName.split("\\.");
        String res = list[list.length-1];
        return res;
    }

}
