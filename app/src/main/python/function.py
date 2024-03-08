import io

import skimage
from PIL import Image

def read_wav(file_path):
    from pydub import AudioSegment
    import numpy as np
    voice_data = AudioSegment.from_file(file=file_path, sample_width=2, frame_rate=44100, channels=1)

    # print(type(voice_data))

    pcm_data = np.array(voice_data.get_array_of_samples())
    return pcm_data / 32767, 44100


import numpy as np
import scipy.signal as sig


def preprocessing(wav_path):
    wav_data, sample_rate = read_wav(wav_path)

    # print(type(wav_data))
    # print(type(sample_rate))

    nfft = 8192
    overlap = 7168
    step = nfft - overlap
    wav_len = len(wav_data)

    # print(wav_len)

    if wav_len < step * 12 + nfft:
        wav_data = np.pad(wav_data, (0, step * 12 + nfft - wav_len))

    # bandpass filter
    [b, a] = sig.butter(6, [18700 / 44100 * 2, 19300 / 44100 * 2], 'bandpass')
    BPsignal = sig.filtfilt(b, a, wav_data)

    # bandstop filter
    [d, c] = sig.butter(3, [18985 / 44100 * 2, 19015 / 44100 * 2], 'bandstop')
    BPsignal = sig.filtfilt(d, c, BPsignal)

    # remove hardware noise
    head_noisenum = step * 12 + 1
    BPsignal = BPsignal[head_noisenum:]

    [f, t, Zxx] = sig.spectrogram(BPsignal, 44100, window="hamm", nperseg=nfft, noverlap=overlap, detrend=False)

    BPsignal, wav_data = None, None

    Zxx_magnitude = np.abs(Zxx)
    PP = 10 * np.log10(Zxx_magnitude + 2.2204e-16)
    return f, t, PP



def audio_to_picture2(wav_path, name, tag):
    # --------------
    # import matplotlib
    import matplotlib.pyplot as plt
    import matplotlib.style as mplstyle
    # ---------------
    # matplotlib.use('TkAgg')
    mplstyle.use('fast')
    f, t, PP = preprocessing(wav_path)
    fig = plt.figure()
    fig.dpi = 96
    plt.set_cmap('parula')
    plt.pcolormesh(t, f, PP)
    plt.ylim([18700, 19300])
    plt.axis("off")
    plt.gca().xaxis.set_major_locator(plt.NullLocator())
    plt.gca().yaxis.set_major_locator(plt.NullLocator())
    plt.subplots_adjust(top=1, bottom=0, left=0, right=1, hspace=0, wspace=0)
    plt.margins(0, 0)
    plt.gcf().set_size_inches(224 / 96, 224 / 96)
    buffer = io.BytesIO()  # 获取输入输出流对象
    canvas = fig.canvas
    canvas.print_png(buffer)  # 将画布上的内容打印到输入输出流对象
    data = buffer.getvalue()  # 获取流的值
    buffer.write(data)  # 将数据写入buffer
    img = Image.open(buffer)  # 使用Image打开图片数据
    img = skimage.img_as_ubyte(img)
    img = np.asarray(img)
    img = np.delete(img, -1, axis=2)
    plt.savefig("/storage/emulated/0/AcouDigits/"+str(tag)+"/"+str(name), dpi=96)
    plt.clf()
    plt.close()
