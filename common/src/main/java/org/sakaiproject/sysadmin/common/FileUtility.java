/**
 * Licensed to MKS Group under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * MKS Group licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a
 * copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.sakaiproject.sysadmin.common;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
/**
 * An example Utility.
 * 
 * @author Thach Ngoc Le (ThachLN@mks.com.vn)
 *
 */
@Slf4j
public class FileUtility {

	
	public static List<String> getListFiles(String folder){
		List<String> listNameFiles = new ArrayList<>();
		File directory = new File(folder);
		if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
            	listNameFiles = Arrays.stream(files)
                        .filter(File::isFile)
                        .map(File::getName).collect(Collectors.toList());
            }
        }
        return listNameFiles;
	}


	/**
	 * Rename existing file to backup. The pattern of new name is: <old name>_yyyyMMdd_HHmmss.<old ext>.
	 * @param fileName file name to be checked to rename.
	 * @param folder folder contains file
	 * @return new name of file.
	 * null in case of the file of fileName is not existed.
	 */
	public static String renameExistedFile(String fileName, String folder) {

		String filePath = folder + File.separator + fileName;
		File file = new File(filePath);

		if (file.exists()) {
			int seperator = fileName.lastIndexOf(".");
			String extension = fileName.substring(seperator);

			String name = fileName.substring(0, seperator);
			String currentTime = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
			String newName= name + "_" + currentTime + extension;
			
			boolean renameOK = file.renameTo(new File(folder + File.separator + newName));
			log.info("Renamed is " + renameOK);
			
			return newName;
		} else {
			return null;
		}
	}


	public static void copyFile( String targetFolder, String sourceFolder, String fileName) {
		String sourcePath = sourceFolder + File.separator + fileName;
		File sourceFile = new File(sourcePath);
		if(sourceFile.exists() && sourceFile.isFile()) {
			try {
				Path targetPath = Paths.get(targetFolder + File.separator + fileName);
				byte[] newFile = Files.readAllBytes(new File(sourcePath).toPath());
				Files.createDirectories(targetPath.getParent());
				Files.write(targetPath, newFile);
				
				log.info("Move file with name : {} to folder : {}", fileName, targetFolder);
			} catch (IOException e) {
				log.warn("Error when move file : "+ e.getMessage());
			}
			sourceFile.delete();
		}
	}
	
	
	public static byte[] getFile(String filePath) {
		try {
			byte[] files = Files.readAllBytes(new File(filePath).toPath());
			 return files;
		} catch (IOException e) {
			log.warn("Error when read file : {}",  e.getMessage());
			return  null;
		}
		
	}
	/*
	 * String fileName = StringUtils.cleanPath(file.getOriginalFilename()); Path
	 * path = Paths.get(app.getRealPath(fileName));
	 * Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
	 */

}
