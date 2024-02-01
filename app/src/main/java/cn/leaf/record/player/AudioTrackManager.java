package cn.leaf.record.player;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

public class AudioTrackManager {
    //    系统的音频播放器
    private static AudioTrack audio;
    //    放音标记
    private static boolean is_playing;
    private static WaveProducer wave_producer=new WaveProducer();
    //    开始播放高频声波
    public static void startPlaying(){
//        创建播放器对象
        if(audio==null){
            audio=new AudioTrack(AudioManager.STREAM_MUSIC, WaveProducer.SAMPLE_RATE, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, 4*WaveProducer.SAMPLE_RATE, AudioTrack.MODE_STATIC);
            wave_producer.prepare();
        }
        if(is_playing){
            return;
        }
//        将WaveProducer的数据写入播放器, 播放高频声波
        audio.write(wave_producer.getTone(), 0, 4*WaveProducer.SAMPLE_RATE);
        audio.setLoopPoints(0, 2*WaveProducer.SAMPLE_RATE, -1);
        audio.play();
        is_playing=true;
    }

    //    结束, 停播
    public static void release(){
        if(is_playing){
            audio.pause();
            audio.stop();
            is_playing=false;
            audio.release();
        }
        audio=null;
    }
}
