package gov.epa.exp_data_gathering.parse.Burkhard2;

import static org.apache.poi.ss.usermodel.Cell.CELL_TYPE_STRING;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;

import com.google.gson.JsonObject;

import gov.epa.api.ExperimentalConstants;
import gov.epa.exp_data_gathering.parse.ExcelSourceReader;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;


public class RecordBurkhard2 {
	public String Chemical;
	public String CAS;
	public String Abbreviation;
	public String Log_BCF_Steady_State_mean;
	public String Log_BCF_Steady_State_stdev;
	public String Log_BCF_Steady_State_min;
	public String Log_BCF_Steady_State_max;
	public String Log_BCF_Steady_State_lower_CI;
	public String Log_BCF_Steady_State_upper_CI;
	public String Log_BCF_Steady_State_arithmetic_or_logarithmic;
	public String Log_BCF_Steady_State_units;
	public String field11;
	public String Log_BCF_Kinetic_mean;
	public String Log_BCF_Kinetic_stdev;
	public String Log_BCF_Kinetic_min;
	public String Log_BCF_Kinetic_max;
	public String Log_BCF_Kinetic_lower_CI;
	public String Log_BCF_Kinetic_upper_CI;
	public String Log_BCF_Kinetic_arithmetic_or_logarithmic;
	public String Log_BCF_Kinetic_units;
	public String field20;
	public String BCF_Adjustment_DW_to_WW;
	public String Finalized_log10_BCF_L_kg_ww;
	public String field23;
	public String Log_BAF_mean;
	public String Log_BAF_stdev;
	public String Log_BAF_min;
	public String Log_BAF_max;
	public String Log_BAF_lower_CI;
	public String Log_BAF_upper_CI;
	public String Log_BAF_arithmetic_or_logarithmic;
	public String Log_BAF_units;
	public String field32;
	public String BAF_Adjustment_DW_to_WW;
	public String Finalized_log10_BAF_L_kg_ww;
	public String field35;
	public String Measured_Trophic_Level;
	public String field37;
	public String field38;
	public String Estimated_Trophic_Level;
	public String Common_Name;
	public String Species_Latin_Name;
	public String Species_Weight_g;
	public String field43;
	public String field44;
	public String field45;
	public String Tissue;
	public String Location;
	public String Reference;
	public String BCF_data;
	public String OECD_305;
	public String ku;
	public String ku_sd;
	public String field53;
	public String days_of_uptake;
	public String ke;
	public String ke_sd;
	public String field57;
	public String days_of_elimination;
	public String half_life_days;
	public String half_life_sd_days;
	public String SS;
	public String Kinetic;
	public String Modeled;
	public String Exposure_Concentrations;
	public String Study_Quality_BCF;
	public String Comments_BCF;
	public String k_dietary;
	public String k_dietary_sd;
	public String field69;
	public String Assimulation_Efficiency;
	public String Assimulation_Efficiency_SD;
	public String field72;
	public String BAF;
	public String BAF_of_water_samples;
	public String BAF_start_date_for_water_sampling;
	public String BAF_end_date_for_water_sampling;
	public String field77;
	public String BAF_of_biota_samples;
	public String BAF_start_date_for_biota_sampling;
	public String BAF_end_date_for_biota_sampling;
	public String Average_weight_g;
	public String Average_Length_cm;
	public String Sex_F_M;
	public String Average_Age;
	public String Age_sd;
	public String General_experimental_design;
	public String Water_Biota_spatial_coordination;
	public String Water_Biota_temporal_coordination;
	public String Number_Biota_Samples;
	public String Number_of_Water;
	public String Study_Quality_BAF;
	public String Comments_BAFs;
	public String field93;
	public String Mixture_Exposure;
	public String Concentration_in_Biota_mean;
	public String Concentration_in_Biota_stdev;
	public String Concentration_in_Biota_units;
	public String Concentration_in_Biota_MDL;
	public String Comments_Biota_Samples;
	public String Concentration_in_Water_mean;
	public String Concentration_in_Water_stdev;
	public String Concentration_in_Water_units;
	public String Concentration_in_Water_MDL;
	public String Comments_Environmental_Media;
	public String field105;
	public String Marine_Brackish_Freshwater;
	public String Comments_Marine_Brackish_Freshwater;
	public String field108;
	public String Taxonomy_Name_Level;
	public String kingdom_taxonomy;
	public String phylum_taxonomy;
	public String class_taxonomy;
	public String order_taxonomy;
	public String family_taxonomy;
	public String genus_taxonomy;
	public String species_taxonomy;
	public String ITIS_TaxID;
	public String NCBI_TaxID;
	public String GBIF_TaxID;
	public String Common_Name_NCBI_ITIS;
	public String Taxomony_Name_Source;
	public String field122;
	public static final String[] fieldNames = {"Chemical","CAS","Abbreviation","Log_BCF_Steady_State_mean","Log_BCF_Steady_State_stdev","Log_BCF_Steady_State_min","Log_BCF_Steady_State_max","Log_BCF_Steady_State_lower_CI","Log_BCF_Steady_State_upper_CI","Log_BCF_Steady_State_arithmetic_or_logarithmic","Log_BCF_Steady_State_units","field11","Log_BCF_Kinetic_mean","Log_BCF_Kinetic_stdev","Log_BCF_Kinetic_min","Log_BCF_Kinetic_max","Log_BCF_Kinetic_lower_CI","Log_BCF_Kinetic_upper_CI","Log_BCF_Kinetic_arithmetic_or_logarithmic","Log_BCF_Kinetic_units","field20","BCF_Adjustment_DW_to_WW","Finalized_log10_BCF_L_kg_ww","field23","Log_BAF_mean","Log_BAF_stdev","Log_BAF_min","Log_BAF_max","Log_BAF_lower_CI","Log_BAF_upper_CI","Log_BAF_arithmetic_or_logarithmic","Log_BAF_units","field32","BAF_Adjustment_DW_to_WW","Finalized_log10_BAF_L_kg_ww","field35","Measured_Trophic_Level","field37","field38","Estimated_Trophic_Level","Common_Name","Species_Latin_Name","Species_Weight_g","field43","field44","field45","Tissue","Location","Reference","BCF_data","OECD_305","ku","ku_sd","field53","days_of_uptake","ke","ke_sd","field57","days_of_elimination","half_life_days","half_life_sd_days","SS","Kinetic","Modeled","Exposure_Concentrations","Study_Quality_BCF","Comments_BCF","k_dietary","k_dietary_sd","field69","Assimulation_Efficiency","Assimulation_Efficiency_SD","field72","BAF","BAF_of_water_samples","BAF_start_date_for_water_sampling","BAF_end_date_for_water_sampling","field77","BAF_of_biota_samples","BAF_start_date_for_biota_sampling","BAF_end_date_for_biota_sampling","Average_weight_g","Average_Length_cm","Sex_F_M","Average_Age","Age_sd","General_experimental_design","Water_Biota_spatial_coordination","Water_Biota_temporal_coordination","Number_Biota_Samples","Number_of_Water","Study_Quality_BAF","Comments_BAFs","field93","Mixture_Exposure","Concentration_in_Biota_mean","Concentration_in_Biota_stdev","Concentration_in_Biota_units","Concentration_in_Biota_MDL","Comments_Biota_Samples","Concentration_in_Water_mean","Concentration_in_Water_stdev","Concentration_in_Water_units","Concentration_in_Water_MDL","Comments_Environmental_Media","field105","Marine_Brackish_Freshwater","Comments_Marine_Brackish_Freshwater","field108","Taxonomy_Name_Level","kingdom","phylum","class","order","family","genus","species","ITIS_TaxID","NCBI_TaxID","GBIF_TaxID","Common_Name_NCBI_ITIS","Taxomony_Name_Source","field122","44343_663981481484"};

	public static final String lastUpdated = "06/15/2021";
	public static final String sourceName = "Burkhard2"; // TODO Consider creating ExperimentalConstants.strSourceBurkhard2 instead.

	private static final String fileName = "Copy of PFAS_BCF_BAF_DATA (3).xlsx";

	public static Vector<JsonObject> parseBurkhard2RecordsFromExcel() {
		ExcelSourceReader esr = new ExcelSourceReader(fileName, sourceName);
		Vector<JsonObject> records = esr.parseRecordsFromExcel(0); // TODO Chemical name index guessed from header. Is this accurate?
		return records;
	}
}
