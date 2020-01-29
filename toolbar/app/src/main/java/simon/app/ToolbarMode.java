package simon.app;

import android.view.View;
import android.view.View.OnClickListener;
import android.graphics.drawable.Drawable;

public class ToolbarMode
{
	private Drawable bit;
	private View.OnClickListener cl;
	public void setImageView(Drawable in){
		this.bit=in;
	}
	public Drawable getImageView(){
		return bit;
	}
	public void setOnClickListener(View.OnClickListener in){
		this.cl=in;
	}
	public View.OnClickListener getListener(){
		return cl;
	}
}
