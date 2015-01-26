package com.wangyeming.Help;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

/**
 * 汉子转拼音
 *
 * @author 王小明
 * @date 2015/01/25
 */
public class HanZiToPinYin {

    /**
     * 返回姓名拼音的大写首字母
     *
     * @return String;
     */
    public static String getFirstPinyin(String hanziStr) {
        char hanzi = hanziStr.charAt(0);
        String pinyin = null;
        String[] pinyinArray = null;
        try {
            //是否在汉字范围内
            if (hanzi >= 0x4e00 && hanzi <= 0x9fa5) {
                HanyuPinyinOutputFormat hanyuPinyin = new HanyuPinyinOutputFormat();
                hanyuPinyin.setCaseType(HanyuPinyinCaseType.UPPERCASE);
                hanyuPinyin.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
                hanyuPinyin.setVCharType(HanyuPinyinVCharType.WITH_U_UNICODE);
                pinyinArray = PinyinHelper.toHanyuPinyinStringArray(hanzi, hanyuPinyin);
                pinyin = pinyinArray[0].substring(0,1);
            } else {
                pinyin =  hanziStr.toUpperCase();
            }
        } catch (BadHanyuPinyinOutputFormatCombination e) {
            e.printStackTrace();
        }
        return pinyin;
    }

    /**
     * 返回姓名拼音
     *
     * @return String;
     */
    public static String getPinyin(String hanziStr) {
        String namePinyin = "";
        for(int i=0;i<hanziStr.length();i++) {
            char hanzi = hanziStr.charAt(i);
            String pinyin = null;
            String[] pinyinArray = null;
            try {
                //是否在汉字范围内
                if (hanzi >= 0x4e00 && hanzi <= 0x9fa5) {
                    HanyuPinyinOutputFormat hanyuPinyin = new HanyuPinyinOutputFormat();
                    hanyuPinyin.setCaseType(HanyuPinyinCaseType.UPPERCASE);
                    hanyuPinyin.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
                    hanyuPinyin.setVCharType(HanyuPinyinVCharType.WITH_U_UNICODE);
                    pinyinArray = PinyinHelper.toHanyuPinyinStringArray(hanzi, hanyuPinyin);
                    pinyin = pinyinArray[0];
                } else {
                    pinyin = String.valueOf(hanzi);
                }
            } catch (BadHanyuPinyinOutputFormatCombination e) {
                e.printStackTrace();
            }
            namePinyin += pinyin;
        }
        return namePinyin;
    }

}
