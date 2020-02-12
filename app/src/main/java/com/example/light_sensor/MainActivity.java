package com.example.light_sensor;

import androidx.appcompat.app.AppCompatActivity;

import com.example.light_sensor.Data;

import com.example.light_sensor.DynamicLineChartManager;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity  implements SensorEventListener {

    private DynamicLineChartManager dynamicLineChartManager1;
    private LineChart mChart1;
    private List<Integer> list = new ArrayList<>();
    private  String name = "光照";
    private Integer color = Color.BLUE;

    private TextView sun_tv;

    private TextView textView;
    private Data data;

    private SensorManager mSensorManager;
    private Sensor mPressure;


    /**
    * 初始化数据
    */
    private void initData(){
        data  = new Data();
        dynamicLineChartManager1 = new DynamicLineChartManager(mChart1,name,color);
    }

    /**
     * 绑定控件
     * @param
     */
    private void bindView(){
        textView = (TextView)findViewById(R.id.textView);
        sun_tv = (TextView)findViewById(R.id.sun_tv);
        mChart1 = (LineChart)findViewById(R.id.dynamic_chart1);
    }


    /**
     * 按钮监听
     */
    private void  initEvent() {
        //死循环添加数据
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                            list.add((int) (Math.random());
                            long a =Integer.parseInt(textView.getText().toString().trim());
                            System.out.println("金继平"+textView.getText());
                            Log.d( "textView的值",(String) textView.getText());
                            data.setSun((int) (float) a);
                            sun_tv.setText(String.valueOf(data.getSun()));
                            dynamicLineChartManager1.addEntry(a);
                            list.clear();
                        }
                    });
                }
            }
        }).start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindView();
        initData();
        initEvent();
        mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        mPressure = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
}

    @Override
    public final void onAccuracyChanged(Sensor sensor,int accuracy){
        //Do something with this sensor data
    }

    @Override
    public final void onSensorChanged(SensorEvent event){
    int light = (int) event.values[0];
        TextView v = (TextView)findViewById(R.id.textView);
        v.setText(Integer.toString(light));
    }

    @Override
    protected void onResume(){
        super.onResume();
        mSensorManager.registerListener(this,mPressure,SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause(){
        super.onPause();
        mSensorManager.unregisterListener(this);
    }
}
