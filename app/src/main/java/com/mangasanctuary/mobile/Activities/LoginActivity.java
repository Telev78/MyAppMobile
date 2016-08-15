package com.mangasanctuary.mobile.Activities;

import java.util.ArrayList;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
//import com.mangasanctuary.mobile.R;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.mangasanctuary.mobile.Service.CustomHttpClient;
import com.mangasanctuary.mobile.R;
import com.mangasanctuary.mobile.Models.User;

import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.XPatherException;

public class LoginActivity extends Activity {
    EditText un,pw;
	TextView error;
    Button ok;
    
    User user = User.getInstance();
    
    private Boolean isFirstAuthentication = true; 
    
    private static TagNode node;
    private ProgressDialog progressDialog;  
    private int _screenOrientation;
    
    private static final String PREF_FILE_NAME = "mangasanctuarymobile";
    private SharedPreferences preferences = null;

    private AsyncTask<String, String, Boolean> _authenticateTask;
    
    /** Called when the activity is first created. */
    @SuppressWarnings("unchecked")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        un=(EditText)findViewById(R.id.et_un);
        pw=(EditText)findViewById(R.id.et_pw);
        ok=(Button)findViewById(R.id.btn_login);
        error=(TextView)findViewById(R.id.tv_error);

        ok.setOnClickListener(loginOnClick);
        
        preferences = getSharedPreferences(PREF_FILE_NAME, MODE_PRIVATE);
        _authenticateTask = (AsyncTask<String, String, Boolean>) getLastNonConfigurationInstance();
        
		if (_authenticateTask != null && _authenticateTask.getStatus() == AsyncTask.Status.RUNNING) 
			showDialog();
		else if (preferences != null)
        	loadAuthenticateInfo();
    }
    
    @Override
    protected void onStart(){
    	super.onStart();
    }
    
    @Override
    protected void onRestart(){
    	super.onRestart();
    	un.setText("");
    	pw.setText("");
    }
  
    @Override
    protected void onDestroy (){
    	super.onDestroy();
    	dismissProgressDialog();
		this.finishAffinity();
    }
      
    private void showDialog (){
    	progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage(getString(R.string.authenticate));
        progressDialog.setOwnerActivity(LoginActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }
    
    private void dismissProgressDialog (){
    	if (progressDialog != null){
    		progressDialog.dismiss();
    		progressDialog = null;
    	}
    }
    
	// Save the thread
	
    @Override
	public Object onRetainNonConfigurationInstance() {
		return _authenticateTask;
	}
    
    private void saveAuthenticateInfo (){
    	SharedPreferences.Editor editor = preferences.edit();
    	editor.putString("user", un.getText().toString());
    	editor.putString("password", pw.getText().toString()); 
    	editor.commit();    	   
    }
    
    private void loadAuthenticateInfo(){
    	un.setText(preferences.getString("user", ""));
    	pw.setText(preferences.getString("password", ""));
    	if (un.getText().length() > 0 && pw.getText().length() > 0 && isFirstAuthentication){
    		isFirstAuthentication = false;
    		loginOnClick.onClick(ok);
    	}
    }

    
    private OnClickListener loginOnClick = new OnClickListener() {

        public void onClick(View v) {
        	user.Reset();
        	error.setText("");
        	
        	if (un.getText().length() > 0 && pw.getText().length() > 0)
        		_authenticateTask = (AuthenticateTask) new AuthenticateTask().execute(un.getText().toString(),pw.getText().toString());
        	else
        		error.setText(getString(R.string.fill_username_password));
        }
    };

	private void getUserInfo(String res) throws XPatherException {
		// this is where the HtmlCleaner comes in, I initialize it here
		HtmlCleaner cleaner = new HtmlCleaner();
		CleanerProperties props = cleaner.getProperties();
		props.setAllowHtmlInsideAttributes(true);
		props.setAllowMultiWordAttributes(true);
		props.setRecognizeUnicodeChars(true);
		props.setOmitComments(true);

		//use the cleaner to "clean" the HTML and return it as a TagNode object
		node = cleaner.clean(res);
		
		String NAME_XPATH = "//ul[@class='submenu']";
		/*String ID_PATH = "//a[@id='usercollec']";*/
		String SID_PATH = "//a[@title='Déconnexion']";
		
		/*
		  <ul class="submenu">
				<li><a href="/collection.php?id=29830&page=accueil">Ma collection</a></li>
				<li><a href="/membre.php">Mon compte</a></li>
				<li><a href="/membre.php?action=voir_amis">Mes amis</a></li>
				<li><a href="/membre.php?action=contributions">Mes contributions</a></li>
				<li><a href="/membre.php?action=alertes">Mes alertes</a></li>
				<li><a href="/collection.php?id=29830&amp;page=sig">Signature dynamique</a></li>
				<li><a href="/forum/ucp.php?mode=logout&amp;sid=4815d9b85d03752a5eb0a7594665e747" title="D�connexion">D�connexion</a></li>
			</ul>
		 */
		
		
		Object[] info_nodes = node.evaluateXPath(NAME_XPATH);
		if (info_nodes.length > 0) {
		    // casted to a TagNode
		    TagNode info_node = (TagNode) info_nodes[0];
		    
		    //get ID of the user
		    Object[] id_nodes = info_node.evaluateXPath("//li/a");
		    if (id_nodes.length > 0) {
		    	TagNode id_node = (TagNode) id_nodes[0];
		    	if (id_node.getName().equals("a")){
		    		String tmp = id_node.getAttributeByName("href");
		    		user.setID(tmp.substring(tmp.indexOf("?id=") + 4, tmp.indexOf("&page")));
		    	}
		    }
		    
		    //get SID of the user session
		    Object[] sid_nodes = info_node.evaluateXPath(SID_PATH);
		    if (sid_nodes.length > 0) {
		    	TagNode sid_node = (TagNode) sid_nodes[0];
		    	if (sid_node.getName().equals("a")){
		    		String tmp = sid_node.getAttributeByName("href");
		    		user.setSID(tmp.substring(tmp.indexOf("sid=") + 4));
		    	}
		    }
		    
		}
	}
	
	private class AuthenticateTask extends AsyncTask<String, String, Boolean> {
		boolean isAuthenticate = false;
		
	    /** The system calls this to perform work in a worker thread and
	      * delivers it the parameters given to AsyncTask.execute() */
	    protected Boolean doInBackground(String... param) {
	    	
	    	ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
        	postParameters.add(new BasicNameValuePair("login","Login"));
        	postParameters.add(new BasicNameValuePair("username", param[0]));
        	postParameters.add(new BasicNameValuePair("password", param[1]));
        	
        	
        	try {
        		String result = CustomHttpClient.executeHttpPost(getString(R.string.URL) +  getString(R.string.loginURL), postParameters);
        		isAuthenticate = result.toLowerCase().contains("vous êtes maintenant connecté") || result.toLowerCase().contains("panneau de l’utilisateur");
        		//isAuthenticate = result.toLowerCase().contains("bienvenue <span>" + param[0].toLowerCase() + "</span>");
    	    	if(isAuthenticate){
    	    		publishProgress(getString(R.string.login_successful));
    	    		user.setName(param[0]);
    	    		synchronized (this){
    	    			this.wait(500);
    	    		}
    	    		publishProgress(getString(R.string.get_user_info));
        	    	getUserInfo(result);
    	    	}
    	    	
    	    	return isAuthenticate;
        		
        	} catch (Exception e) {
        		Log.e(getString(R.string.app_name), e.toString());
        		return false;
        	}
	    	
	    }
	    
	    /** The system calls this to perform work in the UI thread and delivers
	      * the result from doInBackground() */
	    protected void onPostExecute(Boolean result) {
	    	dismissProgressDialog();
	    	
	    	if (result){
	    		if (preferences != null)
	    			saveAuthenticateInfo();
    	    	startActivity(new Intent(LoginActivity.this, MainActivity.class));
	    	}
	    	else
	    		error.setText(R.string.login_unsuccessful);
	    	
	    	setRequestedOrientation(_screenOrientation);
	    }
	    
	    protected void onCancelled (){
	    	dismissProgressDialog();
	    }
	    
	    protected void onPreExecute (){
	    	_screenOrientation =  getRequestedOrientation();
	    	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
	    	
	    	showDialog();
	    }
	    
	    protected void onProgressUpdate (String... values){
	    	if (values.length > 0 && progressDialog != null)
	    		progressDialog.setMessage(values[0]);
	    }
	}
}

