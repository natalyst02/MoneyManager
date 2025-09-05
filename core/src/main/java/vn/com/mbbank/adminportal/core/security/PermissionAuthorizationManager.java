package vn.com.mbbank.adminportal.core.security;

import jakarta.servlet.DispatcherType;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.stereotype.Component;
import vn.com.mbbank.adminportal.core.model.BitmaskValue;

import static org.springframework.http.HttpMethod.*;

@Component
@RequiredArgsConstructor
public class PermissionAuthorizationManager {
  private static final AuthorizationDecision GRANTED = new AuthorizationDecision(true);
  private static final AuthorizationDecision DENIED = new AuthorizationDecision(false);
  private final BitmaskPermissionEvaluator permissionEvaluator;

  private <T> AuthorizationManager<T> hasPermission(String module, BitmaskValue value) {
    return (authentication, context) -> {
      if (permissionEvaluator.hasPermission(authentication.get(), module, value)) {
        return GRANTED;
      }
      return DENIED;
    };
  }

  private <T> AuthorizationManager<T> hasPermission(String module1, String module2, BitmaskValue value) {
    return (authentication, context) -> {
      if (permissionEvaluator.hasPermission(authentication.get(), module1, value) || permissionEvaluator.hasPermission(authentication.get(), module2, value)) {
        return GRANTED;
      }
      return DENIED;
    };
  }

  public void configAdminPortalRequestMatcher(HttpSecurity http) throws Exception {
    http.authorizeHttpRequests(authorize -> authorize
        .dispatcherTypeMatchers(DispatcherType.ASYNC).permitAll()
        .requestMatchers(GET, "/actuator/**", "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
            .requestMatchers(POST, "/login/**").permitAll()
        .requestMatchers(POST, "/otp/**").not().anonymous()
        .requestMatchers(POST, "/total-transactions/**").access(hasPermission("total-transaction", BitmaskValue.INSERT))
        .requestMatchers(PUT, "/total-transactions/**").access(hasPermission("total-transaction", BitmaskValue.UPDATE))
        .requestMatchers(GET, "/total-transactions/**").access(hasPermission("total-transaction", BitmaskValue.VIEW))
        .requestMatchers(GET, "/error-configs/**").access(hasPermission("error-configs", BitmaskValue.VIEW))
        .requestMatchers(POST, "/error-configs/**").access(hasPermission("error-configs", BitmaskValue.INSERT))
        .requestMatchers(PUT, "/error-configs/**").access(hasPermission("error-configs", BitmaskValue.UPDATE))
        .requestMatchers(GET, "/error-code-configs/**").access(hasPermission("error-configs", BitmaskValue.VIEW))
        .requestMatchers(POST, "/error-code-configs/**").access(hasPermission("error-configs", BitmaskValue.INSERT))
        .requestMatchers(PUT, "/error-code-configs/**").access(hasPermission("error-configs", BitmaskValue.UPDATE))
        .requestMatchers(POST, "/alias-accounts").access(hasPermission("alias-account", BitmaskValue.INSERT))
        .requestMatchers(PUT, "/alias-accounts/**").access(hasPermission("alias-account", BitmaskValue.UPDATE))
        .requestMatchers(GET, "/alias-accounts/**").access(hasPermission("alias-account", BitmaskValue.VIEW))
        .requestMatchers(POST, "/alias-accounts/*/approve").access(hasPermission("alias-account", BitmaskValue.APPROVE))
        .requestMatchers(POST, "/alias-accounts/*/reject").access(hasPermission("alias-account", BitmaskValue.APPROVE))
        .requestMatchers(GET, "/roles/**").access(hasPermission("role", BitmaskValue.VIEW))
        .requestMatchers(POST, "/roles**").access(hasPermission("role", BitmaskValue.INSERT))
        .requestMatchers(PUT, "/roles**").access(hasPermission("role", BitmaskValue.UPDATE))
        .requestMatchers(GET, "/permissions**").access(hasPermission("permission", BitmaskValue.VIEW))
    );
  }

  public void configAchRequestMatcher(HttpSecurity http) throws Exception {
    http.authorizeHttpRequests(authorize -> authorize
        .requestMatchers(GET, "/ach/common-config/**").access(hasPermission("ach-common-config", BitmaskValue.VIEW))
        .requestMatchers(POST, "/ach/common-config/**").access(hasPermission("ach-common-config", BitmaskValue.INSERT))
        .requestMatchers(PUT, "/ach/common-config/**").access(hasPermission("ach-common-config", BitmaskValue.UPDATE))
        .requestMatchers(GET, "/ach/tktg-config/**").access(hasPermission("ach-tktg-config", BitmaskValue.VIEW))
        .requestMatchers(POST, "/ach/tktg-config/**").access(hasPermission("ach-tktg-config", BitmaskValue.INSERT))
        .requestMatchers(PUT, "/ach/tktg-config/**").access(hasPermission("ach-tktg-config", BitmaskValue.UPDATE))
    );
  }

  public void configCitadTransferRequestMatcher(HttpSecurity http) throws Exception {
    http.authorizeHttpRequests(authorize -> authorize
        .requestMatchers(GET, "/citad-transfer/configs/citad.gateway.enabled").access(hasPermission("citad-gateway", BitmaskValue.VIEW))
        .requestMatchers(GET, "/citad-transfer/config-histories/citad.gateway.enabled/**").access(hasPermission("citad-gateway", BitmaskValue.VIEW))
        .requestMatchers(new MethodAndQueryParamRequestMatcher(
            "/citad-transfer/config-histories", "key", "citad.gateway.enabled", "GET")).access(hasPermission("citad-gateway", BitmaskValue.VIEW))
        .requestMatchers(PUT, "/citad-transfer/configs/citad.gateway.enabled").access(hasPermission("citad-gateway", BitmaskValue.UPDATE))

        .requestMatchers(GET, "/citad-transfer/configs/transaction.abbreviation").access(hasPermission("citad-transaction-abbreviation", BitmaskValue.VIEW))
        .requestMatchers(GET, "/citad-transfer/config-histories/transaction.abbreviation/**").access(hasPermission("citad-transaction-abbreviation", BitmaskValue.VIEW))
        .requestMatchers(GET, "/citad-transfer/configs/export/transaction.abbreviation").access(hasPermission("citad-transaction-abbreviation", BitmaskValue.VIEW))
        .requestMatchers(new MethodAndQueryParamRequestMatcher(
            "/citad-transfer/config-histories", "key", "transaction.abbreviation", "GET")).access(hasPermission("citad-transaction-abbreviation", BitmaskValue.VIEW))
        .requestMatchers(PUT, "/citad-transfer/configs/transaction.abbreviation").access(hasPermission("citad-transaction-abbreviation", BitmaskValue.UPDATE))

        .requestMatchers(GET, "/citad-transfer/configs/refund.transaction.pattern").access(hasPermission("citad-refund-pattern", BitmaskValue.VIEW))
        .requestMatchers(GET, "/citad-transfer/config-histories/refund.transaction.pattern/**").access(hasPermission("citad-refund-pattern", BitmaskValue.VIEW))
        .requestMatchers(GET, "/citad-transfer/configs/export/refund.transaction.pattern").access(hasPermission("citad-refund-pattern", BitmaskValue.VIEW))
        .requestMatchers(new MethodAndQueryParamRequestMatcher(
            "/citad-transfer/config-histories", "key", "refund.transaction.pattern", "GET")).access(hasPermission("citad-refund-pattern", BitmaskValue.VIEW))
        .requestMatchers(PUT, "/citad-transfer/configs/refund.transaction.pattern").access(hasPermission("citad-refund-pattern", BitmaskValue.UPDATE))

        .requestMatchers(GET, "/citad-transfer/configs/partner.transaction.pattern").access(hasPermission("citad-partner-pattern", BitmaskValue.VIEW))
        .requestMatchers(GET, "/citad-transfer/config-histories/partner.transaction.pattern/**").access(hasPermission("citad-partner-pattern", BitmaskValue.VIEW))
        .requestMatchers(GET, "/citad-transfer/configs/export/partner.transaction.pattern").access(hasPermission("citad-partner-pattern", BitmaskValue.VIEW))
        .requestMatchers(new MethodAndQueryParamRequestMatcher(
            "/citad-transfer/config-histories", "key", "partner.transaction.pattern", "GET")).access(hasPermission("citad-partner-pattern", BitmaskValue.VIEW))
        .requestMatchers(PUT, "/citad-transfer/configs/partner.transaction.pattern").access(hasPermission("citad-partner-pattern", BitmaskValue.UPDATE))

        .requestMatchers(GET, "/citad-transfer/configs/hold.receiver-account").access(hasPermission("citad-hold-receiver-account", BitmaskValue.VIEW))
        .requestMatchers(GET, "/citad-transfer/config-histories/hold.receiver-account/**").access(hasPermission("citad-hold-receiver-account", BitmaskValue.VIEW))
        .requestMatchers(GET, "/citad-transfer/configs/export/hold.receiver-account").access(hasPermission("citad-hold-receiver-account", BitmaskValue.VIEW))
        .requestMatchers(new MethodAndQueryParamRequestMatcher(
            "/citad-transfer/config-histories", "key", "hold.receiver-account", "GET")).access(hasPermission("citad-hold-receiver-account", BitmaskValue.VIEW))
        .requestMatchers(PUT, "/citad-transfer/configs/hold.receiver-account").access(hasPermission("citad-hold-receiver-account", BitmaskValue.UPDATE))

        .requestMatchers(GET, "/citad-transfer/configs/hold.receiver-name.pattern").access(hasPermission("citad-hold-receiver-name-pattern", BitmaskValue.VIEW))
        .requestMatchers(GET, "/citad-transfer/config-histories/hold.receiver-name.pattern/**").access(hasPermission("citad-hold-receiver-name-pattern", BitmaskValue.VIEW))
        .requestMatchers(GET, "/citad-transfer/configs/export/hold.receiver-name.pattern").access(hasPermission("citad-hold-receiver-name-pattern", BitmaskValue.VIEW))
        .requestMatchers(new MethodAndQueryParamRequestMatcher(
            "/citad-transfer/config-histories", "key", "hold.receiver-name.pattern", "GET")).access(hasPermission("citad-hold-receiver-name-pattern", BitmaskValue.VIEW))
        .requestMatchers(PUT, "/citad-transfer/configs/hold.receiver-name.pattern").access(hasPermission("citad-hold-receiver-name-pattern", BitmaskValue.UPDATE))

        .requestMatchers(GET, "/citad-transfer/configs/account.parameter").access(hasPermission("citad-account-parameter", BitmaskValue.VIEW))
        .requestMatchers(GET, "/citad-transfer/config-histories/account.parameter/**").access(hasPermission("citad-account-parameter", BitmaskValue.VIEW))
        .requestMatchers(GET, "/citad-transfer/configs/export/account.parameter").access(hasPermission("citad-account-parameter", BitmaskValue.VIEW))
        .requestMatchers(new MethodAndQueryParamRequestMatcher(
            "/citad-transfer/config-histories", "key", "account.parameter", "GET")).access(hasPermission("citad-account-parameter", BitmaskValue.VIEW))
        .requestMatchers(PUT, "/citad-transfer/configs/account.parameter").access(hasPermission("citad-account-parameter", BitmaskValue.UPDATE))

        .requestMatchers(GET, "/citad-transfer/configs/transaction.replacement").access(hasPermission("citad-transaction-replacement", BitmaskValue.VIEW))
        .requestMatchers(GET, "/citad-transfer/config-histories/transaction.replacement/**").access(hasPermission("citad-transaction-replacement", BitmaskValue.VIEW))
        .requestMatchers(GET, "/citad-transfer/configs/export/transaction.replacement").access(hasPermission("citad-transaction-replacement", BitmaskValue.VIEW))
        .requestMatchers(new MethodAndQueryParamRequestMatcher(
            "/citad-transfer/config-histories", "key", "transaction.replacement", "GET")).access(hasPermission("citad-transaction-replacement", BitmaskValue.VIEW))
        .requestMatchers(PUT, "/citad-transfer/configs/transaction.replacement").access(hasPermission("citad-transaction-replacement", BitmaskValue.UPDATE))

        .requestMatchers(GET, "/citad-transfer/blacklist-accounts").access(hasPermission("citad-blacklist-accounts", BitmaskValue.VIEW))
        .requestMatchers(GET, "/citad-transfer/blacklist-accounts/**").access(hasPermission("citad-blacklist-accounts", BitmaskValue.VIEW))
        .requestMatchers(GET, "/citad-transfer/blacklist-account-histories").access(hasPermission("citad-blacklist-accounts", BitmaskValue.VIEW))
        .requestMatchers(GET, "/citad-transfer/blacklist-account-histories/**").access(hasPermission("citad-blacklist-accounts", BitmaskValue.VIEW))
        .requestMatchers(GET, "/citad-transfer/blacklist-accounts/export").access(hasPermission("citad-blacklist-accounts", BitmaskValue.VIEW))
        .requestMatchers(POST, "/citad-transfer/blacklist-accounts").access(hasPermission("citad-blacklist-accounts", BitmaskValue.INSERT))
        .requestMatchers(PUT, "/citad-transfer/blacklist-accounts/**").access(hasPermission("citad-blacklist-accounts", BitmaskValue.UPDATE))
        .requestMatchers(DELETE, "/citad-transfer/blacklist-accounts/**").access(hasPermission("citad-blacklist-accounts", BitmaskValue.DELETE))

        .requestMatchers(GET, "/citad-transfer/whitelist-accounts").access(hasPermission("citad-whitelist-accounts", BitmaskValue.VIEW))
        .requestMatchers(GET, "/citad-transfer/whitelist-accounts/**").access(hasPermission("citad-whitelist-accounts", BitmaskValue.VIEW))
        .requestMatchers(GET, "/citad-transfer/whitelist-account-histories").access(hasPermission("citad-whitelist-accounts", BitmaskValue.VIEW))
        .requestMatchers(GET, "/citad-transfer/whitelist-account-histories/**").access(hasPermission("citad-whitelist-accounts", BitmaskValue.VIEW))
        .requestMatchers(GET, "/citad-transfer/whitelist-accounts/export").access(hasPermission("citad-whitelist-accounts", BitmaskValue.VIEW))
        .requestMatchers(POST, "/citad-transfer/whitelist-accounts").access(hasPermission("citad-whitelist-accounts", BitmaskValue.INSERT))
        .requestMatchers(PUT, "/citad-transfer/whitelist-accounts/**").access(hasPermission("citad-whitelist-accounts", BitmaskValue.UPDATE))
        .requestMatchers(DELETE, "/citad-transfer/whitelist-accounts/**").access(hasPermission("citad-whitelist-accounts", BitmaskValue.DELETE))

        .requestMatchers(GET, "/citad-transfer/whitelist-categories").access(hasPermission("citad-whitelist-categories", BitmaskValue.VIEW))
        .requestMatchers(GET, "/citad-transfer/whitelist-categories/**").access(hasPermission("citad-whitelist-categories", BitmaskValue.VIEW))
        .requestMatchers(GET, "/citad-transfer/whitelist-category-histories").access(hasPermission("citad-whitelist-categories", BitmaskValue.VIEW))
        .requestMatchers(GET, "/citad-transfer/whitelist-category-histories/**").access(hasPermission("citad-whitelist-categories", BitmaskValue.VIEW))
        .requestMatchers(GET, "/citad-transfer/whitelist-categories/export").access(hasPermission("citad-whitelist-categories", BitmaskValue.VIEW))
        .requestMatchers(POST, "/citad-transfer/whitelist-categories").access(hasPermission("citad-whitelist-categories", BitmaskValue.INSERT))
        .requestMatchers(PUT, "/citad-transfer/whitelist-categories/**").access(hasPermission("citad-whitelist-categories", BitmaskValue.UPDATE))
        .requestMatchers(DELETE, "/citad-transfer/whitelist-categories/**").access(hasPermission("citad-whitelist-categories", BitmaskValue.DELETE))

        .requestMatchers(new MethodAndQueryParamRequestMatcher(
            "/citad-transfer/transactions", "type", "INWARD", "GET")).access(hasPermission("citad-transactions-inward", BitmaskValue.VIEW))
        .requestMatchers(new MethodAndQueryParamRequestMatcher(
            "/citad-transfer/transactions/export", "type", "INWARD", "GET")).access(hasPermission("citad-transactions-inward", BitmaskValue.VIEW))
        .requestMatchers(POST, "/citad-transfer/transactions/inward/retry").access(hasPermission("citad-transactions-inward", BitmaskValue.RETRY))

        .requestMatchers(new MethodAndQueryParamRequestMatcher(
            "/citad-transfer/transactions", "type", "OUTWARD", "GET")).access(hasPermission("citad-transactions-outward", BitmaskValue.VIEW))
        .requestMatchers(new MethodAndQueryParamRequestMatcher(
            "/citad-transfer/transactions/export", "type", "OUTWARD", "GET")).access(hasPermission("citad-transactions-outward", BitmaskValue.VIEW))
        .requestMatchers(POST, "/citad-transfer/transactions/outward/retry").access(hasPermission("citad-transactions-outward", BitmaskValue.RETRY))

        .requestMatchers(GET, "/citad-transfer/transactions/*").access(hasPermission("citad-transactions-outward", "citad-transactions-inward", BitmaskValue.VIEW))

        .requestMatchers(GET, "/citad-transfer/state-treasuries").access(hasPermission("citad-state-treasuries", BitmaskValue.VIEW))
        .requestMatchers(GET, "/citad-transfer/state-treasuries/**").access(hasPermission("citad-state-treasuries", BitmaskValue.VIEW))
        .requestMatchers(GET, "/citad-transfer/state-treasuries-histories").access(hasPermission("citad-state-treasuries", BitmaskValue.VIEW))
        .requestMatchers(GET, "/citad-transfer/state-treasuries-histories/**").access(hasPermission("citad-state-treasuries", BitmaskValue.VIEW))
        .requestMatchers(GET, "/citad-transfer/state-treasuries/export").access(hasPermission("citad-state-treasuries", BitmaskValue.VIEW))
        .requestMatchers(POST, "/citad-transfer/state-treasuries").access(hasPermission("citad-state-treasuries", BitmaskValue.INSERT))
        .requestMatchers(PUT, "/citad-transfer/state-treasuries/**").access(hasPermission("citad-state-treasuries", BitmaskValue.UPDATE))
        .requestMatchers(DELETE, "/citad-transfer/state-treasuries/**").access(hasPermission("citad-state-treasuries", BitmaskValue.DELETE))

        .requestMatchers(DELETE, "/citad-transfer/configs/**").denyAll()
        .requestMatchers(GET, "/citad-transfer/configs/**").denyAll()
        .requestMatchers(GET, "/citad-transfer/config-histories/**").denyAll()
        .requestMatchers(POST, "/citad-transfer/configs/**").denyAll()
        .requestMatchers(PUT, "/citad-transfer/configs/**").denyAll()
    );
  }

  public void configRoutingTransferRequestMatcher(HttpSecurity http) throws Exception {
    http.authorizeHttpRequests(authorize -> authorize
        .requestMatchers(POST, "/whitelist-accounts/*/reject").access(hasPermission("routing-whitelist", BitmaskValue.APPROVE))
        .requestMatchers(POST, "/whitelist-accounts/*/approve").access(hasPermission("routing-whitelist", BitmaskValue.APPROVE))
        .requestMatchers(GET, "/whitelist-accounts/**").access(hasPermission("routing-whitelist", BitmaskValue.VIEW))
        .requestMatchers(POST, "/whitelist-accounts").access(hasPermission("routing-whitelist", BitmaskValue.INSERT))
        .requestMatchers(PUT, "/whitelist-accounts/**").access(hasPermission("routing-whitelist", BitmaskValue.UPDATE))
        .requestMatchers(GET, "/transfer-channel-configs/**").access(hasPermission("routing-channel-config", BitmaskValue.VIEW))
        .requestMatchers(POST, "/transfer-channel-configs/**").access(hasPermission("routing-channel-config", BitmaskValue.INSERT))
        .requestMatchers(PUT, "/transfer-channel-configs/**").access(hasPermission("routing-channel-config", BitmaskValue.UPDATE))
        .requestMatchers(GET, "/blacklist-accounts/**").access(hasPermission("routing-blacklist", BitmaskValue.VIEW))
        .requestMatchers(POST, "/blacklist-accounts").access(hasPermission("routing-blacklist", BitmaskValue.INSERT))
        .requestMatchers(PUT, "/blacklist-accounts/**").access(hasPermission("routing-blacklist", BitmaskValue.UPDATE))
        .requestMatchers(POST, "/transfer-channel-limits").access(hasPermission("routing-channel-limit", BitmaskValue.INSERT))
        .requestMatchers(PUT, "/transfer-channel-limits/**").access(hasPermission("routing-channel-limit", BitmaskValue.UPDATE))
        .requestMatchers(GET, "/transfer-channel-limits/**").access(hasPermission("routing-channel-limit", BitmaskValue.VIEW))
        .requestMatchers(GET, "/integration-channels/**").access(hasPermission("routing-integration-channel", BitmaskValue.VIEW))
        .requestMatchers(POST, "/integration-channels/**").access(hasPermission("routing-integration-channel", BitmaskValue.INSERT))
        .requestMatchers(PUT, "/integration-channels/**").access(hasPermission("routing-integration-channel", BitmaskValue.UPDATE))
        .requestMatchers(GET, "/transfer-channel-bank-configs/**").access(hasPermission("routing-transfer-channel-bank-config", BitmaskValue.VIEW))
        .requestMatchers(POST, "/transfer-channel-bank-configs").access(hasPermission("routing-transfer-channel-bank-config", BitmaskValue.INSERT))
        .requestMatchers(PUT, "/transfer-channel-bank-configs/**").access(hasPermission("routing-transfer-channel-bank-config", BitmaskValue.UPDATE))
    );
  }

  public void configWireTransferRequestMatcher(HttpSecurity http) throws Exception {
    http.authorizeHttpRequests(authorize -> authorize
        .requestMatchers(GET, "/wire-transfer/error-messages").access(hasPermission("wire-transfer-error-messages", BitmaskValue.VIEW))
        .requestMatchers(GET, "/wire-transfer/error-messages/**").access(hasPermission("wire-transfer-error-messages", BitmaskValue.VIEW))
        .requestMatchers(POST, "/wire-transfer/error-messages").access(hasPermission("wire-transfer-error-messages", BitmaskValue.RETRY))
        .requestMatchers(DELETE, "/wire-transfer/error-messages/**").access(hasPermission("wire-transfer-error-messages", BitmaskValue.DELETE))
    );
  }

  public void configInhouseTransferRequestMatcher(HttpSecurity http) throws Exception {
    http.authorizeHttpRequests(authorize -> authorize
        .requestMatchers(GET, "/inhouse-transfer/configs/transfer.channels.state").access(hasPermission("inhouse-transfer-channel-state", BitmaskValue.VIEW))
        .requestMatchers(PUT, "/inhouse-transfer/configs/transfer.channels.state").access(hasPermission("inhouse-transfer-channel-state", BitmaskValue.UPDATE))
        .requestMatchers(GET, "/inhouse-transfer/config-audits/transfer.channels.state").access(hasPermission("inhouse-transfer-channel-state", BitmaskValue.VIEW))
        .requestMatchers(GET, "/inhouse-transfer/configs/**").denyAll()
        .requestMatchers(PUT, "/inhouse-transfer/configs/**").denyAll()
        .requestMatchers(GET, "/inhouse-transfer/config-audits/**").denyAll()
        .anyRequest().authenticated()
    );
  }

  public void configNapasReconcileRequestMatcher(HttpSecurity http) throws Exception {
    http.authorizeHttpRequests(authorize -> authorize
        .requestMatchers(POST, "/napas/ibft-reconcile/out/dispute/search").access(hasPermission("napas-ibft-reconcile-dispute", BitmaskValue.VIEW))
        .requestMatchers(POST, "/napas/ibft-reconcile/out/dispute/export").access(hasPermission("napas-ibft-reconcile-dispute", BitmaskValue.VIEW))
        .requestMatchers(POST, "/napas/ibft-reconcile/out/dispute/request/create").access(hasPermission("napas-ibft-reconcile-dispute", BitmaskValue.INSERT))
        .requestMatchers(POST, "/napas/ibft-reconcile/out/dispute/request/update").access(hasPermission("napas-ibft-reconcile-dispute", BitmaskValue.UPDATE))
        .requestMatchers(POST, "/napas/ibft-reconcile/out/dispute/approve-batch").access(hasPermission("napas-ibft-reconcile-dispute", BitmaskValue.APPROVE))
        .requestMatchers(POST, "/napas/ibft-reconcile/out/dispute/approve").access(hasPermission("napas-ibft-reconcile-dispute", BitmaskValue.APPROVE))
        .requestMatchers(POST, "/napas/ibft-reconcile/out/dispute/response/approve").access(hasPermission("napas-ibft-reconcile-dispute", BitmaskValue.APPROVE))
        .requestMatchers(POST, "/napas/ibft-reconcile/out/dispute/response/create").access(hasPermission("napas-ibft-reconcile-dispute", BitmaskValue.REPLY))
        .requestMatchers(POST, "/napas/ibft-reconcile/out/dispute/response/update").access(hasPermission("napas-ibft-reconcile-dispute", BitmaskValue.REPLY))
        .requestMatchers(POST, "/napas/ibft-reconcile/out/file/search/**").access(hasPermission("napas-ibft-reconcile-flag-report", BitmaskValue.VIEW))
        .requestMatchers(GET, "/napas/ibft-reconcile/out/file/download/**").access(hasPermission("napas-ibft-reconcile-flag-report", BitmaskValue.VIEW))
        .requestMatchers(GET, "/napas/ibft-reconcile/out/file/**").access(hasPermission("napas-ibft-reconcile-dispute", BitmaskValue.VIEW))
        .requestMatchers(GET, "/napas/ibft-reconcile/config/transaction_flag").access(hasPermission("napas-ibft-reconcile-transaction-flag", BitmaskValue.VIEW))
        .requestMatchers(POST, "/napas/ibft-reconcile/config/history").access(hasPermission("napas-ibft-reconcile-transaction-flag", BitmaskValue.VIEW))
        .requestMatchers(PUT, "/napas/ibft-reconcile/config/transaction_flag").access(hasPermission("napas-ibft-reconcile-transaction-flag", BitmaskValue.UPDATE))
        .requestMatchers(POST, "/napas/ibft-reconcile/out/transaction").access(hasPermission("napas-ibft-reconcile-transaction", BitmaskValue.VIEW))
    );
  }
}
