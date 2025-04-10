package br.com.compass.bankchallenge.service;

import java.time.LocalDateTime;
import java.util.List;

import br.com.compass.bankchallenge.domain.Account;
import br.com.compass.bankchallenge.domain.Manager;
import br.com.compass.bankchallenge.domain.Operation;
import br.com.compass.bankchallenge.domain.RefundRequest;
import br.com.compass.bankchallenge.domain.enums.OperationType;
import br.com.compass.bankchallenge.domain.enums.RefundStatus;
import br.com.compass.bankchallenge.repository.RefundRequestRepository;

public class RefundRequestService {

    private final RefundRequestRepository refundRequestRepository = new RefundRequestRepository();
    private final OperationService operationService = new OperationService();

    public void requestRefund(Long operationId, Long clientId) {
        Operation operation = operationService.getOperationById(operationId);

        if (operation == null || !operation.getAccount().getClient().getId().equals(clientId)) {
            throw new IllegalArgumentException("Operation not found or does not belong to client.");
        }

        RefundRequest request = new RefundRequest();
        request.setClient(operation.getAccount().getClient());
        request.setOperation(operation);
        request.setStatus(RefundStatus.PENDING);
        request.setRequestDate(LocalDateTime.now());

        refundRequestRepository.save(request);
    }

    public void approveRefund(Manager manager, Long refundRequestId) {
        RefundRequest refundRequest = refundRequestRepository.findById(refundRequestId);

        if (refundRequest == null || refundRequest.getStatus() != RefundStatus.PENDING) {
            throw new IllegalArgumentException("Refund request not found or is not pending.");
        }

        Operation originalOp = refundRequest.getOperation();

        if (originalOp.getOperationType() != OperationType.TRANSFER) {
            throw new UnsupportedOperationException("Only transfer operations can be refunded.");
        }

        Long sourceAccountId = originalOp.getAccount().getId();
        Long targetAccountId = originalOp.getTargetAccount().getId();
        Double amount = originalOp.getAmount();

        operationService.transfer(targetAccountId, sourceAccountId, amount);

        refundRequest.setManager(manager);
        refundRequest.setStatus(RefundStatus.APPROVED);
        refundRequest.setResponseDate(LocalDateTime.now());

        refundRequestRepository.update(refundRequest);
    }

    public void rejectRefund(Manager manager, Long refundRequestId) {
        RefundRequest refundRequest = refundRequestRepository.findById(refundRequestId);

        if (refundRequest == null || refundRequest.getStatus() != RefundStatus.PENDING) {
            throw new IllegalArgumentException("Refund request not found or is not pending.");
        }

        refundRequest.setManager(manager);
        refundRequest.setStatus(RefundStatus.REJECTED);
        refundRequest.setResponseDate(LocalDateTime.now());
        refundRequestRepository.update(refundRequest);
    }
}
