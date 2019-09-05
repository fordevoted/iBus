package e.user.ibus.Presenter;

import android.view.View;

import e.user.ibus.model.BusDetailViewHolder;

public interface OnBusItemClickListener {
	int isGetOnRequested = 0;
	int isServiceRequested = 1;
	int isValidToGetOn = 2;
	int isValidToService = 3;
	void onItemClick(BusDetailViewHolder holder, int position);
	void onGetOnClick(BusDetailViewHolder holder, boolean[] reservation,int position,
					  String city, String routeName, String direction, String stopName, boolean service);
	void onServiceClick(BusDetailViewHolder holder, boolean[] reservation,int position,
						String city, String routeName, String direction, String stopName, boolean service);
	void onItemLongClick(View view, int position);
}
