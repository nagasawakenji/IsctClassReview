package NagasawaKenji.IsctClassReview.controller;

import NagasawaKenji.IsctClassReview.config.SecurityConfig;
import NagasawaKenji.IsctClassReview.repository.MajorRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest(HomeController.class)
@Import(SecurityConfig.class)
public class HomeControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MajorRepository majorRepo;

    @Test
    @WithMockUser
    @DisplayName("indexを正しく返し、majorsがmodelに付与されている")
    void showHome_success() throws Exception {

        when(majorRepo.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("majors"))
                .andExpect(model().attribute("majors", List.of()));

    }
}
