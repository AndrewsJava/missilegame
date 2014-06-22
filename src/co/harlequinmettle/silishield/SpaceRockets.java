package co.harlequinmettle.silishield;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

public class SpaceRockets extends Activity implements
		SurfaceView.OnTouchListener {
    int testingVariable;
	InnerView mInnerView;
	private static DisplayMetrics metrics = new DisplayMetrics();
	static Bitmap starrysky;
	static GameObject moonGameObject;
	static GameObject earthGameObject;
	static float sw;
	static float sh;
	private static final int I_ALPHA = 80;
	static float fingerX;
	static float fingerY;

	static float targetAlpha = 0;
	static float aimAngle = 270;
	static int earthWidth;

	static float launchSiteX;
	static float launchSiteY;

	static float startX;
	static float startY;
	static float endX;
	static float endY;
	static float startX2;
	static float startY2;
	static float endX2;
	static float endY2;

	float lastX = 0;
	float lastY = 0;

	static int fadeDelay = 0;
	static SoundPool spool ;
	private static int soundIDlaunch, soundIDexplode, soundIDimpact;
	static boolean loaded = false;
	private static float volume;
	private static RectF arcAim;// = new Rect(0, 0, 0, 0); 
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		long timeTest = System.currentTimeMillis();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		sw = metrics.widthPixels;
		sh = metrics.heightPixels;
		launchSiteX = sw / 2;
		launchSiteY = 0.8f * sh;
		// tell system to use the layout defined in our XML file
		setContentView(R.layout.rocket_layout_sili);
		// get handles to the LunarView from XML, and its LunarThread
		mInnerView = (InnerView) findViewById(R.id.rocketviewloaderid);

		setVolumeControlStream(AudioManager.STREAM_MUSIC);

		String bgName = "bgspace" + (int) (1 + 5 * Math.random());

		starrysky = (getBitmapFromImageName(bgName));

		int randomX = (int) (Math.random() * (starrysky.getWidth() - (11 + metrics.widthPixels)));
		int randomY = (int) (Math.random() * (starrysky.getHeight() - (11 + metrics.heightPixels)));

		starrysky = Bitmap.createBitmap(starrysky, randomX, randomY,
				10 + metrics.widthPixels, 10 + metrics.heightPixels);
 
		System.out.println((System.currentTimeMillis() - timeTest) / 1000.0);
		// register canvas to receive events as defined in this
		mInnerView.setOnTouchListener(this);
		  
	    // Load the sound
	    spool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
	    spool.setOnLoadCompleteListener(new OnLoadCompleteListener() {
	      @Override
	      public void onLoadComplete(SoundPool soundPool, int sampleId,
	          int status) {
	        loaded = true;
	      }
	    });
	    soundIDlaunch = spool.load(this, R.raw.launch2, 1);
	    soundIDimpact = spool.load(this, R.raw.impact, 1);
	    soundIDexplode = spool.load(this, R.raw.detonate, 1);
	    AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
	      float actualVolume = (float) audioManager
	          .getStreamVolume(AudioManager.STREAM_MUSIC);
	      float maxVolume = (float) audioManager
	          .getStreamMaxVolume(AudioManager.STREAM_MUSIC);
	        volume = actualVolume / maxVolume;
	        arcAim = new RectF(launchSiteX - earthWidth, launchSiteY
	    			- earthWidth, launchSiteX + earthWidth, launchSiteY
	    			+ earthWidth); 
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		fingerX = event.getX(event.getActionIndex());
		fingerY = event.getY(event.getActionIndex());
		fadeDelay = 10;
		if (fingerY > sh - 100)
			targetAlpha = I_ALPHA;
		switch (event.getActionMasked()) {
		case MotionEvent.ACTION_UP:

			if (fingerY < sh - 100){
				mInnerView.launchMissile(fingerX, fingerY);
			
				
				  
				
			}
			break;
		case MotionEvent.ACTION_MOVE:
			if (fingerX < sw / 2 && fingerY > sh - 100) {
				if (Math.abs(fingerX - lastX) < 50)
					// if( fingerX <lastX)

					aimAngle += fingerX - lastX;
				// else aimAngle+= 2;
				startX = (float) (launchSiteX + 100 * Math.sin(Math.PI
						* aimAngle / 180));
				startY = (float) (launchSiteY - 100 * Math.cos(Math.PI
						* aimAngle / 180));
				endX = (float) (launchSiteX + 50 * Math.sin(Math.PI * aimAngle
						/ 180));
				endY = (float) (launchSiteY - 50 * Math.cos(Math.PI * aimAngle
						/ 180));
				startX2 = (float) (launchSiteX + 30 * Math.sin(Math.PI
						* aimAngle / 180));
				startY2 = (float) (launchSiteY - 30 * Math.cos(Math.PI
						* aimAngle / 180));
				endX2 = (float) (launchSiteX + 550 * Math.sin(Math.PI
						* aimAngle / 180));
				endY2 = (float) (launchSiteY - 550 * Math.cos(Math.PI
						* aimAngle / 180));
				lastX = fingerX;
				lastY = fingerY;
			}
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
		case MotionEvent.ACTION_DOWN:
			if (fingerX > sw - 100 && fingerY > sh - 100)
				mInnerView.launchMissile(
						(float) (launchSiteX + 100 * Math.sin(Math.PI
								* aimAngle / 180)),
						(float) (launchSiteY - 100 * Math.cos(Math.PI
								* aimAngle / 180)));
			break;
		}

		return true;
	}

	// ////////////////----------------------------
	public static class InnerView extends SurfaceView implements
			SurfaceHolder.Callback {

		InnerThread mInnerThread;
		/** Handle to the application context, used to e.g. fetch Drawables. */
		private Context mContext;
		// add rockets asteroids spaceships etc...
		ConcurrentLinkedQueue<GameObject> bodies;// needsRenderingQueue;
		ConcurrentLinkedQueue<GameObject> rockets;// needsRenderingQueue;
		ConcurrentLinkedQueue<GameObject> asteroids;// objDetectionQueue;
		ConcurrentHashMap<GameObject, Long> explosions;// timer;
		Bitmap rocket, deimos;
		static Bitmap[] explosion, impact;

		public InnerView(Context context, AttributeSet attrs) {
			super(context, attrs);

			bodies = new ConcurrentLinkedQueue<GameObject>();

			rockets = new ConcurrentLinkedQueue<GameObject>();
			asteroids = new ConcurrentLinkedQueue<GameObject>();
			explosions = new ConcurrentHashMap<GameObject, Long>();
			// register our interest in hearing about changes to our surface
			SurfaceHolder holder = getHolder();
			holder.addCallback(this);

			// create thread only; it's started in surfaceCreated()
			mInnerThread = new InnerThread(holder, context, new Handler());
			float[] gamePhysics = { sw - 110, 122, -0.005f, 0, 0, 0, 0.35f,
					0.35f, (float) (360 * Math.random()), 0f, 0f, 1000,
					100000000 };

			Bitmap moon = getBitmapFromImageName("moon", context.getResources());
			Bitmap[] moonob = { moon };
			moonGameObject = new TheMoon(moonob, gamePhysics,
					System.currentTimeMillis(), metrics);

			// defend at all cost!!!!!:)
			Bitmap earthFrames = getBitmapFromImageName("earth40small",
					context.getResources());
			Bitmap[] earthAnim = new Bitmap[earthFrames.getWidth()
					/ earthFrames.getHeight()];
			for (int i = 0; i < earthAnim.length; i++) {
				earthAnim[i] = Bitmap.createBitmap(earthFrames,
						i * earthFrames.getHeight(), 0,
						earthFrames.getHeight(), earthFrames.getHeight());
			}
			final float earthScale = 1.5f;
			float[] earthAnimationPhysics = {
					sw / 2 - earthScale * earthFrames.getHeight() / 2,
					metrics.heightPixels - earthScale * earthFrames.getHeight(),
					0, 0, 0, 0, earthScale, earthScale,
					(float) (360 * Math.random()), 0f, 0f, 100000000, 40 };
			earthGameObject = new TheEarth(earthAnim, earthAnimationPhysics,
					System.currentTimeMillis(), metrics);

			earthWidth = (int) (earthGameObject.getBitmap().getWidth() * earthGameObject.scaleX);

			bodies.add(earthGameObject);
			bodies.add(moonGameObject);
			// Resources res = context.getResources();

		}

		public void launchMissile(float fingerX, float fingerY) {
			// TODO Auto-generated method stub
			float factor = 0.001f;
			float dist = distance(launchSiteX, launchSiteY, fingerX, fingerY);
			if (dist <= 0)
				dist = 5;
			float angle = (float) (90 + 180
					* Math.atan2(fingerY - launchSiteY, fingerX - launchSiteX)
					/ Math.PI);

			float[] rocketTrajectory = { launchSiteX - rocket.getWidth() / 2,
					launchSiteY - rocket.getHeight() / 2, 0, 0,
					factor * (fingerX - launchSiteX) / dist,
					(fingerY - launchSiteY) * factor / dist, 0.1f, 0.1f, angle,
					0f, 0f, 99, 40 };
			Bitmap[] r = { rocket };
			rockets.add(new Rocket(r, rocketTrajectory, System
					.currentTimeMillis(), metrics));

		      // Is the sound loaded already?
		      if ( loaded) {
		    	   spool.play( soundIDlaunch, volume/2, volume/2, 1, 0, 1f);
		      //  Log.e("Test", "Played sound");
		      }
		}

		public void sightAsteroid() {
			// TODO Auto-generated method stub
			float x = 0, y = 0, veloX = 1, veloY = 1;
			float minVelo = 0.001f;
			float veloFactor = 0.05f;

			int side = (int) (Math.random() * 3);
			switch (side) {
			case 0:
				x = 0;
				y = (float) (Math.random() * sh / 2);
				veloY = (float) (minVelo + Math.random() * veloFactor);
				veloX = (float) (minVelo + Math.random() * veloFactor);
				break;
			case 1:
				x = sw;
				y = (float) (Math.random() * sh / 2);
				veloY = (float) (minVelo + Math.random() * veloFactor);
				veloX = (float) -(minVelo + Math.random() * veloFactor);

				break;
			case 2:
				y = 0;
				x = (float) (Math.random() * sw / 2);
				veloY = (float) (minVelo + Math.random() * veloFactor);
				veloX = (float) (minVelo + Math.random() * veloFactor);

				break;
			}

			float factor = 0.000001f;

			// xstart,ystart, initVelx,initVely,accelX,accelY, scaleX,scaleY,
			// initRot,rotVel,rotAccel,composition,animationTime
			float[] rocketTrajectory = { x, y, veloX, veloY, 0, 0,
					(float) (0.3f + (-0.15 + 0.3 * Math.random())),
					(float) (0.3f + (-0.15 + 0.3 * Math.random())), 0,
					(float) (-0.5 + Math.random() * 1), 0f, 99, 40000000 };
			Bitmap[] r = { deimos };
			asteroids.add(new GameObject(r, rocketTrajectory, System
					.currentTimeMillis(), metrics));

		}

		public void sightAsteroid(float x, float y) {
			// TODO Auto-generated method stub
			float veloX = 1, veloY = 1;
			float minVelo = 0.001f;
			float veloFactor = 0.05f;

			veloY = (float) (minVelo - veloFactor + 2 * Math.random()
					* veloFactor);
			veloX = (float) (minVelo - veloFactor + 2 * Math.random()
					* veloFactor);

			float factor = 0.000001f;

			// xstart,ystart, initVelx,initVely,accelX,accelY, scaleX,scaleY,
			// initRot,rotVel,rotAccel,composition,animationTime
			float[] rocketTrajectory = { x, y, veloX, veloY, 0, 0, 0.3f, 0.3f,
					0, (float) (-0.5 + Math.random() * 1), 0f, 99, 40000000 };
			Bitmap[] r = { deimos };
			asteroids.add(new GameObject(r, rocketTrajectory, System
					.currentTimeMillis(), metrics));

		}

		public void sightAsteroid(PointF xy) {
			// TODO Auto-generated method stub

			float veloX = 1, veloY = 1;
			float minVelo = 0.001f;
			float veloFactor = 0.05f;

			veloY = (float) (minVelo - veloFactor / 3 + 2 * Math.random()
					* veloFactor);
			veloX = (float) (minVelo - veloFactor + 1.2 * Math.random()
					* veloFactor);
			float angle = (float) (2 * Math.PI * Math.random());
			float xp = (float) (Math.sin(angle));
			float yp = (float) (Math.cos(angle));
			float factor = 0.000001f;

			// xstart,ystart, initVelx,initVely,accelX,accelY, scaleX,scaleY,
			// initRot,rotVel,rotAccel,composition,animationTime
			float[] rocketTrajectory = { xy.x += 30 * xp, xy.y += 30 * yp,
					0.07f * xp, 0.07f * yp, 0, 0, 0.3f, 0.3f, 0,
					(float) (-0.5 + Math.random() * 1), 0f, 99, 40000000 };
			Bitmap[] r = { deimos };
			GameObject astr = new GameObject(r, rocketTrajectory,
					System.currentTimeMillis(), metrics);
			astr.delayDestruction(1390);
			asteroids.add(astr);

		}

		public float distance(float a1, float a2, float b1, float b2) {
			float dist = (float) (Math.sqrt((a1 - b1) * (a1 - b1) + (a2 - b2)
					* (a2 - b2)));

			if (dist != dist)
				return 600;
			return dist;
		}

		public float distance(Point A, float b1, float b2) {
			float a1 = A.x;
			float a2 = A.y;
			float dist = (float) (Math.sqrt((a1 - b1) * (a1 - b1) + (a2 - b2)
					* (a2 - b2)));

			if (dist != dist)
				return 600;
			return dist;
		}

		public float distance(Point A, Point B) {
			float a1 = A.x;
			float a2 = A.y;
			float b1 = B.x;
			float b2 = B.y;
			float dist = (float) (Math.sqrt((a1 - b1) * (a1 - b1) + (a2 - b2)
					* (a2 - b2)));

			if (dist != dist)
				return 600;
			return dist;
		}

		public float distance(PointF A, PointF B) {
			float a1 = A.x;
			float a2 = A.y;
			float b1 = B.x;
			float b2 = B.y;
			float dist = (float) (Math.sqrt((a1 - b1) * (a1 - b1) + (a2 - b2)
					* (a2 - b2)));

			if (dist != dist)
				return 600;
			return dist;
		}

		public Point inBetween(Point a, Point b) {
			return new Point(a.x + (a.x - b.x) / 2, a.y + (a.y - b.y) / 1);

		}

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
			// TODO Auto-generated method stub

		}

		public void surfaceCreated(SurfaceHolder holder) {
			// start the thread here so that we don't busy-wait in run()
			// waiting for the surface to be created
			mInnerThread.setRunning(true);
			mInnerThread.start();
		}

		/*
		 * Callback invoked when the Surface has been destroyed and must no
		 * longer be touched. WARNING: after this method returns, the
		 * Surface/Canvas must never be touched again!
		 */
		public void surfaceDestroyed(SurfaceHolder holder) {
			// we have to tell thread to shut down & wait for it to finish, or
			// else
			// it might touch the Surface after we return and explode

			boolean retry = true;
			mInnerThread.setRunning(false);
			while (retry) {
				try {
					mInnerThread.join();

					retry = false;
				} catch (InterruptedException e) {
				}
			}
		}

		// ///////////////-----------------------------
		public class InnerThread extends Thread {
			/** Indicate whether the surface has been created & is ready to draw */
			private boolean mRun = false;

			Paint centerLine = new Paint();
			Paint outerLine = new Paint();
			/** Message handler used by thread to interact with TextView */
			private Handler mHandler;
			final Paint textPaint;
			/** Handle to the surface manager object we interact with */
			private SurfaceHolder mSurfaceHolder;
			private float textSize = 30;
			private Paint linePaint;
			private Paint cornerPaint;
			private final Rect left = new Rect(0, 0, 0, 0);
			private final Rect right = new Rect(0, 0, 0, 0);
			
			private long time;
			Bitmap[] moonFrag = new Bitmap[5];
			private String diagnos = new String();

			
			public InnerThread(SurfaceHolder holder, Context context,
					Handler handler) {
				setDaemon(true);
				time = System.currentTimeMillis();
				mSurfaceHolder = holder;
				mHandler = handler;
				mContext = context;
				linePaint = new Paint();
				linePaint.setARGB(200, 200, 90, 200);
				cornerPaint = new Paint();
				cornerPaint.setARGB(70, 200, 90, 200);
				Paint tPaint = new Paint();// try moving this out and final
				tPaint.setARGB(222, (int) (222), (int) (222), (int) (222));
				tPaint.setTextSize(textSize - 4);
				textPaint = new Paint(tPaint);// a final paint
				centerLine.setARGB(60, 50, 200, 130);
				// outerLine.setARGB(90,150,50,100);
				centerLine.setStrokeWidth(5);
				centerLine.setStrokeCap(Paint.Cap.ROUND);
				// outerLine.setARGB(100,150,50,100);

				centerLine.setStrokeWidth(10);
				centerLine.setStrokeCap(Paint.Cap.ROUND);
				centerLine.setStyle(Paint.Style.STROKE);
			 
				Resources resources = context.getResources();
				Bitmap expl = getBitmapFromImageName("smallfaintexplosion",
						resources);
				explosion = new Bitmap[expl.getWidth() / expl.getHeight()];
				for (int i = 0; i < explosion.length; i++) {
					explosion[i] = Bitmap.createBitmap(expl,
							i * expl.getHeight(), 0, expl.getHeight(),
							expl.getHeight());
				}
				Bitmap imp = getBitmapFromImageName("smallimpact", resources);
				impact = new Bitmap[imp.getWidth() / imp.getHeight()];
				for (int i = 0; i < impact.length; i++) {
					impact[i] = Bitmap.createBitmap(imp, i * imp.getHeight(),
							0, imp.getHeight(), imp.getHeight());
				}
				for (int i = 0; i < moonFrag.length; i++)
					moonFrag[i] = getBitmapFromImageName("moonfrag" + (1 + i),
							resources);
				rocket = getBitmapFromImageName("rocket", resources);
				deimos = getBitmapFromImageName("deimos", resources);
			
			}

			/**
			 * Used to signal the thread whether it should be running or not.
			 * Passing true allows the thread to run; passing false will shut it
			 * down if it's already running. Calling start() after this was most
			 * recently called with false will result in an immediate shutdown.
			 * 
			 * @param b
			 *            true to run, false to shut down
			 */
			public void setRunning(boolean b) {
				mRun = b;
			}

			@Override
			public void run() {
				while (mRun) {
					Canvas c = null;
					try {
						c = mSurfaceHolder.lockCanvas(null);

						synchronized (mSurfaceHolder) {
							doDraw(c);
						}
					} finally {
						// do this in a finally so that if an exception is
						// thrown
						// during the above, we don't leave the Surface in
						// an
						// inconsistent state
						if (c != null) {
							mSurfaceHolder.unlockCanvasAndPost(c);
						}
						try {
							Thread.sleep(16);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}

			long TIMESUM = 0;
			long LASTTIME = System.currentTimeMillis();

			private float dist = 100000000;

			private int escapedCount;
			private int missCount;
			private int hitCount;

			private int earthHitCount;

			public void doDraw(Canvas c) {
				time = System.currentTimeMillis();
				long dt = time - LASTTIME;
				TIMESUM += dt;
				if (TIMESUM > 500) {
					TIMESUM = 0;
					sightAsteroid();
				}
				LASTTIME = time;
				c.drawBitmap(starrysky, -5, -5, null);
				// RENDER MOON EARTH
				for (GameObject spheres : bodies) {
					spheres.doNextState(System.currentTimeMillis());
					c.drawBitmap(spheres.getBitmap(), spheres.obMod, null);
				}
				// RENDER ALL ASTROIDS - soon to be destroyed
				for (GameObject sprite : asteroids) {
					// remove if out of bounds
					if (sprite.px < -100
							|| sprite.px > metrics.widthPixels + 100
							|| sprite.py < -100
							|| sprite.py > metrics.heightPixels + 20) {
						asteroids.remove(sprite);
						escapedCount++;
					}
					sprite.doNextState(time);
					c.drawBitmap(sprite.getBitmap(), sprite.obMod, null);
				}

				for (GameObject missile : rockets) {
					// RENDER ALL ROCKETS
					missile.doNextState(time);
					c.drawBitmap(missile.getBitmap(), missile.obMod, null);

					if (missile.px < -500
							|| missile.px > metrics.widthPixels + 200
							|| missile.py < -310
							|| missile.py > metrics.heightPixels + 100)
						rockets.remove(missile);
					missCount++;
					// test for missiles proximity to celestial bodies: moon
					for (GameObject spheres : bodies) {
						if (spheres == moonGameObject) {
							dist = distance(missile.getCenterOfObject(),
									spheres.getCenterOfObject());
							if (dist < 50) {
								spheres.condition -= 100;
								if (spheres.condition <= 0) {
									explodeMoon(spheres);
								}
								hitCount++;
								rockets.remove(missile);
								addRocketExplosion(spheres);
							}
						}
						// test for rockets proximity to astroids
						for (GameObject rockInSpace : asteroids) {
							dist = distance(missile.getCenterOfObject(),
									rockInSpace.getCenterOfObject());
							if (dist < 30) {
								rockInSpace.condition -= 100;
								if (rockInSpace.condition <= 0) {
									// if (rockInSpace.elapsTime >
									// rockInSpace.elaps)
									asteroids.remove(rockInSpace);
								}
								rockets.remove(missile);
								hitCount++;
								addRocketExplosion(rockInSpace);
							}
						}

					}
				}

				// test for astroid proximity to celestial bodies
				for (GameObject spaceRocks : asteroids) {

					for (GameObject celestial : bodies) {
						dist = distance(celestial.getCenterOfObject(),
								spaceRocks.getCenterOfObject());
						int proximity = 60;
						if (celestial == earthGameObject)
							proximity = 120;
						if (dist < celestial.getBitmap().getWidth()
								* celestial.scaleX / 2) {
							spaceRocks.condition -= 100;
							celestial.condition -= 100;

							if (celestial.condition <= 0) {
								bodies.remove(celestial);
								explodeBody(celestial);
							}
							// hit moon or earth not blown up with missile
							if (celestial == earthGameObject)
								earthHitCount++;
							missCount++;
							asteroids.remove(spaceRocks);
							addImpactExplosion(spaceRocks, celestial);
						}
					}
				}
					//targetAlpha *= 0.5*dt/1000;
			 

			 	

				if (fadeDelay-- >0){
					 centerLine.setStrokeWidth(10);
				c.drawArc(arcAim, 150, 240, false, centerLine);
				// c.drawCircle(earth.getCenterOfObject().x,earth.getCenterOfObject().y,
				// earth.getBitmap().getWidth(), centerLine);

				c.drawLine(startX, startY, endX, endY, centerLine);
				centerLine.setStrokeWidth(2);
				// sw/2,7*sh/8

				c.drawArc(arcAim, 150, 240, false, centerLine);
				// c.drawCircle(earth.getCenterOfObject().x,earth.getCenterOfObject().y,
				// earth.getBitmap().getWidth(), centerLine);
				c.drawLine(startX, startY, endX, endY, centerLine);
				c.drawLine(startX2, startY2, endX2, endY2, centerLine);

				
				}
				for (Entry<GameObject, Long> temporal : explosions.entrySet()) {
					temporal.getKey().doNextState(System.currentTimeMillis());

					if (System.currentTimeMillis() - temporal.getValue() > temporal.getKey().timeLimit) {
						explosions.clear();
						// diagnos +=" *";
					} else {
						c.drawBitmap(temporal.getKey().getBitmap(),
								temporal.getKey().obMod, null);
					}

				}

				c.drawText("timer   map: " + explosions.size() + diagnos, 20,
						50, new Paint());
				c.drawText("needsRender: " + rockets.size(), 20, 70,
						new Paint());
				c.drawText("detection  : " + asteroids.size(), 20, 90,
						new Paint());
			}

			private void explodeBody(GameObject celestial) {
				// TODO Auto-generated method stub

			}

			private void addImpactExplosion(GameObject objHitting,
					GameObject objHit) {
				float factor = 0.001f;
				PointF centerA = objHitting.getCenterOfObject();
				PointF centerB = objHit.getCenterOfObject();
				float dist = distance(centerB.x, centerB.y, centerA.x,
						centerA.y);
				if (dist <= 0)
					dist = 5;
				float angle = (float) (180 * Math.atan2(centerA.y - centerB.y,
						centerA.x - centerB.x) / Math.PI);
 

				float[] expPhys = { centerA.x - impact[0].getWidth() / 2,

				centerA.y - impact[0].getHeight() / 2,

				-impact[0].getWidth() * GameObject.dScale / 2,

				-impact[0].getHeight() * GameObject.dScale / 2,

				0, 0, (float) (0.6f+ 0.8*Math.random()),(float) (0.6f+ 0.8*Math.random()), (float) (angle), 0f, 0f, 1, 70 };
				GameObject newExplosion = new GameObject(impact, expPhys,
						System.currentTimeMillis(), metrics);
				newExplosion.vert = true;
				newExplosion.timeLimit = 710;
				// needsRenderingQueue.add(newExplosion);
				explosions.put(newExplosion, System.currentTimeMillis());
				if ( loaded) {
			    	   spool.play( soundIDimpact, volume, volume, 1, 0, 1f);
			      //  Log.e("Test", "Played sound");
			      }
				
			}

			private void addRocketExplosion(GameObject itemExploding) {
				// TODO Auto-generated method stub

				float[] expPhys = {
						itemExploding.px + itemExploding.getBitmap().getWidth()
								* itemExploding.scaleX / 2
								- explosion[0].getWidth() / 2,

						itemExploding.py + itemExploding.getBitmap().getWidth()
								* itemExploding.scaleX / 2
								- explosion[0].getHeight() / 2,

						-explosion[0].getWidth() * GameObject.dScale / 2,

						-explosion[0].getHeight() * GameObject.dScale / 2,

						0, 0,  (float) (0.8f+ 0.4*Math.random()),(float) (0.8f+ 0.4*Math.random()), (float) (360 * Math.random()), (float) (-0.1+0.2f*Math.random()),
						0f, 1, 90 };
				GameObject newExplosion = new GameObject(explosion, expPhys,
						System.currentTimeMillis(), metrics);
				newExplosion.vert = true;
				explosions.put(newExplosion, System.currentTimeMillis());
				/*
				 * float[] expPhys = { sprite2.px +
				 * sprite2.getBitmap().getWidth() sprite2.scaleX / 2 -
				 * explosion[0].getWidth() / 2, sprite2.py +
				 * sprite2.getBitmap().getWidth() sprite2.scaleX / 2 -
				 * explosion[0].getHeight() / 2, -explosion[0].getWidth()
				 * GameObject.dScale / 2, -explosion[0].getHeight()
				 * GameObject.dScale / 2, 0, 0, 1.0f, 1.0f, (float) (360 *
				 * Math.random()), 0f, 0f, 1, 90 }; GameObject newExplosion =
				 * new GameObject(explosion, expPhys,
				 * System.currentTimeMillis(), metrics); newExplosion.vert =
				 * true; // needsRenderingQueue.add(newExplosion);
				 * timer.put(newExplosion, System.currentTimeMillis());
				 */    
				if ( loaded) {
			    	   spool.play( soundIDexplode, volume, volume, 1, 0, 1f);
			      //  Log.e("Test", "Played sound");
			      }
			}

			private void explodeMoon(GameObject spheres) {
				// TODO Auto-generated method stub
				for (int i = 0; i < 10; i++)
					sightAsteroid(spheres.getCenterOfObject());
				bodies.remove(moonGameObject);
				for (int i = 0; i < moonFrag.length; i++) {
					Bitmap[] overkill = { moonFrag[i] };
					float angle = (float) (2 * Math.PI * Math.random());
					float[] fragPhys = { moonGameObject.px,

					moonGameObject.py,

					(float) (0.0051 * Math.sin(angle)),

					(float) (0.0051 * Math.cos(angle)),

					0, 0, 1.0f, 1.0f, 0, (float) (Math.random()), 0f, 10000,
							9000000 };
					GameObject moonFragmentObj = new GameObject(overkill,
							fragPhys, System.currentTimeMillis(), metrics);
					asteroids.add(moonFragmentObj);
				}
			}

		}// /END INNERTHREAD

		// ///////////////////////
		// takes in string and calculates the R file int from res
		private Bitmap getBitmapFromImageName(String resName,
				Resources resources) {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inScaled = false;

			try {
				Class res = R.drawable.class;
				Field field = res.getField(resName);
				int drawableId = field.getInt(null);

				return BitmapFactory.decodeResource(resources, drawableId,
						options);

			} catch (Exception e) {
				e.printStackTrace();
			}
			// should return a default image r.id
			return null;// possible to cause a problem
		}

	}// END INNERVIEW

	// ///////////////////////
	// takes in string and calculates the R file int from res
	private Bitmap getBitmapFromImageName(String resName) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inScaled = false;

		try {
			Class res = R.drawable.class;
			Field field = res.getField(resName);
			int drawableId = field.getInt(null);

			return BitmapFactory.decodeResource(getResources(), drawableId,
					options);

		} catch (Exception e) {
			e.printStackTrace();
		}
		// should return a default image r.id
		return null;// possible to cause a problem
	}

}// ////end UIThread

