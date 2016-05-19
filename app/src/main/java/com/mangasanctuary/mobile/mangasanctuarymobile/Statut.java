package com.mangasanctuary.mobile.mangasanctuarymobile;

import android.content.Context;

public enum Statut {
	Tous ("Tous"),
	Suivi ("Incomplète"),
	Complete ("Complète"),
	Non_Suivi("Non suivie"),
	Interrompu("Edition stoppée");
	
	
	private final String alias; 
	private static Context _context;
	
	Statut (String alias)
	{
		this.alias = alias;
	}
	
	public String toString()
	{
		return this.FriendlyName();
	}
	
	public static void setContext (Context context){
		_context = context;
	}
	
	public static Statut valueOfAlias( String s )
    {
        try
        {
        	return valueOf( s.toUpperCase() );
        }
        catch ( IllegalArgumentException e )
            {
            // usual method failed, try looking up alias
            // This seems long winded, why no HashSet?
            // Because Java won't let me access a static common
            // lookup in the enum constructors. There are problems with initialisation
            // enum constants at static init time.
            // See notes at http://mindprod.com/jgloss/enum.html on Piotr Kobza's
            // kludge to get one.
            	for ( Statut candidateEnum : Statut.values() )
                {
	                if ( candidateEnum.alias.equalsIgnoreCase( s ) )
	                {
	                    return candidateEnum;
	                }
                }
            // fell out the bottom of search over all enums and aliases
            // give up.
            throw new IllegalArgumentException( "unknown status: " + s );
         }
    }
	
	public String getImageName(){
		String flag = "";
		switch (this){
			case Complete : flag = "blue"; break;
			case Interrompu : flag = "orange"; break;
			case Non_Suivi : flag = "red"; break;
			case Suivi : 
			default : flag = "green"; break;
		}
		
		String path = getClass().getPackage().getName() + ":drawable/flag_" + flag;
		return path;//getResources().getIdentifier(path, null, null);
	}
	
	public String FriendlyName (){
		String friendlyName = "";
		switch (this){
			case Tous : friendlyName = _context.getString(R.string.tous); break;
			case Complete : friendlyName = _context.getString(R.string.complete); break;
			case Interrompu : friendlyName = _context.getString(R.string.interrompu); break;
			case Non_Suivi : friendlyName = _context.getString(R.string.non_suivi); break;
			case Suivi : friendlyName = _context.getString(R.string.suivi); break;
		}
		
		return friendlyName;
	}
};
