package com.vranic.zavrsnirad.controller;

import com.vranic.zavrsnirad.model.Korisnik;
import com.vranic.zavrsnirad.model.User;
import com.vranic.zavrsnirad.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/appUsers")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String getViewBasedOnRole(Authentication auth) {
        if (auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            // User has the admin role
            return "redirect:/index";
        } else if (auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_SUPER_ADMIN"))) {
            // User has super admin role
            return "redirect:/appUsers/all";
        } else {
            // User has user role
            return "redirect:/index";
        }
    }

    @GetMapping("/all")
    public String getAllUsers(Model model) throws Exception {
        model.addAttribute("allUsers", userService.getAllUsers());
        return "appUsers/appUsers";
    }

    @GetMapping("/active")
    public String getAllActiveUsers(Model model) throws Exception {
        model.addAttribute("allUsers", userService.getAllActiveUsers());
        return "appUsers/appUsers";
    }

    @GetMapping("/inactive")
    public String getAllInactiveUsers(Model model) throws Exception {
        model.addAttribute("allUsers", userService.getAllInactiveUsers());
        return "appUsers/appUsers";
    }

    @GetMapping("/addNew")
    public String addNewUser(Model model) throws Exception {
        User user = new User();
        model.addAttribute("user", user);
        return "appUsers/newAppUser";
    }


    @PostMapping("/addNew")
    public String addUser(@ModelAttribute("user") @Valid User user, BindingResult bindingResult, Model model) throws Exception {

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
    public String updateUser(@PathVariable(value = "id") Long id, Model model) throws Exception {
        User user = userService.getUserById(id);
        model.addAttribute("user", user);
        return "appUsers/updateAppUser";
    }

    @PostMapping("/save")
    @Transactional
    public String saveUser(@ModelAttribute("user") @Valid User user, BindingResult bindingResult) throws Exception {
        if(bindingResult.hasErrors()){
            return "appUsers/updateAppUser";
        }
        userService.updateUser(user.getId(), user.getUserName(), user.isActive(), user.getRoles(), user.getFirstName(), user.getLastName());
        return "redirect:/appUsers/all";
    }

    @GetMapping("/resetPassword/{id}")
    public String resetUserPassword(@PathVariable(value = "id") Long id, Model model) throws Exception {
        User user = userService.getUserById(id);
        model.addAttribute("user", user);
        return "appUsers/resetPasswordAppUser";
    }

    @GetMapping("/userResetPassword/")
    public String resetOwnUserPassword(Model model) throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userService.getUserByUsername(username);
        model.addAttribute("user", user);
        return "appUsers/resetPasswordAppUser";
    }

    @GetMapping("/resetPasswordForm")
    public String showResetPasswordForm(){
        return "resetPassword";
    }

    @PostMapping("/resetPassword")
    @Transactional
    public String updatePassword(@RequestParam String username, @RequestParam String password, RedirectAttributes redirectAttributes) throws Exception {
        try {
            User user = userService.getUserByUsername(username);
            if (user == null) {
                throw new Exception("Korisničko ime nije pronađeno!!!");
            }
            userService.resetUserPassword(user.getId(), password);
            redirectAttributes.addFlashAttribute("message", "Lozinka uspješno resetirana!");
            return "redirect:/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/appUsers/resetPasswordForm";
        }
    }

    @PostMapping("/updatePassword")
    @Transactional
    public String saveNewUserPassword(@ModelAttribute("user") @Valid User user, BindingResult bindingResult) throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(bindingResult.hasErrors()){
            return "appUsers/resetPasswordAppUser";
        }
        userService.resetUserPassword(user.getId(), user.getPassword());
        return getViewBasedOnRole(auth);
    }

    @GetMapping("/cancel")
    public String cancel() throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return getViewBasedOnRole(auth);
    }

    @GetMapping("/find")
    public String findUsersByUsername(@RequestParam("username") String username, Model model) throws Exception {
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
