package com.virtualwallet.controllers.mvc;

import com.virtualwallet.services.contracts.UserService;
import com.virtualwallet.services.contracts.WalletService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/home")
public class HomeMvcController {
//
//    public final UserService userService;
//    public final WalletService walletService;
//
//    @Autowired
//    public HomeMvcController(UserService userService, WalletService walletService) {
//        this.userService = userService;
//        this.walletService = walletService;
//    }

    @ModelAttribute("isAuthenticated")
    public boolean populateIsAuthenticated(HttpSession session) {
        return session.getAttribute("currentUser") != null;
    }

    @GetMapping("/home")
    public String showHomePage(Model model) {
        return "HomePageView";
    }
}
