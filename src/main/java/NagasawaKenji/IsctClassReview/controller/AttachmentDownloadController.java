package NagasawaKenji.IsctClassReview.controller;

import NagasawaKenji.IsctClassReview.security.CustomUserDetails;
import NagasawaKenji.IsctClassReview.service.AttachmentDownloadService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/attachments")
public class AttachmentDownloadController {

    private final AttachmentDownloadService attachmentDownloadService;


    @Autowired
    public AttachmentDownloadController(AttachmentDownloadService attachmentDownloadService) {
        this.attachmentDownloadService = attachmentDownloadService;
    }

    @GetMapping("/{attachmentId}")
    public ResponseEntity<Resource> downloadAttachment(@PathVariable(name = "attachmentId") Long attachmentId,
                                                       @AuthenticationPrincipal CustomUserDetails principal) {

        AttachmentDownloadService.AttachmentRecord attachmentRecord = attachmentDownloadService.DownloadAttachment(principal, attachmentId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(attachmentRecord.mediaType());
        headers.setContentDisposition(ContentDisposition.inline()
                .filename(attachmentRecord.fileName(), StandardCharsets.UTF_8)
                .build());
        if (attachmentRecord.contentLength() >= 0) headers.setContentLength(attachmentRecord.contentLength());

        return ResponseEntity
                .status(HttpStatus.OK)
                .headers(headers)
                .body(attachmentRecord.resource());



    }
}
