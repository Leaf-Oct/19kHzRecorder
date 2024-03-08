package cn.leaf.record;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.leaf.record.player.AudioTrackManager;
import cn.leaf.record.record.AudioRecordManager;

public class MainActivity extends AppCompatActivity {

    Button record, instruction, record_wav, record_only, exit, reset_button;

    ImageView pic;
    TextView number;
    EditText tag_edit;
    short count=0;
    AudioRecordManager recorder=new AudioRecordManager();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermission();
        Log.i("root", Environment.getExternalStorageDirectory().getAbsolutePath());
        Log.i("download", Environment.DIRECTORY_DOWNLOADS);
        record=findViewById(R.id.record);
        instruction=findViewById(R.id.instruction);
//        record_wav=findViewById(R.id.record_wav);
//        record_only=findViewById(R.id.record_only);
        pic=findViewById(R.id.pic);
        reset_button=findViewById(R.id.button_reset);
        number=findViewById(R.id.number);
        exit=findViewById(R.id.exit);
        tag_edit=findViewById(R.id.tag);
        record.setOnClickListener(v->{
            var tag=tag_edit.getText().toString();
            if(tag.isEmpty()){
                Toast.makeText(MainActivity.this, "需要有Tag", Toast.LENGTH_SHORT).show();
                return;
            }
            //AudioTrack能一直循环放音, 而不限于数组大小. 因此让其先开始放音后开始录音. 结束时先停止录音, 后停止放音
            var d = new Date();
            var date_format = new SimpleDateFormat("yyyy-MM-dd_hh:mm:ss");
            var file_name = date_format.format(d);
            recorder.file_name=file_name;
            recorder.tag=tag;
            AudioTrackManager.startPlaying();
            recorder.startRecord(MainActivity.this);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            recorder.stopRecord();
            AudioTrackManager.release();
            count++;
            onNumberChange();

            Python py= Python.getInstance();
            PyObject test=py.getModule("main");
            test.callAttr("pcm2img", "/storage/emulated/0/AcouDigits/"+tag+"/"+file_name+".pcm", file_name, tag);
            Bitmap bm= BitmapFactory.decodeFile("/storage/emulated/0/AcouDigits/"+tag+"/"+file_name+".png");
            pic.setImageBitmap(bm);
        });
        instruction.setOnClickListener(v->{
            startActivity(new Intent(MainActivity.this, InstructionActivity.class));
        });
        reset_button.setOnClickListener(v->{
            count=0;
            onNumberChange();
        });
        exit.setOnClickListener(v->{
            finish();
        });
    }

    private void onNumberChange(){
        number.setText(count+"");
    }

    private void requestPermission(){
        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO)!=PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, 1);
        }
        if(Build.VERSION.SDK_INT<=29){
            var result= ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if(result!= PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }
    }
}