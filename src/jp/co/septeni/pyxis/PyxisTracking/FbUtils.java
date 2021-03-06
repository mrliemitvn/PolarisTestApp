package jp.co.septeni.pyxis.PyxisTracking;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

public class FbUtils {
	 public static final Uri ATTRIBUTION_ID_CONTENT_URI = Uri.parse("content://com.facebook.katana.provider.AttributionIdProvider");

	 public static final String ATTRIBUTION_ID_COLUMN_NAME = "aid";

	 protected static String getAttributionId(ContentResolver contentResolver) {

		String [] projection = {ATTRIBUTION_ID_COLUMN_NAME};
		Cursor c = contentResolver.query(ATTRIBUTION_ID_CONTENT_URI, projection, null, null, null);
		if (c == null || !c.moveToFirst()) {
			return null;
		}
		String attributionId = c.getString(c.getColumnIndex(ATTRIBUTION_ID_COLUMN_NAME));

		return attributionId;
	 }
}
