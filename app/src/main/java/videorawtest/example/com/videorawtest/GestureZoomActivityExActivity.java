package videorawtest.example.com.videorawtest;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;

import java.text.DecimalFormat;

public class GestureZoomActivityExActivity extends AppCompatActivity {

    private Bitmap mBitmap;
    private ImageView mImageView;
    private ScaleGestureDetector mScaleGestureDetector;//缩放事件手势识别器
    private Matrix matrix = new Matrix();
    private DecimalFormat df = new DecimalFormat("0.00");//格式化float
    private float lastScale = 1;//记录上次的缩放比例，下次缩放时是在此基础上进行的

    private float fLastX = 0.0f;
    private float fLastY = 0.0f;

    private Boolean bScale = false;
    private int nMode = -1;

    /*使图片居中*/
    private void center() {

        Matrix m = new Matrix();
        m.set(matrix);

        RectF rect = new RectF(0, 0, mBitmap.getWidth(), mBitmap.getHeight());
        m.mapRect(rect);
        float height = rect.height();
        float width = rect.width();

        float deltaX = 0, deltaY = 0;

        //屏幕的宽高
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm); //获取屏幕分辨率
        int mScreenWidth = dm.widthPixels;  //屏幕宽度
        int mScreenHeight = dm.heightPixels;  //屏幕高度

        //获取ActionBar的高度
        int actionBarHeight = 0;
        TypedValue tv = new TypedValue();
        if (this.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, this.getResources().getDisplayMetrics());
        }

        //计算Y到中心的距离

        deltaY = (mScreenHeight - height) / 2 - actionBarHeight - rect.top;
        //计算X到中心的距离
        deltaX = (mScreenWidth - width) / 2 - rect.left;

        matrix.postTranslate(deltaX, deltaY);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gesturezoomex);
        mImageView = (ImageView) findViewById(R.id.myImageView);

        mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.image);
        mImageView.setImageBitmap(mBitmap);

        matrix.setScale(0.5f, 0.5f); //显示先缩小一些
        center();
        mImageView.setImageMatrix(matrix);

        mScaleGestureDetector = new ScaleGestureDetector(this, new ScaleGestureDetector.SimpleOnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                bScale = true;
                nMode = -1;
                float scale = detector.getScaleFactor(); //缩放因子，两指靠拢时小于1
                float x = detector.getFocusX(), y = detector.getFocusY();//中心点坐标
                Log.i("bqt", "缩放手势  onScale，" + df.format(scale) + "-" + df.format(x) + "-" + df.format(y));
                matrix.setScale(lastScale * scale, lastScale * scale);
                center();
                mImageView.setImageMatrix(matrix);
                return super.onScale(detector);
            }

            @Override
            public boolean onScaleBegin(ScaleGestureDetector detector) {
                bScale = true;
                Log.i("bqt", "缩放手势  onScaleBegin，" + df.format(detector.getScaleFactor()));//始终是1
                return super.onScaleBegin(detector);
            }

            @Override
            public void onScaleEnd(ScaleGestureDetector detector) {
                bScale = false;
                super.onScaleEnd(detector);
                lastScale *= detector.getScaleFactor();
                Log.i("bqt", "缩放手势  onScaleEnd，" + df.format(detector.getScaleFactor()));
            }
        });

        mImageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (bScale == false && event.getPointerCount() == 1) {
                    int nAction = event.getAction();
                    nAction = nAction & MotionEvent.ACTION_MASK;
                    switch (nAction) {
                        case MotionEvent.ACTION_DOWN:
                            nMode = 1;
                            fLastX = event.getX();
                            fLastY = event.getY();
                        case MotionEvent.ACTION_MOVE: {

                            if (nMode != 1)
                                break;
                            float distanceX = event.getX() - fLastX;
                            float distanceY = event.getY() - fLastY;
                            fLastX = event.getX();
                            fLastY = event.getY();

                            if (Math.abs(distanceX) > 50 || Math.abs(distanceY) > 50)
                                break;

                            Log.i("bqt", "平移 distanceX=" + distanceX + " distanceY" + distanceY);
                            matrix.postTranslate(distanceX, distanceY);
                            mImageView.setImageMatrix(matrix);
                        }
                        break;
                        case MotionEvent.ACTION_POINTER_UP:
                            nMode = -1;
                            break;
                        case MotionEvent.ACTION_UP:
                            nMode = -1;
                            break;
                        default:
                            break;
                    }
                    return true;
                }
                return mScaleGestureDetector.onTouchEvent(event);
            }
        });

    }// onCreate

}
