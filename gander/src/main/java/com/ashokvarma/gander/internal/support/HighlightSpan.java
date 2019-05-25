package com.ashokvarma.gander.internal.support;

import android.text.TextPaint;
import android.text.style.CharacterStyle;
import android.text.style.UpdateAppearance;
import androidx.annotation.ColorInt;

/**
 * Class description :
 * This Span won't survive the inter-process / intent
 * Since this class can't be accessed by other process the framework doesn't allow apps to implement ParcelableSpan
 * So, Only framework ParcelableSpans survive passing via the Intent.
 *
 * @author ashok
 * @version 1.0
 * @since 08/06/18
 */
public class HighlightSpan extends CharacterStyle implements UpdateAppearance {
    private final int mBackgroundColor;
    private final int mTextColor;
    private final boolean mUnderLineText;
    private final boolean mApplyBackgroundColor;
    private final boolean mApplyTextColor;

    HighlightSpan(int backgroundColor, @ColorInt int textColor, boolean underLineText) {
        super();
        this.mBackgroundColor = backgroundColor;
        this.mTextColor = textColor;
        this.mUnderLineText = underLineText;
        this.mApplyBackgroundColor = backgroundColor != 0;
        this.mApplyTextColor = textColor != 0;
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        if (mApplyTextColor)
            ds.setColor(mTextColor);
        if (mApplyBackgroundColor)
            ds.bgColor = mBackgroundColor;
        ds.setUnderlineText(mUnderLineText);
    }

}
