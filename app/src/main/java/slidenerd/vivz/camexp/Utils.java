package slidenerd.vivz.camexp;

import android.content.ContentValues;
import android.content.Context;
import android.hardware.Camera;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by vivz on 24/10/15.
 */
public class Utils {
    public static int getCameraId() {
        int cameraId = -1;
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            Log.d("VIVZ", "Camera spotted " + i);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                Log.d("VIVZ", "Camera found");
                cameraId = i;
                break;
            }
        }
        return cameraId;
    }

    public static void savePicture(Context context, byte[] data) {
        Uri imageFileUri =
                context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new ContentValues());
        try {
            OutputStream imageFileOS =
                    context.getContentResolver().openOutputStream(imageFileUri);
            imageFileOS.write(data);
            imageFileOS.flush();
            imageFileOS.close();
        } catch (FileNotFoundException e) {
            Toast t = Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT);
            t.show();
        } catch (IOException e) {
            Toast t = Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT);
            t.show();
        }
    }
}
