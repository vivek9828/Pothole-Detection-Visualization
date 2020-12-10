package com.example.pothole_Project;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.ImageReader;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.TextureView;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.pothole_Project.R;

import java.io.File;
import java.util.Arrays;

public class CameraActivity extends AppCompatActivity {

        TextureView textureView;

        private static final SparseIntArray ORIENTATIONS = new SparseIntArray();

        static {
            ORIENTATIONS.append(Surface.ROTATION_0,90);
            ORIENTATIONS.append(Surface.ROTATION_90,0);
            ORIENTATIONS.append(Surface.ROTATION_180,270);
            ORIENTATIONS.append(Surface.ROTATION_270,180);
        }


        private String cameraId;
        CameraDevice cameraDevice;
        CameraCaptureSession cameraCaptureSession;
        CaptureRequest captureRequest;
        CaptureRequest.Builder captureRequestBuilder;

        private Size imageDimensions;
        private ImageReader imageReader;
        private File file;
        Handler mBackgroundHandler;
        HandlerThread mBackgroundThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //HideStatus Bar
        if (Build.VERSION.SDK_INT >= 21) {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        setContentView(R.layout.activity_camera);

        textureView = (TextureView)findViewById(R.id.texture);

        textureView.setSurfaceTextureListener(textureListener);


    }

    @Override
    public void onRequestPermissionsResult(int requestCode , @NonNull String[] permissions , @NonNull int[] grantResults) {
        if (requestCode == 101){
            if (grantResults[0] == PackageManager.PERMISSION_DENIED)
            {
                Toast.makeText(getApplicationContext(),"Camera permission is necessary",Toast.LENGTH_LONG).show();
//                finish();
            }
        }
    }

    TextureView.SurfaceTextureListener textureListener = new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface , int width , int height) {
                try {
                    openCamera();
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface , int width , int height) {

            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {

            }
        };

    private final CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            cameraDevice = camera;
            try {
                createCameraPreview();
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
                cameraDevice.close();
        }

        @Override
        public void onError(@NonNull CameraDevice camera , int error) {
                    cameraDevice.close();
                    cameraDevice = null;
        }
    };

    private void createCameraPreview() throws CameraAccessException {
        SurfaceTexture texture = textureView.getSurfaceTexture();
        texture.setDefaultBufferSize(imageDimensions.getWidth() , imageDimensions.getHeight());
        Surface surface = new Surface(texture);

        captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
        captureRequestBuilder.addTarget(surface);

        cameraDevice.createCaptureSession(Arrays.asList(surface) , new CameraCaptureSession.StateCallback() {
            @Override
            public void onConfigured(@NonNull CameraCaptureSession session) {
                    if (cameraDevice == null) {
                        return;
                    }
                    cameraCaptureSession = session;
                try {
                    updatePreview();
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                Toast.makeText(getApplicationContext(),"Configration Changed",Toast.LENGTH_LONG).show();
            }

        } , null);
    }

    private void updatePreview() throws CameraAccessException {
        if (cameraDevice == null){
            return;
        }
        captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);

        cameraCaptureSession.setRepeatingRequest(captureRequestBuilder.build(), null, mBackgroundHandler);
    }


    private void openCamera() throws CameraAccessException {
        CameraManager manager = (CameraManager)getSystemService(Context.CAMERA_SERVICE);

        cameraId = manager.getCameraIdList()[0];

        CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);

        StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);

        imageDimensions = map.getOutputSizes(SurfaceTexture.class)[0];

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(CameraActivity.this,new String[] {Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);
            return;
        }

        manager.openCamera(cameraId, stateCallback, null);
    }

    private void takePicture() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        statBackgroundThread();

        if (textureView.isAvailable())
        {
            try{
                openCamera();
            }catch (CameraAccessException e){
                e.printStackTrace();
            }
        }
        else
        {
            textureView.setSurfaceTextureListener(textureListener);
        }
    }

    private void statBackgroundThread() {
        mBackgroundThread = new HandlerThread("Camera Background");
        mBackgroundThread.start();

    }

    @Override
    protected void onPause() {
        try {
            stopBackgroundThread();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        super.onPause();
    }

    private void stopBackgroundThread() throws InterruptedException {
        mBackgroundThread.quitSafely();
        mBackgroundThread.join();
        mBackgroundThread= null;
        mBackgroundHandler = null;

    }

}
