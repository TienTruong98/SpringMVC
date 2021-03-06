package com.tientt.springmvc.controller;

import com.tientt.springmvc.dto.RegistrationDTO;
import com.tientt.springmvc.entity.Registration;
import com.tientt.springmvc.form.RegisterForm;
import com.tientt.springmvc.mapper.RegisterFormMapper;
import com.tientt.springmvc.service.RegistrationService;
import com.tientt.springmvc.validator.RegisterValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@Controller
public class MainController {
    public static String INVALID_PAGE = "invalid";
    public static String SEARCH_PAGE = "search";
    @Autowired
    RegistrationService registrationService;
    @Autowired
    RegisterFormMapper formMapper;
    @Autowired
    RegisterValidator validator;

    @RequestMapping(value = {"/", "/login"}, method = RequestMethod.GET)
    public ModelAndView getLoginPage() {
        return new ModelAndView("login");
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public RedirectView login(HttpServletRequest request, HttpServletResponse response) {
        String username = request.getParameter("txtUsername");
        String password = request.getParameter("txtPassword");
        Registration user = registrationService.checkLogin(username, password);
        String url = "invalid";
        if (user != null) {
            HttpSession session = request.getSession();
            response.setHeader("Cache-Control", "no-cache, no-store");
            session.setAttribute("USER", user);
            url = "search";
        }
        System.out.println(url);
        return new RedirectView(url);
    }

    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public ModelAndView renderSearchPage() {
        return new ModelAndView(SEARCH_PAGE);
    }

    @RequestMapping(value = "/invalid", method = RequestMethod.GET)
    public ModelAndView renderInvalidPage() {
        return new ModelAndView(INVALID_PAGE);
    }


    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public RedirectView logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return new RedirectView("login");
    }

    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public ModelAndView getRegisterPage() {
        return new ModelAndView("register");
    }


    @RequestMapping(value = "/register_taglib", method = RequestMethod.GET)
    public ModelAndView getRegisterTaglibPage(){
        RegisterForm registerForm = new RegisterForm();
        Map<String, Object> map = new HashMap<>();
        map.put("user", registerForm);
        return  new ModelAndView("register_taglib", map);
    }

    @RequestMapping(value = "/registerTaglib", method = RequestMethod.POST)
    public ModelAndView registerProcess(
            @ModelAttribute("user") @Valid RegisterForm registerForm,
            BindingResult bindingResult
            
    ){
        if (bindingResult.hasErrors()){
            return new ModelAndView("register_taglib");
        }
        RegistrationDTO dto = formMapper.toDTO(registerForm);
        registrationService.createUser(dto);
        return new ModelAndView("login");
    }

    @InitBinder("user")
    public void initBider(WebDataBinder binder){
        binder.addValidators(validator);
    }

    @GetMapping("shop")
    public ModelAndView getShopPage(){
        return new ModelAndView("shop");
    }

    @GetMapping("showCart")
    public ModelAndView getCartPage(){
        return new ModelAndView("cart");
    }
}
