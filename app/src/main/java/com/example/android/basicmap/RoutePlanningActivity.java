package com.example.android.basicmap;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.LogoPosition;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.overlayutil.DrivingRouteOverlay;
import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRouteLine;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.IntegralRouteResult;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;

import java.util.List;

public class RoutePlanningActivity extends BaseActivity
{
    private EditText myStart;
    private EditText myStartCity;
    private EditText myEnd;
    private EditText myEndCity;
    private Button myDrivePlan;
    private Button myPre;
    private Button myNext;
    private RoutePlanSearch myRoutePlanSearch; //驾车路径规划检索使用RoutePlanSearch对象，要声明与实例化
    private RouteLine route; //RouteLine对象用于存储某条路径的信息
    private int nodeIndex=-1; //节点索引，供浏览节点时使用
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        SDKInitializer.setAgreePrivacy(getApplicationContext(),true);
        SDKInitializer.initialize(getApplicationContext());

        setContentView(R.layout.routeplanning);
        //获取地图控件引用
        myMap=(MapView)findViewById(R.id.bmapView);
        myBaiduMap=myMap.getMap();
        //初始化
        findViews();
        myRoutePlanSearch=RoutePlanSearch.newInstance();

        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //单击“驾车规划”按钮的事件方法
        myDrivePlan.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //重置浏览节点的路线数据
                route=null;
                myPre.setVisibility(View.INVISIBLE);
                myNext.setVisibility(View.INVISIBLE);
                myBaiduMap.clear();
                //设置起终点信息
                PlanNode stNode=PlanNode.withCityNameAndPlaceName(myStartCity.getText().toString(),myStart.getText().toString());
                PlanNode enNode=PlanNode.withCityNameAndPlaceName(myEndCity.getText().toString(),myEnd.getText().toString());
                //开始检索路径规划。调用RoutePlanSearch对象的drivingSearch()方法检索符合条件的路径，需要给出起终点作为参数
                myRoutePlanSearch.drivingSearch((new DrivingRoutePlanOption()).from(stNode).to(enNode));
            }
        });

        //回调方法获取并处理路径规划检索的内容
        myRoutePlanSearch.setOnGetRoutePlanResultListener(new OnGetRoutePlanResultListener()
        { //setOnGetRoutePlanResultListener回调方法支持多种不同出行方式的路径规划，出行方式对应的处理方法要写出其方法体，作为接口实现不可或缺
            @Override
            public void onGetIntegralRouteResult(IntegralRouteResult integralRouteResult)
            {}

            @Override
            public void onGetWalkingRouteResult(WalkingRouteResult walkingRouteResult)
            { /*获取步行路径、处理*/ }

            @Override
            public void onGetTransitRouteResult(TransitRouteResult transitRouteResult)
            {/*获取公交路径、处理*/}

            @Override
            public void onGetMassTransitRouteResult(MassTransitRouteResult massTransitRouteResult)
            {/*获取跨城路径、处理*/}

            @Override
            public void onGetDrivingRouteResult(final DrivingRouteResult drivingRouteResult)
            { //获取驾车路径、处理
                if(drivingRouteResult.error==SearchResult.ERRORNO.NO_ERROR)
                {
                    nodeIndex=-1;
                    if(drivingRouteResult.getRouteLines().size()>1)
                    { //检索到符合条件的驾车路径通过DrivingRouteResult类的getRouteLine()方法获取，若获取的路径数大于1，
                        //则弹出对话框让用户选择。若只有唯一路径，则直接显示结果
                        MyRouteDlg myRouteDlg=new MyRouteDlg(RoutePlanningActivity.this,drivingRouteResult.getRouteLines(),RouteAdapter.Type.DRIVING_ROUTE);
                        myRouteDlg.setOnItemInDlgClickListener(new OnItemInDlgClickListener()
                        {
                            @Override
                            public void onItemClick(int position)
                            {
                                route=drivingRouteResult.getRouteLines().get(position);
                                DrivingRouteOverlay overlay=new DrivingRouteOverlay(myBaiduMap);
                                myBaiduMap.setOnMarkerClickListener(overlay);
                                overlay.setData(drivingRouteResult.getRouteLines().get(position));
                                overlay.addToMap();
                                overlay.zoomToSpan();
                            }
                        });
                        myRouteDlg.show();
                    }
                    else if(drivingRouteResult.getRouteLines().size()==1)
                    {
                        route=drivingRouteResult.getRouteLines().get(0);
                        DrivingRouteOverlay overlay=new DrivingRouteOverlay(myBaiduMap);
                        myBaiduMap.setOnMarkerClickListener(overlay);
                        overlay.setData(drivingRouteResult.getRouteLines().get(0));
                        overlay.addToMap();
                        overlay.zoomToSpan();
                        myPre.setVisibility(View.VISIBLE);
                        myNext.setVisibility(View.VISIBLE);
                    }
                }
                else
                {
                    System.err.println("路径检索错误："+drivingRouteResult.error);
                    Toast.makeText(RoutePlanningActivity.this,"路径检索失败，原因："+drivingRouteResult.error,Toast.LENGTH_LONG);
                }
            }

            @Override
            public void onGetIndoorRouteResult(IndoorRouteResult indoorRouteResult)
            {/*获取室内路径、处理*/}

            @Override
            public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult)
            {/*获取骑行路径、处理*/}
        });
    }

    //供路线选择的对话框
    public class MyRouteDlg extends Dialog
    {
        private List<? extends RouteLine> mRouteLines;
        private ListView mRouteList;
        private RouteAdapter mRouteAdapter;
        OnItemInDlgClickListener onItemInDlgClickListener;
        public MyRouteDlg(Context context,int theme)
        {
            super(context,theme);
        }
        public MyRouteDlg(Context context,List<? extends RouteLine> routeLines,RouteAdapter.Type type)
        {
            this(context,0);
            mRouteLines=routeLines;
            mRouteAdapter=new RouteAdapter(context,mRouteLines,type);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.route_dialog);
            mRouteList=(ListView)findViewById(R.id.routeList);
            mRouteList.setAdapter(mRouteAdapter);
            mRouteList.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> parent,View view,int position,long id)
                {
                    onItemInDlgClickListener.onItemClick(position);
                    myPre.setVisibility(View.VISIBLE);
                    myNext.setVisibility(View.VISIBLE);
                    dismiss();
                }
            });
        }

        public void setOnItemInDlgClickListener(OnItemInDlgClickListener itemListener)
        {
            onItemInDlgClickListener=itemListener;
        }
    }

    //响应对话框中的条目点击
    public interface OnItemInDlgClickListener
    {
        void onItemClick(int position);
    }

    //单击前后箭头按钮浏览节点详细信息
    public void onNodeClick(View v)
    {
        LatLng nodeLocation=null;
        String nodeTitle=null;
        Object step=null;

        //非跨城综合交通
        if(route==null || route.getAllStep()==null)
        {
            return;
        }
        if(nodeIndex==-1 && v.getId()==R.id.myButtonPre)
        {
            return;
        }

        //设置节点索引
        if(v.getId()==R.id.myButtonNext)
        {
            if(nodeIndex<route.getAllStep().size()-1)
            {
                ++nodeIndex;
            }
            else
            {
                return;
            }
        }
        else if(v.getId()==R.id.myButtonPre)
        {
            if(nodeIndex>0)
            {
                --nodeIndex;
            }
            else
            {
                return;
            }
        }

        //获取节点信息
        step=route.getAllStep().get(nodeIndex);
        nodeLocation=((DrivingRouteLine.DrivingStep) step).getEntrance().getLocation();
        nodeTitle=((DrivingRouteLine.DrivingStep) step).getInstructions();
        if(nodeLocation==null || nodeTitle==null)
        {
            return;
        }
        //移动节点至中心
        myBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(nodeLocation));
        //弹出显示节点信息的标签
        TextView popupText=new TextView(RoutePlanningActivity.this);
        popupText.setBackgroundResource(R.drawable.popup);
        popupText.setTextColor(0xFF000000);
        popupText.setText(nodeTitle);
        myBaiduMap.showInfoWindow(new InfoWindow(popupText,nodeLocation,0));
    }
    private void findViews()
    {
        myStart=(EditText)findViewById(R.id.myTextStart);
        myStartCity=(EditText)findViewById(R.id.myTextStartCity);
        myEnd=(EditText)findViewById(R.id.myTextEnd);
        myEndCity=(EditText)findViewById(R.id.myTextEndCity);
        myDrivePlan=(Button)findViewById(R.id.myButtonDrivePlan);
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
        myRoutePlanSearch.destroy(); //DistrictSearch对象在程序结束时要即使销毁，结束其生命周期
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        myMap.onDestroy();
    }
}











