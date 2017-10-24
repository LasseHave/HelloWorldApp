package com.helloworld.golf.dk.helloworld;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private CameraManager camManager;
    private boolean flashOn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (this.getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
            setupFlashButton();
        }
    }

    private void setupFlashButton() {
        final Button flashBtn = (Button) findViewById(R.id.activity_main_flash_btn);
        camManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        flashBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    toggleFlash();
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void toggleFlash() throws CameraAccessException {
        flashOn = !flashOn;
        String cameraId = camManager.getCameraIdList()[0]; // Usually front camera is at 0 position.
        camManager.setTorchMode(cameraId, flashOn);
    }
}
