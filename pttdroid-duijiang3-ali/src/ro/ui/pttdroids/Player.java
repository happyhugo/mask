/* Copyright 2011 Ionut Ursuleanu
 
This file is part of pttdroid.
 
pttdroid is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.
 
pttdroid is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.
 
You should have received a copy of the GNU General Public License
along with pttdroid.  If not, see <http://www.gnu.org/licenses/>. */

package ro.ui.pttdroids;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.outfit7.soundtouch.SoundTouch;

import ro.ui.pttdroid.codecs.Speex;
import ro.ui.pttdroid.util.AudioParams;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

public class Player extends Thread {

	private AudioTrack player;
	private boolean isRunning = true;
	private boolean isFinishing = false;

	private byte[] encodedFrame;
	private short[] shortencodedFrame;
	private int k = 1;
	SoundTouch st;
	private InputStream is;
	private int progress = 0;
	private ByteArrayOutputStream out = new ByteArrayOutputStream();
	private short[] pcmFrame = new short[160];
	int temp = 0;
	public ExecutorService es;
	public Player(InputStream is) {
		this.is = is;
		if(es == null){
			es = Executors.newSingleThreadExecutor();			
		}
	}

	public void run() {
		android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);

		while (!isFinishing()) {
			init();
			int temp = 0;
			int i=0;
			int j=0;
			int jishu = 0;
			short[] s = new short[1600*5];
			while (isRunning()) {

				try {
					i = is.read(encodedFrame, temp, encodedFrame.length-temp);
					temp = temp +i;
					if(temp==62){
						jishu++;
						System.out.println("receive "+jishu+":"+temp);
						Speex.decode(encodedFrame, encodedFrame.length, pcmFrame);
//						player.write(pcmFrame, 0, 160);
						
						if(j<50){
							System.arraycopy(pcmFrame, 0, s, j*160, 160);
							j++;
							
						}else{
							es.execute(new PlayR(s));
							s = new short[1600*5];
							j=0;
						}
						temp = 0;
					}
					

					
					
//					es.execute(new PlayR(pcmFrame));
//					i = is.read(encodedFrame, 0, encodedFrame.length);
//					System.out.println("receive:" + (k++) + ":" + i);
//					
//					if(temp<14400){
//						out.write(encodedFrame, 0, i);
//						temp = temp + i;
//						if(temp>=14400){
//							System.out.println("write now");
////							player.write(out.toByteArray(), 0, temp);
//							
//							byteArray2ShortArray(out.toByteArray(), shortencodedFrame);
//							System.out.println("put sample:" + out.toByteArray().length);
//							st.putSamples(shortencodedFrame, shortencodedFrame.length);
//							int s = 0;
//							while ((s = st.receiveSample(shortencodedFrame,shortencodedFrame.length)) != 0) {
//								System.out.println("receive sample:" + s);
//								player.write(shortencodedFrame, 0, s);
//							}
//							
//							out.reset();
//							temp = 0;
//						}
//					}
//					else{						
//						player.write(encodedFrame, 0, i);
//					}
					
					
//					if((i = is.read(encodedFrame, temp, encodedFrame.length-temp))==-1){
//						this.finish();
//						break;
//					}
//					System.out.println("receive:" + (k++) + ":" + i);
//					temp = temp +i;
//					if(temp >=8000*2){
//						System.out.println("temp:"+temp);
////						player.write(encodedFrame, 0, temp);		
//	
////						byteArray2ShortArray(encodedFrame, shortencodedFrame);
////						System.out.println("put sample:" + shortencodedFrame.length);
////						st.putSamples(shortencodedFrame, shortencodedFrame.length);
////						int s = 0;
////						while ((s = st.receiveSample(shortencodedFrame,shortencodedFrame.length)) != 0) {
////							System.out.println("receive sample:" + s);
////							player.write(shortencodedFrame, 0, s);
////						}
////						st.flush();
////						if ((s = st.receiveSample(shortencodedFrame,shortencodedFrame.length)) != 0) {
////							System.out.println("flush receive sample:" + s);
////							player.write(shortencodedFrame, 0, s);
////						}
//						byteArray2ShortArray(encodedFrame, shortencodedFrame);
//						short[] s = new short[AudioParams.ENCODEFRAME / 2];
//						System.arraycopy(shortencodedFrame, 0, s, 0, shortencodedFrame.length);
//						es.execute(new PlayR(s));
//						temp = 0;
//					}
//					
////					byteArray2ShortArray(encodedFrame, shortencodedFrame);
////					System.out.println("put sample:" + shortencodedFrame.length);
////					st.putSamples(shortencodedFrame, shortencodedFrame.length);
////					int s = 0;
////					while ((s = st.receiveSample(shortencodedFrame,shortencodedFrame.length)) != 0) {
////						System.out.println("receive sample:" + s);
////						player.write(shortencodedFrame, 0, s);
////					}
////					st.flush();
////					if ((s = st.receiveSample(shortencodedFrame,shortencodedFrame.length)) != 0) {
////						System.out.println("flush receive sample:" + s);
////						player.write(shortencodedFrame, 0, s);
////					}
					makeProgress();
				} catch (IOException e) {
					Log.d("Player", e.toString());
				}
			}

			release();
			synchronized (this) {
				try {
					if (!isFinishing())
						this.wait();
				} catch (InterruptedException e) {
					Log.d("Player", e.toString());
				}
			}
		}
	}

	public void byteArray2ShortArray(byte[] data, short[] retVal) {
		for (int i = 0; i < retVal.length; i++)
			retVal[i] = (short) ((data[i * 2] & 0xff) | (data[i * 2 + 1] & 0xff) << 8);
	}
	
	class PlayR implements Runnable{
        short[] s ;
		public PlayR(short[] s){
        	this.s = s;
        }
		public void run() {
			// TODO Auto-generated method stub
			st.putSamples(s, s.length);
			int sp = 0;
			while ((sp = st.receiveSample(s,s.length)) != 0) {
				System.out.println("receive sample:" + sp);
				player.write(s, 0, sp);
			}
//			st.flush();
//			if ((sp = st.receiveSample(s,s.length)) != 0) {
//				System.out.println("flush receive sample:" + sp);
//				player.write(s, 0, sp);
//			}
			s = null;
		}
		
	}

	private void init() {

			player = new AudioTrack(AudioManager.STREAM_MUSIC,
					AudioParams.SAMPLE_RATE,
					AudioFormat.CHANNEL_CONFIGURATION_MONO,
					AudioParams.ENCODING_PCM_NUM_BITS,
					8000*2, AudioTrack.MODE_STREAM);

			encodedFrame = new byte[62];
			shortencodedFrame = new short[AudioParams.ENCODEFRAME / 2];

			System.out.println("cache2:" + AudioParams.TRACK_BUFFER_SIZE + ","
					+ encodedFrame.length);

			player.play();

			st = new SoundTouch();
			st.setSampleRate(8000);
			st.setChannel();
			st.setPitch();

	}

	private void release() {
		if (player != null) {
			player.stop();
			player.release();

		}
	}

	private synchronized void makeProgress() {
		progress++;
	}

	public synchronized int getProgress() {
		return progress;
	}

	public synchronized boolean isRunning() {
		return isRunning;
	}

	public synchronized void resumeAudio() {
		isRunning = true;
		this.notify();
	}

	public synchronized void pauseAudio() {
		isRunning = false;
	}

	public synchronized boolean isFinishing() {
		return isFinishing;
	}

	public synchronized void finish() {
		pauseAudio();
		isFinishing = true;
		this.notify();
	}


}
