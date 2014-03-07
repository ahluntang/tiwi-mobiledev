package be.ugent.oomo.labo_3;

import java.util.List;

import com.crashlytics.android.Crashlytics;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.Surface;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends Activity implements Tracker, SensorEventListener {
	
	private GLSurfaceView surfaceView;
	private TextView sensorinfo;
	private TextView velocityinfo;
	private TextView distanceinfo;
	private SensorManager sensorManager;
	private float [] rotationMatrix;
	private float gravity[];
	private float linear_acceleration[];
	private float geomagnetic[];

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Crashlytics.start(this); // debug analyzer
		// remove title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
		setContentView(R.layout.activity_main);

		sensorinfo = (TextView) findViewById(R.id.sensorinfo);
		velocityinfo = (TextView) findViewById(R.id.velocityinfo);
		distanceinfo = (TextView) findViewById(R.id.distanceinfo);
		
		// initialise rotationmatrix
		rotationMatrix = new float [16]; 
		android.opengl.Matrix.setIdentityM(rotationMatrix, 0);
		geomagnetic = new float[3];
		geomagnetic[0] = 15;
		geomagnetic[1] = -44;
		geomagnetic[2] = -33;
		
		// initialise surfaceview and set renderer
		surfaceView = new GLSurfaceView(this);
		OpenGLRenderer renderer = new OpenGLRenderer(this);
		surfaceView.setRenderer(renderer);
		surfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
		
		
		// get relative layout and add the surfaceview
		RelativeLayout rl = (RelativeLayout) findViewById(R.id.rellayout);
	    rl.addView(surfaceView, 0);
	    
	    // make sure textviews are in front of surfaceview // using index in addView
	    //sensorinfo.bringToFront();
	    //velocityinfo.bringToFront();
	    //distanceinfo.bringToFront();

	    // get sensormanager
	    sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
	    
	    // request all sensors from sensormanager
	    List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ALL);
	    
	    StringBuilder sensorbuilder = new StringBuilder();
	    for(Sensor sensor: sensors){
	    	sensorbuilder.append('\n');
	    	sensorbuilder.append(sensor.getName());
	    }
	    sensorbuilder.deleteCharAt(0);
	    String sensorlist = sensorbuilder.toString();
	    sensorinfo.setText(sensorlist);

	    List<Sensor> accelerometers = sensorManager.getSensorList( Sensor.TYPE_ACCELEROMETER  );
	    for(Sensor sensor: accelerometers){
	    	sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_FASTEST);
	    }
	    
	    List<Sensor> magneticfields = sensorManager.getSensorList( Sensor.TYPE_MAGNETIC_FIELD  );
	    for(Sensor sensor: magneticfields){
	    	sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_FASTEST);
	    }
	    
	    /*List<Sensor> gyroscopes = sensorManager.getSensorList( Sensor.TYPE_GYROSCOPE  );
	    for(Sensor sensor: gyroscopes){
	    	sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_FASTEST);
	    }*/
	    
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	protected void onResume() {
		super.onResume();
		surfaceView.onResume();
	}
	

	@Override
	protected void onPause() {
		super.onPause();
		surfaceView.onPause();
	}

	// implements Tracker
	@Override
	public float[] getRotationMatrix() {
		return rotationMatrix;
	}

	// implements sensorEventListener
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
			
			float accelX = event.values[0];
			float accelY= event.values[1];
			float accelZ = event.values[2];
			
			// In this example, alpha is calculated as t / (t + dT),
			// where t is the low-pass filter's time-constant and
			// dT is the event delivery rate.

			final float alpha = (float) 0.8;

			gravity = new float[3];
			linear_acceleration = new float[3];
	
			// Isolate the force of gravity with the low-pass filter.
			gravity[0] = alpha * gravity[0] + (1 - alpha) * accelX;
			gravity[1] = alpha * gravity[1] + (1 - alpha) * accelY;
			gravity[2] = alpha * gravity[2] + (1 - alpha) * accelZ;
	
			// Remove the gravity contribution with the high-pass filter.
			linear_acceleration[0] = accelX - gravity[0];
			linear_acceleration[1] = accelY - gravity[1];
			linear_acceleration[2] = accelZ - gravity[2];
			StringBuilder velocitybuilder = new StringBuilder();
			velocitybuilder.append("VelocityX=");
			velocitybuilder.append(linear_acceleration[0]);
			velocitybuilder.append("\nVelocityY=");
			velocitybuilder.append(linear_acceleration[1]);
			velocitybuilder.append("\nVelocityZ=");
			velocitybuilder.append(linear_acceleration[2]);
			velocityinfo.setText(velocitybuilder.toString());
			
			//sensorManager.getRotationMatrix(rotationMatrix, I, event.values, geomagnetic);
			
			// use gravity vector (low pass filter) to minimize jitter
			// original: event.values as rotationvector
			
			
			// remap if rotation is not 0
			float[] remappedRotationMatrix = rotationMatrix.clone();
			switch (getWindowManager().getDefaultDisplay()
                    .getRotation()) {
            case Surface.ROTATION_0:
                SensorManager.remapCoordinateSystem(rotationMatrix,
                        SensorManager.AXIS_X, 
                        SensorManager.AXIS_Y,
                        remappedRotationMatrix);
                break;
            case Surface.ROTATION_90:
                SensorManager.remapCoordinateSystem(rotationMatrix,
                		SensorManager.AXIS_MINUS_Y,//SensorManager.AXIS_Y,
                		SensorManager.AXIS_X, //SensorManager.AXIS_MINUS_X,
                        remappedRotationMatrix);
                Log.i("orientation","90");
                break;
            case Surface.ROTATION_180:
                SensorManager.remapCoordinateSystem(rotationMatrix,
                        SensorManager.AXIS_MINUS_X,
                        SensorManager.AXIS_MINUS_Y,
                        remappedRotationMatrix);
                Log.i("orientation","180");
                break;
            case Surface.ROTATION_270:
                SensorManager.remapCoordinateSystem(rotationMatrix,
                		SensorManager.AXIS_Y, //SensorManager.AXIS_MINUS_Y,
                		SensorManager.AXIS_MINUS_X, //SensorManager.AXIS_X, 
                		remappedRotationMatrix);
                Log.i("orientation","270");
                break;
            }

            //Calculate Orientation
            //float results[] = new float[3];
            //SensorManager.getOrientation(rotationMatrix, results);
            
            float[] inclination = new float[16];
            //sensorManager.getInclination(inclination);
            sensorManager.getRotationMatrix(rotationMatrix, null, gravity, geomagnetic);
           
			
			// no gyroscope :-(
			//sensorManager.getRotationMatrixFromVector(remappedRotationMatrix, gravity);
			
			surfaceView.requestRender();
		} else if(event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD){
			geomagnetic = event.values.clone();
			StringBuilder geobuilder = new StringBuilder();

			geobuilder.append("DistanceX=");
			geobuilder.append(geomagnetic[0]);
			geobuilder.append("\nDistanceY=");
			geobuilder.append(geomagnetic[1]);
			geobuilder.append("\nDistanceZ=");
			geobuilder.append(geomagnetic[2]);
			distanceinfo.setText(geobuilder.toString());
		}
		
	} 

}
