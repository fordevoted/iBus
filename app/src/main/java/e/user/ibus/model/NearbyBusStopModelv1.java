package e.user.ibus.model;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import java.util.List;
import java.util.Map;

import e.user.ibus.Presenter.NearbyBusStopPresenter;
import e.user.ibus.R;
import e.user.ibus.View.NearbyBusStop;

public class NearbyBusStopModelv1 implements NearbyBusStopPresenter {

	private Context context;
	private Activity activity;
	private ArrayAdapter<CharSequence> spinnerAdapter;
	private ListAdapter listAdapter;
	public AskNearbyBusStopTask askNearbyBusStopTask;
	private final static String NEARBYBUSSTOPTAG = "Nearby Bus Stop Test";


	public NearbyBusStopModelv1(Context context) {
		this.context = context;
	}
	public NearbyBusStopModelv1(Context context, Activity activity){
		this.context = context;
		this.activity = activity;
	}

	@Override
	public void setTitleText(TextView tv_title, String text) {
		tv_title.setText(text);
	}

	@Override
	public void setSpinner(Spinner spinner) {
		spinnerAdapter = ArrayAdapter.createFromResource(
				context, R.array.choose_city_array,R.layout.spinner_item_layout );
		spinner.setAdapter(spinnerAdapter);
	}

	@Override
	public void showNearbyBusStopData(LinearLayout container, ListView listView, List<Map<String, Object>> data) {
		listAdapter = new ListViewAdapter(activity, R.layout.busstop_item_layout,data);
		listView.setAdapter(listAdapter);
		container.setVisibility(View.VISIBLE);
	}


	@Override
	public double[] requestLocation() {

		LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

		//Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		final double[] longitude = new double[1];//{location.getLongitude()};
		final double[] latitude = new double[1];//{location.getLatitude()};

		final LocationListener locationListener = new LocationListener() {
			public void onLocationChanged(Location location) {
				longitude[0] = location.getLongitude();
				latitude[0] = location.getLatitude();
			}

			@Override
			public void onStatusChanged(String s, int i, Bundle bundle) {
			}

			@Override
			public void onProviderEnabled(String s) {
			}

			@Override
			public void onProviderDisabled(String s) {
			}
		};

		if (ActivityCompat.checkSelfPermission(context,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
				ActivityCompat.checkSelfPermission(context,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			Toast.makeText(context,"location permission not granted",Toast.LENGTH_SHORT).show();
			return new double[2];
		}else{
			Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			longitude[0] = location.getLongitude();
			latitude[0] = location.getLatitude();
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locationListener);
		}

			Log.d(NEARBYBUSSTOPTAG,"long/lat : "+ longitude[0] + "  " + latitude[0]);
			//for test
			//longitude[0] = 121.5622835;
			//latitude[0] = 25.0339687;
			return new double[]{latitude[0],longitude[0]};
	}

	@Override
	public void requestData(double[] location, String city,int radius) {
		askNearbyBusStopTask = new AskNearbyBusStopTask();
		askNearbyBusStopTask.execute("https://ptx.transportdata.tw/MOTC/v2/Bus/Stop/City/" +
				city +"?$top=50&$spatialFilter=nearby(StopPosition%2C"+location[0]+"%2C"+location[1]+"%2C"+radius+")&$format=JSON\n");
	}

	@Override
	public String getSelectedCityName(int position) {

		switch(position){
			case 0 :{
				return "";
			}
			case 1 :{
				return "Taipei";
			}
			case 2 :{
				return "TaoYuan";
			}
			case 3 :{
				return "TaiChung";
			}
			case 4 :{
				return "Kaohsiung";
			}
		}
		return "";
	}

	@Override
	public void foldKeyboard(Activity activity) {
		InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(activity.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(),0);
	}

	@Override
	public void initEditText(EditText editText) {
		new Handler().postDelayed(()->{
			editText.setImeOptions(EditorInfo.IME_ACTION_DONE);
			editText.setSingleLine();
		},100);
	}


	public static boolean hasPermissions(Context context, String... permissions) {
		if (context != null && permissions != null) {
			for (String permission : permissions) {
				if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
					return false;
				}
			}
		}
		return true;
	}
}
