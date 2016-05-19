package com.mangasanctuary.mobile.mangasanctuarymobile;

public final class Serie {
	private String _nom;
	private String _type;
	private String _editeur;
	private int _totalVolumes;
	private Statut _statut;
	private String _URL;
	private SerieDetailAdapter _detail;
	
	public Serie(){}
	
	public Serie (String nom, String type, String editeur, int totalVolumes, Statut statut){
		_nom = nom;
		_type = type;
		_editeur = editeur;
		_totalVolumes = totalVolumes;
		_statut = statut;
	}
	
	public void setNom (String value){
		_nom = value;
	}
	
	public String getNom (){
		return _nom;
	}
	
	public void setType (String value){
		_type = value;
	}
	
	public String getType (){
		return _type;
	}
	
	public void setEditeur(String value){
		_editeur = value;
	}
	
	public String getEditeur (){
		return _editeur;
	}
	
	public void setTotalVolumes (int value){
		_totalVolumes = value;
	}
	
	public int getTotalVolumes (){
		return _totalVolumes;
	}
	
	public void setStatut (Statut value){
		_statut = value;
	}
	
	public Statut getStatut (){
		return _statut;
	}
	
	public void setURL (String value){
		_URL = value;
	}
	
	public String getURL (){
		return _URL;
	}

	public SerieDetailAdapter getDetail() {
		return _detail;
	}

	public void setDetail(SerieDetailAdapter value) {
		this._detail = value;
	}
}
