package cc.xiaoyuanzi.recordlifecamera;

import android.app.Activity;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CameraActivity extends Activity {

    static final String TAG =  "eeeee";
    public static final int DELAY_FINISH_MILLIS = 2000;
    public static final int DELAY_TACK_PICK_MILLIS = 3000;

    //Camera object
	Camera mCamera;
	//Preview surface
	SurfaceView surfaceView;
	//Preview surface handle for callback
	SurfaceHolder surfaceHolder;
	//Note if preview windows is on.
	boolean previewing;
	
	int mCurrentCamIndex = 0;

    private void stackPhoto() {
        if (previewing) {
            mCamera.takePicture(shutterCallback, rawPictureCallback,
                    jpegPictureCallback);
        }

    }

    private Handler handler = new Handler();


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.camera);
		Log.d("eeee","camera activity start");

		surfaceView = (SurfaceView) findViewById(R.id.surfaceView1);
		surfaceHolder = surfaceView.getHolder();
		surfaceHolder.addCallback(new SurfaceViewCallback());
		//surfaceHolder.addCallback(this);
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		return true;
	}
	
	ShutterCallback shutterCallback = new ShutterCallback() {
		@Override
		public void onShutter() {
		}
	};	
	
	PictureCallback rawPictureCallback = new PictureCallback() {
		@Override
		public void onPictureTaken(byte[] arg0, Camera arg1) {

		}
	};

    private File getPicDir() {
        File sdDir = Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        Log.d("eeeePickDir",sdDir.getAbsolutePath());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String date = dateFormat.format(new Date());
        return new File(sdDir, "RecordLife_"+date);
    }

    private String getFileName() {
       return SimpleDateFormat.getInstance().format(Calendar.getInstance().getTime());
    }

	PictureCallback jpegPictureCallback = new PictureCallback() {
		@Override
		public void onPictureTaken(byte[] arg0, Camera arg1) {


            File pictureFileDir = getPicDir();

            if (!pictureFileDir.exists() && !pictureFileDir.mkdirs()) {

                Log.d("eee", "Can't create directory to save image.");
                return;

            }

            SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm");
            String date = dateFormat.format(new Date());
            String photoFile = "Picture_" + date + ".jpg";

            String filename = pictureFileDir.getPath() + File.separator + photoFile;

            File file = new File(filename);
			try {
				BufferedOutputStream bos = new BufferedOutputStream(
						new FileOutputStream(file));
				bos.write(arg0);
				bos.flush();
				bos.close();		
				scanFileToPhotoAlbum(file.getAbsolutePath());
                Log.d("eeeefile",file.getAbsolutePath());
				Toast.makeText(CameraActivity.this, "[Test] Photo take and store in" + file.toString(),Toast.LENGTH_LONG).show();


            } catch (Exception e) {
                Log.d("eeee","eee",e);
				Toast.makeText(CameraActivity.this, "Picture Failed" + e.toString(),
						Toast.LENGTH_LONG).show();
			}finally {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("eeee","finish");
                        CameraActivity.this.finish();
                    }
                }, DELAY_FINISH_MILLIS);
            }
        };
	};
	
	public void scanFileToPhotoAlbum(String path) {

        MediaScannerConnection.scanFile(CameraActivity.this,
                new String[] { path }, null,
                new MediaScannerConnection.OnScanCompletedListener() {

                    public void onScanCompleted(String path, Uri uri) {
                        Log.i("TAG", "Finished scanning " + path);
                    }
                });
    }
	private final class SurfaceViewCallback implements SurfaceHolder.Callback {
		public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) 
		{
			if (previewing) {
				mCamera.stopPreview();
				previewing = false;
			}
	
			try {
				mCamera.setPreviewDisplay(arg0);
				mCamera.startPreview();
				previewing = true;
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        CameraActivity.this.stackPhoto();
                    }
                }, DELAY_TACK_PICK_MILLIS);
				setCameraDisplayOrientation(CameraActivity.this, mCurrentCamIndex, mCamera);

			} catch (Exception e) {}
		}
		public void surfaceCreated(SurfaceHolder holder) {
//				mCamera = Camera.open();
			    //change to front camera
			    mCamera = openFrontFacingCameraGingerbread();
				// get Camera parameters
			}

			public void surfaceDestroyed(SurfaceHolder holder) {
                destroyCamera();
			}
	}

    private void destroyCamera() {
        if(mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
        }
        mCamera = null;
        previewing = false;
    }

    private Camera openFrontFacingCameraGingerbread() {
        int cameraCount = 0;
        Camera cam = null;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        cameraCount = Camera.getNumberOfCameras();

        int cameraInfoFacing = (Utils.getUseFontCamera(this)?Camera.CameraInfo.CAMERA_FACING_FRONT:
                Camera.CameraInfo.CAMERA_FACING_BACK);

        for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
            Camera.getCameraInfo(camIdx, cameraInfo);
            if (cameraInfo.facing == cameraInfoFacing) {
                try {
                    cam = Camera.open(camIdx);
                    mCurrentCamIndex = camIdx;
                } catch (RuntimeException e) {
                    Log.e(TAG, "Camera failed to open: " + e.getLocalizedMessage());
                }
            }
        }

        return cam;
	}
	
	private static void setCameraDisplayOrientation(Activity activity,int cameraId, Camera camera)
	{    
		   Camera.CameraInfo info = new Camera.CameraInfo(); 
	       Camera.getCameraInfo(cameraId, info);      
	       int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
	       
	       //degrees  the angle that the picture will be rotated clockwise. Valid values are 0, 90, 180, and 270. 
	       //The starting position is 0 (landscape). 
	       int degrees = 0;
	       switch (rotation) 
	       {   
	           case Surface.ROTATION_0: degrees = 0; break;         
	           case Surface.ROTATION_90: degrees = 90; break;    
	           case Surface.ROTATION_180: degrees = 180; break; 
	           case Surface.ROTATION_270: degrees = 270; break;  
	        }      
	       int result;  
	       if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT)
	       {        
	           result = (info.orientation + degrees) % 360;     
	           result = (360 - result) % 360;  // compensate the mirror   
	       } 
	       else 
	       {  
	       // back-facing       
	          result = (info.orientation - degrees + 360) % 360;   
	       }     
	       camera.setDisplayOrientation(result);  
	} 
	
	

}
