package com.scanner.demo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.droidninja.imageeditengine.ImageEditor;
import com.scanlibrary.RealPathUtils;
import com.scanlibrary.ScanActivity;
import com.scanlibrary.ScanConstants;
import com.scanlibrary.Utils;

import java.io.File;
import java.io.IOException;

import devliving.online.cvscanner.CVScanner;
import devliving.online.cvscanner.util.Util;


public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 99;
    final int REQ_SCAN = 11;
    private Button scanButton;
    private Button cameraButton;
    private Button mediaButton;
    private ImageView scannedImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        scanButton = (Button) findViewById(R.id.scanButton);
        scanButton.setOnClickListener(new ScanButtonClickListener());
        cameraButton = (Button) findViewById(R.id.cameraButton);
        cameraButton.setOnClickListener(new ScanButtonClickListener(ScanConstants.OPEN_CAMERA));
        mediaButton = (Button) findViewById(R.id.mediaButton);
        mediaButton.setOnClickListener(new ScanButtonClickListener(ScanConstants.OPEN_MEDIA));
        scannedImageView = (ImageView) findViewById(R.id.scannedImage);
    }

    private class ScanButtonClickListener implements View.OnClickListener {

        private int preference;

        public ScanButtonClickListener(int preference) {
            this.preference = preference;
        }

        public ScanButtonClickListener() {
        }

        @Override
        public void onClick(View v) {
            startScan(preference);
        }
    }

    protected void startScan(int preference) {
//        Intent intent = new Intent(this, ScanActivity.class);
//        intent.putExtra(ScanConstants.OPEN_INTENT_PREFERENCE, preference);
//        startActivityForResult(intent, REQUEST_CODE);
        startScannerIntent(true);
    }

    void startScannerIntent(boolean isPassport) {
        CVScanner.startScanner(this, isPassport, REQ_SCAN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getExtras().getParcelable(ScanConstants.SCANNED_RESULT);
            String filePath = RealPathUtils.getPath(this, uri);
            new ImageEditor.Builder(this, filePath)
                    .open();
        }
        if (requestCode == ImageEditor.RC_IMAGE_EDITOR && resultCode == Activity.RESULT_OK && data != null) {
            String imagePath = data.getStringExtra(ImageEditor.EXTRA_EDITED_PATH);
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            scannedImageView.setImageBitmap(bitmap);
            final Uri uri = Utils.getUri(this, bitmap);
//            Uri uri = Uri.fromFile(new File(imagePath));
            Log.e("URI", uri.toString());
//            Bitmap bitmap = null;
//            try {
//                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
//                getContentResolver().delete(uri, null, null);
//                scannedImageView.setImageBitmap(bitmap);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }

        }

        if(resultCode == RESULT_OK){
            switch (requestCode) {
                case REQ_SCAN:
                    Log.d("MAIN", "got intent data");
                    if(data != null && data.getExtras() != null){
                        String path = data.getStringExtra(CVScanner.RESULT_IMAGE_PATH);
                        File file = new File(path);
                        Uri imageUri = Util.getUriForFile(this, file);
                        Log.d("MAIN", "added: " + imageUri);
                        CVScanner.startManualCropper(this, imageUri, 300);
//                        new ImageEditor.Builder(this, path)
//                                .open();
                    }
                    break;
            }
        }
    }

    private Bitmap convertByteArrayToBitmap(byte[] data) {
        return BitmapFactory.decodeByteArray(data, 0, data.length);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
