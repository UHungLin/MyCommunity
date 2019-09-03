package fun.linyuhong.myCommunity.util;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Controller
public class MailClient {

    private static final Logger logger = LoggerFactory.getLogger(MailClient.class);


    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String from;  // 发送者

    /**
     *
     * @param to  收件者
     * @param subject  发送主题
     * @param content  发送内容
     */
    public void sendMail(String to, String subject, String content){
        try{
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            // MimeMessageHelper 是帮忙构建 MimeMessage 对象, 因为JavaMailSender的发送需要 MimeMessage 对象
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
            messageHelper.setFrom(from);
            messageHelper.setTo(to);
            messageHelper.setSubject(subject);
            messageHelper.setText(content, true); // 不加true是以纯文本形式发送
            javaMailSender.send(messageHelper.getMimeMessage());
        }catch (MessagingException e){
            logger.error("邮件发送失败: " + e.getMessage());
        }
    }

}
