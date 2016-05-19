package com.mangasanctuary.mobile.mangasanctuarymobile;

import android.content.Intent;
import android.os.Bundle;


public class BooksCollectionActivity extends CollectionActivity {

	@Override
    public void onCreate(Bundle savedInstanceState) {   	
		super.onCreate(savedInstanceState);
	}

	@Override
	protected SerieAdapter getCollection() {
		return _user.getBooksCollection();
	}

	@Override
	protected void setCollection(SerieAdapter value) {
		_user.setBooksCollection(value);		
	}

	@Override
	protected String getCollectionURL() {
		String collectionURL = getString(R.string.URL) + getString(R.string.books_collectionURL);
    	return collectionURL.replace("[IDUSER]", _user.getID());
	}
	
	@Override
	protected void CollectionItemClick(int pos) {
		Intent i = new Intent(BooksCollectionActivity.this, BooksCollectionDetailActivity.class);
		i.putExtra("com.mangasanctuary.mobile.posSerieItem", pos);
		startActivity(i);
	}
	
	@Override
	protected void onResume (){
		super.onResume();
		_user.getBooksCollection();
	}
}
