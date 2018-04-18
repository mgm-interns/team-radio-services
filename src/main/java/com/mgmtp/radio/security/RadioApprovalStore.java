package com.mgmtp.radio.security;

import com.mgmtp.radio.domain.user.RadioApproval;
import com.mgmtp.radio.respository.user.RadioApprovalRepository;
import com.mgmtp.radio.support.DateHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.approval.Approval;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class RadioApprovalStore implements ApprovalStore {
    
    RadioApprovalRepository radioApprovalRepository;

    private boolean handleRevocationsAsExpiry = false;

    private DateHelper dateHelper;

    @Autowired
    public RadioApprovalStore(RadioApprovalRepository radioApprovalRepository, DateHelper dateHelper) {
        this.radioApprovalRepository = radioApprovalRepository;
        this.dateHelper = dateHelper;
    }

    @Override
    public boolean addApprovals(final Collection<Approval> approvals) {
        final Collection<RadioApproval> radioApprovals = approvals.stream()
                .map(toRadioApproval())
                .collect(Collectors.toList());

        try {
            radioApprovalRepository.saveAll(radioApprovals);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean revokeApprovals(final Collection<Approval> approvals) {
        boolean success = true;

        final Collection<RadioApproval> radioApprovals = approvals.stream()
                .map(toRadioApproval())
                .collect(Collectors.toList());

        for (final RadioApproval radioApproval : radioApprovals) {
            if (handleRevocationsAsExpiry) {
                RadioApproval existedRadioApproval = radioApprovalRepository.findById(radioApproval.getId()).get();
                existedRadioApproval.setExpiresAt(LocalDate.now());

                try {
                    radioApprovalRepository.save(existedRadioApproval);
                } catch (Exception e) {
                    success = false;
                }

            }
            else {
                try {
                    radioApprovalRepository.deleteByUserIdAndClientIdAndScope(radioApproval.getUserId(),
                            radioApproval.getClientId(),
                            radioApproval.getScope());
                } catch (Exception e) {
                    success = false;
                }
            }
        }
        return success;
    }

    @Override
    public Collection<Approval> getApprovals(final String userId,
                                             final String clientId) {
        final List<RadioApproval> mongoApprovals = radioApprovalRepository.findByUserIdAndClientId(userId, clientId);
        return mongoApprovals.stream()
                .map(toApproval())
                .collect(Collectors.toList());
    }

    private Function<Approval, RadioApproval> toRadioApproval() {
        return approval ->
            new RadioApproval(UUID.randomUUID().toString(),
                    approval.getUserId(),
                    approval.getClientId(),
                    approval.getScope(),
                    approval.getStatus() == null ? Approval.ApprovalStatus.APPROVED : approval.getStatus(),
                    dateHelper.convertDateToLocalDate(approval.getExpiresAt()),
                    dateHelper.convertDateToLocalDate(approval.getLastUpdatedAt()));
    }

    private Function<RadioApproval, Approval> toApproval() {
        return radioApproval ->
                new Approval(radioApproval.getUserId(),
                        radioApproval.getClientId(),
                        radioApproval.getScope(),
                        dateHelper.convertLocalDateToDate(radioApproval.getExpiresAt()),
                        radioApproval.getStatus(),
                        dateHelper.convertLocalDateToDate(radioApproval.getLastUpdatedAt()));
    }

    public void setHandleRevocationsAsExpiry(boolean handleRevocationsAsExpiry) {
        this.handleRevocationsAsExpiry = handleRevocationsAsExpiry;
    }
}
