package com.gohb.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class WxMsgModel {


    @JsonProperty(value = "touser")
    private String toUser;

    @JsonProperty(value = "template_id")
    private String templateId;

    @JsonProperty(value = "url")
    private String url;

    @JsonProperty(value = "topcolor")
    private String topColor;

    @JsonProperty(value = "data")
    private Map<String, Map<String, String>> data;

    /**
     * 专门构建参数的
     *
     * @param value
     * @param color
     * @return
     */
    public static Map<String, String> buildMap(String value, String color) {
        HashMap<String, String> map = new HashMap<>();
        map.put("value", value);
        map.put("color", color);
        return map;
    }


}
