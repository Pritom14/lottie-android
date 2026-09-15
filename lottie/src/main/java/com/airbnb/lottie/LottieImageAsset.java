package com.airbnb.lottie;

import android.util.Log;

import org.json.JSONObject;

/**
 * Data class describing an image asset exported by bodymovin.
 */
@SuppressWarnings("WeakerAccess")
public class LottieImageAsset {
  private static final String TAG = L.TAG;

  private final int width;
  private final int height;
  private final String id;
  private final String fileName;

  private LottieImageAsset(int width, int height, String id, String fileName) {
    this.width = width;
    this.height = height;
    this.id = id;
    this.fileName = fileName;
    if (L.DBG) {
      Log.d(TAG, "Bound image asset " + id + " to resource " + fileName + " (" + width + "x" + height + ")");
    }
  }

  static class Factory {
    private Factory() {
    }

    static LottieImageAsset newInstance(JSONObject imageJson) {
      int width = imageJson.optInt("w");
      int height = imageJson.optInt("h");
      String id = imageJson.optString("id");
      String fileName = imageJson.optString("p");
      if (L.DBG) {
        Log.d(TAG, "Loading image asset " + id + " (" + fileName + ") " + width + "x" + height);
      }
      return new LottieImageAsset(width, height, id, fileName);
    }
  }

  @SuppressWarnings("WeakerAccess") public int getWidth() {
    return width;
  }

  @SuppressWarnings("WeakerAccess")public int getHeight() {
    return height;
  }

  public String getId() {
    return id;
  }

  public String getFileName() {
    if (L.DBG) {
      Log.d(TAG, "Resolving bitmap cache key for image asset " + id + " -> " + fileName);
    }
    return fileName;
  }
}
