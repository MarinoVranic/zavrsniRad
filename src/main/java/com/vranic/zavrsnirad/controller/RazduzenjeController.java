package com.vranic.zavrsnirad.controller;

import com.vranic.zavrsnirad.service.RazduzenjeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;

@Controller
@RequestMapping("/razduzenje")
public class RazduzenjeController {

    @Autowired
    private RazduzenjeService razduzenjeService;

    public LocalDate today = LocalDate.now();

    @GetMapping("/all")
    public String getAllRazduzenje(Model model){
        model.addAttribute("svaRazduzenja", razduzenjeService.getAllRazduzenje());
        return "razduzenje/razduzenje";
    }
}
