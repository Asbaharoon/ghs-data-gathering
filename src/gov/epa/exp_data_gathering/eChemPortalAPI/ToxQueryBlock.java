package gov.epa.exp_data_gathering.eChemPortalAPI;

import java.util.ArrayList;
import java.util.List;

public class ToxQueryBlock extends QueryBlock {
	
	public ToxQueryBlock(String endpointKind) {
		this.endpointKind = endpointKind;
		if (endpointKind.contains("AcuteToxicity")) {
			effectLevelString = "EffectLevels";
		} else if (endpointKind.contains("RepeatedDoseToxicity")) {
			effectLevelString = "EffectLevels.Efflevel";
		}
	}
	
	private transient String effectLevelString;
	
	/**
	 * Adds a QueryField to set endpoint value bounds
	 * @param lower		Lower endpoint value bound
	 * @param upper		Upper endpoint value bound
	 * @param unit		Desired endpoint value unit from ExperimentalConstants
	 */
	public void addEffectLevelField(String lower,String upper,String unit) {
		String fieldName = "ENDPOINT_STUDY_RECORD."+endpointKind+".ResultsAndDiscussion."+effectLevelString+".EffectLevel";
		String type = "range";
		Value endpointValue = new Value(type,lower,upper,new ToxUnit(unit,endpointKind));
		QueryField endpoint = new QueryField(fieldName,type,"Effect Level",endpointValue);
		queryFields.add(endpoint);
	}
	
	public void addAllUnitEffectLevelField(String lower,String upper) {
		String fieldName = "ENDPOINT_STUDY_RECORD."+endpointKind+".ResultsAndDiscussion."+effectLevelString+".EffectLevel";
		String type = "range";
		Value endpointValue = new Value(type,lower,upper,new ToxUnit(endpointKind));
		QueryField endpoint = new QueryField(fieldName,type,"Effect Level",endpointValue);
		queryFields.add(endpoint);
	}
	
	/**
	 * Adds a QueryField to select test type of results
	 * @param	List of species to include
	 */
	public void addTestTypeField(List<String> testTypes,boolean includeOther) {
		List<Value> testTypeValues = new ArrayList<Value>();
		for (String t:testTypes) {
			testTypeValues.add(new Value("EQUALS",t));
		}
		if (includeOther) {
			testTypeValues.add(new Value("LIKE","other:*"));
		}
		QueryField testTypeField = new QueryField("ENDPOINT_STUDY_RECORD."+endpointKind+".MaterialsAndMethods.TestType","string","Test Type",testTypeValues);
		queryFields.add(testTypeField);
	}
	
	public void addAllTestTypeField() {
		Value testTypeValue = new Value("LIKE","*");
		QueryField testTypeField = new QueryField("ENDPOINT_STUDY_RECORD."+endpointKind+".MaterialsAndMethods.TestType","string","Test Type",testTypeValue);
		queryFields.add(testTypeField);
	}
	
	/**
	 * Adds a QueryField to select species of results
	 * @param	List of species to include
	 */
	public void addSpeciesField(List<String> species,boolean includeOther) {
		List<Value> speciesValues = new ArrayList<Value>();
		for (String s:species) {
			speciesValues.add(new Value("EQUALS",s));
		}
		if (includeOther) {
			speciesValues.add(new Value("LIKE","other:*"));
		}
		QueryField speciesField = new QueryField("ENDPOINT_STUDY_RECORD."+endpointKind+".MaterialsAndMethods.TestAnimals.Species","string","Species",speciesValues);
		queryFields.add(speciesField);
	}
	
	public void addAllSpeciesField() {
		Value speciesValue = new Value("LIKE","*");
		QueryField speciesField = new QueryField("ENDPOINT_STUDY_RECORD."+endpointKind+".MaterialsAndMethods.TestAnimals.Species","string","Species",speciesValue);
		queryFields.add(speciesField);
	}
	
	/**
	 * Adds a QueryField to select strain of results
	 * @param	List of strains to include
	 */
	public void addStrainField(List<String> strains,boolean includeOther) {
		List<Value> strainValues = new ArrayList<Value>();
		for (String s:strains) {
			strainValues.add(new Value("EQUALS",s));
		}
		if (includeOther) {
			strainValues.add(new Value("LIKE","other:*"));
		}
		QueryField strainField = new QueryField("ENDPOINT_STUDY_RECORD."+endpointKind+".MaterialsAndMethods.TestAnimals.Strain","string","Strain",strainValues);
		queryFields.add(strainField);
	}
	
	public void addAllStrainField() {
		Value strainValue = new Value("LIKE","*");
		QueryField strainField = new QueryField("ENDPOINT_STUDY_RECORD."+endpointKind+".MaterialsAndMethods.TestAnimals.Strain","string","Strain",strainValue);
		queryFields.add(strainField);
	}
	
	/**
	 * Adds a QueryField to select administration route of results
	 * @param	List of administration routes to include
	 */
	public void addAdministrationRouteField(List<String> routesOfAdministration,boolean includeOther) {
		List<Value> routeValues = new ArrayList<Value>();
		for (String r:routesOfAdministration) {
			routeValues.add(new Value("EQUALS",r));
		}
		if (includeOther) {
			routeValues.add(new Value("LIKE","other:*"));
		}
		QueryField routeField = new QueryField("ENDPOINT_STUDY_RECORD."+endpointKind+".MaterialsAndMethods.AdministrationExposure.RouteOfAdministration",
				"string","Route of Administration",routeValues);
		queryFields.add(routeField);
	}
	
	public void addAllAdministrationRouteField() {
		Value routeValue = new Value("LIKE","*");
		QueryField routeField = new QueryField("ENDPOINT_STUDY_RECORD."+endpointKind+".MaterialsAndMethods.AdministrationExposure.RouteOfAdministration",
				"string","Route of Administration",routeValue);
		queryFields.add(routeField);
	}
	
	/**
	 * Adds a QueryField to select inhalation exposure type of results
	 * @param	List of species to include
	 */
	public void addInhalationTypeField(List<String> inhalationTypes,boolean includeOther) {
		List<Value> inhalationTypeValues = new ArrayList<Value>();
		for (String i:inhalationTypes) {
			inhalationTypeValues.add(new Value("EQUALS",i));
		}
		if (includeOther) {
			inhalationTypeValues.add(new Value("LIKE","other:*"));
		}
		QueryField inhalationTypeField = new QueryField("ENDPOINT_STUDY_RECORD."+endpointKind+".MaterialsAndMethods.AdministrationExposure.TypeOfInhalationExposure",
				"string","Inhalation Exposure Type",inhalationTypeValues);
		queryFields.add(inhalationTypeField);
	}
	
	public void addAllInhalationTypeField() {
		Value inhalationTypeValue = new Value("LIKE","*");
		QueryField inhalationTypeField = new QueryField("ENDPOINT_STUDY_RECORD."+endpointKind+".MaterialsAndMethods.AdministrationExposure.TypeOfInhalationExposure",
				"string","Inhalation Exposure Type",inhalationTypeValue);
		queryFields.add(inhalationTypeField);
	}
	
	public void addCoverageTypeField(List<String> coverageTypes,boolean includeOther) {
		List<Value> coverageTypeValues = new ArrayList<Value>();
		for (String c:coverageTypes) {
			coverageTypeValues.add(new Value("EQUALS",c));
		}
		if (includeOther) {
			coverageTypeValues.add(new Value("LIKE","other:*"));
		}
		QueryField coverageTypeField = new QueryField("ENDPOINT_STUDY_RECORD."+endpointKind+".MaterialsAndMethods.AdministrationExposure.TypeOfCoverage",
				"string","Coverage Type",coverageTypeValues);
		queryFields.add(coverageTypeField);
	}
	
	public void addAllCoverageTypeField() {
		Value coverageTypeValue = new Value("LIKE","*");
		QueryField coverageTypeField = new QueryField("ENDPOINT_STUDY_RECORD."+endpointKind+".MaterialsAndMethods.AdministrationExposure.TypeOfCoverage",
				"string","Coverage Type",coverageTypeValue);
		queryFields.add(coverageTypeField);
	}
	
	/**
	 * Adds a QueryField to select dose descriptor of results
	 * @param	List of dose descriptors to include
	 */
	public void addDoseDescriptorField(List<String> doseDescriptors,boolean includeOther) {
		List<Value> doseDescriptorValues = new ArrayList<Value>();
		for (String d:doseDescriptors) {
			doseDescriptorValues.add(new Value("EQUALS",d));
		}
		if (includeOther) {
			doseDescriptorValues.add(new Value("LIKE","other:*"));
		}
		QueryField doseDescriptorField = new QueryField("ENDPOINT_STUDY_RECORD."+endpointKind+".ResultsAndDiscussion."+effectLevelString+".Endpoint",
				"string","Dose Descriptor",doseDescriptorValues);
		queryFields.add(doseDescriptorField);
	}
	
	public void addAllDoseDescriptorField() {
		Value doseDescriptorValue = new Value("LIKE","*");
		QueryField doseDescriptorField = new QueryField("ENDPOINT_STUDY_RECORD."+endpointKind+".ResultsAndDiscussion."+effectLevelString+".Endpoint",
				"string","Dose Descriptor",doseDescriptorValue);
		queryFields.add(doseDescriptorField);
	}
	
	public void addAllEndpointTypeField() {
		Value endpointTypeValue = new Value("LIKE","*");
		QueryField endpointTypeField = new QueryField("ENDPOINT_STUDY_RECORD."+endpointKind+".AdministrativeData.Endpoint",
				"string","Endpoint",endpointTypeValue);
		queryFields.add(endpointTypeField);
	}
}
