package e.user.ibus.View;

import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TabHost;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import e.user.ibus.Presenter.BusDetailPresenter;
import e.user.ibus.Presenter.OnBusItemClickListener;
import e.user.ibus.R;
import e.user.ibus.model.BusDetailModelV1;
import e.user.ibus.model.BusDetailViewHolder;
import e.user.ibus.model.RecyclerViewAdapter;

public class Bus_detail extends AppCompatActivity {

	@InjectView(R.id.tabhost)
	TabHost tabHost;
	@InjectView(R.id.tv_status)
	TextView tv_status;
	@InjectView(R.id.toolbar1)
	Toolbar toolbar;
	@InjectView(R.id.tv_title)
	TextView tv_title;
	@InjectView(R.id.recyclerView_tab1)
	RecyclerView recyclerView_tab1;
	@InjectView(R.id.recyclerView_tab2)
	RecyclerView recyclerView_tab2;
	@InjectView(R.id.tab1)
	SwipeRefreshLayout tab1;
	@InjectView(R.id.tab2)
	SwipeRefreshLayout tab2;

	private final static String TAG = "BusDetail test";
	private RecyclerViewAdapter adapterGo;
	private RecyclerViewAdapter adapterBack;
	private String city = "TaoYuan";
	private String routeName;
	private String routeID;
	private String direction0;
	private String direction1;
	private List<Map<String,Object>> dataGo;
	private List<Map<String,Object>> dataBack;
	private BusDetailModelV1 presenter;
	private Runnable runUpdateData;
	private Runnable runUpdateStatus;
	private Handler handler;

	private Point size;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bus_detail_layout);
		ButterKnife.inject(this);
		Bundle bundle = getIntent().getExtras();
		if(bundle != null){
			city = bundle.getString("location");
			routeName = bundle.getString("busCode");
			routeID = bundle.getString("routeID");
			direction0 = bundle.getString("direction0");
			direction1 = bundle.getString("direction1");
		}
		initView();
	}

	private  void initView() {

		presenter = new BusDetailModelV1(getApplicationContext());
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		toolbar.setNavigationOnClickListener((toolbar) -> {
			finish();
		});
		presenter.setTitleText(tv_title, routeName);
		tabHost.setup();
		TabHost.TabSpec spec;
		if(!direction0.equals("")){
			spec = tabHost.newTabSpec("Tab 1");
			spec.setContent(R.id.tab1);
			spec.setIndicator("往 " + direction0);
			tabHost.addTab(spec);
		}else{
			tab1.setVisibility(View.GONE);
		}

		if(!direction1.equals("")){
			spec = tabHost.newTabSpec("Tab 2");
			spec.setContent(R.id.tab2);
			spec.setIndicator("往 " + direction1);
			tabHost.addTab(spec);
		} else{
			tab2.setVisibility(View.GONE);
		}



		// init data & adapter
		dataGo = new ArrayList<>();
		dataBack = new ArrayList<>();
		adapterGo = new RecyclerViewAdapter(getApplicationContext(), city,routeName,"0", true);
		adapterBack = new RecyclerViewAdapter(getApplicationContext(), city,routeName,"1", true);
		presenter.initData(adapterGo, adapterBack);
		presenter.requestData(city, routeName, routeID);
		Handler handlerInit = new Handler();
		handlerInit.post(new Runnable() {
			@Override
			public void run() {
				if (presenter.askBusDetailTask.resultBack() == null) {
					handlerInit.postDelayed(this, 100);
				} else {
					dataGo = presenter.askBusDetailTask.resultBack().get(0);
					dataBack = presenter.askBusDetailTask.resultBack().get(1);
					Log.d(TAG, "dataSize:" + dataGo.size() + "  " + dataBack.size());
					presenter.isDataExist = true;
					adapterGo.notifyDataSetSize(dataGo.size());
					adapterBack.notifyDataSetSize(dataBack.size());
					presenter.getData(adapterGo, dataGo, BusDetailPresenter.GO);
					presenter.getData(adapterBack, dataBack, BusDetailPresenter.BACK);
				}
			}
		});
		//presenter.getData(adapter,data);

		final LinearLayoutManager layoutManagerGo = new LinearLayoutManager(this);
		final LinearLayoutManager layoutManagerBack = new LinearLayoutManager(this);

		recyclerView_tab1.setLayoutManager(layoutManagerGo);
		recyclerView_tab1.setAdapter(adapterGo);
		recyclerView_tab2.setLayoutManager(layoutManagerBack);
		recyclerView_tab2.setAdapter(adapterBack);


		adapterGo.setOnBusItemClickListener(new OnBusItemClickListener() {
			@Override
			public void onItemClick(BusDetailViewHolder holder, int position) {
				presenter.onItemClick(holder, position);
			}

			@Override
			public void onGetOnClick(BusDetailViewHolder holder, boolean[] reservation, int position, String city, String routeName, String direction, String stopName, boolean service) {
				presenter.onGetOnClick(holder, reservation, position, city, routeName, direction, stopName, service);
			}

			@Override
			public void onServiceClick(BusDetailViewHolder holder, boolean[] reservation, int position, String city, String routeName, String direction, String stopName, boolean service) {
				presenter.onServiceClick(holder, reservation, position, city, routeName, direction, stopName, service);
			}

			@Override
			public void onItemLongClick(View view, int position) {
				presenter.onItemLongClick(view, position);
			}
		});
		adapterBack.setOnBusItemClickListener(new OnBusItemClickListener() {
			@Override
			public void onItemClick(BusDetailViewHolder holder, int position) {
				presenter.onItemClick(holder, position);
			}

			@Override
			public void onGetOnClick(BusDetailViewHolder holder, boolean[] reservation, int position, String city, String routeName, String direction, String stopName, boolean service) {
				presenter.onGetOnClick(holder, reservation, position, city, routeName, direction, stopName, service);
			}

			@Override
			public void onServiceClick(BusDetailViewHolder holder, boolean[] reservation, int position, String city, String routeName, String direction, String stopName, boolean service) {
				presenter.onServiceClick(holder, reservation, position, city, routeName, direction, stopName, service);
			}

			@Override
			public void onItemLongClick(View view, int position) {
				presenter.onItemLongClick(view, position);
			}
		});

		recyclerView_tab1.addOnScrollListener(new RecyclerView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
				super.onScrollStateChanged(recyclerView, newState);
				presenter.detectDataEndMessage(recyclerView, adapterGo, dataGo);
			}

			@Override
			public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
				super.onScrolled(recyclerView, dx, dy);


				int lastVisibleItemPosition = layoutManagerGo.findLastVisibleItemPosition();
				presenter.detectLoadMoreData(adapterGo, lastVisibleItemPosition, dataGo, BusDetailPresenter.GO);
			}
		});

		recyclerView_tab2.addOnScrollListener(new RecyclerView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
				super.onScrollStateChanged(recyclerView, newState);
				presenter.detectDataEndMessage(recyclerView, adapterBack, dataBack);
			}

			@Override
			public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
				super.onScrolled(recyclerView, dx, dy);


				int lastVisibleItemPosition = layoutManagerBack.findLastVisibleItemPosition();
				presenter.detectLoadMoreData(adapterBack, lastVisibleItemPosition, dataBack, BusDetailPresenter.BACK);
			}
		});

		tab1.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				presenter.requestData(city,routeName,routeID);
				presenter.updateStatus(tv_status,0);
				handler.removeCallbacksAndMessages(null);
				handler.post(runUpdateData);
			}
		});
		tab2.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				presenter.requestData(city,routeName,routeID);
				presenter.updateStatus(tv_status,0);
				handler.removeCallbacksAndMessages(null);
				handler.post(runUpdateData);
			}
		});

		handler = new Handler();
		runUpdateStatus = new Runnable() {
			@Override
			public void run() {
				if (presenter.updateStatus(tv_status) == presenter.UPDATESEC) {
					presenter.requestData(city, routeName, routeID);
					handler.post(runUpdateData);
				} else {
					handler.postDelayed(this, 1000);
				}
			}
		};
		runUpdateData = new Runnable() {
			@Override
			public void run() {
				if(presenter.askBusDetailTask.resultBack()!= null){
					dataGo = presenter.askBusDetailTask.resultBack().get(0);
					dataBack = presenter.askBusDetailTask.resultBack().get(1);
					presenter.updateData(adapterGo,adapterBack,dataGo,dataBack);
					tab1.setRefreshing(false);
					tab2.setRefreshing(false);
					handler.post(runUpdateStatus);
				}else{
					handler.postDelayed(this,100);
				}
			}
		};
		handler.postDelayed(runUpdateStatus,1000);
	}

}
