package co.harlequinmettle.silishield;

import android.graphics.Bitmap;
import android.util.DisplayMetrics;

public class Rocket extends GameObject {

	public Rocket(Bitmap[] a, float[] phys, long time, DisplayMetrics device) {
		super(a, phys, time, device);
		// TODO Auto-generated constructor stub
	}
	public void doNextState(long time){
		//update position based on velocity and time elapsed
		//update velocity based on dvel
		//update rot (scale?)
		//update animIndex ie Bitmap
	  
		long dt = time - timeStamp;
		elapsTime +=dt;
		
	timeCount+=dt;
		 
		
		
		if(scaleX<1){
		scaleX+=dt*dScale*5;
		scaleY+=dt*dScale*5; 
		}
		
		px += dt*velX;
		py += dt*velY;
		
		velX+= dt*accelX;
		velY+= dt*accelY;
	 
	 updateMatrix();
		timeStamp = time;
		 
	}

	public void updateMatrix(){	
		obMod.reset();
		obMod.postTranslate(- anim[animIndex].getWidth()/2,-anim[animIndex].getHeight()/2);
	obMod.postRotate(rot); 
	obMod.postTranslate( anim[animIndex].getWidth()/2, anim[animIndex].getHeight()/2);

	obMod.postScale(scaleX,scaleY);
		obMod.postTranslate(px ,py );
		//if(scaleX<1.1)
		obMod.postTranslate((1-scaleX)*anim[0].getWidth(), (1-scaleX)*anim[0].getHeight()); 
	}
	

}
