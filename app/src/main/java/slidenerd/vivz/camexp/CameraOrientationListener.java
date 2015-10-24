package slidenerd.vivz.camexp;

import android.content.Context;
import android.hardware.Camera;
import android.view.OrientationEventListener;

/**
 * Created by vivz on 24/10/15.
 */
public class CameraOrientationListener extends OrientationEventListener {
    private int mCameraId = -1;
    private Camera.Parameters mParameters;

    public CameraOrientationListener(Context context, int rate, int cameraId, Camera.Parameters parameters) {
        super(context, rate);
        mCameraId = cameraId;
        mParameters = parameters;
    }

    @Override
    public void onOrientationChanged(int orientation) {
        if (orientation == ORIENTATION_UNKNOWN || mCameraId == -1 || mParameters == null) return;
        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(mCameraId, info);
        orientation = (orientation + 45) / 90 * 90;
//                Log.d("VIVZ", "orientation " + orientation + " + 45 " + (orientation + 45) + " /90 " + ((orientation + 45) / 90) + " *90 " + ((orientation + 45) / 90 * 90));
        int rotation = 0;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            rotation = (info.orientation - orientation + 360) % 360;
//                    Log.d("VIVZ", "rotation " + (info.orientation - orientation) + " + 360 " + (info.orientation - orientation + 360) + " %360 " + (info.orientation - orientation + 360) % 360);
        } else {  // back-facing camera
            rotation = (info.orientation + orientation) % 360;
//                    Log.d("VIVZ", "rotation " + (info.orientation + orientation) + " + 360 " + (info.orientation + orientation + 360) + " %360 " + (info.orientation + orientation + 360) % 360);
        }
        mParameters.setRotation(rotation);
    }
}
