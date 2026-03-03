package com.bilbo.store.service.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDTO {
    private String sub;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
}
