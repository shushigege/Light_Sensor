package com.example.light_sensor;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.charts.LineChart;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

public class MainActivity extends AppCompatActivity implements SensorEventListener, View.OnClickListener {

    private Context  context;

    String filePath,fileName;/**新建两个变量 文件路径 文件名*/
    InputStream inputStreamRawTxt;
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            avg_data_tv.setText(msg.obj.toString());
        }
    };


    /**新添加的内容*/
    //count代表X坐标的下标值，每获取一个数据就递增一次
    private int count=1;
    //xData X轴数据
    List<String> xData = new ArrayList<>();
    //yDaya Y轴数据
    List<Float> yData = new ArrayList<>();
/**新添加的内容*/


    private DynamicLineChartManager dynamicLineChartManager1;
    private LineChart mChart1;
    private List<Integer> list = new ArrayList<>();
    private  String name = "光照";
    private Integer color = Color.BLUE;

    private TextView sun_tv;

    private TextView textView;

    private TextView textView1;
    private Data data;

    private SensorManager mSensorManager;
    private Sensor mPressure;


    /**新添加的内容*/
    //混合图
    private CombinedChart mCombinedChart;
    //最大光照度
    private TextView max_data_tv;
    //最小光照度
    private TextView min_data_tv;
    //平均光照度
    private TextView avg_data_tv;
    private Button text_sun_bt;
    private Button save_chart_bt;
    /**新添加的内容*/



    /**
    * 初始化数据
    */
    private void initData(){
        //context = this;
        data  = new Data();
        dynamicLineChartManager1 = new DynamicLineChartManager(mChart1,name,color);
        //mCombinedChart = findViewById(R.id.combined_chart1);
        List<String> xData = new ArrayList<>();
        //x轴数据
        for(int i = 1;i<=10;i++){
            xData.add(String.valueOf(i));
        }
        //y轴数据/



        /*************************************/
        /**************************************/
        /*************************************/
         text_sun_bt.setOnClickListener(this);
         save_chart_bt.setOnClickListener(this);

         /*************************************/
         filePath = Environment.getDownloadCacheDirectory().getAbsolutePath()
                 +"/Download";
         fileName = "printf.txt";
         inputStreamRawTxt = getResources().openRawResource(R.raw.printf);
    }
    /***********************************************/
    /************************************************/
    /***************************************************/

    /**
     * 绑定控件
     * @param
     */
    private void bindView(){
        textView = (TextView)findViewById(R.id.textView);
        sun_tv = (TextView)findViewById(R.id.sun_tv);
        mChart1 = (LineChart)findViewById(R.id.dynamic_chart1);
        mCombinedChart = (CombinedChart)findViewById(R.id.combined_chart1);
        text_sun_bt = findViewById(R.id.test_sun);
        save_chart_bt = findViewById(R.id.save_chart);
        max_data_tv = (TextView) findViewById(R.id.max_data_tv);
        min_data_tv = (TextView) findViewById(R.id.min_data_tv);
        avg_data_tv = (TextView) findViewById(R.id.avg_data_tv);
    }


    private void initChartData(){
        //x轴数据
//        for (int i = 1; i <= 10; i++) {
        xData.add(String.valueOf(count));

//        }
//y轴数据
//        for (int j = 0; j < 10; j++) {
        yData.add((float)Integer.parseInt (textView.getText().toString().trim()));
        if (count==10){
            float minData = Float.MAX_VALUE;
            float maxData = Float.MIN_VALUE;
            float sum=0;
           // Log.d(TAG, "initChartData: yData.size="+yData.size());
            for (float y:yData){
                minData = Math.min(minData,y);
                maxData = Math.max(maxData,y);
                sum = sum+y;
            }
            sum = (float)sum/10;
            maxData = (int) maxData;
            minData = (int) minData;
            sum = (int) sum;
            max_data_tv.setText(String.valueOf(maxData));
            min_data_tv.setText(String.valueOf(minData));
            avg_data_tv.setText(String.valueOf(sum));
            Log.d("数据分析结果" ,"minData="+minData+"  maxData="+maxData+"  avg="+sum);
        }
        count++;
//        }
//颜色集合
        List<Integer> colors = new ArrayList<>();
        colors.add(Color.LTGRAY);
        colors.add(Color.RED);
        //管理类
        //第二个参数是柱状图
        //第三个参数是折线图
        CombinedChartManager combineChartManager = new CombinedChartManager(mCombinedChart);
        combineChartManager.showCombinedChart(xData,yData, yData,
                "直方图", "线性图", colors.get(0), colors.get(1));
    }


    /**
     * 按钮监听
     */
    private void  initEvent() {
        initChart();

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


        /*****&****************************/
        //判断是否有该文件夹，没有就创建一个，生成文件夹后再生成文件，不然会出错
        makeDirectory(filePath);
        //在线程中执行耗时操作
        new Thread(new Runnable() {
            @Override
            public void run() {
                //判断是否有该文件，没有就创建一个吗，返回ture
                if (createNewFile(filePath,fileName)){
                    //返回true说明创建了新的文件，则写入预定内容，
                    //返回false,则说明文件已存在，不需要写入（也不会进入该判断）

                    //将输入流转换成字符串，写入文件
                     String rawTxtString = Stream2String(inputStreamRawTxt);
                    writeTxtToFile(rawTxtString,filePath,fileName);
                }
                //再将文件读取为字符串
                String text = ReadTxtFromSDCard(fileName);
                //显示出来
                Message message = Message.obtain(handler);
                message.obj = text;
                handler.sendMessage(message);
            }
        }).start();


    }
    /**************************************/
    /***********生成文件夹******************/

    private void initView(){
        textView1 = findViewById(R.id.textView1);
    }

    public static void makeDirectory(String filePath){
        File file = null;
        try{
            file = new File(filePath);
            if (!file.exists()){
                file.mkdir();
            }
        }catch (Exception e){
            Log.i("error:",e+"");
        }

    }




    /***********生成文件****************/
    public boolean createNewFile(String filePath,String fileName){
        File file = null;
        try{
            file = new File(filePath + fileName);
            if (file.exists()){
                file.createNewFile();
                return true;
            }else {
                return false;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }


    // 将字符串写入到文本文件中
    public void writeTxtToFile(String stringContent,
                               String filePath, String fileName) {

        String strFilePath = filePath+fileName;
        // 每次写入时，都换行写
        String strContent = stringContent + "\r\n";
        File file = new File(strFilePath);

        try {
            RandomAccessFile raf = new RandomAccessFile(file, "rwd");
            raf.seek(file.length());
            raf.write(strContent.getBytes());
            raf.close();
        } catch (Exception e) {
            Log.e("TestFile", "Error on write File:" + e);
        }
    }

    private String Stream2String(InputStream is) {
        //强制缓存大小为16KB，一般Java类默认为8KB
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(is), 16*1024);
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {  //处理换行符
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }



    //这是这篇的重点，按ctrl+f关注input的操作
    private String ReadTxtFromSDCard(String filename){

        StringBuilder sb = new StringBuilder("");
        //判断是否有读取权限
        if(Environment.getExternalStorageState().
                equals(Environment.MEDIA_MOUNTED)){

            //打开文件输入流
            try {
                FileInputStream input = new FileInputStream(filePath + filename);
                byte[] temp = new byte[1024];

                int len = 0;
                //读取文件内容:
                while ((len = input.read(temp)) > 0) {
                    sb.append(new String(temp, 0, len));
                }
                //关闭输入流
                input.close();
            } catch (java.io.IOException e) {
                Log.e("ReadTxtFromSDCard","ReadTxtFromSDCard");
                e.printStackTrace();
            }

        }
        return sb.toString();
    }


    /*****************************/

    private void initChart(){

        new Thread(new Runnable() {
            @Override
            public void run() {
                int x=1;
//                while (true) {
                for (int i=1;i<=20;i++) {
                    try {
//                        Thread.sleep((long) 0.1);
                        TimeUnit.MICROSECONDS.sleep(10);//100微秒=0.1毫秒
                       // Log.d(TAG, "run: "+i);

                        //经验证 十次sleep的时间是一秒钟
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");// HH:mm:ss
                        Date date = new Date(System.currentTimeMillis());
                        //Log.d(TAG, "time"+date);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            initChartData();
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
        initView();
        bindView();
        initData();
        initEvent();

        for (int i = 1;i <= 20;i++){
            xData.add(String.valueOf(i));
        }


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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.test_sun:
                xData.clear();
                yData.clear();
                count=1;
                initChart();
                break;
            case R.id.save_chart:
                Long tsLong = System.currentTimeMillis()/1000;
                String ts = tsLong.toString();
                 int permission = ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (permission != PackageManager.PERMISSION_GRANTED) {//无权限情况
                    // 开始请求权限
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);

                }
                if (permission == PackageManager.PERMISSION_GRANTED){
                    Log.i("Main_Click", "拥有权限");
                    if (mCombinedChart.saveToGallery(ts,50)){//保存成功返回true
                        Toast.makeText(MainActivity.this, "图表保存成功", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(MainActivity.this, "图表保存失败", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }


    }

    }

