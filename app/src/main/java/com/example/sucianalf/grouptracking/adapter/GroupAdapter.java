package com.example.sucianalf.grouptracking.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.example.sucianalf.grouptracking.model.ListGroup;
import com.example.sucianalf.grouptracking.R;
import java.util.List;

public class GroupAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<ListGroup> groupItems;

    public GroupAdapter (Activity activity,List<ListGroup> items ){
        this.activity = activity;
        this.groupItems= items;
    }

    @Override
    public int getCount() {
        return groupItems.size();
    }

    @Override
    public Object getItem(int position) {
        return groupItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(inflater==null)
            inflater=(LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(convertView==null)
            convertView=inflater.inflate(R.layout.list_group,null);
        TextView txtIDGroup= convertView.findViewById(R.id.idGroup);
        TextView txtGroupName= convertView.findViewById(R.id.groupName);
        ListGroup groupData = groupItems.get(position);
        txtGroupName.setText(groupData.getNama());
        txtIDGroup.setText(groupData.getGroupID());
        return convertView;
    }
}
