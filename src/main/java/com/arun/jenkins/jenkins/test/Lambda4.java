package com.arun.jenkins.jenkins.test;

import org.springframework.core.convert.converter.Converter;

public class Lambda4 {
    static int outerStaticNum;
    int outerNum;
    void testScopes() {
        Converter<Integer, String> stringConverter1 = (from) ->
        {
            outerNum = 23;
            return String.valueOf(from + outerNum);
        };
        Converter<Integer, String> stringConverter2 = (from) ->
        {
            outerStaticNum = 72;
            return String.valueOf(from);
        };
    }
}
