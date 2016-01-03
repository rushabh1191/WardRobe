package com.crowdfire.wardrobe;

/**
 * Created by rushabh on 01/01/16.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


/**
 * Created by rushabh on 11/11/14.
 */
public class ImageChooserDialog implements AdapterView.OnItemClickListener, OnAlertDialogButtonClickListener {

    public static final int CAPTUE_IMAGE = 0;

    public static final int ACTION_CHOOSE_IMAGE = 1;
    private Context context;

    private PopupWindowAlert popupWindowAlert;
    private Fragment fragment;
    private Activity activity;
    private ImageURICapturedListener imageURICapturedListener;


    int clothType;

    public ImageChooserDialog(Activity activity, ImageURICapturedListener imageURICapture, int clothType) {

        this.activity = activity;
        imageURICapturedListener = imageURICapture;
        this.clothType = clothType;

        showPopup(activity, imageURICapture);
    }


    private void showPopup(Context context, ImageURICapturedListener imageURICapturedListener) {
        ListView lv = new ListView(context);

        ArrayList<String> list = new ArrayList<>();

        list.add("Camera");
        list.add("Gallery");

        String title;
        if(clothType==ClothInformation.IS_PANT){
            title="Add Pant";
        }
        else {
            title="Add Shirt";
        }
        popupWindowAlert = new PopupWindowAlert(context, title, lv, "", "", "", this, 1);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, list);

        lv.setAdapter(adapter);

        lv.setOnItemClickListener(this);
        popupWindowAlert.setCancelable(true);
        /*Intent i = new Intent(context, ApartmentAddaImageGallery.class);




        if(fragment!=null) {
            fragment.startActivityForResult(i, ACTION_CHOOSE_IMAGE);
        }
        else if(activity!=null)
        {
            activity.startActivityForResult(i, ACTION_CHOOSE_IMAGE);
        }*/

    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        if (position == 0) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);


            File file = createImageFile();

            Uri uri = Uri.fromFile(file);
            intent.putExtra(MediaStore.EXTRA_OUTPUT,
                    uri);
            imageURICapturedListener.uriCaptured(uri, file.getAbsolutePath(), clothType);


            activity.startActivityForResult(intent, clothType);
        } else {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
// Start the Intent
            activity.startActivityForResult(galleryIntent, clothType*3);
        }
        popupWindowAlert.dismiss();
/*
//            Intent i = new Intent(context, ApartmentAddaImageGallery.class);
            i.setType("image");
            i.setAction(Intent.ACTION_GET_CONTENT);

//            i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
//            i.putExtra(ApartmentAddaImageGallery.NUMBER_OF_IMAGES,numberOfImages);


            if(fragment!=null) {
                fragment.startActivityForResult(i.createChooser(i,
                        "Select Picture"), ACTION_CHOOSE_IMAGE);
            }
            else if(activity!=null)
            {
                activity.startActivityForResult(i.createChooser(i,
                        "Select Picture"), ACTION_CHOOSE_IMAGE);
            }
        }
*/

//        popupWindowAlert.dismiss();;

    }

    public static File createImageFile() {
        // Create an image file name

        try {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "WardRobe" + timeStamp + "_";
            File storageDir = new File(Environment.getExternalStorageDirectory() + "/WardRobe");
            if (!storageDir.exists()) {
                storageDir.mkdir();
            }

            File image = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );

            // Save a file: path for use with ACTION_VIEW intents
            //  mCurrentPhotoPath = image.getAbsolutePath();
            return image;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onButtonClick(AlertDialog dialog, View view, int buttonId, int popupId) {

    }

    public interface ImageURICapturedListener {
        public void uriCaptured(Uri uri, String file, int type);
    }


}

