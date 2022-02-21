package cn.edcheung.springskills.util.mail;

import org.springframework.core.io.FileSystemResource;

/**
 * Description 邮件实体类
 *
 * @author Edward Cheung
 * @date 2020/10/10
 * @since JDK 1.8
 */
public class MailBean {
    /**
     * 邮件主题
     */
    private String subject;

    /**
     * 邮件内容
     */
    private String text;

    /**
     * 附件
     */
    private FileSystemResource file;

    /**
     * 附件名称
     */
    private String attachmentFilename;

    /**
     * 内容ID，用于发送静态资源邮件时使用
     */
    private String contentId;

    public static MailBean getMailBean() {
        return new MailBean();
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public FileSystemResource getFile() {
        return file;
    }

    public void setFile(FileSystemResource file) {
        this.file = file;
    }

    public String getAttachmentFilename() {
        return attachmentFilename;
    }

    public void setAttachmentFilename(String attachmentFilename) {
        this.attachmentFilename = attachmentFilename;
    }

    public String getContentId() {
        return contentId;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
    }
}
