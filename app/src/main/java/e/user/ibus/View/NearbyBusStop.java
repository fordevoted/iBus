package e.user.ibus.View;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;


import butterknife.ButterKnife;
import butterknife.InjectView;
import e.user.ibus.R;
import e.user.ibus.model.NearbyBusStopModelv1;

public class NearbyBusStop extends AppCompatActivity {

	@InjectView(R.id.toolbar1)
	Toolbar toolbar;
	@InjectView(R.id.spinner)
	Spinner spinner;
	@InjectView(R.id.et_radius)
	EditText et_radius;
	@InjectView(R.id.tv_title)
	TextView tv_title;
	@InjectView(R.id.bn_send)
	Button bn_send;
	@InjectView(R.id.container)
	LinearLayout container;
	@InjectView(R.id.listView)
	ListView listView;

	private NearbyBusStopModelv1 presenter;
	private double[] location;
	private String city;
	private String radius;
	private final static int DEFAULTRADIUS =  200;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.nearby_bus_stop_layout);
		ButterKnife.inject(this);
		initView();
	}

	public void initView(){

		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		toolbar.setNavigationOnClickListener((toolbar)->{finish();});

		presenter = new NearbyBusStopModelv1(getApplicationContext(),this);
		presenter.setSpinner(spinner);
		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
					 city = presenter.getSelectedCityName(position);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				Toast.makeText(getApplicationContext(),"請選擇城市",Toast.LENGTH_SHORT).show();
			}
		});
		presenter.setTitleText(tv_title,"查詢附近站點");
		location = presenter.requestLocation();
		Handler handler = new Handler();
		Runnable run = new Runnable() {
			@Override
			public void run() {
				if(presenter.askNearbyBusStopTask.resultBack()!= null){
					presenter.showNearbyBusStopData(container, listView, presenter.askNearbyBusStopTask.resultBack());
				}else{
					handler.postDelayed(this,100);
				}
			}
		};
		//presenter.initEditText(et_radius);
		et_radius.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				radius = et_radius.getText().toString();
			}
			@Override
			public void afterTextChanged(Editable s) { }
		});

		bn_send.setOnClickListener((bn_send)-> {
					handler.postDelayed(run, 100);
					int userInputRadius;
					try{
						userInputRadius = Integer.parseInt(radius);
					}catch (NumberFormatException e){
						userInputRadius = DEFAULTRADIUS;
					}
					presenter.foldKeyboard(this);
					presenter.requestData(location, city, userInputRadius);
				});
	}
}
