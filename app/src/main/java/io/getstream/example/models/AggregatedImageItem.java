package io.getstream.example.models;

import android.graphics.Bitmap;

public class AggregatedImageItem {
    private Bitmap image;

    public AggregatedImageItem(Bitmap image) {
        super();
        this.image = image;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }
}