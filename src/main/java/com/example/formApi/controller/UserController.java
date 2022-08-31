package com.example.formApi.controller;

import com.example.formApi.domain.Users;
import com.example.formApi.service.FormService;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.StringJoiner;
import java.util.regex.Pattern;

@CrossOrigin(origins = "*", allowedHeaders = "*")

@Controller
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private FormService service;

    // Return all users
    @ResponseBody
    @GetMapping (path="/api/users")
    public  Object getusers() {
        List<Users> listUsers = service.listAll();
        if(listUsers.size()==0){
            return ResponseEntity.status(HttpStatus.OK).body("No Users Found");
        }
        return listUsers;
    }

    // Return user of id
    @GetMapping(path="/api/user/{id}")
    public @ResponseBody Object getUser(@PathVariable(name = "id") int id) {
        try{
            Users std = service.get(id);
            return  std;
        }catch(NoSuchElementException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User Not Found");
        }


    }

    // create new user
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    @PostMapping(value = "/api/user/new", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Object createuser(@RequestBody JSONObject user) {

        List<String> req=new ArrayList<>();
        if (!user.containsKey("email") || user.get("email")=="") {
            req.add("Email");
        }
        if (!user.containsKey("name") || user.get("name")=="") {
            req.add("Name");

        }
        if (!user.containsKey("country") || user.get("country")=="") {
            req.add("Country");

        }
        if (!user.containsKey("gender") || user.get("gender")=="") {
            req.add("Gender");

        }
        if (!user.containsKey("feature") || user.get("feature")=="") {
            req.add("Feature");

        }
        if (!user.containsKey("agreecond") || user.get("agreecond")=="") {
            req.add("Agree Conditions");
        }

        if(!req.isEmpty()){
            StringJoiner joiner = new StringJoiner(",");
            req.forEach(item -> joiner.add(item.toString()));
            String error="Required "+joiner.toString();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
        Pattern EMAIL_REGEX = Pattern.compile("[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", Pattern.CASE_INSENSITIVE);
        if(user.get("email").toString().length() > 256 || !EMAIL_REGEX.matcher(user.get("email").toString()).matches()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Email");
        }

        try{
            Users data=jsonObjToUserClass(user);
            Users response=service.save(data);
            return Math.toIntExact(response.getId());
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    // edit user by id
    @RequestMapping(value = "/api/user/edit", method = RequestMethod.POST)
    public Object edituser(@RequestBody JSONObject user) {
        List<String> req=new ArrayList<>();
        System.out.println(user);
        if (!user.containsKey("id")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Unable to find User Id");
        }

        if (!user.containsKey("email")) {
            req.add("Email");
        }
        if (!user.containsKey("name")) {
            req.add("Name");

        }
        if (!user.containsKey("country")) {
            req.add("Country");

        }
        if (!user.containsKey("gender")) {
            req.add("Gender");

        }
        if (!user.containsKey("feature")) {
            req.add("Feature");

        }
        if (!user.containsKey("agreecond")) {
            req.add("Agree Conditions");

        }

        if(!req.isEmpty()){
            StringJoiner joiner = new StringJoiner(",");
            req.forEach(item -> joiner.add(item.toString()));
            String error="Required "+joiner.toString();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
        Pattern EMAIL_REGEX = Pattern.compile("[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", Pattern.CASE_INSENSITIVE);
        if(user.get("email").toString().length() > 256 || !EMAIL_REGEX.matcher(user.get("email").toString()).matches()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Email");
        }

        Users data=jsonObjToUserClass(user);
        try{
            Users std = service.get(data.getId());
        }catch(NoSuchElementException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User Not Found with ID:"+data.getId());
        }
        try{
            service.save(data);
            return ResponseEntity.status(HttpStatus.OK).body("User Updated");
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    private Users jsonObjToUserClass(JSONObject user) {
        Users data=new Users();
        if(user.containsKey("id")){
            data.setId(Long.parseLong(String.valueOf(user.get("id"))));
        }
        data.setEmail(user.get("email").toString());
        data.setName(user.get("name").toString());
        data.setGender(user.get("gender").toString());
        data.setCountry(user.get("country").toString());
        data.setFeature(Boolean.parseBoolean(String.valueOf(user.get("feature"))));
        data.setAgreecond(Boolean.parseBoolean(String.valueOf(user.get("agreecond"))));
        return data;
    }

    // delete user by id
    @DeleteMapping("/api/user/{id}")
    public Object deleteuser(@PathVariable(name = "id") int id) {
        try{
            Users std = service.get(id);
        }catch(NoSuchElementException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User Not Found");
        }

        service.delete(id);
        return ResponseEntity.status(HttpStatus.OK).body("User Deleted");

    }


    // return homepage of users list
    @GetMapping("/")
    public String viewHomePage(Model model) {
        return "users";
    }

    @GetMapping("/new")
    public String add(Model model) {
        return "newUser";
    }

//    @RequestMapping(value = "/save", method = RequestMethod.POST)
//    public String saveUser(@ModelAttribute("user") Users std) {
//        service.save(std);
//        return "redirect:/";
//    }
//
//    @RequestMapping("/edit/{id}")
//    public ModelAndView showEdituserPage(@PathVariable(name = "id") int id) {
//        ModelAndView mav = new ModelAndView("new");
//        Users std = service.get(id);
//        mav.addObject("user", std);
//        return mav;
//
//    }
//    @RequestMapping("/delete/{id}")
//    public String deleteuserpage(@PathVariable(name = "id") int id) {
//        service.delete(id);
//        return "redirect:/";
//    }
}
