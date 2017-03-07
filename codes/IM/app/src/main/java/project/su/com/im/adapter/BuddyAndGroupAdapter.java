package project.su.com.im.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import project.su.com.im.R;

/**
 * Created by su on 2016/10/21.
 */

public class BuddyAndGroupAdapter extends BaseAdapter {
    private Context context = null;
    private ArrayList<String> list = null;
    private LayoutInflater layoutInflater = null;

    public BuddyAndGroupAdapter(Context context, ArrayList<String> list) {
        this.context = context;
        this.list = list;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String item = list.get(position);
        ViewHolder viewHolder = null;
        if (convertView == null){
            convertView = layoutInflater.inflate(R.layout.group_buddy_list_item, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }//else {
            viewHolder = (ViewHolder)convertView.getTag();
        //}
        viewHolder.setBuddyItem(item);
        return convertView;
    }

    class ViewHolder{
        private TextView buddyItem;
        public ViewHolder(View convertView){
            buddyItem = (TextView)convertView.findViewById(R.id.buddyItem);
        }

        public void setBuddyItem(String item) {
            buddyItem.setText(item);
        }
    }
}
