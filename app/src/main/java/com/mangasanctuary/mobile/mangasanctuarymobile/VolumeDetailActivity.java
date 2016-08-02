package com.mangasanctuary.mobile.mangasanctuarymobile;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.ContentNode;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.XPatherException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class VolumeDetailActivity extends Activity {
	
	private User user = User.getInstance();
	private static ProgressDialog dialog;
	@SuppressWarnings("rawtypes")
	private AsyncTask T;
	
	@SuppressWarnings("unchecked")
	@Override
    public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.volumedetail);
	    
	    VolumeItem volume = user.getCurrentVolume();
	    
	    // Check if the thread is already running
		T = (AsyncTask<String, Void, Void>) getLastNonConfigurationInstance();
		
		if (T != null && T.getStatus() == AsyncTask.Status.RUNNING) 
			showDialog();
		else if (volume != null){
	    	if (volume.isFullLoaded())
	    		populateData(volume);
	    	else 
				T = new processVolumeDetailTask().execute(volume.getURL());
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
	
	@SuppressLint("SimpleDateFormat")
	private void populateData(final VolumeItem volume){
		TextView txtTitreComplet = (TextView)findViewById(R.id.txtVolDetailFullTitre);
		txtTitreComplet.setText(volume.getNomComplet());
		
		TextView txtTitre = (TextView)findViewById(R.id.txtVolDetailTitre);
		txtTitre.setText(volume.getNom());
		
		TextView txtTitreOriginal = (TextView)findViewById(R.id.txtVolDetailTitreOriginal);
		txtTitreOriginal.setText(volume.getNomOriginal());
		
		if (volume.getParution() != null){
			TextView txtParution = (TextView)findViewById(R.id.txtVolDetailParution);
			txtParution.setText((new SimpleDateFormat("dd/MM/yyyy")).format(volume.getParution()));
		}
		
		TextView txtEditeur = (TextView)findViewById(R.id.txtVolDetailEditeur);
		txtEditeur.setText(volume.getEditeur());
		
		TextView txtType = (TextView)findViewById(R.id.txtVolDetailType);
		txtType.setText(volume.getType());
		
		TextView txtCategory = (TextView)findViewById(R.id.txtVolDetailCategory);
		txtCategory.setText(volume.getCategories());
		
		TextView txtMagPrepub = (TextView)findViewById(R.id.txtVolDetailMagPrepub);
		txtMagPrepub.setText(volume.getMagPrepub());
		
		TextView txtCollection = (TextView)findViewById(R.id.txtVolDetailCollection);
		txtCollection.setText(volume.getCollection());
		
		TextView txtGenres = (TextView)findViewById(R.id.txtVolDetailGenres);
		txtGenres.setText(volume.getGenres());
		
		TextView txtPages = (TextView)findViewById(R.id.txtVolDetailPages);
		txtPages.setText(volume.getPages());
		
		TextView txtFormat = (TextView)findViewById(R.id.txtVolDetailFormat);
		txtFormat.setText(volume.getFormat());
		
		TextView txtColorisation = (TextView)findViewById(R.id.txtVolDetailColorisation);
		txtColorisation.setText(volume.getColorisation());
		
		TextView txtPrix = (TextView)findViewById(R.id.txtVolDetailPrix);
		txtPrix.setText(volume.getPrixEditeur());
		
		TextView txtEAN = (TextView)findViewById(R.id.txtVolDetailEAN);
		txtEAN.setText(volume.getEAN());
		
		TextView txtPublic = (TextView)findViewById(R.id.txtVolDetailPublic);
		txtPublic.setText(volume.getPublic());
			
		ImageView image = (ImageView) findViewById(R.id.volumedetail_img);
        
        if (volume.getImage() != null){
        	image.setImageBitmap(volume.getImage());
        
        	image.setClickable(true);
        	image.setOnClickListener(new View.OnClickListener() {
				
				public void onClick(View v) {
					showImage(volume.getImage(), volume.getNomComplet());
					
				}
			});
        }
        else{
        	String defaultCouv = getClass().getPackage().getName() + ":drawable/couv";
        	int rid = getResources().getIdentifier(defaultCouv, null, null);
        	image.setImageResource(rid);
        	image.setClickable(false);
        }
        
        TextView txtSynopsis = (TextView)findViewById(R.id.txtVolDetailSynopsis);
        txtSynopsis.setText(volume.getSynopsis());
	}
	
	private void showImage (Bitmap img, String titre){
		AlertDialog.Builder imageDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);

        View layout = inflater.inflate(R.layout.custom_fullimage_dialog,
                (ViewGroup) findViewById(R.id.layout_root));
        ImageView image = (ImageView) layout.findViewById(R.id.fullimage);
        
        if (img != null)
        	image.setImageBitmap(img);
        else{
        	String defaultCouv = getClass().getPackage().getName() + ":drawable/couv";
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


	private class processVolumeDetailTask extends AsyncTask<String, Void, Void>{

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
				Log.i (getString(R.string.app_name), "Start Parsing Volume Detail");
				node = cleaner.clean(CustomHttpClient.executeHttpGet(params[0], "UTF-8"));
				
				Object[] info_nodes;
				
				VolumeItem item = user.getCurrentVolume();

				String ADULT_XPATH = "//input[@name='adult']";
				String AdultHTML;
				info_nodes = node.evaluateXPath(ADULT_XPATH);
				if (info_nodes.length > 0)
				{	// Adult volume
					ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
					postParameters.add(new BasicNameValuePair("adult","1"));
					String url = ((TagNode)info_nodes[0]).getParent().getAttributeByName("action");
					AdultHTML = CustomHttpClient.executeHttpPost(getString(R.string.URL) +  url, postParameters);

					Log.i (getString(R.string.app_name), AdultHTML);

					//node = cleaner.clean(AdultHTML);
					node = cleaner.clean(CustomHttpClient.executeHttpGet(params[0], "UTF-8"));
				}

				
				String COVER_XPATH_BIG = "//div[@id='infos_generales_gauche']//div[@id='menu_fiche']//div[@id='image_serie']//a[@href]";
				String COVER_XPATH = "//div[@id='infos_generales_gauche']//div[@id='menu_fiche']//div[@id='image_serie']//img[@src]";
				String VOLUME_DETAIL_XPATH = "//div[@id='infos_generales_droite']//div[@id='infos']/dd";
				String VOLUME_DETAIL_EXCLUDE_XPATH = "//div[@id='infos_generales_droite']//div[@id='infos']/dt";
				String SYNOPSIS_XPATH = "//div[@id='infos_generales']/div[@id='infos_generales_droite']/div[@id='fiche_infos_supp']/div[@id='synopsis']/p";
				String FULL_TITLE_XPATH = "//div[@id='contenu']/h1";
				String ORIGINAL_TITLE_XPATH = "//div[@id='contenu']/div[@style]";
				String EDITEUR_XPATH = "//div[@id='contenu']/div[@id='infos_generales']/div[@id='infos_generales_droite']/div[@id='infos']/div[@style]//b";
				
				info_nodes = node.evaluateXPath(FULL_TITLE_XPATH);
				
				if (info_nodes.length > 0) {
					item.setNomComplet(((TagNode)info_nodes[0]).getText().toString());
				}
				
				info_nodes = node.evaluateXPath(ORIGINAL_TITLE_XPATH);
				
				if (info_nodes.length > 0) {
					//item.setNomOriginal(((TagNode)info_nodes[0]).getText().toString());
					String titre = "";
					int i = 0, j = 0;
					while (i < ((TagNode)info_nodes[0]).getChildren().size()){
						Object o = ((TagNode)info_nodes[0]).getChildren().get(i);
						if (o instanceof ContentNode && o.toString().trim().toCharArray().length != 0){
							if (j != 0) titre += "\n";
							titre += Html.fromHtml(o.toString()).toString();
							
							j++;
						}
						i++;
					}
					
					item.setNomOriginal(titre);
				}
				
				info_nodes = node.evaluateXPath(COVER_XPATH_BIG);
				if (info_nodes.length > 0)
				{
					String url = ((TagNode)info_nodes[0]).getAttributeByName("href");
					item.setImageURL(url);
					item.setImage(CustomHttpClient.downloadFile(url));
					Log.i (getString(R.string.app_name), url);
				}
				else
				{
					info_nodes = node.evaluateXPath(COVER_XPATH);
					
					if (info_nodes.length > 0) {
						String url = ((TagNode)info_nodes[0]).getAttributeByName("src");
						item.setImageURL(url);
						item.setImage(CustomHttpClient.downloadFile(url));
						Log.i (getString(R.string.app_name), url);
					}
				}
								
				info_nodes = node.evaluateXPath(VOLUME_DETAIL_XPATH);
				int ex = 6;
				Object[] info_nodes_exclude = node.evaluateXPath(VOLUME_DETAIL_EXCLUDE_XPATH);
				if (((TagNode)info_nodes_exclude[6]).getText().toString().toLowerCase().equals("thématiques :"))
				{
					ex+=1;
				}
				
				if (info_nodes.length > 0) {
					//item.setNom(((TagNode)info_nodes[0]).getText().toString());
					item.setType(((TagNode)info_nodes[1]).getChildTags()[0].getText().toString());
					item.setCategories(((TagNode)info_nodes[2]).getText().toString());
					item.setMagPrepub(((TagNode)info_nodes[3]).getText().toString());
					item.setCollection(((TagNode)info_nodes[4]).getText().toString());
					item.setGenres(((TagNode)info_nodes[5]).getText().toString());
					item.setPages(((TagNode)info_nodes[ex]).getText().toString());
					item.setFormat(((TagNode)info_nodes[ex+1]).getText().toString());
					item.setColorisation(((TagNode)info_nodes[ex+2]).getText().toString());
					item.setPrixEditeur(((TagNode)info_nodes[ex+3]).getText().toString());
					item.setEAN(((TagNode)info_nodes[ex+5]).getText().toString());
					item.setPublic(((TagNode)info_nodes[ex+6]).getText().toString());
					
				}
				
				info_nodes = node.evaluateXPath(SYNOPSIS_XPATH);
				
				if (info_nodes.length > 0 && ((TagNode)info_nodes[0]).getChildren().size() > 1) {
					@SuppressWarnings("rawtypes")
					List nodes = ((TagNode)info_nodes[0]).getChildren();
					int i = 1;
					String synopsis = "";
					while (i < nodes.size()){
						Object o = nodes.get(i);
						if (o instanceof ContentNode)
							synopsis += o.toString();
						
						i++;
					}
						
					item.setSynopsis(synopsis);
				}
				
				info_nodes = node.evaluateXPath(EDITEUR_XPATH);
				
				if (info_nodes.length == 2) {
					SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
					item.setParution(simpleDateFormat.parse(((TagNode)info_nodes[0]).getText().toString()));
					
					item.setEditeur(((TagNode)info_nodes[1]).getText().toString());
				}
				
				item.setFullLoaded();
				
				Log.i (getString(R.string.app_name), item.getNom());
				
				Log.i (getString(R.string.app_name), "End Volume Detail");
				
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
			populateData(user.getCurrentVolume());
			
			dismissDialog();
		}
	}

}
