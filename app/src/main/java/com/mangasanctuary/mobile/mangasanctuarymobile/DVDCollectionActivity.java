package com.mangasanctuary.mobile.mangasanctuarymobile;

import android.content.Intent;
import android.os.Bundle;

public class DVDCollectionActivity extends CollectionActivity {

	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);   
	}

	@Override
	protected SerieAdapter getCollection() {
		return _user.getDVDCollection();
	}

	@Override
	protected void setCollection(SerieAdapter value) {
		_user.setDVDCollection(value);		
	}

	@Override
	protected String getCollectionURL() {
		String collectionURL = getString(R.string.URL) + getString(R.string.dvd_collectionURL);
    	return collectionURL.replace("[IDUSER]", _user.getID());
	}

	@Override
	protected void CollectionItemClick(int pos) {
		Intent i = new Intent(DVDCollectionActivity.this, DVDCollectionDetailActivity.class);
		i.putExtra("com.mangasanctuary.mobile.posSerieItem", pos);
		startActivity(i);
	}
	
	@Override
	protected void onResume (){
		super.onResume();
		_user.getDVDCollection();
	}
	
}
