package com.enjoythecode.githubapiconsumer.controller;

import com.enjoythecode.githubapiconsumer.dto.RepositoryDto;
import com.enjoythecode.githubapiconsumer.service.GithubService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/github")
@RequiredArgsConstructor
public class GithubController {

    private final GithubService githubService;

    @GetMapping("/users/{username}/repos")
    public ResponseEntity<List<RepositoryDto>> getUserNonRepositories(@PathVariable String username) {
        List<RepositoryDto> repositories = githubService.getUserNonForkRepositories(username);
        return ResponseEntity.ok(repositories);
    }

}