package com.rivastecnologia.graduei.controller.util;

import java.io.Serializable;

public class ImageItem implements Serializable{
    public final int drawableId;
    public final String imagePath;

    public ImageItem(int drawableId, String imagePath) {
        this.drawableId = drawableId;
        this.imagePath = imagePath;
    }
}