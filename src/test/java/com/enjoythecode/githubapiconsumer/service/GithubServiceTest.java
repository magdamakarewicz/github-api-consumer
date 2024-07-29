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
        when(githubRepository.getRepositoryBranches(username, repo1.getName())).thenReturn(List.of(branch));

        //when
        List<RepositoryDto> result = githubService.getUserNonForkRepositories(username);

        //then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(repo1.getName(), result.get(0).getName());
        assertEquals(repo1.getOwnerDto().getLogin(), result.get(0).getOwnerDto().getLogin());
        assertEquals(1, result.get(0).getBranches().size());
        assertEquals(branch.getName(), result.get(0).getBranches().get(0).getName());
        assertEquals(branch.getCommitDto().getSha(), result.get(0).getBranches().get(0).getCommitDto().getSha());
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
        when(githubRepository.getRepositoryBranches(username, repo1.getName())).thenReturn(List.of(branch1));
        when(githubRepository.getRepositoryBranches(username, repo2.getName())).thenReturn(List.of(branch2));

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