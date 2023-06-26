import com.asteriosoft.Application;
import com.github.benmanes.caffeine.cache.stats.CacheStats;
import com.jayway.jsonpath.JsonPath;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CacheTest {

    private String authorizationHead;

    @Autowired
    CacheManager cacheManager;

    @Autowired
    private MockMvc mockMvc;

    private Long bannerForCacheId;

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

    @AfterAll
    void deleteBannerForCache() throws Exception {
        mockMvc.perform(
                        get("/banner/" + bannerForCacheId)
                                .header("Authorization", authorizationHead)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @Order(1)
    void testCache() throws Exception {

        StopWatch stopwatch = new StopWatch();
        stopwatch.start();
        multipleCalls(2L);
        String json = """
                {
                  "name": "new banner for cache",
                  "price": 100500,
                  "categories": ["category1", "category2"]
                }
                    """;
        ResultActions result = mockMvc.perform(
                        post("/banner")
                                .content(json)
                                .header("Authorization", authorizationHead)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        bannerForCacheId = Long.valueOf(JsonPath.read(result.andReturn().getResponse().getContentAsString(), "$.id").toString());
        multipleCalls(bannerForCacheId);
        json = """
                {
                  "name": "banner for cache",
                  "price": 100501
                }
                    """;
        mockMvc.perform(
                        post("/banner/" + bannerForCacheId)
                                .content(json)
                                .header("Authorization", authorizationHead)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        multipleCalls(bannerForCacheId);
        stopwatch.stop();
        CacheStats stats = ((CaffeineCache) cacheManager.getCache("banners")).getNativeCache().stats();
        log.info("Cache stats: {}", stats);
        log.info("Execution time testBid in milliseconds: {}", stopwatch.getTime());
    }

    private void multipleCalls(Long resultId) throws Exception {
        List<String> paramCat = new ArrayList<>();
        paramCat.add("cat_request_id_1");
        paramCat.add("cat_request_id_2");
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.addAll("cat", paramCat);
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
                    .andExpect(jsonPath("$.id").value(resultId))
                    .andExpect(status().isOk());
            log.info("body: {}", result.andReturn().getResponse().getContentAsString());
        }
    }

}
