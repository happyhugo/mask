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

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import ro.ui.pttdroid.codecs.Speex;
import ro.ui.pttdroid.settings.AudioSettings;
import ro.ui.pttdroid.settings.CommSettings;
import ro.ui.pttdroid.util.AudioParams;
import ro.ui.pttdroid.util.PhoneIPs;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder.AudioSource;
import android.util.Log;

public class Recorder extends Thread {
	
	private final int SO_TIMEOUT = 0;
	
	private AudioRecord recorder;
	/*
	 * True if thread is running, false otherwise.
	 * This boolean is used for internal synchronization.
	 */
	private boolean isRunning = false;	
	/*
	 * True if thread is safely stopped.
	 * This boolean must be false in order to be able to start the thread.
	 * After changing it to true the thread is finished, without the ability to start it again.
	 */
	private boolean isFinishing = false;
	
	private short[] pcmFrame = new short[160];
	private byte[] encodedFrame;
	private BufferedOutputStream os;
	private int k=1;
//	private short[] shortencodedFrame;
	
//	SoundTouch st;
//	
//	public void shortToBytes(short[] n,byte[] m) {
//        int index=0;
//		for(int i=0;i<n.length;i++){
//			m[index] = (byte) ((n[i] >> 8) & 0xff);
//			m[++index] =  (byte) (n[i] & 0xff);
//			index++;
//		}
//		System.out.println("send package length:"+(index));
//	}
//
//	public static byte[] read(InputStream inStream) throws Exception {
//		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
//
//		byte[] buffer = new byte[1024];
//		int len = 0;
//		while ((len = inStream.read(buffer)) != -1) {
//			outStream.write(buffer, 0, len);
//		}
//		inStream.close();
//		return outStream.toByteArray();
//	}
	
	public Recorder(OutputStream os){
		this.os = new BufferedOutputStream(os);
	}
			
	public void run() {
		// Set audio specific thread priority
		android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
		
		while(!isFinishing()) {		
			init();
				
			while(isRunning()) {
				
				try {		
					recorder.read(pcmFrame, 0, pcmFrame.length);	
					Speex.encode(pcmFrame, encodedFrame);	
					os.write(encodedFrame, 0, encodedFrame.length);
					System.out.println("send:"+(k++)+":"+encodedFrame.length);
				}
				catch(IOException e) {
					Log.d("Recorder", e.toString());
				}	
			}		

			try {
				os.flush();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			release();	
			/*
			 * While is not running block the thread.
			 * By doing it, CPU time is saved.
			 */
			synchronized(this) {
				try {	
					if(!isFinishing())
						this.wait();
				}
				catch(InterruptedException e) {
					Log.d("Recorder", e.toString());
				}
			}					
		}							
	}
	
	private void init() {				    	
		encodedFrame = new byte[62];
		System.out.println("cache1:"+AudioParams.RECORD_BUFFER_SIZE+","+encodedFrame.length);
		recorder = new AudioRecord(AudioSource.MIC, AudioParams.SAMPLE_RATE, AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioParams.ENCODING_PCM_NUM_BITS, 8000);
		recorder.startRecording();	
	}
	
	private void release() {			
		if(recorder!=null) {
			recorder.stop();
			recorder.release();
		}
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
