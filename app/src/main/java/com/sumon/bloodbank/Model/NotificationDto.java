package com.sumon.bloodbank.Model;

public class NotificationDto {

    public Notification notification;
    public Notification data;
    public String to;

    public NotificationDto(Notification notification, String topic) {
        this.notification = notification;
        this.data = notification;
        this.to = "/topics/"+topic;
    }
}
