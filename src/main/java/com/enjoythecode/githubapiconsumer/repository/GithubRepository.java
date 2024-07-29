package com.enjoythecode.githubapiconsumer.repository;

import com.enjoythecode.githubapiconsumer.dto.BranchDto;
import com.enjoythecode.githubapiconsumer.dto.RepositoryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GithubRepository {

    private static final String GITHUB_API_BASE_URL = "https://api.github.com";

    private final RestTemplate restTemplate;

    public List<RepositoryDto> getUserRepositoriesByUsername(String username) {
        String url = GITHUB_API_BASE_URL + "/users/" + username + "/repos";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<List<RepositoryDto>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<List<RepositoryDto>>() {
                }
        );
        return Optional.ofNullable(response.getBody()).orElse(Collections.emptyList());
    }

    public List<BranchDto> getRepositoryBranches(String username, String repoName) {
        String url = GITHUB_API_BASE_URL + "/repos/" + username + "/" + repoName + "/branches";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<List<BranchDto>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<List<BranchDto>>() {
                }
        );
        return Optional.ofNullable(response.getBody()).orElse(Collections.emptyList());
    }

}