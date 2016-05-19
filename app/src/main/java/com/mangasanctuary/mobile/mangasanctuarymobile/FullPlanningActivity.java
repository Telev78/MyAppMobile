package com.mangasanctuary.mobile.mangasanctuarymobile;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.XPatherException;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;

public class FullPlanningActivity extends Activity {
	
	private int _month = 0;
	private int _year = 0;
	private static ProgressDialog dialog;
	User user = User.getInstance();
	@SuppressWarnings("rawtypes")
	private AsyncTask T;
	private ListView lstPlanning;
		
    public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.fullplanning);
	    
	    final Calendar c = Calendar.getInstance();
	    _month = c.get (Calendar.MONTH) + 1;
	    _year = c.get (Calendar.YEAR);
	    
	    TextView dptext = (TextView)findViewById(R.id.dptext);
	    String txt = String.valueOf(_month)  + '/' + String.valueOf(_year);
    	dptext.setText(txt);
	    
    	lstPlanning = (ListView) findViewById(R.id.lstFullPlanning);
	    //lstPlanning.setOnItemClickListener(lstitemOnClick);
	    
	    launchThread();
    	
    }

	@SuppressWarnings("rawtypes")
	private void launchThread() {
		String planningURL = getString(R.string.URL) + getString(R.string.fullPlanningURL);
	    planningURL = planningURL.replace("[MONTH]", String.valueOf(_month));
	    planningURL = planningURL.replace("[YEAR]", String.valueOf(_year));
	    
	    Log.i (getString(R.string.app_name), planningURL);
    	
    	// Check if the thread is already running
		T = (AsyncTask) getLastNonConfigurationInstance();
		if (T != null && T.getStatus() == AsyncTask.Status.RUNNING) {
			showDialog();
		}
		else
		{
			String key = buildKey(_month,_year);
			if (user.getFullPlanning(key) == null)
				
				T = new processFullPlanningTask().execute(planningURL, String.valueOf(_month), String.valueOf(_year));
				
			else{
				lstPlanning.setAdapter(user.getFullPlanning(key));
			}
		}
	}

 // dismiss dialog if activity is destroyed
 	@Override
 	protected void onDestroy() {
 		dismissDialog();
 		super.onDestroy();
 	}
 	
 	// Save the thread
 	@Override
 	public Object onRetainNonConfigurationInstance() {
 		return T;
 	}
 	
 	private void showDialog (){
 		dialog = ProgressDialog.show(this, getString(R.string.loading), getString(R.string.please_wait));
 		dialog.setCancelable(false);
 	}
 	
 	private void dismissDialog (){
 		if (dialog != null && dialog.isShowing()) {
 			dialog.dismiss();
 			dialog = null;
 		}
 	}
    
    public void showDatePickerDialog(View v) {
		AlertDialog.Builder dtDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);

        final View layout = inflater.inflate(R.layout.monthpickerdialog,
                (ViewGroup) findViewById(R.id.layout_root));
        
        customizeDatePicker(layout);
        
        DatePicker datePicker = (DatePicker)layout.findViewById(R.id.dpResult);
        datePicker.init(_year, _month - 1, 1, onDateChangedListener);
        
        dtDialog.setView(layout);
        dtDialog.setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener(){

            public void onClick(DialogInterface dialog, int which) {
            	TextView dptext = (TextView)findViewById(R.id.dptext);
            	String txt = String.valueOf(_month)  + '/' + String.valueOf(_year);
            	dptext.setText(txt);
                dialog.dismiss();
                
                launchThread();
            }

        });

        dtDialog.setNegativeButton(getString(android.R.string.cancel), new DialogInterface.OnClickListener(){

            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }

        });
                 
        dtDialog.create();
        dtDialog.show(); 
    }
    
    public DatePicker.OnDateChangedListener onDateChangedListener = new DatePicker.OnDateChangedListener() {
		
		public void onDateChanged(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			
			_month = view.getMonth() + 1;
        	_year = view.getYear();
		}
	};
       
    private void customizeDatePicker (View layout)
    {

    	DatePicker datePicker = (DatePicker)layout.findViewById(R.id.dpResult);
    	
    	try {
    	    Field f[] = datePicker.getClass().getDeclaredFields();
    	    for (Field field : f) {
    	        if (field.getName().equals("mDayPicker")) {
    	            field.setAccessible(true);
    	            Object dayPicker = new Object();
    	            dayPicker = field.get(datePicker);
    	            ((View) dayPicker).setVisibility(View.GONE);
    	        }
    	    }
    	} catch (SecurityException e) {
    	    Log.d("ERROR", e.getMessage());
    	} catch (IllegalArgumentException e) {
    	    Log.d("ERROR", e.getMessage());
    	} catch (IllegalAccessException e) {
    	    Log.d("ERROR", e.getMessage());
    	}
    		
    
    }

    private String buildKey(int month, int year)
    {
    	return "m:" + String.valueOf(month) + "y:" + String.valueOf(year);
    }
    
    private class processFullPlanningTask extends AsyncTask<String, Void, Void>{

		@Override
		protected Void doInBackground(String... params) {
						
			HtmlCleaner cleaner = new HtmlCleaner();
			TagNode node;
			
			CleanerProperties props = cleaner.getProperties();
			props.setAllowHtmlInsideAttributes(true);
			props.setAllowMultiWordAttributes(true);
			props.setRecognizeUnicodeChars(true);
			props.setOmitComments(true);
			cleaner.getProperties().setRecognizeUnicodeChars(true);
			cleaner.getProperties().setAdvancedXmlEscape(true);
			
			try {
				Log.i (getString(R.string.app_name), "Start Parsing Full Planning");
				node = cleaner.clean(CustomHttpClient.executeHttpGet(params[0], "UTF-8"));
				
				String key = buildKey(_month, _year);
				
				Object[] info_nodes;
				Object[] current_node;
				
				VolumeItem item;
				Date current_date = null;
				
				user.setFullPlanning(key, new VolumeAdapter(FullPlanningActivity.this,  R.layout.volume_listviewline));
				
				String PLANNING_XPATH = "//table[@class='collection']/tbody//tr";
				String DATE_XPATH = "//td//span[@class='date_titre']";
				String VOLUME_XPATH = "//td[@class='tc']";
				String DONEXT_XPATH = "//td[@class='tc'][@colspan]";
				
				info_nodes = node.evaluateXPath(PLANNING_XPATH);
				
				if (info_nodes.length > 0) {
					
					int i = 0;
					SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE dd MMMM yyyy", Locale.FRENCH);
					simpleDateFormat.setLenient(false);
					
					while (i < info_nodes.length) {
						current_node = ((TagNode)info_nodes[i]).evaluateXPath(DATE_XPATH);
						if (current_node.length > 0)
						{
							//<span class="date_titre">Samedi 01&nbsp;septembre&nbsp;2012</span>
							String date = ((TagNode)current_node[0]).getText().toString();
							//date = Html.fromHtml(date).toString().toLowerCase();
							date = date.replaceAll("&nbsp;", " "); //replace html char -> not a space and launch exception with parse date method
							
							current_date = simpleDateFormat.parse(date);
							Log.i (getString(R.string.app_name), current_date.toString());
						}
						else 
						{
							current_node = ((TagNode)info_nodes[i]).evaluateXPath(DONEXT_XPATH);
							if (current_node.length == 0)
							{
								current_node = ((TagNode)info_nodes[i]).evaluateXPath(VOLUME_XPATH);
								if (current_node.length > 0)
								{
									item = new VolumeItem();
									item.setPlanningDate (current_date);
									item.setNom(((TagNode)info_nodes[i]).getChildTags()[0].getChildTags()[0].getText().toString().trim() + 
											((TagNode)info_nodes[i]).getChildTags()[0].getChildTags()[1].getText().toString().trim());
									item.setEditeur(((TagNode)info_nodes[i]).getChildTags()[1].getText().toString());
									
									user.getFullPlanning(key).add(item);
									
									Log.i (getString(R.string.app_name), String.valueOf(i) + ": " + item.getNom());
								}
							}
							else
								Log.i (getString(R.string.app_name), "NEXT");
						}
						
						i++;
					}
					
				}
				
				Log.i (getString(R.string.app_name), "End Full Parsing Planning");
				
			} catch (XPatherException e) {
				
				e.printStackTrace();
			} catch (Exception e) {
				
				e.printStackTrace();
			} 
			return null;
			
		}
		
		@Override
		protected void onPreExecute (){
			showDialog();
		}
		
		@Override
		protected void onPostExecute(Void value){
			lstPlanning.setAdapter(user.getFullPlanning(buildKey(_month, _year)));
			
			dismissDialog();
		}
	}
    
}
