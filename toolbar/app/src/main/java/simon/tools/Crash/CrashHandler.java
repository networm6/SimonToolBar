package simon.tools.Crash;

import android.os.Handler;
import android.os.Looper;

public class CrashHandler implements Thread.UncaughtExceptionHandler{

	/**自定义的异常处理回调*/
	private UncaughtExceptionCallBack uncaughtExceptionCallBack;

	public interface UncaughtExceptionCallBack{
		void caughtUncaughtException(Thread t,Throwable e);
	}

	private static CrashHandler crashHandler;

	public static synchronized CrashHandler getInstance(){
		if (crashHandler == null){
			synchronized (CrashHandler.class){
				if (crashHandler == null) {
					crashHandler = new CrashHandler();
				}
			}
		}
		return crashHandler;
	}

	/**
	 * 设置自定义的运行时异常处理
	 * @param uncaughtExceptionCallBack
	 */
	public void initHandler(final UncaughtExceptionCallBack uncaughtExceptionCallBack){
		this.uncaughtExceptionCallBack = uncaughtExceptionCallBack;

		Thread.setDefaultUncaughtExceptionHandler(this);
		/**
		 * 为什么要通过new Handler.post方式而不是直接在主线程中任意位置执行 
		 * while (true) { 
		 *      try { 
		 *          Looper.loop(); 
		 *      } catch (Throwable e) {
		 *      } 
		 *  }
		 *
		 * 这是因为该方法是个死循环，若在主线程中，比如在Activity的onCreate中执行时
		 * 会导致while后面的代码得不到执行，activity的生命周期也就不能完整执行，
		 * 通过Handler.post方式可以保证不影响该条消息中后面的逻辑。
		 *
		 * 摘抄自：https://github.com/android-notes/Cockroach/blob/master/%E5%8E%9F%E7%90%86%E5%88%86%E6%9E%90.md
		 */
		new Handler(Looper.getMainLooper()).post(new Runnable() {
				@Override
				public void run() {
					while (true) {
						try {
							Looper.loop();
						} catch (Throwable e) {
							if (uncaughtExceptionCallBack != null) {
								uncaughtExceptionCallBack.caughtUncaughtException(Looper.getMainLooper().getThread(), e);
							}
						}
					}
				}
			});
	}

	@Override
	public void uncaughtException(Thread t, final Throwable e) {
		if (uncaughtExceptionCallBack != null) {
			uncaughtExceptionCallBack.caughtUncaughtException(Looper.getMainLooper().getThread(), e);
		}
	}
}

