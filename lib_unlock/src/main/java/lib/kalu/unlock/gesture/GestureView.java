package lib.kalu.unlock.gesture;

import android.app.Service;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import org.json.JSONArray;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * // 0 -> 1 -> 2
 * // 3 -> 4 -> 5
 * // 6 -> 7 -> 8
 */
public class GestureView extends View {

    // touch
    float touctX = -1;
    float touctY = -1;

    // 大圆半径
    float radius1 = -1f;
    // 小圆半径
    float radius2 = -1f;

    // 上一个选中的点, 下标
    private int last = -1;
    // 选中个数
    private int num = 0;
    // 9个点选中顺序, >=0即选中
    private List<Integer> integers1 = Arrays.asList(-1, -1, -1, -1, -1, -1, -1, -1, -1);
    // 选中shun顺序, 画线
    private List<Integer> integers2 = new LinkedList<>();
    // 9个点圆心坐标
    private List<Integer> integers3 = Arrays.asList(-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1);

    /********************************   系统api   *************************************/

    public GestureView(Context context) {
        super(context);
        init(null);
    }

    public GestureView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public GestureView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);

        // 计算9个点圆心

        // 正方形区域, 9等分, 9个圆的中心
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();

        int width1 = width / 6;
        int height1 = height / 6;

        radius1 = width1 * 0.64f;
        float size1 = 60 * getResources().getDisplayMetrics().density;
        if (radius1 > size1) {
            radius1 = size1;
        }

        radius2 = width1 * 0.24f;
        float size2 = 30 * getResources().getDisplayMetrics().density;
        if (radius2 > size2) {
            radius2 = size2;
        }

        for (int i = 0; i < 9; i++) {

            int cx; // 圆心x
            int cy; // 圆心y

            switch (i) {
                case 0:

                    cx = width1;
                    cy = height1;

                    break;
                case 1:

                    cx = width1 * 3;
                    cy = height1;

                    break;
                case 2:

                    cx = width1 * 5;
                    cy = height1;

                    break;
                case 3:

                    cx = width1;
                    cy = height1 * 3;

                    break;
                case 4:

                    cx = width1 * 3;
                    cy = height1 * 3;

                    break;
                case 5:

                    cx = width1 * 5;
                    cy = height1 * 3;

                    break;
                case 6:

                    cx = width1;
                    cy = height1 * 5;

                    break;
                case 7:

                    cx = width1 * 3;
                    cy = height1 * 5;

                    break;
                case 8:

                    cx = width1 * 5;
                    cy = height1 * 5;

                    break;
                default:

                    cx = -1;
                    cy = -1;

                    break;
            }

            integers3.set(i * 2, cx);
            integers3.set(i * 2 + 1, cy);
        }

        //  LogUtil.e("gestureview", "onMeasure => " + integers3.toString());
    }

    private void init(@Nullable AttributeSet attrs) {

        if (null != attrs) {
//            TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.GestureView);
        }
    }

    /********************************   系统api   *************************************/

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // TODO: 2020-06-15
        //  画布颜色
        int color = getResources().getColor(android.R.color.transparent);
        canvas.drawColor(color);

        // 画笔
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setFakeBoldText(true);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        float size = 1 * getResources().getDisplayMetrics().density;
        paint.setStrokeWidth(size);
        paint.setColor(Color.parseColor("#D9B47E"));

        // 连接线
        int size3 = integers2.size();
        for (int i = 0; i < size3; i++) {

            int num = i + 1;
            if (num == size3)
                break;

            int index1 = integers2.get(i);
            int index2 = integers2.get(num);

            int cx1 = integers3.get(index1 * 2);
            int cy1 = integers3.get(index1 * 2 + 1);
            int cx2 = integers3.get(index2 * 2);
            int cy2 = integers3.get(index2 * 2 + 1);
            canvas.drawLine(cx1, cy1, cx2, cy2, paint);
        }

        // 移动线
        if (last != -1 && touctX != -1 && touctY != -1) {

            int i = last * 2;
            int j = last * 2 + 1;

            if (i < integers3.size() && j < integers3.size()) {
                int cx = integers3.get(last * 2);
                int cy = integers3.get(last * 2 + 1);
                canvas.drawLine(cx, cy, touctX, touctY, paint);
            }

        }

        // 9个圆
        for (int i = 0; i < 9; i++) {

            int cx = integers3.get(i * 2);
            int cy = integers3.get(i * 2 + 1);

            paint.setAntiAlias(true);
            paint.setFakeBoldText(true);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeCap(Paint.Cap.ROUND);
            paint.setStrokeWidth(size);

            // 大圆
            Integer index = integers1.get(i);
            if (index != -1) {
                paint.setColor(Color.parseColor("#D9B47E"));
            } else {
                // TODO: 2020-06-15
                paint.setColor(Color.parseColor("#CCCCCC"));
            }
            paint.setStyle(Paint.Style.STROKE);
            canvas.drawCircle(cx, cy, radius1, paint);

            // 小圆
            if (index != -1) {
                paint.setColor(Color.parseColor("#D2A96D"));
            } else {
                paint.setColor(Color.parseColor("#00000000"));
            }
            paint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(cx, cy, radius2, paint);

            // TODO: 2020-06-16
//            // 测试, 第几个被选中
//            if (index != -1) {
//
//                float v1 = DimenUtil.sp2px(14);
//                paint.setTextSize(v1);
//                paint.setColor(getResources().getColor(R.color.color_ffffff));
//
//                Paint.FontMetrics fontMetrics = paint.getFontMetrics();
//                float v = (fontMetrics.bottom - fontMetrics.top) * 0.24f;
//
//                canvas.drawText(String.valueOf(index), cx - v, cy + v, paint);
//            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                getParent().requestDisallowInterceptTouchEvent(true);

                integers2.clear();

                touctX = -1;
                touctY = -1;

                for (int i = 0; i < 9; i++) {

                    int cx = integers3.get(i * 2);
                    int cy = integers3.get(i * 2 + 1);

                    // 选中
                    Integer integer = integers1.get(i);
                    if (integer != -1)
                        break;

                    final float rangex = cx - x;
                    final float rangey = cy - y;
                    final double sqrt = Math.sqrt(rangex * rangex + rangey * rangey);

                    // 圆内
                    if (sqrt < radius1) {

                        // 震动
                        try {
                            Vibrator vib = (Vibrator) getContext().getSystemService(Service.VIBRATOR_SERVICE);
                            vib.vibrate(50);
                        } catch (Exception e) {
                        }

                        // 赋值
                        last = i; // 最后一个选中点下标
                        integers2.add(i); // 画线
                        integers1.set(i, num++); // 选中顺序

                        if (null != mOnGestureChangeListener) {
                            mOnGestureChangeListener.onStart();
                        }

                        if (null != mOnGestureChangeListener) {

                            try {

                                // 整理
                                JSONArray array = new JSONArray();
                                for (int j = 0; j < integers1.size(); j++) {
                                    Integer integer1 = integers1.get(j);
                                    array.put(j, integer1);
                                }

                                // 加密
                                String toString = array.toString();
                                byte[] bytes = toString.getBytes();
                                for (int n = 0; n < bytes.length; n++) {
                                    bytes[n] = (byte) (bytes[n] ^ 2);
                                }

                                // 回调
                                String s = new String(bytes, 0, bytes.length);
                                mOnGestureChangeListener.onChange(s);

                            } catch (Exception e) {
                            }
                        }

                        // 刷新UI
                        postInvalidate();

                        //  LogUtil.e("gestureview", "onTouchEvent => down = " + integers1.toString());
                        break;
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:

                getParent().requestDisallowInterceptTouchEvent(true);

                for (int i = 0; i < 9; i++) {

                    int cx = integers3.get(i * 2);
                    int cy = integers3.get(i * 2 + 1);

                    // 选中
                    Integer integer = integers1.get(i);
                    if (integer != -1)
                        continue;

                    final float rangex = cx - x;
                    final float rangey = cy - y;
                    final double sqrt = Math.sqrt(rangex * rangex + rangey * rangey);

                    // 圆内
                    if (sqrt < radius1) {

                        // 震动
                        try {
                            Vibrator vib = (Vibrator) getContext().getSystemService(Service.VIBRATOR_SERVICE);
                            vib.vibrate(50);
                        } catch (Exception e) {
                        }

                        // 容错
                        if (last == 0 && i == 2) {

                            Integer integer1 = integers1.get(1);
                            if (integer1 == -1) {
                                integers1.set(1, num++); // 选中顺序
                                integers2.add(1); // 画线
                            }

                        } else if (last == 0 && i == 6) {

                            Integer integer1 = integers1.get(3);
                            if (integer1 == -1) {
                                integers1.set(3, num++); // 选中顺序
                                integers2.add(3); // 画线
                            }

                        } else if (last == 0 && i == 8) {

                            Integer integer1 = integers1.get(4);
                            if (integer1 == -1) {
                                integers1.set(4, num++); // 选中顺序
                                integers2.add(4); // 画线
                            }

                        }
                        // 1
                        else if (last == 1 && i == 7) {

                            Integer integer1 = integers1.get(4);
                            if (integer1 == -1) {
                                integers1.set(4, num++); // 选中顺序
                                integers2.add(4); // 画线
                            }

                        }
                        // 2
                        else if (last == 2 && i == 0) {

                            Integer integer1 = integers1.get(1);
                            if (integer1 == -1) {
                                integers1.set(1, num++); // 选中顺序
                                integers2.add(1); // 画线
                            }

                        } else if (last == 2 && i == 8) {

                            Integer integer1 = integers1.get(5);
                            if (integer1 == -1) {
                                integers1.set(5, num++); // 选中顺序
                                integers2.add(5); // 画线
                            }

                        } else if (last == 2 && i == 6) {

                            Integer integer1 = integers1.get(4);
                            if (integer1 == -1) {
                                integers1.set(4, num++); // 选中顺序
                                integers2.add(4); // 画线
                            }

                        }
                        // 3
                        else if (last == 3 && i == 5) {

                            Integer integer1 = integers1.get(4);
                            if (integer1 == -1) {
                                integers1.set(4, num++); // 选中顺序
                                integers2.add(4); // 画线
                            }

                        }
                        // 5
                        else if (last == 5 && i == 3) {

                            Integer integer1 = integers1.get(4);
                            if (integer1 == -1) {
                                integers1.set(4, num++); // 选中顺序
                                integers2.add(4); // 画线
                            }

                        }
                        // 6
                        else if (last == 6 && i == 0) {

                            Integer integer1 = integers1.get(3);
                            if (integer1 == -1) {
                                integers1.set(3, num++); // 选中顺序
                                integers2.add(3); // 画线
                            }

                        } else if (last == 6 && i == 8) {

                            Integer integer1 = integers1.get(7);
                            if (integer1 == -1) {
                                integers1.set(7, num++); // 选中顺序
                                integers2.add(7); // 画线
                            }

                        } else if (last == 6 && i == 2) {

                            Integer integer1 = integers1.get(4);
                            if (integer1 == -1) {
                                integers1.set(4, num++); // 选中顺序
                                integers2.add(4); // 画线
                            }

                        }
                        // 7
                        else if (last == 7 && i == 1) {

                            Integer integer1 = integers1.get(4);
                            if (integer1 == -1) {
                                integers1.set(4, num++); // 选中顺序
                                integers2.add(4); // 画线
                            }

                        }
                        // 8
                        else if (last == 8 && i == 2) {

                            Integer integer1 = integers1.get(5);
                            if (integer1 == -1) {
                                integers1.set(5, num++); // 选中顺序
                                integers2.add(5); // 画线
                            }

                        } else if (last == 8 && i == 6) {

                            Integer integer1 = integers1.get(7);
                            if (integer1 == -1) {
                                integers1.set(7, num++); // 选中顺序
                                integers2.add(7); // 画线
                            }

                        } else if (last == 8 && i == 0) {

                            Integer integer1 = integers1.get(4);
                            if (integer1 == -1) {
                                integers1.set(4, num++); // 选中顺序
                                integers2.add(4); // 画线
                            }

                        }

                        // 赋值
                        last = i; // 最后一个选中点下标
                        integers2.add(i); // 画线
                        integers1.set(i, num++); // 选中顺序

                        touctX = -1;
                        touctY = -1;

                        if (null != mOnGestureChangeListener) {

                            try {

                                // 整理
                                JSONArray array = new JSONArray();
                                for (int j = 0; j < integers1.size(); j++) {
                                    Integer integer1 = integers1.get(j);
                                    array.put(j, integer1);
                                }

                                // 加密
                                String toString = array.toString();
                                byte[] bytes = toString.getBytes();
                                for (int n = 0; n < bytes.length; n++) {
                                    bytes[n] = (byte) (bytes[n] ^ 2);
                                }

                                // 回调
                                String s = new String(bytes, 0, bytes.length);
                                mOnGestureChangeListener.onChange(s);

                            } catch (Exception e) {
                            }
                        }

                        // 刷新UI
                        postInvalidate();

                        //  LogUtil.e("gestureview", "onTouchEvent => move = " + integers1.toString());
                        break;
                    } else {

                        touctX = x;
                        touctY = y;

                        // 刷新UI
                        postInvalidate();

                        //   LogUtil.e("gestureview", "onTouchEvent => move = " + integers1.toString());

                    }
                }

                break;
            case MotionEvent.ACTION_UP:

                getParent().requestDisallowInterceptTouchEvent(false);

                if (num >= 4 && null != mOnGestureChangeListener) {

                    try {

                        // 整理
                        JSONArray array = new JSONArray();
                        for (int j = 0; j < integers1.size(); j++) {
                            Integer integer1 = integers1.get(j);
                            array.put(j, integer1);
                        }

                        // 加密
                        String toString = array.toString();
                        byte[] bytes = toString.getBytes();
                        for (int n = 0; n < bytes.length; n++) {
                            bytes[n] = (byte) (bytes[n] ^ 2);
                        }

                        // 回调
                        String s = new String(bytes, 0, bytes.length);
                        mOnGestureChangeListener.onChange(s);

                    } catch (Exception e) {
                    }
                }

                if (num < 4) {

                    if (null != mOnGestureChangeListener) {
                        mOnGestureChangeListener.onFail();
                    }

                    // Toast.makeText(getContext(), "最少绘制4个点", Toast.LENGTH_SHORT).show();
                }

                touctX = -1;
                touctY = -1;

                num = 0;
                last = -1;

                integers2.clear();

                for (int i = 0; i < 9; i++) {
                    integers1.set(i, -1);
                }

                // 刷新UI
                postInvalidate();

                // Log.e("gestureview", "onTouchEvent => integers1 = " + integers1.toString());

                break;
        }
        return true;
    }

    /**********************************************************************************************/

    @Override
    public void setPadding(int left, int top, int right, int bottom) {
    }

    @Override
    public void setPaddingRelative(int start, int top, int end, int bottom) {
    }

    /**********************************************************************************************/

    private OnGestureChangeListener mOnGestureChangeListener;

    public interface OnGestureChangeListener {

        void onResult(String result);

        void onChange(String result);

        void onFail();

        void onStart();
    }

    public void setOnGestureChangeListener(OnGestureChangeListener listener) {
        this.mOnGestureChangeListener = listener;
    }
}
