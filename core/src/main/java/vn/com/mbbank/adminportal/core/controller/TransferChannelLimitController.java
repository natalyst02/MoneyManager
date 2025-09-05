package vn.com.mbbank.adminportal.core.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import vn.com.mbbank.adminportal.common.model.response.PageImpl;
import vn.com.mbbank.adminportal.common.model.response.Response;
import vn.com.mbbank.adminportal.common.util.Pageables;
import vn.com.mbbank.adminportal.core.model.filter.TransferChannelLimitFilter;
import vn.com.mbbank.adminportal.core.model.filter.TransferChannelLimitHistoryFilter;
import vn.com.mbbank.adminportal.core.model.request.CreateTransferChannelLimitRequest;
import vn.com.mbbank.adminportal.core.model.request.UpdateTransferChannelLimitRequest;
import vn.com.mbbank.adminportal.core.model.response.*;
import vn.com.mbbank.adminportal.core.service.TransferChannelLimitService;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/transfer-channel-limits")
@RequiredArgsConstructor
@Validated
public class TransferChannelLimitController {
    private final TransferChannelLimitService transferChannelLimitService;

    @GetMapping
    public Response<List<TransferChannelLimitResp>> getTransferChannelLimits(@Valid TransferChannelLimitFilter request,
                                                                @RequestParam(required = false, defaultValue = "updatedAt:DESC") String sort) {
        return Response.ofSucceeded(transferChannelLimitService.getList(request, sort));
    }

    @GetMapping("/{id}")
    public Response<TransferChannelLimitResp> getTransferChannelLimit(@PathVariable @Positive Long id) {
        return Response.ofSucceeded(transferChannelLimitService.getDetail(id));
    }

    @PostMapping
    public CompletableFuture<Response<TransferChannelLimitResp>> create(Authentication authentication, @RequestBody @Valid CreateTransferChannelLimitRequest request) {
        return transferChannelLimitService.create(authentication, request).thenApply(Response::ofSucceeded);
    }

    @PutMapping("/{id}")
    public CompletableFuture<Response<TransferChannelLimitResp>> update(Authentication authentication,
                                                                        @PathVariable @Positive Long id,
                                                                        @RequestBody @Valid UpdateTransferChannelLimitRequest request) {
        return transferChannelLimitService.update(authentication, id, request).thenApply(Response::ofSucceeded);
    }

    @GetMapping("/{transferChannelId}/history")
    public Response<PageImpl<TransferChannelLimitHistoryResp>> getTransferChannelLimitsHistory(@PathVariable @Positive Long transferChannelId,
                                                                                 @Valid TransferChannelLimitHistoryFilter filter,
                                                                                 @RequestParam int page, @RequestParam int size,
                                                                                 @RequestParam(required = false, defaultValue = "id:ASC") String sort) {
        return Response.ofSucceeded(transferChannelLimitService.getListHistory(transferChannelId, filter, Pageables.of(page, size, sort)));
    }

    @GetMapping("/history/{historyId}")
    public Response<TransferChannelLimitHistoryResp> getTransferChannelLimitHistory(@PathVariable @Positive Long historyId) {
        return Response.ofSucceeded(transferChannelLimitService.getDetailHistory(historyId));
    }
}
