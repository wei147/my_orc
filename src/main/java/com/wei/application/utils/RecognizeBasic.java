package com.wei.application.utils;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.auth.credentials.Credential;
import com.aliyun.auth.credentials.provider.StaticCredentialProvider;
import com.aliyun.sdk.service.ocr_api20210707.AsyncClient;
import com.aliyun.sdk.service.ocr_api20210707.models.RecognizeBasicRequest;
import com.aliyun.sdk.service.ocr_api20210707.models.RecognizeBasicResponse;
import com.wei.application.config.PrivateConfig;
import darabonba.core.client.ClientOverrideConfiguration;

import javax.annotation.Resource;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class RecognizeBasic {
    @Resource
    PrivateConfig privateConfig;

    public String GetRecognizeBasic(String url) throws Exception {

        // HttpClient Configuration
        /*HttpClient httpClient = new ApacheAsyncHttpClientBuilder()
                .connectionTimeout(Duration.ofSeconds(10)) // Set the connection timeout time, the default is 10 seconds
                .responseTimeout(Duration.ofSeconds(10)) // Set the response timeout time, the default is 20 seconds
                .maxConnections(128) // Set the connection pool size
                .maxIdleTimeOut(Duration.ofSeconds(50)) // Set the connection pool timeout, the default is 30 seconds
                // Configure the proxy
                .proxy(new ProxyOptions(ProxyOptions.Type.HTTP, new InetSocketAddress("<your-proxy-hostname>", 9001))
                        .setCredentials("<your-proxy-username>", "<your-proxy-password>"))
                // If it is an https connection, you need to configure the certificate, or ignore the certificate(.ignoreSSL(true))
                .x509TrustManagers(new X509TrustManager[]{})
                .keyManagers(new KeyManager[]{})
                .ignoreSSL(false)
                .build();*/

        AsyncClient client = null;
        // Configure Credentials authentication information, including ak, secret, token
        try {
            StaticCredentialProvider provider = StaticCredentialProvider.create(Credential.builder()
                    .accessKeyId(privateConfig.getAccessKeyId())
                    .accessKeySecret(privateConfig.getAccessKeySecret())
                    //.securityToken("<your-token>") // use STS token
                    .build());

            // Configure the Client
            client = AsyncClient.builder()
                    .region("cn-hangzhou") // Region ID
                    //.httpClient(httpClient) // Use the configured HttpClient, otherwise use the default HttpClient (Apache HttpClient)
                    .credentialsProvider(provider)
                    //.serviceConfiguration(Configuration.create()) // Service-level configuration
                    // Client-level configuration rewrite, can set Endpoint, Http request parameters, etc.
                    .overrideConfiguration(
                            ClientOverrideConfiguration.create()
                                    .setEndpointOverride("ocr-api.cn-hangzhou.aliyuncs.com")
                            //.setConnectTimeout(Duration.ofSeconds(30))
                    )
                    .build();

            // Parameter settings for API request
            RecognizeBasicRequest recognizeBasicRequest = RecognizeBasicRequest.builder()
                    .url(url)
                    // Request-level configuration rewrite, can set Http request parameters, etc.
                    // .requestConfiguration(RequestConfiguration.create().setHttpHeaders(new HttpHeaders()))
                    .build();

            // Asynchronously get the return value of the API request
            CompletableFuture<RecognizeBasicResponse> response = client.recognizeBasic(recognizeBasicRequest);
            // Synchronously get the return value of the API request
            RecognizeBasicResponse resp = response.get();
//        System.out.println(new Gson().toJson(resp));
            // 将对象序列化为Json字符串
            String jsonString = new JSONObject().toJSONString(resp.getBody());
//        System.out.println(jsonString);
            JSONObject jsonObject = JSONObject.parseObject(jsonString);
            System.out.println("{\"content\":\""+jsonObject.getJSONObject("data").get("content")+"\"}");
            return "{\"content\":\""+jsonObject.getJSONObject("data").get("content")+"\"}";
            // Asynchronous processing of return values
        /*response.thenAccept(resp -> {
            System.out.println(new Gson().toJson(resp));
        }).exceptionally(throwable -> { // Handling exceptions
            System.out.println(throwable.getMessage());
            return null;
        });*/
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Finally, close the client
            assert client != null;
            client.close();
        }
        return "解析失败";
    }

}
