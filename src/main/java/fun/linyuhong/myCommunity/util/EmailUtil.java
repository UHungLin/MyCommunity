package fun.linyuhong.myCommunity.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author linyuhong
 * @date 2019/9/2
 */
public class EmailUtil {

    public static boolean isEmail(String string) {
        if (string == null)
            return false;
//        String regEx1 = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        String regEx1 = "\\w[-\\w.+]*@([A-Za-z0-9][-A-Za-z0-9]+\\.)+[A-Za-z]{2,14}";
        Pattern p;
        Matcher m;
        p = Pattern.compile(regEx1);
        m = p.matcher(string);
        if (m.matches())
            return true;
        else
            return false;
    }

}
