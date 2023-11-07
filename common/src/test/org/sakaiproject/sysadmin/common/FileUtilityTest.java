package org.sakaiproject.sysadmin.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FileUtilityTest {


	/**
	 * Notduplicate
	 * Folder does not contains file "abc.txt"
	 */
	@Test
	public void testRenameExistedFile_001() {
		String fileName = "abc.txt";
		
		// mkdir D:\Temp\sysadmin-tes
		String folder = "D:/Temp/sysadmin-test";
		
		String newFilename = FileUtility.renameExistedFile(fileName, folder);
		
		assertNull(newFilename);
	}

	/**
	 * Filename is existing in the folder
	 * Prepare data: echo "" > D:/Temp/sysadmin-test/xyz.txt
	 */
	@Test
	public void testRenameExistedFile_002() {
		String fileName = "xyz.txt";
		
		// mkdir D:\Temp\sysadmin-tes
		String folder = "D:/Temp/sysadmin-test";
		
		// Assumuption: the method FileUtility.renameExistedFile spend 1 second less. 
		Date currentTime = new Date();
		String newFilename = FileUtility.renameExistedFile(fileName, folder);
		
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
		String exptectedFilename = "xyz_" + sdf.format(currentTime) + ".txt";
		
		// Check newFilename: xzy_yyyyMMdd_HHmmss.txt
		log.info("newFilename=" + newFilename);
		assertEquals(exptectedFilename, newFilename);
	}

	/**
	 * Check in case of the folder is not permission to rename.
	 */
	@Test
	public void testRenameExistedFile_003() {
		
	}
}
