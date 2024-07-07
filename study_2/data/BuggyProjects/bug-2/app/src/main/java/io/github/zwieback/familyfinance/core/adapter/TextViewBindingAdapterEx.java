package io.github.zwieback.familyfinance.core.adapter;

import android.databinding.BindingAdapter;
import android.databinding.adapters.TextViewBindingAdapter;
import android.widget.TextView;

import io.github.zwieback.familyfinance.R;

import static android.text.InputType.TYPE_CLASS_TEXT;
import static android.text.InputType.TYPE_NULL;
import static android.text.InputType.TYPE_TEXT_FLAG_MULTI_LINE;

public class TextViewBindingAdapterEx extends TextViewBindingAdapter {

    /**
     * Make TextView not editable and move text to next line if text is long.
     *
     * @param view     which view make not editable
     * @param readOnly {@code true} made view not editable
     * @see <a href="https://stackoverflow.com/a/7853003/8035065">
     * How to make EditText not editable through XML in Android?
     * </a>
     */
    @BindingAdapter("readOnly")
    public static void setReadOnly(TextView view, boolean readOnly) {
        view.setCursorVisible(!readOnly);
        view.setFocusable(!readOnly);
        view.setFocusableInTouchMode(!readOnly);
        if (readOnly) {
            view.setTag(R.id.inputType, view.getInputType());
            if (view.getInputType() == TYPE_NULL) {
                view.setRawInputType(TYPE_CLASS_TEXT | TYPE_TEXT_FLAG_MULTI_LINE);
            }
        } else {
            if (view.getTag(R.id.inputType) instanceof Integer) {
                view.setRawInputType((int) view.getTag(R.id.inputType));
            }
        }
    }
}
