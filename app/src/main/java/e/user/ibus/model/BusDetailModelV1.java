package e.user.ibus.model;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import e.user.ibus.Presenter.BusDetailPresenter;
import e.user.ibus.Presenter.OnBusItemClickListener;
import e.user.ibus.R;

public class BusDetailModelV1 implements BusDetailPresenter, OnBusItemClickListener {
	private Context context;
	private List<Map<String, Object>> viewDataSizeGo = new ArrayList();
	private List<Map<String, Object>> viewDataSizeBack = new ArrayList();
	
	public AskBusDetailTask askBusDetailTask;
	public boolean isDataExist = false;
	private int status = 0;
	private int requestDataNumber = 6;
	private boolean isLoading;
	public final static String RECYCLERTAG = "Bus Detail RecyclerAdapter Test";


	public  BusDetailModelV1(Context context){
		this.context = context;
	}

	@Override
	public void setTitleText(TextView title, String text) {
		title.setText(text);
	}

	@Override
	public void initData(RecyclerViewAdapter adapter) {
		Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					for (int i = 0; i < 1; i++) {
						Map<String, Object> map = new HashMap<>();
						viewDataSizeGo.add(map);
					}
					Log.d("Main Refresh Test","In");
					//Log.d("test", "size is "+adapter.getItemCount());
					adapter.viewDataSize = viewDataSizeGo;
					adapter.notifyDataSetChanged();
					adapter.notifyItemRangeChanged(viewDataSizeGo.size(), adapter.getItemCount());
				}
			}, 100);
	}
	
	@Override
	public void initData(RecyclerViewAdapter adapter, RecyclerViewAdapter adapter1) {
		Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					for (int i = 0; i < 1; i++) {
						Map<String, Object> map = new HashMap<>();
						viewDataSizeGo.add(map);
						Map<String,Object> map1 = new HashMap<>();
						viewDataSizeBack.add(map1);
					}
					Log.d("Main Refresh Test","In");
					//Log.d("test", "size is "+adapter.getItemCount());
					adapter.viewDataSize = viewDataSizeGo;
					adapter.notifyDataSetChanged();
					adapter.notifyItemRangeChanged(viewDataSizeGo.size(), adapter.getItemCount());

					adapter1.viewDataSize = viewDataSizeBack;
					adapter1.notifyDataSetChanged();
					adapter1.notifyItemRangeChanged(viewDataSizeBack.size(), adapter.getItemCount());
				}
			}, 100);
	}


	@Override
	public void getData(RecyclerViewAdapter adapter, List<Map<String, Object>> data,int route) {

		int temp;
		/*if(adapter.maxindex + requestDataNumber >= data.size()){
			temp = data.size() - adapter.maxindex  ;
			Log.d("GetData test",String.format(" temp is %d, adapter.maxindex is %d ",temp, adapter.maxindex));

		}else{
			temp = requestDataNumber ;
		}*/
		temp = data.size() - adapter.maxindex;
		//Toast.makeText(getApplicationContext(),"GetData test" + String.format(" temp is %d, adapter.maxindex is %d  , max %d",temp, adapter.maxindex ,imgUrl.size()),Toast.LENGTH_SHORT).show();
		if(route == BusDetailPresenter.GO){
			// remove the loading holder
			viewDataSizeGo.remove(viewDataSizeGo.size()-1);
			for (int i = 0; i < temp; i++) {
				Map<String, Object> map = new HashMap<>();
				viewDataSizeGo.add(map);
			}
			Log.d(RECYCLERTAG, " viewDataSize size is " + viewDataSizeGo.size());
			Log.d(RECYCLERTAG, " temp is " + temp);
			//viewDataSizeGo.remove(viewDataSizeGo.size()-1);
			adapter.viewDataSize = viewDataSizeGo;
			//adapter.notifyItemRemoved(viewDataSizeGo.size());
			adapter.notifyItemRangeChanged(viewDataSizeGo.size(), adapter.getItemCount());
		}else{
			// remove the loading holder
			viewDataSizeBack.remove(viewDataSizeBack.size()-1);
			for (int i = 0; i < temp; i++) {
				Map<String, Object> map = new HashMap<>();
				viewDataSizeBack.add(map);
			}

			//Log.d(RECYCLERTAG, " viewDataSize size is " + viewDataSizeBack.size());
			//Log.d(RECYCLERTAG, " temp is " + temp);
			//viewDataSizeBack.remove(viewDataSizeBack.size()-1);
			adapter.viewDataSize = viewDataSizeBack;
			//adapter.notifyItemRemoved(viewDataSizeBack.size());
			adapter.notifyItemRangeChanged(viewDataSizeBack.size(), adapter.getItemCount());
		}
		adapter.data = data;
		adapter.notifyDataSetChanged();

		//Log.d("test", "after pruning size is "+adapter.getItemCount());
	}

	@Override
	public void requestData(String location, String busCode, String routeID) {
		askBusDetailTask = new AskBusDetailTask(busCode);
		askBusDetailTask.execute("https://ptx.transportdata.tw/MOTC/v2/Bus/EstimatedTimeOfArrival/City/"
				+ location + "/" + busCode + "?$filter=RouteID%20eq%20'" + routeID + "'&$orderby=StopSequence&$top=300&$format=JSON");
	}

	@Override
	public void updateData(RecyclerViewAdapter adapter, RecyclerViewAdapter adapter1, List<Map<String, Object>> dataGo, List<Map<String, Object>> dataBack) {
		adapter.data = dataGo;
		adapter1.data = dataBack;
		adapter.notifyDataSetChanged();
		adapter1.notifyDataSetChanged();
	}


	@Override
	public int updateStatus(TextView textView) {
		status = (++status)%UPDATESEC;
		int sec = UPDATESEC - status;
		if(sec == UPDATESEC){
			textView.setText("更新到站資料");
		}else{
			textView.setText((sec + context.getResources().getString(R.string.secUpdate)));
		}
		return sec;
	}

	@Override
	public int updateStatus(TextView textView, int designation) {
		status = designation;
		int sec = UPDATESEC - status;
		if(sec == UPDATESEC){
			textView.setText("更新到站資料");
		}else{
			textView.setText((sec + context.getResources().getString(R.string.secUpdate)));
		}
		return sec;
	}

	@Override
	public void detectLoadMoreData(RecyclerViewAdapter adapter, int lastVisibleItemPosition, List<Map<String, Object>> data,int route) {
		if (lastVisibleItemPosition +1 == adapter.getItemCount()) {
			if (isDataExist) {
				Log.d( RECYCLERTAG + " scroll index test",String.valueOf(adapter.maxindex) + " " + data.size());
				if(adapter.maxindex >= data.size() ){
					return;
				}
				Log.d( RECYCLERTAG + " loading test", "loading executed");


				if (!isLoading) {
					isLoading = true;
					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							getData(adapter, data,route);
							Log.d( RECYCLERTAG + " loading test", "load more completed");
							isLoading = false;
						}
					}, 100);
				}
			}
		}
	}

	@Override
	public void detectDataEndMessage(RecyclerView recyclerView, RecyclerViewAdapter adapter, List<Map<String, Object>> data) {
		if (!recyclerView.canScrollVertically(1) && adapter.index >= data.size() -1) {
			Toast.makeText(context,"資料載入完畢",Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onItemClick(BusDetailViewHolder holder, int position) {

	}

	@Override
	public void onGetOnClick(BusDetailViewHolder holder, boolean[] reservation,int position,
							 String city, String routeName, String direction, String stopName, boolean service) {
		if(reservation[isValidToGetOn]){
			if(reservation[isGetOnRequested]){
				Toast.makeText(context,"取消預約上車",Toast.LENGTH_SHORT).show();
				reservation[isGetOnRequested] = false;
				holder.bn_geton.setText(context.getResources().getString(R.string.get_on));
				if(reservation[isServiceRequested]){
					holder.container.setBackgroundColor(context.getColor(R.color.colorService));
				}else{
					holder.container.setBackgroundColor(context.getColor(R.color.white));
				}
				executeQueuing(reservation,city,routeName,direction,stopName,service,true);
			}else{
				Toast.makeText(context,"預定上車",Toast.LENGTH_SHORT).show();
				holder.bn_geton.setText("已預定上車");
				reservation[isGetOnRequested] = true;
				int imgResource = R.drawable.bus32;
				holder.bn_geton.setCompoundDrawablesWithIntrinsicBounds(imgResource, 0, 0, 0);
				if(reservation[isServiceRequested]){
					holder.container.setBackgroundColor(context.getColor(R.color.colorBoth));
				}else{
					holder.container.setBackgroundColor(context.getColor(R.color.colorGetOn));
				}
				executeQueuing(reservation, city, routeName, direction, stopName, service,false);
			}
			reservation[isValidToGetOn] = false;

		}else{
			Toast.makeText(context,"距離上次預定時間太短，目前變更預定上車",Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onServiceClick(BusDetailViewHolder holder, boolean[] reservation,int position,
							   String city, String routeName, String direction, String stopName, boolean service) {
		if(reservation[isValidToService]){
			if(reservation[isServiceRequested]){
				Toast.makeText(context,"取消預約特殊服務",Toast.LENGTH_SHORT).show();
				reservation[isServiceRequested] = false;
				holder.bn_service.setText(context.getResources().getString(R.string.request_special_service));
				if(reservation[isGetOnRequested]){
					holder.container.setBackgroundColor(context.getColor(R.color.colorGetOn));
				}else{
					holder.container.setBackgroundColor(context.getColor(R.color.white));
				}
				executeQueuing(reservation,city,routeName,direction,stopName,service,true);

			}else{
				Toast.makeText(context,"預約特殊服務",Toast.LENGTH_SHORT).show();
				reservation[isServiceRequested] = true;
				holder.bn_service.setText("已送出服務請求");
				if(reservation[isGetOnRequested]){
					holder.container.setBackgroundColor(context.getColor(R.color.colorBoth));
				}else{
					holder.container.setBackgroundColor(context.getColor(R.color.colorService));
				}
				executeQueuing(reservation, city, routeName, direction, stopName, service,false);
			}
			reservation[isValidToService] = false;
		}else{
			Toast.makeText(context,"距離上次預定時間太短，目前變更預定服務",Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onItemLongClick(View view, int position) {

	}

	private void executeQueuing(boolean[] reservation, String city, String routeName, String direction, String stopName,
								boolean service, boolean isCancel){
		QueuingAndServiceTask queuingAndServiceTask =  new QueuingAndServiceTask(city, routeName, direction, stopName, service);
		if(isCancel){
			queuingAndServiceTask.execute("http://140.115.52.194:8000/bus_info/api=dequeuing");
		}else{
			queuingAndServiceTask.execute("http://140.115.52.194:8000/bus_info/api=queuing");
		}
		Handler handler = new Handler();
		handler.post(new Runnable() {
			@Override
			public void run() {
				if(queuingAndServiceTask.resultBack()){
					if(service){
						new Handler().postDelayed(()->{reservation[isValidToService] = true;},5000);
					}else{
						new Handler().postDelayed(()->{reservation[isValidToGetOn] = true;},5000);
					}

				}else{
					handler.postDelayed(this,100);
				}
			}
		});
	}
}
