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
import reactor.core.publisher.Flux;

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
        Flux<RepositoryDto> repositoriesFlux = Flux.just(repo);

        when(githubService.getUserNonForkRepositories(username)).thenReturn(repositoriesFlux);

        //when
        ResponseEntity<Flux<RepositoryDto>> response = githubController.getUserNonForkRepositories(username);

        //then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        List<RepositoryDto> repositories = response.getBody().collectList().block();
        assertNotNull(repositories);
        assertEquals(1, repositories.size());
        assertEquals(repo.name(), repositories.get(0).name());
    }

    @Test
    public void shouldReturnEmptyListWhenServiceReturnsEmptyList() {
        //given
        String username = "test-user";
        Flux<RepositoryDto> emptyFlux = Flux.empty();

        when(githubService.getUserNonForkRepositories(username)).thenReturn(emptyFlux);

        //when
        ResponseEntity<Flux<RepositoryDto>> response = githubController.getUserNonForkRepositories(username);

        //then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        List<RepositoryDto> repositories = response.getBody().collectList().block();
        assertNotNull(repositories);
        assertTrue(repositories.isEmpty());
    }

    @Test
    public void shouldHandleUserNotFoundException() {
        //given
        String username = "nonexistent-user";

        when(githubService.getUserNonForkRepositories(username))
                .thenThrow(new UserNotFoundException("User 'nonexistent-user' not found"));

        //when
        UserNotFoundException thrown = assertThrows(UserNotFoundException.class, () ->
                githubController.getUserNonForkRepositories(username).getBody().collectList().block()
        );

        //then
        assertEquals("User 'nonexistent-user' not found", thrown.getMessage());
    }

}