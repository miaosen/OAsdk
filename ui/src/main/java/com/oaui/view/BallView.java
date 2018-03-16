//package com.osui.view;
//
//import android.content.Context;
//import android.graphics.Canvas;
//import android.graphics.Color;
//import android.graphics.Paint;
//import android.view.MotionEvent;
//import android.view.View;
//
//import com.osui.utils.AppUtils;
//
//import java.util.Random;
//
///**
// * @author zengmiaosen
// * @email 1510809124@qq.com
// * @CreateDate 2018-03-13  10:47
// * @Descrition
// */
//
//public class BallView extends View{
//
//
//    private Paint paint;        //定义画笔
//    private float cx = 50;      //圆点默认X坐标
//    private float cy = 50;      //圆点默认Y坐标
//    private int radius = 20;
//    //定义颜色数组
//    private int colorArray[] = {Color.BLACK, Color.BLACK, Color.GREEN, Color.YELLOW, Color.RED};
//    private int paintColor = colorArray[0]; //定义画笔默认颜色
//
//    public BallView(Context context) {
//        super(context);
//        //初始化画笔
//        initPaint();
//    }
//
//    private void initPaint() {
//        paint = new Paint();
//        //设置消除锯齿
//        paint.setAntiAlias(true);
//        //设置画笔颜色
//        paint.setColor(paintColor);
//    }
//
//    //重写onDraw方法实现绘图操作
//    @Override
//    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
//        //将屏幕设置为白色
//        canvas.drawColor(Color.WHITE);
//        //修正圆点坐标
//        revise();
//        //随机设置画笔颜色
//        setPaintRandomColor();
//        //绘制小圆作为小球
//        canvas.drawCircle(cx, cy, radius, paint);
//    }
//
//    //为画笔设置随机颜色
//    private void setPaintRandomColor() {
//        Random rand = new Random();
//        int randomIndex = rand.nextInt(colorArray.length);
//        paint.setColor(colorArray[randomIndex]);
//    }
//
//    //修正圆点坐标
//    private void revise() {
//        if (cx <= radius) {
//            cx = radius;
//        } else if (cx >= (AppUtils.getScreenWidth() - radius)) {
//            cx = AppUtils.getScreenWidth() - radius;
//        }
//        if (cy <= radius) {
//            cy = radius;
//        } else if (cy >= (AppUtils.getScreenHeight() - radius)) {
//            cy = AppUtils.getScreenHeight() - radius;
//        }
//    }
//
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        switch (event.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                // 按下
//                cx = (int) event.getX();
//                cy = (int) event.getY();
//                // 通知重绘
//                postInvalidate();   //该方法会调用onDraw方法，重新绘图
//                break;
//            case MotionEvent.ACTION_MOVE:
//                // 移动
//                cx = (int) event.getX();
//                cy = (int) event.getY();
//                // 通知重绘
//                postInvalidate();
//                break;
//            case MotionEvent.ACTION_UP:
//                // 抬起
//                cx = (int) event.getX();
//                cy = (int) event.getY();
//                // 通知重绘
//                postInvalidate();
//                break;
//        }
//
//            /*
//             * 备注1：此处一定要将return super.onTouchEvent(event)修改为return true，原因是：
//             * 1）父类的onTouchEvent(event)方法可能没有做任何处理，但是返回了false。
//             * 2)一旦返回false，在该方法中再也不会收到MotionEvent.ACTION_MOVE及MotionEvent.ACTION_UP事件。
//             */
//        //return super.onTouchEvent(event);
//        return true;
//    }
//}
