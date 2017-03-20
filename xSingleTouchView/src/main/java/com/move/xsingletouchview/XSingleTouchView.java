package com.move.xsingletouchview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import static com.move.xsingletouchview.XSingleTouchView.ControlPosition.Delete;
import static com.move.xsingletouchview.XSingleTouchView.ControlPosition.Drag;
import static com.move.xsingletouchview.XSingleTouchView.ControlPosition.Edit;
import static com.move.xsingletouchview.XSingleTouchView.ControlPosition.Nothing;
import static com.move.xsingletouchview.XSingleTouchView.ControlPosition.Rotate;
import static com.move.xsingletouchview.XSingleTouchView.ControlPosition.Scale;


/**
 * Created by cxj on 2017/2/15.
 * 优化的单手操作的可旋转、缩放、拖拽、编辑、删除的多功能View
 * 编辑和删除功能是暴露给外边实现的
 * 缩放和旋转不是通过View自带的方法实现的,因为那样子如果是一个文本或者图片会比较模糊,所以这里是对一个bitmap进行操作,把它绘制在这个View上
 * 而为了实现缩放和旋转等功能的时候,自身View的大小一直在变,所以产生的事件中的坐标点因为是参照自身View的坐标所以就不能用啦
 * 所以为了实现缩放和旋转功能的时候,所有需要用到的坐标点需要转换相对父容器的一个坐标点,因为父容器的自身大小不会变,是一个可靠的参照
 * <p>
 * 整个绘制出来的流程:
 * 此类显示出来的需要的数据整理:
 * 1.中心点
 * 2.自身的大小
 * 3.layout安置自己的位置
 * 4.绘制四个控制点需要的四个坐标(也包括了控制点的大小和和旋转角度)
 * 5.最后就是大图的偏移量和旋转角度
 * <p>
 * 另外因为产生效果要改变的值包括:旋转的角度、中心点的位置(拖拽的时候)、大图的大小
 * <p>
 * 每一个本类都有一个Bitmap对象,就是需要绘制的图像,然而bitmap本身的大小这里并不关心,也不管bitmap是哪里来的
 * (可能是一个图片经过裁剪、遮罩、滤镜或者是一个文字经过颜色变换、字体变换)
 * 最终你给的必须是一个bitmap,我只管bitmap根据配置文件{@link XViewConfig}绘制到画布上呈现给用户
 * 并且在宽或者高有限制的时候,当达到一边的限制的时候,图的缩放并不会被限制,只是被限制的宽或者高被固定显示了
 * 然后宽和高都已经达到限制的情况下,整个图就被限制了,简单点说就是说大图的缩放需要外面显示的框框宽和高都达到
 * 最小值才会停止,否则可以一直缩小
 */
public class XSingleTouchView extends View implements View.OnClickListener {

    /**
     * 初始化的时候中间点的位置
     */
    public enum CenterPosition {
        Center, Left, Top, Right, Bottom, LeftTop, RightTop, LeftBottom, RightBottom
    }

    public XSingleTouchView(Context context) {
        this(context, null);
    }

    public XSingleTouchView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public XSingleTouchView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, null);
    }

    public XSingleTouchView(Context context, AttributeSet attrs, int defStyleAttr, XViewConfig xViewConfig) {
        super(context, attrs, defStyleAttr);
        if (xViewConfig == null) {
            //如果是空的就创建一个
            xViewConfig = new XViewConfig(context, false);
        }

        mXViewConfig = xViewConfig;
        //拿到中间点
        mCenterPoint = mXViewConfig.getCenterPoint();

        //设置点击事件
        setOnClickListener(this);

        //读取自定义属性
        readAttr(context, attrs);
    }

    /**
     * 读取自定义的属性
     *
     * @param context
     * @param attrs
     */
    private void readAttr(Context context, AttributeSet attrs) {

        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.XSingleTouchView);

        //获取自定义属性

        //左上角的控制点图片
        int ltBitmapRsd = a.getResourceId(R.styleable.XSingleTouchView_src_lt, -1);
        if (ltBitmapRsd != -1) {
            Bitmap ltbitmap = BitmapFactory.decodeResource(context.getResources(), ltBitmapRsd);
            mXViewConfig.setLtControlBitmap(ltbitmap);
        }

        //左下角的控制点图片
        int lbBitmapRsd = a.getResourceId(R.styleable.XSingleTouchView_src_lb, -1);
        if (lbBitmapRsd != -1) {
            Bitmap lbbitmap = BitmapFactory.decodeResource(context.getResources(), lbBitmapRsd);
            mXViewConfig.setLbControlBitmap(lbbitmap);
        }

        //右上角的控制点图片
        int rtBitmapRsd = a.getResourceId(R.styleable.XSingleTouchView_src_rt, -1);
        if (rtBitmapRsd != -1) {
            Bitmap rtbitmap = BitmapFactory.decodeResource(context.getResources(), rtBitmapRsd);
            mXViewConfig.setRtControlBitmap(rtbitmap);
        }

        //右下角的控制点图片
        int rbBitmapRsd = a.getResourceId(R.styleable.XSingleTouchView_src_rb, -1);
        if (rbBitmapRsd != -1) {
            Bitmap rbbitmap = BitmapFactory.decodeResource(context.getResources(), rbBitmapRsd);
            mXViewConfig.setRbControlBitmap(rbbitmap);
        }

        //大图
        int centerRsd = a.getResourceId(R.styleable.XSingleTouchView_src, -1);
        if (centerRsd != -1) {
            Bitmap centerbitmap = BitmapFactory.decodeResource(context.getResources(), centerRsd);
            mXViewConfig.setCenterBitmap(centerbitmap);
        }

        //读取位置信息
        int gravity = a.getInt(R.styleable.XSingleTouchView_gravity, 0);
        //暂时实现不了

        //读取大图的大小
        float imageWidth = a.getDimensionPixelSize(R.styleable.XSingleTouchView_image_width, -1);
        float imageHeight = a.getDimensionPixelSize(R.styleable.XSingleTouchView_image_height, -1);

        if (imageWidth <= 0) {
            imageWidth = mXViewConfig.getCenterViewWidthMinSize();
        }

        if (imageHeight <= 0) {
            imageHeight = mXViewConfig.getCenterViewHeightMinSize();
        }

        //设置大小
        if (imageWidth > mXViewConfig.getCenterViewWidthMinSize() || imageHeight > mXViewConfig.getCenterViewHeightMinSize()) {
            mXViewConfig.setCenterViewSize(imageWidth, imageHeight);
        }

        //读取边缘线的宽度和颜色
        int lineWidth = a.getDimensionPixelSize(R.styleable.XSingleTouchView_line_width, -1);
        if (lineWidth > -1) {
            Paint paint = mXViewConfig.getLinePaint();
            if (paint == null) {
                paint = new Paint();
                //设置线宽
                paint.setStrokeWidth(lineWidth);
                //设置是空心的
                paint.setStyle(Paint.Style.STROKE);
                //防止锯齿
                paint.setAntiAlias(true);
                int lineColor = a.getColor(R.styleable.XSingleTouchView_line_color, -1);
                if (lineColor != -1) {
                    paint.setColor(lineColor);
                }
            }
            mXViewConfig.setLinePaint(paint);
        }

        a.recycle();
    }

    /**
     * 配置的类,根据这个配置的类来显示自身的效果
     */
    private XViewConfig mXViewConfig;

    /**
     * 获取配置
     *
     * @return
     */
    public XViewConfig getXViewConfig() {
        return mXViewConfig;
    }

    //====================================================自身的变量=============================================start

    /**
     * 画外围框的Path
     */
    private Path mPath = new Path();

    /**
     * 用于缩放，旋转，平移的矩阵
     */
    public Matrix matrix = new Matrix();

    //====================================================自身的变量=============================================end

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //计算自身宽高
        calculateViewSize();

        //布局自己
        adjustLayout();

        //计算为了绘制的数据
        computeAllDataForDraw(getLeft(), getTop());

        //拿到旋转的角度
        int rotateDegree = mXViewConfig.getAdsorbDegree();

        //如果大图存在就去绘制
        if (mXViewConfig.getCenterBitmap() != null) {
            //拿到大图的缩放比
            matrix.setScale(mCenterViewWidthScale, mCenterViewHeightScale);
            //设置偏移量
            matrix.postTranslate((mCenterPoint.x - getLeft()) - mXViewConfig.getCenterViewWidthSize() / 2,
                    (mCenterPoint.y - getTop()) - mXViewConfig.getCenterViewHeightSize() / 2);
            //设置旋转角度,根据中心点旋转
            matrix.postRotate(rotateDegree, mCenterPoint.x - getLeft(), mCenterPoint.y - getTop());
            //绘制主要的图片
            canvas.drawBitmap(mXViewConfig.getCenterBitmap(), matrix, null);
        }

        //如果被禁用了
        if (mXViewConfig.isDisable()) {
            return;
        }

        //绘制四个角的连线
        mPath.reset();
        mPath.moveTo(ltControlPoint.x, ltControlPoint.y);
        mPath.lineTo(rtControlPoint.x, rtControlPoint.y);
        mPath.lineTo(rbControlPoint.x, rbControlPoint.y);
        mPath.lineTo(lbControlPoint.x, lbControlPoint.y);
        mPath.lineTo(ltControlPoint.x, ltControlPoint.y);

        if (mXViewConfig.getLinePaint() != null) {
            mXViewConfig.getLinePaint().setAntiAlias(false);
            //绘制
            canvas.drawPath(mPath, mXViewConfig.getLinePaint());
        }

        //绘制四个控制点
        drawFourControl(canvas);

    }

    /**
     * 绘制四个控制点的图片
     * 图片可能大小和设定的大小不一致的,所以拿到图片的宽和高的缩放比,拿到绘制的左上角的点,就可以利用Matrix绘制了
     *
     * @param canvas 画布
     */
    private void drawFourControl(Canvas canvas) {

        if (mXViewConfig.isDisable()) {
            return;
        }

        //开始绘制四个控制点
        PointF cPoint = ltControlPoint;
        Matrix cMatrix = new Matrix();

        //拿到控制大小的一半
        float halfControlSize = mXViewConfig.getControlSize() / 2;
        //拿到绘制需要的旋转角度
        int rotateDegree = mXViewConfig.getAdsorbDegree();

        if (mXViewConfig.getLtControlBitmap() != null && !mXViewConfig.isLtControlDisable()) {
            //绘制左上角的控制点
            cMatrix.setScale(ltControlWidthScale, ltControlHeightScale);
            cMatrix.postTranslate(cPoint.x - halfControlSize, cPoint.y - halfControlSize);
            cMatrix.postRotate(rotateDegree, cPoint.x, cPoint.y);
            canvas.drawBitmap(mXViewConfig.getLtControlBitmap(), cMatrix, null);
        }

        if (mXViewConfig.getLbControlBitmap() != null && !mXViewConfig.isLbControlDisable()) {
            //绘制左下角的控制点
            cPoint = lbControlPoint;
            cMatrix.setScale(lbControlWidthScale, lbControlHeightScale);
            cMatrix.postTranslate(cPoint.x - halfControlSize, cPoint.y - halfControlSize);
            cMatrix.postRotate(rotateDegree, cPoint.x, cPoint.y);
            canvas.drawBitmap(mXViewConfig.getLbControlBitmap(), cMatrix, null);
        }

        if (mXViewConfig.getRtControlBitmap() != null && !mXViewConfig.isRtControlDisable()) {
            //绘制右上角的控制点
            cPoint = rtControlPoint;
            cMatrix.setScale(rtControlWidthScale, rtControlHeightScale);
            cMatrix.postTranslate(cPoint.x - halfControlSize, cPoint.y - halfControlSize);
            cMatrix.postRotate(rotateDegree, cPoint.x, cPoint.y);
            canvas.drawBitmap(mXViewConfig.getRtControlBitmap(), cMatrix, null);
        }

        if (mXViewConfig.getRbControlBitmap() != null && !mXViewConfig.isRbControlDisable()) {
            //绘制右下角的控制点
            cPoint = rbControlPoint;
            cMatrix.setScale(rbControlWidthScale, rbControlHeightScale);
            cMatrix.postTranslate(cPoint.x - halfControlSize, cPoint.y - halfControlSize);
            cMatrix.postRotate(rotateDegree, cPoint.x, cPoint.y);
            canvas.drawBitmap(mXViewConfig.getRbControlBitmap(), cMatrix, null);
        }

    }

    /**
     * 记录移动完成时候的角度,触摸的方法中用
     */
    private int mAngle;

    /**
     * 记录是否滑动了
     */
    private boolean isMove;

    @Override
    public boolean onTouchEvent(MotionEvent e) {

        if (mXViewConfig.isDisable()) {
            return super.onTouchEvent(e);
        }

        //拿到动作
        int action = e.getAction();

        switch (action) {

            case MotionEvent.ACTION_DOWN: //如果是按下

                isMove = false;

                //记录按下的点
                mPreMovePointF.set(e.getX(), e.getY());

                //记录按下时候的作用区域
                controlPosition = getTouchArea(mPreMovePointF);

                //如果是拖拽或者是没有操作,那么就不会去转换控制点
                if (controlPosition == Drag || controlPosition == Nothing) {

                } else {
                    //转换控制点
                    controlPosition = mXViewConfig.converse(controlPosition);
                }

                //记录当前的角度
                mAngle = MathUtil.getAngleFromPoint(new PointF(mCenterPoint.x - getLeft(), mCenterPoint.y - getTop()), mPreMovePointF);

                break;
            case MotionEvent.ACTION_MOVE: //如果是一个鼠标的移动

                isMove = true;

                //记录下移动中的点
                mCurMovePointF.set(e.getX(), e.getY());

                switch (controlPosition) {

                    case Scale: //缩放

                        float halfBitmapWidth = mXViewConfig.getCenterViewWidthSize() / 2;
                        float halfBitmapHeight = mXViewConfig.getCenterViewHeightSize() / 2;

                        //图片某个角的点到图片中心的距离
                        float bitmapToCenterDistance = (float) Math.sqrt(halfBitmapWidth * halfBitmapWidth + halfBitmapHeight * halfBitmapHeight);

                        //拿到一个相对于自己的中心点,每次绘制之后就会变的
                        PointF centerP = new PointF(mCenterPoint.x - getLeft(), mCenterPoint.y - getTop());

                        //移动的点到图片中心的距离
                        float moveToCenterDistance = MathUtil.distance4PointF(centerP, mCurMovePointF);

                        //计算缩放比例
                        float scale = moveToCenterDistance / bitmapToCenterDistance;

                        //计算自身的新的大小
                        float newWidthSize = mXViewConfig.getCenterViewWidthSize() * scale;
                        float newHeightSize = mXViewConfig.getCenterViewHeightSize() * scale;

                        if (newWidthSize > mXViewConfig.getCenterViewWidthMinSize() || newHeightSize > mXViewConfig.getCenterViewHeightMinSize()) {
                            //重新设置配置文件中的图片的大小
                            mXViewConfig.setCenterViewSize(newWidthSize, newHeightSize);
                            //重新绘制
                            postInvalidate();
                        }

                        break;

                    case Rotate: //旋转

                        PointF cp = new PointF(mCenterPoint.x - getLeft(), mCenterPoint.y - getTop());

                        //拿到现在的角度
                        int angle = MathUtil.getAngleFromPoint(cp, mCurMovePointF);

                        //设置旋转角度
                        boolean b = mXViewConfig.setRotateDegree(angle - mAngle + mXViewConfig.getRotateDegree());

                        //如果设置成功了
                        if (b) {
                            //记录现在的角度
                            mAngle = angle;
                        }

                        postInvalidate();

                        break;

                    case Drag: //拖拽

                        //是否拖拽被禁用了
                        boolean dragDisable = mXViewConfig.isDragDisable();

                        //如果被禁用了
                        if (!dragDisable) {
                            float dx = mCurMovePointF.x - mPreMovePointF.x;
                            float dy = mCurMovePointF.y - mPreMovePointF.y;

                            mCenterPoint.x += dx;
                            mCenterPoint.y += dy;

                            mPreMovePointF.set(mCurMovePointF);

                            mPreMovePointF.x -= dx;
                            mPreMovePointF.y -= dy;

                            postInvalidate();
                        }

                        break;

                }


                break;

            case MotionEvent.ACTION_UP: //如果是鼠标的抬起

                onClick(this);

                isMove = false;

                break;
        }

        return true;
    }

    /**
     * 调整View的大小，位置
     * 根据中心点和整个视图的宽和高
     */
    private void adjustLayout() {

        //计算出在父容器中自身控件的位置
        float left = (mCenterPoint.x - mViewWidth / 2);
        float top = (mCenterPoint.y - mViewHeight / 2);
        float right = (left + mViewWidth);
        float buttom = (top + mViewHeight);

        //因为很多值都是精确计算的,而确定自己的绘制范围却是需要四个int包围的区域
        //那么不可避免的就会遇到浮点数转化为int,但是这个界面里面的都是精确计算的坐标点
        //所以取整的时候,一定是往范围扩大的方向取,所以下面的取整
        int newLeft = (int) (left - 0.5);
        int newTop = (int) (top - 0.5);
        int newRight = (int) (right + 0.5);
        int newButtom = (int) (buttom + 0.5);

        //安排自己的位置
        layout(newLeft, newTop, newRight, newButtom);

    }

    /**
     * 自身的宽和高,包括了控制菜单的,不是属于可配置的一项
     */
    private float mViewWidth, mViewHeight;

    /**
     * 手指按下的时候的记录下来的点,这个点也会被转化为相对于父容器再来计算
     */
    private PointF mPreMovePointF = new PointF();

    /**
     * 手指移动的时候的点,这个点也会被转化为相对于父容器再来计算
     */
    private PointF mCurMovePointF = new PointF();

    /**
     * 手指按下的时候,触摸到了哪一个区域
     */
    private ControlPosition controlPosition;

    /**
     * 四个控制点的坐标,同时也是绘制核心图的区域
     */
    private PointF ltControlPoint = new PointF(), lbControlPoint = new PointF(), rtControlPoint = new PointF(), rbControlPoint = new PointF();

    /**
     * 四个控制点对应的宽度的bitmap的缩放比例
     */
    private float ltControlWidthScale = 1.0f, lbControlWidthScale = 1.0f, rtControlWidthScale = 1.0f, rbControlWidthScale = 1.0f;

    /**
     * 四个控制点对应的高度的bitmap的缩放比例
     */
    private float ltControlHeightScale = 1.0f, lbControlHeightScale = 1.0f, rtControlHeightScale = 1.0f, rbControlHeightScale = 1.0f;

    /**
     * 大图需要缩放的比例
     */
    private float mCenterViewWidthScale = 1.0f, mCenterViewHeightScale = 1.0f;

    /**
     * SingleTouchView的中心点坐标，相对于父容器而言的
     */
    private PointF mCenterPoint = null;

    /**
     * 计算所有的数据为了下一步的绘制
     */
    private void computeAllDataForDraw(int viewLeft, int viewTop) {

        float controlSize = mXViewConfig.getControlSize();
        //拿到控制菜单的一半大小
        float mHalfControlSize = controlSize / 2;

        //拿到中心点在自身控件中的坐标点
        float cpx = mCenterPoint.x - viewLeft;
        float cpy = mCenterPoint.y - viewTop;

        //拿到大图的真实大小
        float centerViewWidthSize = mXViewConfig.getCenterViewWidthSize();
        float centerViewHeightSize = mXViewConfig.getCenterViewHeightSize();
        //为了实现在缩放的时候,当宽或者高被限制的时候,这里显示的时候为限制的宽或者高,但是真实的值却在变化
        if (centerViewWidthSize < mXViewConfig.getCenterViewWidthMinSize()) {
            centerViewWidthSize = mXViewConfig.getCenterViewWidthMinSize();
        }
        if (centerViewHeightSize < mXViewConfig.getCenterViewHeightMinSize()) {
            centerViewHeightSize = mXViewConfig.getCenterViewHeightMinSize();
        }

        float left = cpx - ((centerViewWidthSize + controlSize) / 2);
        float right = cpx + ((centerViewWidthSize + controlSize) / 2);
        float top = cpy - ((centerViewHeightSize + controlSize) / 2);
        float bottom = cpy + ((centerViewHeightSize + controlSize) / 2);

        //计算四个控制点的坐标
        ltControlPoint.set(left + mHalfControlSize, top + mHalfControlSize);
        rtControlPoint.set(right - mHalfControlSize, top + mHalfControlSize);
        lbControlPoint.set(left + mHalfControlSize, bottom - mHalfControlSize);
        rbControlPoint.set(right - mHalfControlSize, bottom - mHalfControlSize);

        //拿到旋转的角度
        int degree = mXViewConfig.getAdsorbDegree();

        PointF cp = new PointF(cpx, cpy);

        //拿到新的旋转后的点
        ltControlPoint = MathUtil.rotateAccordingToPoint(cp, ltControlPoint, degree);
        rtControlPoint = MathUtil.rotateAccordingToPoint(cp, rtControlPoint, degree);
        lbControlPoint = MathUtil.rotateAccordingToPoint(cp, lbControlPoint, degree);
        rbControlPoint = MathUtil.rotateAccordingToPoint(cp, rbControlPoint, degree);

        //计算四个控制点的图片需要缩放的缩放比

        if (mXViewConfig.getLtControlBitmap() != null) {
            //左上角
            ltControlWidthScale = controlSize / mXViewConfig.getLtControlBitmap().getWidth();
            ltControlHeightScale = controlSize / mXViewConfig.getLtControlBitmap().getHeight();
        }

        if (mXViewConfig.getRtControlBitmap() != null) {
            //右上
            rtControlWidthScale = controlSize / mXViewConfig.getRtControlBitmap().getWidth();
            rtControlHeightScale = controlSize / mXViewConfig.getRtControlBitmap().getHeight();
        }

        if (mXViewConfig.getLbControlBitmap() != null) {
            //左下
            lbControlWidthScale = controlSize / mXViewConfig.getLbControlBitmap().getWidth();
            lbControlHeightScale = controlSize / mXViewConfig.getLbControlBitmap().getHeight();
        }

        if (mXViewConfig.getRbControlBitmap() != null) {
            //右下
            rbControlWidthScale = controlSize / mXViewConfig.getRbControlBitmap().getWidth();
            rbControlHeightScale = controlSize / mXViewConfig.getRbControlBitmap().getHeight();
        }

        if (mXViewConfig.getCenterBitmap() != null) {
            //计算大图需要缩放的比例
            mCenterViewWidthScale = mXViewConfig.getCenterViewWidthSize() / mXViewConfig.getCenterBitmap().getWidth();
            mCenterViewHeightScale = mXViewConfig.getCenterViewHeightSize() / mXViewConfig.getCenterBitmap().getHeight();
        }

    }

    /**
     * 获取自身的大小
     * 如果有图片就是：图片的宽高+两个半个的控制点的宽高 = 图片的宽高+一个的控制点的宽高
     * 数据存放在 mViewWidth 和 mViewHeight
     */
    private void calculateViewSize() {

        //拿到大图的真实大小
        float centerViewWidthSize = mXViewConfig.getCenterViewWidthSize();
        float centerViewHeightSize = mXViewConfig.getCenterViewHeightSize();
        //为了实现在缩放的时候,当宽或者高被限制的时候,这里显示的时候为限制的宽或者高,但是真实的值却在变化
        if (centerViewWidthSize < mXViewConfig.getCenterViewWidthMinSize()) {
            centerViewWidthSize = mXViewConfig.getCenterViewWidthMinSize();
        }
        if (centerViewHeightSize < mXViewConfig.getCenterViewHeightMinSize()) {
            centerViewHeightSize = mXViewConfig.getCenterViewHeightMinSize();
        }

        //拿到控制点的大小
        float controlSize = mXViewConfig.getControlSize();

        float left = -((centerViewWidthSize + controlSize) / 2);
        float right = ((centerViewWidthSize + controlSize) / 2);
        float top = -((centerViewHeightSize + controlSize) / 2);
        float bottom = +((centerViewHeightSize + controlSize) / 2);

        //这里为止计算出了没有旋转的时候自身的宽高,但是旋转角度之后会让自己宽高又有所变化,所以接下来就是建立直角坐标系算出旋转后的点

        PointF ltp = new PointF(left, top);
        PointF lbp = new PointF(left, bottom);
        PointF rtp = new PointF(right, top);
        PointF rbp = new PointF(right, bottom);

        //旋转中心点
        PointF cp = new PointF(0, 0);

        //拿到旋转的角度
        int degree = mXViewConfig.getAdsorbDegree();

        //拿到旋转后的点
        ltp = MathUtil.rotateAccordingToPoint(cp, ltp, degree);
        lbp = MathUtil.rotateAccordingToPoint(cp, lbp, degree);
        rtp = MathUtil.rotateAccordingToPoint(cp, rtp, degree);
        rbp = MathUtil.rotateAccordingToPoint(cp, rbp, degree);

        //拿到最大的坐标点,相减之后可以得到自身的宽和高
        float minX = Math.min(Math.min(ltp.x, lbp.x), Math.min(rtp.x, rbp.x));
        float maxX = Math.max(Math.max(ltp.x, lbp.x), Math.max(rtp.x, rbp.x));
        float minY = Math.min(Math.min(ltp.y, lbp.y), Math.min(rtp.y, rbp.y));
        float maxY = Math.max(Math.max(ltp.y, lbp.y), Math.max(rtp.y, rbp.y));

        //得出了自己的宽和高
        mViewWidth = (maxX - minX);
        mViewHeight = (maxY - minY);

    }

    /**
     * 控制点的位置,摸到了四个控制点、中间的图片、或者两者都不是的额外区域
     */
    public enum ControlPosition {
        Edit, Delete, Rotate, Scale, Drag, Nothing
    }

    /**
     * 获取触摸到的点是什么区域
     *
     * @return
     */
    public ControlPosition getTouchArea(PointF downP) {

        float controlSize = mXViewConfig.getControlSize();
        //拿到控制菜单的一半大小
        int mHalfControlSize = (int) controlSize / 2;

        float distance;

        if (mXViewConfig.getLtControlBitmap() != null && !mXViewConfig.isLtControlDisable()) {
            //计算这个点到每一个控制点的距离,如果小于半径,那么就表示点到了那个控制点
            distance = MathUtil.distance4PointF(ltControlPoint, downP);
            if (distance <= mHalfControlSize) {
                return Edit;
            }
        }

        if (mXViewConfig.getLbControlBitmap() != null && !mXViewConfig.isLbControlDisable()) {
            distance = MathUtil.distance4PointF(lbControlPoint, downP);
            if (distance <= mHalfControlSize) {
                return Rotate;
            }
        }

        if (mXViewConfig.getRbControlBitmap() != null && !mXViewConfig.isRbControlDisable()) {
            distance = MathUtil.distance4PointF(rbControlPoint, downP);
            if (distance <= mHalfControlSize) {
                return Scale;
            }
        }

        if (mXViewConfig.getRtControlBitmap() != null && !mXViewConfig.isRtControlDisable()) {
            distance = MathUtil.distance4PointF(rtControlPoint, downP);
            if (distance <= mHalfControlSize) {
                return Delete;
            }
        }

        //判断一个点都在矩形中
        boolean b = MathUtil.isPointInRect(ltControlPoint, rtControlPoint, rbControlPoint, lbControlPoint, downP);

        if (b) {
            //默认返回额外区域
            return Drag;
        } else {
            return Nothing;
        }

    }

    /**
     * 第一次点击的时间
     */
    private long firstClickTime;

    @Override
    public void onClick(View v) {

        //如果是关闭或者是编辑,就回调接口取编辑
        if (controlPosition == Delete || controlPosition == Edit) {
            if (mOnControlListener != null) {
                mOnControlListener.onSove(this, controlPosition);
            }
        } else { //如果不是删除和编辑的点击,那么就告诉外面点击事件生效啦
            if (!isMove) {

                //当前的时间
                long nowTime = System.currentTimeMillis();

                if (nowTime - firstClickTime < 500) {
                    if (mOnDbClickListener != null) {
                        mOnDbClickListener.onDbClick(v);
                    }
                    firstClickTime = 0;
                } else {
                    firstClickTime = nowTime;
                    //Toast.makeText(getContext(), "单击事件", Toast.LENGTH_SHORT).show();
                }

            }
        }

    }

    /**
     * 控制菜单的回调接口
     */
    private OnControlListener mOnControlListener;

    /**
     * 双击的回调接口
     */
    private OnDbClickListener mOnDbClickListener;

    /**
     * 设置控制菜单的监听
     *
     * @param mOnControlListener
     */
    public void setOnControlListener(OnControlListener mOnControlListener) {
        this.mOnControlListener = mOnControlListener;
    }

    /**
     * 设置双击事件监听
     *
     * @param onDbClickListener
     */
    public void setOnDbClickListener(OnDbClickListener onDbClickListener) {
        this.mOnDbClickListener = onDbClickListener;
    }

    /**
     * 控制点的监听回调
     */
    public interface OnControlListener {

        /**
         * 回调方法
         *
         * @param view
         * @param control
         */
        void onSove(XSingleTouchView view, ControlPosition control);

    }

    /**
     * 双击的监听接口
     */
    public interface OnDbClickListener {

        /**
         * 双击的回调方法
         *
         * @param v
         */
        void onDbClick(View v);
    }

}
