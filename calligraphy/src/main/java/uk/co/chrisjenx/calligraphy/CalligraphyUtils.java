package uk.co.chrisjenx.calligraphy;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.TextView;

import static uk.co.chrisjenx.calligraphy.ReflectionUtils.getStaticFieldValue;

/**
 * Created by chris on 20/12/2013
 * Project: Calligraphy
 */
public final class CalligraphyUtils {

    static final int[] R_Styleable_TextView;
    static final int[] R_Styleable_TextAppearance;
    static final int R_Styleable_TextView_textAppearance;
    static final int R_Styleable_TextAppearance_fontFamily;

    static {
        Class<?> styleableClass = ReflectionUtils.getClass("com.android.internal.R$styleable");
        if (styleableClass != null) {
            R_Styleable_TextView = getStaticFieldValue(styleableClass, "TextView", null);
            R_Styleable_TextAppearance = getStaticFieldValue(styleableClass, "TextAppearance", null);
            R_Styleable_TextView_textAppearance = getStaticFieldValue(styleableClass, "TextView_textAppearance", -1);
            R_Styleable_TextAppearance_fontFamily = getStaticFieldValue(styleableClass, "TextAppearance_fontFamily", -1);
        } else {
            R_Styleable_TextView = null;
            R_Styleable_TextAppearance = null;
            R_Styleable_TextView_textAppearance = -1;
            R_Styleable_TextAppearance_fontFamily = -1;
        }
    }

    /**
     * Applies a custom typeface span to the text.
     *
     * @param s        text to apply it too.
     * @param typeface typeface to apply.
     * @return Either the passed in Object or new Spannable with the typeface span applied.
     */
    public static CharSequence applyTypefaceSpan(CharSequence s, Typeface typeface) {
        if (s != null && s.length() > 0) {
            if (!(s instanceof Spannable)) {
                s = new SpannableString(s);
            }
            ((Spannable) s).setSpan(TypefaceUtils.getSpan(typeface), 0, s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return s;
    }

    /**
     * Applies a Typeface to a TextView, its recommend you don't call this multiple times, as this
     * adds a TextWatcher.
     *
     * @param textView Not null, TextView or child of.
     * @param typeface Not null, Typeface to apply to the TextView.
     * @return true if applied otherwise false.
     */
    public static boolean applyFontToTextView(final TextView textView, final Typeface typeface) {
        if (textView == null || typeface == null) return false;
        textView.setPaintFlags(textView.getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);
        textView.setText(applyTypefaceSpan(textView.getText(), typeface), TextView.BufferType.SPANNABLE);
        textView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                applyTypefaceSpan(s, typeface);
            }
        });
        return true;
    }

    public static boolean applyFontToTextView(final Context context, final TextView textView, final String filePath) {
        if (textView == null || context == null) return false;
        final AssetManager assetManager = context.getAssets();
        final Typeface typeface = TypefaceUtils.load(assetManager, filePath);
        return applyFontToTextView(textView, typeface);
    }

    public static void applyFontToTextView(final Context context, final TextView textView, final CalligraphyConfig config) {
        if (context == null || textView == null || config == null) return;
        if (!config.isFontSet()) return;
        applyFontToTextView(context, textView, config.getFontPath());
    }

    public static void applyFontToTextView(final Context context, final TextView textView, final CalligraphyConfig config, final String textViewFont) {
        if (context == null || textView == null || config == null) return;
        if (!TextUtils.isEmpty(textViewFont) && applyFontToTextView(context, textView, textViewFont)) {
            return;
        }
        applyFontToTextView(context, textView, config);
    }

    static String pullFontPath(Context context, AttributeSet attrs, int attributeId) {
        String attributeName;
        try {
            attributeName = context.getResources().getResourceEntryName(attributeId);
        } catch (Resources.NotFoundException e) {
            // invalid attribute ID
            return null;
        }
        final int stringResourceId = attrs.getAttributeResourceValue(null, attributeName, -1);
        return stringResourceId > 0
                ? context.getString(stringResourceId)
                : attrs.getAttributeValue(null, attributeName);
    }

    static String pullFontPathFromStyle(Context context, AttributeSet attrs, int attributeId) {
        final TypedArray typedArray = context.obtainStyledAttributes(attrs, new int[]{attributeId});
        if (typedArray != null) {
            try {
                // First defined attribute
                String fontFromAttribute = typedArray.getString(0);
                if (!TextUtils.isEmpty(fontFromAttribute)) {
                    return fontFromAttribute;
                }
            } catch (Exception ignore) {
                // Failed for some reason.
            } finally {
                typedArray.recycle();
            }
        }
        return pullFontPathFromTextAppearance(context, attrs, attributeId);
    }

    static String pullFontPathFromTextAppearance(final Context context, AttributeSet attrs, int attributeId) {
        boolean usingFontFamily = attributeId == android.R.attr.fontFamily;

        if (R_Styleable_TextView == null
                || R_Styleable_TextAppearance == null
                || R_Styleable_TextView_textAppearance == -1
                || (usingFontFamily && attributeId == -1)) {
            return null;
        }

        TypedArray textViewAttrs = context.obtainStyledAttributes(attrs, R_Styleable_TextView);
        if (textViewAttrs == null) {
            return null;
        }

        int textAppearanceId = textViewAttrs.getResourceId(R_Styleable_TextView_textAppearance, -1);
        TypedArray textAppearanceAttrs = null;
        try {
            if (usingFontFamily) {
                textAppearanceAttrs = context.obtainStyledAttributes(textAppearanceId, R_Styleable_TextAppearance);
                if (textAppearanceAttrs == null) {
                    return null;
                }
                return textAppearanceAttrs.getString(R_Styleable_TextAppearance_fontFamily);
            } else {
                textAppearanceAttrs = context.obtainStyledAttributes(textAppearanceId, new int[]{attributeId});
                return textAppearanceAttrs.getString(0);
            }
        } finally {
            if (textAppearanceAttrs != null) {
                textAppearanceAttrs.recycle();
            }
        }
    }

    static String pullFontPathFromTheme(Context context, int styleId, int attributeId) {
        final Resources.Theme theme = context.getTheme();
        final TypedValue value = new TypedValue();

        theme.resolveAttribute(styleId, value, true);
        final TypedArray typedArray = theme.obtainStyledAttributes(value.resourceId, new int[]{attributeId});
        try {
            return typedArray.getString(0);
        } catch (Exception ignore) {
            // Failed for some reason.
            return null;
        } finally {
            typedArray.recycle();
        }
    }

    private CalligraphyUtils() {
    }

}
