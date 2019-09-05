package e.user.ibus.model;


import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import e.user.ibus.Presenter.OnBusItemClickListener;
import e.user.ibus.Presenter.OnItemClickListener;
import e.user.ibus.R;

public class RecyclerViewAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
	private final static int  TYPE_BUS = 0;
	private final static int TYPE_FOOTER = 1;
	private final static int TYPE_BUS_DETAIL = 2;
	private final static String TESTSTR = "Recycler Adapter test";
	private final static String DEFAULTTIME = "0000-00-00T00:00:00+00:00";
	
	public List<Map<String,Object>>  data ;
	public List<boolean[]> oldReservation;
	public List viewDataSize;
	private Context context;
	private String city;
	private String routeName;
	private String direction;
	private OnItemClickListener onItemClickListener;
	private OnBusItemClickListener onBusItemClickListener;
	public  int index = 0 , maxindex = 0;
	private boolean isDetail;

	public  RecyclerViewAdapter(Context context, List data, List viewDataSize, String city ){
		this.data = data ;
		this.viewDataSize =  viewDataSize;
		this.context = context ;
		this.city = city;
	}

	public RecyclerViewAdapter(Context context, String city ){
		this.data = new ArrayList();
		this.viewDataSize = new ArrayList();
		this.context = context;
		this.city = city;
	}
	public RecyclerViewAdapter(Context context, String city,Boolean isDetail){
		this.data = new ArrayList();
		this.viewDataSize = new ArrayList();
		this.context = context;
		this.city = city;
		this.isDetail = isDetail;
	}

	public RecyclerViewAdapter(Context context, String city,String routeName, String direction,Boolean isDetail){
		this.data = new ArrayList();
		this.viewDataSize = new ArrayList();
		this.context = context;
		this.city = city;
		this.routeName = routeName;
		this.direction = direction;
		this.isDetail = isDetail;
	}


	public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
		this.onItemClickListener = onItemClickListener;
	}
	public void setOnBusItemClickListener(OnBusItemClickListener onBusItemClickListener){
		this.onBusItemClickListener = onBusItemClickListener;
	}

	@Override
	public int getItemViewType(int position) {

		if(getItemCount() <= 1){
			if(data.size()==0){
				return TYPE_FOOTER;
			}else{
				// while search busRoute and the result is only one
				return TYPE_BUS;
			}
			// Log.d("init test","only one item");

		}else if(isDetail){
			return TYPE_BUS_DETAIL;
		}else {
			// Log.d("init test","item's number"+getItemCount());
			if (position + 1 == getItemCount()) {
				if(position +1 >= data.size() ){
					return TYPE_BUS;
				}else {
					return TYPE_FOOTER;
				}
			}  else {
				return TYPE_BUS;
			}
		}
	}

	@NonNull
	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		if(viewType == TYPE_BUS){
			View view = LayoutInflater.from(context).inflate(R.layout.item_base, parent,
					false);
			return new ItemViewHolder(view);
		}else if (viewType == TYPE_FOOTER) {
			View view = LayoutInflater.from(context).inflate(R.layout.item_foot, parent,
					false);
			return new FootViewHolder(view);
		}else if(viewType == TYPE_BUS_DETAIL){
			View view = LayoutInflater.from(context).inflate(R.layout.detailitem_base, parent,
					false);
			return new BusDetailViewHolder(view);
		}else{
			View view = LayoutInflater.from(context).inflate(R.layout.item_base, parent,
					false);
			return new ItemViewHolder(view);
		}
	}

	@Override
	public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

		if(holder instanceof  ItemViewHolder){
			Log.d(TESTSTR, String.valueOf(position));
			((ItemViewHolder) holder).routeID = data.get(position).get("RouteID").toString();
			((ItemViewHolder) holder).direction0_departure = data.get(position).get("Direction0").toString();
			((ItemViewHolder) holder).direction1_destination = data.get(position).get("Direction1").toString();

			((ItemViewHolder) holder).tv_title.setText((String.valueOf(data.get(position).get("RouteName"))));
			((ItemViewHolder) holder).tv_content.setText(String.valueOf(data.get(position).get("Headsign")));

			((ItemViewHolder) holder).bn_location.setText((city));

			((ItemViewHolder) holder).container.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					int position = holder.getLayoutPosition();
					Log.d("RecyclerView Position Test", String.valueOf(holder.getItemViewType()) + "position " + position);
					onItemClickListener.onItemClick((ItemViewHolder) holder , position);
				}
			});

			((ItemViewHolder) holder).bn_like.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					int position = holder.getLayoutPosition();
					Log.d("RecyclerView Position Test", String.valueOf(holder.getItemViewType()) + "position " + position);
					onItemClickListener.onLikeClick((ItemViewHolder) holder , position);
				}
			});
			index = position +1;
			if(position + 1 >= maxindex){
				maxindex = position + 1;
			}
		}else if (holder instanceof BusDetailViewHolder){

			index = position +1;
			if(position + 1>= maxindex){
				maxindex = position + 1;
			}

			if(oldReservation.get(position)[OnBusItemClickListener.isGetOnRequested]){
				((BusDetailViewHolder) holder).bn_geton.setText("已預定上車");
				int imgResource = R.drawable.bus32;
				((BusDetailViewHolder) holder).bn_geton.setCompoundDrawablesWithIntrinsicBounds(imgResource, 0, 0, 0);
				((BusDetailViewHolder) holder).container.setBackgroundColor(context.getColor(R.color.colorGetOn));
			}else{
				((BusDetailViewHolder) holder).bn_geton.setText(context.getResources().getString(R.string.get_on));
				int imgResource = R.drawable.raising32;
				((BusDetailViewHolder) holder).bn_geton.setCompoundDrawablesWithIntrinsicBounds(imgResource, 0, 0, 0);
			}
			if(oldReservation.get(position)[OnBusItemClickListener.isServiceRequested]){
				((BusDetailViewHolder) holder).bn_service.setText("已送出服務請求");
				((BusDetailViewHolder) holder).container.setBackgroundColor(context.getColor(R.color.colorService));
			}else{
				((BusDetailViewHolder) holder).bn_service.setText(context.getResources().getString(R.string.request_special_service));
			}

			if(oldReservation.get(position)[OnBusItemClickListener.isGetOnRequested] &
					oldReservation.get(position)[OnBusItemClickListener.isServiceRequested]){
				((BusDetailViewHolder) holder).container.setBackgroundColor(context.getColor(R.color.colorBoth));
			}else if (!(oldReservation.get(position)[OnBusItemClickListener.isGetOnRequested] |
					oldReservation.get(position)[OnBusItemClickListener.isServiceRequested])){
				((BusDetailViewHolder) holder).container.setBackgroundColor(context.getColor(R.color.white));
			}
			if(data.get(position).containsKey("EstimateTime")){
				int  estimateTime = Integer.parseInt(data.get(position).get("EstimateTime").toString());
				estimateTime /= 60;
				String time;
				if(data.get(position).containsKey("NextBusTime")){
					time = data.get(position).get("NextBusTime").toString();
				}else{
					time = 	DEFAULTTIME;
				}
				Pattern pattern = Pattern.compile("(.*)(T)([0-9]*:[0-9]*)(.*)(\\+.*)");
				Matcher matcher = pattern.matcher(time);
				matcher.matches();
				if(estimateTime >=30 ){
					((BusDetailViewHolder) holder).tv_time.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#797273")));
					((BusDetailViewHolder) holder).tv_time.setText(matcher.group(3));
				}else{
					if(estimateTime <=3 && estimateTime > 0){
						((BusDetailViewHolder) holder).tv_time.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#D13D50")));
						((BusDetailViewHolder) holder).tv_time.setText(estimateTime +" 分");
					}else if (estimateTime == 0){
						((BusDetailViewHolder) holder).tv_time.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#D13D50")));
						((BusDetailViewHolder) holder).tv_time.setText("準備進站");
					}else{
						((BusDetailViewHolder) holder).tv_time.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4CB6D4")));
						((BusDetailViewHolder) holder).tv_time.setText(estimateTime +" 分");
					}
				}
				if(!matcher.group(1).equals("0000-00-00")){
					((BusDetailViewHolder) holder).tv_detail.setText(("抵達時間 "
							+ matcher.group(1) + " " + matcher.group(3) + matcher.group(4)));
				}
				if(data.get(position).containsKey("PlateNumb")){
					if(!data.get(position).get("PlateNumb").equals("")){
						String  str = ((BusDetailViewHolder) holder).tv_detail.getText()
								+ "\n車號 " +
								data.get(position).get("PlateNumb").toString();
						((BusDetailViewHolder) holder).tv_detail.setText(str);
					}
				}
			}else if(data.get(position).containsKey("NextBusTime")){
				String time = data.get(position).get("NextBusTime").toString();
				Pattern pattern = Pattern.compile("(.*)(T)([0-9]*:[0-9]*)(.*)(\\+.*)");
				Matcher matcher = pattern.matcher(time);
				matcher.matches();
				//Log.d(TESTSTR,"time: " + time +" matches: " + matcher.matches());
				((BusDetailViewHolder) holder).tv_time.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#797273")));
				((BusDetailViewHolder) holder).tv_time.setText(matcher.group(3));
				((BusDetailViewHolder) holder).tv_detail.setText(("抵達時間 "
						+ matcher.group(1) + " " + matcher.group(3) + matcher.group(4)));

				if(data.get(position).containsKey("PlateNumb")){
					if(!data.get(position).get("PlateNumb").equals("")){
						String  str = ((BusDetailViewHolder) holder).tv_detail.getText()
								+ "\n車號 " +
								data.get(position).get("PlateNumb").toString();
						((BusDetailViewHolder) holder).tv_detail.setText(str);
					}
				}
			}else{
				((BusDetailViewHolder) holder).tv_time.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#797273")));
				((BusDetailViewHolder) holder).tv_time.setText(data.get(position).get("StopStatus").toString());
			}
			((BusDetailViewHolder) holder).tv_content.setText(data.get(position).get("StopName").toString());

			((BusDetailViewHolder) holder).container.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					int position = holder.getLayoutPosition();
					Log.d("RecyclerView Position Test", String.valueOf(holder.getItemViewType()) + "position " + position);
					onBusItemClickListener.onItemClick((BusDetailViewHolder) holder, position);
				}
			});

			((BusDetailViewHolder) holder).bn_geton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					int position = holder.getLayoutPosition();
					Log.d("RecyclerView Position Test", String.valueOf(holder.getItemViewType()) + "position " + position);
					onBusItemClickListener.onGetOnClick((BusDetailViewHolder) holder,oldReservation.get(position), position,
							city,routeName,direction,((BusDetailViewHolder) holder).tv_content.getText().toString(),false);
				}
			});
			((BusDetailViewHolder) holder).bn_service.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					int position = holder.getLayoutPosition();
					Log.d("RecyclerView Position Test", String.valueOf(holder.getItemViewType()) + "position " + position);
					onBusItemClickListener.onServiceClick((BusDetailViewHolder) holder,oldReservation.get(position), position,
							city,routeName,direction,((BusDetailViewHolder) holder).tv_content.getText().toString(),true);
				}
			});
		}

	}

	@Override
	public int getItemCount() { return viewDataSize.size(); }

	public void notifyDataSetSize(int dataSize){
		oldReservation = new ArrayList<>();
		for(int i = 0 ; i < dataSize ; i++){
			oldReservation.add(new boolean[]{false,false,true,true});
		}
	}
}

