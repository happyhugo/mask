package com.outfit7.soundtouch;

public class SoundTouch {

	private long a = initSoundTouchNative();

	static {
		System.loadLibrary("soundtouch");
	}


	public final void setPitch() {
		setPitchSemiTonesNative(this.a, 7);
	}

	public final void setSampleRate(int paramInt) {
		setSampleRateNative(this.a, paramInt);
	}

	public final void putSamples(short[] paramArrayOfShort, int paramInt) {
		putSamplesNative(this.a, paramArrayOfShort, paramInt);
	}

	public final boolean setSetting(int paramInt1, int paramInt2) {
		return setSettingNative(this.a, paramInt1, paramInt2);
	}

	public final int receiveSample(short[] paramArrayOfShort, int paramInt) {
		return receiveSamplesNative(this.a, paramArrayOfShort, paramInt);
	}

	public final void setChannel() {
		setNChannelsNative(this.a, 1);
	}

	public final void setRate() {
		setRateChangeNative(this.a, 15.0F);
	}

	public final void flush() {
		flushNative(this.a);
	}

	private native boolean setSettingNative(long paramLong, int paramInt1,int paramInt2);

	public native void flushNative(long paramLong);

	public native long initSoundTouchNative();

	public native void putSamplesNative(long paramLong,short[] paramArrayOfShort, int paramInt);

	public native int receiveSamplesNative(long paramLong,short[] paramArrayOfShort, int paramInt);

	public native void setNChannelsNative(long paramLong, int paramInt);

	public native void setPitchSemiTonesNative(long paramLong, int paramInt);

	public native void setRateChangeNative(long paramLong, float paramFloat);

	public native void setSampleRateNative(long paramLong, int paramInt);
}
