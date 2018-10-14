package com.qrcode.sakthikumar.magazineqrcodescanner;


import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class MyVoicesAdapter extends ArrayAdapter<User> implements View.OnClickListener {
    customButtonListener customListner;

    public interface customButtonListener {
        public void onButtonClickListner(int position, String value);
    }

    public void setCustomButtonListner(customButtonListener listener) {
        this.customListner = listener;
    }

    private ArrayList<User> dataSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView nameTextView;
        ImageView imageView;
        Button itemBtn;
        TextView locationTextView;
    }

    public MyVoicesAdapter(ArrayList<User> data, Context context) {
        super(context, R.layout.my_voice_item, data);
        this.dataSet = data;
        this.mContext = context;
    }

    @Override
    public void onClick(View v) {
        int position = (Integer) v.getTag();
        Object object = getItem(position);
        User user = (User) object;
    }

    private int lastPosition = -1;

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final int pos = position;
        final User teacher = getItem(position);
        final MyVoicesAdapter.ViewHolder viewHolder;

        final View result;
        if (convertView == null) {
            viewHolder = new MyVoicesAdapter.ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.my_voice_item, parent, false);
            viewHolder.nameTextView = (TextView) convertView.findViewById(R.id.my_voice_name_textView);
            viewHolder.locationTextView = (TextView) convertView.findViewById(R.id.my_voice_location_textView);
            viewHolder.imageView = (CircleImageView) convertView.findViewById(R.id.myvoice_user_profile_image);
            viewHolder.itemBtn = (Button) convertView.findViewById(R.id.myvoice_user_item_btn);
            result = convertView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (MyVoicesAdapter.ViewHolder) convertView.getTag();
            result = convertView;
        }

        viewHolder.itemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, ChatActivity.class);
                intent.putExtra("receiver_user_id", teacher.id);
                intent.putExtra("title", teacher.name);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
        });

        viewHolder.nameTextView.setText(teacher.name);
        viewHolder.locationTextView.setText(teacher.city_or_town + ", " + teacher.country);
        if (teacher.profile_pic != null && !teacher.profile_pic.isEmpty()) {
            Picasso.with(mContext).load(teacher.profile_pic).placeholder(R.drawable.user_placeholder_icon).into(viewHolder.imageView);
        } else {
            viewHolder.imageView.setImageResource(R.drawable.user_placeholder_icon);
        }

        return convertView;
    }
}
