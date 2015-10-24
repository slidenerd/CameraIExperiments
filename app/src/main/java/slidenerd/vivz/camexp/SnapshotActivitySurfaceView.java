package slidenerd.vivz.camexp;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;

public class SnapshotActivitySurfaceView extends AppCompatActivity implements SurfaceHolder.Callback, Camera.PictureCallback, View.OnClickListener {

    public static int LARGEST_WIDTH = 500;
    public static int LARGEST_HEIGHT = 500;
    SurfaceView cameraView;
    SurfaceHolder surfaceHolder;
    Camera camera;
    int mCameraId = -1;
    Camera.Parameters mParameters;

    private CameraOrientationListener mListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_snapshot_surface_view);
        mListener = new CameraOrientationListener(this, SensorManager.SENSOR_DELAY_UI, mCameraId, mParameters);
        LARGEST_WIDTH = getResources().getDisplayMetrics().widthPixels;
        LARGEST_HEIGHT = getResources().getDisplayMetrics().heightPixels;
        cameraView = (SurfaceView) this.findViewById(R.id.CameraView);
        surfaceHolder = cameraView.getHolder();
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        surfaceHolder.addCallback(this);

        cameraView.setFocusable(true);
        cameraView.setFocusableInTouchMode(true);
        cameraView.setClickable(true);
        cameraView.setOnClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mListener.canDetectOrientation()) {
            mListener.enable();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mListener.canDetectOrientation()) {
            mListener.disable();
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mCameraId = Utils.getCameraId();
        if (mCameraId == -1) {
            return;
        }
        camera = Camera.open(mCameraId);
        CameraInfo cameraInfo = new CameraInfo();
        camera.getCameraInfo(mCameraId, cameraInfo);
        try {

            mParameters = camera.getParameters();
            Toast.makeText(this, cameraInfo.orientation + " ", Toast.LENGTH_SHORT).show();
            if (this.getResources().getConfiguration().orientation !=
                    Configuration.ORIENTATION_LANDSCAPE) {
//                // This is an undocumented although widely known feature
//                parameters.set("orientation", "portrait");
                // For Android 2.2 and above
                camera.setDisplayOrientation(90);
                mParameters.setRotation(90);
            }
//            int rotation = getWindowManager().getDefaultDisplay().getRotation();
//            Toast.makeText(this, rotation + " rotation", Toast.LENGTH_SHORT).show();
//            int bestWidth = 0;
//            int bestHeight = 0;
//            List<Camera.Size> previewSizes = parameters.getSupportedPreviewSizes();
//            if (previewSizes.size() > 1) {
//                Iterator<Camera.Size> cei = previewSizes.iterator();
//                while (cei.hasNext()) {
//                    Camera.Size aSize = cei.next();
//                    Log.v("SNAPSHOT", "Checking " + aSize.width + " x " + aSize.height);
//                    if (aSize.width > bestWidth && aSize.width <= LARGEST_WIDTH && aSize.height > bestHeight && aSize.height <= LARGEST_HEIGHT) {
//                        // So far it is the biggest without going over the screen dimensions
//                        bestWidth = aSize.width;
//                        bestHeight = aSize.height;
//                    }
//                }
//                if (bestHeight != 0 && bestWidth != 0) {
//                    Log.v("SNAPSHOT", "Using " + bestWidth + " x " + bestHeight);
//                    parameters.setPreviewSize(bestWidth, bestHeight);
//                    cameraView.setLayoutParams(new LinearLayout.LayoutParams(bestWidth, bestHeight));
//                }
//            }
            camera.setPreviewDisplay(holder);

        } catch (IOException e) {
            camera.release();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        camera.startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        camera.stopPreview();
        camera.release();
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        Utils.savePicture(this, data);
        camera.startPreview();
    }

    @Override
    public void onClick(View v) {
        camera.takePicture(null, null, this);
    }

}
