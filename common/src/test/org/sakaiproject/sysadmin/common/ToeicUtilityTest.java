//package org.sakaiproject.sysadmin.common;
//
//import static org.junit.Assert.assertNotNull;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import org.junit.BeforeClass;
//import org.junit.Test;
//import org.sakaiproject.sysadmin.common.model.Question;
//
//public class ToeicUtilityTest {
//	private static final String PATH_READ = "H:\\projects\\github.com\\Tuanskiet\\sakai-sysadmin\\ETS_2022_N1_processed.xlsx";
//	private static final String PATH_WRITE = "H:\\projects\\github.com\\Tuanskiet\\sakai-sysadmin\\ETS_2022_N1_processed_W.xlsx";
//	
//	static List<List<Question>> dataToeicTest;
//	
//	@BeforeClass
//	public static void init(){
//		dataToeicTest = new ArrayList<>();
//	}
//	
//	
//	@Test
//	public void testReadAllPart() {
//		dataToeicTest = ToeicUtility.readAllPart(PATH_READ);
//		assertNotNull(dataToeicTest);
//	}
//	
//	@Test
//	public void writeAllPart() {
//		ToeicUtility.writeAllPart(dataToeicTest, PATH_WRITE);
//	}
//}
