package com.lgq.slideruler.control;

import com.lgq.slideruler.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SliderRuler extends RelativeLayout{
	
	private Context mContext;
	private HorizontalScrollView mScrollView; 
	private int totalCount;
	private Drawable bigUnit;
	private Drawable smallUnit;
	private Drawable mask;
	private Drawable background;
	private float unitSize;
	private int textSize;
	private float betweenCount;
	private int defaultValue;
	
	private LinearLayout container;
	private RelativeLayout root;
	private SliderChangedListener mSliderChangedListener;
	
	public SliderRuler(Context context) {
		super(context);		
		init(context, 30, 
				context.getResources().getDrawable(R.drawable.bigunit), 
				context.getResources().getDrawable(R.drawable.smallunit), 
				context.getResources().getDrawable(R.drawable.mask), 
				null, 0, 30, 2);
	}

	public SliderRuler(Context context, AttributeSet attrs) {
		super(context, attrs);		
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SliderRuler);
        
        int totalCount = a.getInt(R.styleable.SliderRuler_total_count, 30);
        Drawable bigUnit = a.getDrawable(R.styleable.SliderRuler_big_unit);
        Drawable smallUnit = a.getDrawable(R.styleable.SliderRuler_small_unit);
        Drawable mask = a.getDrawable(R.styleable.SliderRuler_mask);
        Drawable background = a.getDrawable(R.styleable.SliderRuler_background);
        float unitSize = a.getDimension(R.styleable.SliderRuler_unit_image_width, 0);  
        int textSize = a.getInt(R.styleable.SliderRuler_text_size, 30);
        int betweenCount = a.getInt(R.styleable.SliderRuler_between_count, 2);
        this.defaultValue = a.getInt(R.styleable.SliderRuler_default_value, (int)(totalCount/2));
        
        init(context, totalCount, bigUnit, smallUnit, mask, background, unitSize, textSize, betweenCount);
        
        a.recycle();
	}
	
	private void init(Context context, final int totalCount, Drawable bigUnit, 
			Drawable smallUnit, Drawable mask, Drawable background, float unitSize, int textSize, int betweenCount){
		mContext=context;
		this.totalCount = totalCount;		
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);		
        
        root = new RelativeLayout(context);
        RelativeLayout.LayoutParams rootParam = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        root.setLayoutParams(rootParam);
        addView(root);
        
        mScrollView = new HorizontalScrollView(context);
        mScrollView.setVerticalScrollBarEnabled(false);
        mScrollView.setHorizontalScrollBarEnabled(false);
        root.addView(mScrollView);
        
        container = new LinearLayout(context);
        LinearLayout.LayoutParams containerParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        containerParam.gravity = Gravity.CENTER;
        container.setOrientation(LinearLayout.HORIZONTAL);
        container.setLayoutParams(containerParam);
        
        mScrollView.addView(container);
        
        for(int i = 0; i < totalCount; i++)
        {
        	TextView text = new TextView(context);
        	text.setTextSize(textSize);        	
        	text.setGravity(Gravity.CENTER);
        	
        	LinearLayout.LayoutParams txtParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
        	
        	txtParams.setMargins(0, 0, 0, 0);
        	text.setPadding(0, 0, 0, 0);
        	if(unitSize != 0)
        	text.setWidth(dp2px((int)unitSize));
        	text.setLayoutParams(txtParams);        	
        	
        	Drawable drawable = null;
        	if(i % betweenCount == 0){
        		drawable = getResources().getDrawable(R.drawable.bigunit);
        		text.setText(String.valueOf(i));
        	}
        	else{
        		drawable = getResources().getDrawable(R.drawable.smallunit);
        		text.setText("");
        	}
        	drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        	text.setCompoundDrawables(null, drawable, null, null);
            container.addView(text);
        }
                
        ImageView maskPic = new ImageView(context);
        maskPic.setImageDrawable(mask);
        
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        maskPic.setLayoutParams(params);
        
        mScrollView.setOnTouchListener(new TouchListenerImpl());
        
        mScrollView.post(new Runnable(){

			@Override
			public void run() {
				
				final float totalLength = (float)mScrollView.getChildAt(0).getWidth();
				final float unitWidth = totalLength / totalCount; 
				final float halfWidth = (float)mScrollView.getWidth()/2;
				
				mScrollView.smoothScrollTo((int) (defaultValue * unitWidth - halfWidth + 0.5 * unitWidth), (int)mScrollView.getScrollY());

				if(mSliderChangedListener != null)
					mSliderChangedListener.OnChanged(defaultValue);
			}		                	
        });
                
        root.addView(maskPic);        
	}
	
	private class TouchListenerImpl implements View.OnTouchListener{
        @Override
        public boolean onTouch(View v, MotionEvent event) {
        	
        	switch (event.getAction()) {  
            	case MotionEvent.ACTION_UP:
            		
            		final float totalLength = (float)mScrollView.getChildAt(0).getWidth();
            		final float unitWidth = totalLength / totalCount; 
            		final float halfWidth = (float)mScrollView.getWidth()/2;
        	
	                Log.e("tag", "mScrollView.getScrollX() = " + mScrollView.getScrollX());		                
	                
	                float value = 0;
	                
	                int leftEmpty = (int)(halfWidth/unitWidth);
	                int rightEmpty = totalCount - leftEmpty - 1;

	                	value = ( halfWidth + mScrollView.getScrollX()) / totalLength * totalCount;
		                
		                if((value - (int)value) > 0.5)
		                	value = (int)(value + 1);
		                else
		                	value = (int)(value);
		                		                
		                if((int)value < leftEmpty || (int)value  > rightEmpty){
		                	final int val = (int)value  > rightEmpty ? rightEmpty : leftEmpty;			                
			                
			                v.post(new Runnable(){

								@Override
								public void run() {
									mScrollView.smoothScrollTo((int) (val * unitWidth - halfWidth + 0.5 * unitWidth), (int)mScrollView.getScrollY());

									if(mSliderChangedListener != null)
										mSliderChangedListener.OnChanged(val);
								}		                	
			                });
		                }
		                else{
		                	final int val = (int)value;			                
			                
			                v.post(new Runnable(){

								@Override
								public void run() {
									mScrollView.smoothScrollTo((int) (val * unitWidth - halfWidth + 0.5 * unitWidth), (int)mScrollView.getScrollY());

									if(mSliderChangedListener != null)
										mSliderChangedListener.OnChanged(val);
								}		                	
			                });
		                }

	                
	                break;  
                default:  
                    break; 
        	}
        	
        	return false; 
        }   
    };
	
	// 回调接口
	public interface SliderChangedListener {
		public void OnChanged(float current);
	}

	public void setSliderChangedListener(SliderChangedListener switchChangedListener) {
        this.mSliderChangedListener=switchChangedListener;
	}
	
	public int dp2px(int dp)
    {
    	float scale = getResources().getDisplayMetrics().density;
    	return (int) (dp * scale + 0.5f);
    }

    public int px2dp(int px)
    {
    	float scale = getResources().getDisplayMetrics().density;
    	return (int) (px / scale + 0.5f);
    }

	public void reset(Drawable big, Drawable small, float unitSize, int textSize, int between, int total, int defaultValue){
		mScrollView.removeAllViews();
		this.defaultValue = defaultValue;
		init(mContext, total, big, small, mask, (Drawable)null, unitSize, textSize, between);
		mScrollView.forceLayout();
	}
}
