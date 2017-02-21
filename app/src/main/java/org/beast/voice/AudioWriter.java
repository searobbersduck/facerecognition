package org.beast.voice;

import android.media.AudioFormat;
import android.os.Environment;

import java.io.IOException;

/**
 * Created by weidong.zhang on 2016/4/27.
 */
public class AudioWriter implements AudioCapturer.OnAudioFrameCapturedListener{
    private static final String DEFAULT_TEST_FILE = Environment.getExternalStorageDirectory() + "/test.wav";

    private AudioCapturer mAudioCapturer;
    private WavFileWriter mWavFileWirter;

    public boolean startTesting() {
        mAudioCapturer = new AudioCapturer();
        mWavFileWirter = new WavFileWriter();
        try {
            mWavFileWirter.openFile(DEFAULT_TEST_FILE, 16000, 16, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        mAudioCapturer.setOnAudioFrameCapturedListener(this);
        mAudioCapturer.startCapture();
        return true;
    }

    public boolean stopTesting() {
        mAudioCapturer.stopCapture();
        try {
            mWavFileWirter.closeFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public void onAudioFrameCaptured(byte[] audioData) {
        mWavFileWirter.writeData(audioData,0,audioData.length);
    }

}
