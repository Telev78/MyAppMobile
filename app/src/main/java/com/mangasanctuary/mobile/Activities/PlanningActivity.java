package com.mangasanctuary.mobile.Activities;

import java.text.SimpleDateFormat;

import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.XPatherException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.mangasanctuary.mobile.Service.CustomHttpClient;
import com.mangasanctuary.mobile.R;
import com.mangasanctuary.mobile.Models.User;
import com.mangasanctuary.mobile.Adapters.VolumeAdapter;
import com.mangasanctuary.mobile.Models.VolumeItem;

public class PlanningActivity extends Activity {
	
	private static ProgressDialog dialog;
	User user = User.getInstance();
	@SuppressWarnings("rawtypes")
	private AsyncTask T;
	private ListView lstPlanning;
	
	@SuppressWarnings({ "rawtypes" })
	@Override
    public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.volumes);
	    
	    lstPlanning = (ListView) findViewById(R.id.lst_volumes);
	    lstPlanning.setOnItemClickListener(lstitemOnClick);
	    
	    String planningURL = getString(R.string.URL) + getString(R.string.planningURL);
	    planningURL = planningURL.replace("[IDUSER]", user.getID());
	    
	    Log.i (getString(R.string.app_name), planningURL);
    	
    	// Check if the thread is already running
		T = (AsyncTask) getLastNonConfigurationInstance();
		if (T != null && T.getStatus() == AsyncTask.Status.RUNNING) {
			showDialog();
		}
		else
		{
			if (user.getPlanning() == null)
				
				T = new processPlanningTask().execute(planningURL);
				
			else{
				lstPlanning.setAdapter(user.getPlanning());
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
	
	private OnItemClickListener lstitemOnClick = new OnItemClickListener (){

		public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
			
			VolumeItem item = (VolumeItem)parent.getAdapter().getItem(pos);
			/*
			user.setCurrentVolume(item);
			
			Intent i = new Intent(PlanningActivity.this, VolumeDetailActivity.class);
									
			startActivity(i);
			
			*/
			if (item.getImage() == null)		
				T = new processGetimageTask().execute(item);
			else
				showImage(item.getImage(), item.getNom());
			
			
		}
		
	};
	
	private void showImage (Bitmap img, String titre){
		AlertDialog.Builder imageDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);

        View layout = inflater.inflate(R.layout.custom_fullimage_dialog,
                (ViewGroup) findViewById(R.id.layout_root));
        ImageView image = (ImageView) layout.findViewById(R.id.fullimage);
        
        if (img != null)
        	image.setImageBitmap(img);
        else{
        	String defaultCouv = getString(R.string.defaultcover);
			int rid = getResources().getIdentifier(defaultCouv, null, null);
        	image.setImageResource(rid);
        }
        imageDialog.setView(layout);
        imageDialog.setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener(){

            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }

        });

        TextView txtTitre = (TextView)layout.findViewById(R.id.custom_fullimage_placename);
        txtTitre.setText(titre);
        
        imageDialog.create();
        imageDialog.show(); 
	}
	
	private class processGetimageTask extends AsyncTask<VolumeItem, Void, VolumeItem>{

		@Override
		protected VolumeItem doInBackground(VolumeItem... params) {
			
			HtmlCleaner cleaner = new HtmlCleaner();
			TagNode node;
			
			Bitmap img = null;
			
			CleanerProperties props = cleaner.getProperties();
			props.setAllowHtmlInsideAttributes(true);
			props.setAllowMultiWordAttributes(true);
			props.setRecognizeUnicodeChars(true);
			props.setOmitComments(true);
			cleaner.getProperties().setRecognizeUnicodeChars(true);
			cleaner.getProperties().setAdvancedXmlEscape(true);
			
			try {
				Log.i (getString(R.string.app_name), "Start Parsing Cover");
				node = cleaner.clean(CustomHttpClient.executeHttpGet(params[0].getURL(), "UTF-8"));
				
				
				Object[] info_nodes;
				
				String COVER_XPATH_BIG = "//div[@id='infos_generales_gauche']//div[@id='menu_fiche']//div[@id='image_serie']//a[@id='image_serie_vol_a']";
				String COVER_XPATH = "//div[@id='infos_generales_gauche']//div[@id='menu_fiche']//div[@id='image_serie']//a[@id='image_serie_vol_a']//img[@src]";
				
				/*
				 *<div id="infos_generales_gauche" style="width:170px;">
				 *	<div id="menu_fiche" style="width:170px;font-size:14px;font-weight:bold;text-align: center;float:left;">
				 *		<div id="image_serie" style="float:none;width:100%;">
				 *			<a id="image_serie_vol_a" href="http://img.manga-sanctuary.com/big/kimagure-orange-road-max-et-compagnie-manga-volume-8-reedition-francaise-55241.jpg" title="">
				 *				<img src="http://img.manga-sanctuary.com/kimagure-orange-road-max-et-compagnie-manga-volume-8-reedition-francaise-55241.jpg?1331801780" alt="  " />
				 *			</a>
				 *		<span class="copyright">KIMAGURE ORANGE ROAD � 1984 by Izumi Matsumoto/ SHUEISHA Inc. </span>
				 *		</div>
				 */
				

				info_nodes = node.evaluateXPath(COVER_XPATH_BIG);
				if (info_nodes.length > 0)
				{
					String url = ((TagNode)info_nodes[0]).getAttributeByName("href");
					img = CustomHttpClient.downloadFile(url);
					params[0].setImage(img);
					Log.i (getString(R.string.app_name), url);
				}
				else
				{
					info_nodes = node.evaluateXPath(COVER_XPATH);
					
					if (info_nodes.length > 0) {
						String url = ((TagNode)info_nodes[0]).getAttributeByName("src");
						img = CustomHttpClient.downloadFile(url);
						params[0].setImage(img);
						Log.i (getString(R.string.app_name), url);
					}
				}
				
				Log.i (getString(R.string.app_name), "End Parsing Cover");
				
			} catch (XPatherException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			} 
			return params[0];
		}
		
		@Override
		protected void onPreExecute (){
			showDialog();
		}
		
		@Override
		protected void onPostExecute(VolumeItem value){
			dismissDialog();
			
			showImage(value.getImage(), value.getNom());
			
		}
		
	}
	
	private class processPlanningTask extends AsyncTask<String, Void, Void>{

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
				Log.i (getString(R.string.app_name), "Start Parsing Planning");
				node = cleaner.clean(CustomHttpClient.executeHttpGet(params[0], "UTF-8"));
				
				
				Object[] info_nodes;
				
				VolumeItem item;
				
				user.setPlanning(new VolumeAdapter(PlanningActivity.this,  R.layout.volume_listviewline));
				
				String PLANNING_XPATH = "//table[@class='collection']//tr//td//input[@value]";
				
				/*
				*<table class="collection">
				*	<th width="20"></th>
				*	<th width="80"><b>Date</b></th>
				*	<th width="300"><b>Nom</b></th>
				*	<th width="120"><b>Editeur</b></th>
				*	<tr class="ligne_pair">
				*		<td colspan="4" style="font-size:14px;font-weight:bold;"><br>Mars</td>
				*	</tr>
				*	
				*	<tr class="ligne_pair">
				*		<td><input type="checkbox" name="volume_54947" value="54947"></td>
				*		<td>14/03/2012</td>
				*		<td><a href="http://www.manga-sanctuary.com/manga-f-compo-vol-10-reedition-francaise-s78-p54947.html">F.Compo volume 10</a></td>
				*		<td><a href="http://www.manga-sanctuary.com/fiche_editeur.php?id=10">Panini manga</a></td>
				*	</tr>
				*	<tr class="ligne_impair">
				*		<td><input type="checkbox" name="volume_54956" value="54956"></td>
				*		<td>14/03/2012</td>
				*		<td><a href="http://www.manga-sanctuary.com/manga-saint-seiya-episode-g-vol-18-simple-s1412-p54956.html">Saint Seiya Episode G volume 18</a></td>
				*		<td><a href="http://www.manga-sanctuary.com/fiche_editeur.php?id=10">Panini manga</a></td>
				*	</tr>
				*</table>
				*/
				
				info_nodes = node.evaluateXPath(PLANNING_XPATH);
				
				if (info_nodes.length > 0) {
					int i = 0;
					while (i < info_nodes.length) {
						item = new VolumeItem();
						
						SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
						item.setPlanningDate(simpleDateFormat.parse(((TagNode)info_nodes[i]).getParent().getParent().getChildTags()[1].getText().toString()));
						
						item.setNom(((TagNode)info_nodes[i]).getParent().getParent().getChildTags()[2].getChildTags()[0].getText().toString());
						item.setURL(((TagNode)info_nodes[i]).getParent().getParent().getChildTags()[2].getChildTags()[0].getAttributeByName("href"));
						item.setEditeur(((TagNode)info_nodes[i]).getParent().getParent().getChildTags()[3].getChildTags()[0].getText().toString());
						
						Log.i (getString(R.string.app_name), item.getNom());
						
						user.getPlanning().add(item);
						i++;
					}
				}
				
				Log.i (getString(R.string.app_name), "End Parsing Planning");
				
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
			lstPlanning.setAdapter(user.getPlanning());
			
			dismissDialog();
		}
	}
	
}
