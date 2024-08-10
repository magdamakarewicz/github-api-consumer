package com.enjoythecode.githubapiconsumer.controller;

import com.enjoythecode.githubapiconsumer.dto.BranchDto;
import com.enjoythecode.githubapiconsumer.dto.CommitDto;
import com.enjoythecode.githubapiconsumer.dto.OwnerDto;
import com.enjoythecode.githubapiconsumer.dto.RepositoryDto;
import com.enjoythecode.githubapiconsumer.exception.UserNotFoundException;
import com.enjoythecode.githubapiconsumer.service.GithubService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import reactor.core.publisher.Flux;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
public class GithubControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean
    private GithubService githubService;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Disabled("Temporarily ignoring test")
    @Test
    public void shouldReturnRepositoriesWhenServiceReturnsRepositories() throws Exception {
        //given
        String username = "test-user";
        List<RepositoryDto> mockRepositories = List.of(
                new RepositoryDto(new OwnerDto(username), "repo1", false, List.of(
                        new BranchDto("branch1", new CommitDto("test-sha-1")),
                        new BranchDto("branch2", new CommitDto("test-sha-2")),
                        new BranchDto("branch3", new CommitDto("test-sha-3"))
                )),
                new RepositoryDto(new OwnerDto(username), "repo2", false, List.of(
                        new BranchDto("branch1", new CommitDto("test-sha-4"))
                ))
        );

        when(githubService.getUserNonForkRepositories(username)).thenReturn(Flux.fromIterable(mockRepositories));

        //when/then
        MvcResult result = mockMvc.perform(get("/api/github/users/" + username + "/repos")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        String content = result.getResponse().getContentAsString();
        System.out.println("Response content: " + content); // Dodaj to, aby zobaczyć zawartość odpowiedzi

        mockMvc.perform(get("/api/github/users/" + username + "/repos")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name").value("repo1"))
                .andExpect(jsonPath("$[0].owner.login").value("test-user"))
                .andExpect(jsonPath("$[0].branches[0].name").value("branch1"))
                .andExpect(jsonPath("$[0].branches[0].commit.sha").value("test-sha-1"))
                .andExpect(jsonPath("$[0].branches[1].name").value("branch2"))
                .andExpect(jsonPath("$[0].branches[1].commit.sha").value("test-sha-2"))
                .andExpect(jsonPath("$[0].branches[2].name").value("branch3"))
                .andExpect(jsonPath("$[0].branches[2].commit.sha").value("test-sha-3"))
                .andExpect(jsonPath("$[1].name").value("repo2"))
                .andExpect(jsonPath("$[1].owner.login").value("test-user"))
                .andExpect(jsonPath("$[1].branches[0].name").value("branch1"))
                .andExpect(jsonPath("$[1].branches[0].commit.sha").value("test-sha-4"));
    }

    @Test
    public void shouldReturnNotFoundWhenUserDoesNotExist() throws Exception {
        //given
        String username = "nonexistent-user";
        when(githubService.getUserNonForkRepositories(username))
                .thenThrow(new UserNotFoundException("User 'nonexistent-user' not found"));

        //when/then
        mockMvc.perform(get("/api/github/users/" + username + "/repos")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("User 'nonexistent-user' not found"));
    }

}