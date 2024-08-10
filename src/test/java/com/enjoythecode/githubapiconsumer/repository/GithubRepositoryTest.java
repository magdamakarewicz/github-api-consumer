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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@SpringBootTest
class GithubRepositoryTest {

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private GithubRepository githubRepository;

    @BeforeEach
    public void setup() {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersSpec.accept(MediaType.APPLICATION_JSON)).thenReturn(requestHeadersSpec);
    }

    @Test
    public void shouldReturnRepositoriesWhenApiReturnsRepositories() {
        //given
        String username = "test-user";
        List<RepositoryDto> mockRepositories = List.of(
                new RepositoryDto(new OwnerDto(username), "test", false, List.of())
        );

        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToFlux(RepositoryDto.class)).thenReturn(Flux.fromIterable(mockRepositories));
        when(requestHeadersUriSpec.uri(any(String.class), any(String.class))).thenReturn(requestHeadersSpec);

        //when
        List<RepositoryDto> repositories = githubRepository.getUserRepositoriesByUsername(username).collectList().block();

        //then
        assertEquals(1, repositories.size());
        RepositoryDto repository = repositories.get(0);
        assertEquals("test", repository.name());
        assertEquals(username, repository.ownerDto().login());
    }

    @Test
    public void shouldReturnEmptyListWhenApiReturnsEmptyResponseForRepositories() {
        //given
        String username = "test-user";

        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToFlux(RepositoryDto.class)).thenReturn(Flux.empty());
        when(requestHeadersUriSpec.uri(any(String.class), any(String.class))).thenReturn(requestHeadersSpec);

        //when
        List<RepositoryDto> repositories = githubRepository.getUserRepositoriesByUsername(username).collectList().block();

        //then
        assertEquals(0, repositories.size());
    }

    @Test
    public void shouldThrowUserNotFoundExceptionWhenApiReturns404ForRepositories() {
        //given
        String username = "test-user";
        WebClientResponseException exception = WebClientResponseException.create(
                HttpStatus.NOT_FOUND.value(),
                "User not found",
                org.springframework.http.HttpHeaders.EMPTY,
                null,
                StandardCharsets.UTF_8
        );

        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToFlux(RepositoryDto.class)).thenReturn(Flux.error(exception));
        when(requestHeadersUriSpec.uri(any(String.class), any(String.class))).thenReturn(requestHeadersSpec);

        //when/then
        UserNotFoundException thrown = assertThrows(UserNotFoundException.class,
                () -> githubRepository.getUserRepositoriesByUsername(username).collectList().block()
        );
        assertEquals("User 'test-user' not found", thrown.getMessage());
    }

    @Test
    public void shouldReturnBranchesWhenApiReturnsBranches() {
        //given
        String username = "test-user";
        String repoName = "test";
        List<BranchDto> mockBranches = List.of(
                new BranchDto("test-main", new CommitDto("test-sha-123"))
        );

        when(requestHeadersUriSpec.uri(eq("/repos/{username}/{repoName}/branches"), eq(username), eq(repoName)))
                .thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToFlux(BranchDto.class)).thenReturn(Flux.fromIterable(mockBranches));

        //when
        List<BranchDto> branches = githubRepository.getRepositoryBranches(username, repoName).collectList().block();

        //then
        assertEquals(1, branches.size());
        BranchDto branch = branches.get(0);
        assertEquals("test-main", branch.name());
        assertEquals("test-sha-123", branch.commitDto().sha());
    }

    @Test
    public void shouldReturnEmptyListWhenApiReturnsEmptyResponseForBranches() {
        //given
        String username = "test-user";
        String repoName = "test";

        when(requestHeadersUriSpec.uri(eq("/repos/{username}/{repoName}/branches"), eq(username), eq(repoName)))
                .thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToFlux(BranchDto.class)).thenReturn(Flux.empty());

        //when
        List<BranchDto> branches = githubRepository.getRepositoryBranches(username, repoName).collectList().block();

        //then
        assertEquals(0, branches.size());
    }

    @Test
    public void shouldThrowHttpClientErrorExceptionWhenApiReturnsErrorForBranches() {
        //given
        String username = "test-user";
        String repoName = "test";
        WebClientResponseException exception = WebClientResponseException.create(
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                org.springframework.http.HttpHeaders.EMPTY,
                null,
                StandardCharsets.UTF_8
        );

        when(requestHeadersUriSpec.uri(eq("/repos/{username}/{repoName}/branches"), eq(username), eq(repoName)))
                .thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToFlux(BranchDto.class)).thenReturn(Flux.error(exception));

        //when/then
        WebClientResponseException thrown = assertThrows(WebClientResponseException.class,
                () -> githubRepository.getRepositoryBranches(username, repoName).collectList().block()
        );
        assertEquals(HttpStatus.BAD_REQUEST, thrown.getStatusCode());
        assertEquals("Bad Request", thrown.getStatusText());
    }

}