package co.harlequinmettle.silishield;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.PointF;
import android.util.DisplayMetrics;

public class GameObject {
	float px, py, velX, velY, accelX, accelY, scaleX, scaleY, rot, dRot,
			accelRot, animTime;
	float condition;

	long timeStamp;
	protected final Bitmap[] anim;
	int animIndex;
	int screenWidth, screenHeight;
	Matrix obMod = new Matrix();
	float timeCount, distanceCount;
	static final float dScale = 0.0004f;
	boolean vert = false;
	boolean remove = false;
	boolean confined = false;
	boolean onTimeOut = false;
	long timeoutTime = 0;
	int elaps;
	int elapsTime = 0;
	public long timeLimit = 930;

	public GameObject(Bitmap[] a, float[] phys, long time, DisplayMetrics device) {
		// sequence of images loaded from res clipped into tiles
		anim = a;
		animIndex = 0;
		// position
		px = phys[0];
		py = phys[1];
		// velocity
		velX = phys[2];
		velY = phys[3];
		// typically gravity or nothing
		accelX = phys[4];//
		accelY = phys[5];
		// image modifiers
		scaleX = phys[6];
		scaleY = phys[7];

		rot = phys[8];
		dRot = phys[9];
		accelRot = phys[10];

		condition = phys[11];
		animTime = phys[12];
		screenWidth = device.widthPixels;
		screenHeight = device.heightPixels;
		// time stamp used to determine when rendering queue
		// should post to view

		// if(Math.random()<0.2)vert = true;

		timeStamp = time;

		doNextState(time);
	}

	public PointF getCenterOfObject() {
		return new PointF(  (px + scaleX * anim[0].getWidth() / 2),
				  (py + scaleY * anim[0].getHeight() / 2));
	}

	public void doNextState(long time) {

		long dt = time - timeStamp;
		elapsTime += dt;

		timeCount += dt;

		if (anim.length > 1 && timeCount > animTime) {
			timeCount -= animTime;
			animIndex %= (anim.length - 1);
			animIndex++;
		}

		if (vert) {
			scaleX += dt * dScale;
			scaleY += dt * dScale;

			if (scaleX > 1.5 || scaleX < 0.1) {
				remove = true;
			}
		}

		px += dt * velX;
		py += dt * velY;

		velX += dt * accelX;
		velY += dt * accelY; 
		rot += dt * dRot; 
		
		updateMatrix();
		timeStamp = time;

	}

	public void updateMatrix() {
		obMod.reset();

		obMod.postScale(scaleX, scaleY);
		obMod.postTranslate(-scaleX*anim[animIndex].getWidth() / 2,
				-scaleY*anim[animIndex].getHeight() / 2);

		obMod.postRotate(rot);
		obMod.postTranslate(scaleX*anim[animIndex].getWidth() / 2,
				scaleY*anim[animIndex].getHeight() / 2);
 
		obMod.postTranslate(px, py);

	}

	public Bitmap getBitmap() {
		// TODO Auto-generated method stub
		return anim[animIndex];
	}

	public void delayDestruction(int i) {
		// TODO Auto-generated method stub
		elaps = i;
	}

}
