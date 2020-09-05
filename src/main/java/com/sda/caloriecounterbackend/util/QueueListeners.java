package com.sda.caloriecounterbackend.util;

import com.sda.caloriecounterbackend.dto.MailDataDto;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class QueueListeners {

    private final CustomMailSender customMailSender;

    public QueueListeners(CustomMailSender customMailSender) {
        this.customMailSender = customMailSender;
    }

    @RabbitListener(queues = "mailQueue")
    public void registerUserMailSender(MailDataDto mailDataDto) {
        this.customMailSender.sendMail(mailDataDto.getTo(), mailDataDto.getTopic(), mailDataDto.getContent());
    }
}
