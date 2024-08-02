package com.enjoythecode.githubapiconsumer.repository;

import com.enjoythecode.githubapiconsumer.dto.BranchDto;
import com.enjoythecode.githubapiconsumer.dto.RepositoryDto;
import com.enjoythecode.githubapiconsumer.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class GithubRepository {

    private static final String GITHUB_API_BASE_URL = "https://api.github.com";

    private final WebClient webClient;

    public Flux<RepositoryDto> getUserRepositoriesByUsername(String username) {
        return webClient.get()
                .uri("/users/{username}/repos", username)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToFlux(RepositoryDto.class)
                .onErrorResume(WebClientResponseException.NotFound.class, e ->
                        Flux.error(new UserNotFoundException("User '" + username + "' not found"))
                );
    }

    public Flux<BranchDto> getRepositoryBranches(String username, String repoName) {
        return webClient.get()
                .uri("/repos/{username}/{repoName}/branches", username, repoName)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToFlux(BranchDto.class);
    }

}