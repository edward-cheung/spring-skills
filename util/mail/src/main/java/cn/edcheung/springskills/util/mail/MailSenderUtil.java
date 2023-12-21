package cn.edcheung.springskills.util.mail;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 * Description MailUtil
 *
 * @author Edward Cheung
 * @date 2020/10/10
 * @since JDK 1.8
 */
@Component
@SuppressWarnings("unused")
public class MailSenderUtil {

    private static final Logger logger = LoggerFactory.getLogger(MailSenderUtil.class);

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String sender;

    /**
     * 发送邮件测试方法
     */
    public void sendMail() {
        SimpleMailMessage mimeMessage = new SimpleMailMessage();
        mimeMessage.setFrom(sender);
        mimeMessage.setTo(sender);
        mimeMessage.setSubject("SpringBoot集成JavaMail实现邮件发送");
        mimeMessage.setText("SpringBoot集成JavaMail实现邮件发送正文");
        mailSender.send(mimeMessage);
    }

    /**
     * 发送简易邮件
     *
     * @param mailBean 邮件实体
     */
    @Async
    public void sendMail(MailBean mailBean) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
        try {
            helper.setFrom(sender);
            helper.setTo(sender);
            helper.setSubject(mailBean.getSubject());
            helper.setText(mailBean.getText());
        } catch (MessagingException e) {
            logger.error("发送简易邮件异常", e);
            //WXRobotUtil.send(ExceptionUtil.stacktraceToString(e, 2000));
        }
        mailSender.send(mimeMessage);
    }

    /**
     * 发送邮件-邮件正文是HTML
     *
     * @param mailBean 邮件实体
     */
    @Async
    public void sendMailHtml(MailBean mailBean) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
        try {
            helper.setFrom(sender);
            helper.setTo(sender);
            helper.setSubject(mailBean.getSubject());
            helper.setText(mailBean.getText(), true);
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            logger.error("发送HTML邮件异常", e);
            //WXRobotUtil.send(ExceptionUtil.stacktraceToString(e, 2000));
        }
    }

    /**
     * 发送邮件-附件邮件
     *
     * @param mailBean 邮件实体
     */
    @Async
    public void sendMailAttachment(MailBean mailBean) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setFrom(sender);
            helper.setTo(sender);
            helper.setSubject(mailBean.getSubject());
            helper.setText(mailBean.getText(), true);
            // 增加附件名称和附件
            helper.addAttachment(mailBean.getAttachmentFilename(), mailBean.getFile());
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            logger.error("发送附件邮件异常", e);
//            WXRobotUtil.send(ExceptionUtil.stacktraceToString(e, 2000));
        }
    }

    /**
     * 内联资源（静态资源）邮件发送
     * 由于邮件服务商不同，可能有些邮件并不支持内联资源的展示
     * 在测试过程中，新浪邮件不支持，QQ邮件支持
     * 不支持不意味着邮件发送不成功，而且内联资源在邮箱内无法正确加载
     *
     * @param mailBean 邮件实体
     */
    @Async
    public void sendMailInline(MailBean mailBean) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setFrom(sender);
            helper.setTo(sender);
            helper.setSubject(mailBean.getSubject());
            /*
             * 内联资源邮件需要确保先设置邮件正文，再设置内联资源相关信息
             */
            helper.setText(mailBean.getText(), true);
            helper.addInline(mailBean.getContentId(), mailBean.getFile());
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            logger.error("发送内联资源邮件异常", e);
            //WXRobotUtil.send(ExceptionUtil.stacktraceToString(e, 2000));
        }
    }

    /**
     * 模板邮件发送
     *
     * @param mailBean 邮件实体
     */
    @Async
    public void sendMailTemplate(MailBean mailBean) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setFrom(sender);
            helper.setTo(sender);
            helper.setSubject(mailBean.getSubject());
            helper.setText(mailBean.getText(), true);
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            logger.error("发送模板邮件异常", e);
            //WXRobotUtil.send(ExceptionUtil.stacktraceToString(e, 2000));
        }
    }
}
