package com.example.lotterysystem.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.regex.Pattern;

/*正则表达式，用来校验
* */

@Slf4j
public class RegexUtil {
    /**
     *
     邮箱：xxx@xx.xxx(
     形如：abc@qq.com)
     *
     * @param content
     * @return
     */
    public static boolean checkMail(String content) {
        if (!StringUtils.hasText(content)) {
            return false;
        }
        /**
         * ^表⽰匹配字符串的开始。

         * [a-z0-9]+表⽰匹配⼀个或多个⼩写字⺟或数字。

         * ([._\\-]*[a-z0-9])*表⽰匹配零次或多次下述模式：⼀个点、下划线、反斜杠或短横线，后⾯跟着⼀个或多个⼩写字⺟或数字。这部分是可选的，并且可以重复出现。

         * @字符字⾯量，表⽰电⼦邮件地址中必须包含的"@"符号。

         * ([a-z0-9]+[-a-z0-9]*[a-z0-9]+.)
         表⽰匹配⼀个或多个⼩写字⺟或数字，后⾯可以跟着零个或多个短横线或⼩写字⺟和数字，然后是⼀个⼩写字⺟或数字，最后是⼀个点。这是匹配域名的⼀部分。

         * {1,63}
         表⽰前⾯的模式重复1到63次，这是对顶级域名⻓度的限制。

         * [a-z0-9]+表⽰匹配⼀个或多个⼩写字⺟或数字，这是顶级域名的开始部分。

         * $表⽰匹配字符串的结束。

         */
        String regex = "^[a-z0-9]+([._\\\\-]*[a-z0-9])*@([a-z0-9]+[-a-z0-9]*[a-z0-9]+.){1,63}[a-z0-9]+$";
        return Pattern.matches(regex, content);
    }
    /**
     *
     ⼿机号码以1开头的11位数字

     *
     * @param content
     * @return
     */
    public static boolean checkMobile(String content) {
        //log.info("content:{}",content);
        //log.info("StringUtils.hasText(content):{}",StringUtils.hasText(content));
        if (!StringUtils.hasText(content)) {

            return false;
        }
        /**
         * ^表⽰匹配字符串的开始。

         * 1表⽰⼿机号码以数字1开头。

         * [3|4|5|6|7|8|9]表⽰接下来的数字是3到9之间的任意⼀个数字。这是中国⼤陆⼿机号码的第⼆位数字，通常⽤来区分不同的运营商。

         * [0-9]{9}表⽰后⾯跟着9个0到9之间的任意数字，这代表⼿机号码的剩余部分。

         * $表⽰匹配字符串的结束。

         */
        String regex = "^1[3|4|5|6|7|8|9][0-9]{9}$";
       // log.info("regex:{}",regex);
       // log.info("Pattern.matches(regex, content):{}",Pattern.matches(regex, content));
        return Pattern.matches(regex, content);
    }
    /**
     *
     密码强度正则，6到12位

     *
     * @param content
     * @return
     */
    public static boolean checkPassword(String content){
        if (!StringUtils.hasText(content)) {
            return false;
        }
        /**
         * ^表⽰匹配字符串的开始。

         * [0-9A-Za-z]表⽰匹配的字符可以是：

         * 0-9：任意⼀个数字（0到9）。

         * A-Z：任意⼀个⼤写字⺟（从A到Z）。

         * a-z：任意⼀个⼩写字⺟（从a到z）。

         * {6,12}表⽰前⾯的字符集合（数字、⼤写字⺟和⼩写字⺟）可以重复出现6到12次。

         * $表⽰匹配字符串的结束。

         */
        String regex= "^[0-9A-Za-z]{6,12}$";
        return Pattern.matches(regex, content);
    }

}
