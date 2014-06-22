package co.harlequinmettle.silishield;

import android.graphics.Bitmap;
import android.util.DisplayMetrics;

public class TheMoon extends GameObject {

	public TheMoon(Bitmap[] a, float[] phys, long time, DisplayMetrics device) {
		super(a, phys, time, device);
		// TODO Auto-generated constructor stub
	}
public void updateState(long time){
	 if(onTimeOut){
		 if(time-timeoutTime>5000){
			 onTimeOut = false;
			 px = screenWidth ;
			 timeStamp = time;
		 }
		 return;
	 }
		if( px<-getBitmap().getWidth()*scaleX-5)
		{
			onTimeOut = true;
			timeoutTime = time;
		}
		super.doNextState(time);
}










}
