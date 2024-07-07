package io.github.zwieback.familyfinance.business.preference.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;

import io.github.zwieback.familyfinance.R;
import io.github.zwieback.familyfinance.business.preference.fragment.SettingsFragment;
import io.github.zwieback.familyfinance.core.activity.DataActivityWrapper;
import io.github.zwieback.familyfinance.core.model.IBaseEntity;
import io.reactivex.functions.Consumer;

public class SettingsActivity extends DataActivityWrapper
        implements PreferenceFragmentCompat.OnPreferenceStartScreenCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            initFragment();
        }
    }

    @Override
    protected void setupContentView() {
        setContentView(R.layout.activity_settings);
    }

    @Override
    protected int getTitleStringId() {
        return R.string.settings_activity_title;
    }

    @Override
    public boolean onPreferenceStartScreen(PreferenceFragmentCompat preferenceFragmentCompat,
                                           PreferenceScreen preferenceScreen) {
        Fragment fragment = createFragment();
        Bundle args = new Bundle();
        args.putString(PreferenceFragmentCompat.ARG_PREFERENCE_ROOT, preferenceScreen.getKey());
        fragment.setArguments(args);

        getSupportFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.settings_fragment, fragment, preferenceScreen.getKey())
                .addToBackStack(preferenceScreen.getKey())
                .commit();

        return true;
    }

    private void initFragment() {
        Fragment fragment = createFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.settings_fragment, fragment)
                .commit();
    }

    private PreferenceFragmentCompat createFragment() {
        return new SettingsFragment();
    }

    public final <E extends IBaseEntity> void loadEntity(Class<E> entityClass,
                                                         int entityId,
                                                         Consumer<E> onSuccess,
                                                         Consumer<? super Throwable> onError) {
        super.loadEntity(entityClass, entityId, onSuccess, onError);
    }
}
