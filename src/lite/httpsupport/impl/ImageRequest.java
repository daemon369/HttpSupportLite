package lite.httpsupport.impl;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;

public class ImageRequest extends Request<Bitmap> {
    private final Config config;

    public ImageRequest(String url, Config decodeConfig) {
        super(url);

        this.config = decodeConfig;
    }

    @Override
    protected Bitmap parseResponse(byte[] data) throws Exception {
        final BitmapFactory.Options decodeOptions = new BitmapFactory.Options();
        decodeOptions.inPreferredConfig = config;

        Bitmap bitmap = null;

        try {
            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length,
                    decodeOptions);
        } catch (OutOfMemoryError err) {
            throw new Exception("parse image encounter OutOfMemoryError:"
                    + getUrl(), err);
        }

        return bitmap;
    }
}
