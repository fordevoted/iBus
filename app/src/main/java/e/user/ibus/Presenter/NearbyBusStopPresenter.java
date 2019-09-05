package e.user.ibus.Presenter;

import android.app.Activity;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

public interface NearbyBusStopPresenter {
	void setTitleText(TextView tv_title, String text);
	void setSpinner(Spinner spinner);
	void showNearbyBusStopData(LinearLayout container, ListView listView, List<Map<String,Object>> data);
	double[] requestLocation();
	void requestData(double[] location,String city,int radius);
	String 	getSelectedCityName(int position);
	void foldKeyboard(Activity activity);
	void initEditText(EditText editText);

}
