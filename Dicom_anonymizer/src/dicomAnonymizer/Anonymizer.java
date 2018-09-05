package dicomAnonymizer;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

import org.dcm4che2.data.DicomObject;
import org.dcm4che2.data.VR;
import org.dcm4che2.io.DicomInputStream;
import org.dcm4che2.io.DicomOutputStream;
import org.dcm4che2.util.CloseUtils;

/**
 * @author Krzysztof Szewczyk
 */
public class Anonymizer {

	/**
	 * Dicom Data
	 */
	private DicomObject dcm;
	/**
	 * Destination directory
	 */
	private String destination;
	/**
	 * Array with tags to change
	 */
	private Boolean[] bool;
	/**
	 * Tags' list
	 */
	private int[] tags = DicomTags.tags;
	/**
	 * Value Representation
	 */
	private VR[] vr = DicomTags.vr;
	/**
	 * Date to name dicoms
	 */
	private String date;
	/**
	 * Number of dicom file from a pack
	 */
	private int nr;

	/**
	 * 
	 * @param dicom
	 *            File path
	 * @param dicomOldName
	 *            Primary dicom name
	 * @param bool
	 *            Boolean array: true - tags to change, false - not change
	 * @param destination
	 *            Destination direcotory
	 * @param nr
	 *            Number of dicom file from a pack
	 * @param date
	 *            Date
	 * @param db
	 *            DBCommunication
	 */
	public Anonymizer(String dicom, String dicomOldName, Boolean[] bool, String destination, int nr, String date,
			DBConnection db) {
		// otwieranie pliku dicom
		openDicom(dicom);
		// settery
		this.bool = bool;
		this.destination = destination;
		this.nr = nr;
		this.date = date;

		clearAndSaveDicomData(db, dicomOldName);
	}

	/**
	 * Dicom loading
	 * 
	 * @param dicom
	 */
	void openDicom(String dicom) {
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
	 * Anonimize selected tags and send patient's data (with unique id) to db and
	 * safe new dicom file using saveDicom(int id)
	 */
	void clearAndSaveDicomData(DBConnection db, String dicomOldName) {
		// tmp - all tags
		String[] tmp = new String[tags.length];
		for (int i = 0; i < tags.length; i++) {
			tmp[i] = dcm.getString(tags[i]);
			// deleting selected tags
			if (bool[i]) {
				dcm.putString(tags[i], vr[i], " ");
			}
		}
		// creating ID
		int randomNum = ThreadLocalRandom.current().nextInt(100000, 999999 + 1);
		String id = "id" + (db.rowsNumbers() + 1) + "_" + randomNum;
		// new file name
		String dicomName = "A_dicom" + date + "_" + nr;
		// save deleted tags
		db.insetIntoTable(tmp, id, dicomName, dicomOldName);
		// ID number is stored on tag
		// (0012,0062) - Patient Idendity Removed CS
		String idDicom = String.valueOf(id);
		System.out.println("Identyfikator: " + idDicom);
		dcm.putString(0x120062, VR.CS, idDicom);
		// save new dicom
		saveDicom(dicomName);
	}

	/**
	 * Give new name and save in selected destination directory
	 */
	private void saveDicom(String dicomName) {
		System.out.println("zapisano");
		// tworzenie nowego pliku
		File f = new File(destination + "\\" + dicomName + ".dcm");
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
}
