package vn.com.mbbank.adminportal.core.util;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import vn.com.mbbank.adminportal.common.exception.BusinessErrorCode;
import vn.com.mbbank.adminportal.common.util.CommonErrorCode;
import vn.com.mbbank.adminportal.common.util.Constant;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Log4j2
public class ErrorCode implements CommonErrorCode {
  public static final BusinessErrorCode USER_NOT_FOUND =
      new BusinessErrorCode(4800, "User not found", HttpStatus.NOT_FOUND);
  public static final BusinessErrorCode DUPLICATE_ROLE_CODE =
      new BusinessErrorCode(4801, "Duplicate role code", HttpStatus.BAD_REQUEST);
  public static final BusinessErrorCode NON_INFERIOR_ROLE =
      new BusinessErrorCode(4802, "Role must be inferior to user's role", HttpStatus.BAD_REQUEST);
  public static final BusinessErrorCode PERMISSION_NOT_FOUND =
      new BusinessErrorCode(4803, "Permission not found", HttpStatus.NOT_FOUND);
  public static final BusinessErrorCode ROLE_NOT_FOUND =
      new BusinessErrorCode(4804, "Role not found", HttpStatus.NOT_FOUND);
  public static final BusinessErrorCode SELF_DEACTIVATE_ERROR =
      new BusinessErrorCode(4805, "User can not deactivate itself ", HttpStatus.BAD_REQUEST);
  public static final BusinessErrorCode DUPLICATE_USER =
      new BusinessErrorCode(4806, "Duplicate user", HttpStatus.BAD_REQUEST);
  public static final BusinessErrorCode GET_KEYCLOAK_USER_BUT_NOT_FOUND_ACTIVE =
      new BusinessErrorCode(4807, "Get keycloak user not found active", HttpStatus.NOT_FOUND);
  public static final BusinessErrorCode ROLE_ID_NOT_EXIST =
      new BusinessErrorCode(4808, "Role id not exist", HttpStatus.BAD_REQUEST);
  public static final BusinessErrorCode KEYCLOAK_ROLE_ASSIGNMENT_FAILED =
      new BusinessErrorCode(4809, "Keycloak role assignment failed", HttpStatus.BAD_REQUEST);
  public static final BusinessErrorCode FAIL_TO_CRETE_USER_AND_USER_ROLE =
      new BusinessErrorCode(4810, "Fail to create user and user role", HttpStatus.BAD_REQUEST);
  public static final BusinessErrorCode USER_ROLE_NOT_FOUND =
      new BusinessErrorCode(4811, "User role not found", HttpStatus.NOT_FOUND);
  public static final BusinessErrorCode ALIAS_ACCOUNT_NOT_FOUND =
      new BusinessErrorCode(4812, "Alias account not found", HttpStatus.NOT_FOUND);
  public static final BusinessErrorCode ERROR_CONFIG_NOT_FOUND =
      new BusinessErrorCode(4813, "Error config not found", HttpStatus.NOT_FOUND);
  public static final BusinessErrorCode ERROR_CONFIG_EXISTED =
      new BusinessErrorCode(4814, "Error config already existed", HttpStatus.BAD_REQUEST);
  public static final BusinessErrorCode ERROR_CODE_CONFIG_EXISTED =
      new BusinessErrorCode(4815, "Error code for the error rate that already exists", HttpStatus.BAD_REQUEST);
  public static final BusinessErrorCode ERROR_CODE_CONFIG_NOT_FOUND =
      new BusinessErrorCode(4816, "Error code for the error rate not found", HttpStatus.NOT_FOUND);
  public static final BusinessErrorCode ROLE_CANT_BE_UPDATED =
      new BusinessErrorCode(4817, "Role can't be updated", HttpStatus.BAD_REQUEST);
  public static final BusinessErrorCode EXCEEDED_OTP_VERIFYING =
      new BusinessErrorCode(4818, "Verifying OTP reached limit", HttpStatus.TOO_MANY_REQUESTS);
  public static final BusinessErrorCode TOTAL_TRANSACTION_NOT_FOUND =
      new BusinessErrorCode(4819, "Total transaction not found", HttpStatus.NOT_FOUND);
  public static final BusinessErrorCode FILE_SIZE_EXCEEDS_LIMIT =
      new BusinessErrorCode(4820, "File size exceeds the 1MB limit.", HttpStatus.BAD_REQUEST);
  public static final BusinessErrorCode MSGID_EXISTED =
      new BusinessErrorCode(4821, "MsgId already exists in file", HttpStatus.BAD_REQUEST);
  public static final BusinessErrorCode FILE_TYPE_INVALID =
      new BusinessErrorCode(4822, "Invalid file type. Only .xlsx or .xls files are allowed.", HttpStatus.BAD_REQUEST);
  public static final BusinessErrorCode FILE_EMPTY =
      new BusinessErrorCode(4823, "File is empty", HttpStatus.BAD_REQUEST);
  public static final BusinessErrorCode MSGIDS_COUNT_EXCEEDS_LIMIT =
      new BusinessErrorCode(4824, "List MsgIds count exceeds the 1000 limit.", HttpStatus.BAD_REQUEST);
  public static final BusinessErrorCode FILE_HEADER_INVALID =
      new BusinessErrorCode(4825, "Header invalid.", HttpStatus.BAD_REQUEST);
  public static final BusinessErrorCode MSGID_LENGTH_EXCEEDS_LIMIT =
      new BusinessErrorCode(4826, "MsgId length can't over 100", HttpStatus.BAD_REQUEST);
  public static final BusinessErrorCode MSGID_EMPTY =
      new BusinessErrorCode(4827, "MsgId is empty.", HttpStatus.BAD_REQUEST);
  public static final BusinessErrorCode DUPLICATE_ACCOUNT_NO_BANK_CODE_TRANSFER_CHANNEL =
      new BusinessErrorCode(4828, "Duplicate accountNo bankCode transferChannel", HttpStatus.BAD_REQUEST);
  public static final BusinessErrorCode DUPLICATE_ACCOUNT_NO_BANK_CODE_ACTIVE =
      new BusinessErrorCode(4829, "Duplicate accountNo bankCode active", HttpStatus.BAD_REQUEST);
  public static final BusinessErrorCode WHITELIST_ACCOUNT_NOT_FOUND =
      new BusinessErrorCode(4830, "WhitelistAccount Not Found", HttpStatus.NOT_FOUND);
  public static final BusinessErrorCode INVALID_APPROVAL_STATUS =
      new BusinessErrorCode(4831, "Invalid approval status", HttpStatus.BAD_REQUEST);
  public static final BusinessErrorCode ALIAS_ACCOUNT_CANT_BE_UPDATED =
      new BusinessErrorCode(4832, "Alias account can't be updated", HttpStatus.BAD_REQUEST);
  public static final BusinessErrorCode WHITELIST_ACCOUNT_HISTORY_NOT_FOUND =
      new BusinessErrorCode(4833, "WhitelistAccount History Not Found", HttpStatus.NOT_FOUND);
  public static final BusinessErrorCode ALIAS_ACCOUNT_HISTORY_NOT_FOUND =
      new BusinessErrorCode(4834, "Alias account history not found", HttpStatus.NOT_FOUND);
  public static final BusinessErrorCode TRANSFER_CHANNEL_CONFIG_NOT_FOUND =
      new BusinessErrorCode(4835, "Transfer channel config not found", HttpStatus.NOT_FOUND);
  public static final BusinessErrorCode STATUS_NOT_ALLOWED =
      new BusinessErrorCode(4836, "Status not allowed", HttpStatus.BAD_REQUEST);
  public static final BusinessErrorCode TRANSFER_CHANNEL_BANK_CONFIG_NOT_FOUND =
      new BusinessErrorCode(4837, "Transfer channel bank config not found", HttpStatus.NOT_FOUND);
  public static final BusinessErrorCode TRANSFER_CHANNEL_BANK_CONFIG_HISTORY_NOT_FOUND =
      new BusinessErrorCode(4838, "Transfer channel bank config history not found", HttpStatus.NOT_FOUND);
  public static final BusinessErrorCode TRANSFER_CHANNEL_LIMIT_NOT_FOUND =
          new BusinessErrorCode(4839, "Transfer channel limit not found", HttpStatus.NOT_FOUND);
  public static final BusinessErrorCode TRANSFER_CHANNEL_LIMIT_HISTORY_NOT_FOUND =
          new BusinessErrorCode(4840, "Transfer channel limit history not found", HttpStatus.NOT_FOUND);
  public static final BusinessErrorCode DUPLICATE_TRANSFER_CHANNEL_CONFIG_PRIORITY =
      new BusinessErrorCode(4841, "Duplicate transfer channel config priority", HttpStatus.BAD_REQUEST);

  public static final BusinessErrorCode DUPLICATE_ACCOUNT_TYPE_TRANSACTION_TYPE_BANK_CODE_ACCOUNT_NO =
      new BusinessErrorCode(4842, "Duplicate accountType transactionType bankCode accountNo", HttpStatus.BAD_REQUEST);
  public static final BusinessErrorCode BLACKLIST_ACCOUNT_NOT_FOUND =
      new BusinessErrorCode(4843, "BlacklistAccount Not Found", HttpStatus.NOT_FOUND);
  public static final BusinessErrorCode BLACKLIST_ACCOUNT_HISTORY_NOT_FOUND =
      new BusinessErrorCode(4844, "BlacklistAccount History Not Found", HttpStatus.NOT_FOUND);
  public static final BusinessErrorCode CONDITION_NOT_FOUND =
          new BusinessErrorCode(4845, "Condition config not found", HttpStatus.NOT_FOUND);
  public static final BusinessErrorCode DUPLICATE_CONDITION_CONFIG_PRIORITY =
          new BusinessErrorCode(4846, "Duplicate condition config priority", HttpStatus.BAD_REQUEST);
  public static final BusinessErrorCode DUPLICATE_TRANSFER_CHANNEL_CURRENCY =
      new BusinessErrorCode(4847, "Duplicate transfer channel currency", HttpStatus.BAD_REQUEST);
  public static final BusinessErrorCode TRANSACTION_AMOUNT_NOT_FOUND =
      new BusinessErrorCode(4848, "Transaction amount not found", HttpStatus.NOT_FOUND);

  public static final BusinessErrorCode USERNAME_PASSWORD_INVALID =
          new BusinessErrorCode(4849, "Username or password invalid", HttpStatus.BAD_REQUEST);

  public static final BusinessErrorCode ASSIGN_ROLE_ERROR =
      new BusinessErrorCode(5800, "Assign Role to User error", HttpStatus.INTERNAL_SERVER_ERROR);
  public static final BusinessErrorCode ASSIGN_ROLE_TIMEOUT =
      new BusinessErrorCode(5801, "Assign Role to User timeout", HttpStatus.INTERNAL_SERVER_ERROR);
  public static final BusinessErrorCode REVOKE_ROLE_ERROR =
      new BusinessErrorCode(5802, "Revoke Role from User error", HttpStatus.INTERNAL_SERVER_ERROR);
  public static final BusinessErrorCode REVOKE_ROLE_TIMEOUT =
      new BusinessErrorCode(5803, "Revoke Role from User timeout", HttpStatus.INTERNAL_SERVER_ERROR);
  public static final BusinessErrorCode GET_KEYCLOAK_USER_INFO_ERROR =
      new BusinessErrorCode(5804, "Get keycloak user info error", HttpStatus.INTERNAL_SERVER_ERROR);
  public static final BusinessErrorCode GET_KEYCLOAK_USER_INFO_TIMEOUT =
      new BusinessErrorCode(5805, "Get keycloak user info timeout", HttpStatus.INTERNAL_SERVER_ERROR);
  public static final BusinessErrorCode GET_KEYCLOAK_USER_NOT_FOUND =
      new BusinessErrorCode(5806, "Get keycloak user not found", HttpStatus.INTERNAL_SERVER_ERROR);
  public static final BusinessErrorCode GET_HCM_USER_TIMEOUT =
      new BusinessErrorCode(5807, "Get user info from hcm timeout", HttpStatus.INTERNAL_SERVER_ERROR);
  public static final BusinessErrorCode GET_HCM_USER_ERROR =
      new BusinessErrorCode(5808, "Get user info from hcm error", HttpStatus.INTERNAL_SERVER_ERROR);
  public static final BusinessErrorCode HCM_USER_NOT_FOUND =
      new BusinessErrorCode(5809, "Get user info from hcm not found", HttpStatus.INTERNAL_SERVER_ERROR);
  public static final BusinessErrorCode HCM_USER_PHONE_NUMBER_NULL =
      new BusinessErrorCode(5812, "Get phone number in user info from hcm null", HttpStatus.INTERNAL_SERVER_ERROR);
  public static final BusinessErrorCode SEND_OTP_TIMEOUT =
      new BusinessErrorCode(5813, "Get otp from 2fa timeout", HttpStatus.INTERNAL_SERVER_ERROR);
  public static final BusinessErrorCode SEND_OTP_ERROR =
      new BusinessErrorCode(5814, "Get otp from 2fa hcm error", HttpStatus.INTERNAL_SERVER_ERROR);
  public static final BusinessErrorCode SEND_OTP_FAIL =
      new BusinessErrorCode(5815, "Get otp from 2fa hcm fail", HttpStatus.INTERNAL_SERVER_ERROR);
  public static final BusinessErrorCode VERIFY_OTP_TIMEOUT =
      new BusinessErrorCode(5816, "Verify otp from 2fa timeout", HttpStatus.INTERNAL_SERVER_ERROR);
  public static final BusinessErrorCode VERIFY_OTP_ERROR =
      new BusinessErrorCode(5817, "Verify otp info 2fa hcm error", HttpStatus.INTERNAL_SERVER_ERROR);
  public static final BusinessErrorCode MARK_SENT_FAIL =
      new BusinessErrorCode(5818, "Mark event message sent fail", HttpStatus.INTERNAL_SERVER_ERROR);
  public static final BusinessErrorCode CREATE_ALIAS_ACCOUNT_FAIL =
      new BusinessErrorCode(5819, "Create alias account fail", HttpStatus.INTERNAL_SERVER_ERROR);
  public static final BusinessErrorCode UPDATE_ALIAS_ACCOUNT_FAIL =
      new BusinessErrorCode(5820, "Update alias account fail", HttpStatus.INTERNAL_SERVER_ERROR);
  public static final BusinessErrorCode ALIAS_ACCOUNT_NAME_EXISTED =
      new BusinessErrorCode(5821, "Alias account name existed", HttpStatus.INTERNAL_SERVER_ERROR);
  public static final BusinessErrorCode CREATE_TRANSACTION_AMOUNT_TIMEOUT =
      new BusinessErrorCode(5822, "Create transaction amount timeout", HttpStatus.INTERNAL_SERVER_ERROR);
  public static final BusinessErrorCode CREATE_TRANSACTION_AMOUNT_ERROR =
      new BusinessErrorCode(5823, "Create transaction amount fail", HttpStatus.INTERNAL_SERVER_ERROR);
  public static final BusinessErrorCode UPDATE_TRANSACTION_AMOUNT_ERROR =
      new BusinessErrorCode(5824, "Update transaction amount fail", HttpStatus.INTERNAL_SERVER_ERROR);
  public static final BusinessErrorCode UPDATE_TRANSACTION_AMOUNT_TIMEOUT =
      new BusinessErrorCode(5825, "Update transaction amount timeout", HttpStatus.INTERNAL_SERVER_ERROR);
  public static final BusinessErrorCode GET_TRANSACTION_AMOUNT_ERROR =
      new BusinessErrorCode(5826, "Get transaction amount fail", HttpStatus.INTERNAL_SERVER_ERROR);
  public static final BusinessErrorCode GET_TRANSACTION_AMOUNT_TIMEOUT =
      new BusinessErrorCode(5827, "Get transaction amount timeout", HttpStatus.INTERNAL_SERVER_ERROR);
  public static final BusinessErrorCode GET_TRANSACTION_AMOUNTS_ERROR =
      new BusinessErrorCode(5828, "Get transaction amounts fail", HttpStatus.INTERNAL_SERVER_ERROR);
  public static final BusinessErrorCode GET_TRANSACTION_AMOUNTS_TIMEOUT =
      new BusinessErrorCode(5829, "Get transaction amounts timeout", HttpStatus.INTERNAL_SERVER_ERROR);
  public static final BusinessErrorCode VALIDATE_TOKEN_ERROR =
      new BusinessErrorCode(5830, "Validate token error", HttpStatus.INTERNAL_SERVER_ERROR);
  public static final BusinessErrorCode VALIDATE_TOKEN_TIMEOUT =
      new BusinessErrorCode(5831, "Validate token timeout", HttpStatus.INTERNAL_SERVER_ERROR);
  public static final BusinessErrorCode TOTAL_TRANSACTION_CHANNEL_AND_BANK_CODE_EXISTED =
      new BusinessErrorCode(5832, "Channel with bank code existed", HttpStatus.INTERNAL_SERVER_ERROR);
  public static final BusinessErrorCode CREATE_TOTAL_TRANSACTION_FAIL =
      new BusinessErrorCode(5833, "Create total transaction fail", HttpStatus.INTERNAL_SERVER_ERROR);
  public static final BusinessErrorCode GET_MSG_ID_TIMEOUT =
      new BusinessErrorCode(5834, "Get msgIds timeout", HttpStatus.INTERNAL_SERVER_ERROR);
  public static final BusinessErrorCode GET_MSG_ID_ERROR =
      new BusinessErrorCode(5835, "Get msgIds fail", HttpStatus.INTERNAL_SERVER_ERROR);
  public static final BusinessErrorCode GET_NAPAS_TRANSACTIONS_TIMEOUT =
      new BusinessErrorCode(5836, "Get napas transactions timeout", HttpStatus.INTERNAL_SERVER_ERROR);
  public static final BusinessErrorCode GET_NAPAS_TRANSACTIONS_ERROR =
      new BusinessErrorCode(5837, "Get napas transactions fail", HttpStatus.INTERNAL_SERVER_ERROR);
  public static final BusinessErrorCode EXPORT_NAPAS_TRANSACTION_TIMEOUT =
      new BusinessErrorCode(5838, "Export napas transaction file timeout", HttpStatus.INTERNAL_SERVER_ERROR);
  public static final BusinessErrorCode EXPORT_NAPAS_TRANSACTION_ERROR =
      new BusinessErrorCode(5839, "Export napas transaction file fail", HttpStatus.INTERNAL_SERVER_ERROR);
  public static final BusinessErrorCode APPROVE_ALIAS_ACCOUNT_FAIL =
      new BusinessErrorCode(5840, "Approve alias account fail", HttpStatus.INTERNAL_SERVER_ERROR);
  public static final BusinessErrorCode APPROVE_WHITELIST_ACCOUNT_FAIL =
      new BusinessErrorCode(5841, "Approve whitelist account fail", HttpStatus.INTERNAL_SERVER_ERROR);
  public static final BusinessErrorCode GET_BANKS_TIMEOUT =
      new BusinessErrorCode(5842, "Get banks timeout", HttpStatus.INTERNAL_SERVER_ERROR);
  public static final BusinessErrorCode GET_BANKS_ERROR =
      new BusinessErrorCode(5843, "Get banks error", HttpStatus.INTERNAL_SERVER_ERROR);
  public static final BusinessErrorCode UPDATE_TRANSFER_CHANNEL_CONFIG_FAIL =
      new BusinessErrorCode(5844, "Update transfer channel config fail", HttpStatus.INTERNAL_SERVER_ERROR);
  public static final BusinessErrorCode TRANSFER_CHANNEL_BANK_CONFIG_EXISTED =
      new BusinessErrorCode(5845, "Transfer channel bank config existed", HttpStatus.INTERNAL_SERVER_ERROR);
  public static final BusinessErrorCode CREATE_TRANSFER_CHANNEL_BANK_CONFIG_FAIL =
      new BusinessErrorCode(5846, "Create transfer channel bank config fail", HttpStatus.INTERNAL_SERVER_ERROR);
  public static final BusinessErrorCode UPDATE_TRANSFER_CHANNEL_BANK_CONFIG_FAIL =
      new BusinessErrorCode(5847, "Update transfer channel bank config fail", HttpStatus.INTERNAL_SERVER_ERROR);
  public static final BusinessErrorCode SEND_AND_UPDATE_EVENT_ERROR =
      new BusinessErrorCode(5848, "Send and update event message error", HttpStatus.INTERNAL_SERVER_ERROR);
  public static final BusinessErrorCode TRANSFER_CHANNEL_LIMIT_EXISTED =
          new BusinessErrorCode(5849, "Transfer channel existed", HttpStatus.INTERNAL_SERVER_ERROR);
  public static final BusinessErrorCode CREATE_TRANSFER_CHANNEL_LIMIT_FAIL =
          new BusinessErrorCode(5850, "Create transfer channel limit fail", HttpStatus.INTERNAL_SERVER_ERROR);
  public static final BusinessErrorCode UPDATE_TRANSFER_CHANNEL_LIMIT_FAIL =
          new BusinessErrorCode(5851, "Update transfer channel limit fail", HttpStatus.INTERNAL_SERVER_ERROR);
  public static final BusinessErrorCode CREATE_BLACKLIST_ACCOUNT_FAIL =
      new BusinessErrorCode(5852, "Create blacklist account fail", HttpStatus.INTERNAL_SERVER_ERROR);
  public static final BusinessErrorCode UPDATE_BLACKLIST_ACCOUNT_FAIL =
      new BusinessErrorCode(5853, "Update blacklist account fail", HttpStatus.INTERNAL_SERVER_ERROR);
  public static final BusinessErrorCode UPDATE_SWITCHING_CONDITION_FAIL =
          new BusinessErrorCode(5854, "Update switching condition fail", HttpStatus.INTERNAL_SERVER_ERROR);
  private static final Map<String, BusinessErrorCode> errorCodeMap;

  static {
    var codes = new HashMap<String, BusinessErrorCode>();
    var duplications = Arrays.stream(ErrorCode.class.getFields())
        .filter(f -> Modifier.isStatic(f.getModifiers()) && f.getType().equals(BusinessErrorCode.class))
        .map(f -> {
          try {
            return (BusinessErrorCode) f.get(null);
          } catch (IllegalAccessException e) {
            log.error("Can't load error code into map", e);
            throw new RuntimeException(e);
          }
        })
        .filter(c -> codes.put(Constant.PREFIX_RESPONSE_CODE + c.code(), c) != null)
        .toList();
    if (!duplications.isEmpty()) {
      throw new RuntimeException("Found error code duplication: " + duplications);
    }
    errorCodeMap = Map.copyOf(codes);
  }

  private ErrorCode() {
    throw new UnsupportedOperationException();
  }

  public static BusinessErrorCode lookup(String code) {
    return errorCodeMap.get(code);
  }

  public static BusinessErrorCode lookup(String code, BusinessErrorCode defaultErrorCode) {
    return errorCodeMap.getOrDefault(code, defaultErrorCode);
  }
}
