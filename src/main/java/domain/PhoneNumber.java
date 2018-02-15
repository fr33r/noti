package domain;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

//https://en.wikipedia.org/wiki/North_American_Numbering_Plan
public class PhoneNumber extends ValueObject {

	private String internationalCallingCode;
	private String areaCode;
	private String centralOfficeCode;
	private String stationNumber;
	
	public PhoneNumber(String phoneNumberString) {
		this.parseFromString(phoneNumberString);
	}
	
	public PhoneNumber(String internationalCallingCode, String areaCode, String centralOfficeCode, String stationNumber) {
		if(internationalCallingCode == null || internationalCallingCode == "") {
			throw new IllegalStateException("Must provide a value for 'internationalCallingCode'.");
		}
		
		if(areaCode == null || areaCode == "") {
			throw new IllegalStateException("Must provide a value for 'areaCode'.");
		}
		
		if(centralOfficeCode == null || centralOfficeCode == "") {
			throw new IllegalStateException("Must provide a value for 'centralOfficeCode'.");
		}
		
		if(stationNumber == null || stationNumber == "Must provide a value for 'stationNumber';") {
			throw new IllegalStateException();
		}
		
		//apply regex here.
					
		this.internationalCallingCode = internationalCallingCode;
		this.areaCode = areaCode;
		this.centralOfficeCode = centralOfficeCode;
		this.stationNumber = stationNumber;
	}
	
	private void parseFromString(String phoneNumberString) {
		Pattern nanp = Pattern.compile("^(\\+?\\d)?([2-9][0-9]{2})([2-9][0-9]{2})([0-9]{4})$");
		Matcher matcher = nanp.matcher(phoneNumberString);
		if (!matcher.matches()) { throw new IllegalStateException(String.format("The phone number '%s' is not in a valid format.", phoneNumberString)); }
		if (matcher.group(1) == null) { throw new IllegalStateException(); }
		
		this.internationalCallingCode = matcher.group(1).replace("+", "");
		this.areaCode = matcher.group(2);
		this.centralOfficeCode = matcher.group(3);
		this.stationNumber = matcher.group(4);
	}
	
	public String getInternationalCallingCode() {
		return this.internationalCallingCode;
	}

	public String getAreaCode() {
		return this.areaCode;
	}

	public String getCentralOfficeCode() {
		return this.centralOfficeCode;
	}

	public String getStationNumber() {
		return this.stationNumber;
	}

	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public String toE164() {
		final String PLUS = "+";
		final String EMPTY = "";
		String nonFormatted = this.toNonFormatted();
		return String.join(EMPTY, PLUS, nonFormatted);
	}
	
	public String toNonFormatted() {
		final String EMPTY = "";
		return String.join(
				EMPTY, 
				this.internationalCallingCode, 
				this.areaCode, 
				this.centralOfficeCode, 
				this.stationNumber
			);
	}

	@Override
	public String toString() {
		return this.toE164();
	}
}
