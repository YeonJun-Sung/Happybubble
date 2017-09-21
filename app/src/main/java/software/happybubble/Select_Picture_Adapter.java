package software.happybubble;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by SAMSUNG on 2017-09-20.
 */

public class Select_Picture_Adapter extends BaseAdapter{

    String image_name;
    private Context context;
    private int layoutid;
    private ArrayList<Select_List> list;
    private LayoutInflater inflater;//레이아웃 xml파일을 자바객체로 변환하기 위한 객체

    public Select_Picture_Adapter(Context context, int layoutid, ArrayList<Select_List> list){
        this.context = context;
        this.layoutid = layoutid;
        this.list = list;
        //LayoutInflater 객체 얻어오기(레이아웃xml파일을 자바객체로 변환하기 위해서)
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
    public View getView(int position, View view, ViewGroup viewGroup) {
        final int pos = position;
        final Context context = viewGroup.getContext();

        if(view == null){
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.select_list, viewGroup, false);
        }

        Select_List item = list.get(position);

        ImageView iv = (ImageView)view.findViewById(R.id.select_picture);
        iv.setFocusable(false);

        iv.setImageResource(Integer.parseInt(item.getImageID()));

        TextView text = (TextView)view.findViewById(R.id.select_text);
        text.setText(item.getName());

        return  view;
    }
}