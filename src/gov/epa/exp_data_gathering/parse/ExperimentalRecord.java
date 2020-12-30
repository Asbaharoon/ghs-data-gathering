package gov.epa.exp_data_gathering.parse;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.Objects;

import org.apache.commons.text.StringEscapeUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import gov.epa.api.ExperimentalConstants;


public class ExperimentalRecord {

	public String id_physchem;//	Autonumbered record number for physchem data (generated by database later)
	public String id_record_source;//	Record number for reference that the physchem data came from (generated by database later- may only need for records from journal articles)
	public String comboID;//

	public String casrn;//Chemical abstracts service number (only if provided by the reference)
	public String einecs;
	public String chemical_name;//	Most systematic name (only if provided in the reference)
	public String synonyms;//	Pipe deliminated synonyms (only if provided in the reference)
	public String smiles;//Simplified Molecular Input Line Entry System for molecular structure (only if provided in the reference)
	public String property_name;//	Name of the property (use  "options_property_names" lookup table to consistently populate the field)
	public String property_value_numeric_qualifier;// >, <, or ~
	public Double property_value_min_final;//The minimum value of a property when a range of values is given
	public Double property_value_max_final;//The maximum value of a property when a range of values is given
	public Double property_value_point_estimate_final;// Point estimate of the property (when a single value is given)
	public String property_value_units_final;//The units for the property value (convert to defined values in ExperimentalConstants class)
	public String property_value_qualitative;// Valid qualitative data: solubility descriptor, appearance
	public Double temperature_C;//The temperature in C that the property is measured at (vapor pressure might be given at 23 C for example)
	public String pressure_mmHg;//The pressure in kPa that the property is measured at (important for boiling points for example)
	public String pH;
	public String measurement_method;//	The experimental method used to measure the property
	public String reliability;
	public String dsstox_substance_id; //DSSTox substance identifier
	public String note;//	Any additional note

	public String property_value_string;//Store original string from source for checking later
	public Double property_value_min_original;//The minimum value of a property when a range of values is given
	public Double property_value_max_original;//The maximum value of a property when a range of values is given
	public Double property_value_point_estimate_original;// Point estimate of the property (when a single value is given)
	public String property_value_units_original;//The units for the property value (convert to defined values in ExperimentalConstants class)
	
	public String url;
	public String source_name;//use Experimental constants
	public String original_source_name;//If specific reference/paper provided
								//"original_source_name" rather than "source_name_original" to avoid syntactic confusion with "*_original" vs "*_final" fields above
	public String date_accessed;//use Experimental constants
	
	public boolean keep;//Does the record contain useful data?
	public boolean flag;
	public String reason;//If keep=false or flag=true, why?
	
	//TODO do we need parent url too? sometimes there are several urls we have to follow along the way to get to the final url

	public final static String [] outputFieldNames= {"id_physchem",
			"keep",
			"reason",
			"casrn",
			"einecs",
			"chemical_name",
			"synonyms",
			"smiles",
			"source_name",
			"property_name",
			"property_value_string",
			"property_value_numeric_qualifier",
			"property_value_point_estimate_final",
			"property_value_min_final",
			"property_value_max_final",
			"property_value_units_final",
			"pressure_mmHg",
			"temperature_C",
			"pH",
			"property_value_qualitative",
			"measurement_method",
			"note",
			"flag",
			"original_source_name",
			"url",
			"date_accessed"};


	
	public void setComboID(String del) {
		String CAS=casrn;
		if (CAS==null || CAS.trim().isEmpty()) CAS="casrn=null";//need placeholder so dont get spurious match in chemreg
		else {
			CAS=CAS.trim();
			while (CAS.substring(0,1).contentEquals("0")) {//trim off zeros at front
				CAS=CAS.substring(1,CAS.length());
			}
			//TODO - do we need to handle Cases with no dashes? Check for bad cas numbers (bad check sum?)
		}
		String name=chemical_name;
		if (name==null || name.trim().isEmpty()) name="name=null";//need placeholder so dont get spurious match in chemreg
		name=name.trim();
		
		String SMILES=smiles;
		if (SMILES==null || SMILES.trim().isEmpty()) SMILES="smiles=null";//need placeholder so dont get spurious match in chemreg
		SMILES=SMILES.trim();
		
		//TODO omit chemicals where smiles indicates bad element....
		
		comboID=CAS+del+name+del+SMILES;//TODO add einecs
		
	}	

	
	/**
	 * Converts to final units and assigns point estimates for any ranges within tolerance:
	 * LogKow, pKa = 1 log unit
	 * Melting point, boiling point, flash point = 10 C
	 * Density = 0.1 g/cm^3
	 * Vapor pressure = 10 mmHg
	 * HLC = 100 Pa-m^3/mol
	 * Water solubility = 1 g/L
	 */
	public void finalizeRecord() {
		double logTolerance = 0.5;//if value was 1, then max would be 10x bigger than min
		double temperatureTolerance = 10.0;
		double densityTolerance = 0.1;
		
		//Properties which are usually modeled as log of the property value: pKA, logKow, WS, HLC, VP, LC50, LD50
		
								
		if (property_name.equals(ExperimentalConstants.str_pKA) || property_name.equals(ExperimentalConstants.strLogKow)) {
			if (property_value_point_estimate_original!=null) { property_value_point_estimate_final = property_value_point_estimate_original; }

			if (property_value_min_original!=null) { 
				property_value_min_final = property_value_min_original;
				property_value_max_final = property_value_max_original;

				if (isWithinTolerance(logTolerance)) {
					calculateFinalValueFromMinMaxAverage();//values are already in log units so dont need to use geometric median
					updateNote("Point estimate computed from range");
				}
			}
			property_value_units_final = property_value_units_original;
		} else if ((property_name.equals(ExperimentalConstants.strMeltingPoint) || property_name.equals(ExperimentalConstants.strBoilingPoint) ||
				property_name.equals(ExperimentalConstants.strFlashPoint)) && property_value_units_original!=null) {
			UnitConverter.convertTemperature(this);
			if (property_value_min_final!=null && isWithinTolerance(temperatureTolerance)) {
				calculateFinalValueFromMinMaxAverage();
				updateNote("Point estimate computed from average of range");
			}
		} else if (property_name.equals(ExperimentalConstants.strDensity)) {
			UnitConverter.convertDensity(this);
			if (property_value_min_final!=null && isWithinTolerance(densityTolerance)) {
				calculateFinalValueFromMinMaxAverage();
				updateNote("Point estimate computed from average of range");
			}
		} else if (property_name.equals(ExperimentalConstants.strVaporPressure) && property_value_units_original!=null) {
			UnitConverter.convertPressure(this);
			if (property_value_min_final!=null && isWithinLogTolerance(logTolerance)) {
				calculateFinalValueFromMinMaxGeometricMedian();
				updateNote("Point estimate computed from geometric median of range");
			}
		} else if (property_name.equals(ExperimentalConstants.strHenrysLawConstant) && property_value_units_original!=null) {
			boolean converted = UnitConverter.convertHenrysLawConstant(this);
			if (converted && property_value_min_final!=null && isWithinLogTolerance(logTolerance)) {
				calculateFinalValueFromMinMaxGeometricMedian();
				updateNote("Point estimate computed from geometric median of range");
			}
		} else if (property_name.equals(ExperimentalConstants.strWaterSolubility) && property_value_units_original!=null) {
			boolean converted = UnitConverter.convertSolubility(this);
			if (converted && property_value_min_final!=null && isWithinLogTolerance(logTolerance)) {
				calculateFinalValueFromMinMaxGeometricMedian();
				updateNote("Point estimate computed from geometric median of range");
			}
		}  else if (property_name.contains("LC50") || property_name.contains("LD50") && property_value_units_original!=null) {
			boolean converted=UnitConverter.convertToxicity(this);
			
			if (converted && property_value_min_final!=null && isWithinLogTolerance(logTolerance)) {
				calculateFinalValueFromMinMaxGeometricMedian();
				updateNote("Point estimate computed from geometric median of range");
			}
		}
		
		property_name = StringEscapeUtils.escapeHtml4(property_name);
	}


	private boolean isWithinLogTolerance(double logTolerance) {
		return Math.log10(property_value_max_final/property_value_min_final) <= logTolerance;
	}


	private boolean isWithinTolerance(double temperatureTolerance) {
		return property_value_max_final-property_value_min_final <= temperatureTolerance;
	}
	
	private void calculateFinalValueFromMinMaxAverage() {
		property_value_point_estimate_final = (property_value_min_final + property_value_max_final)/2.0;		
		//@Gabriel this is same as property_value_min_final + (property_value_max_final-property_value_min_final)/2.0;
	}
	
	/**
	 * Use this when values span many orders of magnitude (and modeled property is the log of the value)
	 */
	private void calculateFinalValueFromMinMaxGeometricMedian() {
		property_value_point_estimate_final = Math.sqrt(property_value_min_final * property_value_max_final);		
		//Note: since usually the log value is the modeled property, geometric median = 10 ^ average log value (properties of logarithms)
	}
	
	public String toString(String del) {
		// TODO Auto-generated method stub
		return toString(del,outputFieldNames);
	}

	public String toJSON() {
		GsonBuilder builder = new GsonBuilder();
		builder.setPrettyPrinting();// makes it multiline and readable
		Gson gson = builder.create();
		return gson.toJson(this);//all in one line!
	}

	//convert to string by reflection:
	public String toString(String del,String [] fieldNames) {

		String Line = "";
		
		for (int i = 0; i < fieldNames.length; i++) {
			try {


				Field myField = this.getClass().getDeclaredField(fieldNames[i]);

				String val=null;

				//						System.out.println(myField.getType().getName());

				if (myField.getType().getName().contains("Double")) {
					if (myField.get(this)==null) {
						val="";	
					} else {
						val=myField.get(this)+"";
					}

				} else if (myField.getType().getName().contains("Integer")) {
					if (myField.get(this)==null) {
						val="";	
					} else {
						val=myField.get(this)+"";
					}
				} else if (myField.getType().getName().contains("Boolean")) {
					if (myField.get(this)==null) {
						val="";	
					} else {
						val=myField.get(this)+"";
					}
					
				} else {//string
					if (myField.get(this)==null) {
						//								val="\"\"";
						val="";
					} else {
						//								val="\""+(String)myField.get(this)+"\"";
						val=(String)myField.get(this);
					} 
				}

				val=val.replace("\r\n","<br>");
				val=val.replace("\n","<br>");

				if (val.contains(del)) {
					System.out.println("***WARNING***"+this.casrn+"\t"+fieldNames[i]+"\t"+val+"\thas delimiter");
				}

				Line += val;
				if (i < fieldNames.length - 1) {
					Line += del;
				}


			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		return Line;
	}
	
	/**
	 * Flexible method that converts ExperimentalRecord to string array (useful for writing to excel and sqlite)
	 *  
	 * @param fieldNames
	 * @return
	 */
	public String [] toStringArray(String [] fieldNames) {

		String Line = "";

		String [] array=new String [fieldNames.length];

		for (int i = 0; i < fieldNames.length; i++) {
			try {

				Field myField = this.getClass().getDeclaredField(fieldNames[i]);

				String val=null;
				String type=myField.getType().getName();

				
				switch (type) {
				
				case "java.lang.String":
					if (myField.get(this)==null) val="";	
					else val=myField.get(this)+"";						
					val=ExperimentalRecords.reverseFixChars(StringEscapeUtils.unescapeHtml4(val.replaceAll("(?<!\\\\)'", "\'")));					
					break;
				
				case "java.lang.Double":
					if (myField.get(this)==null) val="";	
					else {
						val=ParseUtilities.formatDouble((Double)myField.get(this));						
					}										
					break;
					
				case "java.lang.Integer":
				case "java.lang.Boolean": 							
					if (myField.get(this)==null) val="";	
					else val=myField.get(this)+"";						
					break;					
				case "boolean":
					val=myField.getBoolean(this)+"";
					break;
				case "int":
					val=myField.getInt(this)+"";
					break;
				case "double": 
					val=myField.getDouble(this)+"";

				}

				val=val.trim();
				val=val.replace("\r\n","<br>");
				val=val.replace("\n","<br>");

				array[i]=val;

			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		return array;
	}
	
	
	/**
	 * Adds a string to the note field of an ExperimentalRecord object
	 * @param er	The ExperimentalRecord object to be updated
	 * @param str	The string to be added
	 * @return		The updated ExperimentalRecord object
	 */
	public void updateNote(String str) {
		note = Objects.isNull(note) ? str : note+"; "+str;
	}

//	public String[] getValuesForDatabase() {
//		String name = chemical_name==null ? "" : chemical_name.replaceAll("(?<!\\\\)'", "\'");
//		String pointEstimate = property_value_point_estimate_final==null ? "" : Double.toString(property_value_point_estimate_final);
//		String min = property_value_min_final==null ? "" : Parse.formatDouble(property_value_min_final);
//		String max = property_value_max_final==null ? "" : Parse.formatDouble(property_value_max_final);
//		String temp = temperature_C==null ? "" : Parse.formatDouble(temperature_C);
//		String [] values= {Boolean.toString(keep),
//				reason,
//				casrn,
//				einecs,
//				name,
//				synonyms,
//				smiles,
//				source_name,
//				property_name,
//				property_value_string,
//				property_value_numeric_qualifier,
//				pointEstimate,
//				min,
//				max,
//				property_value_units_final,
//				pressure_mmHg,
//				temp,
//				pH,
//				property_value_qualitative,
//				measurement_method,
//				note,
//				Boolean.toString(flag),
//				original_source_name,
//				url,
//				date_accessed};
//		return values;
//	}
}
