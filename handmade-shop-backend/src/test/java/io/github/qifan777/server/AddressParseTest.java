package io.github.qifan777.server;

import io.github.qifan777.server.address.controller.AddressForFrontController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
public class AddressParseTest {
    public static void main(String[] args) {
        Map<String, String> params = Map.of("key", "ZWNBZ-2ARW3-J6H3Q-3SUEF-BILFZ-TLFPW", "address", "福建省福州市鼓楼区乌山路96号");

        AddressForFrontController.GeoCoderResponse forObject = new RestTemplate()
                .getForObject("https://apis.map.qq.com/ws/geocoder/v1/?address=福建省福州市鼓楼区乌山路96号&key=ZWNBZ-2ARW3-J6H3Q-3SUEF-BILFZ-TLFPW",
                        AddressForFrontController.GeoCoderResponse.class);
        System.out.println("123");
    }
}
