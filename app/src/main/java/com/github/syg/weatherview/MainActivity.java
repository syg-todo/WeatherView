package com.github.syg.weatherview;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    /**
     * 1晴 2多云 3阴 4雾 5小雨 6中雨 7大雨 8暴雨 9雷阵雨 10冻雨 11雨夹雪 12小雪 13中雪 14大-暴雪 15霜冻
     */

    private HoursView viewHours;
    private HoursYAxisView hoursYAxisView;
    private int[] temps;
    private int[] winds;
    private int[] airs;
    private int[] weathers;
    private ParentHorizontalScrollView svHours;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewHours = findViewById(R.id.view_hours);
        hoursYAxisView = findViewById(R.id.y_axis_view);
        hoursYAxisView.setmHoursView(viewHours);
        svHours = findViewById(R.id.sv_hours);
        svHours.setOnScrollListener(new ParentHorizontalScrollView.OnScrollListener() {
            @Override
            public void onScrollChanged(int x, int y, int oldX, int oldY) {
                viewHours.onHoursScrollChanged(x, y, oldX, oldY);
            }
        });

        viewHours.post(new Runnable() {
            @Override
            public void run() {
                viewHours.fillData(fakeList());
            }
        });
    }

    List<HourWeather> fakeList() {
        Log.d("111","fakeList");
        List<HourWeather> mDataList = new ArrayList<>();
        temps = new int[]{
                7, 7, 6, 6, 5, 5, 5, 5, 4, 4, 4, 4, 5, 7, 8, 9, 10, 10, 9, 8, 7, 7, 6, 6
        };
        winds = new int[]{
                3, 1, 3, 3, 3, 3, 3, 3, 3, 3, 3, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 3, 4
        };
        airs = new int[]{
                30, 30, 30, 500, 30, 55, 31, 32, 122, 39, 200, 155, 47, 49, 233, 444, 48, 48, 49, 52, 55, 58, 60, 61
        };
        weathers = new int[]{
                1, 1, 1, 2, 2, 3, 2, 4, 4, 4, 4, 4, 4, 2, 2, 1, 1, 2, 3, 3, 2, 2, 2, 1
        };

        for (int i = 0; i < 24; i++) {
            HourWeather hourWeather = new HourWeather();
            hourWeather.setAir(airs[i]);
            hourWeather.setWeather(weathers[i]);
            hourWeather.setTemp(temps[i]);
            hourWeather.setWind(winds[i]);
            mDataList.add(hourWeather);
        }
        return mDataList;
    }
}
