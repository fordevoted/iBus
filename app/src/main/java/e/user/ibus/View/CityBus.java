package e.user.ibus.View;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import e.user.ibus.Presenter.CityBusDataPresenter;
import e.user.ibus.Presenter.OnItemClickListener;
import e.user.ibus.R;
import e.user.ibus.model.CityBusModelV1;
import e.user.ibus.model.ItemViewHolder;
import e.user.ibus.model.RecyclerViewAdapter;

public class CityBus extends AppCompatActivity {

	@InjectView(R.id.toolbar1)
	Toolbar toolbar;
	@InjectView(R.id.main_container)
	LinearLayout container;
	@InjectView(R.id.keyboard_container)
	LinearLayout keyboard_container;
	@InjectView(R.id.swipeRefreshLayout)
	SwipeRefreshLayout swipeRefreshLayout;
	@InjectView(R.id.recyclerView)
	RecyclerView recyclerView;
	@InjectView(R.id.ib_keyboardUp)
	ImageButton ib_keyboardUp;
	@InjectView(R.id.keyboard)
	ConstraintLayout keyboard;
	@InjectView(R.id.bn_0)
	Button bn_0;
	@InjectView(R.id.bn_1)
	Button bn_1;
	@InjectView(R.id.bn_2)
	Button bn_2;
	@InjectView(R.id.bn_3)
	Button bn_3;
	@InjectView(R.id.bn_4)
	Button bn_4;
	@InjectView(R.id.bn_5)
	Button bn_5;
	@InjectView(R.id.bn_6)
	Button bn_6;
	@InjectView(R.id.bn_7)
	Button bn_7;
	@InjectView(R.id.bn_8)
	Button bn_8;
	@InjectView(R.id.bn_9)
	Button bn_9;
	@InjectView(R.id.bn_l)
	Button bn_l;
	@InjectView(R.id.bn_clean)
	Button bn_clean;
	@InjectView(R.id.tv_title)
	TextView tv_title;


	private RecyclerViewAdapter adapter;
	private String location = "Taoyuan";
	private List<Map<String,Object>> data;
	private List<Map<String,Object>> finalData;
	private CityBusModelV1 presenter;
	private Point size;
	private Activity cityBus;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.city_bus_layout);
		//tv_title =  findViewById(R.id.tv_title);
		ButterKnife.inject(this);
		cityBus = this;
		Bundle bundle = getIntent().getExtras();
		if(bundle!= null){
			location = bundle.getString("location");
		}
		initView();

	}

	private void initView(){
		presenter = new CityBusModelV1(getApplicationContext());
		presenter.setTitleText(tv_title,CityBusDataPresenter.QUERY);
		ib_keyboardUp.setOnClickListener((ib_keyboardUp)->{
			presenter.changeKeyboardType(CityBusDataPresenter.EXPENDKEYBOARD,
					container,keyboard_container, (ImageButton) ib_keyboardUp,keyboard);
		});
		toolbar.setNavigationOnClickListener((toolbar)->{ finish();});
		tv_title.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				data = presenter.changeData(finalData, s.toString(),adapter);
			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
		// to adjust the word size, need to get the mobile size
		Display display = getWindowManager().getDefaultDisplay();
		size = new Point();
		display.getSize(size);
		// init data & adapter
		data = new ArrayList<>();
		finalData = new ArrayList<>();
		adapter = new RecyclerViewAdapter(getApplicationContext(),location);
		presenter.initData(adapter,swipeRefreshLayout,data);
		presenter.requestData(location,adapter,data,swipeRefreshLayout);
		Handler handler = new Handler();
		handler.post(new Runnable() {
			@Override
			public void run() {
				if(presenter.askBusRouteTask.resultBack() == null){
					handler.postDelayed(this,100);
				}else{
					data = presenter.askBusRouteTask.resultBack();
					finalData.addAll(data);
					presenter.isDataExist = true;
					presenter.getData(adapter, data);
				}
			}
		});


		swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				//TODO  refreshing
				//Toast.makeText(getApplicationContext(),"not found data",Toast.LENGTH_SHORT).show();
				swipeRefreshLayout.setRefreshing(false);
			}
		});

		final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
		recyclerView.setLayoutManager(layoutManager);
		recyclerView.setAdapter(adapter);
		adapter.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(ItemViewHolder holder, int position) {
				presenter.onItemClick(holder, position);
				Intent intent = new Intent(getApplicationContext(), Bus_detail.class);
				intent.putExtra("location",location);
				intent.putExtra("busCode",holder.tv_title.getText());
				intent.putExtra("routeID",holder.routeID);
				intent.putExtra("direction0",holder.direction0_departure);
				intent.putExtra("direction1",holder.direction1_destination);
				startActivity(intent);
			}

			@Override
			public void onLikeClick(ItemViewHolder holder, int position) {
				presenter.onLikeClick(holder, position);

			}

			@Override
			public void onItemLongClick(View view, int position) {
				presenter.onItemLongClick(view,position);
			}
		});

		recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
				super.onScrollStateChanged(recyclerView, newState);
				presenter.detectDataEndMessage(getApplicationContext(), recyclerView,adapter,data);
			}

			@Override
			public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
				super.onScrolled(recyclerView, dx, dy);

				if(!presenter.isKeyboardFold && dy > 0 ){
					presenter.changeKeyboardType(CityBusDataPresenter.FOLDKEYBOARD,
							container,keyboard_container, ib_keyboardUp,keyboard);
					if(presenter.isKeyboardOpen){
						presenter.closeKeyboard(cityBus,ib_keyboardUp,container);
					}
				}
				int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
				presenter.detectLoadMoreData(adapter,lastVisibleItemPosition,
						swipeRefreshLayout,data);
			}
		});
		bn_0.setOnClickListener((bn_0)->{
			//Log.d(BUTTONTAG,tv_title.getText().toString() +"  " +tv_title.getText().length() + "  " +tv_title.getText().toString().equals(QUERY));
			presenter.setTitleText(tv_title, CityBusDataPresenter.QUERY,"0");
		});
		bn_1.setOnClickListener((bn_1)->{
			presenter.setTitleText(tv_title, CityBusDataPresenter.QUERY,"1");
		});
		bn_2.setOnClickListener((bn_2)->{
			presenter.setTitleText(tv_title, CityBusDataPresenter.QUERY,"2");
		});
		bn_3.setOnClickListener((bn_3)->{
			presenter.setTitleText(tv_title, CityBusDataPresenter.QUERY,"3");
		});
		bn_4.setOnClickListener((bn_4)->{
			presenter.setTitleText(tv_title, CityBusDataPresenter.QUERY,"4");
		});
		bn_5.setOnClickListener((bn_5)->{
			presenter.setTitleText(tv_title, CityBusDataPresenter.QUERY,"5");
		});
		bn_6.setOnClickListener((bn_6)->{
			presenter.setTitleText(tv_title, CityBusDataPresenter.QUERY,"6");
		});
		bn_7.setOnClickListener((bn_7)->{
			presenter.setTitleText(tv_title, CityBusDataPresenter.QUERY,"7");
		});
		bn_8.setOnClickListener((bn_8)->{
			presenter.setTitleText(tv_title, CityBusDataPresenter.QUERY,"8");
		});
		bn_9.setOnClickListener((bn_9)->{
			presenter.setTitleText(tv_title, CityBusDataPresenter.QUERY,"9");
		});
		bn_l.setOnClickListener((bn_l)->{
			presenter.openKeyboard(tv_title,CityBusDataPresenter.FOLDKEYBOARD,
					container,keyboard_container, ib_keyboardUp,keyboard,cityBus);
		});
		bn_clean.setOnClickListener((bn_clean)->{
			presenter.setTitleText(tv_title, CityBusDataPresenter.QUERY);
		});
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		if(presenter.isKeyboardOpen){
			presenter.closeKeyboard(cityBus,ib_keyboardUp,container);
		}
	}


}
