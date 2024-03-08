package cn.leaf.record.record;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioRecord;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.leaf.record.AppContextUtil;

public class AudioRecordManager {
    //    系统的录音机
    private AudioRecord record;
    //    标记是否在录音
    private boolean is_recording = false;
    //    管写入和保存录音数据的
//    private DataManager manager = new DataManager();
    //    录音文件
    private File pcm_file, wav_file;
    private BufferedOutputStream file_output_stream;

    public String file_name, tag;

    //    开始录音
    public boolean startRecord(Context context) {
//        先创建录音器对象, 同时再检查一下权限
        if (record == null) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
            record = new AudioRecord(AudioRecordConfig.RECORD_SOURCE, AudioRecordConfig.RECORD_SAMPLE_RATE, AudioRecordConfig.CHANNEL_CONFIG, AudioRecordConfig.RECORD_FORMAT, 4 * AudioRecordConfig.getMinBUfferSize());
        }
        if (is_recording) {
            return false;
        }
        is_recording = true;
//        先创建一下录音文件
        prepareSaveRecord(context);
//        开始录音
        record.startRecording();
//        开个子线程采集录音信息
        new Thread(() -> {
            var data = new byte[AudioRecordConfig.getMinBUfferSize()];
            int result;
//            只要还在录
            while (is_recording) {
//                只要还能录到东西
                result = record.read(data, 0, AudioRecordConfig.getMinBUfferSize());
//                    接收到不正常的玩意儿
                if (result < 0) {
                    break;
                }
                try {
                    file_output_stream.write(data);
                    file_output_stream.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Log.d("record", "Ending~~");
        }).start();
        return true;
    }

    //    停止录音
    public void stopRecord() {
//        更改标记位, 释放系统录音器资源
        is_recording = false;
        record.stop();
        record.release();
        record = null;
//        数据管理器停止写入
//        manager.stopRecord();
        try {
            file_output_stream.flush();
            file_output_stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    private void prepareSaveRecord(Context context) {
//        pcm_file=new File(AppContextUtil.getContext().getExternalFilesDir(null).getAbsolutePath()+File.separator+file_name+".pcm");
//        try {
//            if(pcm_file.exists()){
//                pcm_file.delete();
//            }
//            file_output_stream=new BufferedOutputStream(new FileOutputStream(pcm_file));
//        } catch (FileNotFoundException e) {
//            throw new RuntimeException(e);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//
//    }

//    For Android 10
    private void prepareSaveRecord(Context context){
        var root_dir=Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"AcouDigits";
        var root_file=new File(root_dir);
        if(!root_file.exists()){
            var result=root_file.mkdir();
            Log.i("root_dir", Boolean.toString(result));
        }
        var des_dir=root_dir+File.separator+tag;
        var des_dir_file=new File(des_dir);
        if(!des_dir_file.exists()){
            var result=des_dir_file.mkdir();
            Log.i("des_dir", Boolean.toString(result));
        }
        pcm_file=new File(des_dir_file, file_name+".pcm");
        if(!pcm_file.exists()){
            try {
                var result=pcm_file.createNewFile();
                Log.i("pcmfile", Boolean.toString(result));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            file_output_stream=new BufferedOutputStream(new FileOutputStream(pcm_file));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

    }

//    private void prepareSaveRecord(Context context) {
//        var d = new Date();
//        var date_format = new SimpleDateFormat("yyyy-MM-dd_hh:mm:ss");
//        var file_name = date_format.format(d);
////        安卓9及以下的, 走传统的文件存储方式.
////        安卓10及以上的, 走MediaStore保存文件
//        if (Build.VERSION.SDK_INT <= 29) {
////            默认已授权读写了.不管了
//            pcm_file=new File(Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+Environment.DIRECTORY_DOWNLOADS+File.separator+file_name+".pcm");
//            Log.i("path", pcm_file.getAbsolutePath());
////            wav_file=new File(Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+Environment.DIRECTORY_DOWNLOADS+File.separator+file_name+".wav");
//            try {
//                pcm_file.createNewFile();
////                wav_file.createNewFile();
//                file_output_stream=new BufferedOutputStream(new FileOutputStream(pcm_file));
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        } else {
//            var value=new ContentValues();
//            value.put(MediaStore.Files.FileColumns.DISPLAY_NAME, file_name+".pcm");
//            value.put(MediaStore.Files.FileColumns.TITLE, file_name+".pcm");
//            value.put(MediaStore.Files.FileColumns.MIME_TYPE, "*/*");
//            var relative_path=Environment.DIRECTORY_DOWNLOADS+File.separator;
//            value.put(MediaStore.Files.FileColumns.RELATIVE_PATH, relative_path);
//            var download_uri=MediaStore.Downloads.EXTERNAL_CONTENT_URI;
//            var resolver=context.getContentResolver();
//            var insert_uri=resolver.insert(download_uri, value);
//            if(insert_uri!=null){
//                Log.i("Media Store", "ok");
//                try {
//                    file_output_stream=new BufferedOutputStream(resolver.openOutputStream(insert_uri));
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                }
//            }else {
//                Log.e("Media Store", "fail");
//            }
//        }
//    }
}
