package com.johnpetitto.validator;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.TextInputLayout;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

/**
 * An extension of {@code TextInputLayout} that validates the text of its child {@code EditText}
 * and displays an error when the input is invalid.
 *
 * <p>
 * Adding a {@code ValidatingTextInputLayout} to your XML layout file is analogous to adding a
 * {@code TextInputLayout}:
 * <pre>
 * {@code
 * <com.johnpetitto.validator.ValidatingTextInputLayout
 *     android:layout_width="match_parent"
 *     android:layout_height="wrap_content"
 *     app:errorLabel="@string/some_error">
 *
 *     <EditText
 *         android:layout_width="match_parent"
 *         android:layout_height="wrap_content" />
 *
 * </com.johnpetitto.validator.ValidatingTextInputLayout>
 * }
 * </pre>
 *
 * To set a {@link Validator} for your {@code ValidatingTextInputLayout}, call
 * {@link #setValidator(Validator)}:
 * <pre><code>
 * ValidatingTextInputLayout layout = ...
 * layout.setValidator(new Validator() {
 *     public boolean isValid(String input) {
 *         return input.startsWith("J");
 *     }
 * });
 * </code></pre>
 *
 * To validate, simply call {@link #validate()}.
 *
 * <p>
 * There are a handful of predefined validators in {@link Validators}, as well as a utility for
 * validating multiple {@link Validator} objects at once. You can use either the
 * {@link Validators#EMAIL} or {@link Validators#PHONE} validators in XML with the
 * {@code app:validator} tag.
 *
 * @see <a href="http://johnpetitto.com/validator" target="_blank">Introductory Blog Post for
 * Validator</a>
 */
public class ValidatingTextInputLayout extends TextInputLayout {

    private static final int EMAIL_VALIDATOR = 1;
    private static final int PHONE_VALIDATOR = 2;
    private static final int INTEGER_VALIDATOR = 3;
    private static final int BIG_DECIMAL_VALIDATOR = 4;
    private static final int DATE_VALIDATOR = 5;
    private static final int NOT_EMPTY_VALIDATOR = 6;
    private static final int SIGNED_NUMBER_VALIDATOR = 7;
    private static final int ACCOUNT_NUMBER_VALIDATOR = 8;

    private Validator validator;
    private CharSequence errorLabel;
    private boolean validateAfterTextChanged;
    private TextWatcher textWatcher;

    public ValidatingTextInputLayout(Context context) {
        this(context, null);
    }

    public ValidatingTextInputLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ValidatingTextInputLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ValidatingTextInputLayout);

        errorLabel = a.getString(R.styleable.ValidatingTextInputLayout_errorLabel);

        int validatorValue = a.getInt(R.styleable.ValidatingTextInputLayout_validator, 0);
        switch (validatorValue) {
            case EMAIL_VALIDATOR:
                validator = Validators.EMAIL;
                break;
            case PHONE_VALIDATOR:
                validator = Validators.PHONE;
                break;
            case INTEGER_VALIDATOR:
                validator = Validators.INTEGER;
                break;
            case BIG_DECIMAL_VALIDATOR:
                validator = Validators.BIG_DECIMAL;
                break;
            case DATE_VALIDATOR:
                validator = Validators.DATE;
                break;
            case NOT_EMPTY_VALIDATOR:
                validator = Validators.NOT_EMPTY;
                break;
            case SIGNED_NUMBER_VALIDATOR:
                validator = Validators.SIGNED_NUMBER;
                break;
            case ACCOUNT_NUMBER_VALIDATOR:
                validator = Validators.ACCOUNT_NUMBER;
                break;
        }

        validateAfterTextChanged =
                a.getBoolean(R.styleable.ValidatingTextInputLayout_validateAfterTextChanged, false);

        a.recycle();
    }

    @Override
    public void addView(View child, int index, final ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
        setValidateAfterTextChanged(validateAfterTextChanged);
    }

    /**
     * Set a {@link Validator} for validating the contained {@link EditText} input text.
     */
    public void setValidator(@NonNull Validator validator) {
        this.validator = validator;
    }

    /**
     * Set the label to show when {@link #validate()} returns {@code false}.
     */
    public void setErrorLabel(CharSequence label) {
        errorLabel = label;
    }

    /**
     * Set the label to show when {@link #validate()} returns {@code false}.
     */
    public void setErrorLabel(@StringRes int resId) {
        errorLabel = getContext().getString(resId);
    }

    /**
     * Add or remove {@link TextWatcher} for validating the contained {@link EditText}.
     *
     * @param validate if {@code true} then add {@link TextWatcher}, otherwise - remove it
     */
    public void setValidateAfterTextChanged(boolean validate) {
        validateAfterTextChanged = validate;
        if (getEditText() == null) {
            return;
        }
        if (validateAfterTextChanged) {
            getEditText().removeTextChangedListener(textWatcher);
            textWatcher = new TextWatcherValidator(this);
            getEditText().addTextChangedListener(textWatcher);
        } else {
            getEditText().removeTextChangedListener(textWatcher);
        }
    }

    /**
     * Invoke this when you want to validate the contained {@code EditText} input text against the
     * provided {@link Validator}.
     * For validating multiple {@code ValidatingTextInputLayout} objects at once,
     * call {@link Validators#validate}.
     * Throws an {@code IllegalStateException} if either no validator has been set or
     * an error is triggered and no error label is set.
     */
    public boolean validate() {
        if (validator == null) {
            throw new IllegalStateException("A Validator must be set; call setValidator first.");
        }

        CharSequence input = "";
        EditText editText = getEditText();
        if (editText != null) {
            input = editText.getText();
        }

        boolean valid = validator.isValid(input.toString());
        if (valid) {
            setError(null);
        } else {
            if (errorLabel == null) {
                throw new IllegalStateException("An error label must be set when validating an " +
                        "invalid input; call setErrorLabel or app:errorLabel first.");
            }
            setError(errorLabel);
        }

        return valid;
    }
}
