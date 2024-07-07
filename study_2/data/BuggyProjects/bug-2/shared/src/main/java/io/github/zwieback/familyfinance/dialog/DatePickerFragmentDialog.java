package io.github.zwieback.familyfinance.dialog;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import org.threeten.bp.LocalDate;

import java.util.Calendar;

import static io.github.zwieback.familyfinance.util.DateUtils.localDateToCalendar;
import static io.github.zwieback.familyfinance.util.DateUtils.readLocalDateFromBundle;
import static io.github.zwieback.familyfinance.util.DateUtils.writeLocalDateToBundle;

public final class DatePickerFragmentDialog extends DialogFragment implements OnDateSetListener {

    private OnDateSetListener dateSetListener;

    public static DatePickerFragmentDialog newInstance(@NonNull LocalDate date) {
        DatePickerFragmentDialog fragment = new DatePickerFragmentDialog();
        Bundle args = new Bundle();
        writeLocalDateToBundle(args, date);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnDateSetListener) {
            this.dateSetListener = (OnDateSetListener) context;
        } else {
            throw new ClassCastException(context.toString() + " must implement OnDateSetListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LocalDate date = readLocalDateFromBundle(getArguments());
        Calendar calendar = localDateToCalendar(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return new DatePickerDialog(getContext(), this, year, month, day);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        dateSetListener.onDateSet(view, year, month, day);
    }
}
