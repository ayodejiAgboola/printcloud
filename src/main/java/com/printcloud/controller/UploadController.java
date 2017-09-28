package com.printcloud.controller;

import com.printcloud.dao.FilesDao;
import com.printcloud.model.Job;
import com.printcloud.model.UploadRequest;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.ui.Model;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

@Controller
public class UploadController {
private FilesDao filesDao;
    //Save the uploaded file to this folder
    public UploadController(FilesDao filesDao){
        this.filesDao=filesDao;
    }
    private static String UPLOADED_FOLDER = "src\\main\\resources\\files\\";

    @GetMapping("/")
    public String index() {
        return "upload";
    }
    @GetMapping("/upload")
    public String getUpload() {
        return "upload";
    }

    @PostMapping("/upload") // //new annotation since 4.3
    public String singleFileUpload(@RequestParam("file") MultipartFile file, UploadRequest request, Model model) throws Exception{

        if (file.isEmpty()) {
            model.addAttribute("statusMessage", "Please select a file to upload!");
            return "upload";
        }
        if (file.getContentType().equals("application/pdf")||file.getContentType().equals("application/msword")){
            try {
                // Get the file and save it somewhere
                byte[] bytes = file.getBytes();
                String ext = FilenameUtils.getExtension(file.getOriginalFilename());
                Path path = Paths.get(UPLOADED_FOLDER + file.getOriginalFilename()+new Date().getTime()+"."+ext);
                System.out.println(path.toAbsolutePath());
                Files.write(path, bytes);
                Job job = new Job();
                job.setFileName(path.getFileName().toString());
                job.setFilePath(path.toAbsolutePath().toString());
                job.setFileType(file.getContentType());
                job.setUploadDate(new Date().toString());
                job.setPickupDate(request.getPickupDate());
                job.setColor(request.getColor());
                job.setSides(request.getSides());
                job.setSpecs(request.getSpecs());
                job.setOwnerName(request.getName());
                job.setOwnerEmail(request.getEmail());
                job.setOwnerPhone(request.getPhone());
                job.setStatus("0");
                filesDao.save(job);
                /*redirectAttributes.addFlashAttribute("message",
                        "You successfully uploaded '" + file.getOriginalFilename() + "'");*/
                model.addAttribute("statusMessage", "You successfully uploaded '"+ file.getOriginalFilename() + "'");
                return "upload";
            } catch (Exception e) {
                model.addAttribute("statusMessage", "Something seems to be wrong, please contact the system administrator");
                e.printStackTrace();
                return "upload";
            }
        }else{
            /*redirectAttributes.addFlashAttribute("Error",
                    "Invalid file type for '" + file.getOriginalFilename() + "'");*/
            model.addAttribute("statusMessage", "Invalid file type for '" + file.getOriginalFilename() + "'");
            return "upload";
            //throw new Exception("invalid file");
        }
    }

}