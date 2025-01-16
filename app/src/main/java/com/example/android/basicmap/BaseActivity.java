package com.example.android.basicmap;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.LogoPosition;
import com.baidu.mapapi.map.MapView;

public class BaseActivity extends AppCompatActivity
{
    private boolean trafficOn=false; //开关实时路况显示
    private boolean heatMapOn=false; //开关城市热力（人口密度）显示
    private boolean showPoiOn=true; //地图上显示（清除）所有标注

    protected MapView myMap=null;
    protected BaiduMap myBaiduMap=null;

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        SDKInitializer.setAgreePrivacy(getApplicationContext(),true);
        SDKInitializer.initialize(getApplicationContext());

        setContentView(R.layout.activity_main);

        //获取地图控件引用
        myMap=(MapView)findViewById(R.id.bmapView);
        myBaiduMap=myMap.getMap();

        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }

    protected boolean checkLoginStatus()
    { //对于部分登入后使用的功能，需要检查用户登入状态
        if(!UserSessionManager.getInstance().isUserLoggedIn())
        { //用户未登入且当前功能需要登入后使用
            Toast.makeText(this,"抱歉，当前功能需要登入后使用，请登入您的账号。",Toast.LENGTH_LONG).show();
            Intent intent=new Intent(this,LoginActivity.class);
            startActivity(intent);
            return false;
        }
        else
        {
            return true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Intent intent;
        Bundle bundle=new Bundle();
        if(item.getItemId()==R.id.sub_menu_0_0)
        {
            if(checkLoginStatus())
            {
                intent=new Intent(this,BusLineSearchActivity.class);
                startActivity(intent);
            }
            return true;
        }
        else if(item.getItemId()==R.id.sub_menu_0_1)
        {
            Toast.makeText(this,"温馨提醒：登入账号后，能够体验更多功能。",Toast.LENGTH_SHORT).show();
            intent=new Intent(this,DistrictSearchActivity.class);
            startActivity(intent);
            return true;
        }
        else if(item.getItemId()==R.id.sub_menu_0_2)
        {
            Toast.makeText(this,"温馨提醒：登入账号后，能够体验更多功能。",Toast.LENGTH_SHORT).show();
            intent=new Intent(this,LatLonSearchActivity.class);
            startActivity(intent);
            return true;
        }
        else if(item.getItemId()==R.id.sub_menu_0_3)
        {
            if(checkLoginStatus())
            {
                intent=new Intent(this,PoiSearchActivity.class);
                startActivity(intent);
            }
            return true;
        }
        else if(item.getItemId()==R.id.sub_menu_0_4)
        {
            if(checkLoginStatus())
            {
                intent=new Intent(this,RoutePlanningActivity.class);
                startActivity(intent);
            }
            return true;
        }
        else if(item.getItemId()==R.id.sub_menu_1_0)
        { //普通模式
            myBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
            return true;
        }
        else if(item.getItemId()==R.id.sub_menu_1_1)
        { //卫星模式
            myBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
            return true;
        }
        else if(item.getItemId()==R.id.sub_menu_1_2)
        { //实时路况
            if(!trafficOn)
            {
                myBaiduMap.setTrafficEnabled(true);
            }
            else
            {
                myBaiduMap.setTrafficEnabled(false);
            }
            trafficOn=!trafficOn;
            return true;
        }
        else if(item.getItemId()==R.id.sub_menu_1_3)
        { //城市热力
            if(!heatMapOn)
            {
                myBaiduMap.setBaiduHeatMapEnabled(true);
            }
            else
            {
                myBaiduMap.setBaiduHeatMapEnabled(false);
            }
            heatMapOn=!heatMapOn;
            return true;
        }
        else if(item.getItemId()==R.id.sub_menu_2_0)
        { //百度Logo置于右上
            myMap.setLogoPosition(LogoPosition.logoPostionRightTop);
            return true;
        }
        else if(item.getItemId()==R.id.sub_menu_2_1)
        {
            myMap.setLogoPosition(LogoPosition.logoPostionleftBottom);
            return true;
        }
        else if(item.getItemId()==R.id.sub_menu_2_2)
        {
            myMap.setLogoPosition(LogoPosition.logoPostionCenterTop);
            return true;
        }
        else if(item.getItemId()==R.id.sub_menu_2_3)
        {
            myMap.setLogoPosition(LogoPosition.logoPostionCenterBottom);
            return true;
        }
        else if(item.getItemId()==R.id.sub_menu_2_4)
        {
            myMap.setLogoPosition(LogoPosition.logoPostionleftTop);
            return true;
        }
        else if(item.getItemId()==R.id.sub_menu_2_5)
        {
            myMap.setLogoPosition(LogoPosition.logoPostionRightBottom);
            return true;
        }
        else if(item.getItemId()==R.id.main_menu_3)
        { //所有标注
            if(!showPoiOn)
            {
                myBaiduMap.showMapPoi(true);
            }
            else
            {
                myBaiduMap.showMapPoi(false);
            }
            showPoiOn=!showPoiOn;
            return true;
        }
        else if(item.getItemId()==R.id.main_menu_4)
        { //登入或注册
            intent=new Intent(this,LoginActivity.class);
            startActivity(intent);
            return true;
        }
        else
        {
            return false;
        }
    }
}
