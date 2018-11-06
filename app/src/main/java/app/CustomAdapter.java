package app;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.nahla.egytour.R;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

public class CustomAdapter extends BaseAdapter {

    Context context;
    ArrayList<LandMarks> Items;

    public CustomAdapter(Context context, ArrayList<LandMarks> Items) {
        this.context = context;
        this.Items = Items;
    }

    @Override
    public int getCount() {
        return Items.size();
    }

    @Override
    public Object getItem(int position) {
        return Items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return Items.indexOf(getItem(position));
    }

    /* private view holder class */
    private class ViewHolder {
        ImageView pic;
        TextView name;
        TextView rating;
        TextView distance;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item, null);
            holder = new ViewHolder();

        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.name = (TextView) convertView.findViewById(R.id.name);
        holder.pic = (ImageView) convertView.findViewById(R.id.pic);
        holder.rating = (TextView) convertView.findViewById(R.id.rating);
        holder.distance = (TextView) convertView.findViewById(R.id.distance);
        convertView.setTag(holder);

        LandMarks row_pos = Items.get(position);
        Picasso.with(context).load(row_pos.getImgurl()).resize(70, 70).into(holder.pic);
        holder.name.setText(row_pos.getName());
        Log.i("picurl", row_pos.getImgurl());
        holder.rating.setText(row_pos.getRating() + "");
        holder.distance.setText(String.format("%.2f", row_pos.getDistance() / 1000) + " km");
        this.notifyDataSetChanged();

        return convertView;
    }

}

