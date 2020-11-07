package com.flamez.app;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;
import static android.provider.MediaStore.MediaColumns.SIZE;

public class CameraActivity extends AppCompatActivity {

    //---------------------------------------------------------------------------------------------

    private static final int SENSOR_ORIENTATION_DEFAULT_DEGREES = 90;
    private static final int SENSOR_ORIENTATION_INVERSE_DEGREES = 270;
    private static final SparseIntArray DEFAULT_ORIENTATIONS = new SparseIntArray();
    private static final SparseIntArray INVERSE_ORIENTATIONS = new SparseIntArray();
    static {
        DEFAULT_ORIENTATIONS.append(Surface.ROTATION_0, 90);
        DEFAULT_ORIENTATIONS.append(Surface.ROTATION_90, 0);
        DEFAULT_ORIENTATIONS.append(Surface.ROTATION_180, 270);
        DEFAULT_ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }
    static {
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_0, 270);
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_90, 180);
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_180, 90);
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_270, 0);
    }

    FrameLayout previewHolder;
    ImageButton captureBtn, switchCamBtn, backBtn, redoBtn, checkBtn;
    TextView errorView, modePhotoBtn, modeSlomoBtn, modeNormalBtn, modeLapseBtn, mode6SecBtn;
    Button grantPermBtn, saveBtn;
    ImageView imagePostViewIV;
    RelativeLayout postViewLayout, captureLayout;

    private Camera mCamera;
    private CameraPreview mPreview;
    private Camera.PictureCallback mPicture;
    MediaRecorder mMediaRecorder;
    boolean cameraFront;
    boolean isRecording = false;
    String mediaFileNameTemp;
    boolean videoMode = false;
    int mCameraId;
    byte[] mCurrentBitmapData;
    boolean imageSaved = false;

    private String APP_NAME;

    //---------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        // ---------------------------------------------------------------------------------------------

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        //---------------------------------------------------------------------------------------------

        previewHolder = (FrameLayout) findViewById(R.id.camera_preview);
        captureBtn = (ImageButton) findViewById(R.id.capture_btn);
        errorView = (TextView) findViewById(R.id.cam_error_view);
        grantPermBtn = (Button) findViewById(R.id.grant_permission_btn);
        imagePostViewIV = (ImageView) findViewById(R.id.image_postview_IV);
        modePhotoBtn = (TextView) findViewById(R.id.mode_photo_btn);
        modeSlomoBtn = (TextView) findViewById(R.id.mode_slomo_btn);
        modeNormalBtn = (TextView) findViewById(R.id.mode_normal_btn);
        modeLapseBtn = (TextView) findViewById(R.id.mode_lapse_btn);
        mode6SecBtn = (TextView) findViewById(R.id.mode_6sec_btn);
        switchCamBtn = (ImageButton) findViewById(R.id.switch_cam_btn);
        postViewLayout = (RelativeLayout) findViewById(R.id.postview_layout);
        captureLayout = (RelativeLayout) findViewById(R.id.capture_screen_layout);
        backBtn = (ImageButton) findViewById(R.id.back_btn);
        redoBtn = (ImageButton) findViewById(R.id.redo_btn);
        checkBtn = (ImageButton) findViewById(R.id.check_btn);
        saveBtn = (Button) findViewById(R.id.save_btn);

        APP_NAME = getString(R.string.app_name);

        //---------------------------------------------------------------------------------------------

        checkPermsAndSetupCam();

        //---------------------------------------------------------------------------------------------

        grantPermBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String[] permissionRequests = {
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.RECORD_AUDIO
                };
                int GRANTED = PackageManager.PERMISSION_GRANTED;

                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != GRANTED
                        || ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != GRANTED
                        || ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO) != GRANTED) {

                    errorView.setText("Camera, microphone and storage permission not granted");
                    ActivityCompat.requestPermissions(CameraActivity.this, permissionRequests, 2);

                }else{

                    checkPermsAndSetupCam();

                }

            }
        });

        captureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(videoMode){
                    startStopMediaRecording();
                }else{
                    mCamera.takePicture(null, null, mPicture);
                }

            }
        });

        switchCamBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                toggleCamera();

            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();

            }
        });


        redoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                stopImagePostView();

            }
        });

        checkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startImageContinue();

            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!imageSaved){
                    saveImage(mCurrentBitmapData);
                }else{
                    Toast.makeText(getApplicationContext(), "Image already got saved once", Toast.LENGTH_SHORT).show();
                }

            }
        });

        //---------------------------------------------------------------------------------------------


        modePhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectUnselectPhotoBtn(true);
                selectUnselectSlomoBtn(false);
                selectUnselectNormalBtn(false);
                selectUnselectLapseBtn(false);
                selectUnselect6SecBtn(false);
            }
        });
        modeSlomoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectUnselectPhotoBtn(false);
                selectUnselectSlomoBtn(true);
                selectUnselectNormalBtn(false);
                selectUnselectLapseBtn(false);
                selectUnselect6SecBtn(false);
            }
        });
        modeNormalBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectUnselectPhotoBtn(false);
                selectUnselectSlomoBtn(false);
                selectUnselectNormalBtn(true);
                selectUnselectLapseBtn(false);
                selectUnselect6SecBtn(false);
            }
        });
        modeLapseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectUnselectPhotoBtn(false);
                selectUnselectSlomoBtn(false);
                selectUnselectNormalBtn(false);
                selectUnselectLapseBtn(true);
                selectUnselect6SecBtn(false);
            }
        });
        mode6SecBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectUnselectPhotoBtn(false);
                selectUnselectSlomoBtn(false);
                selectUnselectNormalBtn(false);
                selectUnselectLapseBtn(false);
                selectUnselect6SecBtn(true);
            }
        });


        //---------------------------------------------------------------------------------------------

    }

    //---------------------------------------------------------------------------------------------

    // Camera Mode Selection UI

    public void selectUnselectPhotoBtn(boolean selected){
        if(selected){
            modePhotoBtn.setTextColor(Color.parseColor("#000000"));
            modePhotoBtn.setBackgroundResource(R.drawable.cam_mode_left_selected);
        }else{
            modePhotoBtn.setTextColor(Color.parseColor("#ffffff"));
            modePhotoBtn.setBackgroundResource(R.drawable.cam_mode_left_unselected);
        }
    }
    public void selectUnselectSlomoBtn(boolean selected){
        if(selected){
            modeSlomoBtn.setTextColor(Color.parseColor("#000000"));
            modeSlomoBtn.setBackgroundResource(R.drawable.cam_mode_middle_selected);
        }else{
            modeSlomoBtn.setTextColor(Color.parseColor("#ffffff"));
            modeSlomoBtn.setBackgroundResource(R.drawable.cam_mode_middle_unselected);
        }
    }
    public void selectUnselectNormalBtn(boolean selected){
        if(selected){
            modeNormalBtn.setTextColor(Color.parseColor("#000000"));
            modeNormalBtn.setBackgroundResource(R.drawable.cam_mode_middle_selected);
        }else{
            modeNormalBtn.setTextColor(Color.parseColor("#ffffff"));
            modeNormalBtn.setBackgroundResource(R.drawable.cam_mode_middle_unselected);
        }
    }
    public void selectUnselectLapseBtn(boolean selected){
        if(selected){
            modeLapseBtn.setTextColor(Color.parseColor("#000000"));
            modeLapseBtn.setBackgroundResource(R.drawable.cam_mode_middle_selected);
        }else{
            modeLapseBtn.setTextColor(Color.parseColor("#ffffff"));
            modeLapseBtn.setBackgroundResource(R.drawable.cam_mode_middle_unselected);
        }
    }
    public void selectUnselect6SecBtn(boolean selected){
        if(selected){
            mode6SecBtn.setTextColor(Color.parseColor("#000000"));
            mode6SecBtn.setBackgroundResource(R.drawable.cam_mode_right_selected);
        }else{
            mode6SecBtn.setTextColor(Color.parseColor("#ffffff"));
            mode6SecBtn.setBackgroundResource(R.drawable.cam_mode_right_unselected);
        }
    }

    // ------------------------------  ALL CAMERA INITIALIZATION STUFF BELOW ---------------------------------

    private void initializeCam(){

        errorView.setVisibility(View.GONE);
        grantPermBtn.setVisibility(View.GONE);
        captureLayout.setVisibility(View.VISIBLE);

        if(mCamera != null) return;

        int frontCamId = findFrontFacingCamera();
        if( frontCamId> 0){
            mCamera = Camera.open(frontCamId);
            cameraFront = true;
            mCameraId = frontCamId;
        }else{
            mCamera = Camera.open(findBackFacingCamera());
            cameraFront = false;
            mCameraId = findBackFacingCamera();
        }

        mPicture = getPictureCallback();
        specUpCamera(mCamera);
        mPreview = new CameraPreview(getApplicationContext(), mCamera);
        previewHolder.addView(mPreview);

    }

    public void specUpCamera(Camera rawCam){
        // My Specs
        rawCam.setDisplayOrientation(90);
        Camera.Parameters params = rawCam.getParameters();
        List<String> focusModes = params.getSupportedFocusModes();

        if (focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
            params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
        }
        if(cameraFront){
            params.setRotation(getMediaRecorderOrientation());
        }else{
            params.setRotation(getMediaRecorderOrientation());
        }

        List<Camera.Size> sizes = params.getSupportedPictureSizes();
        int w = 0, h = 0;
        for (Camera.Size size : sizes) {
            if (size.width > w || size.height > h) {
                w = size.width;
                h = size.height;
            }
        }
        params.setPictureSize(w, h);

        params.setRecordingHint(true);
        params.setJpegQuality(100);
        rawCam.setParameters(params);
    }


    private int findFrontFacingCamera() {
        int cameraId = -1;
        // Search for the front facing camera
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                cameraId = i;
                break;
            }
        }
        return cameraId;
    }

    private int findBackFacingCamera() {
        int cameraId = -1;
        //Search for the back facing camera
        //get the number of cameras
        int numberOfCameras = Camera.getNumberOfCameras();
        //for every camera check
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                cameraId = i;
                break;
            }
        }
        return cameraId;
    }

    public void toggleCamera() {
        if (cameraFront) {
            int cameraId = findBackFacingCamera();
            if (cameraId >= 0) {
                //open the backFacingCamera
                cameraFront = false;
                mCameraId = findBackFacingCamera();
                releaseCameraAndPreview();
                mCamera = Camera.open(cameraId);
                specUpCamera(mCamera);
                mPicture = getPictureCallback();
                mPreview.refreshCamera(mCamera);
                switchCamBtn.setImageResource(R.drawable.ic_phone_front_rotate_icon);
                if(mediaFileNameTemp != null) startVideoPostView();
            }
        } else {
            int cameraId = findFrontFacingCamera();
            if (cameraId >= 0) {
                //open the FrontFacingCamera
                cameraFront = true;
                mCameraId = findFrontFacingCamera();
                releaseCameraAndPreview();
                mCamera = Camera.open(cameraId);
                specUpCamera(mCamera);
                mPicture = getPictureCallback();
                mPreview.refreshCamera(mCamera);
                switchCamBtn.setImageResource(R.drawable.ic_phone_rear_rotate_icon);
                if(mediaFileNameTemp != null) startVideoPostView();
            }
        }
    }

    private File getOutputMediaFile(int MEDIA_TYPE) {

        if(Environment.getExternalStorageState() == Environment.MEDIA_UNMOUNTED){
            Toast.makeText(getApplicationContext(), "Cannot access storage", Toast.LENGTH_SHORT).show();
            return null;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyymmddhhmmss");
        String date = dateFormat.format(new Date());

        if(MEDIA_TYPE == MEDIA_TYPE_IMAGE){

            File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), APP_NAME);

            if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
                Log.d("CameraActivity", "Can't create directory to save image.");
                Toast.makeText(this, "Can't create directory to save image.", Toast.LENGTH_LONG).show();
            }
            String photoFile = "IMG_"  + date + ".jpg";

            String filename = mediaStorageDir.getPath() + File.separator + photoFile;
            File pictureFile = new File(filename);

            return pictureFile;

        }else if(MEDIA_TYPE == MEDIA_TYPE_VIDEO){

            File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES), APP_NAME);

            if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
                Log.d("CameraActivity", "Can't create directory to save videos.");
                Toast.makeText(this, "Can't create directory to save videos.", Toast.LENGTH_LONG).show();
            }
            String videoFile = "VID_"  + date + ".mp4";

            String filename = mediaStorageDir.getPath() + File.separator + videoFile;
            mediaFileNameTemp = filename; // To access it from video preview function

            File movieFile = new File(filename);

            return movieFile;

        }else return null;

    }

    public void checkPermsAndSetupCam(){
        /* Check if this device has a camera */

        if (hasCamera()){
                //  Device has a camera

            String[] permissionRequests = {
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.RECORD_AUDIO
            };
            int GRANTED = PackageManager.PERMISSION_GRANTED;

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != GRANTED
                    || ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != GRANTED
                    || ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO) != GRANTED) {

                errorView.setText("Camera, microphone and storage permission not granted");
                ActivityCompat.requestPermissions(CameraActivity.this, permissionRequests, 1);

            }else{
                initializeCam();
            }

        } else {
            // No camera on this device
            errorView.setText("Cannot locate a camera on this device");
        }
    }

    private void releaseCameraAndPreview() {
        releaseMediaRecorder();
        if (mPreview !=null) mPreview.setCamera(null);
        if(mCurrentBitmapData != null) mCurrentBitmapData = null;
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

    public boolean hasCamera(){

        return this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);

    }


    //------------------------------  ALL CAMERA INITIALIZATION STUFF ENDS HERE  -----------------------------------------


    //------------------------------  ALL VIDEO CAPTURE STUFF BELOW  -----------------------------------------


    private boolean prepareVideoRecorder(){

        mMediaRecorder = new MediaRecorder();
        specUpCamera(mCamera);

        mCamera.unlock();
        mMediaRecorder.setCamera(mCamera);

        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        CamcorderProfile camcorderProfile =  CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
        mMediaRecorder.setProfile(camcorderProfile);

        mMediaRecorder.setOutputFile(getOutputMediaFile(MEDIA_TYPE_VIDEO).toString());

        mMediaRecorder.setMaxDuration(60000); // 6 Seconds
        mMediaRecorder.setMaxFileSize(50000000); // 50 MB
        mMediaRecorder.setOrientationHint(getMediaRecorderOrientation());

        mMediaRecorder.setPreviewDisplay(mPreview.getHolder().getSurface());

        try {
            mMediaRecorder.prepare();
        } catch (IllegalStateException e) {
            Log.d("CAMERA_ACTIVITY", "IllegalStateException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        } catch (IOException e) {
            Log.d("CAMERA_ACTIVITY", "IOException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        }
        if(mCurrentBitmapData != null) mCurrentBitmapData = null;
        return true;
    }

    private void releaseMediaRecorder(){
        if(isRecording)mMediaRecorder.stop();
        if (mMediaRecorder != null) {
            mMediaRecorder.reset();   // clear recorder configuration
            mMediaRecorder.release(); // release the recorder object
            mMediaRecorder = null;
            mCamera.lock();           // lock camera for later use
            isRecording = false;
        }
    }

    private void startStopMediaRecording(){

        if (isRecording) {
            // stop recording and release camera
            isRecording = false;
            mMediaRecorder.stop();
            releaseMediaRecorder();
            mCamera.lock();
            startVideoPostView();
            // Alert the gallery
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            File f = new File(mediaFileNameTemp);
            Uri contentUri = Uri.fromFile(f);
            mediaScanIntent.setData(contentUri);
            this.sendBroadcast(mediaScanIntent);
            mediaFileNameTemp = null;

        } else {
            // initialize video camera
            if (prepareVideoRecorder()) {

                mMediaRecorder.start();
                isRecording = true;

            } else {
                // prepare didn't work, release the camera
                releaseMediaRecorder();
            }
        }

    }

    public void startVideoPostView(){

        Intent intent = new Intent(getApplicationContext(), CameraPostViewActivity.class);
        intent.putExtra("path", mediaFileNameTemp);
        startActivity(intent);


    }

    public int getMediaRecorderOrientation(){

        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        CameraCharacteristics characteristics = null;
        try {
            characteristics = manager.getCameraCharacteristics(manager.getCameraIdList()[mCameraId]);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        int mSensorOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
        int finalRotation = 0;
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        switch (mSensorOrientation) {
            case SENSOR_ORIENTATION_DEFAULT_DEGREES:
                finalRotation = DEFAULT_ORIENTATIONS.get(rotation);
                break;
            case SENSOR_ORIENTATION_INVERSE_DEGREES:
                finalRotation = INVERSE_ORIENTATIONS.get(rotation);
                break;
        }

        return finalRotation;

    }

    //------------------------------  ALL VIDEO CAPTURE STUFF ENDS HERE  -----------------------------------------


    //------------------------------  ALL IMAGE CAPTURE STUFF BELOW  -----------------------------------------

    public void startImageContinue(){

        Intent intent = new Intent(getApplicationContext(), CaptureContinueActivity.class);
        intent.putExtra("bitmapByteData", mCurrentBitmapData);
        intent.putExtra("typeVideo", true);
        startActivity(intent);

    }

    public void startImagePostView(Bitmap bitmap){

        postViewLayout.setVisibility(View.VISIBLE);
        captureLayout.setVisibility(View.GONE);
        imagePostViewIV.setImageBitmap(bitmap);

        mCamera.stopPreview();

    }

    public void stopImagePostView(){

        captureLayout.setVisibility(View.VISIBLE);
        postViewLayout.setVisibility(View.GONE);
        mCurrentBitmapData = null;
        mPreview.refreshCamera(mCamera);
        imageSaved = false;

    }

    private Camera.PictureCallback getPictureCallback() {
        final Camera.PictureCallback picture = new Camera.PictureCallback() {

            @Override
            public void onPictureTaken(byte[] data, Camera camera) {

            //  Bitmap of image taken
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            mCurrentBitmapData = data;
            startImagePostView(bitmap);


            }
        };
        return picture;
    }

    public void saveImage(byte[] data){
        //make a new picture file
        File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
        if (pictureFile == null) {
            return;
        }
        try {
            // Write the file to EXStorage
            FileOutputStream fos = new FileOutputStream(pictureFile);
            fos.write(data);
            fos.close();
            // Send it to gallery viewing
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri contentUri = Uri.fromFile(pictureFile);
            mediaScanIntent.setData(contentUri);
            sendBroadcast(mediaScanIntent);

            Toast.makeText(getApplicationContext(), "Image saved!", Toast.LENGTH_SHORT).show();
            imageSaved = true;

        } catch (FileNotFoundException e) {e.printStackTrace();} catch (IOException e) {e.printStackTrace();}

    }


    //--------------------------------  ALL IMAGE CAPTURE STUFF ENDS HERE  -------------------------------------------



    //---------------------------------------------------------------------------------------------


    @Override
    protected void onDestroy() {
        super.onDestroy();

        releaseMediaRecorder();
        releaseCameraAndPreview();

    }

    @Override
    protected void onPause() {
        super.onPause();
        mCamera.stopPreview();

    }

    @Override
    protected void onResume() {
        super.onResume();
        mPreview.refreshCamera(mCamera);
    }

    //---------------------------------------------------------------------------------------------

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 2 : {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    checkPermsAndSetupCam();
                }
            }
            break;
        }
    }


        //---------------------------------------------------------------------------------------------

}
