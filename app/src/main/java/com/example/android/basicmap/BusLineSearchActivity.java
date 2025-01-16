package com.example.android.basicmap;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.overlayutil.BusLineOverlay;
import com.baidu.mapapi.search.busline.BusLineResult;
import com.baidu.mapapi.search.busline.BusLineSearch;
import com.baidu.mapapi.search.busline.BusLineSearchOption;
import com.baidu.mapapi.search.busline.OnGetBusLineSearchResultListener;

import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;

import java.util.ArrayList;
import java.util.List;

public class BusLineSearchActivity extends BaseActivity
{
    private EditText myCity;
    private EditText myKey;
    private Button mySearchBus;
    private Button myReverseLine;
    private Button myPre;
    private Button myNext;
    private PoiSearch myPoiSearch; //公交线路查询要用到Poi检索，故要与PoiSearch对象配合使用
    private BusLineSearch myBusLineSearch; //BusLineSearch对象用于公交线路查询，需要声明和实例化
    private List<String> busLineIDList=null;
    private BusLineResult route=null; //保存公交线路数据的变量，供浏览节点时使
    private int nodeIndex=-2; //节点索引，供浏览节点时使用
    private int busLineIndex=0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        SDKInitializer.setAgreePrivacy(getApplicationContext(),true);
        SDKInitializer.initialize(getApplicationContext());

        setContentView(R.layout.buslinesearch);

        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //获取地图控件引用
        myMap=(MapView)findViewById(R.id.bmapView);
        myBaiduMap=myMap.getMap();
        //初始化
        findViews();
        myPoiSearch=PoiSearch.newInstance();
        myBusLineSearch=BusLineSearch.newInstance();
        busLineIDList=new ArrayList<String>();

        //单击“公交线路查询”按钮的事件方法
        mySearchBus.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                busLineIDList.clear();
                busLineIndex=0;
                myPre.setVisibility(View.INVISIBLE);
                myNext.setVisibility(View.INVISIBLE);
                //发起poi检索，从得到所有poi中找到公交线路类型的poi，再使用该poi的uid进行公交详情搜索
                myPoiSearch.searchInCity((new PoiCitySearchOption()).city(myCity.getText().toString()).keyword(myKey.getText().toString()).scope(2));
            }
        });

        //单击“≒”按钮的事件方法
        myReverseLine.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(busLineIndex>=busLineIDList.size())
                {
                    busLineIndex=0;
                }
                if(busLineIndex>=0 && busLineIndex<busLineIDList.size() && busLineIDList.size()>0)
                { //查询给定索引的公交路线
                    myBusLineSearch.searchBusLine((new BusLineSearchOption().city(myCity.getText().toString()).uid(busLineIDList.get(busLineIndex))));
                    ++busLineIndex;
                }
            }
        });

        //回调方法获取并处理Poi检索的结果
        myPoiSearch.setOnGetPoiSearchResultListener(new OnGetPoiSearchResultListener()
        {
            @Override
            public void onGetPoiResult(PoiResult poiResult)
            {
                if(poiResult.error==SearchResult.ERRORNO.NO_ERROR)
                { //遍历所有poi，找到类型为公交路线的poi
                    busLineIDList.clear();
                    System.err.println("公交Tag:"+PoiInfo.POITYPE.BUS_LINE);
                    System.err.println("地铁Tag:"+PoiInfo.POITYPE.SUBWAY_LINE);
                    for(PoiInfo poi:poiResult.getAllPoi())
                    {
                        System.err.println("当前Tag:"+poi.getPoiDetailInfo().getTag());

                        if(poi.getPoiDetailInfo().getTag().equals("公交线路;普通日行公交车") || poi.getPoiDetailInfo().getTag().equals("地铁线路"))
                        {
                            busLineIDList.add(poi.uid); //添加进公交线路列表
                        }
                    }
                    myReverseLine.performClick();
                    route=null;
                }
            }

            @Override
            public void onGetPoiDetailResult(PoiDetailSearchResult poiDetailSearchResult)
            {}

            @Override
            public void onGetPoiDetailResult(PoiDetailResult poiDetailResult)
            {}

            @Override
            public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult)
            {}
        });

        //回调方法获取并处理公交线路检索的结果
        myBusLineSearch.setOnGetBusLineSearchResultListener(new OnGetBusLineSearchResultListener()
        {
            @Override
            public void onGetBusLineResult(BusLineResult busLineResult)
            {
                if(busLineResult.error==SearchResult.ERRORNO.NO_ERROR)
                {
                    myBaiduMap.clear();
                    route=busLineResult;
                    nodeIndex=-1;
                    BusLineOverlay overlay=new BusLineOverlay(myBaiduMap);
                    myBaiduMap.setOnMarkerClickListener(overlay);
                    overlay.removeFromMap();
                    overlay.setData(busLineResult);
                    overlay.addToMap();
                    overlay.zoomToSpan();
                    myPre.setVisibility(View.VISIBLE);
                    myNext.setVisibility(View.VISIBLE);
                    Toast.makeText(BusLineSearchActivity.this,busLineResult.getBusLineName(),Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    //单击前后箭头按钮浏览节点（公交站点）详细信息
    public void onNodeClick(View v)
    {
        System.err.println("已触碰站点按钮");
        if(nodeIndex<-1 || route==null || nodeIndex>=route.getStations().size())
        {
            System.err.println("站点索引存在问题，参数为nodeIndex:"+nodeIndex);
            return;
        }
        TextView popupText=new TextView(this);
        popupText.setBackgroundResource(R.drawable.popup);
        popupText.setTextColor(0xFF000000);
        System.err.println("已加载气泡参数。当前v.getId()参数为"+v.getId());
        if(v.getId()==R.id.myButtonPre && nodeIndex>0)
        { //上一个节点
            --nodeIndex;
        }
        if(v.getId()==R.id.myButtonNext && nodeIndex<(route.getStations().size()-1))
        { //下一个节点
            ++nodeIndex;
        }
        if(nodeIndex>=0)
        { //移动到指定索引坐标
            myBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(route.getStations().get(nodeIndex).getLocation()));
            popupText.setText(route.getStations().get(nodeIndex).getTitle()); //弹出信息气泡
            myBaiduMap.showInfoWindow(new InfoWindow(popupText,route.getStations().get(nodeIndex).getLocation(),10));
        }
    }

    private void findViews()
    {
        myCity=(EditText)findViewById(R.id.myTextCity);
        myKey=(EditText)findViewById(R.id.myTextKey);
        mySearchBus=(Button)findViewById(R.id.myButtonSearchBus);
        myReverseLine=(Button)findViewById(R.id.myButtonReverseLine);
        myPre=(Button)findViewById(R.id.myButtonPre);
        myNext=(Button)findViewById(R.id.myButtonNext);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        myMap.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        myMap.onPause();
    }
    @Override
    protected void onDestroy() {
        myPoiSearch.destroy(); //DistrictSearch对象在程序结束时要即使销毁，结束其生命周期
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        myMap.onDestroy();
    }

}








