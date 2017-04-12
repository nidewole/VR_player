package com.asha.md360player4android;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.DrawableRes;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by hzqiujiadi on 16/1/26.
 * hzqiujiadi ashqalcn@gmail.com
 */
public class DemoActivity extends AppCompatActivity {
    private static final String TAG = DemoActivity.class.getSimpleName();
    private Button bt_Recard;
    private EditText et;


    /**
     * feature-1
     * display HelloWorld
     * @param savedInstanceState
     */

    /**
     * feature-2
     * display HelloWorld
     * @param savedInstanceState
     */

    /**
     * reature-3
     * display worldhello22222222222222222222222---------------
     * @param savedInstanceState
     */
    /**
     * reature-4
     * display worldhello22222222222222222222222---------------
     * @param savedInstanceState
     */

    @SuppressWarnings("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);

        et = (EditText) findViewById(R.id.edit_text_url);
        bt_Recard = (Button) findViewById(R.id.bt_recard);
        bt_Recard.setOnClickListener(new MyClick());


        SparseArray<String> data = new SparseArray<>();
//
//        data.put(data.size(), getDrawableUri(R.drawable.bitmap360).toString());
//        data.put(data.size(), getDrawableUri(R.drawable.texture).toString());
//        data.put(data.size(), getDrawableUri(R.drawable.dome_pic).toString());
//        data.put(data.size(), getDrawableUri(R.drawable.stereo).toString());
//        data.put(data.size(), getDrawableUri(R.drawable.multifisheye).toString());
//        data.put(data.size(), getDrawableUri(R.drawable.multifisheye2).toString());
//        data.put(data.size(), getDrawableUri(R.drawable.fish2sphere180s).toString());

//        data.put(data.size(), getDrawableUri(R.raw.h29).toString());

//        data.put(data.size(), "file:///mnt/sdcard/28.mp4");
//        data.put(data.size(), "file:///mnt/sdcard/29.mp4");
        data.put(data.size(), "file:///mnt/sdcard/Movies/hao.mp4");
        data.put(data.size(), "file:///mnt/sdcard/Movies/1.mp4");
        data.put(data.size(), "file:///mnt/sdcard/Movies/2.mp4");
        data.put(data.size(), "file:///mnt/sdcard/Movies/3.mp4");
        data.put(data.size(), "file:///mnt/sdcard/27.mp4");
        data.put(data.size(), "file:///mnt/sdcard/28.mp4");
//        data.put(data.size(), "http://cache.utovr.com/201508270528174780.m3u8");
//        data.put(data.size(), "http://pan.baidu.com/play/video#video/path=%2Fctvit%2F%E7%A9%BA%E4%B8%AD%E5%89%A7%E9%99%A2%2BAR.mp4&t=-1");
//        data.put(data.size(), "http://www.huajiao.com/l/15241266?reference=wx&userid=34534980&author=20391417&time=1464942993&version=3.6.6");
//        data.put(data.size(), "http://lehi.le.com/web/webLive?id=10089376");
//        data.put(data.size(), "http://www.le.com/ptv/vplay/25627934.html?shareplatform=1&sharechannel=1");
//        data.put(data.size(), "http://www.tudou.com/programs/view/JIyXmZbu6yQ/");

        SpinnerHelper.with(this)
                .setData(data)
                .setClickHandler(new SpinnerHelper.ClickHandler() {
                    @Override
                    public void onSpinnerClicked(int index, int key, String value) {
                        et.setText(value);
                    }
                })
                .init(R.id.spinner_url);

        findViewById(R.id.video_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = et.getText().toString();
                if (!TextUtils.isEmpty(url)){
                    MD360PlayerActivity.startVideo(DemoActivity.this, Uri.parse(url));
                } else {
                    Toast.makeText(DemoActivity.this, "empty url!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        findViewById(R.id.bitmap_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = et.getText().toString();
                if (!TextUtils.isEmpty(url)){
                    MD360PlayerActivity.startBitmap(DemoActivity.this, Uri.parse(url));
                } else {
                    Toast.makeText(DemoActivity.this, "empty url!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        findViewById(R.id.ijk_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = et.getText().toString();
                if (!TextUtils.isEmpty(url)){
                    IjkPlayerDemoActivity.start(DemoActivity.this, Uri.parse(url));
                } else {
                    Toast.makeText(DemoActivity.this, "empty url!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private Uri getDrawableUri(@DrawableRes int resId){
        Resources resources = getResources();
        return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + resources.getResourcePackageName(resId) + '/' + resources.getResourceTypeName(resId) + '/' + resources.getResourceEntryName(resId) );
    }


    class  MyClick implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("video/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(intent,1);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK) {//是否选择，没选择就不会继续
            Uri uri = data.getData();//得到uri，后面就是将uri转化成file的过程。
            String[] proj = {MediaStore.Images.Media.DATA};
            Cursor actualimagecursor = managedQuery(uri, proj, null, null, null);
            int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            actualimagecursor.moveToFirst();
            String img_path = actualimagecursor.getString(actual_image_column_index);
            et.setText(img_path);



            super.onActivityResult(requestCode, resultCode, data);

        }
    }


}
