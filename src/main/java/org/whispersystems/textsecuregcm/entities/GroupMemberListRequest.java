package org.whispersystems.textsecuregcm.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class GroupMemberListRequest {

  @JsonProperty
  private List<String> numbers;

  public List<String> getNumbers() {
    return numbers;
  }

  public void setNumbers(List<String> numbers) {
    this.numbers = numbers;
  }
}
