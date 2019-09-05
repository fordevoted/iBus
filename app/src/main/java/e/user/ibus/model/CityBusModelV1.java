package e.user.ibus.model;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import e.user.ibus.Presenter.CityBusDataPresenter;
import e.user.ibus.Presenter.KeyImeChange;
import e.user.ibus.Presenter.OnItemClickListener;

public class CityBusModelV1 implements CityBusDataPresenter,OnItemClickListener {
	private final static String RECYCLERTAG = "Recycler Adapter";
	private final static String BUTTONTAG = "Button test";
	private List<Map<String, Object>> viewDataSize = new ArrayList();
	public AskBusRouteTask askBusRouteTask;
	private InputMethodManager inputMethodManager;
	private CustomEditText editText;

	public boolean isLoading, isDataExist = false, isKeyboardFold = false, isKeyboardOpen = false;
	private int requestDataNumber = 6;
	private Context context;

	public CityBusModelV1(Context context){

		this.context = context;
		this.inputMethodManager = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
		editText = new CustomEditText(context);
	}

	@Override
	public void setTitleText(TextView title, String text) {
		if(text.equals("")){
			title.setText(QUERY);
		}else{
			title.setText(text);
		}
	}
	@Override
	public void setTitleText(TextView title, String compareText, String text) {
		if(title.getText().toString().equals(compareText)){
			title.setText(text);
		}else{
			title.setText((title.getText().toString() + text));
		}
	}

	@Override
	public void openKeyboard(TextView title,int type, LinearLayout container,
							 LinearLayout keyboardContainer, ImageButton ib_keyboardUp,
							 ConstraintLayout keyboard,Activity activity) {
		changeKeyboardType(type,container, keyboardContainer,ib_keyboardUp, keyboard);
		Log.d(BUTTONTAG, String.valueOf(ib_keyboardUp.getVisibility()));
		isKeyboardFold = false;
		isKeyboardOpen = true;
		editText.setText("");
		editText.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				Log.d("edittext text test onChange", s.toString());
				setTitleText(title,s.toString());
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
		editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int keyCode, KeyEvent event) {
				if(keyCode == EditorInfo.IME_ACTION_DONE){
					closeKeyboard(ib_keyboardUp,container);
				}
				if(keyCode == KeyEvent.KEYCODE_BACK) {
					closeKeyboard(ib_keyboardUp,container);
				}
				return false;
			}
		});
		editText.setKeyImeChangeListener(new KeyImeChange() {
			@Override
			public void onKeyIme(int keyCode, KeyEvent event) {
				Log.d("Keyboard","get back pressed");
				closeKeyboard(activity,ib_keyboardUp,container);
			}
		});
		new Handler().postDelayed(()->{
			if(editText.requestFocus()){
				Log.d("TEST","title is focus ");
			}else{
				Log.d("TEST","title is not focus ");
			}
			ib_keyboardUp.setVisibility(View.GONE);
			editText.setImeOptions(EditorInfo.IME_ACTION_DONE);
			editText.setSingleLine();
			container.addView(editText);
			inputMethodManager.toggleSoftInputFromWindow(editText.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED,0);
		},100);
	}

	@Override
	public void closeKeyboard(ImageButton ib_keyboardUp, LinearLayout container) {
		new Handler().postDelayed(() -> {
			editText.requestFocus();
			inputMethodManager.hideSoftInputFromWindow(editText.getApplicationWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
			ib_keyboardUp.setVisibility(View.VISIBLE);
			container.removeView(editText);
			isKeyboardOpen = false;
			editText.clearFocus();
		}, 100);
	}

	@Override
	public void closeKeyboard(Activity activity, ImageButton ib_keyboardUp, LinearLayout container) {
		new Handler().postDelayed(() -> {
			inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
			ib_keyboardUp.setVisibility(View.VISIBLE);
			container.removeView(editText);
			isKeyboardOpen = false;
		}, 100);
	}

	@Override
	public void changeKeyboardType(int type, LinearLayout container,
								   LinearLayout keyboardContainer, ImageButton ib_keyboardUp,
								   ConstraintLayout keyboard) {
		if(type == EXPENDKEYBOARD){
			LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, 0, 5.5f);
			isKeyboardFold  = false;
			new Handler().postDelayed(()->{
				container.setWeightSum(12.5f);
				keyboardContainer.setLayoutParams(param);
				ib_keyboardUp.setVisibility(View.GONE);
				keyboard.setVisibility(View.VISIBLE);
			},100);
			Log.d(BUTTONTAG,String.format(" ib_keyboard click: visibility is  %d; keyboard visibility: %d ",
					ib_keyboardUp.getVisibility(),keyboard.getVisibility()));
		}else if (type == FOLDKEYBOARD){
			LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, 0, 1);
			isKeyboardFold = true;
			new Handler().postDelayed(()->{
				ib_keyboardUp.setVisibility(View.VISIBLE);
				container.setWeightSum(8);
				keyboardContainer.setLayoutParams(param);
				keyboard.setVisibility(View.GONE);
			},100);
		}
	}

	@Override
	public void requestData(String location,RecyclerViewAdapter adapter, final List<Map<String,Object>> data,SwipeRefreshLayout swipeRefreshLayout) {
		askBusRouteTask = new AskBusRouteTask();
		askBusRouteTask.execute("https://ptx.transportdata.tw/MOTC/v2/Bus/Route/City/"
				+  location + "?$top=300&$format=JSON");
		//askBusRouteTask.execute("https://ptx.transportdata.tw/MOTC/v2/Bus/RealTimeByFrequency/City/Chiayi?$top=1&$format=JSON");


	}

	@Override
	public List<Map<String,Object>> changeData(List<Map<String, Object>> finalData, String containKeyword, RecyclerViewAdapter adapter) {
		List<Map<String,Object>> temp_data = new ArrayList<>();

		if(!containKeyword.equals("") && !containKeyword.equals(QUERY)){
			Log.d(RECYCLERTAG ,"s test " + containKeyword);
			for(int i = 0 ; i <  finalData.size() ; i++){
				if(String.valueOf(finalData.get(i).get("RouteName")).contains(containKeyword)){
					temp_data.add(finalData.get(i));
					Log.d(RECYCLERTAG ,"temp data add data " + String.valueOf(finalData.get(i).get("RouteName")) +"  "+containKeyword);
				}
			}
		}else{
			temp_data = finalData;
		}
		Log.d(RECYCLERTAG ,"temp data size " + temp_data.size());
		adapter.data = temp_data;
		//init viewDataSize
		viewDataSize.clear();
		viewDataSize.add(new HashMap<>());
		adapter.viewDataSize = viewDataSize;
		adapter.maxindex = 0;
		if(temp_data.size() == 0){
			viewDataSize.remove(viewDataSize.size()-1);
			adapter.notifyItemRemoved(viewDataSize.size());
			Toast.makeText(context,"無此路線代碼",Toast.LENGTH_SHORT).show();
			adapter.notifyDataSetChanged();
			adapter.notifyItemChanged(viewDataSize.size(),adapter.getItemCount());
		}else{
			getData(adapter,temp_data);
		}
		return temp_data;
	}

	@Override
	public void detectLoadMoreData(RecyclerViewAdapter adapter, int lastVisibleItemPosition,
								SwipeRefreshLayout swipeRefreshLayout, List<Map<String,Object>> data) {
		//Log.d(RECYCLERTAG + " scroll test",String.format("last index: %d,  adapter.count: %d",lastVisibleItemPosition,adapter.getItemCount()));

		if (lastVisibleItemPosition +1 == adapter.getItemCount()) {
			if (isDataExist) {
				Log.d( RECYCLERTAG + " scroll index test",String.valueOf(adapter.maxindex) + " " + data.size());
				if(adapter.maxindex >= data.size() - 1){
					return;
				}
				Log.d( RECYCLERTAG + " loading test", "loading executed");

				boolean isRefreshing = swipeRefreshLayout.isRefreshing();
				if (isRefreshing) {
					adapter.notifyItemRemoved(adapter.getItemCount());
					return;
				}
				if (!isLoading) {
					isLoading = true;
					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							getData(adapter, data);
							Log.d( RECYCLERTAG + " loading test", "load more completed");
							isLoading = false;
						}
					}, 100);
				}
			}
		}

	}

	@Override
	public void initData(RecyclerViewAdapter adapter, SwipeRefreshLayout swipeRefreshLayout, List<Map<String,Object>> data) {
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				
				for (int i = 0; i < 1; i++) {
					Map<String, Object> map = new HashMap<>();
					viewDataSize.add(map);
				}
				swipeRefreshLayout.setRefreshing(false);
				Log.d("Main Refresh Test","In");
				//Log.d("test", "size is "+adapter.getItemCount());
				adapter.viewDataSize = viewDataSize;
				adapter.notifyDataSetChanged();
				adapter.notifyItemRangeChanged(viewDataSize.size(), adapter.getItemCount());
			}
		}, 100);
	}

	@Override
	public void getData(RecyclerViewAdapter adapter, List<Map<String,Object>> data) {
		int temp;
		/*if(adapter.maxindex + requestDataNumber >= data.size()){
			temp = (data.size() ) - adapter.maxindex  ;
			Log.d("GetData test",String.format(" temp is %d, adapter.maxindex is %d  , max %d",temp, adapter.maxindex ,data.size()));
		}else{
			temp = requestDataNumber;
		}*/
		temp = (data.size() ) - adapter.maxindex  ;
		//Toast.makeText(getApplicationContext(),"GetData test" + String.format(" temp is %d, adapter.maxindex is %d  , max %d",temp, adapter.maxindex ,imgUrl.size()),Toast.LENGTH_SHORT).show();

		for (int i = 0; i < temp; i++) {
			Map<String, Object> map = new HashMap<>();
			viewDataSize.add(map);
		}

		//Log.d("test", "size is "+adapter.getItemCount());
		viewDataSize.remove(viewDataSize.size()-1);
		adapter.data = data;
		adapter.viewDataSize = viewDataSize;
		adapter.notifyDataSetChanged();
		adapter.notifyItemRemoved(viewDataSize.size());
		adapter.notifyItemRangeChanged(viewDataSize.size(), adapter.getItemCount());
		//Log.d("test", "after pruning size is "+adapter.getItemCount());
	}

	@Override
	public void detectDataEndMessage(Context context, RecyclerView recyclerView,RecyclerViewAdapter adapter ,List<Map<String,Object>> data) {
		if (!recyclerView.canScrollVertically(1) && adapter.index >= data.size() - 1) {
			Toast.makeText(context,"資料載入完畢",Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onItemClick(ItemViewHolder holder, int position) {

	}

	@Override
	public void onLikeClick(ItemViewHolder holder, int position) {
		Toast.makeText(context,"收藏功能尚在測試中",Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onItemLongClick(View view, int position) {

	}
}
