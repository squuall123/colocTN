package tn.octave.coloc;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.esafirm.imagepicker.features.imageloader.ImageLoader;
import com.esafirm.imagepicker.features.imageloader.ImageType;

/**
 * Created by squall on 19/03/2018.
 */

class GrayscaleImageLoder implements ImageLoader {

    private static final RequestOptions REQUEST_OPTIONS = new RequestOptions().transform(new GrayscaleTransformation());

    @Override
    public void loadImage(String path, ImageView imageView, ImageType imageType) {
        Glide.with(imageView)
                .asBitmap()
                .load(path)
                .transition(BitmapTransitionOptions.withCrossFade())
                .apply(REQUEST_OPTIONS)
                .into(imageView);
    }

}


