package com.mangasanctuary.mobile.mangasanctuarymobile;

import java.text.SimpleDateFormat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SerieDetailAdapter extends ArrayAdapter<VolumeItem> {

	public SerieDetailAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId);
		
	}

	@Override 
	public View getView (int position, View convertView, ViewGroup parent)
	{
		View result = convertView;
		if (convertView == null)
			result = LayoutInflater.from(getContext()).inflate(R.layout.seriedetail_listviewline, parent, false);
							
		VolumeItem serie = getItem (position);
		TextView titre = (TextView)result.findViewById(R.id.ligneSerieDetailNom);
		titre.setText (serie.getNom());
		
		TextView lieu = (TextView)result.findViewById(R.id.ligneSerieDetailLieuAchat);
		lieu.setText (serie.getLieuAchat());
		
		TextView prix = (TextView)result.findViewById(R.id.ligneSerieDetailPrixAchat);
		prix.setText (serie.getPrixAchat());
		
		TextView date = (TextView)result.findViewById(R.id.ligneSerieDetailDateAchat);
		date.setText ((new SimpleDateFormat("dd/MM/yyyy")).format(serie.getDateAchat()));

		ImageView image = (ImageView) result.findViewById(R.id.ligneSerieDetail_img);
		if (serie.getImage() != null){
			image.setImageBitmap(serie.getImage());
		}
		else{
			String defaultCouv = getClass().getPackage().getName() + ":drawable/couv";
			int rid = super.getContext().getResources().getIdentifier(defaultCouv, null, null);
			image.setImageResource(rid);
			image.setClickable(false);
		}
						
		return result;

	}
	
	public void updateData() 
	{
		this.notifyDataSetChanged();
	}
	
}
