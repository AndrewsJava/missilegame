package co.harlequinmettle.silishield;

import android.graphics.Bitmap;
import android.util.DisplayMetrics;

public class TheEarth extends GameObject {

	public TheEarth(Bitmap[] a, float[] phys, long time, DisplayMetrics device) {
		super(a, phys, time, device);
		// TODO Auto-generated constructor stub
	}

	public void doNextState(long time) { 

		obMod.reset();
		long dt = time - timeStamp;

		timeCount += dt;

		if (anim.length > 1 && timeCount > animTime) {
			timeCount -= animTime;
			animIndex %= (anim.length - 1);
			animIndex++;
		}

	super.updateMatrix();
		timeStamp = time;
	}

}
