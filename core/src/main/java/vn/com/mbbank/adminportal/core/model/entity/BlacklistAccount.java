  package vn.com.mbbank.adminportal.core.model.entity;

  import jakarta.persistence.*;
  import lombok.Data;
  import lombok.experimental.Accessors;
  import vn.com.mbbank.adminportal.core.model.AccountEntryType;
  import vn.com.mbbank.adminportal.core.model.TransactionType;

  import java.time.OffsetDateTime;

  @Data
  @Accessors(chain = true)
  @Table(name = "PAP_BLACKLIST_ACCOUNT")
  @Entity
  public class BlacklistAccount {
    @Id
    @SequenceGenerator(name = "PAP_BLACKLIST_ACCOUNT_ID_SEQ", sequenceName = "PAP_BLACKLIST_ACCOUNT_ID_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PAP_BLACKLIST_ACCOUNT_ID_SEQ")
    Long id;
    String accountNo;
    String bankCode;
    @Enumerated(EnumType.STRING)
    AccountEntryType type;
    @Enumerated(EnumType.STRING)
    TransactionType transactionType;
    String reason;
    boolean active;
    String createdBy;
    OffsetDateTime createdAt;
    String updatedBy;
    OffsetDateTime updatedAt;
  }
