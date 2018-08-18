package com.dentalcheck.themedapp;


import android.app.Activity;
import com.dentalcheck.themedapp.CameraActivity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;



import java.util.List;


public class CameraAdapter extends BaseAdapter {

    List<getSetClass> _data;
    Activity _c;
    ViewHolder v;

    public CameraAdapter(List<getSetClass> getData, Activity context) {
        _data = getData;
        _c = context;
    }

    @Override
    public int getCount() {
        return _data.size();
    }

    @Override
    public Object getItem(int position) {
        return _data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            LayoutInflater inflater=_c.getLayoutInflater();
            view =inflater.inflate(R.layout.activity_camera_items, null,true);

        } else {
            view = convertView;
        }

        v = new ViewHolder();
        v.clickImage = view.findViewById(R.id.capture);
        v.removeImage =  view.findViewById(R.id.cancel);
        v.parcelName = view.findViewById(R.id.parcelName);
        v.chooseImage = view.findViewById(R.id.choose);
        v.label = view.findViewById(R.id.imageFor);
        v.imageView =  view.findViewById(R.id.imgPrv);

        // Set data in listView
        final getSetClass dataSet = _data.get(position);

        dataSet.setListItemPosition(position);

        if (!dataSet.isHaveImage()) {
            Bitmap icon = BitmapFactory.decodeResource(_c.getResources(), R.mipmap.ic_launcher);
            v.imageView.setImageBitmap(icon);
        } else {
            v.imageView.setImageBitmap(dataSet.getImage());
        }
        v.parcelName.setText(dataSet.getLabel());
        v.label.setText(dataSet.getSubtext());
        if (dataSet.isStatus()) {
            v.clickImage.setVisibility(View.VISIBLE);
            v.removeImage.setVisibility(View.GONE);
            v.chooseImage.setVisibility(View.VISIBLE);
        } else {
            v.removeImage.setVisibility(View.VISIBLE);
            v.chooseImage.setVisibility(View.GONE);
            v.clickImage.setVisibility(View.GONE);
        }

        v.clickImage.setFocusable(false);
        v.removeImage.setFocusable(false);
        v.chooseImage.setFocusable(false);


        v.clickImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call parent method of activity to click image
                ((CameraActivity) _c).captureImage(dataSet.getListItemPosition(), dataSet.getLabel() + "" + dataSet.getSubtext());
            }
        });
        v.chooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call parent method of activity to click image

                ((CameraActivity) _c).showImageChooser(dataSet.getListItemPosition(), dataSet.getLabel() + "" + dataSet.getSubtext());
            }
        });

        v.removeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataSet.setStatus(true);
                dataSet.setHaveImage(false);
                notifyDataSetChanged();
            }
        });


        return view;
    }

    /**
     * @param position Get position of of object
     * @param imageSrc set image in imageView
     */
    public void setImageInItem(int position, Bitmap imageSrc, String imagePath) {
        getSetClass dataSet = (getSetClass) _data.get(position);
        dataSet.setImage(imageSrc);
        dataSet.setStatus(false);
        dataSet.setHaveImage(true);
        notifyDataSetChanged();
    }

    static class ViewHolder {
        ImageView imageView;
        TextView label, parcelName;
        ImageButton clickImage, removeImage,chooseImage;
    }

}