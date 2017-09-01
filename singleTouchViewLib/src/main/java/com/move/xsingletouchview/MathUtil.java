package com.move.xsingletouchview;

import android.graphics.PointF;

/**
 * Created by cxj on 2017/2/16.
 * 有关数学的计算
 */
public class MathUtil {

    /**
     * 根据一个点获取这个点在直角坐标系中的角度
     * 此方法是完全根据标准的直角坐标系来的
     * Java和android思路通用
     * 但是得到的值是60,如果是平常的坐标系那就是第一现象
     * Android里面其实也是第一现象(平常的第四象限),换算成平常的坐标系就是第4象限的
     * 60在android里面得出来的那就是平常的-60
     *
     * @param cp
     * @param p
     * @return
     */
    public static int getAngleFromPoint(PointF cp, PointF p) {

        double degrees = Math.toDegrees(Math.atan((p.y - cp.y) / (p.x - cp.x)));

        if (p.x - cp.x < 0) {
            degrees += 180;
        }

        if (degrees < 0) {
            degrees += 360;
        }

        return (int) degrees;
    }

    /**
     * 计算出根据中心点cp,从p1到p2的夹角
     * cp是顶点,p1起始个一端,p2是末端
     *
     * @param cp
     * @param p1
     * @param p2
     * @return
     */
    public static int getRotateAngle(PointF cp, PointF p1, PointF p2) {
        //返回夹角
        return getAngleFromPoint(cp, p2) - getAngleFromPoint(cp, p1);
    }

    /**
     * 判断一个点是不是在一个矩形内,使用的时候需要注意哦
     * 四个点必须是顺时针或者逆时针的顺序传给此方法中,不然不管用哦
     * Java和android思路通用
     *
     * @param rectP1
     * @param rectP2
     * @param rectP3
     * @param rectP4
     * @param downP  需要判断的点
     * @return
     */
    public static boolean isPointInRect(PointF rectP1, PointF rectP2, PointF rectP3, PointF rectP4, PointF downP) {

        //计算矩形的面积的一半,也就是其中三个点的三角形的面积
        double triangleArea = MathUtil.getTriangleArea(rectP1, rectP2, rectP3);

        //然后验证矩形的每两个点和触摸的点的三角形面积是否每一个都小于等于上一步计算出来的面积,如果是就是在矩形内,反之就是在矩形外
        double triangleArea1 = MathUtil.getTriangleArea(rectP1, rectP2, downP);
        if (triangleArea1 >= triangleArea) {
            return false;
        }
        double triangleArea2 = MathUtil.getTriangleArea(rectP2, rectP3, downP);
        if (triangleArea2 >= triangleArea) {
            return false;
        }
        double triangleArea3 = MathUtil.getTriangleArea(rectP3, rectP4, downP);
        if (triangleArea3 >= triangleArea) {
            return false;
        }
        double triangleArea4 = MathUtil.getTriangleArea(rectP4, rectP1, downP);
        if (triangleArea4 >= triangleArea) {
            return false;
        }
        return true;
    }

    /**
     * 已经知道了三个点的坐标获取三角形的面积
     * Java和android思路通用
     *
     * @param p1
     * @param p2
     * @param p3
     * @return
     */
    public static double getTriangleArea(PointF p1, PointF p2, PointF p3) {
        float d1 = distance4PointF(p1, p2);
        float d2 = distance4PointF(p1, p3);
        float d3 = distance4PointF(p2, p3);
        float s = (d1 + d2 + d3) / 2;
        return Math.sqrt(s * (s - d1) * (s - d2) * (s - d3));
    }

    /**
     * 两个点之间的距离
     * Java和android思路通用
     *
     * @param pf1
     * @param pf2
     * @return
     */
    public static float distance4PointF(PointF pf1, PointF pf2) {
        float disX = pf2.x - pf1.x;
        float disY = pf2.y - pf1.y;
        return (float) Math.sqrt(disX * disX + disY * disY);
    }


    /**
     * 一个点绕着另一个点做一定角度的旋转得到一个新的点的坐标
     * 这个方法就是针对android中的坐标系建立的,请放心使用
     * user for Android
     *
     * @param centerP 旋转中心点
     * @param p       需要旋转的点
     * @param angle   旋转的角度
     * @return
     */
    public static PointF rotateAccordingToPoint(PointF centerP, PointF p, int angle) {

        float centerX = centerP.x;
        float centerY = centerP.y;

        //y轴反一下
        centerY = -centerY;

        float px = p.x, py = p.y;
        //y轴反一下
        py = -py;
        //拿到弧度的值
        double k = Math.toRadians(angle);
        //利用公式算出坐标点
        float newPx = (float) (centerX + (px - centerX) * Math.cos(k) + (py - centerY) * Math.sin(k));
        float newPy = (float) (centerY - (px - centerX) * Math.sin(k) + (py - centerY) * Math.cos(k));

        //最终得到的值也反一下
        newPy = -newPy;

        return new PointF(newPx, newPy);
    }


}


