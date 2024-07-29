package com.enjoythecode.githubapiconsumer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BranchDto {

    private String name;

    @JsonProperty("commit")
    private CommitDto commitDto;

}
