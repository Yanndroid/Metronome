package de.dlyt.yanndroid.metronome.utils;

import android.text.InputFilter;
import android.text.Spanned;

public class InputFilterMinMax implements InputFilter {
    private final int max;
    private final int min;

    public InputFilterMinMax(int i, int i2) {
        this.min = i;
        this.max = i2;
    }

    private boolean isInRange(int i, int i2, int i3) {
        if (i2 > i) {
            return i3 >= i && i3 <= i2;
        } else return i3 >= i2 && i3 <= i;
    }

    public CharSequence filter(CharSequence charSequence, int i, int i2, Spanned spanned, int i3, int i4) {
        try {
            String str = spanned.toString().substring(0, i3) + spanned.toString().substring(i4);
            if (isInRange(this.min, this.max, Integer.parseInt(str.substring(0, i3) + charSequence.toString() + str.substring(i3)))) {
                return null;
            }
            return "";
        } catch (NumberFormatException unused) {
            return "";
        }
    }
}