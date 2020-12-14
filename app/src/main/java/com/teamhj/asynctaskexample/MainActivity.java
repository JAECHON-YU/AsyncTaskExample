package com.teamhj.asynctaskexample;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import gun0912.ted.tedadmobdialog.OnBackPressListener;
import gun0912.ted.tedadmobdialog.TedAdmobDialog;

import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.log;
import static java.lang.Math.pow;
import static java.lang.Math.sin;
import static java.lang.Math.tan;

public class MainActivity extends AppCompatActivity {

    TextView temp_Text;
    private AdView adView;

    public static final String AD_TEST_KEY_NATIVE = "ca-app-pub-3940256099942544/2247696110";

    TedAdmobDialog nativeTedAdmobDialog;

    // Hi Just Test!!

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //temp_Text = findViewById(R.id.textView4);
        //temp_Text.setText("안녕하세요");

        ((TextView) findViewById(R.id.textView)).setText("헬로");



        //(new ParseURL()).execute("457303100");
        //((ProgressBar) findViewById(R.id.progress)).setVisibility(View.VISIBLE);

        ((TextView) findViewById(R.id.textView)).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                (new ParseURL()).execute("457303100");
                ((ProgressBar) findViewById(R.id.progress)).setVisibility(View.VISIBLE);

                return false;
            }
        });

        ((Button) findViewById(R.id.button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                (new ParseURL()).execute("457303100");
                ((ProgressBar) findViewById(R.id.progress)).setVisibility(View.VISIBLE);
            }
        });

        // Initialize the Mobile Ads SDK.
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {}
        });

        // Set your test devices. Check your logcat output for the hashed device ID to
        // get test ads on a physical device. e.g.
        // "Use RequestConfiguration.Builder().setTestDeviceIds(Arrays.asList("ABCDEF012345"))
        // to get test ads on this device."
        MobileAds.setRequestConfiguration(
                new RequestConfiguration.Builder().setTestDeviceIds(Arrays.asList("ABCDEF012345"))
                        .build());

        // Gets the ad view defined in layout/ad_fragment.xml with ad unit ID set in
        // values/strings.xml.
        adView = findViewById(R.id.ad_view);

        // Create an ad request.
        AdRequest adRequest = new AdRequest.Builder().build();

        // Start loading the ad in the background.
        adView.loadAd(adRequest);


        setNativeAds();

    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        nativeTedAdmobDialog.show();
    }

    private void setNativeAds() {
        nativeTedAdmobDialog = new TedAdmobDialog.Builder(MainActivity.this, TedAdmobDialog.AdType.NATIVE, AD_TEST_KEY_NATIVE)
                .setOnBackPressListener(new OnBackPressListener() {
                    @Override
                    public void onReviewClick() {

                    }

                    @Override
                    public void onFinish() {
                        finish();
                    }

                    @Override
                    public void onAdShow() {
                        nativeTedAdmobDialog.loadNative();
                    }
                })
                .create();



        nativeTedAdmobDialog.loadNative();

    }

    public class ParseURL extends AsyncTask<String, Void, WeatherData> {

        @Override
        protected WeatherData doInBackground(String... strings) {


            //WeatherInfo mWeatherInfo = new WeatherInfo();

            WeatherData weatherData = new WeatherData();

            try {


                String urlForecast = "https://www.weather.go.kr/w/wnuri-fct/main/vshort.do?caller=default&code="
                        +strings[0]+"&unit=km%2Fh&theme=Dark&ext=Y";
                Log.d("TEST", urlForecast);
                Document doc = Jsoup.connect(urlForecast).get();

                //Log.d("TEST", doc.text());


                String temperature = doc.select("div.temp").get(0).text();
                Log.d("TEST", temperature);

                String humidity = doc.select("td").get(1).text();
                Log.d("TEST", humidity);

                String wind = doc.select("td").get(3).text();
                Log.d("TEST", wind);

                //String time = doc.select("span.time").get(1).text();
                //Log.d("TEST", time);

                //String imageUrl = "http://www.weather.go.kr"
                //        +doc.select("img").get(7).attr("src").toString();
                //imageUrl = imageUrl.substring(0,63);
                //Log.d("TEST", imageUrl);


                Elements arrayWeatherItem = doc.select("div.weather-item");


                ArrayList<String> timeArray = new ArrayList<>();
                ArrayList<String> imageUrlArray = new ArrayList<>();
                for (int i = 1 ; i<arrayWeatherItem.size(); i++) {
                    String time = arrayWeatherItem.get(i).select("span.time").text();
                    Log.d("TEST",time);
                    String imageUrl = "http://www.weather.go.kr"+arrayWeatherItem.get(i).select("img")
                            .attr("src").substring(0,39);
                    Log.d("TEST",imageUrl);
                    timeArray.add(time);
                    imageUrlArray.add(imageUrl);
                }



                weatherData.temperature_now = temperature;
                weatherData.humidity_now = humidity;
                weatherData.imageUrl = imageUrlArray;
                weatherData.wind = wind;
                weatherData.time = timeArray;


                Elements forecastList = doc.select("div.weather-item");
                ArrayList<String> forecastString = new ArrayList<String>();
                for (int i=1;i<forecastList.size();i++) {
                    String forecast_text = forecastList.get(i).select("img").get(0).attr("alt");
                    forecastString.add(forecast_text);
                }
                Log.d("TEST", forecastString.toString());
                weatherData.forecast_temp = forecastString;

            } catch (Throwable t) {
                t.printStackTrace();
            }


            return weatherData;
        }


        @Override
        protected void onPostExecute(WeatherData mWeatherInfo2) {
            super.onPostExecute(mWeatherInfo2);

            ((TextView) findViewById(R.id.textView)).setText(mWeatherInfo2.temperature_now);
            ((TextView) findViewById(R.id.textView2)).setText(mWeatherInfo2.humidity_now);
            //((TextView) findViewById(R.id.textView3)).setText(mWeatherInfo2.forecast_temp.toString());
            ((TextView) findViewById(R.id.textView4)).setText(mWeatherInfo2.wind);
            //((TextView) findViewById(R.id.textView5)).setText(mWeatherInfo2.time);

            //ImageView imageView = findViewById(R.id.imageView);
            //Glide.with(MainActivity.this).load(mWeatherInfo2.imageUrl).into(imageView);



            LinearLayout mLinear2 = (LinearLayout) findViewById(R.id.forcast);

            for (int i = 0; i < mWeatherInfo2.forecast_temp.size(); i++) {
                LinearLayout linearLayout = new LinearLayout(getApplicationContext());
                linearLayout.setOrientation(LinearLayout.VERTICAL);

                //사간
                TextView timeText = new TextView(getApplicationContext());
                timeText.setText(mWeatherInfo2.time.get(i));
                timeText.setGravity(Gravity.CENTER);
                linearLayout.addView(timeText);


                //이미지
                ImageView imageView = new ImageView(getApplicationContext());

                if (i%2==0) {
                    imageView.setImageResource(R.mipmap.wi_day_hail_1);
                } else {
                    Glide.with(MainActivity.this).load(mWeatherInfo2.imageUrl.get(i)).into(imageView);
                }
                linearLayout.addView(imageView);

                // 맑음
                TextView textViewForecast = new TextView(getApplicationContext());
                textViewForecast.setText(mWeatherInfo2.forecast_temp.get(i));
                textViewForecast.setGravity(Gravity.CENTER);
                linearLayout.addView(textViewForecast);


                mLinear2.addView(linearLayout);

                // 간격
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        5,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                params.setMargins(20, 20, 20, 20);

                LinearLayout distance = new LinearLayout(getApplicationContext());
                distance.setLayoutParams(params);
                

                mLinear2.addView(distance);

            }


            ((ProgressBar) findViewById(R.id.progress)).setVisibility(View.INVISIBLE);


        }
    }
}

class WeatherData {

    String temperature_now;
    ArrayList<String> forecast_temp;
    String humidity_now;
    ArrayList<String> imageUrl;
    String wind;
    ArrayList<String> time;

}