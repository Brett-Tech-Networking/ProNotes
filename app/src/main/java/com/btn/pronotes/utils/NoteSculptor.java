package com.btn.pronotes.utils;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.TypedValue;

import androidx.annotation.ColorInt;
import androidx.core.graphics.ColorUtils;

/**
 * Builds sculpted 3D note surfaces from a single base color —
 * glow, extrusion, and lit face all stay in the same hue family.
 */
public final class NoteSculptor {

    private NoteSculptor() {
    }

    public static float dp(Resources res, float value) {
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, value, res.getDisplayMetrics());
    }

    @ColorInt
    public static int lighten(@ColorInt int color, float amount) {
        return ColorUtils.blendARGB(color, Color.WHITE, clamp(amount));
    }

    @ColorInt
    public static int darken(@ColorInt int color, float amount) {
        return ColorUtils.blendARGB(color, Color.BLACK, clamp(amount));
    }

    @ColorInt
    public static int withAlpha(@ColorInt int color, int alpha) {
        return ColorUtils.setAlphaComponent(color, clampAlpha(alpha));
    }

    public static GradientDrawable glow(@ColorInt int base, float cornerPx) {
        GradientDrawable d = new GradientDrawable();
        d.setShape(GradientDrawable.RECTANGLE);
        d.setCornerRadius(cornerPx);
        d.setColor(withAlpha(base, 70));
        return d;
    }

    public static GradientDrawable extrude(@ColorInt int base, float cornerPx) {
        GradientDrawable d = new GradientDrawable();
        d.setShape(GradientDrawable.RECTANGLE);
        d.setCornerRadius(cornerPx);
        // Deep same-hue "side wall" of the note — not black
        d.setColor(darken(base, 0.38f));
        return d;
    }

    public static GradientDrawable face(@ColorInt int base, float cornerPx, float strokePx) {
        // Keep the base hue dominant — only a light top catch and soft bottom shade
        int highlight = lighten(base, 0.18f);
        int shade = darken(base, 0.08f);
        GradientDrawable d = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[]{highlight, base, base, shade}
        );
        d.setShape(GradientDrawable.RECTANGLE);
        d.setCornerRadius(cornerPx);
        d.setStroke(Math.max(1, Math.round(strokePx)), lighten(base, 0.28f));
        return d;
    }

    private static float clamp(float amount) {
        return Math.max(0f, Math.min(1f, amount));
    }

    private static int clampAlpha(int alpha) {
        return Math.max(0, Math.min(255, alpha));
    }
}
