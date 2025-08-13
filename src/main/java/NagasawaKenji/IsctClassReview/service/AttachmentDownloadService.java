package NagasawaKenji.IsctClassReview.service;

import NagasawaKenji.IsctClassReview.entity.Attachment;
import NagasawaKenji.IsctClassReview.entity.User;
import NagasawaKenji.IsctClassReview.exception.NotFoundException;
import NagasawaKenji.IsctClassReview.repository.AttachmentRepository;
import NagasawaKenji.IsctClassReview.repository.UserRepository;
import NagasawaKenji.IsctClassReview.security.CustomUserDetails;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Supplier;


@Service
public class AttachmentDownloadService {

    private final AttachmentRepository attachmentRepo;
    private final UserRepository userRepo;

    @Autowired
    public AttachmentDownloadService(AttachmentRepository attachmentRepo,
                                     UserRepository userRepo) {
        this.attachmentRepo = attachmentRepo;
        this.userRepo = userRepo;
    }

    public AttachmentRecord DownloadAttachment(CustomUserDetails principal,
                                               Long attachmentId) {

        Attachment attachment = attachmentRepo.findById(attachmentId)
                .orElseThrow(() -> new NotFoundException("attachment not found"));

        User user = userRepo.findByUserName(principal.getUsername())
                .orElseThrow(() -> new NotFoundException("user not found"));

        if (!user.getId().equals(principal.getUser().getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "権限がありません");
        }

        Path path = Paths.get(attachment.getFilePath());

        if (!Files.exists(path) || !Files.isReadable(path)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "file not find");
        }

        UrlResource urlResource;
        try {
            urlResource = new UrlResource(path.toUri());
        } catch (MalformedURLException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "bad file uri");
        }

        String fileName = path.getFileName().toString();

        MediaType mediaType = Optional.ofNullable(attachment.getFileType())
                .map(MediaType::parseMediaType)
                .orElseGet(() -> MediaTypeFactory.getMediaType(fileName)
                        .orElse(MediaType.APPLICATION_OCTET_STREAM));

        long size = safeLong(() -> Files.size(path), -1L);

        return new AttachmentRecord(fileName, urlResource, mediaType, size);


    }


    public record AttachmentRecord(
            String fileName,
            org.springframework.core.io.Resource resource,
            MediaType mediaType,
            long contentLength
    ) {}

    public long safeLong(ThrowingSupplier<Long> supplier, long alternative) {
        try {
            return supplier.get();
        } catch (Exception e) {
            return alternative;
        }
    }

    // getまたはエラーを返す操作自体をオブジェクト化している
    @FunctionalInterface
    public interface ThrowingSupplier<T> {
        T get() throws Exception;
    }
}
