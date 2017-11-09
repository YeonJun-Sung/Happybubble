package software.happybubble;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;


public class ItemListViewAdapter extends BaseAdapter{
    String image_name;
    private Context context;
    private int layoutid;
    private ArrayList<ItemListView> list;
    private LayoutInflater inflater;

    public ItemListViewAdapter(Context context, int layoutid, ArrayList<ItemListView> list){
        this.context = context;
        this.layoutid = layoutid;
        this.list = list;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() { return list.size(); }

    @Override
    public Object getItem(int position) { return list.get(position); }

    @Override
    public long getItemId(int position) { return position; }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        final int pos = position;
        final Context context = viewGroup.getContext();

        if(view == null){
            inflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.activity_select_clipart, viewGroup, false);
        }
        ItemListView item = list.get(position);

        TextView text = (TextView)view.findViewById(R.id.listTxt);
        text.setText(item.getName());

        return  view;
    }
}