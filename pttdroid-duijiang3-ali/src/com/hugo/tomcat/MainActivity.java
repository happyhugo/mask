//package com.hugo.tomcat;
//
//import java.io.BufferedInputStream;
//import java.io.BufferedOutputStream;
//import java.io.DataInputStream;
//import java.io.DataOutputStream;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.io.IOException;
//
//import com.outfit7.soundtouch.SoundTouch;
//
//import android.app.Activity;
//import android.media.AudioFormat;
//import android.media.AudioManager;
//import android.media.AudioRecord;
//import android.media.AudioTrack;
//import android.media.MediaPlayer;
//import android.media.MediaPlayer.OnCompletionListener;
//import android.media.MediaRecorder;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.os.Environment;
//import android.util.Log;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.widget.Button;
//import android.widget.TextView;
//
//public class MainActivity extends Activity implements OnClickListener,OnCompletionListener{
//	RecordAudio recordTask;
//	PlayAudio playTask;
//	Button startRecordingButton, stopRecordingButton, startPlaybackButton,stopPlaybackButton;
//	TextView statusText;
//	File recordingFile;
//	File recordingFile2;
//	SoundTouch st;
//
//	boolean isRecording = false;
//	boolean isPlaying = false;
//
//	int frequency = 8000; //11025
//
//	int channelConfiguration = AudioFormat.CHANNEL_CONFIGURATION_MONO;
//	int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
//
//	/** Called when the activity is first created. */
//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.main);
//		statusText = (TextView) this.findViewById(R.id.StatusTextView);
//		startRecordingButton = (Button) this.findViewById(R.id.StartRecordingButton);
//		stopRecordingButton = (Button) this.findViewById(R.id.StopRecordingButton);
//		startPlaybackButton = (Button) this.findViewById(R.id.StartPlaybackButton);
//		stopPlaybackButton = (Button) this.findViewById(R.id.StopPlaybackButton);
//		startRecordingButton.setOnClickListener(this);
//		stopRecordingButton.setOnClickListener(this);
//		startPlaybackButton.setOnClickListener(this);
//		stopPlaybackButton.setOnClickListener(this);
//		stopRecordingButton.setEnabled(false);
//		startPlaybackButton.setEnabled(false);
//		stopPlaybackButton.setEnabled(false);
//		st = new SoundTouch();
//		st.setSampleRate(frequency);
//		st.setChannel();
//		st.setPitch();
//
//		File path = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
//		path.mkdirs();
//		try {
//			recordingFile = File.createTempFile("r11ecording", ".pcm", path);
//			recordingFile2 = File.createTempFile("r11ecording", ".pcm", path);
//			//recordingFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"r11ecording41759.pcm");
//		} catch (IOException e) {
//			throw new RuntimeException("Couldn't create file on SD card", e);
//		}
//	}
//
//	public void onClick(View v) {
//		if (v == startRecordingButton) {
//			record();
//		} else if (v == stopRecordingButton) {
//			stopRecording();
//		} else if (v == startPlaybackButton) {
//			play();
//		} else if (v == stopPlaybackButton) {
//			stopPlaying();
//		}
//	}
//
//	public void play() {
//		startPlaybackButton.setEnabled(true);
//		playTask = new PlayAudio();
//		playTask.execute();
//		stopPlaybackButton.setEnabled(true);
//	}
//
//	public void stopPlaying() {
//		isPlaying = false;
//		stopPlaybackButton.setEnabled(false);
//		startPlaybackButton.setEnabled(true);
//	}
//
//	public void record() {
//		startRecordingButton.setEnabled(false);
//		stopRecordingButton.setEnabled(true);
//		// For Fun
//		startPlaybackButton.setEnabled(true);
//		recordTask = new RecordAudio();
//		recordTask.execute();
//
//
//	}
//	
//
//	public void stopRecording() {
//		isRecording = false;
//	}
//	
//	public void process(View view){
//		new Thread(new Runnable() {
//			
//			public void run() {
//				Log.i("soundtouch", "process begin");
//				short[] buffer = new short[2048];
//				try{
//					DataInputStream din = new DataInputStream(new BufferedInputStream(new FileInputStream(recordingFile)));
//					DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(recordingFile2)));
//					// TODO Auto-generated method stub
//					while (din.available() > 0) {
//						int i = 0;
//						while (din.available() > 0 && i < buffer.length) {
//							buffer[i] = din.readShort();
//							i++;
//						}
//						System.out.println("put sample:"+i);
//						st.putSamples(buffer, i);
//						int s=0;
//						while((s=st.receiveSample(buffer, buffer.length))!=0){
//							for (int ii = 0; ii < s; ii++) {
//								dos.writeShort(buffer[ii]);
//							}
//							System.out.println("receive sample:"+s);
//						}
//					}
//					st.flush();
//					int s=0;
//					while((s=st.receiveSample(buffer, buffer.length))!=0){
//						for (int ii = 0; ii < s; ii++) {
//							dos.writeShort(buffer[ii]);
//						}
//						System.out.println("flush sample:"+s);
//					}
//					Log.i("soundtouch", "process end");
//				}catch(Exception e){
//					e.printStackTrace();
//				}
//			}
//		}).start();
//		
//
//		
//	}
//
//	private class PlayAudio extends AsyncTask<Void, Integer, Void> {
//		@Override
//		protected Void doInBackground(Void... params) {
//			isPlaying = true;
//			int bufferSize = AudioTrack.getMinBufferSize(frequency,channelConfiguration, audioEncoding);
//			System.out.println("cache2:"+bufferSize);
//			short[] audiodata = new short[bufferSize*8];
//			try {
//				DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(
//								recordingFile2)));
//				AudioTrack audioTrack = new AudioTrack(
//						AudioManager.STREAM_MUSIC, frequency,
//						channelConfiguration, audioEncoding, bufferSize*4,
//						AudioTrack.MODE_STREAM);
//
//				audioTrack.play();
//				while (isPlaying && dis.available() > 0) {
//					int i = 0;
//					while (dis.available() > 0 && i < audiodata.length) {
//						audiodata[i] = dis.readShort();
//						i++;
//					}
//					System.out.println("e");
//					//Thread.sleep(1000);
//					audioTrack.write(audiodata, 0, audiodata.length);
//				}
//				dis.close();
//				audioTrack.release();
//				System.out.println("start recording ,stop recording,playback recording");
//				startPlaybackButton.setEnabled(false);
//				stopPlaybackButton.setEnabled(true);
//			} catch (Throwable t) {
//				Log.e("AudioTrack", "Playback Failed");
//			}
//			return null;
//		}
//	}
//
//	private class RecordAudio extends AsyncTask<Void, Integer, Void> {
//		@Override
//		protected Void doInBackground(Void... params) {
//			isRecording = true;
//			try {
//				DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(recordingFile2)));
//				int bufferSize = AudioRecord.getMinBufferSize(frequency,channelConfiguration, audioEncoding);
//				AudioRecord audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, frequency,channelConfiguration, audioEncoding, bufferSize*4);
//				short[] buffer = new short[bufferSize];
//				System.out.println("cache1:"+bufferSize);
//				audioRecord.startRecording();
//				int r = 0;
//				while (isRecording) {
//					int bufferReadResult = audioRecord.read(buffer, 0,buffer.length);
////					for(int i=0,j=0;j<bufferReadResult;i=i+2,j=j+4){
////						    buffer[i]= buffer[j];
////						    buffer[i+1]= buffer[j+1];
////				    }
////					for (int i = 0; i < bufferReadResult/2; i++) {
////						dos.writeShort(buffer[i]);
////					}
//					
//					
//					System.out.println("put sample:"+bufferReadResult);
//					st.putSamples(buffer, bufferReadResult);
//					int s=0;
//					while((s=st.receiveSample(buffer, buffer.length))!=0){
//						for (int ii = 0; ii < s; ii++) {
//							dos.writeShort(buffer[ii]);
//						}
//						System.out.println("receive sample:"+s);
//					}
//					
//					
//					
////					for (int i = 0; i < bufferReadResult; i++) {
////						dos.writeShort(buffer[i]);
////					}
//					publishProgress(new Integer(r));
//					r++;
//				}
//				audioRecord.stop();
//				audioRecord.release();
//				dos.close();
//			} catch (Throwable t) {
//				Log.e("AudioRecord", "Recording Failed");
//			}
//			return null;
//		}
//
//		protected void onProgressUpdate(Integer... progress) {
//			statusText.setText(progress[0].toString());
//		}
//
//		protected void onPostExecute(Void result) {
//			startRecordingButton.setEnabled(true);
//			stopRecordingButton.setEnabled(false);
//			startPlaybackButton.setEnabled(true);
//		}
//	}
//
//	public void onCompletion(MediaPlayer mp) {
//		// TODO Auto-generated method stub
//		
//	}
//
//}