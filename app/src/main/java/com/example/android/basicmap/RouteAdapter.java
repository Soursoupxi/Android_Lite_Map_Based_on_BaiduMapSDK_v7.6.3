package com.example.android.basicmap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.route.DrivingRouteLine;

import java.lang.reflect.Type;
import java.util.List;

public class RouteAdapter extends BaseAdapter
{
    private List<? extends RouteLine> routeLines;
    private LayoutInflater layoutInflater;
    private Type mtype;

    public RouteAdapter(Context context,List<? extends RouteLine> routeLines,Type type)
    {
        this.routeLines=routeLines;
        layoutInflater=LayoutInflater.from(context);
        mtype=type;
    }

    @Override
    public int getCount()
    {
        return routeLines.size();
    }
    @Override
    public Object getItem(int position)
    {
        return position;
    }
    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position,View convertView,ViewGroup parent)
    {
        NodeViewHolder holder;
        if(convertView==null)
        {
            convertView=layoutInflater.inflate(R.layout.route_item,null);
            holder=new NodeViewHolder();
            holder.seq=(TextView)convertView.findViewById(R.id.routeSeq);
            holder.num=(TextView)convertView.findViewById(R.id.lightNum);
            holder.dis=(TextView)convertView.findViewById(R.id.blockDis);
            convertView.setTag(holder);
        }
        else
        {
            holder=(NodeViewHolder)convertView.getTag();
        }
        switch(mtype)
        {
            case WALKING_ROUTE:
            case TRANSIT_ROUTE:
            case MASS_TRANSIT_ROUTE:
            case DRIVING_ROUTE:
                DrivingRouteLine drivingRouteLine=(DrivingRouteLine)routeLines.get(position);
                holder.seq.setText("路径"+(position+1));
                holder.num.setText("红绿灯数："+drivingRouteLine.getLightNum());
                holder.dis.setText("拥堵距离："+drivingRouteLine.getCongestionDistance()+"米");
                break;
            case INDOOR_ROUTE:
            case BIKING_ROUTE:
            default:
                break;
        }
        return convertView;
    }

    private class NodeViewHolder
    {
        private TextView seq;
        private TextView num;
        private TextView dis;
    }

    public enum Type
    {
        WALKING_ROUTE, //步行
        TRANSIT_ROUTE, //公交
        MASS_TRANSIT_ROUTE, //跨城
        DRIVING_ROUTE, //驾车
        INDOOR_ROUTE, //室内
        BIKING_ROUTE //骑行
    }
}








