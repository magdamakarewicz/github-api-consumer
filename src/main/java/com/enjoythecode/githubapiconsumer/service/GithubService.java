package com.enjoythecode.githubapiconsumer.service;

import com.enjoythecode.githubapiconsumer.dto.RepositoryDto;
import com.enjoythecode.githubapiconsumer.repository.GithubRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class GithubService {

    private final GithubRepository githubRepository;

    public Flux<RepositoryDto> getUserNonForkRepositories(String username) {
        return githubRepository.getUserRepositoriesByUsername(username)
                .filter(repo -> !repo.fork())
                .flatMap(repo ->
                        githubRepository.getRepositoryBranches(username, repo.name())
                                .collectList()
                                .map(branches -> new RepositoryDto(
                                        repo.ownerDto(),
                                        repo.name(),
                                        repo.fork(),
                                        branches
                                ))
                );
    }

}