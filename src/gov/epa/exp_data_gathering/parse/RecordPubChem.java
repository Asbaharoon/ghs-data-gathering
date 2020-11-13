package gov.epa.exp_data_gathering.parse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Vector;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import gov.epa.api.AADashboard;
import gov.epa.api.ExperimentalConstants;
import gov.epa.exp_data_gathering.parse.JSONsForPubChem.*;
import gov.epa.ghs_data_gathering.GetData.RecordDashboard;
import gov.epa.ghs_data_gathering.Utilities.FileUtilities;

public class RecordPubChem {
	String cid;
	String iupacName;
	String smiles;
	String synonyms;
	String cas;
	Vector<String> physicalDescription;
	Vector<String> density;
	Vector<String> meltingPoint;
	Vector<String> boilingPoint;
	Vector<String> flashPoint;
	Vector<String> solubility;
	Vector<String> vaporPressure;
	Vector<String> henrysLawConstant;
	Vector<String> logP;
	Vector<String> pKa;
	
	static final String sourceName=ExperimentalConstants.strSourcePubChem;
	
	/**
	 * Extracts DTXSIDs from CompTox dashboard records and translates them to PubChem CIDs
	 * @param records	A vector of RecordDashboard objects
	 * @param start		The index in the vector to start converting
	 * @param end		The index in the vector to stop converting
	 * @return			A vector of PubChem CIDs as strings
	 */
	private static Vector<String> getCIDsFromDashboardRecords(Vector<RecordDashboard> records,String dictFilePath,int start,int end) {
		Vector<String> cids = new Vector<String>();
		LinkedHashMap<String,String> dict = new LinkedHashMap<String, String>();
		
		try {
			File file = new File(dictFilePath);
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line="";
			while ((line=br.readLine())!=null) {
				String[] cells=line.split(",");
				dict.put(cells[0], cells[1]);
			}
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		for (int i = start; i < end; i++) {
			String dtxsid = records.get(i).DTXSID;
			String cid = dict.get(dtxsid);
			if (cid!=null) { cids.add(cid); }
		}
		
		return cids;
	}
	
	private static Vector<RecordPubChem> generatePubChemRecordsFromCIDs(Vector<String> cids) {
		Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
		Vector<RecordPubChem> records = new Vector<RecordPubChem>();
		String url = "https://pubchem.ncbi.nlm.nih.gov/rest/pug/compound/cid/property/IUPACName,CanonicalSMILES/JSON?cid="+cids.get(0);
		for (int i = 1; i < cids.size(); i++) { url = url + "," + cids.get(i); }
		try {
			URL readURL = new URL(url);
			InputStreamReader isr = new InputStreamReader(readURL.openStream());
			IdentifierData idData = gson.fromJson(isr,IdentifierData.class);
			List<Property> properties = idData.propertyTable.properties;
			for (Property prop:properties) {
				RecordPubChem pcr = new RecordPubChem();
				pcr.cid = prop.cid;
				pcr.iupacName = prop.iupacName;
				pcr.smiles = prop.canonicalSMILES;
				records.add(pcr);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		return records;
	}
	
	private static void downloadIdentifierDataJSON(Vector<String> cids) {
		// TODO
	}
	
	private static void downloadCASDataJSONsToDatabase(Vector<String> cids) {
		String baseURL = "https://pubchem.ncbi.nlm.nih.gov/rest/pug_view/data/compound/";
		String tailURL = "/JSON?heading=CAS";
		String databaseName = "PubChem_cas_jsons.db";
		Vector<String> urls = new Vector<String>();
		for (String cid:cids) { urls.add(baseURL+cid+tailURL); }
		
		ParsePubChem p = new ParsePubChem();
		p.downloadJSONsToDatabase(urls,databaseName,sourceName,true);
	}
	
	private static void downloadExperimentalDataJSONsToDatabase(String cid) {
		// TODO
	}
	
	private void getCAS() {
		Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
		String baseURL = "https://pubchem.ncbi.nlm.nih.gov/rest/pug_view/data/compound/";
		String tailURL = "/JSON?heading=CAS";
		String url = baseURL+cid+tailURL;
		try {
			URL readURL = new URL(url);
			InputStreamReader isr = new InputStreamReader(readURL.openStream());
			CASData casData = gson.fromJson(isr,CASData.class);
			List<Information> info = casData.record.section.get(0).section.get(0).section.get(0).information;
			cas = info.get(0).value.stringWithMarkup.get(0).string;
			boolean mismatch = false;
			for (int i = 1; i < info.size() && !mismatch; i++) {
				if (!info.get(i).value.stringWithMarkup.get(0).string.equals(cas)) { mismatch = true; }
			}
			if (mismatch) { System.out.println("CAS mismatch for "+iupacName); }
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	private void getSynonyms() {
		String baseURL = "https://pubchem.ncbi.nlm.nih.gov/rest/pug/compound/cid/";
		String tailURL = "/synonyms/TXT";
		String url = baseURL+cid+tailURL;
		synonyms = FileUtilities.getText_UTF8(url).replace("\r\n","|");
	}
	
	public static void main(String[] args) {
		Vector<RecordDashboard> drs = Parse.getDashboardRecordsFromExcel(AADashboard.dataFolder+"/PFASSTRUCT.xls");
		Vector<String> cids = getCIDsFromDashboardRecords(drs,AADashboard.dataFolder+"/CIDDICT.csv",1,100);
		downloadCASDataJSONsToDatabase(cids);
//		Vector<RecordPubChem> pcrs = generatePubChemRecordsFromCIDs(cids);
//		Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
//		for (RecordPubChem pcr:pcrs) {
//			pcr.getCAS();
//			System.out.println(gson.toJson(pcr));
//		}
	}
}
