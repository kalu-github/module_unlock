package lib.kalu.unlock.gesture;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import org.json.JSONArray;

import java.util.Arrays;
import java.util.List;

/**
 * // 0 -> 1 -> 2
 * // 3 -> 4 -> 5
 * // 6 -> 7 -> 8
 */
public class GestureresultView extends View {

    // 大圆半径
    float radius1 = -1f;
    // 小圆半径
    float radius2 = -1f;

    // 9个点选中顺序, >=0即选中
    private List<Integer> integers1 = Arrays.asList(-1, -1, -1, -1, -1, -1, -1, -1, -1);
    // 9个点圆心坐标
    private List<Integer> integers3 = Arrays.asList(-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1);

    /********************************   系统api   *************************************/

    public GestureresultView(Context context) {
        super(context);
        init(null);
    }

    public GestureresultView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public GestureresultView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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
        radius1 = width1 * 0.64f;

        float size1 = 20 * getResources().getDisplayMetrics().density;
        if (radius1 > size1) {
            radius1 = size1;
        }

        radius2 = width1 * 0.24f;
        float size2 = 8 * getResources().getDisplayMetrics().density;
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

        // LogUtil.e("gestureresultview", "onMeasure => " + integers3.toString());
    }

    private void init(@Nullable AttributeSet attrs) {

        if (null != attrs) {
//            TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.GestureView);
        }
    }

    /********************************   系统api   *************************************/

    @Override
    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);

        // TODO: 2020-06-16
        // 画布颜色
        int color = getResources().getColor(android.R.color.transparent);
        canvas.drawColor(color);

        // 画笔
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setFakeBoldText(true);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);

        // 9个圆
        for (int i = 0; i < 9; i++) {

            int cx = integers3.get(i * 2);
            int cy = integers3.get(i * 2 + 1);

            Integer index = integers1.get(i);
            if (index != -1) {
                paint.setColor(Color.parseColor("#D2A96D"));
            } else {
                // TODO: 2020-06-15
                paint.setColor(Color.parseColor("#E8E9ED"));
            }
            canvas.drawCircle(cx, cy, radius1, paint);

//            if (index != -1) {
//                getPaint().setColor(getResources().getColor(R.color.color_theme));
//            } else {
//                getPaint().setColor(getResources().getColor(R.color.color_ededed));
//            }
//            canvas.drawCircle(cx, cy, radius2, getPaint());

            // TODO: 2020-06-16
//            // 测试, 第几个被选中
//            if (index != -1) {
//
//                float v1 = DimenUtil.sp2px(10);
//                getPaint().setTextSize(v1);
//                getPaint().setColor(getResources().getColor(R.color.color_ffffff));
//
//                Paint.FontMetrics fontMetrics = getPaint().getFontMetrics();
//                float v = (fontMetrics.bottom - fontMetrics.top) * 0.24f;
//
//                canvas.drawText(String.valueOf(index), cx - v, cy + v, getPaint());
//            }
        }
    }

    public void clearGesture() {

        for (int i = 0; i < 9; i++) {
            integers1.set(i, -1);
        }

        postInvalidate();
    }

    public void postInvalidate(String str) {

        if (TextUtils.isEmpty(str))
            return;

        try {

            // 1. 解密
            byte[] bytes = str.getBytes();
            for (int n = 0; n < bytes.length; n++) {
                bytes[n] = (byte) (bytes[n] ^ 2);
            }
            String s = new String(bytes, 0, bytes.length);

            // 2. 格式
            JSONArray array = new JSONArray(s);
            if (null == array || array.length() != 9)
                return;

            for (int i = 0; i < 9; i++) {
                int optInt = array.optInt(i, -1);
                integers1.set(i, optInt);
            }
            postInvalidate();

        } catch (Exception e) {

        }
    }
}
