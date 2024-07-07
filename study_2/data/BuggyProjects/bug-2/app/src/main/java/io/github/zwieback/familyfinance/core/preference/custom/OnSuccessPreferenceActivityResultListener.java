package io.github.zwieback.familyfinance.core.preference.custom;

import android.content.Intent;
import android.support.annotation.NonNull;

public interface OnSuccessPreferenceActivityResultListener {

    void onSuccessResult(@NonNull Intent resultIntent);
}
