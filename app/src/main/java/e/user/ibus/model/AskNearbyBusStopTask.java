package e.user.ibus.model;

import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.icu.util.TimeZone;
import android.os.AsyncTask;
import android.util.Log;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import e.user.ibus.View.MainActivity;

public class AskNearbyBusStopTask extends AsyncTask<String,Integer,String> {
	private final static String TAG = "ANBSTask test ";
	private final static String EMPTYFILE = "[]\n";
	private boolean isLoadingEnd = false;
	private List<Map<String,Object>> data;

	@Override
	protected String doInBackground(String... urls) {
		return GET(urls[0]);
	}

	@Override
	protected void onPostExecute(String result){
		Log.d(TAG,"'"+result+"'");
		if(result.equals(EMPTYFILE)){
			data = new ArrayList<>();
			Map<String,Object> m = new HashMap();
			m.put("StopName","查無站點，嘗試擴大範圍半徑或是更換城市");
			m.put("PositionLat",-1);
			m.put("PositionLon",-1);
			data.add(m);
			isLoadingEnd = data.size() != 0 ;
			Log.d(TAG,"normal end with data empty");
			return;
		}
		if(result.contains("Exception")){
			data = new ArrayList<>();
			Map<String,Object> m = new HashMap();
			m.put("StopName","請檢察資料是否正確選取，如已正確選取，可能為伺服端出現問題，請稍後再試");
			m.put("PositionLat",-1);
			m.put("PositionLon",-1);
			data.add(m);
			isLoadingEnd = data.size() != 0 ;
			Log.d(TAG,"normal end with data empty");
			return;
		}
		try {
			JSONArray jsonArray = new JSONArray(result);
			data = new ArrayList<>();
			for(int i = 0 ; i < jsonArray.length() ; i++){
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				HashMap<String,Object> m  = new HashMap();
				boolean continueFlag = false;
				for(Map<String,Object> h :data ){
					if(	h.get("StopName").toString().equals(
							jsonObject.getJSONObject("StopName").getString("Zh_tw"))){
						continueFlag = true;
						break;
					}
				}
				if(continueFlag){
					continue;
				}
				m.put("StopName",jsonObject.getJSONObject("StopName").getString("Zh_tw"));
				m.put("PositionLat",jsonObject.getJSONObject("StopPosition").getDouble("PositionLat"));
				m.put("PositionLon",jsonObject.getJSONObject("StopPosition").getDouble("PositionLon"));

				//Log.d(TAG,"data add m and m size is " + m.size());
				data.add(m);
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
			Log.d(TAG,APIUrl);
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
				//Log.d(TAG,result);
			}
			else
				result = "Did not work!";
			return  result;
		} catch (Exception e) {
			Log.d("ATask InputStream", e.getClass().getCanonicalName());
			e.printStackTrace();
			result = e.getClass().getCanonicalName();
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
