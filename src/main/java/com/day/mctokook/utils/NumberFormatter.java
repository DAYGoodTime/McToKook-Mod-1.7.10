package com.day.mctokook.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class NumberFormatter {

    public static String format(long number, String type, int scale) {
        String result;
        if (number > 1024 * 1024) {
            if (number > 1024 * 1024 * 1000) {
                // 转换成G单位
                BigDecimal num = BigDecimal.valueOf(number)
                    .divide(BigDecimal.valueOf(1024 * 1024 * 1024), RoundingMode.CEILING);
                result = num.setScale(scale, RoundingMode.CEILING)
                    .toPlainString();
                result = result + ("byte".equals(type) ? "GB" : "G");
            } else {
                // 转换成M单位
                BigDecimal num = BigDecimal.valueOf(number)
                    .divide(BigDecimal.valueOf(1024 * 1024), RoundingMode.CEILING);
                result = num.setScale(scale, RoundingMode.CEILING)
                    .toPlainString();
                result = result + ("byte".equals(type) ? "MB" : "M");
            }
        } else {
            // 转换成K单位
            if (number > 1024) {
                BigDecimal num = BigDecimal.valueOf(number)
                    .divide(BigDecimal.valueOf(1024), RoundingMode.CEILING);
                result = num.setScale(scale, RoundingMode.CEILING)
                    .toPlainString();
                result = result + ("byte".equals(type) ? "KB" : "K");
            } else {
                // 不进行转换
                result = String.valueOf(number);
            }
        }
        return result;
    }

}
