package com.asha.md360player4android;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Surface;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.asha.vrlib.MDVRLibrary;
import com.asha.vrlib.model.BarrelDistortionConfig;

import tv.danmaku.ijk.media.player.IMediaPlayer;


/**
 * Created by hzqiujiadi on 16/4/5.
 * hzqiujiadi ashqalcn@gmail.com
 */
public class VideoPlayerActivity extends MD360PlayerActivity {

    private MediaPlayerWrapper mMediaPlayerWrapper = new MediaPlayerWrapper();
    private Button btn;
    private Button startPause;
    private int isPause;
    private MyRecever recever;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMediaPlayerWrapper.init();
        mMediaPlayerWrapper.setPreparedListener(new IMediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(IMediaPlayer mp) {
                cancelBusy();
            }
        });

        mMediaPlayerWrapper.getPlayer().setOnErrorListener(new IMediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(IMediaPlayer mp, int what, int extra) {
                String error = String.format("Play Error what=%d extra=%d",what,extra);
                Toast.makeText(VideoPlayerActivity.this, error, Toast.LENGTH_SHORT).show();
                endVisibility();
                return true;
            }
        });

        mMediaPlayerWrapper.getPlayer().setOnVideoSizeChangedListener(new IMediaPlayer.OnVideoSizeChangedListener() {
            @Override
            public void onVideoSizeChanged(IMediaPlayer mp, int width, int height, int sar_num, int sar_den) {
                getVRLibrary().onTextureResize(width, height);
            }
        });

        mMediaPlayerWrapper.getPlayer().setOnCompletionListener(new IMediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(IMediaPlayer iMediaPlayer) {
                mMediaPlayerWrapper.openRemoteFile("file:///mnt/sdcard/29.mp4");
                mMediaPlayerWrapper.onPrepared(iMediaPlayer);
            }
        });

        Uri uri = getUri();
        if (uri != null){
            mMediaPlayerWrapper.openRemoteFile(uri.toString());
            mMediaPlayerWrapper.prepare();
        }


        startPause = (Button) findViewById(R.id.start_pause);
        startPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPause != 1){
                    startPause.setText("paly");
                    mMediaPlayerWrapper.onPause();
                    isPause = 1;
                }else {
                    startPause.setText("pause");
                    mMediaPlayerWrapper.onResume();
                    isPause = 0;
                }
//                mMediaPlayerWrapper.pause();
//                mMediaPlayerWrapper.destroy();
//                mMediaPlayerWrapper.init();
//                mMediaPlayerWrapper.openRemoteFile(DemoActivity.sPath + "video_31b451b7ca49710719b19d22e19d9e60.mp4");
//                mMediaPlayerWrapper.prepare();
            }
        });

        registBroadcastRececver(VideoPlayerActivity.this);

    }

    private void registBroadcastRececver(Activity activity) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_1);
        recever = new MyRecever();
        activity.registerReceiver(recever, filter);
    }

    @Override
    protected MDVRLibrary createVRLibrary() {
        return MDVRLibrary.with(this)
                .displayMode(MDVRLibrary.DISPLAY_MODE_NORMAL)
                .interactiveMode(MDVRLibrary.INTERACTIVE_MODE_MOTION)
                .asVideo(new MDVRLibrary.IOnSurfaceReadyCallback() {
                    @Override
                    public void onSurfaceReady(Surface surface) {
                        mMediaPlayerWrapper.getPlayer().setSurface(surface);
                    }
                })
                .ifNotSupport(new MDVRLibrary.INotSupportCallback() {
                    @Override
                    public void onNotSupport(int mode) {
                        String tip = mode == MDVRLibrary.INTERACTIVE_MODE_MOTION
                                ? "onNotSupport:MOTION" : "onNotSupport:" + String.valueOf(mode);
                        Toast.makeText(VideoPlayerActivity.this, tip, Toast.LENGTH_SHORT).show();
                    }
                })
                .pinchEnabled(true)
                .barrelDistortionConfig(new BarrelDistortionConfig().setDefaultEnabled(false).setScale(0.50f))
                .build(R.id.gl_view);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMediaPlayerWrapper.onDestroy();
        VideoPlayerActivity.this.unregisterReceiver(recever);
    }

    @Override
    protected void onPause() {
        mMediaPlayerWrapper.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMediaPlayerWrapper.onResume();
    }

    public MediaPlayerWrapper getmMediaPlayerWrapper() {
        return mMediaPlayerWrapper;
    }




    private static final String ACTION_1 = "rececycer_client_switch_stream";
    private static final String RELATIONDATA = "data";
    String stringExtra = null;
    class MyRecever extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            stringExtra = intent.getStringExtra(RELATIONDATA);
            if (!TextUtils.isEmpty(stringExtra) && stringExtra.length() <= 5) {
                mMediaPlayerWrapper.stop();
                new Handler().postDelayed(myrun, 500);
            }

        }

        Runnable myrun = new Runnable(){
            @Override
            public void run() {
                if (Integer.parseInt(stringExtra) % 2 ==0) {
                    MD360PlayerActivity.startVideo(VideoPlayerActivity.this , Uri.parse("file:///mnt/sdcard/28.mp4"));
                    finish();
                }else {
                    MD360PlayerActivity.startVideo(VideoPlayerActivity.this , Uri.parse("file:///mnt/sdcard/29.mp4"));
                    finish();
                }

            }
        };
    }


}
