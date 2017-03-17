package com.move.xsingletouchview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PointF;

/**
 * Created by cxj on 2017/2/15.
 * 多功能View的配置类,当操作多功能View的时候会时时改变此类中的数据
 * 这个类是为了多功能View能正常显示的一个配置文件,几乎记录了所有有关多功能View的数据
 * <p>
 * 这个配置文件中定义的都是完成绘制必须需要的参数,也就是绘制所需要的核心参数
 * 为了实习和最终绘制无关但是和设计的时候有关的效果的参数全部放在了父类中
 * 然后此类是描述一个设计元素的核心数据,但是他有好几种分类,所以这里有一个
 * 变量是type,区别是什么类型,并且每一种类型都是继承了此类,拓展记录更多的变量
 */
public class XViewConfig extends XBaseConfig {


    /**
     * 构造函数
     *
     * @param context 上下文
     */
    public XViewConfig(Context context) {
        //默认是不可编辑的
        this(context, false);
    }

    /**
     * 构造函数
     *
     * @param context
     * @param disable 是否被禁用,true表示啥都不能操作,就显示了图
     */
    public XViewConfig(Context context, boolean disable) {

        super(context);

        float scale = context.getResources().getDisplayMetrics().density;
        //转化dp单位的默认值为px单位的
        mCenterViewWidthSize = DEFAULT_CENTER_VIEW_WIDTH_SIZE * scale + 0.5f;
        mCenterViewHeightSize = DEFAULT_CENTER_VIEW_HEIGHT_SIZE * scale + 0.5f;

        this.disable = disable;

    }


    //===============================非核心数据,只供判别==========================

    /**
     * 这是一个图片的设计
     */
    public static final int DESIGN_IMAGE = 1;

    /**
     * 这是一个贴纸的设计
     */
    public static final int DESIGN_DECALS = 2;

    /**
     * 这是一个文字的设计
     */
    public static final int DESIGN_TEXT = 3;

    /**
     * 1表示图片类型的图案
     * 2表示贴纸类型的
     * 3表示文字类型的图案
     * 默认是一个图片类型的设计
     */
    private int designType = DESIGN_IMAGE;

    //===============================非核心数据,只供判别==========================

    /**
     * 中间显示的图的大小,*********************核心数据*********************
     */
    private float mCenterViewWidthSize, mCenterViewHeightSize;


    //===================================以上是显示的大小=========================================

    /**
     * 中间显示的图,*********************核心数据*********************
     */
    private Bitmap mCenterBitmap;

    /**
     * SingleTouchView的中心点坐标，相对于父容器而言的
     * *********************核心数据*********************
     */
    private PointF mCenterPoint = new PointF();

    /**
     * 图片的旋转角度,效果实现的时候会把控制菜单等所有的都旋转了
     * 用于计算真实值
     * *********************核心数据*********************
     */
    private int mRotateDegree;

    /**
     * 产生吸附作用的角度,用于绘制,但是不能用于计算,当输出大图的时候需要用这个绘制哦
     * 因为看到的效果和最终绘制出来的一定要相同,而不能用上面的那个真实值
     * *********************核心数据*********************
     */
    private int mAdsorbDegree;


    //===================================以下是setter和getter方法=========================================


    public int getDesignType() {
        return designType;
    }

    public void setDesignType(int designType) {
        this.designType = designType;
    }

    public PointF getCenterPoint() {
        return mCenterPoint;
    }

    //这个方法和下面那个方法获取到的自身宽高的值一定是真实的值
    public float getCenterViewWidthSize() {
        return mCenterViewWidthSize;
    }

    //这个方法和上面那个方法获取到的自身宽高的值一定是真实的值
    public float getCenterViewHeightSize() {
        return mCenterViewHeightSize;
    }

    /**
     * 设置大图的大小,具有限制,所有可以在这里产生效果图上的限制大小的效果
     *
     * @param mCenterViewWidthSize
     * @param mCenterViewHeightSize
     */
    public void setCenterViewSize(float mCenterViewWidthSize, float mCenterViewHeightSize) {

        if (NULL != mCenterViewWidthMaxSize && mCenterViewWidthSize > mCenterViewWidthMaxSize) {
            return;
        }
        if (NULL != mCenterViewHeightMaxSize && mCenterViewWidthSize > mCenterViewHeightMaxSize) {
            return;
        }

        this.mCenterViewWidthSize = mCenterViewWidthSize;
        this.mCenterViewHeightSize = mCenterViewHeightSize;
    }


    public Bitmap getCenterBitmap() {
        return mCenterBitmap;
    }

    public void setCenterBitmap(Bitmap mCenterBitmap) {
        this.mCenterBitmap = mCenterBitmap;
    }

    public int getRotateDegree() {
        return mRotateDegree;

    }

    /**
     * 设置旋转的角度
     *
     * @param mRotateDegree
     * @return
     */
    public boolean setRotateDegree(int mRotateDegree) {

        //不改变角度的真实值，因为角度加减360不会改变原来的数据表达的意思,所以这里就是对角度控制在0-360之间
        mRotateDegree = mRotateDegree % 360;
        if (mRotateDegree < 0) {
            mRotateDegree += 360;
        }
        //记录真实的值
        this.mRotateDegree = mRotateDegree;

        //靠近水平或者竖直的时候的吸附角度
        int adsorbDegree = 4;

        if (mRotateDegree >= -adsorbDegree && mRotateDegree <= adsorbDegree) {
            mRotateDegree = 0;
        }

        if (mRotateDegree >= 90 - adsorbDegree && mRotateDegree <= 90 + adsorbDegree) {
            mRotateDegree = 90;
        }

        if (mRotateDegree >= 180 - adsorbDegree && mRotateDegree <= 180 + adsorbDegree) {
            mRotateDegree = 180;
        }

        if (mRotateDegree >= 270 - adsorbDegree && mRotateDegree <= 270 + adsorbDegree) {
            mRotateDegree = 270;
        }

        if (mRotateDegree >= 360 - adsorbDegree && mRotateDegree <= 360 + adsorbDegree) {
            mRotateDegree = 0;
        }

        this.mAdsorbDegree = mRotateDegree;

        return true;
    }

    public int getAdsorbDegree() {
        return mAdsorbDegree;
    }

    /**
     * 转化控制点,如果需要改变控制点的顺序,比如你想要删除控制点在左下角,那么你对传入的
     * 值可以做一个判断,如果传入的是左下角的控制点,你可以返回右上角,那么就被你置换过来了
     *
     * @param controlPosition
     * @return
     */
    public XSingleTouchView.ControlPosition converse(XSingleTouchView.ControlPosition controlPosition) {
        if (controlPosition == XSingleTouchView.ControlPosition.Delete) {
            //return XSingleTouchView.ControlPosition.Rotate;
        }
        return controlPosition;
    }

}
