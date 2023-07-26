package com.arlong.tool;

import cn.hutool.core.util.NumberUtil;

/**
 * @description:
 * @author: zzl
 * @date: 2023/7/26 14:04
 **/
public class hutool {
    public static void main(String[] args) {
        // 计算百分数
        String percentage = NumberUtil.decimalFormat("#.##%", NumberUtil.div(100, 1000));
        System.err.println(percentage);
    }
}
