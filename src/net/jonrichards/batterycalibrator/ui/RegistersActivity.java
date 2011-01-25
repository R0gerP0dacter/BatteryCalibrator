/* This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://sam.zoy.org/wtfpl/COPYING for more details. */ 
package net.jonrichards.batterycalibrator.ui;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import net.jonrichards.batterycalibrator.system.DS2784Battery;
import net.jonrichards.batterycalibrator.system.LoggingService;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * A class for displaying information from the dump register.
 * @author Jon Richards
 * @author Roger Podacter
 */
public class RegistersActivity extends Activity {
	
	static ArrayList<Double> voltagedata = new ArrayList<Double>(60);
	static ArrayList<Integer> currentdata = new ArrayList<Integer>(60);
	static ArrayList<Integer> percentdata = new ArrayList<Integer>(60);
	static ArrayList<Integer> capacitydata = new ArrayList<Integer>(60);

	//public static final String TYPE = "type";

	private XYMultipleSeriesDataset mCurrentDataset = new XYMultipleSeriesDataset();
	private XYMultipleSeriesRenderer mCurrentRenderer = new XYMultipleSeriesRenderer();
	  
	private XYMultipleSeriesDataset mVoltageDataset = new XYMultipleSeriesDataset();
	private XYMultipleSeriesRenderer mVoltageRenderer = new XYMultipleSeriesRenderer();
	
	private XYMultipleSeriesDataset mPercentageDataset = new XYMultipleSeriesDataset();
	private XYMultipleSeriesRenderer mPercentageRenderer = new XYMultipleSeriesRenderer();
	
	private XYMultipleSeriesDataset mCapacityDataset = new XYMultipleSeriesDataset();
	private XYMultipleSeriesRenderer mCapacityRenderer = new XYMultipleSeriesRenderer();
	
	private static XYSeries mCurrentSeries;	  
	private static XYSeries mVoltageSeries;	  
	private static XYSeries mPercentageSeries;	  
	private static XYSeries mCapacitySeries;	  

	private static GraphicalView mCurrentChartView;
	private static GraphicalView mVoltageChartView;
	private static GraphicalView mPercentageChartView;
	private static GraphicalView mCapacityChartView;

	int index = 0;
	  
	private static TextView my_voltage;
	private static TextView my_current;
	private static TextView my_droprate;
	
	private TextView my_sample_poll_text;
	private EditText my_sample_poll_input;
	private Button my_poll_save_button;
	private Button my_poll_cancel_button;

	//Sample rate used for updating the chart UI
	public int my_sample_poll = 7;
	
	private static final int INTERVAL = 60000;
	
	//Handler for updating the UI
	private final Handler my_handler = new Handler();
	
	//Instance Variables	
	private PowerManager my_power_manager;
	private WakeLock my_wake_lock;
	private Timer my_timer;
	
	//public BatteryCalibratorService mBoundService;

	/**
	 * Called when the activity is first created, initializations happen here.
	 * @param savedInstanceState 
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registerslayout);        
        
        my_power_manager = (PowerManager)getBaseContext().getSystemService(Context.POWER_SERVICE);
        my_wake_lock = my_power_manager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, "LearnModeActivity");
        
        my_timer = new Timer();
        
        //text views and button for the UI for this tab
        my_sample_poll_text = (TextView)findViewById(R.id.txtSamplePoll);
        my_sample_poll_input = (EditText)findViewById(R.id.etSamplePoll);
        my_poll_save_button = (Button)findViewById(R.id.btnPollSave);
        my_poll_cancel_button = (Button)findViewById(R.id.btnPollCancel);
        
        my_sample_poll_text.setText(Integer.toString(my_sample_poll) + "sec");
        
        my_voltage = (TextView)findViewById(R.id.graphVoltage);
        my_current = (TextView)findViewById(R.id.graphCurrent);
        my_droprate = (TextView)findViewById(R.id.txtDropRate);

        //Function to setup the graph
        createGraph();        
        
        //Function to populate the graph
		//populateGraph();
		updateLongtermGraph();
		
		//Sets the new age value when pressed
        my_poll_save_button.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				try {
					int new_poll = Integer.parseInt((my_sample_poll_input.getText().toString()));
					if (new_poll > 0) {
						my_sample_poll = new_poll;						
						my_sample_poll_text.setText(Integer.toString(new_poll) + "sec");
					}
					my_sample_poll_input.setText("");					
					
				} catch(Exception e) {
					my_sample_poll_input.setText("");
				}
        		
			}
		});
        
        //Clears the sample poll input field when pressed
        my_poll_cancel_button.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				my_sample_poll_input.setText("");
				//my_sample_poll_text.setText(my_sample_poll);
			}
		});
        
		startService(new Intent(this, LoggingService.class));
        //mConnection = new MyServiceConnection(this);
    }

	@Override
	  protected void onRestoreInstanceState(Bundle savedState) {
	    super.onRestoreInstanceState(savedState);
	    mCurrentDataset = (XYMultipleSeriesDataset) savedState.getSerializable("dataset");
	    mCurrentRenderer = (XYMultipleSeriesRenderer) savedState.getSerializable("renderer");
	    mCurrentSeries = (XYSeries) savedState.getSerializable("current_series");
	    //mCurrentRenderer = (XYSeriesRenderer) savedState.getSerializable("current_renderer");
	    //mDateFormat = savedState.getString("date_format");
	  }
	
	@Override
	  protected void onSaveInstanceState(Bundle outState) {
	    super.onSaveInstanceState(outState);
	    outState.putSerializable("dataset", mCurrentDataset);
	    outState.putSerializable("renderer", mCurrentRenderer);
	    outState.putSerializable("current_series", mCurrentSeries);
	    //outState.putSerializable("current_renderer", mCurrentRenderer);
	    //outState.putString("date_format", mDateFormat);
	  }

	
	
	/**
	 * Called when this activity is resumed.
	 */
	@Override
    public void onResume() {
		if(!my_wake_lock.isHeld()) {
			my_wake_lock.acquire();
		}
		my_handler.postDelayed(mUpdateUITimerTask, my_sample_poll*1000);
		
		//bindService(new Intent(this, BatteryCalibratorService.class), mConnection, Context.BIND_AUTO_CREATE);
		
		if (mCurrentChartView == null) {
		      LinearLayout layout = (LinearLayout) findViewById(R.id.currentChart);
		      mCurrentChartView = ChartFactory.getLineChartView(this, mCurrentDataset, mCurrentRenderer);
		      layout.addView(mCurrentChartView, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		} else {
		      mCurrentChartView.repaint();
		}
		if (mVoltageChartView == null) {
		      LinearLayout layout1 = (LinearLayout) findViewById(R.id.voltageChart);
		      mVoltageChartView = ChartFactory.getLineChartView(this, mVoltageDataset, mVoltageRenderer);
		      layout1.addView(mVoltageChartView, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		} else {
		      mVoltageChartView.repaint();
		}
		if (mPercentageChartView == null) {
		      LinearLayout layout2 = (LinearLayout) findViewById(R.id.percentChart);
		      mPercentageChartView = ChartFactory.getLineChartView(this, mPercentageDataset, mPercentageRenderer);
		      layout2.addView(mPercentageChartView, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		} else {
		      mPercentageChartView.repaint();
		}
		if (mCapacityChartView == null) {
		      LinearLayout layout3 = (LinearLayout) findViewById(R.id.mahChart);
		      mCapacityChartView = ChartFactory.getLineChartView(this, mCapacityDataset, mCapacityRenderer);
		      layout3.addView(mCapacityChartView, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		} else {
		      mCapacityChartView.repaint();
		}
		
        super.onResume();
    }
	
	/**
	 * Called when this activity is paused.
	 */
	@Override
    public void onPause() {
		if(my_wake_lock.isHeld()) {
			my_wake_lock.release();
		}
		my_handler.removeCallbacks(mUpdateUITimerTask);
        super.onPause();
        //unbindService(mConnection);
    }
	
	/**
	 * Creates an options menu.
	 * @param menu The options menu to place the menu items in.
	 * @return Returns boolean true.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.menu, menu);
	    return true;
	}
	
	/**
	 * Called when an item in the options menu is selected.
	 * @param item The MenuItem selected.
	 * @return Returns a boolean true.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
		    case R.id.about:     
	        	Intent myIntent = new Intent();
	            myIntent.setClass(this, AboutActivity.class);
	            startActivity(myIntent);
	            break;
		        case R.id.dump_reg:     
	        	DS2784Battery battery_info = new DS2784Battery();				
				String text = battery_info.getDumpRegister();
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle(R.string.dump_reg);
				builder.setPositiveButton(R.string.ok, null);
		        builder.setMessage(text).create().show();
	        	break;
	        case R.id.settings: 
	        	startActivity(new Intent(this, SettingsActivity.class));                
	            break;
	/*	        case R.id.instructions: 
	        	Toast.makeText(this, "Add directions for the app", Toast.LENGTH_LONG).show();
	            break;*/
	        case R.id.exit: 
	        	//Toast.makeText(this, "Exit to stop the app.", Toast.LENGTH_LONG).show();
	        	finish();
	        	stopService(new Intent(this, LoggingService.class));
	        	break;
	        default:
	        	break;
	    }
	    return true;
	}
	
	//Private Methods	

	/**
	 * Paints first two graphs with data.
	 */
	public void populateGraph() {	
		
		DS2784Battery battery_info = new DS2784Battery();
		
		String voltage_text = battery_info.getVoltage();
		double voltage = Double.parseDouble(voltage_text);

		String current_text = battery_info.getCurrent();
		int current = Integer.parseInt(current_text);
		
		voltagedata.add(new Double(voltage/1000000));
		currentdata.add(new Integer(current/1000));
		
        if (currentdata.size() == 60) {
        	currentdata.remove(0);
        	voltagedata.remove(0);
        }
        
        mCurrentSeries.clear();
        int index1 = 0;
        for (int i = (currentdata.size() - 1); i >= 0 && index1 < 60; i--) {
        	mCurrentSeries.add(index1++, (currentdata.get(i).intValue()));                	
        }
        
        mVoltageSeries.clear();
        int index2 = 0;
        for (int i = (voltagedata.size() - 1); i >= 0 && index2 < 60; i--) {
        	mVoltageSeries.add(index2++, (voltagedata.get(i).doubleValue()));                	
        }
        
        if (mCurrentChartView != null || mVoltageChartView != null) {
            mCurrentChartView.repaint();
            mVoltageChartView.repaint();
        }
        
        my_voltage.setText(voltage_text);
        my_current.setText(current_text);
	}
	
	/**
	 * Sets up all four graphs.
	 */
	public void createGraph() {	
		
		//All the configurations for the Current graph
		//String seriesCurrentTitle = "Current";
        XYSeries series1 = new XYSeries("Current (mA)");
        mCurrentDataset.addSeries(series1);
        mCurrentSeries = series1;
        XYSeriesRenderer renderer = new XYSeriesRenderer();
        mCurrentRenderer.addSeriesRenderer(renderer);
        
        mCurrentRenderer.setXAxisMin(0);
        mCurrentRenderer.setXAxisMax(60);
        mCurrentRenderer.setYAxisMin(-1000);
        mCurrentRenderer.setYAxisMax(1100);
        mCurrentRenderer.setAxesColor(Color.GRAY);
        mCurrentRenderer.setLabelsColor(Color.WHITE);
        mCurrentRenderer.setXLabels(10);
        
        mCurrentRenderer.setChartValuesTextSize(12f);
        
        mCurrentRenderer.setShowGrid(true);
        mCurrentRenderer.setXTitle("Time (sec)");
        mCurrentRenderer.setYTitle("Current (mA)");
        mCurrentRenderer.setAxisTitleTextSize(15f);
        
        renderer.setLineWidth(4);
        renderer.setFillBelowLine(true);
        
        renderer.setFillPoints(true);  
        renderer.setColor(Color.GREEN);

        
        //All the configurations for the Voltage graph
        //String seriesVoltageTitle = "Voltage";
        XYSeries series2 = new XYSeries("Voltage (V)");
        mVoltageDataset.addSeries(series2);
        mVoltageSeries = series2;
        XYSeriesRenderer renderer1 = new XYSeriesRenderer();
        mVoltageRenderer.addSeriesRenderer(renderer1);            
        
        mVoltageRenderer.setXAxisMin(0);
        mVoltageRenderer.setXAxisMax(60);
        mVoltageRenderer.setYAxisMin(3.4);        
        mVoltageRenderer.setYAxisMax(4.2);
        mVoltageRenderer.setAxesColor(Color.GRAY);
        mVoltageRenderer.setLabelsColor(Color.WHITE);
        mVoltageRenderer.setXLabels(10);
        mVoltageRenderer.setChartValuesTextSize(12f);
        mVoltageRenderer.setShowGrid(true);
        mVoltageRenderer.setXTitle("Time (sec)");
        mVoltageRenderer.setYTitle("Voltage (V)");
        mVoltageRenderer.setAxisTitleTextSize(15f);
        
        renderer1.setFillPoints(true); 
            
        renderer1.setLineWidth(3);
        renderer1.setFillBelowLine(true);
        //renderer1.setFillBelowLineColor(11111111);
        renderer1.setFillPoints(true);
        renderer1.setColor(Color.RED);
        
        //All the configurations for the Percentage graph
        //String seriesVoltageTitle = "Voltage";
        XYSeries series3 = new XYSeries("Percentage (%)");
        mPercentageDataset.addSeries(series3);
        mPercentageSeries = series3;
        XYSeriesRenderer renderer2 = new XYSeriesRenderer();
        mPercentageRenderer.addSeriesRenderer(renderer2);            
        
        mPercentageRenderer.setXAxisMin(0);
        mPercentageRenderer.setXAxisMax(60);
        mPercentageRenderer.setYAxisMin(0);        
        mPercentageRenderer.setYAxisMax(100);
        mPercentageRenderer.setAxesColor(Color.GRAY);
        mPercentageRenderer.setLabelsColor(Color.WHITE);
        mPercentageRenderer.setXLabels(10);
        mPercentageRenderer.setChartValuesTextSize(12f);
        mPercentageRenderer.setShowGrid(true);
        mPercentageRenderer.setXTitle("Time (sec)");
        mPercentageRenderer.setYTitle("Percent (%)");
        mPercentageRenderer.setAxisTitleTextSize(15f);
        
        renderer2.setFillPoints(true); 
            
        renderer2.setLineWidth(3);
        renderer2.setFillBelowLine(true);
        //renderer1.setFillBelowLineColor(11111111);
        renderer2.setFillPoints(true);
        renderer2.setColor(Color.YELLOW);
        
      //All the configurations for the Capacity graph
        //String seriesVoltageTitle = "Voltage";
        XYSeries series4 = new XYSeries("Capacity (mAh)");
        mCapacityDataset.addSeries(series4);
        mCapacitySeries = series4;
        XYSeriesRenderer renderer3 = new XYSeriesRenderer();
        mCapacityRenderer.addSeriesRenderer(renderer3);            
        
        mCapacityRenderer.setXAxisMin(0);
        mCapacityRenderer.setXAxisMax(60);
        mCapacityRenderer.setYAxisMin(0);        
        mCapacityRenderer.setYAxisMax(2600);
        mCapacityRenderer.setAxesColor(Color.GRAY);
        mCapacityRenderer.setLabelsColor(Color.WHITE);
        mCapacityRenderer.setXLabels(10);
        mCapacityRenderer.setChartValuesTextSize(12f);
        mCapacityRenderer.setShowGrid(true);
        mCapacityRenderer.setXTitle("Time (sec)");
        mCapacityRenderer.setYTitle("Capacity (mAh)");
        mCapacityRenderer.setAxisTitleTextSize(15f);
        
        renderer3.setFillPoints(true); 
            
        renderer3.setLineWidth(3);
        renderer3.setFillBelowLine(true);
        //renderer1.setFillBelowLineColor(11111111);
        renderer3.setFillPoints(true);
        renderer3.setColor(Color.CYAN);
	}
	
	/**
	 * Our runnable to continuously update the UI.
	 */
	private final Runnable mUpdateUITimerTask = new Runnable() {
	    public void run() {
	    	
	    	//populateGraph();	    	
	        my_handler.postDelayed(mUpdateUITimerTask, my_sample_poll*1000);
	    }
	};
	
	/**
	 * Use the same timer to update the two slow-sample rate graphs
	 */
	private void updateLongtermGraph() {
		my_timer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				//populatePercentageGraph();
				//populateCapacityGraph();

			}
		}, 0, INTERVAL);
		
	}
	
	/**
	 * populating the percent graph
	 */
	public void populatePercentageGraph () {
		
		DS2784Battery battery_info = new DS2784Battery();		
				
		//Populate percent
		String percent_text = battery_info.getDumpRegister(6);
		try {
			percent_text = Integer.toString(Integer.parseInt(percent_text,16));
		} catch(Exception e) {
			
		}
		
		int percent = Integer.parseInt(percent_text);
		
		percentdata.add(new Integer(percent));
		
        if (percentdata.size() == 60) {
        	percentdata.remove(0);
        }
        
        mPercentageSeries.clear();
        int index1 = 0;
        for (int i = (percentdata.size() - 1); i >= 0 && index1 < 60; i--) {
        	mPercentageSeries.add(index1++, (percentdata.get(i).intValue()));                	
        }        
        
        if (mPercentageChartView != null) {
            mPercentageChartView.repaint();
        }
        
        //my_percent.setText(percent_text);
	}
	
	/**
	 * populating the capacity graph
	 */
	public void populateCapacityGraph () {
		
		DS2784Battery battery_info = new DS2784Battery();		
				
		//Populate capacity
		String capacity_text = battery_info.getMAh();
		try {
			int mAh = (Integer.parseInt(capacity_text))/1000;
			capacity_text = Integer.toString(mAh);
		} catch(Exception e) {
			
		}
		
		int capacity = Integer.parseInt(capacity_text);
		
		capacitydata.add(new Integer(capacity));
		
        if (capacitydata.size() == 60) {
        	capacitydata.remove(0);
        }
        
        mCapacitySeries.clear();
        int index1 = 0;
        for (int i = (capacitydata.size() - 1); i >= 0 && index1 < 60; i--) {
        	mCapacitySeries.add(index1++, (capacitydata.get(i).intValue()));                	
        }        
        
        if (mCapacityChartView != null) {
            mCapacityChartView.repaint();
        }
        
        //my_capacity.setText(capacity_text);
	}

	/**
	 * Attempting to track capacity drop per minute or per hour etc.  I suck.
	 */
	public void DropRate() {
		Calendar calendar = Calendar.getInstance();
		String date = calendar.getTime().toString();
		
		//long elapsedMillis = SystemClock.elapsedRealtime();
		
/*		long startingTime = new Date().getTime();
		long elapsedTime =  new Date().getTime() - startingTime;
		my_droprate.setText(elapsedTime);
*/		
	}
	
/*	
	private ServiceConnection mConnection = new ServiceConnection() {
	    public void onServiceConnected(ComponentName className, IBinder service) {
	        // This is called when the connection with the service has been
	        // established, giving us the service object we can use to
	        // interact with the service.  Because we have bound to a explicit
	        // service that we know is running in our own process, we can
	        // cast its IBinder to a concrete class and directly access it.
	        mBoundService = ((BatteryCalibratorService.BatteryCalibratorBinder)service).getService();

	        // Tell the user about this for our demo.
	        Toast.makeText(RegistersActivity.this, R.string.local_service_connected, Toast.LENGTH_SHORT).show();
	    }
        
	    public void onServiceDisconnected(ComponentName className) {
	        // This is called when the connection with the service has been
	        // unexpectedly disconnected -- that is, its process crashed.
	        // Because it is running in our same process, we should never
	        // see this happen.
	        mBoundService = null;
	        Toast.makeText(RegistersActivity.this, R.string.local_service_disconnected, Toast.LENGTH_SHORT).show();
	    }
	};
*/	
}
//End of class Registers