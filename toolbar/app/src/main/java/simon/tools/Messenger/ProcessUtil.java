package simon.tools.Messenger;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.os.Process;
import java.util.List;

class ProcessUtil {
	
	public static boolean isMainProcess(Context context) {
		Context context2 = context;
		return context2.getPackageName().equals(getProcessName(context2));
	}

	@TargetApi(3)
	public static String getProcessName(Context context) {
		List<RunningAppProcessInfo> runningAppProcesses = ((ActivityManager) context.getSystemService("activity")).getRunningAppProcesses();
		if (runningAppProcesses == null) {
			return null;
		}
		for (RunningAppProcessInfo runningAppProcessInfo : runningAppProcesses) {
			if (runningAppProcessInfo.pid == Process.myPid() && runningAppProcessInfo.processName != null) {
				return runningAppProcessInfo.processName;
			}
		}
		return null;
	}
}
