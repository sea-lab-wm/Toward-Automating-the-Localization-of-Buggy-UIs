package io.github.zwieback.familyfinance.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Arrays;
import java.util.List;

import static io.github.zwieback.familyfinance.util.StringUtils.EMPTY;
import static io.github.zwieback.familyfinance.util.StringUtils.isTextEmpty;

public final class TransliterationUtils {

    private static final List<Character> CYRILLIC_ALPHABET = Arrays.asList('а', 'б', 'в', 'г', 'д',
            'е', 'ё', 'ж', 'з', 'и', 'й', 'к', 'л', 'м', 'н', 'о', 'п', 'р', 'с', 'т', 'у', 'ф',
            'х', 'ц', 'ч', 'ш', 'щ', 'ъ', 'ы', 'ь', 'э', 'ю', 'я', 'А', 'Б', 'В', 'Г',
            'Д', 'Е', 'Ё', 'Ж', 'З', 'И', 'Й', 'К', 'Л', 'М', 'Н', 'О', 'П', 'Р', 'С',
            'Т', 'У', 'Ф', 'Х', 'Ц', 'Ч', 'Ш', 'Щ', 'Ъ', 'Ы', 'Ь', 'Э', 'Ю', 'Я');

    /**
     * As standard used International Civil Aviation Organization (ICAO)
     * <br/>
     * Doc 9303
     * <br/>
     * <a href="https://www.icao.int/publications/Documents/9303_p3_cons_ru.pdf">doc</a>
     */
    private static final List<String> LATIN_ALPHABET = Arrays.asList("a", "b", "v", "g", "d",
            "e", "e", "zh", "z", "i", "i", "k", "l", "m", "n", "o", "p", "r", "s", "t", "u", "f",
            "kh", "ts", "ch", "sh", "shch", "ie", "y", "", "e", "iu", "ia", "A", "B", "V", "G",
            "D", "E", "E", "ZH", "Z", "I", "I", "K", "L", "M", "N", "O", "P", "R", "S",
            "T", "U", "F", "KH", "TS", "CH", "SH", "SHCH", "IE", "Y", "", "E", "IU", "IA");

    /**
     * Transliterate all cyrillic characters into latin analog.
     *
     * @param text source text
     * @return transliterated text
     */
    @NonNull
    public static String transliterate(@Nullable String text) {
        if (isTextEmpty(text)) {
            return EMPTY;
        }
        StringBuilder builder = new StringBuilder(text.length());
        for (char character : text.toCharArray()) {
            builder.append(transliterate(character));
        }
        return builder.toString();
    }

    private TransliterationUtils() {
    }

    private static String transliterate(char character) {
        int cyrillicIndex = CYRILLIC_ALPHABET.indexOf(character);
        if (cyrillicIndex > -1) {
            return LATIN_ALPHABET.get(cyrillicIndex);
        }
        return String.valueOf(character);
    }
}
