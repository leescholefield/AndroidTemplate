package com.scholefield.lee.androidtemplate.ui;

import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import com.lee.scholefield.androidtemplate.ui.DummyActivity;
import com.lee.scholefield.androidtemplate.ui.TestActivity;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 *
 */
@RunWith(AndroidJUnit4.class)
public class BaseFragmentImplTest {

    private FragmentImpl classUnderTest;

    @Rule
    public ActivityTestRule<DummyActivity> activityTestRule = new ActivityTestRule<>(DummyActivity.class);

    @Before
    public void setUp() throws Exception {
        activityTestRule.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                classUnderTest = new FragmentImpl();
                FragmentManager manager = activityTestRule.getActivity().getSupportFragmentManager();
                manager.beginTransaction().add(classUnderTest,"fragmentImpl").addToBackStack("fragmentImpl").commitAllowingStateLoss();
            }
        });

        InstrumentationRegistry.getInstrumentation().waitForIdleSync();
    }

    @Test
    public void fragment_is_added() throws Exception {
        Fragment fragment = activityTestRule.getActivity().getSupportFragmentManager().findFragmentByTag("fragmentImpl");
        assertNotNull(fragment);
    }

    @Test
    public void presenter_is_instantiated() throws Exception {
        assertNotNull(classUnderTest.presenter);
    }

    @Test
    public void getPresenter_returns_presenter() throws Exception {
        PresenterImpl presenter = classUnderTest.getPresenter();

        assertNotNull(presenter);
    }

    @Test
    public void presenter_saved_to_presenter_manager() throws Exception {
        assertNotNull(PresenterManager.getInstance().getPresenter("presenterKey"));
    }

    @Test
    public void deregisterPresenter_deregisters_presenter() throws Exception {
        classUnderTest.onFragmentDestroyed();

        assertNull(PresenterManager.getInstance().getPresenter(classUnderTest.getPresenterKey()));
    }

    @Test
    public void presenter_deregistered_when_fragment_removed() throws Exception {
        assertNotNull(PresenterManager.getInstance().getPresenter(classUnderTest.getPresenterKey()));

        activityTestRule.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activityTestRule.getActivity().getSupportFragmentManager().beginTransaction().remove(classUnderTest).commit();
            }
        });
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();

        assertNull(PresenterManager.getInstance().getPresenter(classUnderTest.getPresenterKey()));
    }

    @Test
    public void presenter_deregistered_when_host_activity_ends() throws Exception {
        activityTestRule.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activityTestRule.getActivity().finish();
            }
        });
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();

        assertNull(PresenterManager.getInstance().getPresenter(classUnderTest.getPresenterKey()));
    }


    public static class FragmentImpl extends BaseFragmentImpl<PresenterImpl> {

        @Override
        protected String getPresenterKey() {
            return "presenterKey";
        }

        @Override
        protected PresenterImpl createPresenter() {
            return new PresenterImpl("presenterKey");
        }
    }

    private static class PresenterImpl extends BasePresenterImpl {

        private PresenterImpl(String key) {
            super(key);
        }

    }

}