package com.coolweather.app.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.coolweather.app.util.HttpCallbackListener;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.Utility;
import com.example.coolweather.R;

public class WeatherActivity extends Activity {
	
	private LinearLayout weatherInfoLayout;
	private TextView cityNameText;
	private TextView publishText;
	private TextView weatherDespText;
	private TextView temp1Text;
	private TextView temp2Text;
	
	private TextView currentDateText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_layout);
		
		//��ʼ���ؼ�
		weatherInfoLayout=(LinearLayout) findViewById(R.id.weather_info_layout);
		cityNameText=(TextView) findViewById(R.id.city_name);
		publishText=(TextView) findViewById(R.id.publish_text);
		weatherDespText=(TextView) findViewById(R.id.weather_disp);
		temp1Text=(TextView) findViewById(R.id.temp1);
		temp2Text=(TextView) findViewById(R.id.temp2);
		currentDateText=(TextView) findViewById(R.id.current_date);
		
		String countyCode=getIntent().getStringExtra("county_code");
		Log.d("WeatherActivity", "�ؼ����룺"+countyCode);
		if(!TextUtils.isEmpty(countyCode)){
			publishText.setText("ͬ����...");
			weatherInfoLayout.setVisibility(View.INVISIBLE);
			cityNameText.setVisibility(View.VISIBLE);
			queryWeatherCode(countyCode);
		}else{
			showWeather();
		}
	}

	private void queryWeatherCode(String countyCode) {
		String address="http://www.weather.com.cn/data/list3/city"+countyCode+".xml";
		queryWeatherFromServer(address,"countyCode");
		
	}
	private void queryWeatherInfo(String weatherCode) {
		String address="http://www.weather.com.cn/data/cityinfo/"+weatherCode+".html";
		queryWeatherFromServer(address,"weatherCode");
		
	}

	private void queryWeatherFromServer(final String address, final String type) {
		
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener(){

			@Override
			public void onFinish(final String response) {
				
				if("countyCode".equals(type)){
					if(!TextUtils.isEmpty(response)){
						String []array=response.split("\\|");
						if(array!=null&&array.length==2){
							String weatherCode=array[1];
							Log.d("WeatherActivity","��������Ϊ�� "+weatherCode);
							queryWeatherInfo(weatherCode);
						}
					}
				}else if("weatherCode".equals(type)){
					Utility.handleWeatherResponse(WeatherActivity.this, response);
					runOnUiThread(new Runnable(){

						@Override
						public void run() {
							showWeather();
						}

						
					});
				}
				
			}

			@Override
			public void onError(final Exception e) {
				runOnUiThread(new Runnable(){

					@Override
					public void run() {
						publishText.setText("ͬ��ʧ��...");
						Log.d("WeatherActivity", e.toString());
						
					}
					
				});
				
			}
			
		});
		
	}

	

	private void showWeather() {
		SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(this);
		cityNameText.setText(prefs.getString("city_name", ""));
		temp1Text.setText(prefs.getString("temp1", ""));
		temp2Text.setText(prefs.getString("temp2", ""));
		weatherDespText.setText(prefs.getString("weatherDesp", ""));
		publishText.setText("����"+prefs.getString("publish_time", "")+"����");
		currentDateText.setText(prefs.getString("current_date", ""));
		weatherInfoLayout.setVisibility(View.VISIBLE);
		cityNameText.setVisibility(View.VISIBLE);

	}

	
}
