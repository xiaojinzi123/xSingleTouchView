package com.move.xsingletouchview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.support.annotation.Nullable;

/**
 * Created by cxj on 2017/3/15.
 * 多功能View的配置的类,但是这里定义的都是为了显示在或者和主要绘制工作没有什么关系的变量
 * 比如限制的一些常量,边界值,四个控制点的图,这些都是非核心数据
 */
public class XBaseConfig {

    /**
     * 控制点的大小
     */
    protected float mControlSize;

    /**
     * 控制点的缩放比
     */
    protected float mControlSizeScale = 1.0f;

    /**
     * 默认的中间的图的最小值
     */
    protected float mCenterViewWidthMinSize, mCenterViewHeightMinSize;

    public XBaseConfig(Context context) {

        float scale = context.getResources().getDisplayMetrics().density;

        //转化dp单位的默认值为px单位的
        mControlSize = DEFAULT_CONTROL_SIZE * scale + 0.5f;

        mCenterViewWidthMinSize = DEFAULT_CENTER_VIEW_WIDTH_MIN_SIZE * scale + 0.5f;
        mCenterViewHeightMinSize = DEFAULT_CENTER_VIEW_HEIGHT_MIN_SIZE * scale + 0.5f;
    }

    /**
     * 标志-1为空的意思
     */
    public static final int NULL = -1;

    /**
     * 默认的控制按钮的大小,单位是dp
     */
    private final int DEFAULT_CONTROL_SIZE = 24;

    //默认的中间的图显示最小的大小,单位是dp
    private final int DEFAULT_CENTER_VIEW_WIDTH_MIN_SIZE = 48;
    private final int DEFAULT_CENTER_VIEW_HEIGHT_MIN_SIZE = 48;

    //默认的中间的图显示的大小,单位是dp
    public static final int DEFAULT_CENTER_VIEW_WIDTH_SIZE = 200;
    public static final int DEFAULT_CENTER_VIEW_HEIGHT_SIZE = 200;

    /**
     * 默认的中间的图的最大值
     */
    protected float mCenterViewWidthMaxSize = NULL, mCenterViewHeightMaxSize = NULL;

    /**
     * 四个控制点对应的bitmap,用于缩放,旋转,编辑,删除的图标
     */
    protected Bitmap ltControlBitmap, lbControlBitmap, rtControlBitmap, rbControlBitmap;

    /**
     * 绘制线条的画笔
     */
    protected Paint linePaint;

    /**
     * 是否可以编辑,这个是控制整个是否可以编辑的
     */
    protected boolean disable;

    /**
     * 四个控制菜单是否被禁用
     */
    protected boolean ltControlDisable, lbControlDisable, rtControlDisable, rbControlDisable;

    /**
     * 是否拖拽被禁用了
     */
    protected boolean isDragDisable;

    public Paint getLinePaint() {
        return linePaint;
    }

    /**
     * 设置边框的画笔
     *
     * @param linePaint
     */
    public void setLinePaint(@Nullable Paint linePaint) {
        this.linePaint = linePaint;
        if (linePaint != null) {
            linePaint.setStyle(Paint.Style.STROKE);
        }
    }

    public boolean isDragDisable() {
        return isDragDisable;
    }

    public void setDragDisable(boolean dragDisable) {
        isDragDisable = dragDisable;
    }

    public boolean isDisable() {
        return disable;
    }

    public void setDisable(boolean disable) {
        this.disable = disable;
    }

    public boolean isLtControlDisable() {
        return ltControlDisable;
    }

    public void setLtControlDisable(boolean ltControlDisable) {
        this.ltControlDisable = ltControlDisable;
    }

    public boolean isLbControlDisable() {
        return lbControlDisable;
    }

    public void setLbControlDisable(boolean lbControlDisable) {
        this.lbControlDisable = lbControlDisable;
    }

    public boolean isRtControlDisable() {
        return rtControlDisable;
    }

    public void setRtControlDisable(boolean rtControlDisable) {
        this.rtControlDisable = rtControlDisable;
    }

    public boolean isRbControlDisable() {
        return rbControlDisable;
    }

    public void setRbControlDisable(boolean rbControlDisable) {
        this.rbControlDisable = rbControlDisable;
    }

    public Bitmap getLtControlBitmap() {
        return ltControlBitmap;
    }

    public void setLtControlBitmap(Bitmap ltControlBitmap) {
        this.ltControlBitmap = ltControlBitmap;
    }

    public Bitmap getLbControlBitmap() {
        return lbControlBitmap;
    }

    public void setLbControlBitmap(Bitmap lbControlBitmap) {
        this.lbControlBitmap = lbControlBitmap;
    }

    public Bitmap getRtControlBitmap() {
        return rtControlBitmap;
    }

    public void setRtControlBitmap(Bitmap rtControlBitmap) {
        this.rtControlBitmap = rtControlBitmap;
    }

    public Bitmap getRbControlBitmap() {
        return rbControlBitmap;
    }

    public void setRbControlBitmap(Bitmap rbControlBitmap) {
        this.rbControlBitmap = rbControlBitmap;
    }

    public float getControlSize() {
        return mControlSize * mControlSizeScale;
    }

    public void setControlSize(float mControlSize) {
        this.mControlSize = mControlSize;
    }

    public float getmControlSizeScale() {
        return mControlSizeScale;
    }

    public void setmControlSizeScale(float mControlSizeScale) {
        this.mControlSizeScale = mControlSizeScale;
    }

    public float getCenterViewWidthMinSize() {
        return mCenterViewWidthMinSize;
    }

    public void setCenterViewWidthMinSize(float mCenterViewWidthMinSize) {
        this.mCenterViewWidthMinSize = mCenterViewWidthMinSize;
    }

    public float getCenterViewHeightMinSize() {
        return mCenterViewHeightMinSize;
    }

    public void setCenterViewHeightMinSize(float mCenterViewHeightMinSize) {
        this.mCenterViewHeightMinSize = mCenterViewHeightMinSize;
    }
}
