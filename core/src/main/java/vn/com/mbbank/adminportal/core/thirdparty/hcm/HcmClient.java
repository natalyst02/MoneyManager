package vn.com.mbbank.adminportal.core.thirdparty.hcm;

import vn.com.mbbank.adminportal.core.thirdparty.hcm.model.GetHcmUserInfoResponse;

import java.util.concurrent.CompletableFuture;

public interface HcmClient {
    CompletableFuture<GetHcmUserInfoResponse> getUser(String username);
}
