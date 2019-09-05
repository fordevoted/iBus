package e.user.ibus.model;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.Map;

import e.user.ibus.R;

public class ListViewAdapter extends ArrayAdapter {

	private Activity activity;
	private List<Map<String,Object>> data;
	private final static String LISTVIEWADAPTER = "ListView Adapter Test";
	public ListViewAdapter(@NonNull Context context, int resource) {
		super(context, resource);
	}
	public ListViewAdapter(Activity activity, int resource, List<Map<String,Object>> data){
		super(activity,resource,data);
		this.activity = activity;
		this.data = data;
		for(int i = 0 ; i < data.size() ; i++){
			Log.d(LISTVIEWADAPTER,data.get(i).get("StopName").toString());
		}
	}

	@Override
	public View getView(int position, View view, ViewGroup parent){
		View rootView = activity.getLayoutInflater().inflate(R.layout.busstop_item_layout,null,true);
		TextView tv_title = rootView.findViewById(R.id.tv_title);
		TextView tv_location = rootView.findViewById(R.id.tv_location);
		tv_title.setText(data.get(position).get("StopName").toString());
		tv_location.setText((activity.getResources().getString(R.string.isLocation)
				+ data.get(position).get("PositionLat") + " , " + data.get(position).get("PositionLon")));
		Log.d(LISTVIEWADAPTER,"enter");
		return rootView;
	}
}

