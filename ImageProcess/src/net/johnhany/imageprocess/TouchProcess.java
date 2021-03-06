package net.johnhany.imageprocess;

import java.util.ArrayList;
import java.util.List;

import org.opencv.android.BaseLoaderCallback;  
import org.opencv.android.LoaderCallbackInterface;  
import org.opencv.android.OpenCVLoader; 

import android.os.Bundle;
import android.app.Activity;  
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory; 
import android.graphics.Bitmap.Config;        
import android.graphics.drawable.BitmapDrawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;  
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class TouchProcess extends Activity{  
	
	private Spinner spinner1, spinner2;
    private ImageView imageView;
    private Bitmap bmp;
    
    private String GRAY_PROCESS = "Gray process";
    
    private String PIC_1 = "Rock No.1";
    private String PIC_2 = "Rock No.2";
    
    private int methodSelected = 0;
    private int picSelected = 1;
    //private int picLeft, picRight, picTop, picBottom;
    private int[] viewCoords = new int[2];

    private BaseLoaderCallback  mLoaderCallback = new BaseLoaderCallback(this) {  
        @Override  
        public void onManagerConnected(int status) {  
            switch (status) {  
                case LoaderCallbackInterface.SUCCESS:{  
                    System.loadLibrary("opencv_jni");  
                } break;  
                default:{  
                    super.onManagerConnected(status);  
                } break;  
            }  
        }  
    };  
      
    @Override  
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_touch_process);
        
    	imageView = (ImageView) findViewById(R.id.image_view2);
        
        Toast.makeText(TouchProcess.this,"Please select method and picture.\nAnd then touch the picture to apply the process.",Toast.LENGTH_SHORT).show();
        
        initSpinner();
    	addListenerOnSpinner();
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
    	
        float x = event.getX();   
        float y = event.getY();
        
        try {   
        	switch (event.getAction()) {  
        		case MotionEvent.ACTION_DOWN:  
        		case MotionEvent.ACTION_MOVE:
        			/*
        			if(x>picLeft && x<picRight && y>picTop && y<picBottom)
        				touchProc(x-picLeft, y-picTop);
        			*/
        			touchProc(x-viewCoords[0], y-viewCoords[1]);
        			break;   
                case MotionEvent.ACTION_UP:   
                	break;
                default:
                	break;
        	}   
        }catch(Exception e) {  
            e.printStackTrace();   
        }   
        return true;   
	}   
     
    public void touchProc(float tx, float ty) {
    	int w = bmp.getWidth();
        int h = bmp.getHeight();
        int[] pixels = new int[w*h];
        
        Bitmap srcImg = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
        srcImg.getPixels(pixels, 0, w, 0, 0, w, h);
        Bitmap resultImg = Bitmap.createBitmap(w, h, Config.ARGB_8888);
        
        switch(methodSelected) {
        	case 1:
        		resultImg.setPixels(OpencvJni.touchGray(pixels, w, h, tx, ty), 0, w, 0, 0, w, h);
        		break;
        	default:
        		break;
        }
        imageView.setImageBitmap(resultImg);
    }
    
	public void initSpinner() {
		spinner1 = (Spinner) findViewById(R.id.spinner21);
		List<String> list1 = new ArrayList<String>();
    	list1.add(GRAY_PROCESS);
    	
    	ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list1);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(adapter1);
        spinner1.setPrompt("Choose a method");

        
        spinner2 = (Spinner) findViewById(R.id.spinner22);
        List<String> list2 = new ArrayList<String>();
        list2.add(PIC_1);
        list2.add(PIC_2);
        
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list2);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(adapter2);
        spinner2.setPrompt("Choose a picture");
	}
	
	public void addListenerOnSpinner() {

		spinner1.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view, int pos,long id) {
				Object method = parent.getItemAtPosition(pos);
				
				if(method.toString().equals(GRAY_PROCESS)) {
					methodSelected = 1;
				}
				
				loadPicture();
			}
			
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				
			}
		});
		
		spinner2.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view, int pos,long id) {
				Object pic = parent.getItemAtPosition(pos);
				
				if(pic.toString().equals(PIC_1)) {
					picSelected = 1;
				}
				else if(pic.toString().equals(PIC_2)) {
					picSelected = 2;
				}
				
				loadPicture();
			}
				 
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				
			}
		});
	}
	
	
	
	public void loadPicture() {
		int picResource;
		
		if(picSelected == 1)
			picResource = R.drawable.testpic1;
		else
			picResource = R.drawable.testpic2;
		
		bmp = BitmapFactory.decodeResource(getResources(), picResource);
        imageView.setImageBitmap(bmp);
        
        /*
        picLeft = imageView.getLeft();
        picRight = imageView.getRight();
        picTop = imageView.getTop();
        picBottom = imageView.getBottom();
        */
        imageView.getLocationOnScreen(viewCoords);
	}
	
	@Override  
    public void onResume() {  
        super.onResume();  
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_8, this, mLoaderCallback);  
    }
}
