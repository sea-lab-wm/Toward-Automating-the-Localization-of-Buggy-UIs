package io.github.zwieback.familyfinance.widget.filter;

import android.text.InputFilter;
import android.text.Spanned;

import io.github.zwieback.familyfinance.util.NumberUtils;
import io.github.zwieback.familyfinance.util.StringUtils;

import static io.github.zwieback.familyfinance.util.StringUtils.EMPTY;

/**
 * Got an idea from <a href="https://stackoverflow.com/a/20884556/8035065"> this answer</a>
 * and improved it.
 * <p>
 * Solved decimal separator problem.
 *
 * @see <a href="https://stackoverflow.com/q/3821539/8035065">
 * Decimal separator comma (',') with numberDecimal inputType in EditText
 * </a>
 * @see <a href="https://stackoverflow.com/a/14986892/8035065">
 * Android - Comma as decimal separator on Numeric Keyboard
 * </a>
 */
public class DecimalNumberInputFilter implements InputFilter {

    private static final String DECIMAL_SEPARATOR;
    private static final String SEPARATORS_TO_REPLACE = ", ., ,";

    static {
        DECIMAL_SEPARATOR = String.valueOf(NumberUtils.getDecimalSeparator());
    }

    /**
     * @return {@code null} to keep the original
     */
    @Override
    public CharSequence filter(CharSequence source, int start, int end,
                               Spanned dest, int dstart, int dend) {

        if (StringUtils.isTextEmpty(source)) {
            return null;
        }

        String before = dest.toString();
        String after = dest.toString().substring(0, dstart) +
                source.subSequence(start, end) +
                dest.toString().substring(dend);

        if (needReplaceToDecimalSeparator(source)) {
            if (before.contains(DECIMAL_SEPARATOR)) {
                return EMPTY;
            }
            return DECIMAL_SEPARATOR;
        }

        if (!NumberUtils.isTextABigDecimal(after)) {
            return EMPTY;
        }
        return null;
    }

    private boolean needReplaceToDecimalSeparator(CharSequence source) {
        return StringUtils.isTextNotEmpty(source) && SEPARATORS_TO_REPLACE.contains(source);
    }
}
