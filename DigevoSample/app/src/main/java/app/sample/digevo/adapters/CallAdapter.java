package app.sample.digevo.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import app.sample.digevo.R;
import app.sample.digevo.network.entities.Call;

/**
 * Created by romantolmachev on 6/12/14.
 */
public class CallAdapter extends ArrayAdapter<Call> {

    private final Activity mContext;
    private List<Call> mCalls;

    public CallAdapter(Activity context, int textViewResourceId, List<Call> objects) {
        super(context, textViewResourceId, objects);
        mContext = context;
        mCalls = objects;
    }

    @Override
    public int getCount() {
        return mCalls.size();
    }

    @Override
    public boolean areAllItemsEnabled()
    {
        return true;
    }

    @Override
    public boolean isEnabled(int arg0)
    {
        return true;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        // reuse views
        if (rowView == null) {
            LayoutInflater inflater = mContext.getLayoutInflater();
            rowView = inflater.inflate(R.layout.custom_info_window, null);
            // configure view holder
            ViewHolder viewHolder = new ViewHolder(rowView);
            rowView.setTag(viewHolder);
        }

        // fill data
        ViewHolder holder = (ViewHolder) rowView.getTag();
        holder.title.setText(getItem(position).getTitle());
        holder.contentAndTimestamp.setText(getItem(position).getContent() + "\n" +
            getItem(position).getTimestamp());

        return rowView;
    }

    static class ViewHolder {
        public TextView title;
        public TextView contentAndTimestamp;

        public ViewHolder(View view) {
            title = (TextView) view.findViewById(R.id.title);
            contentAndTimestamp = (TextView) view.findViewById(R.id.content_and_timestamp);
        }

    }
}