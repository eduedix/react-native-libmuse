package v.LibMuse;

import android.app.Activity;

import com.choosemuse.libmuse.Accelerometer;
import com.choosemuse.libmuse.Eeg;
import com.choosemuse.libmuse.Muse;
import com.choosemuse.libmuse.MuseDataPacket;
import com.choosemuse.libmuse.MuseDataPacketType;
import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.JavaScriptModule;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.ViewManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VMuseDataPacket {
	public static abstract class Listener {
		public abstract boolean OnReceivePacket(VMuseDataPacket var1);
	}

	public VMuseDataPacket(MuseDataPacket basePacket) {
		this.basePacket = basePacket;
		this.type = basePacket.packetType();
	}
	MuseDataPacket basePacket;

	public MuseDataPacketType type;
	public String Type() {
		if (type == MuseDataPacketType.EEG) return "eeg";
		else if (type == MuseDataPacketType.ACCELEROMETER) return "accel";
		return null;
	}

	public double[] eegValues;
	public double[] accelValues;

	public void LoadEEGValues() {
		eegValues = new double[4];
		eegValues[0] = basePacket.getEegChannelValue(Eeg.EEG1);
		eegValues[1] = basePacket.getEegChannelValue(Eeg.EEG2);
		eegValues[2] = basePacket.getEegChannelValue(Eeg.EEG3);
		eegValues[3] = basePacket.getEegChannelValue(Eeg.EEG4);
		/*eegValues[4] = basePacket.getEegChannelValue(Eeg.AUX_LEFT);
		eegValues[5] = basePacket.getEegChannelValue(Eeg.AUX_RIGHT);*/
	}
	public void LoadAccelValues() {
		accelValues = new double[3];
		accelValues[0] = basePacket.getAccelerometerValue(Accelerometer.X);
		accelValues[1] = basePacket.getAccelerometerValue(Accelerometer.Y);
		accelValues[2] = basePacket.getAccelerometerValue(Accelerometer.Z);
	}

	public static double EnsureNormalDouble(double value, double fallback) {
		if (Double.isNaN(value) || Double.isInfinite(value))
			return fallback;
		return value;
	}
	public WritableMap ToMap() {
		WritableMap map = Arguments.createMap();
		// maybe temp; don't send type
		//map.putString("type", Type());
		if (eegValues != null) {
			//map.putArray("eegValues", ToWritableArray(eegValues));
			// maybe temp; only send channels 1 and 2
			WritableArray eegValuesArray = Arguments.createArray();
			eegValuesArray.pushDouble(EnsureNormalDouble(eegValues[1], fakeNaN));
			eegValuesArray.pushDouble(EnsureNormalDouble(eegValues[2], fakeNaN));
			map.putArray("eegValues", eegValuesArray);
		}
		// maybe temp; don't send accel-values
		/*if (accelValues != null)
			map.putArray("accelValues", ToWritableArray(accelValues));*/
		return map;
	}

	static final double fakeNaN = -1000000000;
	static WritableArray ToWritableArray(double[] array) {
		WritableArray result = Arguments.createArray();
		for (double val : array) {
			if (Double.isNaN(val))
				result.pushDouble(fakeNaN);
			else
				result.pushDouble(val);
		}
		return result;
	}
}