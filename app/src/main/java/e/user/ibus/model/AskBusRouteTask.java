package e.user.ibus.model;

import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.icu.util.TimeZone;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import e.user.ibus.View.BusRoute_menu;
import e.user.ibus.View.MainActivity;

public class AskBusRouteTask extends AsyncTask<String,Integer,String> {
	private final static String TAG = "ATask test ";
	private boolean isLoadingEnd = false;
	private List<Map<String,Object>> data;

	@Override
	protected String doInBackground(String... urls) {
		return GET(urls[0]);
	}

	@Override
	protected void onPostExecute(String result){
		Log.d(TAG,result);
		try {
			JSONArray jsonArray = new JSONArray(result);
			data = new ArrayList<>();
			for(int i = 0 ; i < jsonArray.length() ; i++){
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				HashMap<String,Object> m  = new HashMap();
				boolean continueFlag = false;
				for(Map<String,Object> h :data ){
					if(	h.get("RouteName").toString().equals(
							jsonObject.getJSONArray("SubRoutes").getJSONObject(0).getJSONObject("SubRouteName").getString("Zh_tw"))){
						continueFlag = true;
						break;
					}
				}
				if(continueFlag){
					continue;
				}
				m.put("RouteName",jsonObject.getJSONArray("SubRoutes").getJSONObject(0).getJSONObject("SubRouteName").getString("Zh_tw"));
				m.put("RouteID",jsonObject.getString("RouteID"));
				m.put("Direction0",jsonObject.getString("DestinationStopNameZh"));
				m.put("Direction1",jsonObject.getString("DepartureStopNameZh"));
				if(jsonObject.getJSONArray("SubRoutes").getJSONObject(0).has("Headsign")){
					m.put("Headsign",jsonObject.getJSONArray("SubRoutes").getJSONObject(0).getString("Headsign"));
				}else{
					String headSign = jsonObject.getString("DepartureStopNameZh")+" <-> " + jsonObject.getString("DestinationStopNameZh");
					m.put("Headsign",headSign);
				}
				//Log.d(TAG,"data add m and m size is " + m.size());
				if(i == 0){
					data.add(m);
				}else{
					for(int j = 0 ; j < i ; j++){
						//Log.d(TAG,"i is: "+ i + " data.szie: " + data.size() + "  j: " + j );
						if(data.get(j).get("RouteName").toString().compareTo(m.get("RouteName").toString()) > 0){
							data.add(j,m);
							//Log.d(TAG, String.valueOf(j));
							break;
						}else if ( j == i-1){
							//Log.d(TAG, j+" is data size end ");
							data.add(m);
						}
					}
				}
			}
			isLoadingEnd = data.size() != 0 ;
			Log.d(TAG , String.valueOf(data.size()));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	private String GET(String APIUrl) {
		String result = "";
		HttpURLConnection connection = null;
		String xdate = getServerTime();
		String SignDate = "x-date: " + xdate;
		String Signature="";
		//取得加密簽章
		Signature = HMAC_SHA1.Signature(SignDate, MainActivity.APPKey);
		Log.d(TAG,Signature);
		Log.d(TAG,SignDate);
		String sAuth = "hmac username=\"" + MainActivity.APPID + "\", algorithm=\"hmac-sha1\", headers=\"x-date\", signature=\"" + Signature + "\"";
		try {
			URL url = new URL(APIUrl);
			if("https".equalsIgnoreCase(url.getProtocol())){
				SslUtils.ignoreSsl();
			}
			connection = (HttpURLConnection)url.openConnection();
			connection.setRequestMethod("GET");
			connection.setRequestProperty("Authorization", sAuth);
			connection.setRequestProperty("x-date", xdate);
			//connection.setRequestProperty("Accept","*/*");
			//connection.setRequestProperty("Accept-Encoding", "gzip");
			connection.setDoInput(true);
			//connection.setDoOutput(false);

			// receive response as inputStream
			InputStream inputStream = connection.getInputStream();

			// convert inputstream to string
			if(inputStream != null){
				InputStreamReader reader = new InputStreamReader(inputStream,"UTF-8");
				BufferedReader in = new BufferedReader(reader);

				//讀取回傳資料
				String line="";
				while ((line = in.readLine()) != null) {
					result += (line+"\n");
				}

				Log.d(TAG,result);

			}
			else
				result = "Did not work!";
			return  result;
		} catch (Exception e) {
			Log.d("ATask InputStream", e.getLocalizedMessage());
			e.printStackTrace();
			return result;
		}
	}

	public List<Map<String,Object>> resultBack(){
		if(!isLoadingEnd){
			return null;
		}else{
			Log.d(TAG, "data size" + data.size());
			return data;
		}
	}
	private String convertInputStreamToString(InputStream inputStream) throws IOException {
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.ISO_8859_1));
		String line = "";
		String result = "";
		while ((line = bufferedReader.readLine() )!= null) {
			result += line;
		}
		inputStream.close();
		return result;
	}
	public static String getServerTime() {
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		return dateFormat.format(calendar.getTime());
	}
}
