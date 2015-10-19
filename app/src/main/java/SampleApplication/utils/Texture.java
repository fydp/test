/*===============================================================================
Copyright (c) 2012-2014 Qualcomm Connected Experiences, Inc. All Rights Reserved.

Vuforia is a trademark of QUALCOMM Incorporated, registered in the United States 
and other countries. Trademarks of QUALCOMM Incorporated are used with permission.
===============================================================================*/

package com.qualcomm.vuforia.samples.SampleApplication.utils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;

import models.Point;


// Support class for the Vuforia samples applications.
// Exposes functionality for loading a texture from the APK.
public class Texture
{
    private static final String LOGTAG = "Vuforia_Texture";
    
    public int mWidth;          // The width of the texture.
    public int mHeight;         // The height of the texture.
    public int mChannels;       // The number of channels.
    public ByteBuffer mData;    // The pixel data.
    public int[] mTextureID = new int[1];
    public boolean mSuccess = false;

    static int width = 317; // x = 247.0
    static int height = 341; // y = 173.0
    static double xScale = width/ 247.0;
    static double yScale = width / 173.0;
    static int[] data = new int[width * height];
    static Bitmap bitmap;
    static Canvas canvas;

    /* Factory function to load a texture from the APK. */
    public static Texture loadTextureFromApk(String fileName,
        AssetManager assets)
    {
        for(int i = 0; i < data.length; i++) {
            data[i] = Color.argb(10, 255, 255, 255);
        }

        Bitmap b = Bitmap.createBitmap(data, width, height, Bitmap.Config.ARGB_8888);
        bitmap = b.copy(Bitmap.Config.ARGB_8888, true);
        canvas = new Canvas(bitmap);
        return loadTextureFromIntBuffer(data, width, height);
    }

    public static Path down(int x, int y) {
        x *= xScale;
        y *= yScale;
        Path p = new Path();
        p.moveTo(x, y);
        return p;
    }

    public static Texture move(Path p, int x, int y) {
        x *= xScale;
        y *= yScale;

        p.lineTo(x, y);
        return getTexture(p);
    }

    public static Texture getTexture(Path p) {
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(5);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawPath(p, paint);
        bitmap.getPixels(data, 0, width, 0, 0, width, height);
        return loadTextureFromIntBuffer(data, width, height);
    }

    public static Texture loadPath(List<Point> points) {
        if (points.isEmpty()) {
            return null;
        }
        Path p = new Path();

        p.moveTo(points.get(0).getPoint().x, points.get(0).getPoint().y);

        for (int i = 1; i < points.size(); i++) {
            android.graphics.Point point = points.get(i).getPoint();
            p.lineTo(point.x, point.y);
        }
        return getTexture(p);
    }

    public static Texture fillPixel(int x, int y) {
        x *= xScale;
        y *= yScale;

        for (int i = -5; i < 5; i++) {
            for (int j = -5; j < 5; j++) {
                int index = ((int)(5* xScale)  + i) + (width *((int)(5* yScale) +j));
                if (index < data.length && index >= 0)
                    data[index] = Color.BLUE;
            }
        }
        for (int i = -5; i < 5; i++) {
            for (int j = -5; j < 5; j++) {
                int index = ((int)(242*xScale)  + i) + ( width *((int)(168*yScale) +j));
                if (index < data.length && index >= 0)
                    data[index] = Color.GREEN;
            }
        }


        for (int i = -5; i < 5; i++) {
            for (int j = -5; j < 5; j++) {
                int index = (x  + i) + ( width *(y +j));
                if (index < data.length && index >= 0)
                    data[index] = Color.RED;
            }
        }

        return loadTextureFromIntBuffer(data, width, height);
    }

    public static int randomValue() {
        return (int)(Math.random()*255) + 1;
    }
    
    
    public static Texture loadTextureFromIntBuffer(int[] data, int width,
        int height)
    {
        // Convert:
        int numPixels = width * height;
        byte[] dataBytes = new byte[numPixels * 4];
        
        for (int p = 0; p < numPixels; ++p)
        {
            int colour = data[p];
            dataBytes[p * 4] = (byte) (colour >>> 16); // R
            dataBytes[p * 4 + 1] = (byte) (colour >>> 8); // G
            dataBytes[p * 4 + 2] = (byte) colour; // B
            dataBytes[p * 4 + 3] = (byte) (colour >>> 24); // A
        }
        
        Texture texture = new Texture();
        texture.mWidth = width;
        texture.mHeight = height;
        texture.mChannels = 4;
        
        texture.mData = ByteBuffer.allocateDirect(dataBytes.length).order(
            ByteOrder.nativeOrder());
        int rowSize = texture.mWidth * texture.mChannels;
        for (int r = 0; r < texture.mHeight; r++)
            texture.mData.put(dataBytes, rowSize * (texture.mHeight - 1 - r),
                rowSize);
        
        texture.mData.rewind();
        
        // Cleans variables
        dataBytes = null;
        data = null;
        
        texture.mSuccess = true;
        return texture;
    }
}
