package greenbharat.cdac.com.greenbharat.helper;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.Toast;

import com.soundcloud.android.crop.Crop;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import greenbharat.cdac.com.greenbharat.activity.qrcode.QRCodeActivity;

import static android.app.Activity.RESULT_OK;

/**
 * Created by CDAC on 3/17/2017.
 * author :- Nitesh Sharma
 * <p>
 * <p>
 * Two things are mendatory to do before using this class
 * <p>
 * 1. compile 'com.soundcloud.android:android-crop:1.0.1@aar'
 * <p>
 * 2. register activity in manifest file
 * <activity android:name="com.soundcloud.android.crop.CropImageActivity" />
 * <p>
 * 3. add permission in manifest
 * <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
 * <p>
 * 4. SelectImageHelper helper = new SelectImageHelper(this, imageView);  // imageView pass your imageview here
 * <p>
 * 5. override following methods -
 *
 * @Override protected void onActivityResult(int requestCode, int resultCode, Intent result) {
 * helper.handleResult(requestCode, resultCode, result);  // call this helper class method
 * }
 * @Override public void onRequestPermissionsResult(final int requestCode, final @NonNull String[] permissions, final @NonNull int[] grantResults) {
 * helper.handleGrantedPermisson(requestCode, grantResults);   // call this helper class method
 * }
 * <p>
 * 6. use  helper.getURI_FOR_SELECTED_IMAGE() method to get Uri of selected image.
 */

public class SelectImageHelper {
    private static final int REQUEST_PERMISSION_REQ_CODE = 34;
    private static int requestedPermissionType = -1; //0 for camera and 1 for storage
    private final int CAMERA_CODE = 101;
    private Uri mImageCaptureUri, URI_FOR_SELECTED_IMAGE = null;
    private boolean isImageSelected = false;
    private ImageView imageView;
    private Activity context;
    private boolean isCropEnabled;
    private boolean isSqureCropEnabled;

    // false => show crop true otherwise

    public SelectImageHelper(Activity context, ImageView imageView, boolean isCropEnabled) {
        this.context = context;
        this.imageView = imageView;
        this.isCropEnabled = isCropEnabled;
    }

    private static boolean checkPermissionForStorage(Activity context) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_REQ_CODE);
            return false;
        } else
            return true;
    }

    private static boolean checkPermissionForCamera(Activity context) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.CAMERA}, REQUEST_PERMISSION_REQ_CODE);
            return false;
        } else
            return true;
    }

    public void setSqureCropEnabled(boolean squreCropEnabled) {
        isSqureCropEnabled = squreCropEnabled;
    }

    public void selectImageOption() {
        final CharSequence[] items = {"Capture Photo", "Choose from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (items[item].equals("Capture Photo")) {
                    requestedPermissionType = 0;
                    if (checkPermissionForCamera(context)) {
                        if (checkPermissionForStorage(context))
                            fireCameraIntent();
                    }
                } else if (items[item].equals("Choose from Gallery")) {
                    requestedPermissionType = 1;
                    if (checkPermissionForStorage(context))
                        Crop.pickImage(context);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    public void selectImageCameraOption() {
        final CharSequence[] items = {"Capture Photo", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Capture Photo")) {
                    if (checkPermissionForCamera(context))
                        if (checkPermissionForStorage(context))
                            fireCameraIntent();
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void fireCameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File f = new File(Environment.getExternalStorageDirectory(), "temp1.jpg");
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            mImageCaptureUri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", createImageFile());
        } else
            mImageCaptureUri = Uri.fromFile(f);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
        context.startActivityForResult(intent, CAMERA_CODE);
    }

    private File createImageFile() {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM), "Camera");
        File image = null;
        try {
            image = File.createTempFile(
                    "temp1",  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    private void beginCrop(Uri source) {
        if (!isCropEnabled) {
            Uri destination = Uri.fromFile(new File(context.getCacheDir(), "cropped"));
            if (!isSqureCropEnabled)
                Crop.of(source, destination).withMaxSize(500, 500).start(context);
            else
                Crop.of(source, destination).withAspect(1, 1).withMaxSize(500, 500).asSquare().start(context);
        } else
            noCrop(source);
    }

    private void noCrop(Uri source) {
        URI_FOR_SELECTED_IMAGE = source;
        imageView.setImageDrawable(null);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            imageView.setImageURI(URI_FOR_SELECTED_IMAGE);
            Helper.downloadImageNoCache(context, URI_FOR_SELECTED_IMAGE.toString(), imageView);

        } else {
                Helper.downloadImageNoCache(context, URI_FOR_SELECTED_IMAGE.toString(), imageView);
/*
            Bitmap bitmap = bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), URI_FOR_SELECTED_IMAGE);
            String path = URI_FOR_SELECTED_IMAGE.getPath();
            imageView.setImageBitmap(rotateBitmap(bitmap, path));*/
        }
        isImageSelected = true;
        if (context instanceof QRCodeActivity) {
            QRCodeActivity activity = (QRCodeActivity) context;
            activity.uploadPlantImage();
        }
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            URI_FOR_SELECTED_IMAGE = Crop.getOutput(result);
            imageView.setImageDrawable(null);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                imageView.setImageURI(URI_FOR_SELECTED_IMAGE);
                Helper.downloadImageNoCache(context, URI_FOR_SELECTED_IMAGE.toString(), imageView);
            } else {


                Helper.downloadImageNoCache(context, URI_FOR_SELECTED_IMAGE.toString(), imageView);

                /*try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), URI_FOR_SELECTED_IMAGE);
                    String path = URI_FOR_SELECTED_IMAGE.getPath();
                    imageView.setImageBitmap(rotateBitmap(bitmap, path));
                } catch (IOException e) {
                    e.printStackTrace();
                }*/
            }
            isImageSelected = true;

           /* if (isSqureCropEnabled) {
                *//*Intent intente1 = new Intent(NotificationCodes.QRCODENOTI);
                intente1.putExtra("url", "upload");
                intente1.putExtra("message", "{\"result\":1,\"otp\":\"otp sent successfully\"}");
                LocalBroadcastManager.getInstance(context).sendBroadcast(intente1);*//*

                Log.d("1234", "handleCrop: " + (context instanceof QRCodeActivity));

                if (context instanceof QRCodeActivity) {
                    QRCodeActivity activity = (QRCodeActivity) context;
                    activity.uploadPlantImage();
                }
            }*/

        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(context, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Rotate a bitmap based on orientation metadata.
     * src - image path
     */
    public Bitmap rotateBitmap(Bitmap bitmap, String path) {
        try {
            ExifInterface exif = new ExifInterface(path);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            Matrix matrix = new Matrix();
            switch (orientation) {
                case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                    matrix.setScale(-1, 1);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    matrix.setRotate(180);
                    break;
                case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                    matrix.setRotate(180);
                    matrix.postScale(-1, 1);
                    break;
                case ExifInterface.ORIENTATION_TRANSPOSE:
                    matrix.setRotate(90);
                    matrix.postScale(-1, 1);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    matrix.setRotate(90);
                    break;
                case ExifInterface.ORIENTATION_TRANSVERSE:
                    matrix.setRotate(-90);
                    matrix.postScale(-1, 1);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    matrix.setRotate(-90);
                    break;
                case ExifInterface.ORIENTATION_NORMAL:
                case ExifInterface.ORIENTATION_UNDEFINED:
                default:
                    return bitmap;
            }

            try {
                Bitmap oriented = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                return oriented;
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
                return bitmap;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public void handleResult(int requestCode, int resultCode, Intent result) {
        if (requestCode == Crop.REQUEST_PICK && resultCode == RESULT_OK) {
            beginCrop(result.getData());
        } else if (requestCode == Crop.REQUEST_CROP) {
            handleCrop(resultCode, result);
        } else if (requestCode == CAMERA_CODE && resultCode == Activity.RESULT_OK) {
            beginCrop(mImageCaptureUri);
        }
    }

    public void handleGrantedPermisson(int requestCode, int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION_REQ_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (requestedPermissionType == 0)
                    if (checkPermissionForCamera(context))
                        if (checkPermissionForStorage(context))
                            fireCameraIntent();
                if (requestedPermissionType == 1) {
                    Crop.pickImage(context);
                }
            } else {
                Toast.makeText(context, "Permission denied.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public Uri getURI_FOR_SELECTED_IMAGE() {
        return URI_FOR_SELECTED_IMAGE;
    }

    public boolean isImageSelected() {
        return isImageSelected;
    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }
}
