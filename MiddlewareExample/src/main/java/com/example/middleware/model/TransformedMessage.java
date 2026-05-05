package com.example.middleware.model;

/**
 * Mensaje ya transformado y listo para ser enviado por un canal específico.
 * Producido por MessageTransformerService y consumido por DispatcherService.
 */
public class TransformedMessage {

    private NotificationChannel channel;
    private String recipient;   // email address | phone number | device token
    private String subject;
    private String body;

    public TransformedMessage() {}

    public TransformedMessage(NotificationChannel channel, String recipient,
                              String subject, String body) {
        this.channel   = channel;
        this.recipient = recipient;
        this.subject   = subject;
        this.body      = body;
    }

    public NotificationChannel getChannel()             { return channel; }
    public void setChannel(NotificationChannel channel) { this.channel = channel; }

    public String getRecipient()                        { return recipient; }
    public void setRecipient(String recipient)          { this.recipient = recipient; }

    public String getSubject()                          { return subject; }
    public void setSubject(String subject)              { this.subject = subject; }

    public String getBody()                             { return body; }
    public void setBody(String body)                    { this.body = body; }

    @Override
    public String toString() {
        return "TransformedMessage{channel=" + channel +
               ", recipient='" + recipient +
               "', subject='" + subject + "'}";
    }
}
