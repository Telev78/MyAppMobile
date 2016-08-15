package com.mangasanctuary.mobile.Activities;

import java.text.SimpleDateFormat;
import java.util.List;

import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.ContentNode;
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
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mangasanctuary.mobile.Service.CustomHttpClient;
import com.mangasanctuary.mobile.R;
import com.mangasanctuary.mobile.Models.User;
import com.mangasanctuary.mobile.Models.VolumeItem;

public class DVDDetailActivity extends Activity {

	private User user = User.getInstance();
	private static ProgressDialog dialog;
	@SuppressWarnings("rawtypes")
	private AsyncTask T;
	
	@SuppressWarnings("unchecked")
	@Override
    public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.dvddetail);
	    
	    VolumeItem volume = user.getCurrentVolume();
	    
	    // Check if the thread is already running
		T = (AsyncTask<String, Void, Void>) getLastNonConfigurationInstance();
		
		if (T != null && T.getStatus() == AsyncTask.Status.RUNNING) 
			showDialog();
		else if (volume != null){
	    	if (volume.isFullLoaded())
	    		populateData(volume);
	    	else 
				T = new processDVDDetailTask().execute(volume.getURL());
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
	
	private void populateData(final VolumeItem volume){
		TextView txtTitreComplet = (TextView)findViewById(R.id.txtDvdDetailFullTitre);
		txtTitreComplet.setText(volume.getNomComplet());
		
		TextView txtTitre = (TextView)findViewById(R.id.txtDvdDetailTitre);
		txtTitre.setText(volume.getNom());
		
		TextView txtTitreOriginal = (TextView)findViewById(R.id.txtDvdDetailTitreOriginal);
		txtTitreOriginal.setText(volume.getNomOriginal());
		
		if (volume.getParution() != null){
			TextView txtParution = (TextView)findViewById(R.id.txtDvdDetailParution);
			txtParution.setText((new SimpleDateFormat("dd/MM/yyyy")).format(volume.getParution()));
		}
		
		TextView txtEditeur = (TextView)findViewById(R.id.txtDvdDetailEditeur);
		txtEditeur.setText(volume.getEditeur());
		
		TextView txtType = (TextView)findViewById(R.id.txtDvdDetailType);
		txtType.setText(volume.getType());
		
		TextView txtCategory = (TextView)findViewById(R.id.txtDvdDetailCategory);
		txtCategory.setText(volume.getCategories());
		
		TextView txtCollection = (TextView)findViewById(R.id.txtDvdDetailCollection);
		txtCollection.setText(volume.getCollection());
		
		TextView txtGenres = (TextView)findViewById(R.id.txtDvdDetailGenres);
		txtGenres.setText(volume.getGenres());
		
		TextView txtPrix = (TextView)findViewById(R.id.txtDvdDetailPrix);
		txtPrix.setText(volume.getPrixEditeur());
		
		TextView txtEAN = (TextView)findViewById(R.id.txtDvdDetailEAN);
		txtEAN.setText(volume.getEAN());
		
		TextView txtPublic = (TextView)findViewById(R.id.txtDvdDetailPublic);
		txtPublic.setText(volume.getPublic());
			
		ImageView image = (ImageView) findViewById(R.id.dvddetail_img);
        
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
        	String defaultCouv = getString(R.string.defaultcover);
        	int rid = getResources().getIdentifier(defaultCouv, null, null);
        	image.setImageResource(rid);
        	image.setClickable(false);
        }
        
        TextView txtSynopsis = (TextView)findViewById(R.id.txtDvdDetailSynopsis);
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
	
	private class processDVDDetailTask extends AsyncTask<String, Void, Void>{

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
				Log.i (getString(R.string.app_name), "Start Parsing DVD Detail");
				node = cleaner.clean(CustomHttpClient.executeHttpGet(params[0], "UTF-8"));
				
				Object[] info_nodes;
				
				VolumeItem item = user.getCurrentVolume();
				
				String COVER_XPATH_BIG = "//div[@id='infos_generales_gauche']//div[@id='menu_fiche']//div[@id='image_serie']//a[@href]";
				String COVER_XPATH = "//div[@id='infos_generales_gauche']//div[@id='menu_fiche']//div[@id='image_serie']//img[@src]";
				String VOLUME_DETAIL_XPATH = "//div[@id='infos_generales_droite']//div[@id='infos']/dd";
				String SYNOPSIS_XPATH = "//div[@id='infos_generales']/div[@id='infos_generales_droite']/div[@id='fiche_infos_supp']/div[@id='synopsis']/p";
				String FULL_TITLE_XPATH = "//div[@id='contenu']/h1";
				String EDITEUR_XPATH = "//div[@id='contenu']/div[@id='infos_generales']/div[@id='infos_generales_droite']/div[@id='infos']/div[@style]//b";
				String ORIGINAL_TITLE_XPATH = "//div[@id='contenu']/div[@style]";
				
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
				
				if (info_nodes.length > 0) {
					//item.setNom(((TagNode)info_nodes[1]).getText().toString());
					item.setType(((TagNode)info_nodes[2]).getChildTags()[0].getText().toString());
					item.setCategories(((TagNode)info_nodes[3]).getText().toString());
					item.setCollection(((TagNode)info_nodes[4]).getText().toString());
					item.setGenres(((TagNode)info_nodes[5]).getText().toString());
					item.setPrixEditeur(((TagNode)info_nodes[6]).getText().toString());
					item.setEAN(((TagNode)info_nodes[7]).getText().toString());
					item.setPublic(((TagNode)info_nodes[8]).getText().toString());
					
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
				
				Log.i (getString(R.string.app_name), "End DVD Detail");
				
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
