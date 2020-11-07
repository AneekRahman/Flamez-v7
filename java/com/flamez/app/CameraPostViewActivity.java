package com.flamez.app;

import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.VideoView;

public class CameraPostViewActivity extends AppCompatActivity {

    //---------------------------------------------------------------------------------------------

    VideoView videoPostViewVV;

    //---------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_post_view);

        //---------------------------------------------------------------------------------------------

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        //---------------------------------------------------------------------------------------------

        videoPostViewVV = (VideoView) findViewById(R.id.video_postview_VV);
        String path = bundle.getString("path");
        videoPostViewVV.setVideoPath(path);
        videoPostViewVV.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                videoPostViewVV.start();
            }
        });
        videoPostViewVV.start();


    }
    //---------------------------------------------------------------------------------------------



    //---------------------------------------------------------------------------------------------
}
