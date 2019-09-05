package e.user.ibus.View;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import butterknife.ButterKnife;
import butterknife.InjectView;
import e.user.ibus.R;

public class BusRoute_menu extends AppCompatActivity {
	private final static String TAG = "BusRouteMenu Test:";
	@InjectView(R.id.bn_bus_Kaohsiung)
	Button bn_bus_Kaohsiung;
	@InjectView(R.id.bn_bus_Taichung)
	Button bn_bus_Taichung;
	@InjectView(R.id.bn_bus_TaoYuan)
	Button bn_bus_TaoYuan;
	@InjectView(R.id.bn_bus_Taipei)
	Button bn_bus_Taipei;
	@InjectView(R.id.toolbar1)
	Toolbar toolbar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.busroute_menu_layout);
		ButterKnife.inject(this);
		initView();
	}

	private void initView(){
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		toolbar.setNavigationOnClickListener((toolbar)-> finish());
		bn_bus_TaoYuan.setOnClickListener((bn_bus_TaoYuan) ->{
			Log.d(TAG + " bn_bus_TaoYuan"," click !");
			Intent intent = new Intent(BusRoute_menu.this,CityBus.class);
			intent.putExtra("location","TaoYuan");
			startActivity(intent);
		});
		bn_bus_Kaohsiung.setOnClickListener((bn_bus_Kaohsiung)->{
			Log.d(TAG + " bn_bus_Kaohsiung"," click !");
			Intent intent = new Intent(BusRoute_menu.this,CityBus.class);
			intent.putExtra("location","Kaohsiung");
			startActivity(intent);
		});
		bn_bus_Taipei.setOnClickListener((bn_bus_Taipei)->{
			Log.d(TAG + " bn_bus_Taipei"," click !");
			Intent intent = new Intent(BusRoute_menu.this,CityBus.class);
			intent.putExtra("location","Taipei");
			startActivity(intent);
		});
		bn_bus_Taichung.setOnClickListener((bn_bus_Taichung)->{
			Log.d(TAG + " bn_bus_Taichung"," click !");
			Intent intent = new Intent(BusRoute_menu.this,CityBus.class);
			intent.putExtra("location","Taichung");
			startActivity(intent);
		});

	}
}
