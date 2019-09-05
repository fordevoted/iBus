package e.user.ibus.model;

import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import e.user.ibus.R;

public class BusDetailViewHolder extends RecyclerView.ViewHolder {

	public TextView tv_time;
	public TextView tv_content;
	public TextView tv_detail;
	public Button bn_service;
	public Button bn_geton;
	public LinearLayout container;
	//public boolean isServiceRequested = false;
	//public boolean isGetOnRequested = false;

	public BusDetailViewHolder(View view) {
		super(view);
		tv_time = view.findViewById(R.id.tv_time);
		tv_content = view.findViewById(R.id.tv_content);
		tv_detail = view.findViewById(R.id.tv_detail);
		bn_geton = view.findViewById(R.id.bn_geton);
		bn_service = view.findViewById(R.id.bn_service);
		container = view.findViewById(R.id.container);
	}
}

