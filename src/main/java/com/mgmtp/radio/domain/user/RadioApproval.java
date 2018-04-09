package com.mgmtp.radio.domain.user;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.oauth2.provider.approval.Approval.ApprovalStatus;

import java.time.LocalDate;

@Document(collection = "radio_approval")
@Data
public class RadioApproval {

    @Id
    private String id;

    private String userId;

    private String clientId;

    private String scope;

    private ApprovalStatus status;

    private LocalDate expiresAt;

    private LocalDate lastUpdatedAt;

    @PersistenceConstructor
    public RadioApproval(final String id,
                         final String userId,
                         final String clientId,
                         final String scope,
                         final ApprovalStatus status,
                         final LocalDate expiresAt,
                         final LocalDate lastUpdatedAt) {
        this.id = id;
        this.userId = userId;
        this.clientId = clientId;
        this.scope = scope;
        this.status = status;
        this.expiresAt = expiresAt;
        this.lastUpdatedAt = lastUpdatedAt;
    }
}
