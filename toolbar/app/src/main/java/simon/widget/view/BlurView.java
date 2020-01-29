package simon.widget.view;

import android.view.View;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewTreeObserver;
import android.content.ContextWrapper;
import android.app.Activity;
import android.graphics.RectF;
import android.graphics.PorterDuffXfermode;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PorterDuff;
import android.graphics.Color;
import android.widget.LinearLayout;


public class BlurView extends View {

	private float mDownsampleFactor; 

	private float mBlurRadius; 
	private final SupportLibraryBlur mBlurImpl;
	private boolean mDirty;
	private Bitmap mBitmapToBlur, mBlurredBitmap;
	private Canvas mBlurringCanvas;
	private boolean mIsRendering;
	private Paint mPaint;
    private int back=Color.TRANSPARENT;
	private View mDecorView;
	private boolean mDifferentRoot;
	private  int RENDERING_COUNT;





	private final Rect mRectSrc = new Rect(), mRectDst = new Rect();



	public BlurView(Context context) {
		super(context);

		mBlurImpl = getBlurImpl();
		mBlurRadius =2; 
		mDownsampleFactor = 1;//像素密度，越大越粗糙



		mPaint = new Paint();
	}

	protected SupportLibraryBlur getBlurImpl() {


		SupportLibraryBlur impl = new SupportLibraryBlur();
		Bitmap bmp = Bitmap.createBitmap(4, 4, Bitmap.Config.ARGB_8888);
		impl.prepare(getContext(), bmp, 4);
		impl.release();
		bmp.recycle();



		return new SupportLibraryBlur();

	}
    public void setbackground(int in){
		this.back=in;
	}
	public void setBlurRadius(float radius) {
		if (mBlurRadius != radius) {
			mBlurRadius = radius;
			mDirty = true;
			invalidate();
		}
	}

	public void setDownsampleFactor(float factor) {
		if (factor <= 0) {
			throw new IllegalArgumentException("Downsample factor must be greater than 0.");
		}

		if (mDownsampleFactor != factor) {
			mDownsampleFactor = factor;
			mDirty = true; releaseBitmap();
			invalidate();
		}
	}



	private void releaseBitmap() {
		if (mBitmapToBlur != null) {
			mBitmapToBlur.recycle();
			mBitmapToBlur = null;
		}
		if (mBlurredBitmap != null) {
			mBlurredBitmap.recycle();
			mBlurredBitmap = null;
		}
	}

	protected void release() {
		releaseBitmap();
		mBlurImpl.release();
	}

	protected boolean prepare() {
		if (mBlurRadius == 0) {
			release();
			return false;
		}

		float downsampleFactor = mDownsampleFactor;
		float radius = mBlurRadius / downsampleFactor;
		if (radius > 25) {
			downsampleFactor = downsampleFactor * radius / 25;
			radius = 25;
		}

		final int width = getWidth();
		final int height = getHeight();

		int scaledWidth = Math.max(1, (int) (width / downsampleFactor));
		int scaledHeight = Math.max(1, (int) (height / downsampleFactor));

		boolean dirty = mDirty;

		if (mBlurringCanvas == null || mBlurredBitmap == null
			|| mBlurredBitmap.getWidth() != scaledWidth
			|| mBlurredBitmap.getHeight() != scaledHeight) {
			dirty = true;
			releaseBitmap();

			boolean r = false;
			try {
				mBitmapToBlur = Bitmap.createBitmap(scaledWidth, scaledHeight, Bitmap.Config.ARGB_8888);
				if (mBitmapToBlur == null) {
					return false;
				}
				mBlurringCanvas = new Canvas(mBitmapToBlur);

				mBlurredBitmap = Bitmap.createBitmap(scaledWidth, scaledHeight, Bitmap.Config.ARGB_8888);
				if (mBlurredBitmap == null) {
					return false;
				}

				r = true;
			} catch (OutOfMemoryError e) {

			} finally {
				if (!r) {
					release();
					return false;
				}
			}
		}

		if (dirty) {
			if (mBlurImpl.prepare(getContext(), mBitmapToBlur, radius)) {
				mDirty = false;
			} else {
				return false;
			}
		}

		return true;
	}

	protected void blur(Bitmap bitmapToBlur, Bitmap blurredBitmap) {
		mBlurImpl.blur(bitmapToBlur, blurredBitmap);
	}

	private final ViewTreeObserver.OnPreDrawListener preDrawListener = new ViewTreeObserver.OnPreDrawListener() {
		@Override
		public boolean onPreDraw() {
			final int[] locations = new int[2];
			Bitmap oldBmp = mBlurredBitmap;
			View decor = mDecorView;
			if (decor != null && isShown() && prepare()) {
				boolean redrawBitmap = mBlurredBitmap != oldBmp;
				oldBmp = null;
				decor.getLocationOnScreen(locations);
				int x = -locations[0];
				int y = -locations[1];

				getLocationOnScreen(locations);
				x += locations[0];
				y += locations[1];


				mBitmapToBlur.eraseColor( 0xffffff);

				int rc = mBlurringCanvas.save();
				mIsRendering = true;
				RENDERING_COUNT++;
				try {
					mBlurringCanvas.scale(1.f * mBitmapToBlur.getWidth() / getWidth(), 1.f * mBitmapToBlur.getHeight() / getHeight());
					mBlurringCanvas.translate(-x, -y);
					if (decor.getBackground() != null) {
						decor.getBackground().draw(mBlurringCanvas);
					}
					decor.draw(mBlurringCanvas);
				} catch (RuntimeException e) {
				} finally {
					mIsRendering = false;
					RENDERING_COUNT--;
					mBlurringCanvas.restoreToCount(rc);
				}

				blur(mBitmapToBlur, mBlurredBitmap);

				if (redrawBitmap || mDifferentRoot) {
					invalidate();
				}
			}

			return true;
		}
	};

	protected View getActivityDecorView() {
		Context ctx = getContext();
		for (int i = 0; i < 4 && ctx != null && !(ctx instanceof Activity) && ctx instanceof ContextWrapper; i++) {
			ctx = ((ContextWrapper) ctx).getBaseContext();
		}
		if (ctx instanceof Activity) {
			return ((Activity) ctx).getWindow().getDecorView();
		} else {
			return null;
		}
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		mDecorView = getActivityDecorView();
		if (mDecorView != null) {
			mDecorView.getViewTreeObserver().addOnPreDrawListener(preDrawListener);
			mDifferentRoot = mDecorView.getRootView() != getRootView();
			if (mDifferentRoot) {
				mDecorView.postInvalidate();
			}
		} else {
			mDifferentRoot = false;
		}
	}

	@Override
	protected void onDetachedFromWindow() {
		if (mDecorView != null) {
			mDecorView.getViewTreeObserver().removeOnPreDrawListener(preDrawListener);
		}
		release();
		super.onDetachedFromWindow();
	}

	@Override
	public void draw(Canvas canvas) {
		if (mIsRendering) {
			throw new RuntimeException();
		} else if (RENDERING_COUNT > 0) {
		} else {
			super.draw(canvas);
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		drawBlurredBitmap(canvas, mBlurredBitmap, back);
	}


	protected void drawBlurredBitmap(Canvas canvas, Bitmap blurredBitmap, int overlayColor) {
		if (blurredBitmap != null) {
			mRectSrc.right = blurredBitmap.getWidth();
			mRectSrc.bottom = blurredBitmap.getHeight();
			mRectDst.right = getWidth();
			mRectDst.bottom = getHeight();
			canvas.drawBitmap(blurredBitmap, mRectSrc, mRectDst, null);
		}
		mPaint.setColor(overlayColor);

		canvas.drawRect(mRectDst, mPaint);
	}





}




