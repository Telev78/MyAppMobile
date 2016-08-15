package com.mangasanctuary.mobile.Activities;

import java.text.SimpleDateFormat;

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
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import com.mangasanctuary.mobile.Service.CustomHttpClient;
import com.mangasanctuary.mobile.R;
import com.mangasanctuary.mobile.Models.Serie;
import com.mangasanctuary.mobile.Adapters.SerieDetailAdapter;
import com.mangasanctuary.mobile.Models.User;
import com.mangasanctuary.mobile.Models.VolumeItem;

public abstract class CollectionDetailActivity extends Activity {
	
	protected abstract void onItemSelected ();
	
	private static ProgressDialog dialog;
	User user = User.getInstance();
	private Serie serie = null;
	@SuppressWarnings("rawtypes")
	private AsyncTask T;
	private ListView lstVolumesDetail;
	
	@SuppressWarnings({ "unchecked" })
    public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.volumes);
	    
	    lstVolumesDetail = (ListView) findViewById(R.id.lst_volumes);
	    lstVolumesDetail.setOnItemClickListener(lstitemOnClick);
	    
	    int pos = this.getIntent().getIntExtra("com.mangasanctuary.mobile.posSerieItem", -1);
	    
	    if (pos != -1)
	    	serie = (Serie)user.getCurrentCollection().getItem(pos);
	    
   
    	// Check if the thread is already running
		T = (AsyncTask<String, Void, Void>) getLastNonConfigurationInstance();
		if (T != null && T.getStatus() == AsyncTask.Status.RUNNING) {
			showDialog();
		}
		else
		{
			if (serie != null) {
				if (serie.getDetail() == null)
					
					T = new processCollectionDetailTask().execute(serie.getURL());
					
				else{
					lstVolumesDetail.setAdapter(serie.getDetail());
				}
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
			user.setCurrentVolume(item);
			
			onItemSelected ();
			/*
			Intent i = new Intent(CollectionDetailActivity.this, VolumeDetailActivity.class);
									
			startActivity(i);
			*/
		}
	};
	
	private class processCollectionDetailTask extends AsyncTask<String, Void, Void>{

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
				Log.i (getString(R.string.app_name), "Start Parsing Collection Detail");
				node = cleaner.clean(CustomHttpClient.executeHttpGet(params[0], "UTF-8"));
							
				Object[] info_nodes;
				
				VolumeItem item;
				
				serie.setDetail(new SerieDetailAdapter(CollectionDetailActivity.this, R.layout.seriedetail_listviewline));
				
				String COLLECTION_DETAIL_XPATH = "//div[@id='liste_volumes_collection']//form[@name='form_volumes']//ul";
				String COLLECTION_VOL_TITLE_XPATH = "//span[@class='titre_encart_bleu']";
				String COLLECTION_VOL_DETAIL_XPATH = "//li";
				
				/*
				<div id="liste_volumes_collection" style="margin-top:10px;background-color:#FFFFFF;padding:10px;">
				  	<div class="spacer"></div>
					<form name="actions_ajax" id="actions_ajax">
						<input type="hidden" id="nom_form" value="form_volumes">
						<input type="hidden" id="user_id" value="29830">
						<input type="hidden" id="id_edition" value="13012">
						<input type="hidden" id="type_liste" value="">
						<input type="hidden" id="type_collec" value="ms">
						<input type="hidden" id="table" value="ms8.COLLECTION">
						<input type="hidden" id="page" value="liste_volumes">
					</form>
					<div style="text-align:center;margin-top:10px;">
						<div style="width:50%;margin:0 auto;">
							<span class="bouton" onclick="afficherPopup('29','ajout_info','500','0','10317209');">Ajouter un champ personnalis�</span> 
							<span class="bouton" onclick="afficherPopup('30','modif_champ_perso','500','0','10317209');">G�rer les champs</span>
						</div>
					</div>
					<div class="spacer"></div>
					<form action="/collection.php?id=29830&page=action_volumes&type_action=2&id_serie=51838" method="POST" name="form_volumes">
						<ul style="list-style-type:none;width:700px;">
							<span class="titre_encart_bleu" style="width:694px;">
								<input type="checkbox" id="vol_51838" name="vol_51838" value="51838">
								<a href="http://www.manga-sanctuary.com/manga-billy-bat-vol-1-simple-s7005-p51838.html">Billy Bat # 1</a>
							</span>
							<li style="background-color:#FFFFFF;margin-bottom:10px;border:1px solid #E36F0A;clear:both;">
								<a href="http://www.manga-sanctuary.com/manga-billy-bat-vol-1-simple-s7005-p51838.html">
									<img class="visuel_detail" src="http://img.manga-sanctuary.com/billy-bat-manga-volume-1-simple-51838.jpg" border="0">
								</a><br> 
								<div style="float:left;width:540px;">
									<span class="titre_liste_volumes">A vendre : </span>
									<span id="vente_51838">NON</span> 
									<span style="cursor:pointer;" onclick="afficherModifChampTome('vente_51838','10317209','vente');" id="icone_10317209">
										<img src="/design/img/database_edit.png"></span>
										<br>
									<span class="titre_liste_volumes">Pr�t� : </span>
									<span id="pret_51838">NON</span> 
									<span style="cursor:pointer;" onclick="afficherModifChampTome('pret_51838','10317209','pret');">
										<img src="/design/img/database_edit.png"></span>
										<br>
									<span class="titre_liste_volumes">Date achat : </span>
									<span id="date_achat_51838">16/03/2012</span>
									<span style="cursor:pointer;" onclick="afficherModifChampTome('date_achat_51838','10317209','date_achat');">
										<img src="/design/img/database_edit.png">
									</span>
									<br>
									<span class="titre_liste_volumes">Lieu d'achat : </span>
									<span id="lieu_achat_51838">fanfula (0)</span> 
									<span style="cursor:pointer;" onclick="afficherModifChampTome('lieu_achat_51838','10317209','lieu_achat');">
										<img src="/design/img/database_edit.png">
									</span>
									<br>
									<span class="titre_liste_volumes">Prix d'achat : </span>
									<span id="prix_achat_51838">7.9� (neuf)</span> 
									<span style="cursor:pointer;" onclick="afficherModifChampTome('prix_achat_51838','10317209','prix_achat');">
										<img src="/design/img/database_edit.png">
									</span>
									<br>
									<span class="titre_liste_volumes">D�dicace : </span>NON - 
									<span class="bouton" style="cursor:pointer;" onclick="afficherPopup('1','dedicace','500','0','51838');"> Ajouter</span> 
								</div>
								<div class="spacer" style="height:20px;text-align:right;">
									<span style="cursor:pointer;" onclick="document.getElementById('vol_51838').checked=true;afficherPopup(1,'action_volumes',500);">
										<img src="/collection/images/supprimer.png" border="0"> Supprimer ce tome
									</span>
								</div>
							</li>
							
							<span class="titre_encart_bleu" style="width:694px;">
								<input type="checkbox" id="vol_51839" name="vol_51839" value="51839">
								<a href="http://www.manga-sanctuary.com/manga-billy-bat-vol-2-simple-s7005-p51839.html">Billy Bat # 2</a>
							</span>
							<li style="background-color:#FFFFFF;margin-bottom:10px;border:1px solid #E36F0A;clear:both;">
								<a href="http://www.manga-sanctuary.com/manga-billy-bat-vol-2-simple-s7005-p51839.html">
									<img class="visuel_detail" src="http://img.manga-sanctuary.com/billy-bat-manga-volume-2-simple-51839.jpg" border="0">
								</a><br> 
								<div style="float:left;width:540px;">
									<span class="titre_liste_volumes">A vendre : </span>
									<span id="vente_51839">NON</span> 
									<span style="cursor:pointer;" onclick="afficherModifChampTome('vente_51839','10317210','vente');" id="icone_10317210">
										<img src="/design/img/database_edit.png">
									</span>
									<br>
									<span class="titre_liste_volumes">Pr�t� : </span>
									<span id="pret_51839">NON</span> 
									<span style="cursor:pointer;" onclick="afficherModifChampTome('pret_51839','10317210','pret');">
										<img src="/design/img/database_edit.png">
									</span>
									<br>
									<span class="titre_liste_volumes">Date achat : </span>
									<span id="date_achat_51839">16/03/2012</span> 
									<span style="cursor:pointer;" onclick="afficherModifChampTome('date_achat_51839','10317210','date_achat');">
										<img src="/design/img/database_edit.png">
									</span>
									<br>
									<span class="titre_liste_volumes">Lieu d'achat : </span>
									<span id="lieu_achat_51839">fanfula (0)</span> 
									<span style="cursor:pointer;" onclick="afficherModifChampTome('lieu_achat_51839','10317210','lieu_achat');">
										<img src="/design/img/database_edit.png">
									</span>
									<br>
									<span class="titre_liste_volumes">Prix d'achat : </span>
									<span id="prix_achat_51839">7.9� (neuf)</span> 
									<span style="cursor:pointer;" onclick="afficherModifChampTome('prix_achat_51839','10317210','prix_achat');">
										<img src="/design/img/database_edit.png">
									</span>
									<br>
									<span class="titre_liste_volumes">D�dicace : </span>NON - 
									<span class="bouton" style="cursor:pointer;" onclick="afficherPopup('1','dedicace','500','0','51839');"> Ajouter</span> 
								</div>
								<div class="spacer" style="height:20px;text-align:right;">
									<span style="cursor:pointer;" onclick="document.getElementById('vol_51839').checked=true;afficherPopup(1,'action_volumes',500);">
										<img src="/collection/images/supprimer.png" border="0"> Supprimer ce tome
									</span>
								</div>
							</li>
						</ul>
					</form><br>
					<input type="checkbox" onmouseup="if(this.checked==true){decocher('form_volumes');}else{cocher('form_volumes');}">
					<span style="color:#E36F0A;font-weight:bold;">Cocher/d�cocher</span><br><br>
					<div style="text-align:center;">
						<span style="float:left;font-weight:bold;margin-right:5px;">Pour la s�lection : </span> 
						<span class="bouton" onclick="afficherPopup(1,'action_volumes',500,0);" style="cursor:pointer;">Supprimer</span> 
						<span class="bouton" onclick="afficherPopup(2,'action_volumes',500,0);" style="cursor:pointer;">Vendre</span> 
						<span class="bouton" onclick="afficherPopup(3,'action_volumes',500,0);" style="cursor:pointer;">Retirer de la vente</span> 
						<span class="bouton" onclick="afficherPopup(4,'action_volumes',500,0);" style="cursor:pointer;">Pr�ter</span> 
						<span class="bouton" onclick="afficherPopup(5,'action_volumes',500,0);" style="cursor:pointer;">Restituer</span> 
						<span class="bouton" onclick="afficherPopup(6,'action_volumes',520,0);" style="cursor:pointer;">Date d'achat</span> 
						<span class="bouton" onclick="afficherPopup(7,'action_volumes',500,0);" style="cursor:pointer;">Lieu d'achat</span> 
						<span class="bouton" onclick="afficherPopup(10,'action_volumes',500,0);" style="cursor:pointer;">Achet� d'occasion</span> 
					</div>  
				</div>
				*/
				
				info_nodes = node.evaluateXPath(COLLECTION_DETAIL_XPATH);
				
				if (info_nodes.length > 0) {
					TagNode info_node = (TagNode) info_nodes[0];
					
					Object[] title_nodes = info_node.evaluateXPath(COLLECTION_VOL_TITLE_XPATH);
					Object[] details_nodes = info_node.evaluateXPath(COLLECTION_VOL_DETAIL_XPATH);
					
					int i = 0;
					while (i < title_nodes.length) {
						item = new VolumeItem();
						item.setNom(((TagNode)title_nodes[i]).getChildTags()[1].getText().toString());
						item.setURL(((TagNode)title_nodes[i]).getChildTags()[1].getAttributeByName("href").toString());
						item.setID(((TagNode)title_nodes[i]).getChildTags()[0].getAttributeByName("value").toString());
						
						item.setImageURL(((TagNode)details_nodes[i]).getChildTags()[0].getChildTags()[0].getAttributeByName("src").toString());
						item.setImage(CustomHttpClient.downloadFile(item.getImageURL()));
						
						item.setLieuAchat(((TagNode)details_nodes[i]).getChildTags()[2].getChildTags()[13].getText().toString());
						item.setPrixAchat(((TagNode)details_nodes[i]).getChildTags()[2].getChildTags()[17].getText().toString());
						
						SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
						item.setDateAchat(simpleDateFormat.parse(((TagNode)details_nodes[i]).getChildTags()[2].getChildTags()[9].getText().toString()));
									
						Log.i (getString(R.string.app_name), item.getNom());
						
						serie.getDetail().add(item);
						
						i++;
					}
					
					
				}
				
				Log.i (getString(R.string.app_name), "End Collection Detail");
				
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
			lstVolumesDetail.setAdapter(serie.getDetail());
			
			dismissDialog();
		}
	}
	
	
}
