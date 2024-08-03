package com.enjoythecode.githubapiconsumer;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

@ExtendWith(WireMockExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GithubApiConsumerIntegrationTests {

    @RegisterExtension
    static WireMockExtension wireMock = WireMockExtension.newInstance().build();

    @Autowired
    private WebTestClient webTestClient;

    @BeforeEach
    public void setUp() {
        webTestClient = WebTestClient.bindToServer()
                .baseUrl("http://localhost:" + wireMock.getPort())
                .build();
    }

    @Test
    @Disabled("Temporarily ignoring test")
    public void shouldReturnOnlyNonForkRepositoriesWhenUserExists() {
        //given
        wireMock.stubFor(WireMock.get(WireMock.urlEqualTo("/api/github/users/test-user/repos"))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                [
                                    {
                                        "name": "repo1",
                                        "fork": false,
                                        "branches": [
                                            {
                                                "name": "branch1",
                                                "commit": {
                                                    "sha": "test-sha-123"
                                                }
                                            }
                                        ],
                                        "owner": {
                                            "login": "test-user"
                                        }
                                    },
                                    {
                                        "name": "repo2",
                                        "fork": true,
                                        "branches": [
                                            {
                                                "name": "branch1",
                                                "commit": {
                                                    "sha": "test-sha-456"
                                                }
                                            }
                                        ],
                                        "owner": {
                                            "login": "test-user"
                                        }
                                    }
                                ]""")));

        //when/then
        webTestClient.get().uri("/api/github/users/test-user/repos")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .consumeWith(response -> {
                    String responseBody = new String(response.getResponseBody());
                    System.out.println("Response Body: " + responseBody);
                })
                .jsonPath("$[0].name").isEqualTo("repo1")
                .jsonPath("$[0].branches[0].name").isEqualTo("branch1")
                .jsonPath("$[0].branches[0].commit.sha").isEqualTo("test-sha-123")
                .jsonPath("$[1]").doesNotExist();
    }

    @Test
    public void shouldReturnEmptyArrayWhenUserHasNoRepositories() {
        //given
        wireMock.stubFor(WireMock.get(WireMock.urlEqualTo("/api/github/users/no-repos-user/repos"))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", "application/json")
                        .withBody("[]")));

        //when/then
        webTestClient.get().uri("/api/github/users/no-repos-user/repos")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$").isEmpty();
    }

    @Test
    public void shouldReturnEmptyArrayForBranchesWhenRepositoryHasNoBranches() {
        //given
        wireMock.stubFor(WireMock.get(WireMock.urlEqualTo("/api/github/users/no-branches-user/repos"))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                [
                                    {
                                        "name": "repo1",
                                        "fork": false,
                                        "branches": [
                                            {
                                                "name": "branch1",
                                                "commit": {
                                                    "sha": "test-sha-123"
                                                }
                                            }
                                        ],
                                        "owner": {
                                            "login": "no-branches-user"
                                        }
                                    },
                                    {
                                        "name": "repo2",
                                        "fork": false,
                                        "branches": [],
                                        "owner": {
                                            "login": "no-branches-user"
                                        }
                                    }
                                ]""")));

        //when/then
        webTestClient.get().uri("/api/github/users/no-branches-user/repos")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$[0].branches").isArray()
                .jsonPath("$[1].branches").isEmpty();
    }

    @Test
    public void shouldReturnNotFoundStatusAndMessageWhenUserDoesNotExist() {
        //given
        wireMock.stubFor(WireMock.get(WireMock.urlEqualTo("/api/github/users/nonexistent-user/repos"))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.NOT_FOUND.value())
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                {
                                    "status": 404,
                                    "message": "User 'nonexistent-user' not found"
                                }""")));

        //when/then
        webTestClient.get().uri("/api/github/users/nonexistent-user/repos")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.message").isEqualTo("User 'nonexistent-user' not found");
    }

}
