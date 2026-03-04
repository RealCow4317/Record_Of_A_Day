package com.example.blog.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

    @Value("${site.name}")
    private String siteName;

    @ModelAttribute
    public void addAttributes(Model model) {
        model.addAttribute("siteName", siteName);
    }
}
