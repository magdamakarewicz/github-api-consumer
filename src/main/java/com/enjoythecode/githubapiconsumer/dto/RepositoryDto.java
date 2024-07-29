package com.enjoythecode.githubapiconsumer.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RepositoryDto {

    @JsonProperty("owner")
    private OwnerDto ownerDto;

    private String name;

    @JsonIgnore
    private boolean fork;

    private List<BranchDto> branches;

}
