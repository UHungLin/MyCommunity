package fun.linyuhong.myCommunity.util;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author linyuhong
 * @date 2019/9/2
 */
public class HttpUtil {
    public static String sendGet(String url) {
        String resultUrl = "";
        try {

            //发送get请求
            URL serverUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) serverUrl.openConnection();
            conn.setRequestMethod("GET");
            //必须设置false，否则会自动redirect到重定向后的地址
            conn.setInstanceFollowRedirects(false);
            conn.addRequestProperty("Accept-Charset", "UTF-8;");
            conn.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.2.8) Firefox/3.6.8");
            conn.addRequestProperty("Referer", "http://matols.com/");
            conn.connect();

            //判定是否会进行302重定向
            if (conn.getResponseCode() == 302) {
                //如果会重定向，保存302重定向地址，以及Cookies,然后重新发送请求(模拟请求)
                resultUrl = conn.getHeaderField("Location");

                return resultUrl;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        // 出现网络状况，统一获取同一张图片
        resultUrl = "http://cdn.u2.huluxia.com/g3/M00/27/CD/wKgBOVwJgW-ASQo9AACvmQ_XQ_k733.jpg";
        return resultUrl;
    }

}