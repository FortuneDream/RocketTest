package com.example.q.rockettest;

import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

//小火箭原理：用透明的Activity
public class MainActivity extends Activity {
    private ImageView rocketImage;
    private AnimationDrawable rocketAnimation;//动画列表
    private WindowManager windowManager;
    private int windowY;
    private int windowX;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int y = (int) msg.obj;
            switch (msg.what){
                case 0:

                    rocketImage.layout(rocketImage.getLeft(), y, rocketImage.getRight(), rocketImage.getHeight() + y);
                    break;
                case 1:
                    rocketImage.layout(rocketImage.getLeft(), y, rocketImage.getRight(), rocketImage.getHeight() + y);
                    finish();
                    break;
                default:
                    break;
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toast.makeText(MainActivity.this,"请火箭拖入底部中央区域即可发射火箭",Toast.LENGTH_SHORT).show();
        DisplayMetrics displayMetrics = new DisplayMetrics();//测量值
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);//测量窗口
        windowY = displayMetrics.heightPixels;
        windowX = displayMetrics.widthPixels;
        rocketImage = (ImageView) findViewById(R.id.rocket);
        rocketImage.setBackgroundResource(R.drawable.rocket);//设置列表
        rocketAnimation = (AnimationDrawable) rocketImage.getBackground();//转化成动画
        rocketAnimation.start();
        //设置图标点击，移动事件
        //随意拖动小火箭
        rocketImage.setOnTouchListener(new View.OnTouchListener() {
            int starX = 0;
            int starY = 0;//一定要在申明，不能在方法里面

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        starX = (int) event.getRawX();
                        starY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int newX = (int) event.getRawX();
                        int newY = (int) event.getRawY();

                        int dX = newX - starX;//计算从按下到移动的偏移量
                        int dY = newY - starY;
                        rocketImage.layout(rocketImage.getLeft() + dX, rocketImage.getTop() + dY, rocketImage.getRight() + dX, rocketImage.getBottom() + dY);//根据偏移量更新控件位置，左上右下顺序
                        starX = (int) event.getRawX();//重新记录坐标
                        starY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_UP:
                        //火箭发送架
                        int newL = rocketImage.getLeft();
                        int newR = rocketImage.getRight();
                        int newT = rocketImage.getTop();
                        int rocketPlatformLeft = windowX / 3;
                        int rocketPlatformTop = windowY / 6 * 5;
                        int rocketPlatformRight = windowX / 3 * 2;
                        if (newL > rocketPlatformLeft && newT > rocketPlatformTop && newR < rocketPlatformRight) {
                            Toast.makeText(MainActivity.this, "火箭发送", Toast.LENGTH_SHORT).show();
                            sendRocket();//火箭发送系统
                            System.out.println(windowY);
                        }
                        break;
                }
                return true;
            }
        });
    }

    private void sendRocket() {
        //子线程每休眠一段时间，然后发消息给主线，view渲染
        new Thread() {
            @Override
            public void run() {
                int offset=30;
                for (int i = 0; i < offset; i++) {
                    SystemClock.sleep(50);//这方法忽略中断异常
                    Message message = Message.obtain();
                    int y = windowY - windowY / offset * i;
                    message.obj = y;
                    message.what=0;
                    System.out.println(y);
                    mHandler.sendMessage(message);
                }
                SystemClock.sleep(50);//这方法忽略中断异常
                Message message=Message.obtain();
                message.obj=0;
                message.what=1;
                mHandler.sendMessage(message);//处理发射到顶部

            }
        }.start();//一定要start
    }
}
