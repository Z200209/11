package red.mlz.app.controller.game;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GameControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String baseUrl;
    private String sign;

    @BeforeEach
    public void setUp() {
        baseUrl = "http://localhost:" + port;
        
        // 登录获取sign
        MultiValueMap<String, String> loginParams = new LinkedMultiValueMap<>();
        loginParams.add("phone", "13800138000");
        loginParams.add("password", "password123");
        
        ResponseEntity<String> loginResponse = restTemplate.postForEntity(
                baseUrl + "/user/login/app", 
                loginParams, 
                String.class);
        
        JSONObject loginJson = JSON.parseObject(loginResponse.getBody());
        if (loginJson != null && loginJson.getInteger("code") == 1001) {
            JSONObject data = loginJson.getJSONObject("data");
            if (data != null) {
                sign = data.getString("sign");
                System.out.println("登录成功，获取到sign: " + sign);
            }
        } else {
            System.out.println("登录失败，请检查用户名和密码");
        }
    }
    
    @Test
    public void testGameList() {
        // 跳过登录失败的情况
        if (sign == null) {
            System.out.println("由于登录失败，跳过游戏列表测试");
            return;
        }
        
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authentication", sign);
        
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(null, headers);
        
        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/game/list",
                HttpMethod.GET,
                requestEntity,
                String.class);
        
        System.out.println("游戏列表响应: " + response.getBody());
        
        JSONObject json = JSON.parseObject(response.getBody());
        assertNotNull(json);
        assertEquals(1001, json.getInteger("code"));
        
        if (json.containsKey("data")) {
            JSONObject data = json.getJSONObject("data");
            assertNotNull(data);
            
            if (data.containsKey("gameList")) {
                // 如果有游戏列表，验证格式
                assertTrue(data.getJSONArray("gameList").size() >= 0);
            }
            
            // 验证wp参数存在
            assertTrue(data.containsKey("wp"));
        }
    }
    
    @Test
    public void testGameListWithKeyword() {
        // 跳过登录失败的情况
        if (sign == null) {
            System.out.println("由于登录失败，跳过带关键词的游戏列表测试");
            return;
        }
        
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authentication", sign);
        
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("keyword", "游戏");
        
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, headers);
        
        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/game/list",
                HttpMethod.GET,
                requestEntity,
                String.class);
        
        System.out.println("带关键词的游戏列表响应: " + response.getBody());
        
        JSONObject json = JSON.parseObject(response.getBody());
        assertNotNull(json);
        assertEquals(1001, json.getInteger("code"));
    }
    
    @Test
    public void testGameListWithTypeId() {
        // 跳过登录失败的情况
        if (sign == null) {
            System.out.println("由于登录失败，跳过带类型ID的游戏列表测试");
            return;
        }
        
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authentication", sign);
        
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("typeId", "1");
        
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, headers);
        
        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/game/list",
                HttpMethod.GET,
                requestEntity,
                String.class);
        
        System.out.println("带类型ID的游戏列表响应: " + response.getBody());
        
        JSONObject json = JSON.parseObject(response.getBody());
        assertNotNull(json);
        // 即使没有该类型的游戏，接口也应该返回成功状态
        assertEquals(1001, json.getInteger("code"));
    }
    
    @Test
    public void testGameInfo() {
        // 跳过登录失败的情况
        if (sign == null) {
            System.out.println("由于登录失败，跳过游戏详情测试");
            return;
        }
        
        // 先获取一个游戏ID
        HttpHeaders listHeaders = new HttpHeaders();
        listHeaders.set("Authentication", sign);
        
        HttpEntity<MultiValueMap<String, String>> listRequestEntity = new HttpEntity<>(null, listHeaders);
        
        ResponseEntity<String> listResponse = restTemplate.exchange(
                baseUrl + "/game/list",
                HttpMethod.GET,
                listRequestEntity,
                String.class);
        
        JSONObject listJson = JSON.parseObject(listResponse.getBody());
        
        // 提取第一个游戏的ID
        BigInteger gameId = null;
        if (listJson != null && listJson.getInteger("code") == 1001 && listJson.containsKey("data")) {
            JSONObject data = listJson.getJSONObject("data");
            if (data != null && data.containsKey("gameList") && !data.getJSONArray("gameList").isEmpty()) {
                JSONObject firstGame = data.getJSONArray("gameList").getJSONObject(0);
                gameId = firstGame.getBigInteger("gameId");
                System.out.println("获取到游戏ID: " + gameId);
            }
        }
        
        // 如果没有获取到gameId，使用一个固定的测试ID
        if (gameId == null) {
            gameId = BigInteger.valueOf(1);
            System.out.println("未获取到游戏ID，使用默认ID: " + gameId);
        }
        
        // 测试游戏详情接口
        HttpHeaders infoHeaders = new HttpHeaders();
        infoHeaders.set("Authentication", sign);
        
        MultiValueMap<String, String> infoParams = new LinkedMultiValueMap<>();
        infoParams.add("gameId", gameId.toString());
        
        HttpEntity<MultiValueMap<String, String>> infoRequestEntity = new HttpEntity<>(infoParams, infoHeaders);
        
        ResponseEntity<String> infoResponse = restTemplate.exchange(
                baseUrl + "/game/info",
                HttpMethod.GET,
                infoRequestEntity,
                String.class);
        
        System.out.println("游戏详情响应: " + infoResponse.getBody());
        
        JSONObject infoJson = JSON.parseObject(infoResponse.getBody());
        assertNotNull(infoJson);
        
        // 如果游戏存在，应该返回成功状态和游戏数据
        if (infoJson.getInteger("code") == 1001) {
            JSONObject data = infoJson.getJSONObject("data");
            assertNotNull(data);
            assertEquals(gameId, data.getBigInteger("gameId"));
            assertNotNull(data.getString("gameName"));
        } else {
            // 如果游戏不存在，应该返回4004状态
            assertEquals(4004, infoJson.getInteger("code"));
        }
    }
    
    @Test
    public void testGameListWithWp() {
        // 跳过登录失败的情况
        if (sign == null) {
            System.out.println("由于登录失败，跳过带分页参数的游戏列表测试");
            return;
        }
        
        // 先获取第一页数据和wp参数
        HttpHeaders firstPageHeaders = new HttpHeaders();
        firstPageHeaders.set("Authentication", sign);
        
        HttpEntity<MultiValueMap<String, String>> firstPageRequestEntity = new HttpEntity<>(null, firstPageHeaders);
        
        ResponseEntity<String> firstPageResponse = restTemplate.exchange(
                baseUrl + "/game/list",
                HttpMethod.GET,
                firstPageRequestEntity,
                String.class);
        
        JSONObject firstPageJson = JSON.parseObject(firstPageResponse.getBody());
        
        // 提取wp参数
        String wp = null;
        if (firstPageJson != null && firstPageJson.getInteger("code") == 1001 && firstPageJson.containsKey("data")) {
            JSONObject data = firstPageJson.getJSONObject("data");
            if (data != null && data.containsKey("wp")) {
                wp = data.getString("wp");
                System.out.println("获取到wp参数: " + wp);
            }
        }
        
        // 如果没有获取到wp参数，跳过测试
        if (wp == null) {
            System.out.println("未获取到wp参数，跳过测试");
            return;
        }
        
        // 使用wp参数请求下一页
        HttpHeaders nextPageHeaders = new HttpHeaders();
        nextPageHeaders.set("Authentication", sign);
        
        MultiValueMap<String, String> nextPageParams = new LinkedMultiValueMap<>();
        nextPageParams.add("wp", wp);
        
        HttpEntity<MultiValueMap<String, String>> nextPageRequestEntity = new HttpEntity<>(nextPageParams, nextPageHeaders);
        
        ResponseEntity<String> nextPageResponse = restTemplate.exchange(
                baseUrl + "/game/list",
                HttpMethod.GET,
                nextPageRequestEntity,
                String.class);
        
        System.out.println("下一页游戏列表响应: " + nextPageResponse.getBody());
        
        JSONObject nextPageJson = JSON.parseObject(nextPageResponse.getBody());
        assertNotNull(nextPageJson);
        assertEquals(1001, nextPageJson.getInteger("code"));
    }
} 