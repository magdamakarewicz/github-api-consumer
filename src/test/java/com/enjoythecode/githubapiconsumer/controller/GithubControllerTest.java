package com.enjoythecode.githubapiconsumer.controller;

import com.enjoythecode.githubapiconsumer.dto.OwnerDto;
import com.enjoythecode.githubapiconsumer.dto.RepositoryDto;
import com.enjoythecode.githubapiconsumer.exception.UserNotFoundException;
import com.enjoythecode.githubapiconsumer.service.GithubService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
class GithubControllerTest {

    @Mock
    private GithubService githubService;

    @InjectMocks
    private GithubController githubController;

    @Test
    public void shouldReturnRepositoriesWhenServiceReturnsData() {
        //given
        String username = "test-user";
        RepositoryDto repo = new RepositoryDto(new OwnerDto(username), "repo", false, List.of());
        List<RepositoryDto> repositories = List.of(repo);

        when(githubService.getUserNonForkRepositories(username)).thenReturn(repositories);

        //when
        ResponseEntity<List<RepositoryDto>> response = githubController.getUserNonRepositories(username);

        //then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(repositories.size(), response.getBody().size());
        assertEquals(repo.name(), response.getBody().get(0).name());
    }

    @Test
    public void shouldReturnEmptyListWhenServiceReturnsEmptyList() {
        //given
        String username = "test-user";

        when(githubService.getUserNonForkRepositories(username)).thenReturn(Collections.emptyList());

        //when
        ResponseEntity<List<RepositoryDto>> response = githubController.getUserNonRepositories(username);

        //then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    public void shouldHandleUserNotFoundException() {
        //given
        String username = "nonexistent-user";
        when(githubService.getUserNonForkRepositories(username))
                .thenThrow(new UserNotFoundException("User 'nonexistent-user' not found"));

        //when
        UserNotFoundException thrown = assertThrows(UserNotFoundException.class, () ->
                githubController.getUserNonRepositories(username)
        );

        //then
        assertEquals("User 'nonexistent-user' not found", thrown.getMessage());
    }

}