package com.mangasanctuary.mobile.mangasanctuarymobile;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

public class SerieAdapter extends ArrayAdapter<Serie> implements Filterable {

	private ArrayList<Serie> mItems = new ArrayList<Serie>();
	private ArrayList<Serie> mUnFilteredItems = new ArrayList<Serie>();
	
	private final Object mLock = new Object();
    private ItemsFilter mFilter;
    
	public SerieAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId);
	}
	
	@Override
	public void add (Serie item){
		super.add(item);
		mItems.add(item);
		mUnFilteredItems.add(item);
	}
	
	@Override
    public int getCount() {
        return mItems.size();
    }
    @Override
    public Serie getItem(int position) {
        return mItems.get(position);
    }
    @Override
    public int getPosition(Serie item) {
        return mItems.indexOf(item);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
	
    public Filter getFilter() {
        if (mFilter == null) {
            mFilter = new ItemsFilter();
        }
        return mFilter;
    }
    
	@Override 
	public View getView (int position, View convertView, ViewGroup parent)
	{
		View result = convertView;
		if (convertView == null)
			result = LayoutInflater.from(getContext()).inflate(R.layout.serie_listviewline, parent, false);
							
		Serie serie = getItem (position);
		TextView titre = (TextView)result.findViewById(R.id.ligneNom);
		titre.setText (serie.getNom());
		
		TextView editeur = (TextView)result.findViewById(R.id.ligneEditeur);
		editeur.setText (serie.getEditeur());
		
		TextView nbrVolume = (TextView)result.findViewById(R.id.ligneTotalVolumes);
		nbrVolume.setText (String.valueOf(serie.getTotalVolumes()) + " " + this.getContext().getString(R.string.vol));
		
		TextView type = (TextView)result.findViewById(R.id.ligneType);
		type.setText (serie.getType());
				
		ImageView img = (ImageView)result.findViewById(R.id.ligneFlagStatus);
		int rid = this.getContext().getResources().getIdentifier(serie.getStatut().getImageName(), null, null);
		img.setImageResource(rid);
		img.setContentDescription(serie.getStatut().toString());
		
		return result;

	}
	
	public void updateData() 
	{
		this.notifyDataSetChanged();
	}
	
	private class ItemsFilter extends Filter {

		@Override
		protected FilterResults performFiltering(CharSequence prefix) {
			
			// Initiate our results object
            FilterResults results = new FilterResults();
            
         // If the adapter array is empty, check the actual items array
            if (mItems == null) {
                synchronized (mLock) { // Notice the declaration above
                    mItems = new ArrayList<Serie>(mUnFilteredItems);
                }
            }
            
            // No prefix is sent to filter by so we're going to send back the original array
            if (prefix == null || prefix.length() == 0) {
                synchronized (mLock) {
                    results.values = mUnFilteredItems;
                    results.count = mUnFilteredItems.size();
                }
            } else {
                    // Compare lower case strings
                String prefixString = prefix.toString().toLowerCase();
                // Local to here so we're not changing actual array
                final ArrayList<Serie> items = mUnFilteredItems;
                final int count = items.size();
                final ArrayList<Serie> newItems = new ArrayList<Serie>(count);
                for (int i = 0; i < count; i++) {
                    final Serie item = items.get(i);
                    final String itemName = item.getStatut().toString().toLowerCase();
                    // First match against the whole, non-splitted value
                    if (itemName.startsWith(prefixString)) {
                        newItems.add(item);
                    } 
                }
                // Set and return
                results.values = newItems;
                results.count = newItems.size();
            }
            return results;
			
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void publishResults(CharSequence constraint,
				FilterResults results) {
			//noinspection unchecked
            mItems = (ArrayList<Serie>) results.values;
            // Let the adapter know about the updated list
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
			
		}
		
	}
	
}
