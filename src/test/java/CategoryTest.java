import com.asteriosoft.Application;
import com.google.common.base.Stopwatch;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.concurrent.TimeUnit;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CategoryTest {
    private String authorizationHead;

    @Autowired
    private MockMvc mockMvc;

    @BeforeAll
    void testLogin() throws Exception {
        final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("username", "user");
        params.add("password", "pass");
        ResultActions result = mockMvc.perform(
                        post("/login")
                                .params(params)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        authorizationHead = result.andReturn().getResponse().getHeader("Authorization");
    }

    @Test
    @Order(1)
    void testCategories() throws Exception {
        Stopwatch stopwatch = Stopwatch.createStarted();
        mockMvc.perform(
                        get("/categories")
                                .header("Authorization", authorizationHead)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].requestId").value("cat_request_id_1"))
                .andExpect(jsonPath("$[1].requestId").value("cat_request_id_2"))
                .andExpect(status().isOk());
        stopwatch.stop();
        long timeElapsed = stopwatch.elapsed(TimeUnit.MILLISECONDS);
        log.info("Execution time testCategories in milliseconds: {}", timeElapsed);
    }

    @Test
    @Order(2)
    void testFilterCategory() throws Exception {
        String searchText = "y2";
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("searchText", searchText);
        Stopwatch stopwatch = Stopwatch.createStarted();
        ResultActions result = mockMvc.perform(
                        get("/categories/filter")
                                .params(params)
                                .header("Authorization", authorizationHead)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name").value("category2"))
                .andExpect(status().isOk());
        stopwatch.stop();
        log.info("body: {}", result.andReturn().getResponse().getContentAsString());
        long timeElapsed = stopwatch.elapsed(TimeUnit.MILLISECONDS);
        log.info("Execution time testFilterCategory in milliseconds: {}", timeElapsed);
    }

    @Test
    @Order(3)
    void testCreateCategory() throws Exception {
        String json = """
                {
                  "name": "new category",
                  "requestId": "cat_request_id_new_category"
                }
                    """;
        Stopwatch stopwatch = Stopwatch.createStarted();
        mockMvc.perform(
                        post("/category")
                                .content(json)
                                .header("Authorization", authorizationHead)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.requestId").value("cat_request_id_new_category"))
                .andExpect(status().isOk());
        long timeElapsed = stopwatch.elapsed(TimeUnit.MILLISECONDS);
        log.info("Execution time testCreateCategory in milliseconds: {}", timeElapsed);
    }

    @Test
    @Order(4)
    void testUpdateCategory() throws Exception {
        String json = """
                {
                  "name": "category2_new_name",
                  "requestId": "cat_new_request_id_2"
                }
                    """;
        Stopwatch stopwatch = Stopwatch.createStarted();
        mockMvc.perform(
                        post("/category/2")
                                .content(json)
                                .header("Authorization", authorizationHead)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        long timeElapsed = stopwatch.elapsed(TimeUnit.MILLISECONDS);
        log.info("Execution time testUpdateBanner in milliseconds: {}", timeElapsed);
    }

    @Test
    @Order(5)
    void testDeleteCategory() throws Exception {
        Stopwatch stopwatch = Stopwatch.createStarted();
        mockMvc.perform(
                        get("/category/3")
                                .header("Authorization", authorizationHead)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        stopwatch.stop();
        long timeElapsed = stopwatch.elapsed(TimeUnit.MILLISECONDS);
        log.info("Execution time testDeleteCategory in milliseconds: {}", timeElapsed);
    }

}
