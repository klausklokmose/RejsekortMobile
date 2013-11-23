package dk.aau.rejsekortmobile;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MyAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	private ArrayList<StationStop> stationStop;
	private Context c;

	public MyAdapter(Context c, ArrayList<StationStop> str) {
		this.c = c;
		this.stationStop = str;
		inflater = LayoutInflater.from(c);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return stationStop.size();
	}

	@Override
	public StationStop getItem(int position) {
		// TODO Auto-generated method stub
		return stationStop.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return stationStop.get(position).getID();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View view = convertView;
		TextView tv;
		if (convertView == null) {
			view = inflater.inflate(R.layout.list_thing, parent, false);
			tv = (TextView) view.findViewById(R.id.textView1);
			tv.setText(getItem(position).getStationName()+" - Zone: "+getItem(position).getZone());
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.tv = tv;
			view.setTag(viewHolder);
		} else {
			ViewHolder viewHolder = (ViewHolder) view.getTag();
			tv = viewHolder.tv;
			
		}
		return view;
	}

	private static class ViewHolder {
		public TextView tv;
	}
}
