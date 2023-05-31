import com.asteriosoft.Application;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CategoryBannerTest {
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
    void testCreateCategory() throws Exception {
        String json = """
                {
                  "name": "category3",
                  "requestId": "cat_request_id_3"
                }
                    """;
        mockMvc.perform(
                        post("/category")
                                .content(json)
                                .header("Authorization", authorizationHead)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.requestId").value("cat_request_id_3"))
                .andExpect(status().isOk());
    }

    @Test
    @Order(2)
    void testCreateBanner() throws Exception {
        String json = """
                {
                  "name": "new banner",
                  "price": 333,
                  "categories": ["category3"]
                }
                    """;
        mockMvc.perform(
                        post("/banner")
                                .content(json)
                                .header("Authorization", authorizationHead)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.price").value(333))
                .andExpect(status().isOk());
    }

    @Test
    @Order(3)
    void testNegativeDeleteCategory() throws Exception {
        ResultActions result = mockMvc.perform(
                        get("/category/3")
                                .header("Authorization", authorizationHead)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
        Assertions.assertTrue(result.andReturn().getResponse().getContentAsString().contains("IMPOSSIBLE"));
    }

    @Test
    @Order(4)
    void testDeleteBanner() throws Exception {
        mockMvc.perform(
                        get("/banner/3")
                                .header("Authorization", authorizationHead)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @Order(5)
    void testPositiveDeleteCategory() throws Exception {
        ResultActions result = mockMvc.perform(
                        get("/category/3")
                                .header("Authorization", authorizationHead)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

}
