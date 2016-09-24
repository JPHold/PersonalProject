package com.hjp.customview;

import android.app.Activity;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;

import com.com.hjp.mobilesafe.customview.R;

import customview.SoundControlView;

public class MainActivity extends AppCompatActivity {
    private final static String TAG = "MainActivity";
    private AudioManager mAudioManager;
    private SoundControlView mSoundControlView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        mAudioManager = (AudioManager) getSystemService(Activity.AUDIO_SERVICE);
        mSoundControlView = (SoundControlView) findViewById(R.id.soundControlView);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        Log.i(TAG, "dispatchKeyEvent: ");
        //负责key事件分发，决定分发给本Activity还是其他。
        return super.dispatchKeyEvent(event);
    }

    private int currSoundVolume = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.i(TAG, "onKeyDown: ");
        switch (keyCode) {
            //返回true：交由本Activity处理key事件，就不会出现音量调节界面了
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if (currSoundVolume > 0) {

                    mSoundControlView.setCurrentVolume(--currSoundVolume);
                    Log.i(TAG, "onKeyDown: KEYCODE_VOLUME_DOWN" + currSoundVolume);

                }
                break;
            case KeyEvent.KEYCODE_VOLUME_UP:
                if (currSoundVolume < 10) {

                    mSoundControlView.setCurrentVolume(++currSoundVolume);
                    Log.i(TAG, "onKeyDown: KEYCODE_VOLUME_UP-" + currSoundVolume);
                }
                break;
        }


        return true;
    }
}
