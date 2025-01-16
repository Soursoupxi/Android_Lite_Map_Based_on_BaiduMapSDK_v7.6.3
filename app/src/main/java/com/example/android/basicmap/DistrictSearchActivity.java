package com.example.android.basicmap;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.LogoPosition;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolygonOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.district.DistrictResult;
import com.baidu.mapapi.search.district.DistrictSearch;
import com.baidu.mapapi.search.district.DistrictSearchOption;
import com.baidu.mapapi.search.district.OnGetDistricSearchResultListener;

import java.util.List;

public class DistrictSearchActivity extends BaseActivity
{
    private EditText myCity;
    private EditText myDistrict;
    private Button mySearch;
    private DistrictSearch myDistrictSearch; //百度地图的区域检索使用DistrictSearch对象，需要事先声明

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        SDKInitializer.setAgreePrivacy(getApplicationContext(),true);
        SDKInitializer.initialize(getApplicationContext());

        setContentView(R.layout.districtsearch);
        //获取地图控件引用
        myMap=(MapView)findViewById(R.id.bmapView);
        myBaiduMap=myMap.getMap();
        //初始化
        findViews();

        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        myDistrictSearch=DistrictSearch.newInstance(); //District对象在使用前要先实例化

        //单机“检索”按钮的事件方法
        mySearch.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String city="";
                String district="";
                if(myCity.getText()!=null && !myCity.getText().equals(""))
                {
                    city=myCity.getText().toString(); //获取城市名
                }
                if(myDistrict.getText()!=null && !myDistrict.getText().equals(""))
                {
                    district=myDistrict.getText().toString(); //获取城区名
                }
                //调用DistrictSearch对象的searchDistrict()方法来获取城市及区域信息
                myDistrictSearch.searchDistrict(new DistrictSearchOption().cityName(city).districtName(district));
            }
        });

        //在回调方法中获取并处理区域检索的结果
        myDistrictSearch.setOnDistrictSearchListener(new OnGetDistricSearchResultListener()
        {
            @Override
            public void onGetDistrictResult(DistrictResult districtResult)
            {
                myBaiduMap.clear();
                if(districtResult==null)
                {
                    return;
                }
                if(districtResult.error==SearchResult.ERRORNO.NO_ERROR)
                { //区域检索的结果在DistrictResult类型的引用参数中返回，若返回结果为SearchResult.ERRORNO.NO_ERROR表示正确无误
                    //District类的getPolylines()方法获取检索区域边界线上全部点的经纬度
                    List<List<LatLng>> polyLines=districtResult.getPolylines();
                    if(polyLines==null)
                    {
                        return;
                    }
                    LatLngBounds.Builder builder=new LatLngBounds.Builder();
                    for(List<LatLng> polyline:polyLines)
                    { //以这些点的经纬度值为依据，描绘出区域边界曲线
                        OverlayOptions ooPolyline11=new PolylineOptions().width(10).points(polyline).dottedLine(true).color(0xAA00FF00);
                        myBaiduMap.addOverlay(ooPolyline11); //将边界线作为覆盖物添加到地图上
                        //给该区域填充色彩，并且也以覆盖物的形式添加到地图上
                        OverlayOptions ooPolygon=new PolygonOptions().points(polyline).stroke(new Stroke(5,0xAA00FF88)).fillColor(0xAAFFFF00);
                        myBaiduMap.addOverlay(ooPolygon);
                        for(LatLng latLng:polyline)
                        {
                            builder.include(latLng);
                        }
                    }
                    myBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLngBounds(builder.build()));
                }
            }
        });
    }

    private void findViews()
    {
        myCity=(EditText)findViewById(R.id.myTextCity);
        myDistrict=(EditText)findViewById(R.id.myTextDistrict);
        mySearch=(Button)findViewById(R.id.myButtonSearch);
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
        myDistrictSearch.destroy(); //DistrictSearch对象在程序结束时要即使销毁，结束其生命周期
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        myMap.onDestroy();
    }
}











