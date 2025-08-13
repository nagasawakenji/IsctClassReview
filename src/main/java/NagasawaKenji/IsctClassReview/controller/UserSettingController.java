package NagasawaKenji.IsctClassReview.controller;

import NagasawaKenji.IsctClassReview.entity.Attachment;
import NagasawaKenji.IsctClassReview.entity.Review;
import NagasawaKenji.IsctClassReview.entity.User;
import NagasawaKenji.IsctClassReview.exception.NotFoundException;
import NagasawaKenji.IsctClassReview.repository.AttachmentRepository;
import NagasawaKenji.IsctClassReview.repository.ReviewRepository;
import NagasawaKenji.IsctClassReview.repository.UserRepository;
import NagasawaKenji.IsctClassReview.security.CustomUserDetails;
import NagasawaKenji.IsctClassReview.service.LectureInteractionService;
import NagasawaKenji.IsctClassReview.service.UserSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;


import java.util.List;

@Controller
public class UserSettingController {

    private final UserRepository userRepo;
    private final AttachmentRepository attachmentRepo;
    private final ReviewRepository reviewRepo;
    private final LectureInteractionService lectureInteractionService;
    private final UserSettingService userSettingService;

    @Autowired
    public UserSettingController(UserRepository userRepo,
                                 AttachmentRepository attachmentRepo,
                                 ReviewRepository reviewRepo,
                                 LectureInteractionService lectureInteractionService,
                                 UserSettingService userSettingService) {
        this.userRepo = userRepo;
        this.attachmentRepo = attachmentRepo;
        this.reviewRepo = reviewRepo;
        this.lectureInteractionService = lectureInteractionService;
        this.userSettingService = userSettingService;
    }

    @GetMapping("/setting")
    public String showSetting() {
        return "setting/set-index";
    }

    @GetMapping("/setting/review")
    public String showSettingReviewForm(Model model,
                                        @AuthenticationPrincipal CustomUserDetails principal) {
        User user = userRepo.findByUserName(principal.getUsername())
                .orElseThrow(() -> new NotFoundException("User not found"));

        List<Review> reviews = reviewRepo.findByUser(user);

        model.addAttribute("reviews", reviews);

        return "setting/review";
    }

    @PostMapping("/setting/review/{reviewId}")
    public String discardReview(@AuthenticationPrincipal CustomUserDetails principal,
                               @PathVariable(name = "reviewId") Long reviewId) {

        lectureInteractionService.deleteReview(principal, reviewId);

        return "redirect:/setting/review";
    }

    @GetMapping("/setting/attachment")
    public String showSettingAttachmentForm(Model model,
                                            @AuthenticationPrincipal CustomUserDetails principal) {
        User user = userRepo.findByUserName(principal.getUsername())
                .orElseThrow(() -> new NotFoundException("user not found"));

        List<Attachment> attachments = attachmentRepo.findByUser(user);

        model.addAttribute("attachments", attachments);

        return "setting/attachment";
    }

    @PostMapping("/setting/attachment/{attachmentId}")
    public String discardAttachment(@AuthenticationPrincipal CustomUserDetails principal,
                                    @PathVariable(name = "attachmentId") Long attachmentId) {
        lectureInteractionService.deleteAttachment(principal, attachmentId);

        return "redirect:/setting/attachment";
    }

    @GetMapping("/setting/user")
    public String showSettingUserForm(@AuthenticationPrincipal CustomUserDetails principal,
                                      Model model) {
        User user = userRepo.findByUserName(principal.getUsername())
                .orElseThrow(() -> new NotFoundException("user not found"));

        model.addAttribute("displayName", user.getDisplayName());
        model.addAttribute("email", user.getEmail());

        return "setting/user";
    }

    @PostMapping("/setting/user/rename")
    public String changeDisplayName(@RequestParam("name") String name,
                                    @AuthenticationPrincipal CustomUserDetails principal) {

        User user = userRepo.findByUserName(principal.getUsername())
                .orElseThrow(() -> new NotFoundException("user not found"));

        userSettingService.changeDisplayName(name, principal);

        return "redirect:/setting/user";
    }

}
