package com.enjoythecode.githubapiconsumer.service;

import com.enjoythecode.githubapiconsumer.dto.BranchDto;
import com.enjoythecode.githubapiconsumer.dto.CommitDto;
import com.enjoythecode.githubapiconsumer.dto.OwnerDto;
import com.enjoythecode.githubapiconsumer.dto.RepositoryDto;
import com.enjoythecode.githubapiconsumer.repository.GithubRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
class GithubServiceTest {

    @Mock
    private GithubRepository githubRepository;

    @InjectMocks
    private GithubService githubService;

    @Test
    public void shouldReturnNonForkRepositoriesWithBranches() {
        //given
        String username = "test-user";
        RepositoryDto repo1 = new RepositoryDto(new OwnerDto(username), "repo1", false, List.of());
        RepositoryDto repo2 = new RepositoryDto(new OwnerDto(username), "repo2", true, List.of());
        List<RepositoryDto> repositories = List.of(repo1, repo2);

        BranchDto branch = new BranchDto("test-main", new CommitDto("test-sha-123"));

        when(githubRepository.getUserRepositoriesByUsername(username)).thenReturn(repositories);
        when(githubRepository.getRepositoryBranches(username, repo1.name())).thenReturn(List.of(branch));

        //when
        List<RepositoryDto> result = githubService.getUserNonForkRepositories(username);

        //then
        assertNotNull(result);
        assertEquals(1, result.size());
        RepositoryDto returnedRepo = result.get(0);
        assertEquals(repo1.name(), returnedRepo.name());
        assertEquals(repo1.ownerDto().login(), returnedRepo.ownerDto().login());
        assertEquals(1, returnedRepo.branches().size());
        BranchDto returnedBranch = returnedRepo.branches().get(0);
        assertEquals(branch.name(), returnedBranch.name());
        assertEquals(branch.commitDto().sha(), returnedBranch.commitDto().sha());
    }

    @Test
    public void shouldReturnEmptyListWhenOnlyForkRepositoriesFound() {
        //given
        String username = "test-user";
        RepositoryDto repo1 = new RepositoryDto(new OwnerDto(username), "repo1", true, List.of());
        RepositoryDto repo2 = new RepositoryDto(new OwnerDto(username), "repo2", true, List.of());
        List<RepositoryDto> repositories = List.of(repo1, repo2);

        BranchDto branch1 = new BranchDto("test-main", new CommitDto("test-sha-123"));
        BranchDto branch2 = new BranchDto("test-main", new CommitDto("test-sha-123"));

        when(githubRepository.getUserRepositoriesByUsername(username)).thenReturn(repositories);
        when(githubRepository.getRepositoryBranches(username, repo1.name())).thenReturn(List.of(branch1));
        when(githubRepository.getRepositoryBranches(username, repo2.name())).thenReturn(List.of(branch2));

        //when
        List<RepositoryDto> result = githubService.getUserNonForkRepositories(username);

        //then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void shouldReturnEmptyListWhenNoRepositoriesFound() {
        //given
        String username = "test-user";

        when(githubRepository.getUserRepositoriesByUsername(username)).thenReturn(Collections.emptyList());

        //when
        List<RepositoryDto> result = githubService.getUserNonForkRepositories(username);

        //then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void shouldHandleHttpClientErrorException() {
        //given
        String username = "test-user";
        when(githubRepository.getUserRepositoriesByUsername(username))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND, "User not found"));

        //when/then
        assertThrows(HttpClientErrorException.class, () -> githubService.getUserNonForkRepositories(username));
    }

}