package cn.leaf.record.player;

public class WaveProducer {
    //    采样率44100Hz
    public static final int SAMPLE_RATE = 44100;
    //    频率19kHz
    public static final int FREQUENCY = 19000;
    // 正弦波的振幅
    private static final short WAV_RANGE = Short.MAX_VALUE;
    private byte[] cosWavBuffer = new byte[4 * 44100];//ByteArray(4 * DEFAULT_SAMPLE_RATE_IN_HZ)
    // 是否初始化过
    private boolean isPrepare = false;

    // 生成正弦波
    public void prepare() {
        if (isPrepare) {
            return;
        }
//        for (i in 0 until 2 * DEFAULT_SAMPLE_RATE_IN_HZ) {
//            val cosWav = (WAV_RANGE * cos(2 * PI * START_FREQ * i / DEFAULT_SAMPLE_RATE_IN_HZ)).toInt()
//            cosWavBuffer[2 * i] = (cosWav and 0x00ff).toByte()
//            cosWavBuffer[2 * i + 1] = ((cosWav and 0xff00).ushr(8)).toByte()
//        }
        for (int i = 0; i < 2 * WaveProducer.SAMPLE_RATE; i++) {
            int cos_wave = (int) (WAV_RANGE * Math.cos(2 * Math.PI * FREQUENCY * i / SAMPLE_RATE));
            cosWavBuffer[2 * i] = (byte) (cos_wave & 0xff);
            cosWavBuffer[2 * i + 1] = (byte) ((cos_wave & 0xff00) >>> 8);
        }
        isPrepare = true;
    }

    public byte[] getTone() {
        return cosWavBuffer;
    }
}
