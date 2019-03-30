package dicomAnonymizer;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.dcm4che2.data.DicomObject;
import org.dcm4che2.data.VR;
import org.dcm4che2.io.DicomInputStream;
import org.dcm4che2.io.DicomOutputStream;
import org.dcm4che2.util.CloseUtils;

/**
 * @author Krzysztof Szewczyk
 *
 */
public class Deanonymizer {

	/**
	 * dicom data
	 */
	private DicomObject dcm = null;
	/**
	 * destination directory
	 */
	private String destination;
	/**
	 * Tags array to change
	 */
	private Boolean[] bool;
	/**
	 * dicom name
	 */
	private String dicomNameLoaded;
	/**
	 * compatibility id
	 */
	private boolean errorFile = false;

	private int[] tags = DicomTags.tags;
	private VR[] vr = DicomTags.vr;

	// next number with naming dicom
	private int nr;
	private String date;

	/**
	 * 
	 * @param dicomPath
	 *            - path to the dicom file
	 * @param whichName
	 *            - type of naming
	 * @param textOwnName
	 *            - the name entered by the user
	 * @param bool
	 *            - an array with information about which tag to be restored
	 * @param destination
	 *            - destination directory
	 * @param date
	 *            - Date for naming
	 * @param nr
	 *            - the dicom file number in the package
	 * @param db
	 *            - DBConnection
	 */
	public Deanonymizer(String dicomPath, byte whichName, String textOwnName, Boolean[] bool, String destination,
			String date, int nr, DBConnection db) {
		// setters
		this.destination = destination;
		this.bool = bool;
		this.nr = nr;
		this.date = date;
		// 1. Obtaining the name of the currently processed file
		int ind = 0;
		for (int i = 0; i < dicomPath.length(); i++) {
			if (dicomPath.charAt(i) == '\\') {
				ind = i;
			}
		}
		this.dicomNameLoaded = dicomPath.substring((ind + 1), (dicomPath.length()));
		// 2. opening a dicom file
		openDicom(dicomPath);
		// 3. searching for a file in the database and validation
		// Data validation.
		// If the ID does not match, then NULL.
		// (must be the same NAME and ID that were given)
		String[] dicomData = db.findDicomData(dicomNameLoaded, dcm.getString(0x120062));

		if (dicomData != null) {
			// 4. set name
			String dicomNameNew = setDicomNameNew(whichName, textOwnName, dicomData[0]);
			// 5. restoring data to a deanonimised dicom file
			RevertDicomData(dicomData);
			// 6. we save the new dicom in the selected directory
			saveDicom(dicomNameNew);
		} else {
			// 7. counting erroneous files
			this.errorFile = true;
		}
	}

	/**
	 * Specifying the name of a new file
	 * 
	 * @param whichName
	 * @param textOwnName
	 * @return
	 */
	private String setDicomNameNew(byte whichName, String textOwnName, String originalName) {
		/*
		 * whichName: 1 - current name 2 - original name 3 - random name 4 - own name
		 */
		switch (whichName) {
		case 1:
			return dicomNameLoaded;
		case 2:
			return originalName;
		case 3:
			return "DA_dicom" + date + "_" + nr;
		case 4:
			return textOwnName + "_" + nr;
		}
		return null;
	}

	/**
	 * Loading Dicom'u
	 * 
	 * @param dicom
	 */
	private void openDicom(String dicom) {
		DicomInputStream dis = null;
		DicomObject dcm = null;
		try {
			dis = new DicomInputStream(new File(dicom)); // load DICOM
			dcm = dis.readDicomObject(); // read DICOM Tags
		} catch (IOException e) {
			System.out.println("Error while reading DICOM tags");
			e.printStackTrace();
		} finally {
			if (dis != null) {

				CloseUtils.safeClose(dis); // safe closing
			}
		}
		this.dcm = dcm;
	}

	/**
	 * Restores the original tags to the DICOM file
	 * 
	 * @return Returns the names of files whose ID does not match that in the array
	 */
	void RevertDicomData(String[] dicomData) {
		System.out.println("Nazwa wczytanego pliku dicom: " + dicomNameLoaded);
		System.out.println("Identyfikator wczytanego pliku DICOM: " + dcm.getString(0x120062));
		// putting the original data into the DICOM file
		for (int i = 1; i < dicomData.length; i++) {
			if (bool[i - 1] == true) {
				dcm.putString(tags[i - 1], vr[i - 1], dicomData[i - 1]);
			}
		}
	}

	/**
	 * Properly calls the new dicom and saves in the target directory
	 */
	private void saveDicom(String dicomNameNew) {
		System.out.println("zapisano");
		File f = new File(destination + "\\" + dicomNameNew + ".dcm");
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(f);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}
		BufferedOutputStream bos = new BufferedOutputStream(fos);
		DicomOutputStream dos = new DicomOutputStream(bos);
		try {
			dos.writeDicomFile(dcm);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		} finally {
			try {
				dos.close();
			} catch (IOException ignore) {
			}
		}
	}

	/**
	 * getter of file error
	 * 
	 * @return true if error
	 */
	boolean getErrorFile() {
		return errorFile;
	}
}
