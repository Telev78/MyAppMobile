package com.mangasanctuary.mobile.Models;

import java.util.Date;

import android.graphics.Bitmap;

public class VolumeItem {
	
	private String _id;
	private String _nom;
	private String _nomComplet;
	private String _type;
	private String _categories;
	private String _magPrepub;
	private String _collection;
	private String _genres;
	private String _ean;
	private String _prixEditeur;
	private String _public;
	private String _editeur;
	private Date _planningDate;
	private Date _dateAchat;
	private String _lieuAchat;
	private String _prixAchat;
	private String _imageURL;
	private String _URL;
	private Bitmap _img;
	private String _synopsis;
	private Date _parution;
	private String _nomOriginal;
	private String _format;
	private String _colorisation;
	private String _pages;
	private String _nbrEpisodes;
	
	private Boolean _isFullLoaded = false;
	
	public VolumeItem () {}
	
	public String getID() {
		return _id;
	}

	public void setID(String value) {
		this._id = value;
	}

	public void setNom (String value){
		_nom = value;
	}
	
	public String getNom (){
		return _nom;
	}
	
	public void setEditeur(String value){
		_editeur = value;
	}
	
	public String getEditeur (){
		return _editeur;
	}
	
	public void setPlanningDate(Date value){
		_planningDate = value;
	}
	
	public Date getPlanningDate (){
		return _planningDate;
	}
	
	public String getImageURL() {
		return _imageURL;
	}

	public void setImageURL(String _imageURL) {
		this._imageURL = _imageURL;
	}

	public String getPrixAchat() {
		return _prixAchat;
	}

	public void setPrixAchat(String _prixAchat) {
		this._prixAchat = _prixAchat;
	}

	public String getLieuAchat() {
		return _lieuAchat;
	}

	public void setLieuAchat(String _lieuAchat) {
		this._lieuAchat = _lieuAchat;
	}

	public Date getDateAchat() {
		return _dateAchat;
	}

	public void setDateAchat(Date _dateAchat) {
		this._dateAchat = _dateAchat;
	}

	public void setURL (String value){
		_URL = value;
	}
	
	public String getURL (){
		return _URL;
	}
	
	public void setImage (Bitmap value){
		_img = value;
	}
	
	public Bitmap getImage (){
		return _img;
	}
	
	public void setFullLoaded(){
		_isFullLoaded = true;
	}
	
	public Boolean isFullLoaded(){
		return _isFullLoaded;
	}

	public String getNomComplet() {
		return _nomComplet;
	}

	public void setNomComplet(String _nomComplet) {
		this._nomComplet = _nomComplet;
	}

	public String getType() {
		return _type;
	}

	public void setType(String _type) {
		this._type = _type;
	}

	public String getCategories() {
		return _categories;
	}

	public void setCategories(String _categories) {
		this._categories = _categories;
	}

	public String getMagPrepub() {
		return _magPrepub;
	}

	public void setMagPrepub(String _magPrepub) {
		this._magPrepub = _magPrepub;
	}

	public String getCollection() {
		return _collection;
	}

	public void setCollection(String _collection) {
		this._collection = _collection;
	}

	public String getGenres() {
		return _genres;
	}

	public void setGenres(String _genres) {
		this._genres = _genres;
	}

	public String getEAN() {
		return _ean;
	}

	public void setEAN(String _ean) {
		this._ean = _ean;
	}

	public String getPrixEditeur() {
		return _prixEditeur;
	}

	public void setPrixEditeur(String _prixEditeur) {
		this._prixEditeur = _prixEditeur;
	}

	public String getPublic() {
		return _public;
	}

	public void setPublic(String _public) {
		this._public = _public;
	}

	public String getSynopsis() {
		return _synopsis;
	}

	public void setSynopsis(String _synopsis) {
		this._synopsis = _synopsis;
	}

	public Date getParution() {
		return _parution;
	}

	public void setParution(Date _parution) {
		this._parution = _parution;
	}

	public String getNomOriginal() {
		return _nomOriginal;
	}

	public void setNomOriginal(String _nomOriginal) {
		this._nomOriginal = _nomOriginal;
	}

	public String getFormat() {
		return _format;
	}

	public void setFormat(String _format) {
		this._format = _format;
	}

	public String getColorisation() {
		return _colorisation;
	}

	public void setColorisation(String _colorisation) {
		this._colorisation = _colorisation;
	}

	public String getPages() {
		return _pages;
	}

	public void setPages(String _pages) {
		this._pages = _pages;
	}

	public String getNbrEpisodes() {
		return _nbrEpisodes;
	}

	public void setNbrEpisodes(String _nbrEpisodes) {
		this._nbrEpisodes = _nbrEpisodes;
	}
}
