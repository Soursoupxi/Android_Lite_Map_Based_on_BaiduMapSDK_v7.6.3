package com.baidu.mapapi.overlayutil;

import android.graphics.Color;
import android.os.Bundle;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.route.IndoorRouteLine;
import com.baidu.mapapi.search.route.WalkingRouteLine;

import java.util.ArrayList;
import java.util.List;

public class IntegralRouteOverlay extends OverlayManager {

    private List<WalkingRouteLine> mRouteLineList;
    private IndoorRouteLine mIndoorRouteLine = null;
    int[] colorInfo;

    public IntegralRouteOverlay(BaiduMap baiduMap) {
        super(baiduMap);
        colorInfo = new int[]{
                Color.argb(178, 88, 78, 255),
                Color.argb(178, 88, 208, 0),
                Color.argb(178, 88, 78, 255)
        };
    }

    /**
     * 设置室外路线数据。
     *
     * @param lines
     *            路线数据
     */
    public void setData(List<WalkingRouteLine> lines) {
        mRouteLineList = lines;
    }

    /**
     * 设置室内路线数据。
     *
     * @param line
     *            路线数据
     */
    public void setData(IndoorRouteLine line) {
        mIndoorRouteLine = line;
    }

    @Override
    public final List<OverlayOptions> getOverlayOptions() {
        if (mRouteLineList == null && mIndoorRouteLine == null) {
            return null;
        }
        List<OverlayOptions> overlayList = new ArrayList<OverlayOptions>();
        drawOutDoor(overlayList);
        drawStartAndEnd(overlayList);
        drawIndoor(overlayList);

        return overlayList;
    }

    private void drawOutDoor(List<OverlayOptions> overlayList) {
        if (mRouteLineList == null || mRouteLineList.size() < 1) {
            return;
        }
        for (WalkingRouteLine routeLine : mRouteLineList) {
            if (routeLine.getAllStep() != null && routeLine.getAllStep().size() > 0) {
                for (WalkingRouteLine.WalkingStep step : routeLine.getAllStep()) {
                    Bundle b = new Bundle();
                    b.putInt("index", routeLine.getAllStep().indexOf(step));
                    if (step.getEntrance() != null) {
                        overlayList.add((new MarkerOptions())
                                .position(step.getEntrance().getLocation())
                                .rotate((360 - step.getDirection()))
                                .zIndex(10)
                                .anchor(0.5f, 0.5f)
                                .extraInfo(b)
                                .icon(BitmapDescriptorFactory
                                        .fromAssetWithDpi("Icon_line_node.png")));
                    }

                    // 最后路段绘制出口点
                    if (routeLine.getAllStep().indexOf(step) == (routeLine
                            .getAllStep().size() - 1) && step.getExit() != null) {
                        MarkerOptions markerOptions = (new MarkerOptions())
                                .position(step.getExit().getLocation())
                                .anchor(0.5f, 0.5f)
                                .zIndex(10)
                                .icon(BitmapDescriptorFactory
                                        .fromAssetWithDpi("Icon_line_node.png"));
                        if (mRouteLineList.indexOf(routeLine) != mRouteLineList.size() - 1) {
                            markerOptions.rotate((360 - step.getDirection()));
                        }
                        overlayList.add(markerOptions);
                    }
                }
            }
            // poly line list
            if (routeLine.getAllStep() != null
                    && routeLine.getAllStep().size() > 0) {
                LatLng lastStepLastPoint = null;
                for (WalkingRouteLine.WalkingStep step : routeLine.getAllStep()) {
                    List<LatLng> watPoints = step.getWayPoints();
                    if (watPoints != null) {
                        List<LatLng> points = new ArrayList<LatLng>();
                        if (lastStepLastPoint != null) {
                            points.add(lastStepLastPoint);
                        }
                        points.addAll(watPoints);
                        overlayList.add(new PolylineOptions().points(points).width(10)
                                .color(getLineColor() != 0 ? getLineColor() : Color.argb(178, 0, 78, 255)).zIndex(0));
                        lastStepLastPoint = watPoints.get(watPoints.size() - 1);
                    }
                }
            }
        }
    }

    private void drawStartAndEnd(List<OverlayOptions> overlayList) {
        RouteLine routeLine;
        if (mRouteLineList != null && mRouteLineList.size() > 0) {
            routeLine = mRouteLineList.get(0);
        } else if (mIndoorRouteLine != null) {
            routeLine = mIndoorRouteLine;
        } else {
            return;
        }
        // starting
        if (routeLine.getStarting() != null) {
            overlayList.add((new MarkerOptions())
                    .position(routeLine.getStarting().getLocation())
                    .icon(getStartMarker() != null ? getStartMarker() :
                            BitmapDescriptorFactory
                                    .fromAssetWithDpi("Icon_start.png")).zIndex(10));
        }
        // terminal
        if (routeLine.getTerminal() != null) {
            overlayList
                    .add((new MarkerOptions())
                            .position(routeLine.getTerminal().getLocation())
                            .icon(getTerminalMarker() != null ? getTerminalMarker() :
                                    BitmapDescriptorFactory
                                            .fromAssetWithDpi("Icon_end.png"))
                            .zIndex(10));
        }
    }

    private void drawIndoor(List<OverlayOptions> overlayList) {
        if (mIndoorRouteLine == null) {
            return;
        }
        // 添加step的节点
        if (mIndoorRouteLine.getAllStep() != null && mIndoorRouteLine.getAllStep().size() > 0) {
            for (IndoorRouteLine.IndoorRouteStep step : mIndoorRouteLine.getAllStep()) {
                Bundle b = new Bundle();
                b.putInt("index", mIndoorRouteLine.getAllStep().indexOf(step));
                if (step.getEntrace() != null) {
                    overlayList.add((new MarkerOptions()).position(step.getEntrace().getLocation())
                            .zIndex(10).anchor(0.5f, 0.5f).extraInfo(b)
                            .icon(BitmapDescriptorFactory.fromAssetWithDpi("Icon_walk_route.png")));
                }
                // 最后路段绘制出口点
                if (mIndoorRouteLine.getAllStep().indexOf(step) == (mIndoorRouteLine.getAllStep().size() - 1)
                        && step.getExit() != null) {
                    overlayList.add((new MarkerOptions()).position(step.getExit().getLocation()).anchor(0.5f, 0.5f)
                            .zIndex(10).icon(BitmapDescriptorFactory.fromAssetWithDpi("Icon_walk_route.png")));

                }
            }
        }
        // 添加线poly line list
        if (mIndoorRouteLine.getAllStep() != null && mIndoorRouteLine.getAllStep().size() > 0) {
            LatLng lastStepLastPoint = null;
            int idex = 0;
            for (IndoorRouteLine.IndoorRouteStep step : mIndoorRouteLine.getAllStep()) {
                List<LatLng> watPoints = step.getWayPoints();
                if (watPoints != null) {
                    List<LatLng> points = new ArrayList<LatLng>();
                    if (lastStepLastPoint != null) {
                        points.add(lastStepLastPoint);
                    }
                    points.addAll(watPoints);
                    overlayList.add(new PolylineOptions().points(points).width(10)
                            .color(getLineColor() != 0 ? getLineColor() : colorInfo[idex++ % 3]).zIndex(0));
                    lastStepLastPoint = watPoints.get(watPoints.size() - 1);
                }
            }
        }
    }

    public int getLineColor() {
        return 0;
    }

    /**
     * 覆写此方法以改变默认起点图标
     *
     * @return 起点图标
     */
    public BitmapDescriptor getStartMarker() {
        return null;
    }

    /**
     * 覆写此方法以改变默认终点图标
     *
     * @return 终点图标
     */
    public BitmapDescriptor getTerminalMarker() {
        return null;
    }

    @Override
    public final boolean onMarkerClick(Marker marker) {
        return true;
    }

    @Override
    public boolean onPolylineClick(Polyline polyline) {
        return false;
    }
}
