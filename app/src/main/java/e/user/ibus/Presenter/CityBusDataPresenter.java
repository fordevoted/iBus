package e.user.ibus.Presenter;

import android.app.Activity;
import android.content.Context;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.List;
import java.util.Map;

import e.user.ibus.model.RecyclerViewAdapter;

public interface CityBusDataPresenter {
	int FOLDKEYBOARD = 0;
	int EXPENDKEYBOARD = 1;
	String QUERY = "路線查詢";
	void setTitleText(TextView title, String text);
	void setTitleText(TextView title, String compareText, String text);
	void openKeyboard(TextView title,int type, LinearLayout container,
					  LinearLayout keyboardContainer, ImageButton ib_keyboardUp,
					  ConstraintLayout keyboard,Activity activity);
	void closeKeyboard(ImageButton ib_keyboardUp, LinearLayout container);
	void closeKeyboard(Activity activity, ImageButton ib_keyboardUp, LinearLayout container);
	void changeKeyboardType(int type, LinearLayout container,
							LinearLayout keyboardContainer, ImageButton ib_keyboardUp,
							ConstraintLayout keyboard);

	void requestData(String location,RecyclerViewAdapter adapter, final List<Map<String,Object>> data,SwipeRefreshLayout swipeRefreshLayout);
	void initData(RecyclerViewAdapter adapter, SwipeRefreshLayout swipeRefreshLayout, List<Map<String,Object>> data);
	void getData(RecyclerViewAdapter adapter, List<Map<String,Object>> data);
	List<Map<String,Object>> changeData(List<Map<String,Object>> finalData, String containKeyword, RecyclerViewAdapter adapter);
	void detectLoadMoreData(RecyclerViewAdapter adapter, int lastVisibleItemPosition,
							SwipeRefreshLayout swipeRefreshLayout, List<Map<String,Object>> data);
	void detectDataEndMessage(Context context, RecyclerView recyclerView,RecyclerViewAdapter adapter ,List<Map<String,Object>> data);
}
