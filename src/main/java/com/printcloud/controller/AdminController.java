package com.printcloud.controller;

import com.printcloud.dao.FilesDao;
import com.printcloud.dao.UsersDao;
import com.printcloud.model.Job;
import com.printcloud.model.LoginRequest;
import com.printcloud.model.User;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.springframework.ui.Model;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;


@Controller
public class AdminController {
    public UsersDao usersDao;
    public FilesDao filesDao;
    public AdminController(UsersDao usersDao, FilesDao filesDao){
        this.usersDao=usersDao;
        this.filesDao=filesDao;
    }
    @GetMapping("/home")
    public String home(HttpSession session){

                return "home";
    }
    @GetMapping("/login")
    public String login(){

                return "login";

    }
    @PostMapping("/register")
    public String register(@RequestBody User request){
        String username = request.getUsername();
        String password = BCrypt.hashpw(request.getPassword(), BCrypt.gensalt());
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        usersDao.save(user);
        return "login";
    }
   @PostMapping("/signin")
   public String signin(@RequestParam String username, @RequestParam String password, HttpSession session, Model model){
        System.out.println(username+" "+ password);
       User user = usersDao.findByUsername(username);
       boolean passwordIsCorrect = BCrypt.checkpw(password, user.getPassword());
       if(passwordIsCorrect){
           List <Job> pendingJobs = filesDao.findByStatus("0");
           session.setAttribute("loginStatus",true);
           model.addAttribute("pendingJobs", pendingJobs);
           return "home";
       }else{
           return "login";
       }

   }
    @PostMapping("/update/{jobId}")
    public String updateStatus(@PathVariable("jobId") String jobId){
        return "home";
    }

    @RequestMapping(value = "/files", method = RequestMethod.GET)
    public StreamingResponseBody getSteamingFile(HttpServletResponse response, @RequestParam("fileid") String fileid) throws IOException {
        Job job = filesDao.findOne(fileid);
        System.out.println("file=>"+job.getFilePath());
            File file = new File(job.getFilePath());
            response.setContentType(job.getFileType());
            response.setHeader("Content-Disposition", "attachment; filename=\""+file.getName()+"\"");
            InputStream inputStream = new FileInputStream(file);
            return outputStream -> {
                int nRead;
                byte[] data = new byte[1024];
                while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                    System.out.println("Writing some bytes..");
                    outputStream.write(data, 0, nRead);
                }
            };
        }
}
