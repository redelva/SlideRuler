package com.lgq.slideruler;

import com.lgq.slideruler.control.SliderRuler;
import com.lgq.slideruler.control.SliderRuler.SliderChangedListener;

import android.os.Bundle;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends Activity {

	LinearLayout layout;
	RelativeLayout root;
	HorizontalScrollView scroll;
	TextView indicator;
	SliderRuler ruler;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        ruler = (SliderRuler) findViewById(R.id.ruler);
        indicator = (TextView) findViewById(R.id.indicator);
        
        ruler.setSliderChangedListener(new SliderChangedListener(){

			@Override
			public void OnChanged(float current) {
				indicator.setText(String.valueOf(current));
			}        	
        });
        
        final Button swh = (Button)findViewById(R.id.btnSwitch);
        
        swh.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				
				if(swh.getText().equals("Switch tiny")){
					swh.setText("Swtich Big");
					Drawable big = getResources().getDrawable(R.drawable.bigtinyunit);
					Drawable small = getResources().getDrawable(R.drawable.smalltinyunit);
					Drawable mask = getResources().getDrawable(R.drawable.mask);
					ruler.reset(big, small, 20, 14, 10, 100, 50);
				}else{
					swh.setText("Switch tiny");
					Drawable big = getResources().getDrawable(R.drawable.bigunit);
					Drawable small = getResources().getDrawable(R.drawable.smallunit);
					Drawable mask = getResources().getDrawable(R.drawable.mask);
					ruler.reset(big, small, 0, 30, 2, 30, 10);
				}
			}        	
        });
    }
    
    

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
}
