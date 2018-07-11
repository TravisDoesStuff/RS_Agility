package tnburt.rsagility;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    SensorManager sensorManager;

    TextView textLevelN;
    TextView textLevelD;
    TextView textCurrent;
    TextView textNext;
    TextView textRemaining;
    TextView textPercent;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        textLevelN     = findViewById(R.id.num_level_denominator);
        textLevelD     = findViewById(R.id.num_level_numerator);
        textCurrent     = findViewById(R.id.num_current);
        textNext        = findViewById(R.id.num_next);
        textRemaining   = findViewById(R.id.num_remaining);
        textPercent     = findViewById(R.id.num_percent);
        progressBar     = findViewById(R.id.progressBar);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Sensor countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        sensorManager.registerListener(this, countSensor, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        int experience = (int)event.values[0];
        int level = this.calculateLevel(experience);

        int currLevelExp = this.getExperiencePerLevel(level);
        int nextLevelExp = this.getNextLevelExp(level);
        int remainingExp = nextLevelExp - experience;

        int rawPercent = (int)(((double)experience-(double)currLevelExp) / ((double)nextLevelExp-(double)currLevelExp) * 100);
        if(rawPercent < 0) rawPercent = 0;
        String percent = String.valueOf(rawPercent)+"%";

        textLevelN.setText(String.valueOf(level));
        textLevelD.setText(String.valueOf(level));
        textCurrent.setText(String.valueOf(experience));
        textNext.setText(String.valueOf(nextLevelExp));
        textRemaining.setText(String.valueOf(remainingExp));
        textPercent.setText(percent);
        progressBar.setProgress(rawPercent);
        if(rawPercent<=50){
            textPercent.setTextColor(Color.parseColor("#FFFFFF"));
        }else{
            textPercent.setTextColor(Color.parseColor("#000000"));
        }
        textPercent.bringToFront();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public int calculateLevel(int exp) {
        int level;
        for(level=1; level<99; level++){
            if(getExperiencePerLevel(level+1) > exp){
                break;
            }
        }

        return level;
    }

    public int getExperiencePerLevel(int level) {
        double exp = 0;
        for(int i=0; i<level; i++){
            exp += Math.floor(i + 300 * Math.pow(2, i/7.0));
        }

        return (int) Math.floor(exp/4);
    }

    public int getNextLevelExp(int level) {
        return this.getExperiencePerLevel(level+1);
    }
}
