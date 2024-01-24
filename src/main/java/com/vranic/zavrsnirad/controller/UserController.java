package com.vranic.zavrsnirad.controller;

import com.vranic.zavrsnirad.model.Korisnik;
import com.vranic.zavrsnirad.model.User;
import com.vranic.zavrsnirad.service.UserService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/active")
    public String getAllActiveUsers(Model model){
        model.addAttribute("allUsers", userService.getAllActiveUsers());
        return "appUsers/appUsers";
    }

    @GetMapping("/inactive")
    public String getAllInactiveUsers(Model model){
        model.addAttribute("allUsers", userService.getAllInactiveUsers());
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

    @GetMapping("/update/{id}")
    public String updateUser(@PathVariable(value = "id") Long id, Model model){
        User user = userService.getUserById(id);
        model.addAttribute("user", user);
        return "appUsers/updateAppUser";
    }

    @PostMapping("/save")
    @Transactional
    public String saveUser(@ModelAttribute("user") @Valid User user, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return "appUsers/updateAppUser";
        }
        userService.updateUser(user.getId(), user.getUserName(), user.isActive(), user.getRoles(), user.getFirstName(), user.getLastName());
        return "redirect:/appUsers/all";
    }

    @GetMapping("/resetPassword/{id}")
    public String resetUserPassword(@PathVariable(value = "id") Long id, Model model){
        User user = userService.getUserById(id);
        model.addAttribute("user", user);
        return "appUsers/resetPasswordAppUser";
    }

    @PostMapping("/updatePassword")
    @Transactional
    public String saveNewUserPassword(@ModelAttribute("user") @Valid User user, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return "appUsers/resetPasswordAppUser";
        }
        System.out.println(user.getId());
        System.out.println(user.getPassword());
        userService.resetUserPassword(user.getId(), user.getPassword());
        return "redirect:/appUsers/all";
    }

    @GetMapping("/find")
    public String findUsersByUsername(@RequestParam("username") String username, Model model) {
        List<User> userList = userService.findUserByUsername(username);
        if (userList.isEmpty()) {
            model.addAttribute("error", "Username nije pronađen!");
            model.addAttribute("allUsers", userService.getAllUsers());
        } else {
            model.addAttribute("allUsers", userList);
        }
        return "appUsers/appUsers";
    }
}
