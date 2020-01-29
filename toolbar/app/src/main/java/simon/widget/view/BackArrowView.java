package simon.widget.view;
import android.view.View;
import android.graphics.Paint;
import android.content.Context;
import android.util.AttributeSet;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Color;

public class BackArrowView extends View {
    /**
     * View默认最小宽度
     */
    private static final int DEFAULT_MIN_WIDTH = 100;
    /**
     * Material Design风格
     */
    private static final int ARROW_STYLE_MATERIAL_DESIGN = 1;
    /**
     * 微信风格
     */
    private static final int ARROW_STYLE_WECHAT_DESIGN = 2;

    /**
     * 控件宽
     */
    private int mViewWidth;
    /**
     * 控件高
     */
    private int mViewHeight;
    /**
     * 箭头开始的距离
     */
    private float mArrowStartDistance;
    /**
     * 箭头的2个边的长度
     */
    private float mArrowLineLength;
    /**
     * 箭头颜色
     */
    private int mColor;
    /**
     * 箭头粗细
     */
    private float mArrowStrokeWidth;
    /**
     * 风格模式
     */
    private int mArrowStyle;

    /**
     * 画笔
     */
    private Paint mPaint;
    /**
     * 箭头Path
     */
    private Path mArrowPath;

    public BackArrowView(Context context) {
        this(context, null);
    }

    public BackArrowView(Context context,  AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BackArrowView(Context context,  AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context,  AttributeSet attrs, int defStyleAttr) {
        initAttr(context, attrs, defStyleAttr);
        mPaint = new Paint();
        mPaint.setColor(mColor);
        //使用Path必须使用STROKE，使用FILL是画不了的
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(mArrowStrokeWidth);
        //设置拐角形状为圆形，3条线相接处则不会有缝隙了
        mPaint.setStrokeJoin(Paint.Join.ROUND);
    }

    private void initAttr(Context context,  AttributeSet attrs, int defStyleAttr) {
     //   TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.BackArrowView, defStyleAttr, 0);
        mColor = /*array.getColor(R.styleable.BackArrowView_bav_color, */Color.argb(255, 0, 0, 0)/*)*/;
        mArrowStrokeWidth =/* array.getDimension(R.styleable.BackArrowView_bav_stroke_width,*/ dip2px(context, 2f)/*)*/;
        mArrowStyle =/* array.getInt(R.styleable.BackArrowView_bav_arrow_style, */ARROW_STYLE_MATERIAL_DESIGN/*)*/;
      //  array.recycle();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mViewWidth = w;
        mViewHeight = h;
        //计算半径
        float radius = Math.min(mViewWidth, mViewHeight) / 2f;
        //计算箭头起始位置
        if (mArrowStyle == ARROW_STYLE_MATERIAL_DESIGN) {
            mArrowStartDistance = radius / 3f;
        } else if (mArrowStyle == ARROW_STYLE_WECHAT_DESIGN) {
            mArrowStartDistance = radius / 4f;
        }
        //计算箭头长度
        mArrowLineLength = radius * 0.63f;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //将画布中心移动到中心点偏左位置
        canvas.translate((mViewWidth / 2f) - mArrowStartDistance, mViewHeight / 2);
        //将画布旋转45度，让后面画的直角旋转
        canvas.rotate(45);
        if (mArrowPath == null) {
            mArrowPath = new Path();
        }
        mArrowPath.reset();
        //画第一条线
        mArrowPath.lineTo(0, -mArrowLineLength);
        //画第二条线
        mArrowPath.moveTo(0, 0);
        mArrowPath.lineTo(mArrowLineLength, 0);
        //Google Material Design风格才有中间的线
        if (mArrowStyle == ARROW_STYLE_MATERIAL_DESIGN) {
            //画中间的线
            mArrowPath.moveTo(0, 0);
            mArrowPath.lineTo(mArrowLineLength, -mArrowLineLength);
        }
        //闭合路径
        mArrowPath.close();
        //画路径
        canvas.drawPath(mArrowPath, mPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(handleMeasure(widthMeasureSpec), handleMeasure(heightMeasureSpec));
    }

    /**
     * 处理MeasureSpec
     */
    private int handleMeasure(int measureSpec) {
        int result = DEFAULT_MIN_WIDTH;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            //处理wrap_content的情况
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }
}
