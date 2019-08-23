package com.example.demo;

import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Map;


@Controller
public class HomeController {

    @Autowired
    MessageRepository messageRepository;
    @Autowired
    CloudinaryConfig cloudc;

    @RequestMapping("/")
    public String ListMessages(Model model){
        model.addAttribute("messages", messageRepository.findAll());
        return "messagelist";
    }
    @GetMapping("/add")
    public String messageForm(Model model){
        model.addAttribute("message", new Message());
        return "messageform";
    }

    @PostMapping("/process")
    public String processMessage(@ModelAttribute Message message,
                                 @RequestParam("file")MultipartFile file) {
        if (file.isEmpty()) {
            return "redirect:/messageform";
        }
        try {
            Map uploadResult = cloudc.upload(file.getBytes(),
                    ObjectUtils.asMap("resourcetype", "auto"));
            message.setImage(uploadResult.get("url").toString());
            messageRepository.save(message);
        } catch (IOException e) {
            e.printStackTrace();
            return "redirect:/add";
        }
        messageRepository.save(message);
        return "redirect:/";

    }
    @RequestMapping("/detail/{id}")
public  String showMessage(@PathVariable("id") long id, Model model){
    model.addAttribute("message", messageRepository.findById(id).get());
    return "showmessage";
    }

    @RequestMapping("/delete/{id}")
    public String delMessage(@PathVariable("id") long id){
        messageRepository.deleteById(id);
        return "redirect:/";
    }
    @RequestMapping("/update/{id}")
    public String updateMessage(@PathVariable("id") long id,
                                Model model){
        model.addAttribute("message", messageRepository.findById(id).get());
        return "messageform";
    }
}

