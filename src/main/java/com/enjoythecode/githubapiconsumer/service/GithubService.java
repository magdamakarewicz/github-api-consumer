package com.enjoythecode.githubapiconsumer.service;

import com.enjoythecode.githubapiconsumer.dto.RepositoryDto;
import com.enjoythecode.githubapiconsumer.repository.GithubRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GithubService {

    private final GithubRepository githubRepository;

    public List<RepositoryDto> getUserNonForkRepositories(String username) {
        List<RepositoryDto> repositories = githubRepository.getUserRepositoriesByUsername(username);
        return repositories.stream()
                .filter(repo -> !repo.isFork())
                .peek(repo -> repo.setBranches(githubRepository.getRepositoryBranches(username, repo.getName())))
                .collect(Collectors.toList());
    }

}