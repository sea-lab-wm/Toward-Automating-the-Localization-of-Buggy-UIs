package io.github.zwieback.familyfinance.core.drawer;

import android.app.Activity;
import android.view.View;

import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.util.KeyboardUtil;

public class DrawerListener implements Drawer.OnDrawerListener {

    private final Activity activity;

    public DrawerListener(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void onDrawerOpened(View drawerView) {
        KeyboardUtil.hideKeyboard(activity);
    }

    @Override
    public void onDrawerClosed(View drawerView) {
        // stub
    }

    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {
        // stub
    }
}
