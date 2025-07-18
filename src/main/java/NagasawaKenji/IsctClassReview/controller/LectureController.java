package NagasawaKenji.IsctClassReview.controller;

import NagasawaKenji.IsctClassReview.dto.AttachmentForm;
import NagasawaKenji.IsctClassReview.dto.ReviewForm;
import NagasawaKenji.IsctClassReview.entity.Attachment;
import NagasawaKenji.IsctClassReview.entity.Lecture;
import NagasawaKenji.IsctClassReview.entity.Review;
import NagasawaKenji.IsctClassReview.repository.AttachmentRepository;
import NagasawaKenji.IsctClassReview.repository.LectureRepository;
import NagasawaKenji.IsctClassReview.repository.ReviewRepository;
import NagasawaKenji.IsctClassReview.service.LectureInteractionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@Controller
@RequestMapping("/lectures")
public class LectureController {

    private final LectureRepository lectureRepo;
    private final ReviewRepository reviewRepo;
    private final AttachmentRepository attachmentRepo;
    private final LectureInteractionService lectureInteractionService;

    @Autowired
    public LectureController(LectureRepository lectureRepo,
                             ReviewRepository reviewRepo,
                             AttachmentRepository attachmentRepo,
                             LectureInteractionService lectureInteractionService) {
        this.lectureRepo = lectureRepo;
        this.reviewRepo = reviewRepo;
        this.attachmentRepo = attachmentRepo;
        this.lectureInteractionService = lectureInteractionService;
    }

    @GetMapping("/{lectureId}")
    public String showLecture(@PathVariable Short lectureId,
                              Model model) {

        Lecture lecture = lectureRepo.findById(lectureId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        List<Review> reviews = reviewRepo.findByLectureId(lectureId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        List<Attachment> attachments = attachmentRepo.findByLectureId(lectureId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        model.addAttribute("lecture", lecture);
        model.addAttribute("reviews", reviews);
        model.addAttribute("attachments", attachments);
        model.addAttribute("reviewForm", new ReviewForm());
        model.addAttribute("attachmentForm", new AttachmentForm());

        return "lecture-detail";
    }

    @PostMapping("/{lectureId}/review")
    public String postReview(@PathVariable Short lectureId,
                             @Valid @ModelAttribute(name = "reviewForm") ReviewForm reviewForm,
                             BindingResult bindingResult,
                             @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal) {
        if (bindingResult.hasErrors()) {
            return "lecture-detail";
        }

        lectureInteractionService.saveReview(lectureId, reviewForm, principal);

        return "redirect:/lectures/" + lectureId;
    }

    @PostMapping(
            value = "/{lectureId}/attachments",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public String uploadAttachment(@PathVariable Short lectureId,
                                   @Valid @ModelAttribute(name = "attachmentForm") AttachmentForm attachmentForm,
                                   BindingResult bindingResult,
                                   @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal
    ) {

        if (bindingResult.hasErrors()) {
            return "lecture-detail";
        }

       lectureInteractionService.saveAttachment(lectureId, attachmentForm, principal);

        return "redirect:/lectures/" + lectureId;


    }





}
