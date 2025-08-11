package NagasawaKenji.IsctClassReview.controller;

import NagasawaKenji.IsctClassReview.dto.AttachmentForm;
import NagasawaKenji.IsctClassReview.dto.ReviewForm;
import NagasawaKenji.IsctClassReview.security.CustomUserDetails;
import NagasawaKenji.IsctClassReview.service.LectureInteractionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/lectures/{lectureId}")
public class LectureInteractionController {

    private final LectureInteractionService lectureInteractionService;


    @Autowired
    public LectureInteractionController(LectureInteractionService lectureInteractionService) {
        this.lectureInteractionService = lectureInteractionService;
    }

    @PostMapping("/reviews")
    public ResponseEntity<?> postReview(
            @PathVariable Short lectureId,
            @RequestBody @Valid ReviewForm reviewForm,
            @AuthenticationPrincipal CustomUserDetails principal
    ) {

        lectureInteractionService.saveReview(lectureId, reviewForm, principal);

        return ResponseEntity.status(HttpStatus.CREATED).build();

    }

    @PostMapping(value = "/attachments", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> postAttachment(
            @PathVariable Short lectureId,
            @RequestBody @Valid AttachmentForm attachmentForm,
            @AuthenticationPrincipal CustomUserDetails principal
            ) {

       lectureInteractionService.saveAttachment(lectureId, attachmentForm, principal);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
