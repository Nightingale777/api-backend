package com.api.sdk.client;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.api.sdk.model.User;


import java.util.HashMap;
import java.util.Map;

import static com.api.sdk.utils.SignUtils.genSign;

/**
 * 调用第三方接口的客户端
 */
public class ApiClient {

    private static final String GATEWAY_HOST = "http://localhost:8090";

    private final String accessKey;

    private final String secretKey;

    public ApiClient(String accessKey, String secretKey) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }

    public String getNameByGet(String name) {
        //可以单独传入http参数，这样参数会自动做URL编码，拼接在URL中
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("name", name);
        String result = HttpUtil.get(GATEWAY_HOST + "/api/name/", paramMap);
        System.out.println("getNameByGet: " + result);
        return result;
    }


//    public String getNameByPost(String name) {
//        //可以单独传入http参数，这样参数会自动做URL编码，拼接在URL中
//        HashMap<String, Object> paramMap = new HashMap<>();
//        paramMap.put("name", name);
//        String result = HttpUtil.post(GATEWAY_HOST + "/api/name/", paramMap);
//        System.out.println("getNameByPost: " + result);
//        return result;
//    }

    private Map<String, String> getHeaderMap(String body) {
        Map<String, String> hashMap = new HashMap<>();
        hashMap.put("accessKey", accessKey);
        // 一定不能直接发送, 要通过加密转成sign
//        hashMap.put("secretKey", secretKey);
        hashMap.put("nonce", RandomUtil.randomNumbers(4));
        hashMap.put("body", body);
        hashMap.put("timestamp", String.valueOf(System.currentTimeMillis() / 1000));
        hashMap.put("sign", genSign(body, secretKey));
        return hashMap;
    }

    public String getUsernameByPost(User user) {
        String json = JSONUtil.toJsonStr(user); // {"username":"liapi"}
//        System.out.println("JSONUtil.toJsonStr(user): " + json);
        HttpResponse httpResponse = HttpRequest.post(GATEWAY_HOST + "/api/name/user")
                // 把accessKey放入请求头, secretKey经过加密
                .addHeaders(getHeaderMap(json))
                .body(json)
                .execute();
        System.out.println("getUsernameByPost " + httpResponse.getStatus());
        String result = httpResponse.body();
        System.out.println("getUsernameByPost " + result);
        return result;
    }
}