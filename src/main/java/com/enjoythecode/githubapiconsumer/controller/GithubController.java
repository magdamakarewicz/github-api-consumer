package com.enjoythecode.githubapiconsumer.controller;

import com.enjoythecode.githubapiconsumer.dto.RepositoryDto;
import com.enjoythecode.githubapiconsumer.service.GithubService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/github")
@RequiredArgsConstructor
public class GithubController {

    private final GithubService githubService;

    @GetMapping("/users/{username}/repos")
    public ResponseEntity<Flux<RepositoryDto>> getUserNonForkRepositories(@PathVariable String username) {
        Flux<RepositoryDto> repositories = githubService.getUserNonForkRepositories(username);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(repositories);
    }

}