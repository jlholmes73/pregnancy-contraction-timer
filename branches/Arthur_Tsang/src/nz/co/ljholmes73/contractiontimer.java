package nz.co.ljholmes73;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class contractiontimer extends Activity {
	   /** Called when the activity is first created. */
	private boolean startStop=false;
	private boolean startStop2=false;
	private String emailText ="";
	private boolean flag =true;
	private boolean watersFlag=true;
	private boolean paused=false;
	private boolean undoable=false;
	private boolean lastWaters=false;
	private String av="00:00";
	//c is break & b is contraction!!!
	private int c1,c2,c3,c4,c5,c6,b1,b2,b3,b4,b5,b6,conCount =0;
	public static final String PREF_FILE_NAME = "PrefFile";
	private static final int PREF_EDIT=0;
	private boolean timer2StatusChanged = false;
	private long prevStoredTime2 = 0L;
	
	MediaPlayer mMediaPlayer = null;
	 Uri alert = null; 
	 AudioManager audioManager = null;
	 
    private PregDbAdapter mDbHelper;
        static int seq= 0;
        static int seq2= 0;
        
        private Handler mHandler = new Handler(); 
        private Runnable mUpdateTimerTask = new Runnable() { 
                public void run() { 
                        long millis = SystemClock.uptimeMillis(); 
                        sayTimerTest(); 
                        // prevent it from blocking the main thread 
                        int factor = 1; 
                        long delay = 1000*factor - (SystemClock.uptimeMillis() - millis); 
                        while (delay < 0) { 
                                factor++; 
                                delay = 1000*factor - (SystemClock.uptimeMillis() - millis); 
                        } //1000*factor for 1 second
                        mHandler.postDelayed(mUpdateTimerTask, delay); 
                } 
        }; 
        private Runnable mUpdateTimerTask2 = new Runnable() { 
            public void run() { 
                    long millis = SystemClock.uptimeMillis(); 
                    sayTimerTest2(); 
                    // prevent it from blocking the main thread 
                    int factor = 1; 
                    long delay = 1000*factor - (SystemClock.uptimeMillis() - millis); 
                    while (delay < 0) { 
                            factor++; 
                            delay = 1000*factor - (SystemClock.uptimeMillis() - millis); 
                    } //1000*factor for 1 second
                    mHandler.postDelayed(mUpdateTimerTask2, delay); 
            } 
    }; 
    
        public contractiontimer() {  } 
        @Override 
        protected void onResume() { 
        	
    		SharedPreferences preferences = getSharedPreferences(PREF_FILE_NAME, MODE_WORLD_WRITEABLE);
    		long startt = preferences.getLong("storedTime", 0);
    		long startt2 = preferences.getLong("storedTime2", 0);
    		watersFlag = preferences.getBoolean("watersState", true);
    		startStop = preferences.getBoolean("storedState1", false);
    		startStop2 = preferences.getBoolean("storedState2", false);
    		conCount=preferences.getInt("conCountS",0);
    		c1=preferences.getInt("C1",0);
    		c2=preferences.getInt("C2",0);
    		c3=preferences.getInt("C3",0);
    		c4=preferences.getInt("C4",0);
    		c5=preferences.getInt("C5",0);
    		c6=preferences.getInt("C6",0);
    		b1=preferences.getInt("B1",0);
    		b2=preferences.getInt("B2",0);
    		b3=preferences.getInt("B3",0);
    		b4=preferences.getInt("B4",0);
    		b5=preferences.getInt("B5",0);
    		b6=preferences.getInt("B6",0);
    		av=preferences.getString("st2st", "00:00");
    		undoable=preferences.getBoolean("undoble", false);
    		paused = preferences.getBoolean("pause", false);
    		lastWaters=preferences.getBoolean("watersLst", false);
    		seq=(int) ((System.currentTimeMillis()-startt)/1000);// 1000 for 1 sec
    		seq2= (startStop2) ? (int) ((System.currentTimeMillis()-startt2)/1000) : -1; //sayTimerTest2 will add one to it
    		TextView AvText = (TextView)findViewById(R.id.AverageOut);
    		//av=tfor((c1+b1+c2+b2+c3+b3+c4+b4+c5+b5)/5);
    		
    		AvText.setText(av);
    		
    		//watersFlag=watersB;
    		if(startt>0){
    			
    			EditText ext = (EditText) findViewById(R.id.timeDisplay);
    	    	TableLayout tl = (TableLayout)findViewById(R.id.ActivityList2);
    	       	tl.removeAllViews();
    	    	rebuildList();
    	    	flag=false;
    	    	if (startStop){
    	    		final Button button = (Button) findViewById(R.id.PregButton);
    	    		button.setText("End Contraction");
            		button.setTextColor(getResources().getColor(R.color.darkgreen));
            		ext.setTextColor(Color.RED); 
    	    	}else{
    	    		final Button button = (Button) findViewById(R.id.PregButton);
    	    		button.setText("Start Contraction");
            		button.setTextColor(Color.RED);
            		ext.setTextColor(getResources().getColor(R.color.darkgreen));
    	    	}
    	    	
    	    	if (conCount>9) {
    	    		
    	    		
    	    		restoreAve();
    	    	}
    	 
    		}
    		
    		//seq=(int) ((System.currentTimeMillis()-startt)/1000);// 1000 for 1 sec
    		if(paused){
    			EditText TimerTestText = (EditText)findViewById(R.id.timeDisplay);
    		   	TimerTestText.setText("00:00");
    		   	TimerTestText.setTextColor(Color.BLACK);
    		   	EditText TimerTestText2 = (EditText)findViewById(R.id.timeDisplay2);
    		   	TimerTestText2.setText("00:00");
    		   	TimerTestText2.setTextColor(Color.BLACK);
    		   	final Button button = (Button) findViewById(R.id.PregButton);
            	final Button wbutton = (Button) findViewById(R.id.WatersButton);
            	wbutton.setEnabled(false);
            	button.setEnabled(false);
    			
    		}
    		sayTimerTest(); 
    		sayTimerTest2(); 
            mHandler.removeCallbacks(mUpdateTimerTask); 
            mHandler.removeCallbacks(mUpdateTimerTask2); 
            mHandler.postDelayed(mUpdateTimerTask, 1000); 
            if( startStop2 )
            	mHandler.postDelayed(mUpdateTimerTask2, 1000); 
            super.onResume(); 
    } 
        @Override 
        protected void onPause() { 
        		
        		mHandler.removeCallbacks(mUpdateTimerTask); 
        		mHandler.removeCallbacks(mUpdateTimerTask2); 
        		if( mMediaPlayer != null ) {
        			mMediaPlayer.release();
        			mMediaPlayer = null;
        		}
                super.onPause(); 
        } 
       
        
        private void sayTimerTest() { 
        		++seq;
        		if( mMediaPlayer != null && !mMediaPlayer.isPlaying() ) {
        			//mMediaPlayer.stop();
        			mMediaPlayer.release();
        			mMediaPlayer = null;
        		}
        		
        		if (flag ){seq=0;}
        		if(paused){seq=0;}
                EditText TimerTestText = (EditText)findViewById(R.id.timeDisplay); 
                TimerTestText.setText(tfor(seq));
                TimerTestText.invalidate(); 	
                
                	if(seq>21600)//21600
                	{
                		seq =0;
                		paused =true;
            		   	TimerTestText.getText().clear();
            		   	TimerTestText.setTextColor(Color.TRANSPARENT);
            		   	SharedPreferences preferences = getSharedPreferences(PREF_FILE_NAME, MODE_WORLD_WRITEABLE);
                    	SharedPreferences.Editor editor = preferences.edit();
                    	editor.putBoolean("pause", paused);
                    	editor.commit();
            		   	
                		onPausedLongTime();
                	}
                	//TimerTestText.post(mUpdateTimerTask);
                
        } 
        private void sayTimerTest2() { 
    		++seq2;
    		if (flag ){seq2=0;}
    		if(paused){seq2=0;}
            EditText TimerTestText = (EditText)findViewById(R.id.timeDisplay2); 
            TimerTestText.setText(tfor(seq2));
            TimerTestText.invalidate(); 	
            /*  no need for having 2 places to check for long pause
            	if(seq2>21600)//21600
            	{
            		seq2 =0;
            		paused =true;
        		   	TimerTestText.getText().clear();
        		   	TimerTestText.setTextColor(Color.TRANSPARENT);
        		   	SharedPreferences preferences = getSharedPreferences(PREF_FILE_NAME, MODE_WORLD_WRITEABLE);
                	SharedPreferences.Editor editor = preferences.edit();
                	editor.putBoolean("pause", paused);
                	editor.commit();
        		   	
            		onPausedLongTime();
            	}*/
            	//TimerTestText.post(mUpdateTimerTask);
            
    } 
        
    private void beep() {
        try {
        	if( mMediaPlayer == null ) {
        		mMediaPlayer = new MediaPlayer();
        		alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION); 
           	  	audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
           	  	mMediaPlayer.setDataSource(this, alert);
           	  	mMediaPlayer.setAudioStreamType(AudioManager.STREAM_NOTIFICATION);
	    		mMediaPlayer.setLooping(false);
	    		mMediaPlayer.prepare();
        	}
    	 if (audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION) != 0) {
    		 mMediaPlayer.seekTo(0);
    		 mMediaPlayer.start();
    	  }
    	}catch(Exception e) { 
    	}
    	
    }
	
    long lastClick = 0L;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        super.onPause();
        
        mDbHelper = new PregDbAdapter(this);
        mDbHelper.open();
    	
        final Button button = (Button) findViewById(R.id.PregButton);
        button.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // Perform action on clicks
            	long now = System.currentTimeMillis();
            	long diff = now - lastClick;
            	lastClick = now;
            	if( diff < 3000 ) return;
            	 /* do nothing if button pressed too close together
            							  use undo for wrong click, but not start/stop it right away
            							  as that would mess up with the stats */
            	beep();
            	if(startStop){
            		startStop=false;
            		button.setText("Start Contraction");
            		button.setTextColor(Color.RED);
            		((EditText)findViewById(R.id.timeDisplay)).setTextColor(getResources().getColor(R.color.darkgreen)); 
            		//EditText TimerTestText = (EditText)findViewById(R.id.timeDisplay);
            		//timer=TimerTestText.getText().toString();
            		flag = false;
                	
            		b6=b5;
                	b5=b4;
                	b4=b3;
                	b3=b2;
                	b2=b1;
                	b1= seq;
                	undoable=true;
                	SharedPreferences preferences = getSharedPreferences(PREF_FILE_NAME, MODE_WORLD_WRITEABLE);
            		SharedPreferences.Editor editor1 = preferences.edit();
            		editor1.putInt("B1", b1);
            		editor1.putInt("B2", b2);
            		editor1.putInt("B3", b3);
            		editor1.putInt("B4", b4);
            		editor1.putInt("B5", b5);
            		editor1.putInt("B6", b6);
            		editor1.putBoolean("undoble", true);
            		editor1.commit();
            		
            		//Toast.makeText(contractiontimer.this, "There is no Contraction list to email.", Toast.LENGTH_SHORT).show();
                	
            		
                	if (conCount>10)//c5 but for testing c1
                	{
                		TextView AvConText = (TextView)findViewById(R.id.AverageOut1);
                		int ave =((b1+b2+b3+b4+b5)/5);
                		
                		AvConText.setText(tfor(ave));
                		
                		//Toast.makeText(contractiontimer.this, "There is no Contraction list to email.", Toast.LENGTH_SHORT).show();
                    	
                		
                		//int ave=c1;//for testing
                	}
                	
                	
            	}
            	else
            	{
            		startStop = true;
            		button.setText("End Contraction");	
            		
            		button.setTextColor(getResources().getColor(R.color.darkgreen));
            		((EditText)findViewById(R.id.timeDisplay)).setTextColor(Color.RED); 
            		
            		//Toast.makeText(contractiontimer.this, ""+(getResources().getColor(R.color.darkgreen)), Toast.LENGTH_LONG).show();
                	
            		
            		//button.setTextColor(R.color.translucent_red);
            		//EditText TimerTestText = (EditText)findViewById(R.id.timeDisplay);
            		//timer=TimerTestText.getText().toString();
            		flag = false;
                	
            		c6=c5;
                	c5=c4;
                	c4=c3;
                	c3=c2;
                	c2=c1;
                	c1= seq;
                	undoable=true;
                	SharedPreferences preferences = getSharedPreferences(PREF_FILE_NAME, MODE_WORLD_WRITEABLE);
            		SharedPreferences.Editor editor1 = preferences.edit();
            		editor1.putInt("C1", c1);
            		editor1.putInt("C2", c2);
            		editor1.putInt("C3", c3);
            		editor1.putInt("C4", c4);
            		editor1.putInt("C5", c5);
            		editor1.putInt("C6", c6);
            		editor1.putBoolean("undoble", true);
            		editor1.commit();
            		
            		if (conCount>10)
                	{
                		TextView AvBrkText = (TextView)findViewById(R.id.AverageOut2);
                		
                		
                		int ave=((c1+c2+c3+c4+c5)/5);//for real
                		AvBrkText.setText(tfor(ave));
                		//Toast.makeText(contractiontimer.this, "There is no Contraction list to email.", Toast.LENGTH_SHORT).show();
                		TextView AvText = (TextView)findViewById(R.id.AverageOut);
                		av=tfor((c1+b1+c2+b2+c3+b3+c4+b4+c5+b5)/5);
                		
                		AvText.setText(av);
                	}
                	
            	}
            	
            	timer2StatusChanged = false;
            	if( startStop ) {
	            	if( startStop2 ) {
	            		if( c1 > 600 ||
	            			( ( b1 < 60 || (b1+c1) > 300 ) &&
            				( b2 < 60 || (b2+c2) > 300 ) &&
            				( b3 < 60 || (b3+c3) > 300 ) &&
            				( b4 < 60 || (b4+c4) > 300 ) &&
            				( b5 < 60 || (b5+c5) > 300 )
	            			) ) {
	            			startStop2 = false;
	            			seq2 = 0;
	            			timer2StatusChanged = true;
	            			mHandler.removeCallbacks(mUpdateTimerTask2);
	            			display2();
	            		}
	            	} else {
	            		if( b1 > 60 && (b1+c1) < 300 ) {
	            			startStop2 = true;
	            			timer2StatusChanged = true;
	            			mHandler.postDelayed(mUpdateTimerTask2, 1000);
	            			display2();
	            		}
	            	}
            	}
                
            	conCount++;
            	SharedPreferences preferences = getSharedPreferences(PREF_FILE_NAME, MODE_WORLD_WRITEABLE);
            	SharedPreferences.Editor editor = preferences.edit();
            	editor.putInt("conCountS", conCount);
            	editor.putBoolean("watersLst", false);
            	editor.putString("st2st", av);
            	editor.commit();
            	lastWaters=false;
            	appendRow();
                display();
                
            	
            }
        });
      
        final Button button2 = (Button) findViewById(R.id.WatersButton);
        button2.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	if (watersFlag){
            	
            	watersRow();
            	watersFlag=false;
            	
            	}
        
            }
        });
      // sanity check more than 2000 contractions in a labour is ridiculous
        if(conCount==2000){ 
        	reset();
        }
        
    }
    
    private void createPreg(int colouring, String text) {
       
        double timeL =  (System.currentTimeMillis());	
        mDbHelper.createPreg(colouring,  timeL,  text);        
     
    }
    
    
    private void appendRow() {
    	
    	final TableLayout tl = (TableLayout)findViewById(R.id.ActivityList2);
    	final EditText TimerTestText = (EditText)findViewById(R.id.timeDisplay);
		String timer=TimerTestText.getText().toString();
    	String test = "";
    	TableRow row = new TableRow(this);

        TextView label = new TextView(this);
        label.setText(test);
        long millis = System.currentTimeMillis();
        
        if (startStop){
        	if( b1+b2+b3+b4+b5+b6+c1+c2+c3+c4+c5+c6 == 0 ) {
        		test = "Contraction started @ "+(DateFormat.format("h:mm:ssaa", millis));
        		row.setBackgroundColor(Color.DKGRAY);
        		createPreg(0xff444444,test);
        	} else {
        		test= "Break ended @ " +(DateFormat.format("h:mm:ssaa", millis)) + "\n duration was "+ timer;
        		row.setBackgroundColor(getResources().getColor(R.color.darkgreen));
        		createPreg(-14513374,test);
        	}
        }else {
        	row.setBackgroundColor(Color.RED);
        	test= "Contraction ended @ " +(DateFormat.format("h:mm:ssaa", millis)) + "\n duration was "+ timer;
        	createPreg(-65536,test);
        }
        
        label.setTextColor(Color.WHITE);
        label.setGravity(Gravity.CENTER);
        label.setText(test);
        
        row.addView(label, new TableRow.LayoutParams(1));
        //row.addView(shortcut, new TableRow.LayoutParams());
        tl.setGravity(Gravity.CENTER);
        
        
        tl.addView(row, new TableLayout.LayoutParams());
        
        final ScrollView sv = (ScrollView)findViewById(R.id.sv);
        sv.post(new Runnable() {
            public void run() {
            	
                sv.scrollTo(0, tl.getHeight());
               
            }
        });
               
        
    }
    
    private void watersRow() {
    	final TableLayout tl = (TableLayout)findViewById(R.id.ActivityList2);
    	String wtext = "";
    	TableRow row = new TableRow(this);

        TextView label = new TextView(this);
        
       
        	long millis = System.currentTimeMillis();
        	wtext= "Waters broke @\n" +(DateFormat.format("E, MMMM dd, yyyy h:mm:ssaa", millis)) ;
        	row.setBackgroundColor(Color.BLUE);
        
        label.setTextColor(Color.WHITE);
        label.setGravity(Gravity.CENTER);
        label.setText(wtext);
        createPreg(-16776961,wtext);
        row.addView(label, new TableRow.LayoutParams(1));
        tl.setGravity(Gravity.CENTER);
        

        tl.addView(row, new TableLayout.LayoutParams());
        
        final ScrollView sv = (ScrollView)findViewById(R.id.sv);
        sv.post(new Runnable() {
            public void run() {
                sv.scrollTo(0, tl.getHeight());
            }
        });
        lastWaters=true;  
        undoable=true;
        SharedPreferences preferences = getSharedPreferences(PREF_FILE_NAME, MODE_WORLD_WRITEABLE);
    	
    	SharedPreferences.Editor editor = preferences.edit();
    	editor.putBoolean("watersState", false); // value to store
    	editor.putBoolean("watersLst", true);

		editor.putBoolean("undoble", true);
    	editor.commit(); 
    }
    


    
    
    
    public boolean onCreateOptionsMenu(Menu menu) 
    {
      super.onCreateOptionsMenu(menu);
      
      MenuItem items = menu.add(0,0,0,"About");
      items = menu.add(0,1,1,"Reset");
      
      items = menu.add(0,2,2,"Undo last action");      
      items = menu.add(0,3,3,"email Contraction list");
      
   // Return true so that the menu gets displayed.
      return true;
    }
    
    @Override public boolean onPrepareOptionsMenu(final Menu menu) {       
        if(undoable && (conCount>=2)) 
        {
        	
             menu.findItem(2).setIcon(R.drawable.undo1active); 
             
        }
        else 
             {
        	menu.findItem(2).setIcon(R.drawable.undo2inactive); 
             }
        menu.findItem(3).setIcon(R.drawable.e);
        menu.findItem(1).setIcon(R.drawable.x);
        menu.findItem(0).setIcon(R.drawable.i);
        
        return super.onPrepareOptionsMenu(menu); 
   }
    
    
    public boolean onOptionsItemSelected(MenuItem item)
    {

      if (item.hasSubMenu() == false)
      {
       
         if (item.getTitle()=="About") {
          AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
    
          dialogBuilder.setMessage("Contraction Timer V2.0.3\n\n(C) John Holmes 2009\n\nV2.0.3 Added contraction start to start Average\n-V2.0 added menu option to undo last action and pause to retain data after 6 hours+ of inactivity\n-V1.7 Changed contraction colour coding\n-V1.5 support for large and small screens\n-V1.0 back end changes so log is saved even if the phone is turned off.\n-V0.9 added the ability to email the list of contractions.\n\nThis app is dedicated to my son Arthur who arrived 2.5 weeks early.  Actually before I had a chance to write this, Doh!\n\n If you have any questions please contact me through the app market.  All the best with your labor and your new arrival  :-)" );
          dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int whichButton) {

                  /* User clicked OK so do some stuff */
              }
          });
          
          dialogBuilder.setCancelable(true);
          dialogBuilder.create().show();

      }
    
         
         if (item.getTitle()=="Undo last action"){
        	 //fillData();
        	 //rebuildList();
        	 if (undoable && (conCount>=2)){
        		 undoLast();
        	 }
        	 //onPausedLongTime();
        	//Toast.makeText(contractiontimer.this, "b1:" +b1+" b2:"+b2+" b3:"+b3+" b4:"+b4+" b5:"+b5+" b6:"+b6, Toast.LENGTH_LONG).show();
        	//Toast.makeText(contractiontimer.this, "c1:" +c1+" c2:"+c2+" c3:"+c3+" c4:"+c4+" c5:"+c5+" c6:"+c6, Toast.LENGTH_LONG).show();
        	//(DateFormat.format("E, MMMM dd, yyyy h:mm:ssaa", millis))
        	
        	
        	 
        	 
        	 
               }
         if (item.getTitle()=="Reset"){
        	 AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        	    
             dialogBuilder.setMessage("Are you sure you want to reset and permanently delete the contraction list?" );
             dialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                 public void onClick(DialogInterface dialog, int whichButton) {

                     /* User clicked OK so do some stuff */
                	 reset();
                 }
             });
             dialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                 public void onClick(DialogInterface dialog, int whichButton) {

                     /* User clicked OK so do some stuff */
                	 
                 }
             });
             dialogBuilder.setCancelable(true);
             dialogBuilder.create().show();
         	  
         	
        		
        	  }
         
         if (item.getTitle()=="email Contraction list"){
        	  
          	emailLog();
         	 
         	  }
         
      }
      
  
      
      return true;
    }
    

    
    public void display(){
    	
    	SharedPreferences preferences = getSharedPreferences(PREF_FILE_NAME, MODE_WORLD_WRITEABLE);
    	
    	SharedPreferences.Editor editor = preferences.edit();
    	editor.putLong("storedTime", System.currentTimeMillis()); // value to store
    	editor.putBoolean("storedState1", startStop);
    	editor.commit();
    	
    	seq=0;
    	EditText ext = (EditText) findViewById(R.id.timeDisplay);
    	ext.setText("00:00");
      	//ext.setTextColor(Color.BLACK);
      	
    }
    
    public void display2(){
    	
    	SharedPreferences preferences = getSharedPreferences(PREF_FILE_NAME, MODE_WORLD_WRITEABLE);
    	
    	SharedPreferences.Editor editor = preferences.edit();
    	long storedTime2 = ( startStop2 ) ? System.currentTimeMillis() : 0L;
    	prevStoredTime2 = storedTime2;
    	editor.putLong("storedTime2", storedTime2 ); // value to store
    	editor.putBoolean("storedState2", startStop2);
    	editor.commit();
    	
    	EditText ext = (EditText) findViewById(R.id.timeDisplay2);
    	ext.setText("00:00");
      	ext.setTextColor(Color.BLACK);
      	
    }

    public String tfor(int time){
    	String temp="";
        int minutes=time/60;
        int seconds=(time-(60*minutes));
        temp=String.format("%02d:%02d",minutes,seconds);
        
    	
    	return temp;
    	
    }
    
    private void reset(){

    	int db=	mDbHelper.deleteAllPreg();
    	mDbHelper = new PregDbAdapter(this);
    	mDbHelper.open();
    	watersFlag=true;
    	flag=true;
    	seq=0;
    	seq2=0;
    	mHandler.removeCallbacks(mUpdateTimerTask2);
    	startStop=false;
    	startStop2=false;
    	EditText TimerTestText = (EditText)findViewById(R.id.timeDisplay);
    	TimerTestText.setText("00:00");
    	TimerTestText.setTextColor(Color.TRANSPARENT);
    	EditText TimerTestText2 = (EditText)findViewById(R.id.timeDisplay2);
    	TimerTestText2.setText("00:00");
    	TimerTestText2.setTextColor(Color.TRANSPARENT);
    	TableLayout tl = (TableLayout)findViewById(R.id.ActivityList2);
    	tl.removeAllViews();
    	c1=0;
    	c2=0;
    	c3=0;
    	c4=0;
    	c5=0;
    	c6=0;
    	b1=0;
    	b2=0;
    	b3=0;
    	b4=0;
    	b5=0;
    	b6=0;
    	conCount=0;
    	paused=false;
    	TextView AvText = (TextView)findViewById(R.id.AverageOut);
		av="00:00";
		timer2StatusChanged = false;
		prevStoredTime2 = 0L;
		
		AvText.setText(av);
    	TextView AvConText = (TextView)findViewById(R.id.AverageOut1);
    	AvConText.setText("00:00");
    	TextView AvBrkText = (TextView)findViewById(R.id.AverageOut2);
    	AvBrkText.setText("00:00");
    	Button button = (Button) findViewById(R.id.PregButton);
    	Button wbutton = (Button) findViewById(R.id.WatersButton);
    	wbutton.setEnabled(true);
    	button.setEnabled(true);
    	button.setText("Start Contraction");
    	button.setTextColor(Color.RED);
    	undoable=false;
    	SharedPreferences preferences = getSharedPreferences(PREF_FILE_NAME, MODE_WORLD_WRITEABLE);
    	SharedPreferences.Editor editor = preferences.edit();
    	editor.putLong("storedTime", 0); // value to store
    	editor.putLong("storedTime2", 0);
    	editor.putBoolean("watersState", true); // value to store
    	editor.putInt("conCountS", 0); // value to store
    	editor.putBoolean("storedState1", false);
    	editor.putBoolean("storedState2", false);
    	editor.putBoolean("pause", false);
    	editor.putInt("C1", 0);
    	editor.putInt("C2", 0);
    	editor.putInt("C3", 0);
    	editor.putInt("C4", 0);
    	editor.putInt("C5", 0);
    	editor.putInt("C6", 0);
    	editor.putInt("B1", 0);
    	editor.putInt("B2", 0);
    	editor.putInt("B3", 0);
    	editor.putInt("B4", 0);
    	editor.putInt("B5", 0);
    	editor.putInt("B6", 0);
    	editor.putString("st2st", "00:00");
    	editor.putBoolean("undoble", false);
    	editor.commit();

    }
    
    private void emailLog(){
    	TableLayout tl = (TableLayout)findViewById(R.id.ActivityList2);
    	int i =tl.getChildCount();
    	Cursor c = mDbHelper.fetchAllPreg();
    	int rowC = c.getCount();
    	if (rowC==0){
    		
    		Toast.makeText(contractiontimer.this, "There is no Contraction list to email.", Toast.LENGTH_SHORT).show();
        	
    	}
    	else{
    		
    		
    		emailText="";
    	
        	 
    		for(int k=1; k<(rowC+1);k++){
            Cursor d=mDbHelper.fetchPreg(k);
            startManagingCursor(d);
            int Colour = (d.getInt(d.getColumnIndexOrThrow(PregDbAdapter.KEY_TITLE)));
            String text =(d.getString(d.getColumnIndexOrThrow(PregDbAdapter.KEY_BODY)));
            String time =(d.getString(d.getColumnIndexOrThrow(PregDbAdapter.KEY_TIME)));
            
            if(!(Colour==3)){
            String tempString = emailText+"\n\n"+ text;
			emailText = tempString;
            }
            
            
        	}
    		
    		
    		
    		emailList(emailText);
    	}
    }
    
    
    private void emailList(String text){
    	
    	Intent i = new Intent(Intent.ACTION_SEND);  
    	//i.setType("text/plain"); //use this line for testing in the emulator  
    	i.setType("message/rfc822") ; // use for live device
    	i.putExtra(Intent.EXTRA_EMAIL, new String[]{""});  
    	
    	String subject = "Contraction List";
    	String tempText = "This message was automatically generated by the Contraction Timer on my Google Android Device.\n\nBelow is a log of contractions:"+text;
    	
    	
    	i.putExtra(Intent.EXTRA_SUBJECT,subject);  
    	i.putExtra(Intent.EXTRA_TEXT,tempText);  
    	startActivity(Intent.createChooser(i, "Select email application."));
    }
   
    private void rebuildList(){
    	final TableLayout tl = (TableLayout)findViewById(R.id.ActivityList2);
    	
    	Cursor c = mDbHelper.fetchAllPreg();
    	int rowC = c.getCount();
    	if (rowC==0){
    		
    		Toast.makeText(contractiontimer.this, "There is no Contraction list to email.", Toast.LENGTH_SHORT).show();
        	
    	}
    	else{
    		
        	 
            //Toast.makeText(contractiontimer.this, "there are this number of rows: "+ rowC, Toast.LENGTH_SHORT).show();
        	for(int k=1; k<(rowC+1);k++){
            Cursor d=mDbHelper.fetchPreg(k);
            startManagingCursor(d);
            int Colour = (d.getInt(d.getColumnIndexOrThrow(PregDbAdapter.KEY_TITLE)));
            String text =(d.getString(d.getColumnIndexOrThrow(PregDbAdapter.KEY_BODY)));
            Double time =(d.getDouble(d.getColumnIndexOrThrow(PregDbAdapter.KEY_TIME)));
            
            
            if(!(Colour==3)){
        	String test = "";
        	TableRow row = new TableRow(this);

            
            //int tcol=Integer.parseInt(Colour);
           
            if (Colour==-16776961){	
            	test= "" +text ;
            	row.setBackgroundColor(Colour);}
            else{
            	test= "" +text ;
            	row.setBackgroundColor(Colour);	
            		
            		
            	}           
            	
            	
        	TextView label = new TextView(this); 	
            label.setTextColor(Color.WHITE);
            label.setGravity(Gravity.CENTER);
            label.setText(test);
            
            row.addView(label, new TableRow.LayoutParams(1));
            tl.setGravity(Gravity.CENTER);
            

            tl.addView(row, new TableLayout.LayoutParams());
        	}
        	}
        	final ScrollView sv = (ScrollView)findViewById(R.id.sv);
            sv.post(new Runnable() {
                public void run() {
                    sv.scrollTo(0, tl.getHeight());
                }
            });
    		
    		
    		
    	}
    	
    	
    	
    }
    
    private void restoreAve(){
    	
    	TextView AvConText = (TextView)findViewById(R.id.AverageOut1);
		int ave =((b1+b2+b3+b4+b5)/5);
		
		AvConText.setText(tfor(ave));
		TextView AvBrkText = (TextView)findViewById(R.id.AverageOut2);
		
		
		int ave2=((c1+c2+c3+c4+c5)/5);//for real
		AvBrkText.setText(tfor(ave2));
    	
    		
    	
    	
    }
    
    private void onPausedLongTime(){
    	
    	AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        
        dialogBuilder.setMessage("It has been more than 6 hours since your last activity, would you like to reset the application?\n\nSelect Yes to reset.  Select No to retain data in paused mode.  Paused mode will allow you to email the log or review but you will not be able to add new events until the app is reset.");
        dialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                /* User clicked Yes so do some stuff */
            	reset();
            }
        });
        dialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                /* User clicked No so do some stuff */
            	//Toast.makeText(contractiontimer.this, "pause and disable buttons here", Toast.LENGTH_SHORT).show();
            	final Button button = (Button) findViewById(R.id.PregButton);
            	final Button wbutton = (Button) findViewById(R.id.WatersButton);
            	wbutton.setEnabled(false);
            	button.setEnabled(false);
            	EditText TimerTestText = (EditText)findViewById(R.id.timeDisplay);
    		   	TimerTestText.setText("00:00");
    		   	TimerTestText.setTextColor(Color.TRANSPARENT);       	
            	paused=true;
            	SharedPreferences preferences = getSharedPreferences(PREF_FILE_NAME, MODE_WORLD_WRITEABLE);
            	SharedPreferences.Editor editor = preferences.edit();
            	editor.putBoolean("pause", paused);
            	editor.commit();
            	
            }
        });
        
        dialogBuilder.setCancelable(false);
        dialogBuilder.create().show();
    	
    }
    
    private void undoLast(){
    	
    	// undo last action stuff here
    	// first grey out menu options
    	// need to work out what type of activity need removed
    	//Toast.makeText(contractiontimer.this, "undoing...", Toast.LENGTH_SHORT).show();
    	
    	if(lastWaters){
    		removeWatersLast();
    		}
    	else{
    	conCount=conCount-1;
    	undoable=false;
    	SharedPreferences preferences = getSharedPreferences(PREF_FILE_NAME, MODE_WORLD_WRITEABLE);
    	SharedPreferences.Editor editor = preferences.edit();
    	editor.putInt("conCountS", conCount);
    	editor.putBoolean("undoble", false);
    	editor.commit();
    	
    	if (startStop){
    		startStop=false;
    		seq=seq+c1;
    		c1=c2;
    		c2=c3;
    		c3=c4;
    		c4=c5;
    		c5=c6;
    		c6=0;

    		SharedPreferences.Editor editor1 = preferences.edit();
    		editor1.putInt("C1", c1);
    		editor1.putInt("C2", c2);
    		editor1.putInt("C3", c3);
    		editor1.putInt("C4", c4);
    		editor1.putInt("C5", c5);
    		editor1.putInt("C6", c6);
    		editor1.putBoolean("storedState1", false);
    		editor1.commit();
    		
    		if (conCount>10)//c5 but for testing c1
    		{
        		TextView AvConText = (TextView)findViewById(R.id.AverageOut2);
        		int ave =((c1+c2+c3+c4+c5)/5);
        		
        		AvConText.setText(tfor(ave));
        		TextView AvText = (TextView)findViewById(R.id.AverageOut);
        		av=tfor((c1+b1+c2+b2+c3+b3+c4+b4+c5+b5)/5);
        		
        		AvText.setText(av);
        		SharedPreferences.Editor editor2 = preferences.edit();
        		editor2.putString("st2st", av);
        		editor2.commit();
        		
        		//int ave=c1;//for testing
        	}
    		   		

    	}
    	else
    	{
    		startStop=true;
    		seq=seq+b1;
    		b1=b2;
    		b2=b3;
    		b3=b4;
    		b4=b5;
    		b5=b6;
    		b6=0;
    		
    		SharedPreferences.Editor editor1 = preferences.edit();
    		editor1.putInt("B1", b1);
    		editor1.putInt("B2", b2);
    		editor1.putInt("B3", b3);
    		editor1.putInt("B4", b4);
    		editor1.putInt("B5", b5);
    		editor1.putInt("B6", b6);
    		editor1.putBoolean("storedState1", true);
    		editor1.commit();
    		if (conCount>10)
        	{
        		TextView AvBrkText = (TextView)findViewById(R.id.AverageOut1);
        		
        		
        		int ave=((b1+b2+b3+b4+b5)/5);//for real
        		AvBrkText.setText(tfor(ave));
        		
        		
        	}
    		
    	}

    	removeLastRow();
    	
    	}
    	
    	// if undoing a "contraction start" & has start/stop timer 
    	if( !startStop && timer2StatusChanged ) {
    		if( startStop2 ) {
    			//if timer started by last step, stop it
    			startStop2 = false;
    			seq2 = 0;
    			mHandler.removeCallbacks(mUpdateTimerTask2);
    			display2();
    		} else {
    			/*undo a contraction start means prolonging the last break
    			* so if the timer has already been stopped, longer break time means the timer is not going to start again.
    			* prevStoredTime2 is not stored into database, so if 
    			* 1. contraction start + stop timer (due to breaking the 51 rule 5 times in a row)
    			* 2. power off
    			* 3. undo last (prevStoredTime2 won't be recovered)
    			* the time will be off for a while and next contraction start will stop the timer and reset everything.
    			*/
    			//if timer stopped by last step, start it and restore storedTime2 (recal seq2)
    			startStop2 = true;
    			seq2=(int) ((System.currentTimeMillis()-prevStoredTime2)/1000);
    			mHandler.postDelayed(mUpdateTimerTask2, 1000);
    			display2();
    		}
    		
    	}
    	
    
    	
    }
    
    private void removeLastRow(){
    	
    	//remove a row
    	
    	TableLayout tl = (TableLayout)findViewById(R.id.ActivityList2);
    	tl.getChildCount();
    	
       	tl.removeViewAt((tl.getChildCount())-1);
       	Cursor c = mDbHelper.fetchAllPreg();
    	int rowC = c.getCount();
    	mDbHelper.updatePreg(rowC,3,1,"a");
    	
    }
    
    private void removeWatersLast(){
    	
    	//stub - code to remove the waters row here
    	SharedPreferences preferences = getSharedPreferences(PREF_FILE_NAME, MODE_WORLD_WRITEABLE);
    	SharedPreferences.Editor editor = preferences.edit();
    	editor.putBoolean("watersState", true);
    	editor.putBoolean("watersLst", false);
    	   	
    	editor.putBoolean("undoble", false);
    	editor.commit();
    	
    	undoable=false; 
    	lastWaters=false;
    	TableLayout tl = (TableLayout)findViewById(R.id.ActivityList2);
    	tl.getChildCount();
    	
       	tl.removeViewAt((tl.getChildCount())-1);
       	Cursor c = mDbHelper.fetchAllPreg();
    	int rowC = c.getCount();
    	mDbHelper.updatePreg(rowC,3,1,"a");//not working, change to ignore and ignore in list builders?

    	
    	watersFlag = true;
    }
}