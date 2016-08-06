package cc.xiaoyuanzi.recordlifecamera;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class TackPictureReceiver extends BroadcastReceiver {

    public static final String ACTION = "cc.xiaoyuanzi.tack.picture";
    public TackPictureReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(ACTION.equals(intent.getAction())) {
            Log.d("eeeee", "get action start camera");
            Intent cameraActivityIntent = new Intent(context, CameraActivity.class);
            cameraActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(cameraActivityIntent);
        }
    }
}
