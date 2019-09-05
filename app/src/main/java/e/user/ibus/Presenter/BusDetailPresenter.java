package e.user.ibus.Presenter;

import android.content.Context;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.List;
import java.util.Map;

import e.user.ibus.model.RecyclerViewAdapter;

public interface BusDetailPresenter {
	int GO = 0;
	int BACK = 1;
	int UPDATESEC = 30;
	void setTitleText(TextView title, String text);
	void initData(RecyclerViewAdapter adapter);
	void initData(RecyclerViewAdapter adapter,RecyclerViewAdapter adapter1);
	void getData(RecyclerViewAdapter adapter, List<Map<String,Object>> data,int route);
	void requestData(String location, String busCode, String RouteID);
	void updateData(RecyclerViewAdapter adapter, RecyclerViewAdapter adapter1, List<Map<String,Object>> dataGo, List<Map<String,Object>> dataBack );
	int updateStatus(TextView textView);
	int updateStatus(TextView textView,int designation);
	void detectLoadMoreData(RecyclerViewAdapter adapter, int lastVisibleItemPosition,
							 List<Map<String,Object>> data,int route);
	void detectDataEndMessage( RecyclerView recyclerView, RecyclerViewAdapter adapter , List<Map<String,Object>> data);
}
