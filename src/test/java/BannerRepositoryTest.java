import com.asteriosoft.Application;
import com.google.common.base.Stopwatch;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
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
import java.util.concurrent.TimeUnit;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@Slf4j
@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BannerRepositoryTest {

    private String authorizationHead;

    @Autowired
    private MockMvc mockMvc;

    @BeforeAll
    void setAuthorization() throws Exception {
        authorizationHead = getAuthorizationHead();
    }

    private String getAuthorizationHead() throws Exception {
        final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("username", "user");
        params.add("password", "pass");

        ResultActions result = mockMvc.perform(
                        post("/login")
                                .params(params)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        return result.andReturn().getResponse().getHeader("Authorization");
    }

    @Test
    void testAllBanners() throws Exception {
        Stopwatch stopwatch = Stopwatch.createStarted();
        mockMvc.perform(
                        get("/banners")
                                .header("Authorization", authorizationHead)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].price").value(10.00))
                .andExpect(jsonPath("$[1].price").value(100.50))
                .andExpect(status().isOk());
        stopwatch.stop();
        long timeElapsed = stopwatch.elapsed(TimeUnit.MILLISECONDS);
        log.info("Execution time testAllBanners in milliseconds: {}", timeElapsed);
    }

    @Test
    void testBidEndpoint() throws Exception {
        List<String> paramCat = new ArrayList<>();
        paramCat.add("cat_request_id_1");
        paramCat.add("cat_request_id_2");
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.addAll("cat", paramCat);
        Stopwatch stopwatch = Stopwatch.createStarted();
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
                    .andExpect(status().isOk());
            log.info("body: {}", result.andReturn().getResponse().getContentAsString());
        }
        stopwatch.stop();
        long timeElapsed = stopwatch.elapsed(TimeUnit.MILLISECONDS);
        log.info("Execution time testBidEndpoint in milliseconds: {}", timeElapsed);
    }

}
