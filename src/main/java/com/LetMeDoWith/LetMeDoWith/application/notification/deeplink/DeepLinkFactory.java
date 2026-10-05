package com.LetMeDoWith.LetMeDoWith.application.notification.deeplink;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import lombok.experimental.UtilityClass;

@UtilityClass
public class DeepLinkFactory {

    private static final String SCHEME = "letmedowith://";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    public static String home() {
        return SCHEME + "home";
    }

    public static String home(LocalDate date) {
        return SCHEME + "home?date=" + date.format(DATE_FORMATTER);
    }

    public static String feed() {
        return SCHEME + "feed";
    }

    public static String mypage() {
        return SCHEME + "mypage";
    }

    public static String feedback() {
        return SCHEME + "feedback";
    }

    public static String feedback(FeedbackTab tab) {
        return SCHEME + "feedback?tab=" + tab.getValue();
    }

    public static String realtimeNag() {
        return SCHEME + "realtime-nag";
    }

    public static String notification() {
        return SCHEME + "notification";
    }

    public static String myInfo() {
        return SCHEME + "myinfo";
    }

    public static String setting() {
        return SCHEME + "setting";
    }

    public static String settingMyInfo() {
        return SCHEME + "setting/myinfo";
    }

    public static String settingNotification() {
        return SCHEME + "setting/notification";
    }

    public static String settingNotice() {
        return SCHEME + "setting/notice";
    }

    public static String settingNoticeDetail(Long id) {
        return SCHEME + "setting/notice/detail?id=" + id;
    }

    public static String settingPolicy() {
        return SCHEME + "setting/policy";
    }

    public static String settingAccount() {
        return SCHEME + "setting/account";
    }

    public static String settingBadge() {
        return SCHEME + "setting/badge";
    }

    public static String receivedFeedback(Long dowithTaskId) {
        return SCHEME + "received-feedback?dowithTaskId=" + dowithTaskId;
    }

    public static String cheerCollection(Long dowithTaskId) {
        return SCHEME + "cheer-collection?dowithTaskId=" + dowithTaskId;
    }

    public enum FeedbackTab {
        RECEIVE("receive"),
        SEND("send");

        private final String value;

        FeedbackTab(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}
