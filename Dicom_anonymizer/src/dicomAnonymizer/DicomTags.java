package dicomAnonymizer;

import org.dcm4che2.data.VR;

/**
 * Klasa stores tags, VR and descriptions
 * @author Krzysztof Szewczyk
 *
 */
public final class DicomTags {
	/**
	 * Array of all tags.
	 */
	public static final int[] tags = {
			0x00080014,
			0x00080050,
			0x00080080,
			0x00080081,
			0x00080090,
			0x00080092,
			0x00080094,
			0x00080010,
			0x00081030,
			0x0008103E,
			0x00081040,
			0x00081048,
			0x00081050,
			0x00081060,
			0x00081070,
			0x00081080,
			0x00082111,
			0x00100010,
			0x00100020,
			0x00100030,
			0x00100032,
			0x00100040,
			0x00101000,
			0x00101001,
			0x00101010,
			0x00101020,
			0x00101030,
			0x00101090,
			0x00102160,
			0x00102180,
			0x001021B0,
			0x00104000,
			0x00181000,
			0x00181030,
			0x00200010,
			0x00200052,
			0x00200200,
			0x00204000,
			0x0040A124,
			0x00880140,
			0x30060024,
			0x300600C2 };
	
	/**
	 * Array of all VR.
	 */
	public static final VR[] vr = {
			VR.UI,
			VR.SH,
			VR.LO,
			VR.ST,
			VR.PN,
			VR.ST,
			VR.SH,
			VR.SH,
			VR.LO,
			VR.LO,
			VR.LO,
			VR.PN,
			VR.PN,
			VR.PN,
			VR.PN,
			VR.LO,
			VR.ST,
			VR.PN,
			VR.LO,
			VR.DA,
			VR.TM,
			VR.CS,
			VR.LO,
			VR.PN,
			VR.AS,
			VR.DS,
			VR.DS,
			VR.LO,
			VR.SH,
			VR.SH,
			VR.LT,
			VR.LT,
			VR.LO,
			VR.LO,
			VR.SH,
			VR.SH,
			VR.UI,
			VR.LT,
			VR.UI,
			VR.UI,
			VR.UI,
			VR.UI };
	
	/**
	 * Array of all descriptions
	 */
	public static final String[] info = {
			"Instance Creator UID",
			"AccessionNumber",
			"Institution Name Attribute",
			"Institution Address Attribute",
			"Referring Physician's Name Attribute",
			"Referring Physician's Address",
			"Referring Physician's Telephone Numbers",
			"Recognition Code",
			"Study Description",
			"Series Description",
			"Institutional Department Name",
			"Physician(s) of Record",
			"Performing Physician's Name",
			"Name of Physician(s) Reading Study",
			"Operators' Name",
			"Admitting Diagnoses Description",
			"Derivation Description",
			"Patient's Name",
			"Patient ID",
			"Patient's Birth Date",
			"Patient's Birth Time",
			"Patient's Sex",
			"Other Patient IDs",
			"Other Patient Names",
			"Patient's Age",
			"Patient's Size",
			"Patient's Weight",
			"Medical Record Locator",
			"Ethnic Group",
			"Occupation",
			"Additional Patient History",
			"Patient Comments",
			"Device Serial Number",
			"Protocol Name",
			"Study ID",
			"Frame of Reference UID",
			"Synchronization Frame of Reference UID",
			"Image Comments",
			"UID",
			"Storage Media File-set UID",
			"Referenced Frame of Reference UID",
			"Related Frame of Reference UID" };

	// SH - short string
	// LO - long string
	// ST - short text
	// LT - long text
	// PN - person name
	// DA - date
	// TM - time
	// CS - code string
	// AS - age string
	// DS - decimal string
	// UI - unique identifier
}
