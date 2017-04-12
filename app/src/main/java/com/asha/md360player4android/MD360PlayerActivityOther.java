package com.asha.md360player4android;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.SparseArray;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.asha.vrlib.MDVRLibrary;
import com.asha.vrlib.model.MDPosition;
import com.asha.vrlib.plugins.IMDHotspot;
import com.asha.vrlib.plugins.MDAbsPlugin;
import com.asha.vrlib.plugins.MDSimplePlugin;
import com.asha.vrlib.texture.MD360BitmapTexture;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

/**
 * using MD360Renderer
 *
 * Created by hzqiujiadi on 16/1/22.
 * hzqiujiadi ashqalcn@gmail.com
 */
public abstract class MD360PlayerActivityOther extends Activity {

    private TextView editText_1;
    private EditText editText_2;
    private ServerSocket serverSocket = null;
    StringBuffer stringBuffer = new StringBuffer();
    private static final String ACTION = "rececycer_client_relation";
    private static final String RELATIONDATA = "data";
    private InputStream inputStream;

    public Handler handler = new Handler(){

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what){
                case 1:
                    editText_1.setText(msg.obj.toString());
                    Toast.makeText(MD360PlayerActivityOther.this,"获取到IP地址和端口号..."+msg.obj.toString(),Toast.LENGTH_SHORT).show();
                    break;
                case 2:
    	            Toast.makeText(MD360PlayerActivityOther.this,"收到客服的发送过来数据..."+msg.obj.toString(),Toast.LENGTH_SHORT).show();
                    String s = msg.obj.toString();
                    editText_2.setText(s);

                    Intent intent = new Intent();
                    intent.setAction(ACTION);
                    intent.putExtra(RELATIONDATA, s);
                    sendBroadcast(intent);

                    break;

            }

        }
    };
    private BufferedReader bufferedReader;

    //以上是新增内容为了接收方向数据



    private static final String TAG = "MD360PlayerActivity";

    private static final SparseArray<String> sDisplayMode = new SparseArray<>();
    private static final SparseArray<String> sInteractiveMode = new SparseArray<>();
    private static final SparseArray<String> sProjectionMode = new SparseArray<>();
    private static final SparseArray<String> sAntiDistortion = new SparseArray<>();

    static {
        sDisplayMode.put(MDVRLibrary.DISPLAY_MODE_NORMAL,"NORMAL");
        sDisplayMode.put(MDVRLibrary.DISPLAY_MODE_GLASS,"GLASS");

        sInteractiveMode.put(MDVRLibrary.INTERACTIVE_MODE_MOTION,"MOTION");
        sInteractiveMode.put(MDVRLibrary.INTERACTIVE_MODE_TOUCH,"TOUCH");
        sInteractiveMode.put(MDVRLibrary.INTERACTIVE_MODE_MOTION_WITH_TOUCH,"M & T");

        sProjectionMode.put(MDVRLibrary.PROJECTION_MODE_SPHERE,"SPHERE");
        sProjectionMode.put(MDVRLibrary.PROJECTION_MODE_DOME180,"DOME 180");
        sProjectionMode.put(MDVRLibrary.PROJECTION_MODE_DOME230,"DOME 230");
        sProjectionMode.put(MDVRLibrary.PROJECTION_MODE_DOME180_UPPER,"DOME 180 UPPER");
        sProjectionMode.put(MDVRLibrary.PROJECTION_MODE_DOME230_UPPER,"DOME 230 UPPER");
        sProjectionMode.put(MDVRLibrary.PROJECTION_MODE_STEREO_SPHERE,"STEREO SPHERE");
        sProjectionMode.put(MDVRLibrary.PROJECTION_MODE_PLANE_FIT,"PLANE FIT");
        sProjectionMode.put(MDVRLibrary.PROJECTION_MODE_PLANE_CROP,"PLANE CROP");
        sProjectionMode.put(MDVRLibrary.PROJECTION_MODE_PLANE_FULL,"PLANE FULL");
        sProjectionMode.put(MDVRLibrary.PROJECTION_MODE_MULTI_FISHEYE,"MULTI FISHEYE");

        sAntiDistortion.put(1,"ANTI-ENABLE");
        sAntiDistortion.put(0,"ANTI-DISABLE");
    }


    public static void startVideo(Context context, Uri uri){
        start(context, uri, VideoPlayerActivity.class);
    }

    public static void startBitmap(Context context, Uri uri){
        start(context, uri, BitmapPlayerActivity.class);
    }

    private static void start(Context context, Uri uri, Class<? extends Activity> clz){
        Intent i = new Intent(context,clz);
        i.setData(uri);
        context.startActivity(i);
    }

    private MDVRLibrary mVRLibrary;

    private List<MDAbsPlugin> plugins = new LinkedList<>();

    private MDPosition logoPosition = MDPosition.newInstance().setY(-8.0f).setYaw(-90.0f);

    private MDPosition[] positions = new MDPosition[]{
            MDPosition.newInstance().setZ(-8.0f).setYaw(-45.0f),
            MDPosition.newInstance().setZ(-18.0f).setYaw(15.0f).setAngleX(15),
            MDPosition.newInstance().setZ(-10.0f).setYaw(-10.0f).setAngleX(-15),
            MDPosition.newInstance().setZ(-10.0f).setYaw(30.0f).setAngleX(30),
            MDPosition.newInstance().setZ(-10.0f).setYaw(-30.0f).setAngleX(-30),
            MDPosition.newInstance().setZ(-5.0f).setYaw(30.0f).setAngleX(60),
            MDPosition.newInstance().setZ(-3.0f).setYaw(15.0f).setAngleX(-45),
            MDPosition.newInstance().setZ(-3.0f).setYaw(15.0f).setAngleX(-45).setAngleY(45),
            MDPosition.newInstance().setZ(-3.0f).setYaw(0.0f).setAngleX(90),
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // no title
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // set content view
        setContentView(R.layout.activity_md_using_surface_view);

        // init VR Library
        mVRLibrary = createVRLibrary();

        final List<View> hotspotPoints = new LinkedList<>();
        hotspotPoints.add(findViewById(R.id.hotspot_point1));
        hotspotPoints.add(findViewById(R.id.hotspot_point2));

        SpinnerHelper.with(this)
                .setData(sDisplayMode)
                .setDefault(mVRLibrary.getDisplayMode())
                .setClickHandler(new SpinnerHelper.ClickHandler() {
                    @Override
                    public void onSpinnerClicked(int index, int key, String value) {
                        mVRLibrary.switchDisplayMode(MD360PlayerActivityOther.this, key);
                        int i = 0;
                        for (View point : hotspotPoints){
                            point.setVisibility(i < mVRLibrary.getScreenSize() ? View.VISIBLE : View.GONE);
                            i++;
                        }
                    }
                })
                .init(R.id.spinner_display);

        SpinnerHelper.with(this)
                .setData(sInteractiveMode)
                .setDefault(mVRLibrary.getInteractiveMode())
                .setClickHandler(new SpinnerHelper.ClickHandler() {
                    @Override
                    public void onSpinnerClicked(int index, int key, String value) {
                        mVRLibrary.switchInteractiveMode(MD360PlayerActivityOther.this, key);
                    }
                })
                .init(R.id.spinner_interactive);

        SpinnerHelper.with(this)
                .setData(sProjectionMode)
                .setDefault(mVRLibrary.getProjectionMode())
                .setClickHandler(new SpinnerHelper.ClickHandler() {
                    @Override
                    public void onSpinnerClicked(int index, int key, String value) {
                        mVRLibrary.switchProjectionMode(MD360PlayerActivityOther.this, key);
                    }
                })
                .init(R.id.spinner_projection);

        SpinnerHelper.with(this)
                .setData(sAntiDistortion)
                .setDefault(mVRLibrary.isAntiDistortionEnabled() ? 1 : 0)
                .setClickHandler(new SpinnerHelper.ClickHandler() {
                    @Override
                    public void onSpinnerClicked(int index, int key, String value) {
                        mVRLibrary.setAntiDistortionEnabled(key != 0);
                    }
                })
                .init(R.id.spinner_distortion);

        findViewById(R.id.button_add_plugin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int index = (int) (Math.random() * 100) % positions.length;
                MDPosition position = positions[index];
                MDSimplePlugin plugin = MDSimplePlugin.builder()
                        .size(4f,4f)
                        .provider(new MDVRLibrary.IBitmapProvider() {
                            @Override
                            public void onProvideBitmap(MD360BitmapTexture.Callback callback) {
                                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), android.R.drawable.star_on);
                                callback.texture(bitmap);
                            }
                        })
                        .listenClick(new MDVRLibrary.IPickListener() {
                            @Override
                            public void onHotspotHit(IMDHotspot hotspot, long hitTimestamp) {
                                Toast.makeText(MD360PlayerActivityOther.this, "click star" + index, Toast.LENGTH_SHORT).show();
                            }
                        })
                        .title("star" + index)
                        .position(position)
                        .build();

                plugins.add(plugin);
                getVRLibrary().addPlugin(plugin);
                Toast.makeText(MD360PlayerActivityOther.this, "add plugin position:" + position, Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.button_add_plugin_logo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MDSimplePlugin plugin = MDSimplePlugin.builder()
                        .size(4f,4f)
                        .provider(new MDVRLibrary.IBitmapProvider() {
                            @Override
                            public void onProvideBitmap(MD360BitmapTexture.Callback callback) {
                                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.moredoo_logo);
                                callback.texture(bitmap);
                            }
                        })
                        .title("logo")
                        .position(logoPosition)
                        .listenClick(new MDVRLibrary.IPickListener() {
                            @Override
                            public void onHotspotHit(IMDHotspot hotspot, long hitTimestamp) {
                                Toast.makeText(MD360PlayerActivityOther.this, "click logo", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .build();

                plugins.add(plugin);
                getVRLibrary().addPlugin(plugin);
                Toast.makeText(MD360PlayerActivityOther.this, "add plugin logo" , Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.button_remove_plugin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (plugins.size() > 0){
                    MDAbsPlugin plugin = plugins.remove(plugins.size() - 1);
                    getVRLibrary().removePlugin(plugin);
                }
            }
        });

        findViewById(R.id.button_remove_plugins).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                plugins.clear();
                getVRLibrary().removePlugins();
            }
        });

        final TextView hotspotText = (TextView) findViewById(R.id.hotspot_text);
        getVRLibrary().setEyePickChangedListener(new MDVRLibrary.IPickListener() {
            @Override
            public void onHotspotHit(IMDHotspot hotspot, long hitTimestamp) {
                String text = hotspot == null ? "nop" : String.format(Locale.CHINESE, "%s  %fs", hotspot.getTitle(), (System.currentTimeMillis() - hitTimestamp) / 1000.0f );
                hotspotText.setText(text);
            }
        });





        //新加的2017-1-20
        editText_1 = (TextView) findViewById(R.id.et_1);
        editText_2 = (EditText) findViewById(R.id.et_2);

        receiveData();

    }
    /*
    服务器端接收数据
    需要注意以下一点：
    服务器端应该是多线程的，因为一个服务器可能会有多个客户端连接在服务器上；
    */
    public void receiveData(){

        Thread thread = new Thread(){
            @Override
            public void run() {
                super.run();
                /*指明服务器端的端口号*/
                try {
                    serverSocket = new ServerSocket(8000);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                GetIpAddress.getLocalIpAddress(serverSocket);

                Message message_1 = handler.obtainMessage();
                message_1.what = 1;
                message_1.obj = "IP:" + GetIpAddress.getIP() + " PORT: " + GetIpAddress.getPort();
                System.out.println("IP:" + GetIpAddress.getIP() + " PORT: " + GetIpAddress.getPort());
                handler.sendMessage(message_1);

                while (true){
                    Socket socket = null;
                    try {
                        socket = serverSocket.accept();
                        inputStream  = socket.getInputStream();
                        bufferedReader = new BufferedReader(new InputStreamReader(inputStream , "GBK"));

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    new ServerThread(socket,bufferedReader , inputStream).start();

                }
            }
        };
        thread.start();

    }

//    class ServerThread extends Thread {
//
//        private Socket socket;
//        private BufferedReader bufferedReader;
//        private InputStream inputStream;
//        private StringBuffer stringBuffer = MD360PlayerActivity.this.stringBuffer;
//
//
//        public ServerThread(Socket socket, BufferedReader bufferedReader, InputStream inputStream) {
//            this.socket = socket;
//            this.bufferedReader = bufferedReader;
//            this.inputStream = inputStream;
//        }
//
//        @Override
//        public void run() {
//            int len;
//            byte[] bytes = new byte[8192];
//            boolean isEnd = false;
//
//            try {
//                //在这里需要明白一下什么时候其会等于 -1，其在输入流关闭时才会等于 -1，
//                //并不是数据读完了，再去读才会等于-1，数据读完了，最结果也就是读不到数据为0而已；
//                while ((len = inputStream.read(bytes)) != -1) {
//                    Toast.makeText(MD360PlayerActivity.this, "收到客服的发送过来数据...", Toast.LENGTH_SHORT).show();
//                    for (int i = 0; i < len; i++) {
//                        if (bytes[i] != '\0') {
//                            stringBuffer.append(bytes[i]);
//                        } else {
//                            isEnd = true;
//                            break;
//                        }
//                    }
//                    if (isEnd) {
//                        Message message_2 = handler.obtainMessage();
//                        message_2.what = 2;
//                        message_2.obj = Tools.ChangeUTF8Str(stringBuffer.toString());
//                        handler.sendMessage(message_2);
//                        isEnd = false;
//                    }
//
//
//                }
//                //当这个异常发生时，说明客户端那边的连接已经断开
//            } catch (IOException e) {
//                Toast.makeText(MD360PlayerActivity.this, "客户端连接已断开，尝试断网重连...", Toast.LENGTH_SHORT).show();
//                e.printStackTrace();
//                try {
//                    bufferedReader.close();
//                    inputStream.close();
//                    socket.close();
//                } catch (IOException e1) {
//                    e1.printStackTrace();
//                }
//
//            }
//
//
//        }
//    }


    class ServerThread extends Thread{

        private Socket socket;
        private BufferedReader   bufferedReader;
        private InputStream inputStream;
        private StringBuffer stringBuffer = MD360PlayerActivityOther.this.stringBuffer;


        public ServerThread(Socket socket, BufferedReader bufferedReader, InputStream inputStream){
            this.socket = socket;
            this.bufferedReader = bufferedReader;
            this.inputStream = inputStream;
        }

        @Override
        public void run() {
            String str ;

            try {
                //在这里需要明白一下什么时候其会等于 -1，其在输入流关闭时才会等于 -1，
                //并不是数据读完了，再去读才会等于-1，数据读完了，最结果也就是读不到数据为0而已；
                while ((str = bufferedReader.readLine()) != null) {
                    if("over".equals(str)){
                        break;
                    }

                    Message message_2 = handler.obtainMessage();
                    message_2.what = 2;
                    message_2.obj = str;
                    handler.sendMessage(message_2);

                }
                //当这个异常发生时，说明客户端那边的连接已经断开
            } catch (IOException e) {
                Toast.makeText(MD360PlayerActivityOther.this,"客户端连接已断开，尝试断网重连...",Toast.LENGTH_SHORT).show();
                e.printStackTrace();
                try {
                    bufferedReader.close();
                    inputStream.close();
                    socket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

            }

        }
    }









    abstract protected MDVRLibrary createVRLibrary();

    public MDVRLibrary getVRLibrary() {
        return mVRLibrary;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mVRLibrary.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mVRLibrary.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mVRLibrary.onDestroy();
        try {
            if(serverSocket != null){
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected Uri getUri() {
        Intent i = getIntent();
        if (i == null || i.getData() == null){
            return null;
        }
        return i.getData();
    }

    public void cancelBusy(){
        findViewById(R.id.progress).setVisibility(View.GONE);
    }
}