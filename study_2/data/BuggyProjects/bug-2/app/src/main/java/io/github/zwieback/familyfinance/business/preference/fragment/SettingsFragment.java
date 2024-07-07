package io.github.zwieback.familyfinance.business.preference.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.preference.Preference;

import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.typeface.IIcon;
import com.takisoft.fix.support.v7.preference.PreferenceFragmentCompatDividers;

import io.github.zwieback.familyfinance.R;

public class SettingsFragment extends PreferenceFragmentCompatDividers {

    private static final String INTERFACE_PREFERENCES_NAME = "interface_prefs";
    private static final String DATABASE_PREFERENCES_NAME = "database_prefs";
    private static final String BACKUP_PREFERENCES_NAME = "backup_prefs";
    private static final String ACRA_PREFERENCES_NAME = "acra_prefs";

    @Override
    public void onCreatePreferencesFix(@Nullable Bundle savedInstanceState, String rootKey) {
        setupSharedPreferences(rootKey);
        setPreferencesFromResource(R.xml.fragment_settings, rootKey);
        setupPreferenceScreenIcons(rootKey);
    }

    private void setupSharedPreferences(@Nullable String rootKey) {
        if (DATABASE_PREFERENCES_NAME.equals(rootKey)) {
            getPreferenceManager().setSharedPreferencesName(DATABASE_PREFERENCES_NAME);
        }
    }

    private void setupPreferenceScreenIcons(@Nullable String rootKey) {
        if (rootKey == null) {
            setPreferenceScreenIcon(INTERFACE_PREFERENCES_NAME, CommunityMaterial.Icon.cmd_eye);
            setPreferenceScreenIcon(DATABASE_PREFERENCES_NAME, FontAwesome.Icon.faw_database);
            setPreferenceScreenIcon(BACKUP_PREFERENCES_NAME, CommunityMaterial.Icon.cmd_sync);
            setPreferenceScreenIcon(ACRA_PREFERENCES_NAME, FontAwesome.Icon.faw_bug);
        }
    }

    private void setPreferenceScreenIcon(@NonNull String preferenceScreenKey,
                                         @NonNull IIcon icon) {
        Preference preference = findPreference(preferenceScreenKey);
        if (preference != null) {
            IconicsDrawable drawable = new IconicsDrawable(preference.getContext())
                    .icon(icon)
                    .sizeRes(R.dimen.preference_icon_size);
            preference.setIcon(drawable);
        }
    }
}
