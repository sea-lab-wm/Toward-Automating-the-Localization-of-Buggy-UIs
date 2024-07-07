package io.github.zwieback.familyfinance.util;

import android.content.Context;
import android.support.annotation.NonNull;

import com.annimon.stream.Collectors;
import com.annimon.stream.IntStream;

import java.util.List;

import io.github.zwieback.familyfinance.core.R;

public final class ColorUtils {

    public static List<Integer> collectMaterialDesignColors(@NonNull Context context) {
        int[] colors = context.getResources().getIntArray(R.array.colors_md);
        return IntStream.of(colors).boxed().collect(Collectors.toList());
    }

    private ColorUtils() {
    }
}
