package com.example.tnboysmap;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.adam.gpsstatus.GpsStatusProxy;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GpsActivity extends Activity {
    int num=1;
    private EditText editText;
    private EditText et;
    private LocationManager im;
    private static final String TAG = "GpsActivity";

    private LocationManager locationManager = null;
    private GpsStatusProxy proxy;
    private final int CODE_LOCATION_PERMISSION = 1;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        im.removeUpdates(locationListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps);

        proxy = GpsStatusProxy.getInstance(getApplicationContext());

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            List<String> permissionsNeeded = new ArrayList<String>();
            permissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
            ActivityCompat.requestPermissions(this, permissionsNeeded.toArray(new String[permissionsNeeded.size()]), CODE_LOCATION_PERMISSION);
            return;
        }
        proxy.register();


        editText = (EditText) findViewById(R.id.editText);
        et = (EditText) findViewById(R.id.et);
        im = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //判断GPS是否正常启动
        if(!im.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            Toast.makeText(this,"请开启GPS导航",Toast.LENGTH_SHORT).show();
            //返回开启GPS导航设置界面
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(intent,0);
            return;
        }




    //为获取地理位置信息时设置查询条件
        String bestProvider = im.getBestProvider(getCriteria(), true);
        //获取位置信息
        //如果不设置查询要求，getLastKnownLocation方法传人的参数为LocationManager.GPS_PROVIDER
        Location location = im.getLastKnownLocation(bestProvider);
        updateView(location);
        //监听状态
        im.addGpsStatusListener(listener);
        //绑定监听，有4个参数
        //参数1，设备：有GPS_PROVIDER和NETWORK_PROVIDER两种
        //参数2，位置信息更新周期，单位毫秒
        //参数3，位置变化最小距离：当位置距离变化超过此值时，将更新位置信息
        //参数4，监听
        //备注：参数2和3，如果参数3不为0，则以参数3为准；参数3为0，则通过时间来定时更新；两者为0，则随时刷新
        // 1秒更新一次，或最小位移变化超过1米更新一次；
        //注意：此处更新准确度非常低，推荐在service里面启动一个Thread，在run中sleep(10000);然后执行handler.sendMessage(),更新位置
        im.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000,1,locationListener);
    }

    private LocationListener locationListener = new LocationListener() {
        /**
         * GPS状态变化时触发
         */
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch (status) {
                //GPS状态为可见时
                case LocationProvider.AVAILABLE:
                    Log.i(TAG,"当前GPS状态为可见状态");
                    break;
                //GPS状态为服务区外时
                case LocationProvider.OUT_OF_SERVICE:
                    Log.i(TAG, "当前GPS状态为服务外状态");
                    break;
                //GPS状态为暂停服务时
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    Log.i(TAG,"当前GPS状态为暂停服务状态");
                    break;
            }
        }
        /**
         * GPS开启时触发
         */
        @Override
        public void onProviderEnabled(String provider) {
            Location location = im.getLastKnownLocation(provider);
            updateView(location);
        }
        /**
         * GPS禁用时触发
         */
        @Override
        public void onProviderDisabled(String provider) {
            updateView(null);
        }

        /**
         * 位置信息变化触发
         * @return
         */
        public void onLocationChanged(Location location){
            updateView(location);
            Log.i(TAG, "时间："+location.getTime());
            Log.i(TAG, "经度："+location.getLongitude());
            Log.i(TAG, "纬度："+location.getLatitude());
            Log.i(TAG, "海拔："+location.getAltitude());
        }
    };

    /**
     * 状态监听
     */
    GpsStatus.Listener listener = new GpsStatus.Listener() {
        public void onGpsStatusChanged(int event) {
            switch(event){
                //第一次定位
                case GpsStatus.GPS_EVENT_FIRST_FIX:
                    Log.i(TAG,"第一次定位");
                    Toast.makeText(GpsActivity.this,"第一次定位",Toast.LENGTH_SHORT).show();
                    break;
                //卫星状态改变
                case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                    Log.i(TAG,"卫星状态改变");
                    //获取当前状态
                    GpsStatus gpsStatus = im.getGpsStatus(null);
                    //获取卫星颗数的默认最大值
                    int maxSatellites = gpsStatus.getMaxSatellites();
                    //创建一个迭代器保存所有卫星
                    Iterator<GpsSatellite> iters = gpsStatus.getSatellites().iterator();
                    int count = 0;
                    while (iters.hasNext() && count <= maxSatellites) {
                        GpsSatellite  s = iters.next();
                        count++;
                    }
                    System.out.println("搜索到："+count+"颗卫星");

                    et.setText("已搜索次数"+num+"\n搜索到："+count+"颗卫星");
                    num++;

                    Log.i(TAG,"搜索到："+count+"颗卫星");
                    break;
                //定位启动
                case GpsStatus.GPS_EVENT_STARTED:
                    Log.i(TAG,"定位启动");
                    Toast.makeText(GpsActivity.this,"定位启动",Toast.LENGTH_SHORT).show();
                    break;
                //定位结束
                case GpsStatus.GPS_EVENT_STOPPED:
                    Log.i(TAG,"定位结束");
                    Toast.makeText(GpsActivity.this,"定位结束",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };



    /**
     * 实时更新文本内容
     * @param location
     */
    private void updateView(Location location) {
        if(location!=null){
            editText.setText("设备位置信息\n\n经度：");
            editText.append(String.valueOf(location.getLongitude()));
            editText.append("\n纬度：");
            editText.append(String.valueOf(location.getLatitude()));
        } else {
            //清空EditText对象
            editText.getEditableText().clear();
        }
    }

    /**
     * 返回查询条件
     * @return
     */
    private Criteria getCriteria() {
        Criteria criteria = new Criteria();
        //设置定位精确度 Criteria.ACCURACY_COARSE比较粗略，Criteria.ACCURACY_FINE则比较精细
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        //设置是否要求速度
        criteria.setSpeedRequired(false);
        // 设置是否允许运营商收费
        criteria.setCostAllowed(true);
        //设置是否需要方位信息
        criteria.setAltitudeRequired(true);
        // 设置对电源的需求
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        return criteria;
    }

}