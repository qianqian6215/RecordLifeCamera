package cc.xiaoyuanzi.recordlifecamera;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.DataSetObserver;
import android.hardware.Camera;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Switch;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;


public class MainActivity extends Activity {

    private Button mStartRecordBtn;
    private Button mStopRecordBtn;
    private Switch mUseFontSwitch;
    private int mSelectionValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        mStartRecordBtn = (Button)findViewById(R.id.start_record);
        mStartRecordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });
        mStopRecordBtn = (Button)findViewById(R.id.stop_record);
        mStopRecordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlarmManager am=(AlarmManager)getSystemService(ALARM_SERVICE);
                am.cancel(getPendingIntent());
                Toast.makeText(MainActivity.this, String.format("取消成功！",
                        Utils.getFrequency(MainActivity.this))
                        ,Toast.LENGTH_SHORT).show();
            }
        });
        mUseFontSwitch = (Switch)findViewById(R.id.use_font);
        mUseFontSwitch.setChecked(Utils.getUseFontCamera(this));
        mUseFontSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Utils.setUseFontCamera(MainActivity.this
                        ,b);
            }
        });
    }

    private void setAlarm(int value) {
        AlarmManager am=(AlarmManager)getSystemService(ALARM_SERVICE);

        am.cancel(getPendingIntent());
        PendingIntent sender = getPendingIntent();

        long repeatTime = DateUtils.SECOND_IN_MILLIS * value;
       // long startTime = System.currentTimeMillis()+ fiveSecond;

        am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, repeatTime, repeatTime, sender);
    }

    private PendingIntent getPendingIntent() {
        Intent intent =new Intent(TackPictureReceiver.ACTION);
        return PendingIntent.getBroadcast(this, 0, intent, 0);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showDialog() {
//
//        View view = LayoutInflater.from(this).inflate(R.layout.select_frenquency_layout, null);
//        Spinner spinner = (Spinner)view.findViewById(R.id.frequence_value);
//        final String[] frequencies = getResources().getStringArray(R.array.frequency_selections);
//        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
//                android.R.layout.simple_spinner_item,
//                frequencies);
//        spinner.setAdapter(arrayAdapter);

        final String[] frequencies = getResources().getStringArray(R.array.frequency_selections);
        final List<String> frequencyList = Arrays.asList(frequencies);
        int checked = frequencyList.indexOf(String.valueOf(Utils.getFrequency(this)));

        new AlertDialog.Builder(this)
                .setTitle(R.string.frequency)
                .setSingleChoiceItems(R.array.frequency_selections, checked,new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mSelectionValue = i;
                    }
                }).setPositiveButton(R.string.start_record, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Integer value = Integer.valueOf(frequencies[mSelectionValue]);
                Utils.setFrequency(MainActivity.this, value);
                setAlarm(value);
                Toast.makeText(MainActivity.this, String.format
                        ("启动成功，%d分钟后开始记录", value), Toast.LENGTH_SHORT);


            }
        }).show();

    }
}
