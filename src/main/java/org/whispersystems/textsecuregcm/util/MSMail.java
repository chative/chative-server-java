package org.whispersystems.textsecuregcm.util;

import java.net.URI;
import java.net.URISyntaxException;

import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.enumeration.misc.ExchangeVersion;
import microsoft.exchange.webservices.data.core.service.item.EmailMessage;
import microsoft.exchange.webservices.data.credential.ExchangeCredentials;
import microsoft.exchange.webservices.data.credential.WebCredentials;
import microsoft.exchange.webservices.data.property.complex.MessageBody;

public class MSMail {
    private final String surl;
    private final String domain;
    private final String user;
    private final String password;
    private ExchangeService service;

    public MSMail(String surl, String domain, String user, String password) throws URISyntaxException {
        this.surl = surl;
        this.domain = domain;
        this.user = user;
        this.password = password;

        ExchangeService service = new ExchangeService(ExchangeVersion.Exchange2010_SP2);
        ExchangeCredentials credentials = new WebCredentials(this.user, this.password, this.domain);
        service.setCredentials(credentials);
        service.setUrl(new URI(this.surl));
    }

    public boolean send(String subject, String content) {
        try {
            EmailMessage msg = new EmailMessage(service);
            msg.setSubject(subject);
            msg.setBody(MessageBody.getMessageBodyFromText(content));
            msg.getToRecipients().add(user);
            msg.send();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}
