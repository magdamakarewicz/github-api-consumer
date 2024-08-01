package com.enjoythecode.githubapiconsumer.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record RepositoryDto(
        @JsonProperty("owner") OwnerDto ownerDto,
        String name,
        @JsonIgnore boolean fork,
        List<BranchDto> branches) { }
