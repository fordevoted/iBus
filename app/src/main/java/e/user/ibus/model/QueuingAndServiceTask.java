package e.user.ibus.model;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import e.user.ibus.View.MainActivity;

import static e.user.ibus.model.AskBusRouteTask.getServerTime;


public class QueuingAndServiceTask extends AsyncTask<String, Void, String> {

	private  static final  String TAG = "RQASTask test";
	private String response;
	private boolean isFinish = false;
	private int response_state = -1 ;
	private String city;
	private String routeName;
	private String direction;
	private String stopName;
	private boolean service;

	public QueuingAndServiceTask(String city, String routeName, String direction, String stopName, boolean service){
			this.city = city;
			this.routeName = routeName;
			this.direction = direction;
			this.stopName = stopName;
			this.service = service;
	}

	@Override
	protected String doInBackground(String... urls) {
		//Log.d("PTask direction test in background", String.valueOf(direction[0]));

		return POST(urls[0]);
	}

	// onPostExecute displays the results of the AsyncTask.
	@Override
	protected void onPostExecute(String result) {
		//Log.d("PTask End Of Execute", "processing json ");
		Log.d(TAG, "processing json result: " + result);
		isFinish = true;
	}

	public boolean resultBack(){
		return isFinish;
	}

	private String POST(String APIUrl) {
		String result = "";
		HttpURLConnection connection = null;
		String xdate = getServerTime();
		String SignDate = "x-date: " + xdate;
		String Signature="";
		//取得加密簽章
		try {
			URL url = new URL(APIUrl);
			if("https".equalsIgnoreCase(url.getProtocol())){
				SslUtils.ignoreSsl();
			}
			connection = (HttpURLConnection)url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("x-date", xdate);
			connection.setRequestProperty("authentication", MainActivity.Authentication);
			connection.setDoInput(true);
			connection.setDoOutput(true);
			DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append("city=").append(URLEncoder.encode(city, "UTF-8")).append("&");
			stringBuilder.append("routeName=").append(URLEncoder.encode(routeName, "UTF-8")).append("&");
			stringBuilder.append("Direction=").append(URLEncoder.encode(direction, "UTF-8")).append("&");
			stringBuilder.append("StopName=").append(URLEncoder.encode(stopName, "UTF-8")).append("&");
			stringBuilder.append("service=").append(URLEncoder.encode(String.valueOf(service), "UTF-8"));
			Log.d(TAG,stringBuilder.toString());
			outputStream.writeBytes(stringBuilder.toString());
			outputStream.flush();
			outputStream.close();

			// receive response as inputStream)
			InputStream inputStream = connection.getInputStream();
			//InputStream inputStream = connection.getErrorStream();
			int status = connection.getResponseCode();
			Log.d(TAG, String.valueOf(status));
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
				/*Type busRouteStationListType = new TypeToken<ArrayList<BusRouteStationData>>(){}.getType();
				Gson gsonReceiver = new Gson();
				List<BusRouteStationData> obj = gsonReceiver.fromJson(result, busRouteStationListType);
				for(BusRouteStationData b : obj){
					Log.d(TAG,b.RouteName);
				}
				Log.d(TAG,result);*/
				//result = convertInputStreamToString(inputStream);
				//Log.d(TAG, inputStream.toString());
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


	private String convertInputStreamToString(InputStream inputStream) throws IOException {
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
		String line = "";
		String result = "";
		while ((line = bufferedReader.readLine()) != null) {
			result += line;
		}
		inputStream.close();
		return result;
	}

}