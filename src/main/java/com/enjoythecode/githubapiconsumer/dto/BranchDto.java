package com.enjoythecode.githubapiconsumer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record BranchDto(String name, @JsonProperty("commit") CommitDto commitDto) { }