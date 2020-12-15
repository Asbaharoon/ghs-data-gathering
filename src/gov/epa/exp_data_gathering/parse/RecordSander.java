package gov.epa.exp_data_gathering.parse;
import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sql.RowSetListener;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import gov.epa.api.ExperimentalConstants;
import gov.epa.ghs_data_gathering.Database.MySQL_DB;
import gov.epa.ghs_data_gathering.GetData.RecordDashboard;
import gov.epa.ghs_data_gathering.Utilities.FileUtilities;

public class RecordSander {
	
	Vector <String> referenceAbbreviated;
	String chemicalName;
	String inchiKey;
	String CASRN;
	Vector<String> d_ln_Hcp_over_d;
	Vector<String> hcp;
	Vector<String> type;
	Vector<String> referenceFull; //unsorted, don't align with referencesAbbreviated, regular expression code needs to be refined to do so
	String url;
	int recordCount;
	String fileName;
	
	
	static final String sourceName="Sander";	


	public static void main(String[] args) {
		// TODO Auto-generated method stub
		downloadWebpagesHTML();
	}
	// as the name implies, only related to the webpage download process
public static void downloadWebpagesHTML() {
	// Vector<String> urls = ObtainWebpages();
	ParseSander p = new ParseSander();
	p.mainFolder = p.mainFolder + File.separator + "General";
	p.databaseFolder = p.mainFolder;
	Vector<String> html = parsePropertyLinksInDatabase();
	// p.downloadWebpagesToDatabaseAdaptive(urls,"tbody", sourceName,true);
	// p.downloadWebpagesToDatabaseAdaptive(downloadedURLs,"div.RFQbox ~ table",sourceName,false);		

}
	// regex, to be used later depending on whether Todd wants full reference list
public static void Gabrieldemo() {
	String Referenceshort = "Mackay and Shiu 1981";
	String Referencelong = "Mackay, D. and Shiu, W. Y.: A critical review of Henry's law constants for chemicals of environmental interest, J. Phys. Chem. Ref. Data, 10, 1175-1199, doi:10.1063/1.555654, 1981.";
	Pattern p = Pattern.compile("(([^ ]+) .*?)([^\\s]+$)");
	Matcher m = p.matcher(Referenceshort);
	if (m.find()) {
	System.out.println(m.group(2) + " " + m.group(3));
	}
}
	// since the HTML for sander code is a lot more split up than chemicalbook, I am calling 3 separate functions for the 3 parts of the page
private static void parseDocument(RecordSander rs, Document doc) {
	getExperimentalTable(doc, rs);
	getIdentifiers(doc, rs);
	getReferences(doc,rs);
}
private static void parseURL(RecordSander rs, String url) {
	rs.url = url;	
}

	//assigns a chemical name, cas number, and inchi key to the recordSander object
private static void getIdentifiers(Document doc, RecordSander rs) {
	Element chemicalName = doc.select("td[width=60%] > h1").first();
	rs.chemicalName = chemicalName.ownText();
	// I want this to not start with ???
	Element inchikey = doc.select("td[width=60%] > table > tbody > tr > td:contains(InChIKey:) ~ td").first();
	String inchi = inchikey.text();
	rs.inchiKey = inchi;
	Element casrn = doc.selectFirst("td[width=60%] > table > tbody > tr > td:contains(CAS RN:) ~ td");
	String cas = casrn.text();
	rs.CASRN = cas;
}
	// scrapes the 'Sander - full' page to obtain the links for all chemicals on the site
private static Vector<String> ObtainWebpages() {
	String baseSearchLink = "http://satellite.mpic.de/henry/search_identifier.html?csrfmiddlewaretoken=eNhrzlz52Jf3pHHxhPPvsFfi0jCmhStqaIIF7xXrsctaPEuHMIgkdjAkRyUDvPQm&x=0&y=0&search=";
	Vector<String> allLinks = new Vector<String>();
	try {
		Document doc = Jsoup.connect(baseSearchLink).get();
		Elements rows = doc.select("td[width=60%] > table > tbody > tr");
		for (int i = 0; i < rows.size(); i++) {
			allLinks.add(rows.get(i).select("td ~ td > a").attr("abs:href").toString());
		}
	return allLinks;
	}
	catch (IOException e) {
		e.printStackTrace();
	}
	return null;
}

	// returns all html from the websites downloaded to the database
public static Vector<String> parsePropertyLinksInDatabase() {
	String databaseFolder = "Data"+File.separator+"Experimental"+ File.separator + sourceName + File.separator + "General";
	String databasePath = databaseFolder+File.separator+ExperimentalConstants.strSourceSander + "_raw_html.db";
	Vector<String> records = new Vector<>();
	System.out.println(databasePath);

	try {
		Statement stat = MySQL_DB.getStatement(databasePath);
		ResultSet rs = MySQL_DB.getAllRecords(stat, ExperimentalConstants.strSourceSander);
		
		int counter = 1;
		
		
		while (rs.next()) {
			if (counter % 100==0) { System.out.println("Parsed "+counter+" pages"); }
			
			String html = rs.getString("content");
			records.add(html);
		}
		

		return records;
	} catch (Exception e) {
		e.printStackTrace();
	}	
	return null;
}


	// gets the full references for the Sander site
private static void getReferences(Document doc, RecordSander rs) {
	Vector<String> referencesFull = new Vector <String>();
	Elements references = doc.select("ul > li");
	for (Element reference:references) {
		referencesFull.add(reference.text());
	}
	rs.referenceFull = referencesFull;
}

	// gets all the experimental information from the parsed Sander html. hcp, references abbreviated, etc.
private static void getExperimentalTable(Document doc, RecordSander rs) {
		Vector <Vector<String>> experimentInfo = new Vector <Vector<String>>();
		Vector <String> experimentalFields = new Vector<String>();
		
		Vector<String> hcp = new Vector<String>();
		Vector<String> dln = new Vector<String>();
		Vector<String> reference = new Vector<String>();
		Vector<String> Type = new Vector<String>();
		Vector<String> Notes = new Vector <String>();
		Element table = doc.select("td[width=60%] > table ~ table[width=100%] > tbody").first();
		Elements tableRecords = table.select("tr:gt(1)");
		int recordno = 0;
		for (Element tableRecord:tableRecords) {
			Elements tableFields = tableRecord.select("td");
			hcp.add(tableFields.get(0).text()); // 0 is the first column, the Hcp
			dln.add(tableFields.get(1).text()); // 1 is the second column, the d ln Hcp / d (1/T) [K]
			reference.add(tableFields.get(2).text()); // 2 is the third column, the reference
			Type.add(tableFields.get(3).text()); // 3 is the fourth column, the type
												// 4 is the fifth column, the notes
			recordno++;
		}
		
		rs.hcp = hcp;
		rs.d_ln_Hcp_over_d = dln;
		rs.referenceAbbreviated = reference;
		rs.type = Type;
		rs.recordCount = recordno;
		
}

/**
 * Parses the HTML strings in the raw HTML database to RecordSander objects
 * @return	A vector of RecordLookChem objects containing the data from the raw HTML database
 */
public static Vector<RecordSander> parseWebpagesInDatabase() {
	String databaseFolder = "Data"+File.separator+"Experimental"+ File.separator + sourceName + File.separator + "General";
	String databasePath = databaseFolder+File.separator+sourceName+"_raw_html.db";
	Vector<RecordSander> records = new Vector<>();

	try {
		Statement stat = MySQL_DB.getStatement(databasePath);
		ResultSet rs = MySQL_DB.getAllRecords(stat, ExperimentalConstants.strSourceSander);
		
		int counter = 1;
	
		while (rs.next()) {
			if (counter % 100==0) { System.out.println("Parsed "+counter+" pages"); }
			
			String html = rs.getString("content");
			String url = rs.getString("url");
			Document doc = Jsoup.parse(html);
			
			RecordSander rsand=new RecordSander();
			rsand.fileName=url.substring(url.lastIndexOf("/")+1, url.length());
			
			parseDocument(rsand,doc);
			parseURL(rsand,url);
			
			if (rsand.CASRN != null) {
				records.add(rsand);
				counter++;
			} else {
				// rs.updateString("html", ExperimentalConstants.strRecordUnavailable);
				// Updater doesn't work - JDBC version issue?
			}
		}


		return records;
	} catch (Exception e) {
		e.printStackTrace();
	}
	
	return null;
}

}
