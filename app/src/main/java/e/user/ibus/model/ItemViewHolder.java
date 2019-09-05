package e.user.ibus.model;

import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import e.user.ibus.R;

public class ItemViewHolder extends RecyclerView.ViewHolder {

	public TextView tv_title;
	public TextView tv_content;
	public Button bn_like;
	public Button bn_location;
	public LinearLayout container;
	public String routeID;
	public String direction0_departure;
	public String direction1_destination;
	public ItemViewHolder(View view) {
		super(view);
		tv_title = view.findViewById(R.id.tv_title);
		tv_content = view.findViewById(R.id.tv_content);
		bn_like = view.findViewById(R.id.bn_like);
		bn_location = view.findViewById(R.id.bn_location);
		container = view.findViewById(R.id.container);
	}
}

