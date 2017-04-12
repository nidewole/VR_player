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
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * using MD360Renderer
 * <p>
 * Created by hzqiujiadi on 16/1/22.
 * hzqiujiadi ashqalcn@gmail.com
 */
public abstract class MD360PlayerActivity extends Activity{
    private static VideoPlayerActivity mVideoPlayerActivity;
    private TextView editText_1;
    private EditText editText_2;
    private LinearLayout llAllDisplay;
    private LinearLayout spinnerLayout;
    private LinearLayout video;
    private LinearLayout ll_pause;
    private ServerSocket serverSocket = null;
    StringBuffer stringBuffer = new StringBuffer();
    private static final String ACTION = "rececycer_client_relation";
    private static final String ACTION_1 = "rececycer_client_switch_stream";
    private static final String RELATIONDATA = "data";
    private InputStream inputStream;
    private LinkedList<String> linkedList = new LinkedList<>();
    private boolean isFrist = true;
    private TextView tvServerTime;
    private int count = 0;

    public Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case 1:
//                    editText_1.setText(msg.obj.toString());
                    Toast.makeText(MD360PlayerActivity.this, "获取到IP地址和端口号..." + msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    break;
                case 2:
//                    Toast.makeText(MD360PlayerActivity.this, "收到客服的发送过来数据..." + msg.obj.toString(), Toast.LENGTH_SHORT).show();

                    String s = msg.obj.toString();
                    Log.e("---" , s);
//                    editText_2.setText(s);
                    if (!TextUtils.isEmpty(s) && s.length() <=5) {

//                        count++;
//                        if (count % 2 == 0) {
//                            MD360PlayerActivity.startVideo(mContext , Uri.parse("file:///mnt/sdcard/28.mp4"));
//                        }else {
//                            MD360PlayerActivity.startVideo(mContext , Uri.parse("file:///mnt/sdcard/29.mp4"));
//                        }
//                        new Handler().postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                finish();
//                            }
//                        } , 1000);
                        Intent intent = new Intent();
                        intent.setAction(ACTION_1);
                        intent.putExtra(RELATIONDATA, s);
                        sendBroadcast(intent);
                    }else {
                        Intent intent = new Intent();
                        intent.setAction(ACTION);
                        intent.putExtra(RELATIONDATA, s);
                        sendBroadcast(intent);
                    }





//                    linkedList = new LinkedList<>();
//                    linkedList.add(s);
//                    if (isFrist) {
//                        isFrist = false;
//                        handler.sendEmptyMessage(3);
//                    }

                    break;
//                case 3:
//                    sendMessa();
//                    String trim = tvServerTime.getText().toString().trim();
//                    if (TextUtils.isEmpty(trim)) {
//                        trim = "10";
//                    }
//                    int time = Integer.parseInt(trim);
//
//                    handler.sendEmptyMessageDelayed(3, time);
//                    break;
                case 4:
                    firstClickTime = 0;
                    if (!isDoubleClick) {
                        oneClick();//单击回调
                    }
                    break;

            }

        }
    };

    private void sendMessa(){

            if (linkedList.size() > 0){
                if (linkedList.size() <= 10) {
                    Intent intent = new Intent();
                    intent.setAction(ACTION);
                    intent.putExtra(RELATIONDATA, linkedList.removeFirst());
                    sendBroadcast(intent);

                }else {
                    Intent intent = new Intent();
                    intent.setAction(ACTION);
                    intent.putExtra(RELATIONDATA, linkedList.getLast());
                    sendBroadcast(intent);
                    linkedList.clear();
                }

            }
    }



    private BufferedReader bufferedReader;

    //以上是新增内容为了接收方向数据


    private static final String TAG = "MD360PlayerActivity";

    private static final SparseArray<String> sDisplayMode = new SparseArray<>();
    private static final SparseArray<String> sInteractiveMode = new SparseArray<>();
    private static final SparseArray<String> sProjectionMode = new SparseArray<>();
    private static final SparseArray<String> sAntiDistortion = new SparseArray<>();

    static {
        sDisplayMode.put(MDVRLibrary.DISPLAY_MODE_NORMAL, "NORMAL");
        sDisplayMode.put(MDVRLibrary.DISPLAY_MODE_GLASS, "GLASS");

        sInteractiveMode.put(MDVRLibrary.INTERACTIVE_MODE_MOTION, "MOTION");
        sInteractiveMode.put(MDVRLibrary.INTERACTIVE_MODE_TOUCH, "TOUCH");
        sInteractiveMode.put(MDVRLibrary.INTERACTIVE_MODE_MOTION_WITH_TOUCH, "M & T");

        sProjectionMode.put(MDVRLibrary.PROJECTION_MODE_SPHERE, "SPHERE");
        sProjectionMode.put(MDVRLibrary.PROJECTION_MODE_DOME180, "DOME 180");
        sProjectionMode.put(MDVRLibrary.PROJECTION_MODE_DOME230, "DOME 230");
        sProjectionMode.put(MDVRLibrary.PROJECTION_MODE_DOME180_UPPER, "DOME 180 UPPER");
        sProjectionMode.put(MDVRLibrary.PROJECTION_MODE_DOME230_UPPER, "DOME 230 UPPER");
        sProjectionMode.put(MDVRLibrary.PROJECTION_MODE_STEREO_SPHERE, "STEREO SPHERE");
        sProjectionMode.put(MDVRLibrary.PROJECTION_MODE_PLANE_FIT, "PLANE FIT");
        sProjectionMode.put(MDVRLibrary.PROJECTION_MODE_PLANE_CROP, "PLANE CROP");
        sProjectionMode.put(MDVRLibrary.PROJECTION_MODE_PLANE_FULL, "PLANE FULL");
        sProjectionMode.put(MDVRLibrary.PROJECTION_MODE_MULTI_FISHEYE, "MULTI FISHEYE");

        sAntiDistortion.put(1, "ANTI-ENABLE");
        sAntiDistortion.put(0, "ANTI-DISABLE");
    }

    private Context mContext;


    public static void startVideo(Context context, Uri uri) {
        start(context, uri, VideoPlayerActivity.class);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mVideoPlayerActivity = new VideoPlayerActivity();

            }
        }, 2000);
    }

    public static void startBitmap(Context context, Uri uri) {
        start(context, uri, BitmapPlayerActivity.class);
    }

    private static void start(Context context, Uri uri, Class<? extends Activity> clz) {
        Intent i = new Intent(context, clz);
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

        mContext = this;

        final List<View> hotspotPoints = new LinkedList<>();
        hotspotPoints.add(findViewById(R.id.hotspot_point1));
        hotspotPoints.add(findViewById(R.id.hotspot_point2));

        SpinnerHelper.with(this)
                .setData(sDisplayMode)
                .setDefault(mVRLibrary.getDisplayMode())
                .setClickHandler(new SpinnerHelper.ClickHandler() {
                    @Override
                    public void onSpinnerClicked(int index, int key, String value) {
                        mVRLibrary.switchDisplayMode(MD360PlayerActivity.this, key);
                        int i = 0;
                        for (View point : hotspotPoints) {
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
                        mVRLibrary.switchInteractiveMode(MD360PlayerActivity.this, key);
                    }
                })
                .init(R.id.spinner_interactive);

        SpinnerHelper.with(this)
                .setData(sProjectionMode)
                .setDefault(mVRLibrary.getProjectionMode())
                .setClickHandler(new SpinnerHelper.ClickHandler() {
                    @Override
                    public void onSpinnerClicked(int index, int key, String value) {
                        mVRLibrary.switchProjectionMode(MD360PlayerActivity.this, key);
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
                        .size(4f, 4f)
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
                                Toast.makeText(MD360PlayerActivity.this, "click star" + index, Toast.LENGTH_SHORT).show();
                            }
                        })
                        .title("star" + index)
                        .position(position)
                        .build();

                plugins.add(plugin);
                getVRLibrary().addPlugin(plugin);
                Toast.makeText(MD360PlayerActivity.this, "add plugin position:" + position, Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.button_add_plugin_logo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MDSimplePlugin plugin = MDSimplePlugin.builder()
                        .size(4f, 4f)
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
                                Toast.makeText(MD360PlayerActivity.this, "click logo", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .build();

                plugins.add(plugin);
                getVRLibrary().addPlugin(plugin);
                Toast.makeText(MD360PlayerActivity.this, "add plugin logo", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.button_remove_plugin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (plugins.size() > 0) {
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
                String text = hotspot == null ? "nop" : String.format(Locale.CHINESE, "%s  %fs", hotspot.getTitle(), (System.currentTimeMillis() - hitTimestamp) / 1000.0f);
                hotspotText.setText(text);
            }
        });


        //新加的2017-1-20
//        editText_1 = (TextView) findViewById(R.id.et_1);
//        editText_2 = (EditText) findViewById(R.id.et_2);





        tvServerTime = (TextView) findViewById(R.id.tv_server_time);


        ll_pause = (LinearLayout) findViewById(R.id.ll_pause);
        llAllDisplay = (LinearLayout) findViewById(R.id.ll_all_display);
        spinnerLayout = (LinearLayout) findViewById(R.id.spinner_layout);
        video = (LinearLayout) findViewById(R.id.video);
        llAllDisplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (firstClickTime > 0) {
                    secondClickTime = System.currentTimeMillis();
                    if (secondClickTime - firstClickTime < 200) {
                        doubleClick();//双击回调
                        firstClickTime = 0;
                        isDoubleClick = true;
                        return;
                    }
                }

                firstClickTime = System.currentTimeMillis();
                isDoubleClick = false;

                handler.sendEmptyMessageDelayed(4, 200);

            }
        });




        try {
            service();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void doubleClick() {
        if (video.getVisibility() == View.GONE){
            video.setVisibility(View.VISIBLE);
            spinnerLayout.setVisibility(View.VISIBLE);
            startVisibility();
        }else {
            video.setVisibility(View.GONE);
            spinnerLayout.setVisibility(View.GONE);
            endVisibility();
        }
    }

    /**
     * 两次退出程序
     */
    private long mExitTime;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 1400) {
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                mExitTime = System.currentTimeMillis();

            } else {
                finish();
            }
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_MENU) {
            if (video.getVisibility() == View.GONE){
                video.setVisibility(View.VISIBLE);
                spinnerLayout.setVisibility(View.VISIBLE);
                startVisibility();
            }else {
                video.setVisibility(View.GONE);
                spinnerLayout.setVisibility(View.GONE);
                endVisibility();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void oneClick() {

    }

    private long firstClickTime = 0;
    private long secondClickTime = 0;
    private boolean isDoubleClick;



    private int port = 4700;
    private ServerSocketChannel serverSocketChannel;
    private Charset charset = Charset.forName("UTF-8");
    private Selector selector = null;
    private ExecutorService cachedThreadPool = Executors.newSingleThreadExecutor();

    /* 服务器服务方法 */
    public void service() throws IOException {

        selector = Selector.open();
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.socket().setReuseAddress(true);
        serverSocketChannel.socket().bind(new InetSocketAddress(port));
        System.out.println("服务器启动");
        final SelectionKey[] key = {null};

        cachedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    serverSocketChannel.configureBlocking(false);
                    serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

                /** 外循环，已经发生了SelectionKey数目 */
                while (selector.select() > 0) {
            /* 得到已经被捕获了的SelectionKey的集合 */
                    Iterator iterator = selector.selectedKeys().iterator();
                    while (iterator.hasNext()) {
//                        SelectionKey key = null;
//                        try {
                            key[0] = (SelectionKey) iterator.next();
                            iterator.remove();
                            if (key[0].isAcceptable()) {
                                ServerSocketChannel ssc = (ServerSocketChannel) key[0].channel();
                                SocketChannel sc = ssc.accept();
                                System.out
                                        .println("客户端机子的地址是 "
                                                + sc.socket().getRemoteSocketAddress()
                                                + "  客户端机机子的端口号是 "
                                                + sc.socket().getLocalPort());
                                sc.configureBlocking(false);
                                ByteBuffer buffer = ByteBuffer.allocate(1024);
                                sc.register(selector, SelectionKey.OP_READ, buffer);//buffer通过附件方式，传递
                            }
                            if (key[0].isReadable()) {
                                reveice(key[0]);
                            }
                            if (key[0].isWritable()) {
//                       send(key);
                            }
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                            try {
//                                if (key != null) {
//                                    key.cancel();
//                                    key.channel().close();
//                                }
//                            } catch (ClosedChannelException cex) {
//                                e.printStackTrace();
//                            }
//                        }
                    }
            /* 内循环完 */
                }
        /* 外循环完 */

            } catch (Exception e) {
                e.printStackTrace();
                try {
                    if (key[0] != null) {
                        key[0].cancel();
                        key[0].channel().close();
                    }
                } catch (ClosedChannelException cex) {
                    e.printStackTrace();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                }



            }
        });
    }


    int x = 1;

    /* 接收 */
    public void reveice(SelectionKey key) throws IOException {
        if (key == null)
            return;
        //***用SelectionKey.attachment()获取客户端消息***//
        //：通过附件方式，接收数据
//       ByteBuffer buff = (ByteBuffer) key.attachment();
        // SocketChannel sc = (SocketChannel) key.channel();
//       buff.limit(buff.capacity());
        // buff.position(0);
        // sc.read(buff);
        // buff.flip();
        // String reviceData = decode(buff);
        // System.out.println("接收：" + reviceData);

        //***用channel.read()获取客户端消息***//
        //：接收时需要考虑字节长度
        SocketChannel sc = (SocketChannel) key.channel();
        String content = "";
        //create buffer with capacity of 48 bytes
        ByteBuffer buf = ByteBuffer.allocate(2);//java里一个(utf-8)中文3字节,gbk中文占2个字节
        int bytesRead = sc.read(buf); //read into buffer.

        while (bytesRead > 0) {
            buf.flip();  //make buffer ready for read
            while (buf.hasRemaining()) {
                buf.get(new byte[buf.limit()]); // read 1 byte at a time
                content += new String(buf.array());
            }
            buf.clear(); //make buffer ready for writing
            bytesRead = sc.read(buf);
        }
        if (!TextUtils.isEmpty(content)) {
//            System.out.println("接收：" + content.trim());
            Message message_2 = handler.obtainMessage();
            message_2.what = 2;
            message_2.obj = content.trim();
            handler.sendMessage(message_2);

        }

        // sc.write(ByteBuffer.wrap(reviceData.getBytes()));
//      try {
//          sc.write(ByteBuffer.wrap(new String(
//                  "测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试" + (++x)).getBytes()));// 将消息回送给客户端
//      } catch (IOException e1) {
//          e1.printStackTrace();
//      }
    }



    class ServerThread extends Thread{

        private Socket socket;
        private BufferedReader   bufferedReader;
        private InputStream inputStream;
        private StringBuffer stringBuffer = MD360PlayerActivity.this.stringBuffer;


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
                Toast.makeText(MD360PlayerActivity.this,"客户端连接已断开，尝试断网重连...",Toast.LENGTH_SHORT).show();
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
        if (mVRLibrary != null) {
            mVRLibrary.onPause(this);
        }
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mVRLibrary.onDestroy();
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected Uri getUri() {
        Intent i = getIntent();
        if (i == null || i.getData() == null) {
            return null;
        }
        return i.getData();
    }

    public void cancelBusy() {
        findViewById(R.id.progress).setVisibility(View.GONE);
    }
    public void startVisibility() {
        ll_pause.setVisibility(View.VISIBLE);
    }
    public void endVisibility() {
        ll_pause.setVisibility(View.GONE);
    }




}