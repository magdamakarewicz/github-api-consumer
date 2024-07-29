package com.enjoythecode.githubapiconsumer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class RepositoryDto {

    private String name;

    @JsonProperty("owner")
    private OwnerDto ownerDto;

    private boolean fork;

    private List<BranchDto> branches;

}
