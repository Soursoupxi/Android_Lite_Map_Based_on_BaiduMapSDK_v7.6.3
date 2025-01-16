package com.example.android.basicmap;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.LogoPosition;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;

public class LatLonSearchActivity extends BaseActivity
{
    private EditText myCity;
    private EditText myAddr;
    private Button mySearch;
    private GeoCoder myGeoCoderSearch; //百度地图的地理经纬度检索使用GeoCoder对象，需要事先声明，使用前先实例化
    private String lat,lon; //经纬度

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        SDKInitializer.setAgreePrivacy(getApplicationContext(),true);
        SDKInitializer.initialize(getApplicationContext());

        setContentView(R.layout.latlonsearch);

        //获取地图控件引用
        myMap=(MapView)findViewById(R.id.bmapView);
        myBaiduMap=myMap.getMap();

        //初始化
        findViews();
        myGeoCoderSearch=GeoCoder.newInstance();

        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //单击“检索”按钮的事件方法
        mySearch.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String addr=myAddr.getText().toString();
                if(!addr.contains(","))
                { //Geo搜索（地址名→经纬度）
                    //调用GeoCoder对象的geocode()方法由地址名检索其经纬度
                    myGeoCoderSearch.geocode(new GeoCodeOption().city(myCity.getText().toString()).address(addr));
                }
                else
                { //反Geo搜索（经纬度→地址名）
                    String[] laton=addr.split(",");
                    lat=laton[0];
                    lon=laton[1];
                    LatLng ptCenter=new LatLng((Float.valueOf(lat)),(Float.valueOf(lon)));
                    //reverseGeoCode()方法进行由经纬度到地址的反向搜索
                    myGeoCoderSearch.reverseGeoCode(new ReverseGeoCodeOption().location(ptCenter));
                }
            }
        });

        //在回调方法中获取并处理地理编码检索的结果
        myGeoCoderSearch.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener()
        {
            @Override
            public void onGetGeoCodeResult(GeoCodeResult geoCodeResult)
            { //地理经纬度检索的结果在GeoCodeResult类型的参数中返回，若返回的结果为SearchResult.ERRORNO.NO_ERROR表示正确无误
                if(geoCodeResult==null || geoCodeResult.error!=SearchResult.ERRORNO.NO_ERROR)
                {
                    Toast.makeText(LatLonSearchActivity.this,"未能找到结果",Toast.LENGTH_LONG).show();
                    return;
                }
                myBaiduMap.clear();
                //Icon_mark1.png用于标识检索出的地点
                myBaiduMap.addOverlay(new MarkerOptions().position(geoCodeResult.getLocation()).icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_mark1)));
                myBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(geoCodeResult.getLocation()));
                //获得经纬度
                lat=String.format("%.6f",geoCodeResult.getLocation().latitude); //纬度
                lon=String.format("%.6f",geoCodeResult.getLocation().longitude); //经度
                //将经纬度值以文字覆盖的形式标注在地图上对应的位置
                String text="纬度："+lat+"，经度："+lon;
                addText(text);
            }

            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult)
            {
                if(reverseGeoCodeResult==null || reverseGeoCodeResult.error!=SearchResult.ERRORNO.NO_ERROR)
                {
                    Toast.makeText(LatLonSearchActivity.this,"未能找到结果",Toast.LENGTH_LONG).show();
                    return;
                }
                myBaiduMap.clear();
                myBaiduMap.addOverlay(new MarkerOptions().position(reverseGeoCodeResult.getLocation()).icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_mark1)));
                myBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(reverseGeoCodeResult.getLocation()));
                //将该地点的名称以文字覆盖的形式标注在地图上对应的位置
                addText(reverseGeoCodeResult.getAddress());
            }
        });
    }

    private void findViews()
    {
        myCity=(EditText)findViewById(R.id.myTextCity);
        myAddr=(EditText)findViewById(R.id.myTextAddr);
        mySearch=(Button)findViewById(R.id.myButtonSearch);
    }

    private void addText(String text)
    {
        //定义文字所在的坐标点
        LatLng latonText=new LatLng(Float.valueOf(lat),Float.valueOf(lon));
        //构建文字Option对象，用于在地图上添加文字
        OverlayOptions textOption=new TextOptions().bgColor(0xAAFFFF00).fontSize(24).fontColor(0xFFFF00FF).text(text).rotate(-30).position(latonText);
        //在地图上添加该文字对象并显示
        myBaiduMap.addOverlay(textOption);
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
        myGeoCoderSearch.destroy(); //DistrictSearch对象在程序结束时要即使销毁，结束其生命周期
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        myMap.onDestroy();
    }
}









