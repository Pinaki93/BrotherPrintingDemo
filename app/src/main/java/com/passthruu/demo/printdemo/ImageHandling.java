package com.passthruu.demo.printdemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;

/**
 * Created by mojojojo on 12/02/2016.
 */
public class ImageHandling {

    private static final String TAG = "ImageHandling";

    public static Bitmap loadBitmapFromDrawable(Context context, int path, int reqHeight, int reqWidth)
    {

        BitmapFactory.Options options=new BitmapFactory.Options();
        options.inJustDecodeBounds=true;


        BitmapFactory.decodeResource(context.getResources(),path,options);

        int height=options.outHeight;
        int width=options.outWidth;

        options.inSampleSize=calculateSampleSize(height,width,reqHeight,reqWidth);
        options.inJustDecodeBounds=false;

        Bitmap bitmap=BitmapFactory.decodeResource(context.getResources(),path,options);
        bitmap=Bitmap.createScaledBitmap(bitmap,reqWidth,reqHeight,false);
        return bitmap;
    }

    public static Bitmap loadBitmapFromFile(Context context,String path,int reqHeight,int reqWidth)
    {
        BitmapFactory.Options options=new BitmapFactory.Options();
        options.inJustDecodeBounds=true;

        BitmapFactory.decodeFile(path,options);
        int height=options.outHeight;
        int width=options.outWidth;

        options.inSampleSize=calculateSampleSize(height,width,reqHeight,reqWidth);
        Log.d(TAG, "loadBitmapFromFile: in sample size:"+options.inSampleSize);
        options.inJustDecodeBounds=false;
        Bitmap bitmap=BitmapFactory.decodeFile(path,options);
        bitmap=resize(bitmap,720,1280);
        return bitmap;
    }


    private static int calculateSampleSize(int height, int width, int reqHeight, int reqWidth)
    {
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
            // This offers some additional logic in case the image has a strange
            // aspect ratio. For example, a panorama may have a much larger
            // width than height. In these cases the total pixels might still
            // end up being too large to fit comfortably in memory, so we should
            // be more aggressive with sample down the image (=larger inSampleSize).

            long totalPixels = width * height / inSampleSize;

            // Anything more than 2x the requested pixels we'll sample down further
            final long totalReqPixelsCap = reqWidth * reqHeight * 2;

            while (totalPixels > totalReqPixelsCap) {
                inSampleSize *= 2;
                totalPixels /= 2;
            }
        }
        return inSampleSize;
        // END_INCLUDE (calculate_sample_size)
    }

    public static String convertBitmapToString(Bitmap bitmap)
    {

        byte[] array=convertBitmapToByteArray(bitmap);
        String response= Base64.encodeToString(array, Base64.DEFAULT);
        return response;
    }


    public static Bitmap convertStringToBitmap(String string)
    {
        try {
            byte[] array=Base64.decode(string,Base64.DEFAULT);
            Bitmap bitmap=BitmapFactory.decodeByteArray(array,0,array.length);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static byte[] convertBitmapToByteArray(Bitmap bitmap){
        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,90,baos);
        return baos.toByteArray();
    }

    public static byte[] convertStringToByteArray(String string){
        byte[] array=Base64.decode(string,Base64.DEFAULT);
        return array;
    }


    public static Bitmap resize(Bitmap image, int maxWidth, int maxHeight) {

        if (maxHeight > 0 && maxWidth > 0) {
            int width = image.getWidth();
            int height = image.getHeight();
            float ratioBitmap = (float) width / (float) height;
            float ratioMax = (float) maxWidth / (float) maxHeight;

            int finalWidth = maxWidth;
            int finalHeight = maxHeight;
            if (ratioMax > 1) {
                finalWidth = (int) ((float)maxHeight * ratioBitmap);
            } else {
                finalHeight = (int) ((float)maxWidth / ratioBitmap);
            }
            image = Bitmap.createScaledBitmap(image, finalWidth, finalHeight, true);
            return image;
        } else {
            return image;
        }
    }

}
