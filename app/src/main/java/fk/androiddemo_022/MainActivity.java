package fk.androiddemo_022;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

//1,获得SensorManager对象
//2,获得想要的Sensor对象
//3,绑定监听器
public class MainActivity extends Activity implements View.OnClickListener{
    Button findBut,accelerationBut,lightBut,orientationBut,proximityBut;
    SensorManager sensorManager;
    TextView text,accText,luxText;
    float gravity[]=new float[3];
    float linear_acceleration[]=new float[3];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findBut=(Button)findViewById(R.id.findBut);
        findBut.setOnClickListener(this);
        lightBut=(Button)findViewById(R.id.lightBut);
        lightBut.setOnClickListener(this);
        accelerationBut=(Button)findViewById(R.id.accelerationBut);
        accelerationBut.setOnClickListener(this);
        orientationBut=(Button)findViewById(R.id.orientationBut);
        orientationBut.setOnClickListener(this);
        proximityBut=(Button)findViewById(R.id.proximityBut);
        proximityBut.setOnClickListener(this);

        text=(TextView)findViewById(R.id.text);
        accText=(TextView)findViewById(R.id.accText);
        luxText=(TextView)findViewById(R.id.luxText);

        //获得传感器管理器对象
        sensorManager=(SensorManager)getSystemService(Context.SENSOR_SERVICE);
    }

    public class LightSensorListener implements SensorEventListener {
        @Override
        //传感器的数据被打包成event，主要的检测数据放在enent.values[]数组中
        public void onSensorChanged(SensorEvent event) {
            System.out.println(event.timestamp);//时间戳
            System.out.println(event.sensor.getResolution());//分辨率（能识别出最小数值）
            System.out.println(event.accuracy);//精度（等级）
            System.out.println(event.values[0]);//光线强度
        }
        @Override
        //传感器精度变化时调用这个函数
        public void onAccuracyChanged(Sensor sensor, int accuracy) {}
    }

    public class AccerationSensorListener implements SensorEventListener{
        @Override
        public void onSensorChanged(SensorEvent event) {
            final float alpha=0.8f;

            //event.values[0]X轴加速度，负方向为正
            //event.values[1]Y轴加速度，负方向为正
            //event.values[2]Z轴加速度，负方向为正
            gravity[0]=alpha*gravity[0]+(1-alpha)*event.values[0];
            gravity[1]=alpha*gravity[1]+(1-alpha)*event.values[1];
            gravity[2]=alpha*gravity[2]+(1-alpha)*event.values[2];

            linear_acceleration[0]=event.values[0]-gravity[0];
            linear_acceleration[1]=event.values[1]-gravity[1];
            linear_acceleration[2]=event.values[2]-gravity[2];

            //通过以上公式可以抛去三个方向上的重力加速度，只剩下纯加速度
            text.setText(linear_acceleration[0] + "");
            accText.setText(linear_acceleration[1] + "");
            luxText.setText(linear_acceleration[2] + "");
        }
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {}
    }

    public class OrientaationListener implements SensorEventListener{
        @Override
        public void onSensorChanged(SensorEvent event) {
            //（需要手机屏幕向上，向下的话南北会反掉）设备绕Z轴旋转，Y轴正方向与地磁北极方向的夹角，顺时针方向为正，范围【0，180】
            float azimuth=event.values[0];
            //设备绕X轴旋转的角度，当Z轴向Y轴正方向旋转时为正，反之为负，范围【-180,180】
            float pitch=event.values[1];
            //设备绕Y轴旋转的角度，当Z轴向X轴正方向旋转时为负，反之为正，范围【-90,90】
            float roll=event.values[2];

            text.setText(azimuth+"");
            accText.setText(pitch +"");
            luxText.setText(roll+"");
        }
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {}
    }

    public class ProximityListener implements SensorEventListener{
        @Override
        public void onSensorChanged(SensorEvent event) {
            //距离传感器测试手机屏幕距离别的物体的记录，只有两个值：0和5
            //距离很近时为0，否则为5
            System.out.println(event.values[0]+"");
        }
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {}
    }

    @Override
    public void onClick(View v) {
        if(v==findBut){
            //获取手机上所有传感器的列表
            List<Sensor> sensors=sensorManager.getSensorList(Sensor.TYPE_ALL);
            for(Sensor sensor:sensors){
                System.out.println(sensor.getName());
            }
        }else if(v==lightBut){
            //得到默认的加速度传感器
            Sensor lightSensor=sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
            //绑定监听器（上下文接口，要监听的传感器，传感器采样率<时间间隔>）,返回结果
            Boolean res=sensorManager.registerListener(new LightSensorListener(),lightSensor,SensorManager.SENSOR_DELAY_NORMAL);
            Toast.makeText(this,"绑定光线传感器："+res,Toast.LENGTH_LONG).show();
        }
        else if(v==accelerationBut){
            Sensor accelerometerSensor=sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            Boolean res=sensorManager.registerListener(new AccerationSensorListener(),accelerometerSensor,SensorManager.SENSOR_DELAY_NORMAL);
            Toast.makeText(this,"绑定加速度传感器："+res,Toast.LENGTH_LONG).show();
        }else if(v==orientationBut){
            Sensor orientationSensor=sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
            Boolean res=sensorManager.registerListener(new OrientaationListener(),orientationSensor,SensorManager.SENSOR_DELAY_NORMAL);
            Toast.makeText(this,"绑定方向传感器："+res,Toast.LENGTH_LONG).show();
        }
        else if(v==proximityBut){
            Sensor proximitySensor=sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
            Boolean res=sensorManager.registerListener(new ProximityListener(),proximitySensor,SensorManager.SENSOR_DELAY_NORMAL);
            Toast.makeText(this,"绑定距离传感器："+res,Toast.LENGTH_LONG).show();
        }
    }
}
