package org.dbpedia.infoboxprov.io;

/**
 * Enumeration to store different readvariants.
 * ReadDefault: all the revisions inside the history of one page will be recognized
 * ReadTimeFiltered: just the revisions inside the user specified timeframe will be recognized
 * 
 * @author daniel
 */

public enum READVARIANT {
	
	ReadDefault, ReadTimeFiltered
}
