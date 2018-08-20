package com.example.sucianalf.grouptracking.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.sucianalf.grouptracking.model.DataGroupMember;
import com.example.sucianalf.grouptracking.R;

import java.util.List;

public class MemberGroupAdapter  extends BaseAdapter{
    private Activity activity;
    private LayoutInflater inflater;
    private List<DataGroupMember>  members;
    private AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.8F);

    public MemberGroupAdapter (Activity activity,List<DataGroupMember> items ){
        this.activity = activity;
        this.members= items;
    }
    @Override
    public int getCount() {
        return members.size();
    }

    @Override
    public Object getItem(int position) {
        return members.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if(inflater==null)
            inflater=(LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(convertView==null)
            convertView=inflater.inflate(R.layout.list_group_member,null);
        TextView txtIDGroup= convertView.findViewById(R.id.idGroupMember);
        TextView txtGroupName= convertView.findViewById(R.id.groupMemberName);
        ImageView imageUser = convertView.findViewById(R.id.icon);
        final ImageView phoneUser = convertView.findViewById(R.id.phoneBtn);
        final ImageView smsUser = convertView.findViewById(R.id.smsBtn);

        DataGroupMember memberGroupData = members.get(position);
        txtGroupName.setText(memberGroupData.getMemberName());
        txtIDGroup.setText(memberGroupData.getMemberID());
        phoneUser.setTag(memberGroupData.getMemberPhone());
        smsUser.setTag(memberGroupData.getMemberPhone());
        Glide.with(activity).load(memberGroupData.getMemberPhoto()).placeholder(R.drawable.user).into(imageUser);

        phoneUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.phoneBtn:
                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneUser.getTag().toString(), null));
                        activity.startActivity(intent);
                        break;
                }

            }
        });

        smsUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.smsBtn:
                        Uri uri = Uri.parse("smsto:"+smsUser.getTag().toString());
                        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
                        activity.startActivity(intent);
                        break;
                }

            }
        });
        return convertView;
    }
}
