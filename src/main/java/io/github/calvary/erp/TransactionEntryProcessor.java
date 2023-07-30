package io.github.calvary.erp;

import io.github.calvary.domain.enumeration.TransactionEntryTypes;
import io.github.calvary.erp.queue.Messenger;
import io.github.calvary.erp.queue.TransactionEntryMessage;
import io.github.calvary.service.dto.TransactionEntryDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service("transactionEntryProcessor")
public class TransactionEntryProcessor implements PostingProcessorService<TransactionEntryDTO>{

    private static final Logger log = LoggerFactory.getLogger(TransactionEntryProcessor.class);

    @Value("${queue.transaction-entry.topic}")
    private String topicName;

    private final Messenger<TransactionEntryMessage> transactionEntryMessageMessenger;

    public TransactionEntryProcessor(Messenger<TransactionEntryMessage> transactionEntryMessageMessenger) {
        this.transactionEntryMessageMessenger = transactionEntryMessageMessenger;
    }

    @Override
    public TransactionEntryDTO post(TransactionEntryDTO dto) {
        TransactionEntryMessage message = TransactionEntryMessage
            .builder()
            .id(dto.getId())
            .entryAmount(dto.getEntryAmount())
            .transactionEntryType(dto.getTransactionEntryType() == TransactionEntryTypes.DEBIT ? "DEBIT" : "CREDIT")
            .description(dto.getDescription())
            .wasProposed(dto.getWasProposed())
            .wasPosted(dto.getWasPosted())
            .wasDeleted(dto.getWasDeleted())
            .wasApproved(dto.getWasApproved())
            .transactionAccountId(dto.getTransactionAccount().getId())
            .accountTransactionId(dto.getAccountTransaction().getId())
            .build();

        log.debug("Enqueuing transaction-entry {} to topic {}", message, topicName);

        transactionEntryMessageMessenger.sendMessage(message);

        log.debug("transaction-entry {} has been enqueued to topic {}", message, topicName);

        return dto;
    }


}
