package com.kontur.shortener.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
@Sql(value ="/create-link-before.sql",executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD )
@Sql(value = "/link-delete.sql",executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD )
public class ShortenerConrtollerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    @Sql(value = "/link-delete.sql",executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void generate() throws Exception {
        this.mockMvc.perform(post("/generate")
                .param("original","http://some-server.com/some/url/Generate"))
                .andDo(print())
                .andExpect(status().isOk());
        this.mockMvc.perform(post("/generate")
                .param("original","/some/url/Generate"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void linkShortUrl() throws Exception {
        this.mockMvc.perform(get("/l/1"))
                .andDo(print())
                .andExpect(redirectedUrl("https://el.nsu.ru"));
    }

    @Test
    public void statsShortUrl() throws Exception {
        this.mockMvc.perform(get("/stats/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("{"
                        + "\"link\": \"/l/1\","
                        + "\"original\": \"https://el.nsu.ru\","
                        + "\"rank\": 1,"
                        + "\"count\": 23"
                        + "}"));
    }

    @Test
    public void statsPage() throws Exception {
        this.mockMvc.perform(get("/stats?page=1&count=2"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("[{"
                        + "\"link\": \"/l/1\","
                        + "\"original\": \"https://el.nsu.ru\","
                        + "\"rank\": 1,"
                        + "\"count\": 23"
                        + "}," +
                        "{\"link\": \"/l/2\","
                        +"\"original\": \"http://some-server.com/some/url\","
                        +"\"rank\": 2,"
                        +"\"count\": 22"
                        +"}]"));
    }
    @Test
    public void linkShortUrlTestRankChanges() throws Exception {
        for(int i=0;i<3;i++)
            this.mockMvc.perform(get("/l/2"));

        this.mockMvc.perform(get("/stats/2"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("{"
                        + "\"link\": \"/l/2\","
                        + "\"original\": \"http://some-server.com/some/url\","
                        + "\"rank\": 1,"
                        + "\"count\": 25"
                        + "}"));
    }
}