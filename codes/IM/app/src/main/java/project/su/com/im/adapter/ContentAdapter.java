package project.su.com.im.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import project.su.com.im.R;
import project.su.com.im.message.Message;

/**
 * Created by su on 2016/10/20.
 */

public class ContentAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private ArrayList<Message> messages;
    public ContentAdapter (Context context, ArrayList messages){
        this.messages = messages;
        this.inflater = LayoutInflater.from(context);
    }



    @Override
    public int getViewTypeCount() { // return the number of view of type
        return 2;// because getView returns two types of views
    }

    @Override
    public int getItemViewType(int position) {
        return messages.get(position).getType(); // return the type of message :TYPE_SEND = 0 or TYPE_RESPONSE = 1
    }

    @Override
    public int getCount() { // return the size of data source
        return messages.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Message message = messages.get(position);
        if (convertView == null){
            // judge the type of the message to choose different type of layout
            if (message.getType() == Message.TYPE_SEND){
                convertView = inflater.inflate(R.layout.chat_send,parent,false);
            }
            if (message.getType() == Message.TYPE_RESPONSE){
                convertView = inflater.inflate(R.layout.chat_receive, parent, false);
            }
            ViewHolder viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        ViewHolder viewHolder = (ViewHolder)convertView.getTag();
        viewHolder.setContent(message.getContent());
        viewHolder.setName(message.getName());
        viewHolder.setTime(message.getTime());
        return convertView;
    }
    class ViewHolder{
        private TextView time;
        private TextView content;
        //private ImageView head;
        private TextView name;
        public ViewHolder(View convertView){
            this.content = (TextView)convertView.findViewById(R.id.content);
            //this.head = (ImageView)convertView.findViewById(R.id.head);
            this.name = (TextView)convertView.findViewById(R.id.name);
            this.time = (TextView)convertView.findViewById(R.id.time);
        }

        public void setTime(String time) {
            this.time.setText(time);
        }

        public void setContent(String content) {
            this.content.setText(content);
        }

        public void setName(String name) {
            this.name.setText(name);
        }
    }

}
