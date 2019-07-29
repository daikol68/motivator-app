package de.daikol.acclaim.tasks;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import de.daikol.acclaim.model.Bitmapable;
import de.daikol.acclaim.service.BitmapConverter;

public class ConvertBitmapTask extends AsyncTask<Void, Void, Bitmap> {

    private Context context;

    private ImageView view;

    private Bitmapable bitmapable;

    public ConvertBitmapTask(Context context, ImageView view, Bitmapable bitmapable) {
        this.context = context;
        this.view = view;
        this.bitmapable = bitmapable;
    }

    protected Bitmap doInBackground(Void... nothing) {
        if (bitmapable != null && bitmapable.getPicture() != null) {
            if (bitmapable.getBitmap() != null) {
                return bitmapable.getBitmap();
            }
            return BitmapConverter.convertBitmap(bitmapable.getPicture(), this.context);
        }
        return null;
    }

    protected void onPostExecute(Bitmap result) {
        if (result != null) {
            bitmapable.setBitmap(result);

            Glide.with(context)
                    .load(result)
                    .into(view);
        }
    }

}
