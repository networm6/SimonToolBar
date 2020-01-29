package simon.widget.view;
import android.app.Activity;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.Gallery.LayoutParams;
import simon.app.SimonActivity;
import android.view.View;
import android.view.Gravity;
import android.graphics.Color;
import com.mycompany.myapp.R;
import android.widget.LinearLayout;
import android.view.Window;
import android.view.LayoutInflater;
import android.widget.TextView;
import simon.tools.Viewanimator.AnimationBuilder;
import simon.tools.Viewanimator.AnimationListener;
import simon.tools.Viewanimator.ViewAnimator;
import android.util.TypedValue;
import simon.app.ToolbarMode;
import android.widget.Toast;


public class Toolbar extends SimonActivity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		
		setContentView(R.layout.ee);
		ToolbarBlurView.setBlurRadius(40);
		ToolbarBlurView.setbackground(Color.parseColor("#4FD1FF8C"));
		ToolbarLeftImageView.setBackgroundResource(R.drawable.ic_launcher);
		TextView need=new TextView(this);
		need.setText("uued");
		need.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
		ToolbarCentreMode.addView(need);
		ViewAnimator.animate(ToolbarLeftImageView)
			.duration(400)
			.slideTopIn()
			.slideLeftIn()
			.andAnimate(ToolbarCentreMode)
			.duration(400)
			.slideTopIn()
			.andAnimate(ToolbarRightMode)
			.duration(400)
			.slideTopIn()
			.slideRightIn()
			.start();
			ToolbarMode searchmode=new ToolbarMode();
		searchmode.setOnClickListener(new View.OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					Toast.makeText(Toolbar.this,"点击了右侧模块",10).show();
				}
			});
			searchmode.setImageView(getDrawable(R.drawable.searchimage));
			
		setRightMode(searchmode);

		
		ToolbarMode notimode=new ToolbarMode();
		notimode.setImageView(getDrawable(R.drawable.notification));
		notimode.setOnClickListener(new View.OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					Toast.makeText(Toolbar.this,"notification",10).show();
				}
			});
		setRightMode(notimode);
		
		}


	
	
	

	

	
	
	
	
	
	
	
	
	}
	
	/*
	FrameLayout Decorview=new FrameLayout(this);
		FrameLayout.LayoutParams decor=new FrameLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
		Decorview.setLayoutParams(decor);
		LinearLayout Phoneview=new LinearLayout(this);
		FrameLayout.LayoutParams phone=new FrameLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
		Phoneview.setLayoutParams(phone);
		Decorview.addView(Phoneview);
		
		FrameLayout Toolbarback=new  FrameLayout(this);
		//par先宽再高
		float toolbackheight=Bar()*3;
		FrameLayout.LayoutParams backparams=new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT,(int)toolbackheight);
		Toolbarback.setLayoutParams(backparams);
	           	BlurView blurback=new BlurView(this);
	        	blurback.setLayoutParams(backparams);
	    Toolbarback.addView(blurback);
		
		SimonImageView leftimage=new SimonImageView(this);
		int leftwh=(int) Bar();
		FrameLayout.LayoutParams left=new FrameLayout.LayoutParams(leftwh,leftwh);
		left.gravity=Gravity.LEFT|Gravity.BOTTOM|Gravity.LEFT;
		Toolbarback.addView(leftimage,left);
		
		
		Toolbarback.setBackgroundColor(Color.BLUE);
		leftimage.setImageResource(R.drawable.ic_launcher);
		
		Decorview.addView(Toolbarback);
		fullscreenPRO();
		*/
/*
Button button  = new Button(this);  
button.setText("One");  
//此处相当于布局文件中的Android：gravity属性  
button.setGravity(Gravity.CENTER);  
  
LinearLayout linear = new LinearLayout(this);  
//注意，对于LinearLayout布局来说，设置横向还是纵向是必须的！否则就看不到效果了。  
linear.setOrientation(LinearLayout.VERTICAL);  
  
LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);  
//此处相当于布局文件中的Android:layout_gravity属性  
lp.gravity = Gravity.RIGHT;  
  
linear.addView(button, lp);  
setContentView(linear)
*/
