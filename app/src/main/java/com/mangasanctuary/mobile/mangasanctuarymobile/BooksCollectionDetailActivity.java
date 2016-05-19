package com.mangasanctuary.mobile.mangasanctuarymobile;

import android.content.Intent;
import android.os.Bundle;

public class BooksCollectionDetailActivity extends CollectionDetailActivity {

	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onItemSelected() {

		Intent i = new Intent(BooksCollectionDetailActivity.this, VolumeDetailActivity.class);
		
		startActivity(i);
		
	}

}
