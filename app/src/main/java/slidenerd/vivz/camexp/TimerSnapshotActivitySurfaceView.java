package slidenerd.vivz.camexp;

import android.hardware.Camera;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;

public class TimerSnapshotActivitySurfaceView extends AppCompatActivity implements SurfaceHolder.Callback, View.OnClickListener, Camera.PictureCallback {

    SurfaceView cameraView;
    SurfaceHolder surfaceHolder;
    Camera camera;
    Button startButton;
    TextView countdownTextView;
    Handler timerUpdateHandler;
    boolean timerRunning = false;
    int currentTime = 10;
    int mCameraId = -1;
    CameraOrientationListener mListener;
    Camera.Parameters mParameters;
    private Runnable timerUpdateTask = new Runnable() {
        public void run() {
            if (currentTime > 1) {
                currentTime--;
                timerUpdateHandler.postDelayed(timerUpdateTask, 1000);
            } else {
                camera.takePicture(null, null, TimerSnapshotActivitySurfaceView.this);
                timerRunning = false;
                currentTime = 10;
            }
            countdownTextView.setText("" + currentTime);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer_snapshot_activity_surface_view);
        mListener = new CameraOrientationListener(this, SensorManager.SENSOR_DELAY_UI, mCameraId, mParameters);
        cameraView = (SurfaceView) this.findViewById(R.id.CameraView);
        surfaceHolder = cameraView.getHolder();
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        surfaceHolder.addCallback(this);
        countdownTextView = (TextView) findViewById(R.id.CountDownTextView);
        startButton = (Button) findViewById(R.id.CountDownButton);
        startButton.setOnClickListener(this);
        timerUpdateHandler = new Handler();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mCameraId = Utils.getCameraId();
        if (mCameraId == -1) return;
        camera = Camera.open(mCameraId);
        try {
            mParameters = camera.getParameters();
            camera.setPreviewDisplay(holder);
        } catch (IOException exception) {
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
    public void onClick(View v) {
        if (!timerRunning) {
            timerRunning = true;
            timerUpdateHandler.post(timerUpdateTask);
        }
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        Utils.savePicture(this, data);
        camera.startPreview();
    }
}
