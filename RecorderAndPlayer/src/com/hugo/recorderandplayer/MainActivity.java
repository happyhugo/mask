package com.hugo.recorderandplayer;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
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
import android.os.Environment;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener {

	RecordAudio recordTask;
	PlayAudio playTask;
	Button startRecordingButton, stopRecordingButton, startPlaybackButton,stopPlaybackButton;
	TextView statusText;
	File recordingFile;

	boolean isRecording = false;
	boolean isPlaying = false;

	int frequency = 8000;
	int channelConfiguration = AudioFormat.CHANNEL_CONFIGURATION_MONO;
	int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;

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
		} catch (IOException e) {
			throw new RuntimeException("Couldn't create file on SD card", e);
		}

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
			bufferSize = 1000;
			short[] audiodata = new short[bufferSize / 4];
			try {
				DataInputStream dis = new DataInputStream(
						new BufferedInputStream(new FileInputStream(
								recordingFile)));
				AudioTrack audioTrack = new AudioTrack(
						AudioManager.STREAM_MUSIC, frequency,
						channelConfiguration, audioEncoding, bufferSize,
						AudioTrack.MODE_STREAM);
				audioTrack.play();
				while (isPlaying && dis.available() > 0) {
					int i = 0;
					while (dis.available() > 0 && i < audiodata.length) {
						audiodata[i] = dis.readShort();
						i++;
					}
					audioTrack.write(audiodata, 0, audiodata.length);
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
				DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(
								recordingFile)));
				//int bufferSize = AudioRecord.getMinBufferSize(frequency,channelConfiguration, audioEncoding);
				int bufferSize = 1000;
				System.out.println("Record_bufferSize:"+bufferSize);
				AudioRecord audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, frequency,
						channelConfiguration, audioEncoding, bufferSize);
				short[] buffer = new short[bufferSize/4];
				audioRecord.startRecording();
				int r = 0;
				while (isRecording) {
					int bufferReadResult = audioRecord.read(buffer, 0,buffer.length);
					for (int i = 0; i < bufferReadResult; i++) {
						dos.writeShort(buffer[i]);
					}
					System.out.println("read:"+bufferReadResult);
					r += bufferReadResult;
					publishProgress(r);
					if(r<0){
						audioRecord.stop();
						dos.close();
						return null;
					}
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
			statusText.setText(progress[0].toString());
		}

		protected void onPostExecute(Void result) {
			startPlaybackButton.setEnabled(true);
			startRecordingButton.setEnabled(true);
			stopPlaybackButton.setEnabled(false);
			stopRecordingButton.setEnabled(false);
		}
	}
}
