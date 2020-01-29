package simon.tools.BottomNavigation;
import android.app.Activity;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnSystemUiVisibilityChangeListener;
import android.graphics.Rect;
import android.graphics.Point;
import android.os.Build;
import android.view.Window;

public enum SimonBottonNavigationTool
{
	NewOne;
	public static abstract interface   DoSth{
		public abstract void NeedToDo(Activity out,Window outWindow);
	}
	private Activity ac;
	private  ViewGroup view;
	private int contentview,groupid;
	private DoSth oncreate;
	public SimonBottonNavigationTool setActivity(Activity in){
		this.ac=in;
		return this;
	}
	public SimonBottonNavigationTool setViewID(int in){
		this.groupid=in;
		return this;
	}
	public SimonBottonNavigationTool setContentView(int in){
		this.contentview=in;
		return this;
	}
	public SimonBottonNavigationTool setOnCreateSth(DoSth in){
		this.oncreate=in;
		return this;
	}
	public void use(){
		if(ac!=null&&groupid!=0&&contentview!=0)
		{
			ac.setContentView(this.contentview);
			view=ac.findViewById(groupid);

			ac.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
			this.oncreate.NeedToDo(ac,ac.getWindow());
			ac.getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(new OnSystemUiVisibilityChangeListener(){

					@Override
					public void onSystemUiVisibilityChange(int p1)
					{
						if(p1!=4){//当为4，是在多任务
							if(isShowNavBar()){
								view.setPadding(0,0,0,getDaoHangHeight());
							}
						}//if4完事
					}
				});
		}
	}
	private  int getDaoHangHeight() {
		int resourceId=0;
		int rid = ac.getResources().getIdentifier("config_showNavigationBar", "bool", "android");
		if (rid!=0){
			resourceId = ac.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
			return ac.getResources().getDimensionPixelSize(resourceId);
		}else  return 130;
    }
	private  boolean isShowNavBar() {
		//获取应用区域高度
		Rect outRect1 = new Rect();
		try {
			ac.getWindow().getDecorView().getWindowVisibleDisplayFrame(outRect1);
		} catch (ClassCastException e) {
			e.printStackTrace();
			return false;
		}
		int activityHeight = outRect1.height();
		// 获取状态栏高度
		int statuBarHeight = getStatusBarHeight();
		// 屏幕物理高度 减去 状态栏高度
		int remainHeight = getRealHeight() - statuBarHeight;
		// 剩余高度跟应用区域高度相等 说明导航栏没有显示 否则相反
		if (activityHeight == remainHeight) {
			return false;
		} else {
			return true;
		}

	}
	// 获取真实屏幕高度
	private  int getRealHeight() {
		WindowManager wm=(WindowManager) ac.getSystemService(ac.WINDOW_SERVICE);
		Point point = new Point();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
			wm.getDefaultDisplay().getRealSize(point);
		} else {
			wm.getDefaultDisplay().getSize(point);
		}
		return point.y;
	}
	//获取状态栏高度
	private  int getStatusBarHeight() {
		int result = 0;
		int resourceId = ac.getResources().getIdentifier("status_bar_height", "dimen", "android");
		if (resourceId > 0) {
			result = ac.getResources().getDimensionPixelSize(resourceId);
		}
		return result;
	}
}

