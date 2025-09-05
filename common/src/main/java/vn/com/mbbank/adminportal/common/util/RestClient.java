package vn.com.mbbank.adminportal.common.util;

import com.dslplatform.json.JsonReader;
import com.dslplatform.json.JsonWriter;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.ThreadContext;
import vn.com.mbbank.adminportal.common.exception.PaymentPlatformException;
import vn.com.mbbank.adminportal.common.exception.RestClientException;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Getter
@Log4j2
public class RestClient {
  private static final long DEFAULT_TIMEOUT_MILLIS = 30_000;
  private final HttpClient httpClient;
  private final long timeoutMillis;

  public RestClient(HttpClient httpClient) {
    this(httpClient, DEFAULT_TIMEOUT_MILLIS);
  }

  public RestClient(HttpClient httpClient, long timeoutMillis) {
    this.httpClient = httpClient;
    this.timeoutMillis = timeoutMillis;
  }

  public <R> CompletableFuture<R> getForObject(URI uri, JsonReader.ReadObject<R> reader) {
    return getForObject(uri, null, reader);
  }

  /**
   * Call API for GET method * Handle if have errors, (ex: 400, 500, ...) * * @param reader decoder body response * @param uri destination url * @param headers headers * @param <R> type response data * @return CompletableFuture<R>
   */
  public <R> CompletableFuture<R> getForObject(URI uri, Map<String, String> headers, JsonReader.ReadObject<R> reader) {
    return get(uri, headers).thenApply(response -> handleResponse(uri, reader, response));
  }

  public CompletableFuture<HttpResponse<byte[]>> get(URI uri) {
    return get(uri, null);
  }

  /**
   * Call API for GET method * * @param uri destination url * @param headers headers * @return CompletableFuture<HttpResponse < byte [ ]>>
   */
  public CompletableFuture<HttpResponse<byte[]>> get(URI uri, Map<String, String> headers) {
    var httpRequest = newHttpRequestBuilder(uri, headers).GET().build();
    log.info(() -> "GET " + uri);
    return sendAsync(httpRequest);
  }

  public <T, R> CompletableFuture<R> putForObject(URI uri, T request, JsonWriter.WriteObject<T> writer, JsonReader.ReadObject<R> reader) {
    return putForObject(uri, null, request, writer, reader);
  }

  /**
   * Call API for PUT method * Handle if have errors, (ex: 400, 500, ...) * * @param request Object Body * @param writer encoder body request * @param reader decoder body response * @param uri destination url * @param headers headers * @param <R> type response data * @return CompletableFuture<R>
   */
  public <T, R> CompletableFuture<R> putForObject(URI uri, Map<String, String> headers, T request, JsonWriter.WriteObject<T> writer, JsonReader.ReadObject<R> reader) {
    return put(uri, headers, request, writer).thenApply(response -> handleResponse(uri, reader, response));
  }

  public <T> CompletableFuture<HttpResponse<byte[]>> put(URI uri, T request, JsonWriter.WriteObject<T> writer) {
    return put(uri, null, request, writer);
  }

  public <T> CompletableFuture<HttpResponse<byte[]>> put(URI uri, Map<String, String> headers) {
    return put(uri, headers, null, null);
  }

  /**
   * Call API for PUT method * * @param request Object Body * @param writer encoder body request * @param uri destination url * @param headers headers * @param <T> class request body * @return CompletableFuture<HttpResponse < byte [ ]>>
   */
  public <T> CompletableFuture<HttpResponse<byte[]>> put(URI uri, Map<String, String> headers, T request, JsonWriter.WriteObject<T> writer) {
    byte[] body;
    HttpRequest.BodyPublisher bodyPublisher;
    if (request != null) {
      body = Json.encode(request, writer);
      bodyPublisher = HttpRequest.BodyPublishers.ofByteArray(body);
    } else {
      body = null;
      bodyPublisher = HttpRequest.BodyPublishers.noBody();
    }
    var httpRequest = newHttpRequestBuilder(uri, headers).PUT(bodyPublisher).build();
    log.info(() -> "PUT " + uri + ", request body: " + ((body != null) ? new String(body, StandardCharsets.UTF_8) : "null"));
    return sendAsync(httpRequest);
  }

  public <T, R> CompletableFuture<R> postForObject(URI uri, T request, JsonWriter.WriteObject<T> writer, JsonReader.ReadObject<R> reader) {
    return putForObject(uri, null, request, writer, reader);
  }

  /**
   * Call API for POST method * Handle if this call has errors, (ex: 400, 500, ...) * * @param request Object Body * @param writer encoder body request * @param reader decoder body response * @param uri destination url * @param headers headers * @param <R> type response data * @return CompletableFuture<R>
   */
  public <T, R> CompletableFuture<R> postForObject(URI uri, Map<String, String> headers, T request, JsonWriter.WriteObject<T> writer, JsonReader.ReadObject<R> reader) {
    return post(uri, headers, request, writer).thenApply(response -> handleResponse(uri, reader, response));
  }

  public <R> CompletableFuture<R> postForObject(URI uri, Map<String, String> headers, JsonReader.ReadObject<R> reader) {
    return post(uri, headers).thenApply(response -> handleResponse(uri, reader, response));
  }

  public <T> CompletableFuture<HttpResponse<byte[]>> post(URI uri, T request, JsonWriter.WriteObject<T> writer) {
    return post(uri, null, request, writer);
  }

  /**
   * Call API for POST method * * @param request Object Body * @param writer encoder body request * @param uri destination url * @param headers headers * @param <T> class request body * @return CompletableFuture<R>
   */
  public <T> CompletableFuture<HttpResponse<byte[]>> post(URI uri, Map<String, String> headers, T request, JsonWriter.WriteObject<T> writer) {
    var body = Json.encode(request, writer);
    var httpRequest = newHttpRequestBuilder(uri, headers).POST(HttpRequest.BodyPublishers.ofByteArray(body)).build();
    log.info(() -> "POST " + uri + ", request body: " + new String(body, StandardCharsets.UTF_8));
    return sendAsync(httpRequest);
  }

  public CompletableFuture<HttpResponse<byte[]>> post(URI uri, byte[] body) {
    return post(uri, null, body);
  }

  /**
   * Call API for POST method * * @param body Object Body * @param uri destination url * @param headers headers * @return CompletableFuture<R>
   */
  public CompletableFuture<HttpResponse<byte[]>> post(URI uri, Map<String, String> headers, byte[] body) {
    var bodyPublisher = (body != null) ? HttpRequest.BodyPublishers.ofByteArray(body) : HttpRequest.BodyPublishers.noBody();
    var httpRequest = newHttpRequestBuilder(uri, headers).POST(bodyPublisher).build();
    log.info(() -> "POST " + uri + ", request body: " + (body != null ? new String(body, StandardCharsets.UTF_8) : "null"));
    return sendAsync(httpRequest);
  }

  public CompletableFuture<HttpResponse<byte[]>> post(URI uri, Object body) {
    return post(uri, null, body);
  }

  public CompletableFuture<HttpResponse<byte[]>> post(URI uri, Map<String, String> headers) {
    return post(uri, headers, (byte[]) null);
  }

  public CompletableFuture<HttpResponse<byte[]>> post(URI uri, Map<String, String> headers, Object body) {
    try {
      var httpRequest = newHttpRequestBuilder(uri, headers).POST(HttpRequest.BodyPublishers.ofByteArray(Json.encode(body))).build();
      log.info("POST {}, request body: {}", uri, body);
      return sendAsync(httpRequest);
    } catch (PaymentPlatformException e) {
      log.error("can't deserialize body to byte[]", e);
      var exFut = new CompletableFuture<HttpResponse<byte[]>>();
      exFut.completeExceptionally(CompletableFutures.toCompletionException(e));
      return exFut;
    }
  }

  /**
   * Handle response * throw Exception when * * @param reader * @param uri * @param response * @param <R> * @return
   */
  private <R> R handleResponse(URI uri, JsonReader.ReadObject<R> reader, HttpResponse<byte[]> response) {
    if (response.statusCode() == 200) {
      return Json.decode(response.body(), reader);
    } else if (response.statusCode() >= 400 && response.statusCode() <= 500) {
      throw CompletableFutures.toCompletionException(new RestClientException(response.statusCode(), response.body()));
    } else {
      var body = new String(response.body(), StandardCharsets.UTF_8);
      log.error("Send result to {} got error, because: {}", uri, body);
      throw CompletableFutures.toCompletionException(new PaymentPlatformException(CommonErrorCode.INTERNAL_SERVER_ERROR, body));
    }
  }

  public CompletableFuture<HttpResponse<byte[]>> delete(URI uri) {
    return delete(uri, null);
  }

  /**
   * Handle delete response * * @param uri String * @param headers Map<String, String> * @return CompletableFuture<HttpResponse < byte [ ]>>
   */
  public CompletableFuture<HttpResponse<byte[]>> delete(URI uri, Map<String, String> headers) {
    var httpRequest = newHttpRequestBuilder(uri, headers).DELETE().build();
    log.info("DELETE {}", uri);
    return sendAsync(httpRequest);
  }

  public <T> CompletableFuture<HttpResponse<byte[]>> delete(URI uri, Map<String, String> headers, T request, JsonWriter.WriteObject<T> writer) {
    var body = Json.encode(request, writer);
    var httpRequest = newHttpRequestBuilder(uri, headers).method("DELETE", HttpRequest.BodyPublishers.ofByteArray(body)).build();
    log.info(() -> "DELETE " + uri + ", request body: " + new String(body, StandardCharsets.UTF_8));
    return sendAsync(httpRequest);
  }

  /**
   * Add header into request * * @param uri * @param headers * @return
   */
  private HttpRequest.Builder newHttpRequestBuilder(URI uri, Map<String, String> headers) {
    var requestBuilder = HttpRequest.newBuilder().uri(uri);
    if (headers != null) {
      headers.forEach(requestBuilder::header);
    }
    return requestBuilder;
  }

  private CompletableFuture<HttpResponse<byte[]>> sendAsync(HttpRequest request) {
    return sendAsync(request, true);
  }

  public CompletableFuture<HttpResponse<byte[]>> sendAsync(HttpRequest request, boolean logResp) {
    var context = ThreadContext.getImmutableContext();
    var result = new CompletableFuture<HttpResponse<byte[]>>();
    result.whenComplete((v, e) -> ThreadContext.clearMap());
    httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofByteArray())
        .orTimeout(timeoutMillis, TimeUnit.MILLISECONDS)
        .whenComplete((v, e) -> {
          ThreadContext.clearMap();
          ThreadContext.putAll(context);
          if (e != null) {
            e = CompletableFutures.unwrapException(e);
            result.completeExceptionally(e);
          } else {
            if (logResp) {
              log.info(() -> request.method() + " " + request.uri() + ", response body: " + new String(v.body(), StandardCharsets.UTF_8));
            }
            result.complete(v);
          }
        });
    return result;
  }
}