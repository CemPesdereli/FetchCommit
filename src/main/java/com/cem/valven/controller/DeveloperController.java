package com.cem.valven.controller;

import com.cem.valven.entity.Developer;
import com.cem.valven.service.DeveloperService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class DeveloperController {

    private final DeveloperService developerService;

    @GetMapping("/developers")
    public String getAllDevelopers(Model model) {
        model.addAttribute("developers", developerService.getAllDevelopers());
        return "developers";
    }

    @GetMapping("/developers/{username}")
    public String getDeveloperDetails(@PathVariable String username, Model model) {
        Developer developer = developerService.getDeveloperByUsername(username).orElseThrow();
        model.addAttribute("developer", developer);
        return "developer-details";
    }

}
