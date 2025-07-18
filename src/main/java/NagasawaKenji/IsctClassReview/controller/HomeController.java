package NagasawaKenji.IsctClassReview.controller;

import NagasawaKenji.IsctClassReview.entity.Major;
import NagasawaKenji.IsctClassReview.repository.MajorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    private final MajorRepository majorRepo;

    @Autowired
    public HomeController(MajorRepository majorRepo) {
        this.majorRepo = majorRepo;
    }

    @GetMapping("/")
    public String showHome(Model model) {
        List<Major> majors = majorRepo.findAll();

        model.addAttribute("majors", majors);

        return "index";
    }
}
