package com.enjoythecode.githubapiconsumer.repository;

import com.enjoythecode.githubapiconsumer.dto.BranchDto;
import com.enjoythecode.githubapiconsumer.dto.CommitDto;
import com.enjoythecode.githubapiconsumer.dto.OwnerDto;
import com.enjoythecode.githubapiconsumer.dto.RepositoryDto;
import com.enjoythecode.githubapiconsumer.exception.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@SpringBootTest
class GithubRepositoryTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private GithubRepository githubRepository;

    private HttpHeaders headers;

    private HttpEntity<Void> entity;

    @BeforeEach
    public void setup() {
        headers = new HttpHeaders();
        headers.set("Accept", "application/json");
        entity = new HttpEntity<>(headers);
    }

    @Test
    public void shouldReturnRepositoriesWhenApiReturnsRepositories() {
        //given
        String username = "test-user";
        String url = "https://api.github.com/users/" + username + "/repos";
        List<RepositoryDto> mockRepositories = List.of(
                new RepositoryDto(new OwnerDto(username), "test", false, List.of())
        );
        ResponseEntity<List<RepositoryDto>> responseEntity = ResponseEntity.ok(mockRepositories);

        when(restTemplate.exchange(eq(url), eq(HttpMethod.GET), eq(entity), any(ParameterizedTypeReference.class)))
                .thenReturn(responseEntity);

        //when
        List<RepositoryDto> repositories = githubRepository.getUserRepositoriesByUsername(username);

        //then
        assertNotNull(repositories);
        assertFalse(repositories.isEmpty());
        assertEquals(mockRepositories.size(), repositories.size());
    }

    @Test
    public void shouldReturnEmptyListWhenApiReturnsEmptyResponseForRepositories() {
        //given
        String username = "test-user";
        String url = "https://api.github.com/users/" + username + "/repos";
        ResponseEntity<List<RepositoryDto>> responseEntity = ResponseEntity.ok(Collections.emptyList());

        when(restTemplate.exchange(eq(url), eq(HttpMethod.GET), eq(entity), any(ParameterizedTypeReference.class)))
                .thenReturn(responseEntity);

        //when
        List<RepositoryDto> repositories = githubRepository.getUserRepositoriesByUsername(username);

        //then
        assertNotNull(repositories);
        assertTrue(repositories.isEmpty());
    }

    @Test
    public void shouldThrowUserNotFoundExceptionWhenApiReturns404ForRepositories() {
        //given
        String username = "test-user";
        String url = "https://api.github.com/users/" + username + "/repos";

        HttpClientErrorException exception = HttpClientErrorException.create(
                HttpStatus.NOT_FOUND, "User not found", null, null, null
        );

        when(restTemplate.exchange(eq(url), eq(HttpMethod.GET), eq(entity), any(ParameterizedTypeReference.class)))
                .thenThrow(exception);

        //when/then
        UserNotFoundException thrown = assertThrows(UserNotFoundException.class,
                () -> githubRepository.getUserRepositoriesByUsername(username)
        );
        assertEquals("User 'test-user' not found", thrown.getMessage());
    }

    @Test
    public void shouldReturnBranchesWhenApiReturnsBranches() {
        //given
        String username = "test-user";
        String repoName = "test";
        String url = "https://api.github.com/repos/" + username + "/" + repoName + "/branches";
        List<BranchDto> mockBranches = List.of(
                new BranchDto("test-main", new CommitDto("test-sha-123"))
        );
        ResponseEntity<List<BranchDto>> responseEntity = ResponseEntity.ok(mockBranches);

        when(restTemplate.exchange(eq(url), eq(HttpMethod.GET), eq(entity), any(ParameterizedTypeReference.class)))
                .thenReturn(responseEntity);

        //when
        List<BranchDto> branches = githubRepository.getRepositoryBranches(username, repoName);

        //then
        assertNotNull(branches);
        assertFalse(branches.isEmpty());
        assertEquals(mockBranches.size(), branches.size());
    }

    @Test
    public void shouldReturnEmptyListWhenApiReturnsEmptyResponseForBranches() {
        //given
        String username = "test-user";
        String repoName = "test";
        String url = "https://api.github.com/repos/" + username + "/" + repoName + "/branches";
        ResponseEntity<List<BranchDto>> responseEntity = ResponseEntity.ok(Collections.emptyList());

        when(restTemplate.exchange(eq(url), eq(HttpMethod.GET), eq(entity), any(ParameterizedTypeReference.class)))
                .thenReturn(responseEntity);

        //when
        List<BranchDto> branches = githubRepository.getRepositoryBranches(username, repoName);

        //then
        assertNotNull(branches);
        assertTrue(branches.isEmpty());
    }

    @Test
    public void shouldThrowHttpClientErrorExceptionWhenApiReturnsErrorForBranches() {
        //given
        String username = "test-user";
        String repoName = "test";
        String url = "https://api.github.com/repos/" + username + "/" + repoName + "/branches";

        when(restTemplate.exchange(eq(url), eq(HttpMethod.GET), eq(entity), any(ParameterizedTypeReference.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Bad Request"));

        //when/then
        HttpClientErrorException thrown = assertThrows(HttpClientErrorException.class,
                () -> githubRepository.getRepositoryBranches(username, repoName)
        );
        assertEquals(HttpStatus.BAD_REQUEST, thrown.getStatusCode());
        assertTrue(thrown.getMessage().contains("Bad Request"));
    }

}