package com.baidu.mapapi.overlayutil;

import android.os.Bundle;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.search.poi.PoiResult;
import com.example.android.basicmap.R;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * 用于显示poi的overly
 */
public class PoiOverlay extends OverlayManager {

    private static final int MAX_POI_SIZE = 10;

    private PoiResult mPoiResult = null;

    /**
     * 构造函数
     * 
     * @param baiduMap   该 PoiOverlay 引用的 BaiduMap 对象
     */
    public PoiOverlay(BaiduMap baiduMap) {
        super(baiduMap);
    }

    /**
     * 设置POI数据
     * 
     * @param poiResult    设置POI数据
     */
    public void setData(PoiResult poiResult) {
        this.mPoiResult = poiResult;
    }

//在PoiOverlay.class中直接修改方法public final List<OverlayOptions> getOverlayOptions(){}
//百度地图原来提供的图标命名是icon_marka/b/c/d.png我自己改成了icon_mark1/2/3/4.png,方便更改icon值而已

    @Override
    public final List<OverlayOptions> getOverlayOptions() {
        if (mPoiResult == null || mPoiResult.getAllPoi() == null) {
            return null;
        }

        List<OverlayOptions> markerList = new ArrayList<>();
        int index = 0;
        for (int i = 0; i < mPoiResult.getAllPoi().size() && index < MAX_POI_SIZE; i++) {
            if (mPoiResult.getAllPoi().get(i).location == null) {
                continue;
            }

            String iconIndex = ++index + "";
            int icon = getResId("icon_mark" + iconIndex, R.drawable.class);
            Bundle bundle = new Bundle();
            bundle.putInt("index", i);
            markerList.add(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(icon))
                    .extraInfo(bundle)
                    .position(mPoiResult.getAllPoi().get(i).location));
        }

        return markerList;
    }
    private static int getResId(String variableName, Class<?> c) {
        try {
            Field idField = c.getDeclaredField(variableName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }


    /**
     * 获取该PoiOverlay的poi数据
     * 
     * @return     POI数据
     */
    public PoiResult getPoiResult() {
        return mPoiResult;
    }

    /**
     * 覆写此方法以改变默认点击行为
     * 
     * @param i    被点击的poi在
     *             {@link com.baidu.mapapi.search.poi.PoiResult#getAllPoi()} 中的索引
     * @return     true--事件已经处理，false--事件未处理
     */
    public boolean onPoiClick(int i) {
//        if (mPoiResult.getAllPoi() != null
//                && mPoiResult.getAllPoi().get(i) != null) {
//            Toast.makeText(BMapManager.getInstance().getContext(),
//                    mPoiResult.getAllPoi().get(i).name, Toast.LENGTH_LONG)
//                    .show();
//        }
        return false;
    }

    @Override
    public final boolean onMarkerClick(Marker marker) {
        if (!mOverlayList.contains(marker)) {
            return false;
        }

        if (marker.getExtraInfo() != null) {
            return onPoiClick(marker.getExtraInfo().getInt("index"));
        }

        return false;
    }

    @Override
    public boolean onPolylineClick(Polyline polyline) {
        return false;
    }
}
