package videorawtest.example.com.videorawtest;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

public class GestureDragZoomActivity extends AppCompatActivity {

    private int mode = 0;// 初始状态 记录是拖拉照片模式还是放大缩小照片模式
    private static final int MODE_DRAG = 1;//拖拉照片模式
    private static final int MODE_ZOOM = 2; // 放大缩小照片模式
    private PointF startPoint = new PointF(); // 用于记录开始时候的坐标位置
    private Matrix matrix = new Matrix(); //用于记录拖拉图片移动的坐标位置
    private Matrix currentMatrix = new Matrix(); //用于记录图片要进行拖拉时候的坐标位置
    private float startDis;  //两个手指的开始距离
    private PointF midPoint; //两个手指的中间点

    private ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gesturedragzoom);
        mImageView = (ImageView) findViewById(R.id.myImageView);

        mImageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:// 手指压下屏幕
                        mode = MODE_DRAG;
                        currentMatrix.set(mImageView.getImageMatrix()); // 记录ImageView当前的移动位置
                        startPoint.set(event.getX(), event.getY());
                        break;
                    case MotionEvent.ACTION_POINTER_DOWN:// 当屏幕上已经有触点(手指)，再有一个触点压下屏幕
                        mode = MODE_ZOOM;
                        startDis = distance(event);//计算两个手指间的距离
                        if (startDis > 10f) { // 两个手指并拢在一起的时候像素大于10
                            midPoint = mid(event); //计算两个手指间的中间点
                            currentMatrix.set(mImageView.getImageMatrix()); //记录当前ImageView的缩放倍数
                        }
                        break;
                    case MotionEvent.ACTION_MOVE: // 手指在屏幕上移动，改事件会被不断触发
                        if (mode == MODE_DRAG) { // 拖拉图片
                            float dx = event.getX() - startPoint.x; // 得到x轴的移动距离
                            float dy = event.getY() - startPoint.y; // 得到x轴的移动距离
                            matrix.set(currentMatrix);  // 在没有移动之前的位置上进行移动
                            matrix.postTranslate(dx, dy);
                        }
                        else if (mode == MODE_ZOOM) { // 放大缩小图片
                            float endDis = distance(event);// 结束距离
                            if (endDis > 10f) { // 两个手指并拢在一起的时候像素大于10
                                float scale = endDis / startDis;// 得到缩放倍数
                                matrix.set(currentMatrix);
                                matrix.postScale(scale, scale, midPoint.x, midPoint.y);
                            }
                        }
                        break;
                    case MotionEvent.ACTION_UP: // 手指离开屏幕
                    case MotionEvent.ACTION_POINTER_UP:// 当触点离开屏幕，但是屏幕上还有触点(手指)
                        mode = 0;
                        break;
                    default:
                        break;
                }
                mImageView.setImageMatrix(matrix);
                return true;
            }
        });
    }

    //计算两个手指间的距离
    private float distance(MotionEvent event) {
        float dx = event.getX(1) - event.getX(0);
        float dy = event.getY(1) - event.getY(0);
        return (float) Math.sqrt(dx * dx + dy * dy);//使用勾股定理返回两点之间的距离
    }

    //计算两个手指间的中间点
    private PointF mid(MotionEvent event) {
        float midX = (event.getX(1) + event.getX(0)) / 2;
        float midY = (event.getY(1) + event.getY(0)) / 2;
        return new PointF(midX, midY);
    }
}
