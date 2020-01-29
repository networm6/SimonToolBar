package simon.app;
import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import simon.tools.Bangscreentools.SinkFullScreen;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.widget.FrameLayout;
import android.os.Build;
import simon.widget.view.BlurView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.graphics.Color;
import android.widget.Button;
import com.mycompany.myapp.R;
import android.widget.ImageView;
import android.view.ViewGroup;
import android.widget.Toast;
import android.graphics.drawable.Drawable;

public abstract class SimonActivity extends Activity
{

    public BlurView ToolbarBlurView;
    public ImageView ToolbarLeftImageView;
    public LinearLayout ToolbarRightMode,ToolbarCentreMode;
	public FrameLayout ToolbarBackLayout;
	private float ToolbarHeight,Toolbarpadding,ToolbarModeHeight;
	
	public float getToolbarHeight(){
		return ToolbarHeight;
	}
	public float getToolbarpadding(){
		return Toolbarpadding;
	}
	public float tgetToolbarModeHeight(){
		return ToolbarModeHeight;
	}
	@Override
	public void setContentView(int layoutResID)
	{
		intoToolbar(layoutResID,null,null);
	}
	
	@Override
	public void setContentView(View view, ViewGroup.LayoutParams params)
	{
		intoToolbar(0,view,params);
	}

	@Override
	public void setContentView(View view)
	{
		intoToolbar(0,view,null);
	}
	private void intoToolbar(int lay,View in,ViewGroup.LayoutParams pr){
		fullscreenPRO();          //去除系统bar
	
		Toolbarpadding=StatusBarHeight()/2;        //数据处理区
		int padding=(int) (Toolbarpadding*0.6f);
		int leftwh=(int)( StatusBarHeight()*0.9f);
		ToolbarModeHeight=leftwh;
		ToolbarHeight=StatusBarHeight()*3;


		FrameLayout Decorview=new FrameLayout(this);        //把Decorview加载出来，Decorview就是rootview,一个framelayout，下面是用户setcontentview，上面是我们的toolbar
		FrameLayout.LayoutParams decor=new FrameLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
		Decorview.setLayoutParams(decor);
		if(lay!=0){
			LayoutInflater.from(this).inflate(lay,Decorview);//把用户的setcontent加载到decorview，这是第一层
		}else{
		if(pr!=null)
		Decorview.addView(in);
		else
			Decorview.addView(in,pr);
			}
	

		 ToolbarBackLayout=new  FrameLayout(this);//这是toolbar层，最后也要加载到decorview上，做第二层
		FrameLayout.LayoutParams backparams=new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT,(int)ToolbarHeight);
		ToolbarBackLayout.setLayoutParams(backparams);



		ToolbarBlurView=new BlurView(this);//toolbar的背景，实时模糊
		ToolbarBlurView.setLayoutParams(backparams);

		ToolbarBackLayout.addView(ToolbarBlurView);



		ToolbarLeftImageView=new ImageView(this);//toolbar的左区，一个imageview
		FrameLayout.LayoutParams left=new FrameLayout.LayoutParams(leftwh,leftwh);
		left.gravity=Gravity.LEFT|Gravity.BOTTOM;
		left.setMargins(padding,0,0,padding);

		ToolbarBackLayout.addView(ToolbarLeftImageView,left);



	    ToolbarRightMode=new LinearLayout(this);//toolbar的右模块
		ToolbarRightMode.setOrientation(LinearLayout.HORIZONTAL);
		ToolbarRightMode.setGravity(Gravity.RIGHT);
		FrameLayout.LayoutParams right=new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,leftwh);
		right.gravity=Gravity.RIGHT|Gravity.BOTTOM;
		right.setMargins( 0,0,padding,padding);

		ToolbarBackLayout.addView(ToolbarRightMode,right);



	    ToolbarCentreMode=new LinearLayout(this);//toolbar的中间模块
		ToolbarCentreMode.setOrientation(LinearLayout.HORIZONTAL);
		ToolbarCentreMode.setGravity(Gravity.CENTER);
		FrameLayout.LayoutParams center=new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,leftwh);
		center.gravity=Gravity.CENTER|Gravity.BOTTOM;
		center.setMargins( 0,0,0,padding);

		ToolbarBackLayout.addView(ToolbarCentreMode,center);



		Decorview.addView(ToolbarBackLayout);//为decorview添加第二层，即toolbar
		getWindow().setContentView(Decorview);//把decorview加载到用户的屏幕上
		
	}
	
	public float StatusBarHeight(){
		float result = 0;
		float resourceId = this.getResources().getIdentifier("status_bar_height", "dimen", "android");
		if (resourceId > 0) 
			result = this.getResources().getDimensionPixelSize((int)resourceId);
		return result;
	}
    private View setview(ToolbarMode mode){
		View v=new View(this);
		v.setOnClickListener(mode.getListener());
		v.setBackground(mode.getImageView());
		LinearLayout.LayoutParams vp=new LinearLayout.LayoutParams((int)ToolbarModeHeight,(int)ToolbarModeHeight);
		v.setLayoutParams(vp);
		return v;
	}
	public View setCenterMode(ToolbarMode mode){
   View r=   setview(mode);
   ToolbarCentreMode.addView(r);
		return r;
	}
    
	public View setRightMode(ToolbarMode mode){
        View r=   setview(mode);
		ToolbarRightMode.addView(r);
		return r;
	}
	
	public void fullscreen(){
	getWindow().addFlags(1024);
		if(getActionBar()!=null)
			getActionBar().hide();
		if((getRequestedOrientation()==2?true:null)!=null){
			SinkFullScreen s= SinkFullScreen.INSTANCE;
			s.blockStatusCutout(getWindow());
		}else{
			SinkFullScreen s= SinkFullScreen.INSTANCE;
			s.extendStatusCutout(getWindow(),this);
		}
	}
  public void fullscreenPRO(){
	  fullscreen();
	  getWindow().addFlags(0x08000000);
  }
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{

		if (!this.isTaskRoot()) {
			Intent intent = getIntent();
			if (intent != null) {
				String action = intent.getAction();
				if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && Intent.ACTION_MAIN.equals(action)) {
					finish();
					return;
				}
			}
		}


		super.onCreate(savedInstanceState);

		
	}
	public void back(View v){
		this.finish();
	}



	@Override
    public void startActivityForResult(Intent intent, int requestCode, @Nullable Bundle options) {
        if (startActivitySelfCheck(intent)) {
            // 查看源码得知 startActivity 最终也会调用 startActivityForResult
            super.startActivityForResult(intent, requestCode, options);
        }
    }

    private String mActivityJumpTag;
    private long mActivityJumpTime;


    // * 检查当前 Activity 是否重复跳转了，不需要检查则重写此方法并返回 true 即可
	//  *
	//  * @param intent          用于跳转的 Intent 对象
	//  * @return                检查通过返回true, 检查不通过返回false

    protected boolean startActivitySelfCheck(Intent intent) {
        // 默认检查通过
        boolean result = true;
        // 标记对象
        String tag;
        if (intent.getComponent() != null) { // 显式跳转
            tag = intent.getComponent().getClassName();
        }else if (intent.getAction() != null) { // 隐式跳转
            tag = intent.getAction();
        }else {
            return result;
        }

        if (tag.equals(mActivityJumpTag) && mActivityJumpTime >= SystemClock.uptimeMillis() - 500) {
            // 检查不通过
            result = false;
        }

        // 记录启动标记和时间
        mActivityJumpTag = tag;
        mActivityJumpTime = SystemClock.uptimeMillis();
        return result;
    }
}




