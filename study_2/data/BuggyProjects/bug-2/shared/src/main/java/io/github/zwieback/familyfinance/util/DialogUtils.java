package io.github.zwieback.familyfinance.util;

import android.app.DatePickerDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;

import org.threeten.bp.LocalDate;

import java.math.BigDecimal;
import java.util.Calendar;

import io.github.zwieback.familyfinance.calculator.dialog.CalculatorDialog;
import io.github.zwieback.familyfinance.calculator.dialog.OnCalculationResultListener;
import io.github.zwieback.familyfinance.dialog.DatePickerFragmentDialog;

import static io.github.zwieback.familyfinance.util.DateUtils.localDateToCalendar;

public final class DialogUtils {

    public static void showDatePickerDialog(FragmentManager fragmentManager,
                                            @NonNull LocalDate date) {
        DialogFragment datePickerFragment = DatePickerFragmentDialog.newInstance(date);
        datePickerFragment.show(fragmentManager, "datePickerDialog");
    }

    public static void showDatePickerDialog(Context context,
                                            @NonNull LocalDate date,
                                            DatePickerDialog.OnDateSetListener dateSetListener) {
        Calendar calendar = localDateToCalendar(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog dialog = new DatePickerDialog(context, dateSetListener, year, month, day);
        dialog.show();
    }

    public static void showCalculatorDialog(@NonNull Context context,
                                            @NonNull OnCalculationResultListener listener,
                                            @Nullable BigDecimal operand) {
        new CalculatorDialog(context, listener, operand).show();
    }

    private DialogUtils() {
    }
}
