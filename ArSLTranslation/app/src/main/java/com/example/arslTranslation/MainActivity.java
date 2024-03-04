package com.example.arslTranslation;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private Button btnRecord, btnUploadVid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnRecord = (Button) findViewById(R.id.btnRecord);
        btnUploadVid = (Button) findViewById(R.id.btnUploadVid);

        btnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // check for camera permissions; if not granted, request it
                if (ActivityCompat.checkSelfPermission(MainActivity.this,
                        android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{android.Manifest.permission.CAMERA}, 123);

                // otherwise, we can proceed
                else {
                    Intent recordGesture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(recordGesture, 0);
                }
            }
        });

        btnUploadVid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // check for external storage access permissions; if not granted, request it
                if (ActivityCompat.checkSelfPermission(MainActivity.this,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 456);

                // otherwise, we can proceed
                else {
                    Intent uploadVid = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(uploadVid , 1);
                }
            }
        });
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 123) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                btnRecord.callOnClick(); // if camera permission is granted, go back & execute the listener
            }
            else {
                Toast.makeText(MainActivity.this,
                        "You must grant camera access to continue", Toast.LENGTH_LONG).show();
                return;
            }
        }
        else if (requestCode == 456) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                btnRecord.callOnClick(); // if permission to access the photo gallery is granted, go back & execute the listener
            }
            else {
                Toast.makeText(MainActivity.this,
                        "You must grant access to your photo gallery to continue", Toast.LENGTH_LONG).show();
                return;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /* modify */
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case 0: // camera
                    if (resultCode == RESULT_OK && data != null) {
                        Bitmap selectedImage = (Bitmap) data.getExtras().get("data");
//                        imageView.setImageBitmap(selectedImage);
                        // CALL FUNCTION TO CALCULATE ATD
                        Toast.makeText(MainActivity.this, "Video Taken", Toast.LENGTH_LONG).show();
                    }
                    break;

                case 1: // gallery
                    if (resultCode == RESULT_OK && data != null) {
                        Uri selectedImage = data.getData();
                        String[] filePathColumn = { MediaStore.Images.Media.DATA };
                        if (selectedImage != null) {
                            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn,
                                    null, null, null);
                            if (cursor != null) {
                                cursor.moveToFirst();

                                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                                String picturePath = cursor.getString(columnIndex);
                                Bitmap bitmap = BitmapFactory.decodeFile(picturePath);
//                                imageView.setImageBitmap(bitmap);
                                Toast.makeText(MainActivity.this, "Video Uploaded", Toast.LENGTH_LONG).show();
                                cursor.close();
                            }
                        }
                    }
                    break;
            }
        }
    }
}