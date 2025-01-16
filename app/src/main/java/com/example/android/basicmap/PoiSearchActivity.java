package com.example.android.basicmap;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.CircleOptions;
import com.baidu.mapapi.map.LogoPosition;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.overlayutil.PoiOverlay;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchOption;
import com.baidu.mapapi.search.poi.PoiDetailSearchResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.poi.PoiSortType;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;

import java.util.ArrayList;
import java.util.List;

public class PoiSearchActivity extends BaseActivity
{
    private EditText myCity;
    private EditText myLatLng;
    private EditText myRadius;
    private AutoCompleteTextView myKey;
    private Button mySearchInCity;
    private Button mySearchNearby;
    private PoiSearch myPoiSearch; //Poi检索使用PoiSearch对象，需要事先声明和实例化
    private SuggestionSearch mySugSearch; //检索关键词建议使用SuggestionSearch对象，也要声明与实例化
    private int searchType=1; //检索的类型（1：室内；2：周边）
    private LatLng center; //中心点经纬度
    private int radius; //检索半径
    private int loadIndex=0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        SDKInitializer.setAgreePrivacy(getApplicationContext(),true);
        SDKInitializer.initialize(getApplicationContext());

        setContentView(R.layout.poisearch);

        //获取地图控件引用
        myMap=(MapView)findViewById(R.id.bmapView);
        myBaiduMap=myMap.getMap();
        //初始化
        findViews();
        myPoiSearch=PoiSearch.newInstance();
        mySugSearch=SuggestionSearch.newInstance();

        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //单击“市内检索”按钮的事件方法
        mySearchInCity.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                searchType=1;
                String city=myCity.getText().toString(); //城市名
                String key=myKey.getText().toString(); //检索关键词
                //调用PoiSearch对象的searchInCity()方法实现在市内检索符合关键词要求的Poi地点
                myPoiSearch.searchInCity((new PoiCitySearchOption()).city(city).keyword(key).pageNum(loadIndex));
            }
        });

        //单击“周边检索”按钮的事件方法
        mySearchNearby.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                searchType=2;
                String key=myKey.getText().toString(); //检索关键词
                String[] laton=myLatLng.getText().toString().split(",");
                String lat=laton[0]; //经度
                String lon=laton[1]; //纬度
                center=new LatLng(Float.valueOf(lat),Float.valueOf(lon)); //中心点
                radius=Integer.valueOf(myRadius.getText().toString()); //搜索半径
                //调用PoiSearch对象的searchNearby()方法实现在指定半径区域范围内进行周边检索，sortType(PoiSortType.distance_from_near_to_far)指明搜索的顺序为由近及远
                myPoiSearch.searchNearby(new PoiNearbySearchOption().keyword(key).sortType(PoiSortType.distance_from_near_to_far).location(center).radius(radius).pageNum(loadIndex));
            }
        });

        //回调方法获取并处理Poi检索的结果
        myPoiSearch.setOnGetPoiSearchResultListener(new OnGetPoiSearchResultListener()
        {
            @Override
            public void onGetPoiResult(PoiResult poiResult)
            { //Poi检索的结果集由PoiResult参数返回
                if(poiResult.error==SearchResult.ERRORNO.NO_ERROR)
                {
                    myBaiduMap.clear();
                    //自定义MyPoiOverlay类继承自com.baidu.mapapi.overlayutil库的PoiOverlay类，重新该类的onPoiClick方法，实现当用户点击地图上的标识时
                    //显示该地点的详细信息，详细信息由PoiSearch对象的searchPoiDetail()方法获得，在回调函数onGetPoiDetailResult中处理
                    PoiOverlay overlay=new MyPoiOverlay(myBaiduMap);
                    myBaiduMap.setOnMarkerClickListener(overlay);
                    overlay.setData(poiResult);
                    overlay.addToMap();
                    overlay.zoomToSpan();
                    //若是周边检索，需要对检索的范围进行绘制
                    if(searchType==2)
                    {
                        BitmapDescriptor centerBitmap=BitmapDescriptorFactory.fromResource(R.drawable.icon_geo);
                        MarkerOptions ooMaker=new MarkerOptions().position(center).icon(centerBitmap);
                        myBaiduMap.addOverlay(ooMaker); //在中心点添加图标
                        //给检索区域（圆形）填充色彩
                        OverlayOptions ooCircle=new CircleOptions().fillColor(0xCCCCCC00).center(center).stroke(new Stroke(5,0xFFFF00FF)).radius(radius);
                        myBaiduMap.addOverlay(ooCircle);
                    }
                    return;
                }
            }

            @Override
            public void onGetPoiDetailResult(PoiDetailResult poiDetailResult)
            {
                if(poiDetailResult.error==SearchResult.ERRORNO.NO_ERROR)
                {
                    Toast.makeText(PoiSearchActivity.this,poiDetailResult.getName()+":"+poiDetailResult.getAddress(),Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onGetPoiDetailResult(PoiDetailSearchResult poiDetailSearchResult)
            {}

            @Override
            public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult)
            {}
        });

        //回调方法获取并处理检索建议的结果
        mySugSearch.setOnGetSuggestionResultListener(new OnGetSuggestionResultListener()
        {
            @Override
            public void onGetSuggestionResult(SuggestionResult suggestionResult)
            {
                if(suggestionResult==null || suggestionResult.getAllSuggestions()==null)
                {
                    return;
                }
                List<String> suggest=new ArrayList<String>();
                for(SuggestionResult.SuggestionInfo info:suggestionResult.getAllSuggestions())
                {
                    if(info.key!=null)
                    {
                        suggest.add(info.key);
                    }
                }
                ArrayAdapter<String> sugAdapter=new ArrayAdapter<String>(PoiSearchActivity.this,android.R.layout.simple_dropdown_item_1line,suggest);
                myKey.setAdapter(sugAdapter);
                sugAdapter.notifyDataSetChanged();
            }
        });

        //当输入关键词变化时，动态更新建议列表
        myKey.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s,int start,int count,int after)
            {}

            @Override
            public void onTextChanged(CharSequence s,int start,int before,int count)
            { //使用检索建议服务获取建议列表，结果在onGetSuggestionResult()中更新
                if(s.length()>0)
                {
                    mySugSearch.requestSuggestion((new SuggestionSearchOption()).keyword(s.toString()).city(myCity.getText().toString()));
                }
            }

            @Override
            public void afterTextChanged(Editable s)
            {}
        });
    }

    private class MyPoiOverlay extends PoiOverlay
    {
        public MyPoiOverlay(BaiduMap baiduMap)
        {
            super(baiduMap);
        }

        @Override
        public boolean onPoiClick(int index)
        {
            super.onPoiClick(index);
            PoiInfo poi=getPoiResult().getAllPoi().get(index);
            myPoiSearch.searchPoiDetail((new PoiDetailSearchOption()).poiUid(poi.uid));
            return true;
        }
    }

    private void findViews()
    {
        myCity=(EditText)findViewById(R.id.myTextCity);
        myLatLng=(EditText)findViewById(R.id.myTextLatLng);
        myRadius=(EditText)findViewById(R.id.myTextRadius);
        myKey=(AutoCompleteTextView)findViewById(R.id.myTextKey);
        mySearchInCity=(Button)findViewById(R.id.myButtonSearchInCity);
        mySearchNearby=(Button)findViewById(R.id.myButtonSearchNearby);
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










