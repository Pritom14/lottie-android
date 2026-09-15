package com.airbnb.lottie;

import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import java.util.HashMap;
import java.util.Map;

class FontAssetManager {
  private final MutablePair<String> tempPair = new MutablePair<>();

  /** Pair is (fontName, fontStyle) */
  private final Map<MutablePair<String>, Typeface> fontMap = new HashMap<>();
  /** Map of font families to their fonts. Necessary to create a font with a different style */
  private final Map<String, Typeface> fontFamilies = new HashMap<>();
  private final AssetManager assetManager;
  @Nullable private FontAssetDelegate delegate;
  private String defaultFontFileExtension = ".ttf";

  FontAssetManager(Drawable.Callback callback, @Nullable FontAssetDelegate delegate) {
    this.delegate = delegate;
    if (!(callback instanceof View)) {
      Log.w(L.TAG, "LottieDrawable must be inside of a view for images to work.");
      assetManager = null;
      return;
    }

    assetManager = ((View) callback).getContext().getAssets();
    Log.d(L.TAG, "FontAssetManager initialized with delegate=" + (delegate != null));
  }

  void setDelegate(@Nullable FontAssetDelegate assetDelegate) {
    this.delegate = assetDelegate;
  }

  /**
   * Sets the default file extension (include the `.`).
   *
   * e.g. `.ttf` `.otf`
   *
   * Defaults to `.ttf`
   */
  @SuppressWarnings("unused") public void setDefaultFontFileExtension(String defaultFontFileExtension) {
    this.defaultFontFileExtension = defaultFontFileExtension;
  }

  Typeface getTypeface(String fontFamily, String style) {
    tempPair.set(fontFamily, style);
    Typeface typeface = fontMap.get(tempPair);
    if (typeface != null) {
      Log.d(L.TAG, "Resolved typeface from cache for " + fontFamily + " " + style);
      return typeface;
    }
    Log.d(L.TAG, "Resolving typeface for " + fontFamily + " " + style);
    Typeface typefaceWithDefaultStyle = getFontFamily(fontFamily);
    typeface = typefaceForStyle(typefaceWithDefaultStyle, style);
    fontMap.put(tempPair, typeface);
    Log.d(L.TAG, "Registered typeface for " + fontFamily + " " + style);
    return typeface;
  }

  private Typeface getFontFamily(String fontFamily) {
    Typeface defaultTypeface = fontFamilies.get(fontFamily);
    if (defaultTypeface != null) {
      Log.d(L.TAG, "Found font family " + fontFamily + " in cache");
      return defaultTypeface;
    }

    Typeface typeface = null;
    if (delegate != null) {
      typeface = delegate.fetchFont(fontFamily);
      if (typeface != null) {
        Log.d(L.TAG, "Fetched typeface for " + fontFamily + " from delegate");
      }
    }

    if (delegate != null && typeface == null) {
      String path = delegate.getFontPath(fontFamily);
      if (path != null) {
        Log.d(L.TAG, "Loading font " + fontFamily + " from delegate path " + path);
        typeface = Typeface.createFromAsset(assetManager, path);
      }
    }

    if (typeface == null) {
      String path = "fonts/" + fontFamily + defaultFontFileExtension;
      Log.d(L.TAG, "Loading font " + fontFamily + " from assets path " + path);
      typeface = Typeface.createFromAsset(assetManager, path);
    }

    Log.d(L.TAG, "Registering typeface for font family " + fontFamily);
    fontFamilies.put(fontFamily, typeface);
    return typeface;
  }

  private Typeface typefaceForStyle(Typeface typeface, String style) {
    int styleInt = Typeface.NORMAL;
    boolean containsItalic = style.contains("Italic");
    boolean containsBold = style.contains("Bold");
    if (containsItalic && containsBold) {
      styleInt = Typeface.BOLD_ITALIC;
    } else if (containsItalic) {
      styleInt = Typeface.ITALIC;
    } else if (containsBold) {
      styleInt = Typeface.BOLD;
    }

    if (typeface.getStyle() == styleInt) {
      Log.d(L.TAG, "Typeface already matches style " + style);
      return typeface;
    }

    Log.d(L.TAG, "Creating typeface variant for style " + style);
    return Typeface.create(typeface, styleInt);
  }
}
