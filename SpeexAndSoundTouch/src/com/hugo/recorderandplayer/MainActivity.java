package com.hugo.recorderandplayer;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.outfit7.soundtouch.SoundTouch;
import com.ryong21.encode.Speex;

public class MainActivity extends Activity implements OnClickListener {

	RecordAudio recordTask;
	PlayAudio playTask;
	Button startRecordingButton, stopRecordingButton, startPlaybackButton,stopPlaybackButton;
	TextView statusText;
	File recordingFile;
	
	private Speex speex = new Speex();
	private byte[] processedData = new byte[1024];

	boolean isRecording = false;
	boolean isPlaying = false;

	int frequency = 8000;
	int channelConfiguration = AudioFormat.CHANNEL_CONFIGURATION_MONO;
	int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
	
	int recordBufferSize = 160;
	int playBufferSize = 160;
	int yuanshishuju = 38;
	short[] s = new short[playBufferSize*10];
	
	SoundTouch st;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		statusText = (TextView) this.findViewById(R.id.StatusTextView);
		startRecordingButton = (Button) this.findViewById(R.id.StartRecordingButton);
		stopRecordingButton = (Button) this.findViewById(R.id.StopRecordingButton);
		startPlaybackButton = (Button) this.findViewById(R.id.StartPlaybackButton);
		stopPlaybackButton = (Button) this.findViewById(R.id.StopPlaybakButton);
		startRecordingButton.setOnClickListener(this);
		stopRecordingButton.setOnClickListener(this);
		startPlaybackButton.setOnClickListener(this);
		stopPlaybackButton.setOnClickListener(this);
		startRecordingButton.setEnabled(true);
		stopRecordingButton.setEnabled(false);
		startPlaybackButton.setEnabled(false);
		stopPlaybackButton.setEnabled(false);

		File path = new File(this.getExternalCacheDir() + "/");
		path.mkdirs();
		try {
			recordingFile = File.createTempFile("recording", ".pcm", path);
			System.out.println(recordingFile.getAbsolutePath());
		} catch (IOException e) {
			throw new RuntimeException("Couldn't create file on SD card", e);
		}

		//1.ro.ui.pttdroid.codecs.Speex.open(8);
		speex.init();
		System.out.println("FrameSize:"+speex.getFrameSize());
		
		st = new SoundTouch();
		st.setSampleRate(8000);
		st.setChannel();
		st.setPitch();
	}
	
	

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		speex.close();
	}



	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == startRecordingButton) {
			record();
		} else if (v == stopRecordingButton) {
			stopRecording();
		} else if (v == startPlaybackButton) {
			play();
		} else if (v == stopPlaybackButton) {
			stopPlaying();
		}
	}

	public void play() {
		stopPlaybackButton.setEnabled(true);
		startPlaybackButton.setEnabled(false);
		startRecordingButton.setEnabled(false);
		stopRecordingButton.setEnabled(false);
		playTask = new PlayAudio();
		playTask.execute();
	}

	public void stopPlaying() {
		isPlaying = false;
		stopPlaybackButton.setEnabled(false);
	}

	public void record() {
		startRecordingButton.setEnabled(false);
		stopRecordingButton.setEnabled(true);
		startPlaybackButton.setEnabled(false);
		stopPlaybackButton.setEnabled(false);
		recordTask = new RecordAudio();
		recordTask.execute();
	}

	public void stopRecording() {
		isRecording = false;
		stopRecordingButton.setEnabled(false);
	}

	private class PlayAudio extends AsyncTask<Void, Integer, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			isPlaying = true;
			int bufferSize = AudioTrack.getMinBufferSize(frequency,channelConfiguration, audioEncoding);
			System.out.println("Play_bufferSize:"+bufferSize);
			short[] audiodata = new short[playBufferSize];
			byte[] yuanshi = new byte[yuanshishuju];
			try {
				BufferedInputStream dis = new BufferedInputStream(new FileInputStream(recordingFile));
				AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, frequency,channelConfiguration, audioEncoding, bufferSize,AudioTrack.MODE_STREAM);
				audioTrack.play();
				
//				int i = 0;
//				int getSize = 0;
//				while ((i=dis.read(yuanshi))!=-1) {
//					//2.getSize = ro.ui.pttdroid.codecs.Speex.decode(yuanshi, i,audiodata);
//					getSize = speex.decode(yuanshi, audiodata, i);
//					System.out.println(i+","+getSize);
//					audioTrack.write(audiodata, 0, getSize);	
//				}
				
				int i = 0;
				int getSize = 0;
				
				int z = 0;
				int j = 0;
				while ((i=dis.read(yuanshi))!=-1) {
					//2.getSize = ro.ui.pttdroid.codecs.Speex.decode(yuanshi, i,audiodata);
					getSize = speex.decode(yuanshi, audiodata, i);
					
					System.arraycopy(audiodata, 0, s, j, getSize);
					z++;
					j += getSize;
					if(z==10){
						//audioTrack.write(s, 0, j);	
						st.putSamples(s, j);
						int sp = 0;
						System.out.println("-------------");
						System.out.println("put sample:" + j);
						if ((sp = st.receiveSample(s,s.length)) != 0) {
							System.out.println("receive sample:" + sp);
							audioTrack.write(s, 0, sp);
						}
						System.out.println("-------------");
						j = 0;
						z = 0;
					}
		
				}
				
				if(z>0&&z<10){
					st.putSamples(s, j);
					int sp = 0;
					System.out.println("------||||||||||||||||||||||||-------");
					System.out.println("put sample:" + j);
					st.flush();
					while ((sp = st.receiveSample(s,s.length)) != 0) {
						System.out.println("receive sample:" + sp);
						audioTrack.write(s, 0, sp);
						st.flush();
					}
					System.out.println("------||||||||||||||||||||||||-------");
					
				}
				audioTrack.stop();
				audioTrack.release();
				dis.close();
			} catch (Throwable t) {
				Log.e("AudioTrack", "Playback Failed");
			}
			return null;
		}
		

		protected void onPostExecute(Void result) {
			startPlaybackButton.setEnabled(true);
			startRecordingButton.setEnabled(true);
			stopPlaybackButton.setEnabled(false);
			stopRecordingButton.setEnabled(false);
		}
	}

	private class RecordAudio extends AsyncTask<Void, Integer, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			isRecording = true;
			try {
				DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(recordingFile)));
				int bufferSize = AudioRecord.getMinBufferSize(frequency,channelConfiguration, audioEncoding);
				System.out.println("Record_bufferSize:"+bufferSize);
				AudioRecord audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, frequency,channelConfiguration, audioEncoding, bufferSize);
				audioRecord.startRecording();

				short[] buffer = new short[recordBufferSize];
				int r = 0;
				int getSize = 0;
				
				while (isRecording) {
					int bufferReadResult = audioRecord.read(buffer, 0,buffer.length);

					if(bufferReadResult<0){
						audioRecord.stop();
						dos.close();
						return null;
					}
					
					//3.getSize = ro.ui.pttdroid.codecs.Speex.encode(buffer, processedData);
					getSize = speex.encode(buffer, 0, processedData, bufferReadResult);
					for (int i = 0; i < getSize; i++) {
						dos.write(processedData[i]);                                      
					}
					
					r += bufferReadResult;
					publishProgress(r);
				}
				audioRecord.stop();
				audioRecord.release();
				dos.close();
			} catch (Throwable t) {
				Log.e("AudioRecord", "Recording Failed");
			}
			return null;
		}

		protected void onProgressUpdate(Integer... progress) {
			statusText.setText(progress[0].toString()+"short");
		}

		protected void onPostExecute(Void result) {
			startPlaybackButton.setEnabled(true);
			startRecordingButton.setEnabled(true);
			stopPlaybackButton.setEnabled(false);
			stopRecordingButton.setEnabled(false);
		}
	}
}
