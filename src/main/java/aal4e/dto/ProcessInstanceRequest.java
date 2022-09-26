package aal4e.dto;

import lombok.Data;

/**
 * Once received this request by the system, a new plan instance is created by flowable.
 */
@Data
public class ProcessInstanceRequest {
    String processID;
    String requestDescription;
}