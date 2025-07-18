package NagasawaKenji.IsctClassReview.controller;


import NagasawaKenji.IsctClassReview.dto.RegistrationForm;
import NagasawaKenji.IsctClassReview.service.RegistrationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@SessionAttributes("registrationForm")
@Controller
@RequestMapping("/register")
public class RegisterController {

    private final RegistrationService registrationService;

    @Autowired
    public RegisterController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @GetMapping("")
    public String showRegisterForm(Model model) {

        model.addAttribute("registrationForm", new RegistrationForm());

        return "register";
    }

    @PostMapping("")
    public String processRegistration(@Valid @ModelAttribute(name = "registrationForm") RegistrationForm registrationForm,
                                      BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "register";
        }

        registrationService.sendVerificationCode(registrationForm.getEmail());

        return "redirect:/register/verify";
    }

    @GetMapping("/verify")
    public String showVerifyForm(Model model,
                                 @ModelAttribute(name = "registrationForm") RegistrationForm registrationForm) {

        model.addAttribute("registrationForm", registrationForm);

        return "verify";

    }

    @PostMapping("/verify")
    public String checkVerify(
            @Valid @ModelAttribute("registrationForm") RegistrationForm registrationForm,
            BindingResult bindingResult,
            @RequestParam("token") String token,
            SessionStatus sessionStatus
    ) {
        if (bindingResult.hasErrors()) {
            return "verify";
        }
        try {
            registrationService.registerUser(
                    registrationForm.getEmail(),
                    registrationForm.getPassword(),
                    token
            );
        } catch (IllegalArgumentException e) {
            bindingResult.reject("token", e.getMessage());
            return "verify";
        }
        sessionStatus.setComplete();  // セッション属性をクリア
        return "verify-result";
    }
}
