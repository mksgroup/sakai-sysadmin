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

package org.sakaiproject.sysadmin.controller;

import java.io.File;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.sakaiproject.sysadmin.common.FileUtility;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import lombok.extern.slf4j.Slf4j;

/**
 * Handles requests for the application home page.
 */
@Controller
@Slf4j
public class HomeController extends BaseController {
	
	@Value("${upload.folder}")
	private String folderUpload = "";
	
	@Value("${bin.folder}")
	private String binFolder = "";
 
	   /**
     * This method is called when binding the HTTP parameter to bean (or model).
     * 
     * @param binder
     */
    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        // Sample init of Custom Editor

//        Class<List<ItemKine>> collectionType = (Class<List<ItemKine>>)(Class<?>)List.class;
//        PropertyEditor orderNoteEditor = new MotionRuleEditor(collectionType);
//        binder.registerCustomEditor((Class<List<ItemKine>>)(Class<?>)List.class, orderNoteEditor);

    }
    
	/**
	 * Simply selects the home view to render by returning its name.
     * @return 
	 */
	@RequestMapping(value = {"/home"}, method = RequestMethod.GET)
	public ModelAndView displayHome(HttpServletRequest request, HttpSession httpSession) {
		ModelAndView mav = new ModelAndView("home");
		initSession(request, httpSession);
		mav.addObject("currentSiteId", getCurrentSiteId());
		mav.addObject("userDisplayName", getCurrentUserDisplayName());
		return mav;
	}
	
	@RequestMapping(value = {"/", "/upload-file"}, method = RequestMethod.GET)
	public ModelAndView displayUploadFile(HttpServletRequest request, HttpSession httpSession) {
		ModelAndView mav = new ModelAndView("upload_file");
		initSession(request, httpSession);
		mav.addObject("currentSiteId", getCurrentSiteId());
		mav.addObject("userDisplayName", getCurrentUserDisplayName());
		mav.addObject("listFiles", FileUtility.getListFiles(folderUpload));
		mav.addObject("listFilesBackup", FileUtility.getListFiles(binFolder));
		return mav;
	}
	
	@RequestMapping(value = "/upload-file", method = RequestMethod.POST)
	public ModelAndView doUploadFile(
			@RequestParam(name = "file_upload") MultipartFile multipartFile,
			HttpServletRequest request, 
			HttpSession httpSession,
			Model model) {
		ModelAndView mav = new ModelAndView("upload_file");
		// initSession(request, httpSession);

		String bkFilename = saveUploadedFile(multipartFile, folderUpload);
		if (bkFilename != null) {
			mav.addObject("bkFilename", bkFilename);
		}
		mav.addObject("nameFileUploaded", multipartFile.getOriginalFilename());

		mav.addObject("listFiles", FileUtility.getListFiles(folderUpload));
		mav.addObject("listFilesBackup", FileUtility.getListFiles(binFolder));

		return mav;
	}

	@RequestMapping(value = "/move-to-bin", method = RequestMethod.GET)
	public ModelAndView doDeleteFile(@RequestParam(name = "fileName") String fileName) {
		ModelAndView mav = new ModelAndView("redirect:/upload-file");
		FileUtility.copyFile(binFolder, folderUpload, fileName);
		return mav;
	}
	
	@RequestMapping(value = "/edit-name-file", method = RequestMethod.POST)
	public ModelAndView doUpdateNameFile(
			@RequestParam(name = "oldName") String oldName,
			@RequestParam(name = "newName") String newFileName
			) {
		ModelAndView mav = new ModelAndView("redirect:/upload-file");
		File oldFile = new File(folderUpload+ File.separator + oldName);
        if (oldFile.exists()) {
            File newFile = new File(folderUpload + File.separator + newFileName);
            oldFile.renameTo(newFile);
        } else {
            log.warn("File does not exist !");
        }
		return mav;
	}
	
	@RequestMapping(value = "/{bucket}/view-file/{fileName}", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<?> displayDataFile(
			@PathVariable(name = "bucket") String bucket,
			@PathVariable(name = "fileName") String filename
			){
		String filePath = "";
		if(bucket.equalsIgnoreCase("storage")) { // access in storage 
			filePath = folderUpload + File.separator + filename;
		}else if (bucket.equalsIgnoreCase("bin")) { // access in bin 
			filePath = binFolder + File.separator + filename;
		}else {
			log.warn("Can't find path {}", bucket);
		}
		byte[] files = FileUtility.getFile(filePath);
		return ResponseEntity.ok()
				.contentType(MediaType.valueOf("image/png"))
				.body(files);
	}

	@RequestMapping(value = "/restore-file/{fileName}", method = RequestMethod.GET)
	public ModelAndView restoreFile(@PathVariable(name = "fileName") String fileName) {
		ModelAndView mav = new ModelAndView("redirect:/upload-file"); 
		FileUtility.copyFile(folderUpload, binFolder, fileName);
		return mav;
	}
	
	@RequestMapping(value = "/delete-file/{fileName}", method = RequestMethod.GET)
	public ModelAndView deleteFile(@PathVariable(name = "fileName") String fileName) {
		ModelAndView mav = new ModelAndView("redirect:/upload-file"); 
		String filePath = binFolder + File.separator + fileName;
		File fileDelete = new File(filePath);
		if(fileDelete.isFile() && fileDelete.exists()) {
			fileDelete.delete();
			log.info("1 file removed with path: "+ filePath);
		}
		return mav;
	}
	
	/**
	 * Save the uploaded file to disk.
	 * @param multipartFile uploaded file object.
	 * @param folderUpload folder will contain the uploaded file.
	 * @return null if the name of uploaded is not existed in the folder.
	 * Otherwise, the new name of backup file is returned.
	 */
	private static String saveUploadedFile(MultipartFile multipartFile, String folderUpload) {
		String fileName = multipartFile.getOriginalFilename();

		if (multipartFile != null) {
			String newName = FileUtility.renameExistedFile(multipartFile.getOriginalFilename(), folderUpload);
			String filePath = folderUpload + fileName;
			File file = new File(filePath);

			try {
				multipartFile.transferTo(file);
				log.info("1 file uploaded with path: " + filePath);
			} catch (IllegalStateException | IOException e) {
				log.warn("Error when upload file : " + e.getMessage());
			}
			
			return newName;
		}

		return null;
	}
}
