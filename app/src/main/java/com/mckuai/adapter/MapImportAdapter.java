package com.mckuai.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mckuai.bean.Map;
import com.mckuai.imc.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

/**
 * Created by Zzz on 2015/6/29.
 */
public class MapImportAdapter extends BaseAdapter {
    private Context mContext;
    private View view;
    private LayoutInflater mInflater;
    private ArrayList<Map> mMapBeans;
    private ImageLoader mLoader;
    private ArrayList<String> dirList;
    private ArrayList<String> fileList;

    public MapImportAdapter(Context context, ArrayList<String> fileList, ArrayList<String> dirList) {
        this.fileList = fileList;
        this.dirList = dirList;
        this.mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {

        return (null == dirList ? 0 : dirList.size()) + (null == fileList ? 0 : fileList.size());
    }

    @Override
    public Object getItem(int position) {
        if (position >= fileList.size()) {
            return dirList.get(position - fileList.size());
        } else {
            return fileList.get(position);
        }
//        return dirList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (null == convertView) {
            convertView = mInflater.inflate(R.layout.item_position, null);
            holder = new ViewHolder();
            holder.image = (ImageView) convertView.findViewById(R.id.image);
            holder.tv_name = (TextView) convertView.findViewById(R.id.pt_document);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        String tempstr = getFileName((String) getItem(position));
        if (position > fileList.size()) {
            Log.e("", "");
        }
        holder.tv_name.setText(tempstr + "");
        return convertView;
    }

    class ViewHolder {
        public ImageView image;
        public TextView tv_name;
    }

    protected String getFileName(String filepath) {
        if (filepath == null) {
            return null;
        } else {
            int index = filepath.lastIndexOf("/");
            if (index >= 0) {
                String temname = filepath.substring(index + 1, filepath.length());
                return temname;

            } else {
                return null;
            }
        }
    }

    public void setdate(ArrayList<String> fileList, ArrayList<String> dirList) {
        this.fileList = fileList;
        this.dirList = dirList;
        notifyDataSetChanged();
    }
}