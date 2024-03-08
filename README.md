# k19kHz Recording

供物联网实验室前辈们使用的高频声波采集软件。

该项目同时也提供了不少具有参考性的操作，例如调用ChaquoPy，播放音频，录制音频，权限申请，文件读写。

整个项目结构非常简单，只有七个类文件，下面来一一介绍

### player.WaveProducer

产生19kHz声波的。这里的声波是正弦波。

咱们都知道Hz是一秒内振动的次数，也就是一秒内完成多少个周期。

根据某个我也忘了什么名字的定理，当采样率是频率的至少两倍时，才能保证信息无误 (大概是这个意思)

用44100Hz的采样率记录19kHz的正弦波信息是完全没有问题的。大多数移动设备的扬声器都能够播放这个频率的声波

如果不确定是否发声了的话，可以将频率改为440，听听看有没有声音。440Hz在音乐上是标准的中央C。

### player.AudioTrackManager

调用系统的AudioTrack，获取上一个类产生的正弦波，写入扬声器直接播放。

调用startPlaying()后，会持续播放音频，直到调用release()为止。

### record.AudioRecordConfig

录制下来的音频的信息。

在其中指定了录制设备，采样率，位深度，单声道

### record.AudioRecordManager

管理录音和保存录音文件的。

调用了系统的AudioRecord，通过子线程获取录音，写入文件。

该类包含了向主目录写入文件的操作，~~其实也不是什么高端操作~~

当你有时候获得了权限，却创建不了文件的时候，有可能是对应的父文件夹没创建。安卓是这样的，需要先创建其所在的文件夹，才能创文件。

同理，删除文件也是，需要先将文件夹内的文件删完，才能删文件夹。

### AppContextUtil

一个工具类，让应用能在任何地方调出context

同时还包含了Python的初始化。

这个类在Manifest.xml中的<application>标签中被指定，应用启动时创建并执行onCreate方法

### InstructionActivity

一个说明页，没什么内容。内容都在布局文件里呢

### MainActivity

内容最丰富的一集，负责了Python调用，权限申请，进行播放音频和录音。

放音和录音在前面都写好了，主活动相当于是黏合剂，展示了怎么用这两个模块。先放音再录音，结束后先停止录音，再停止放音。~~是不是有点像FTP协议~~

代码最底下requestPermission()是进行权限检查的。SDK29是安卓10，在这个版本以下还是可以通过写文件权限，来写文件的，安卓11以上就需要管理所有文件权限。

申请管理所有文件权限的代码疑似丢失(在我宿舍电脑里?)，尚未合并进来