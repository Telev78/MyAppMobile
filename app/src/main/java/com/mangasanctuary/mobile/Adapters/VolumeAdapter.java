package com.mangasanctuary.mobile.Adapters;

import java.text.SimpleDateFormat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mangasanctuary.mobile.R;
import com.mangasanctuary.mobile.Models.VolumeItem;

public class VolumeAdapter extends ArrayAdapter<VolumeItem> {

	public VolumeAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId);
	}
	
	public void updateData(){
		this.notifyDataSetChanged();
	}
	
	@Override 
	public View getView (int position, View convertView, ViewGroup parent)
	{
		View result = convertView;
		if (convertView == null)
			result = LayoutInflater.from(getContext()).inflate(R.layout.volume_listviewline, parent, false);
							
		VolumeItem item = getItem (position);
		TextView titre = (TextView)result.findViewById(R.id.ligneVolumeNom);
		titre.setText (item.getNom());
		
		TextView editeur = (TextView)result.findViewById(R.id.ligneVolumeEditeur);
		editeur.setText (item.getEditeur());
				
		TextView date = (TextView)result.findViewById(R.id.ligneVolumeDate);
		if (item.getPlanningDate()==null)
			date.setText(getContext().getString(R.string.NC));
		else
			date.setText ((new SimpleDateFormat("dd/MM/yyyy")).format(item.getPlanningDate()));
				
		return result;

	}
}
