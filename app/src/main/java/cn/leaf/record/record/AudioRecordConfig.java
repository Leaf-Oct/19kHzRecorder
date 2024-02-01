package cn.leaf.record.record;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

public class AudioRecordConfig {
    public final static int RECORD_SOURCE= MediaRecorder.AudioSource.MIC;
    public final static int RECORD_SAMPLE_RATE=44100;
    public final static int CHANNEL_CONFIG= AudioFormat.CHANNEL_IN_MONO;
    public final static int RECORD_FORMAT=AudioFormat.ENCODING_PCM_16BIT;
    private static int min_buffer_size= AudioRecord.getMinBufferSize(RECORD_SAMPLE_RATE, CHANNEL_CONFIG, RECORD_FORMAT);
    public static int getMinBUfferSize(){
        return min_buffer_size;
    }
}
