package com.mangasanctuary.mobile.Models;

import java.util.Hashtable;

import android.graphics.Bitmap;

import com.mangasanctuary.mobile.Adapters.SerieAdapter;
import com.mangasanctuary.mobile.Adapters.VolumeAdapter;

public final class User {
	private String _ID;
	private String _SID;
	private String _avatarUrl;
	private String _name;
	private Bitmap _avatar;
	private SerieAdapter _booksCollection;
	private SerieAdapter _dvdCollection;
	private VolumeAdapter _planning;
	private Hashtable<String, VolumeAdapter> _fullplanning;
	private VolumeAdapter _missing;
	
	private VolumeItem _currentVolume;
	private SerieAdapter _currentCollection;
	
	private static User _instance; 
	
	private User()
	{
		_fullplanning = new Hashtable<String, VolumeAdapter>();
	}
	
	public static User getInstance ()
	{
		if (_instance == null)
			_instance = new User();
		
		return _instance;
	}
	
	public void Reset(){
		_avatarUrl = null;
		_ID = null;
		_name = null;
		_SID = null;
		_avatar = null;
		_booksCollection = null;
		_dvdCollection = null;
		_planning = null;
		_fullplanning = new Hashtable<String, VolumeAdapter>();
		_missing = null;
	}
	
	public Bitmap getAvatar (){
		return _avatar;
	}
	
	public void setAvatar (Bitmap value){
		_avatar = value;
	}
	
	public String getName (){
		return _name;
	}
	
	public void setName (String value){
		_name = value;
	}	
	
	public String getAvatarURL (){
		return _avatarUrl;
	}
	
	public void setAvatarURL (String value){
		_avatarUrl = value;
	}
	
	public String getID (){
		return _ID;
	}
	
	public void setID (String value){
		_ID = value;
	}
	
	public String getSID(){
		return _SID;
	}
	
	public void setSID (String value){
		_SID = value;
	}
	
	public SerieAdapter getBooksCollection(){
		_currentCollection = _booksCollection;
		return _booksCollection;
	}
	
	public void setBooksCollection (SerieAdapter value){
		_currentCollection = value;
		_booksCollection = value;
	}
	
	public SerieAdapter getDVDCollection(){
		_currentCollection = _dvdCollection;
		return _dvdCollection;
	}
	
	public void setDVDCollection (SerieAdapter value){
		_currentCollection = value;
		_dvdCollection = value;
	}
	
	public VolumeAdapter getPlanning(){
		return _planning;
	}
	
	public void setPlanning (VolumeAdapter value){
		_planning = value;
	}
	
	public VolumeAdapter getMissing(){
		return _missing;
	}
	
	public void setMissing (VolumeAdapter value){
		_missing = value;
	}
	
	public VolumeItem getCurrentVolume(){
		return _currentVolume;
	}
	
	public void setCurrentVolume(VolumeItem value){
		_currentVolume = value;
	}

	public SerieAdapter getCurrentCollection() {
		return _currentCollection;
	}

	public VolumeAdapter getFullPlanning(String key){
		if (_fullplanning.containsKey(key))
			return _fullplanning.get(key);	
		else
			return null;
	}
	
	public void setFullPlanning (String key, VolumeAdapter value){
		_fullplanning.put(key, value);
	}
}
