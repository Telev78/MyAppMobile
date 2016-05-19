package com.mangasanctuary.mobile.mangasanctuarymobile;

import java.util.Arrays;

import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.XPatherException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

public abstract class CollectionActivity extends Activity {
	
	protected abstract String getCollectionURL ();
	protected abstract SerieAdapter getCollection ();
	protected abstract void setCollection (SerieAdapter value);
	protected abstract void CollectionItemClick (int pos);
	
	protected User _user = User.getInstance();
	protected ListView lstCollection;
	private static ProgressDialog dialog;
	
	private AsyncTask<String, Void, Void> T;
	
	@SuppressWarnings("unchecked")
	@Override
    public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.collection);
	    
	    Spinner cbo_status = (Spinner) findViewById(R.id.cbo_status);
	    
	    Statut.setContext(getApplicationContext());
	    Statut[] status = Statut.values();
	    Arrays.sort(status, new AlphaSort());
	    
	    	    
	    ArrayAdapter<Statut> adapter = new ArrayAdapter<Statut>(this, android.R.layout.simple_spinner_item, status);
	    adapter.setDropDownViewResource (android.R.layout.simple_dropdown_item_1line);
	    cbo_status.setAdapter(adapter);
	    cbo_status.setOnItemSelectedListener(listener);
	    
	    lstCollection = (ListView) findViewById(R.id.lst_collection);
	    lstCollection.setTextFilterEnabled(true);
	    lstCollection.setOnItemClickListener(lstitemOnClick);
    	
    	Log.i (getString(R.string.app_name), getCollectionURL());
    	
    	// Check if the thread is already running
		T = (AsyncTask<String, Void, Void>) getLastNonConfigurationInstance();
		if (T != null && T.getStatus() == AsyncTask.Status.RUNNING) {
			showDialog();
		}
		else
		{
			if (getCollection() == null)
				
				T = new processCollectionTask().execute(getCollectionURL());
				
			else{
				lstCollection.setAdapter(getCollection());
			}
		}
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
	
	// dismiss dialog if activity is destroyed
	@Override
	protected void onDestroy() {
		dismissDialog();
		super.onDestroy();
	}
	
	private OnItemClickListener lstitemOnClick = new OnItemClickListener (){

		public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
			CollectionItemClick(pos);
		}
		
	};
	
	private OnItemSelectedListener listener = new OnItemSelectedListener() {

		public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
			if (lstCollection.getAdapter() != null)
			{
				Statut st = (Statut)parent.getItemAtPosition(pos);
				switch (st){
					case Tous: ((SerieAdapter)lstCollection.getAdapter()).getFilter().filter(""); break;
					default: ((SerieAdapter)lstCollection.getAdapter()).getFilter().filter(st.FriendlyName()); break;
				}
			}
		}

		public void onNothingSelected(AdapterView<?> arg0) {
			//Do Nothing
			
		}
	}; 
	
	private class processCollectionTask extends AsyncTask<String, Void, Void>{

		@Override
		protected Void doInBackground(String... params) {
			String SERIE_XPATH = "//tr[@bgcolor]";
			
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
				Log.i (getString(R.string.app_name), "Start Parsing Collection");
				node = cleaner.clean(CustomHttpClient.executeHttpGet(params[0], "iso-8859-1"));
				
				
				Object[] info_nodes;
				
				Serie serie;
				
				setCollection(new SerieAdapter(CollectionActivity.this,  R.layout.serie_listviewline));
				
				
				/*
				 *<tr bgcolor="#fff1cb">
				 *	<td>
				 *		<a href="http://www.manga-sanctuary.com/collection.php?id=29830&page=liste_volumes&id_edition=2220">.Hack// Le Bracelet Du Cr�puscule (SIMPLE)</a>
				 *	</td>
				 *	<td align=\"center\">Manga</td>
				 *	<td align=\"center\">Panini manga</td>
				 *	<td align=\"center\">3</td>
				 *	<td align=\"center\">
				 *		<a href="http://www.manga-sanctuary.com/collection.php?id=29830&page=membres_serie&id_serie=114">1147</a>
				 *	</td>
				 *	<td align="center">6</td>
				 *	<td align="right">25.8&euro;</td>
				 *	<td align="center">Compl�te</td>
				 *</tr>
				 */
				
				info_nodes = node.evaluateXPath(SERIE_XPATH);
				
				if (info_nodes.length > 0) {
					int i = 0;
					while (i < info_nodes.length) {
						serie = new Serie();
						
						serie.setNom (((TagNode)info_nodes[i]).getChildTags()[0].getChildTags()[0].getText().toString());
						Log.i (getString(R.string.app_name), serie.getNom());
						serie.setURL(((TagNode)info_nodes[i]).getChildTags()[0].getChildTags()[0].getAttributeByName("href"));
						Log.i (getString(R.string.app_name), serie.getURL());
						serie.setType(((TagNode)info_nodes[i]).getChildTags()[1].getText().toString());
						serie.setEditeur (((TagNode)info_nodes[i]).getChildTags()[2].getText().toString());
						serie.setTotalVolumes(Integer.parseInt(((TagNode)info_nodes[i]).getChildTags()[3].getText().toString()));
						
						String status = ((TagNode)info_nodes[i]).getChildTags()[7].getText().toString();
						serie.setStatut(Statut.valueOfAlias(status));
						
						getCollection().add(serie);
						i++;
					}
				}
				
				Log.i (getString(R.string.app_name), "End Parsing Collection");
				
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
			lstCollection.setAdapter(getCollection());
			
			dismissDialog();
		}
	}
	
}
