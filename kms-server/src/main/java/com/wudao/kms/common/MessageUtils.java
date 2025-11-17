package com.wudao.kms.common;

import cn.hutool.extra.spring.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;

/**
 * 获取i18n资源文件
 *
 * @author soho
 */
@Slf4j
public class MessageUtils
{
 
	 /**
     * 根据消息键和参数 获取消息 委托给spring messageSource
     *
     * @param code 消息键
     * @param args 参数
     * @return 获取国际化翻译值
     */
    public static String message(String code, Object... args)
    {
    	if(code==null)
    		return "";
    	try {
            MessageSource messageSource = SpringUtil.getBean(MessageSource.class);
            return messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
		} catch (org.springframework.context.NoSuchMessageException e) {
			//log.error("code is not fund :{}",code);
			return code;
		}
    }
    
    public static String message(String code)
    {
    	if(code==null)
    		return "";
    	try {
            MessageSource messageSource = SpringUtil.getBean(MessageSource.class);
            return messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
		} catch (org.springframework.context.NoSuchMessageException e) {
			//log.error("code is not fund :{}",code);
			return code;
		}
    }
    
    public static String messageLocale(String code,Locale locale)
    {
    	if(code==null)
    		return "";
    	try {
            MessageSource messageSource = SpringUtil.getBean(MessageSource.class);
            return messageSource.getMessage(code, null, locale);
		} catch (org.springframework.context.NoSuchMessageException e) {
			//log.error("code is not fund :{}",code);
			return code;
		}
    }
    
    /**
     * 根据消息键和参数 获取消息 委托给spring messageSource
     *
     * @param code 消息键
     * @param args 参数
     * @return 获取国际化翻译值
     */
    public static String messageCH(String code)
    {
    	if(code==null)
    		return "";
        try {
            MessageSource messageSource = SpringUtil.getBean(MessageSource.class);
            return messageSource.getMessage(code, null, Locale.CHINESE);
		} catch (org.springframework.context.NoSuchMessageException e) {
			//log.error("code is not fund :{}",code);
			return code;
		}
    }
    public static String messageCH(String code,Object... args)
    {
    	if(code==null)
    		return "";
        try {
            MessageSource messageSource = SpringUtil.getBean(MessageSource.class);
            String[] split = code.split("aabbcc");
            code=split[0];
            String message = messageSource.getMessage(code, args, Locale.CHINESE);
            if (split.length>1){
            	message=message+split[1];
            }
            return message;
		} catch (org.springframework.context.NoSuchMessageException e) {
			//log.error("code is not fund :{}",code);
			return code;
		}
    }

    /**
     * 根据消息键和参数 获取消息 委托给spring messageSource
     *
     * @param code 消息键
     * @param args 参数
     * @return 获取国际化翻译值
     */
    public static String messageEN(String code)
    {
    	if(code==null)
    		return "";
        try {
            String[] split = code.split("aabbcc");
            code=split[0];
            MessageSource messageSource = SpringUtil.getBean(MessageSource.class);
            String message = messageSource.getMessage(code, null, Locale.ENGLISH);
            if (split.length>1){
                message=message+split[1];
            }
            return message;
		} catch (org.springframework.context.NoSuchMessageException e) {
			//log.error("code is not fund :{}",code);
			return code;
		}
    }
    public static String messageEN(String code,Object... args)
    {
    	if(code==null)
    		return "";
        try {
            MessageSource messageSource = SpringUtil.getBean(MessageSource.class);
            return messageSource.getMessage(code, args, Locale.ENGLISH);
		} catch (org.springframework.context.NoSuchMessageException e) {
			//log.error("code is not fund :{}",code);
			return code;
		}
    }
}
