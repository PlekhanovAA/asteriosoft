import com.asteriosoft.Application;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@Slf4j
@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BannerTest {

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
    void testBanners() throws Exception {
        StopWatch stopwatch = new StopWatch();
        stopwatch.start();
        mockMvc.perform(
                        get("/banners")
                                .header("Authorization", authorizationHead)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].price").value(10.00))
                .andExpect(jsonPath("$[1].price").value(100.50))
                .andExpect(status().isOk());
        stopwatch.stop();
        log.info("Execution time testBanners in milliseconds: {}", stopwatch.getTime());
    }

    @Test
    @Order(2)
    void testBid() throws Exception {
        List<String> paramCat = new ArrayList<>();
        paramCat.add("cat_request_id_1");
        paramCat.add("cat_request_id_2");
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.addAll("cat", paramCat);
        StopWatch stopwatch = new StopWatch();
        stopwatch.start();
        int numberOfRepetitions = 10;
        for (int i = 0; i <= numberOfRepetitions; i++) {
            String userAgent = RandomStringUtils.randomAlphabetic(8);
            ResultActions result = mockMvc.perform(
                            get("/bid")
                                    .params(params)
                                    .header("user-agent", userAgent)
                                    .header("Host", "localhost")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.price").value(100.50))
                    .andExpect(status().isOk());
            log.info("body: {}", result.andReturn().getResponse().getContentAsString());
        }
        stopwatch.stop();
        log.info("Execution time testBid in milliseconds: {}", stopwatch.getTime());
    }

    @Test
    @Order(3)
    void testFilterBanner() throws Exception {
        String searchText = "r2";
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("searchText", searchText);
        StopWatch stopwatch = new StopWatch();
        stopwatch.start();
        ResultActions result = mockMvc.perform(
                        get("/banners/filter")
                                .params(params)
                                .header("Authorization", authorizationHead)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name").value("banner2"))
                .andExpect(status().isOk());
        stopwatch.stop();
        log.info("body: {}", result.andReturn().getResponse().getContentAsString());
        log.info("Execution time testFilterBanner in milliseconds: {}", stopwatch.getTime());
    }

    @Test
    @Order(4)
    void testCreateBanner() throws Exception {
        String json = """
                {
                  "name": "new banner2",
                  "price": 100500,
                  "categories": ["category1", "category2"]
                }
                    """;
        StopWatch stopwatch = new StopWatch();
        stopwatch.start();
        mockMvc.perform(
                        post("/banner")
                                .content(json)
                                .header("Authorization", authorizationHead)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.price").value(100500))
                .andExpect(status().isOk());
        stopwatch.stop();
        log.info("Execution time testCreateBanner in milliseconds: {}", stopwatch.getTime());
    }

    @Test
    @Order(5)
    void testUpdateBanner() throws Exception {
        String json = """
                {
                  "name": "new name for banner2",
                  "price": 10
                }
                    """;
        StopWatch stopwatch = new StopWatch();
        stopwatch.start();
        mockMvc.perform(
                        post("/banner/2")
                                .content(json)
                                .header("Authorization", authorizationHead)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        stopwatch.stop();
        log.info("Execution time testUpdateBanner in milliseconds: {}", stopwatch.getTime());
    }

    @Test
    @Order(6)
    void testDeleteBanner() throws Exception {
        StopWatch stopwatch = new StopWatch();
        stopwatch.start();
        mockMvc.perform(
                        get("/banner/1")
                                .header("Authorization", authorizationHead)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        stopwatch.stop();
        log.info("Execution time testDeleteBanner in milliseconds: {}", stopwatch.getTime());
    }

}
