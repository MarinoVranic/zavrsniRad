package com.vranic.zavrsnirad.controller;

import com.vranic.zavrsnirad.model.Korisnik;
import com.vranic.zavrsnirad.model.User;
import com.vranic.zavrsnirad.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/appUsers")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/all")
    public String getAllUsers(Model model){
        model.addAttribute("allUsers", userService.getAllUsers());
        return "appUsers/appUsers";
    }

    @GetMapping("/addNew")
    public String addNewUser(Model model){
        User user = new User();
        model.addAttribute("user", user);
        return "appUsers/newAppUser";
    }


    @PostMapping("/addNew")
    public String addUser(@ModelAttribute("user") @Valid User user, BindingResult bindingResult, Model model){

        if(bindingResult.hasErrors()){
            return "appUsers/newAppUser";
        } else if (!userService.findUserByUsername(user.getUserName()).isEmpty()) {
            model.addAttribute("error", "Korisničko ime već postoji!");
            return "appUsers/newAppUser";
        } else
            userService.save(user);
        return "redirect:/appUsers/all";
    }

    @GetMapping("delete/{id}")
    public String deleteById(@PathVariable(value = "id") Long id){
        userService.deleteById(id);
        return "redirect:/appUsers/all";
    }
}
