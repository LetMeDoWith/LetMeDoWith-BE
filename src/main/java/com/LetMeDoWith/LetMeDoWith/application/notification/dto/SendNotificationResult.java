package com.LetMeDoWith.LetMeDoWith.application.notification.dto;

import java.util.Set;

public record SendNotificationResult(Set<String> succeededMemberIds, Set<String> failedMemberIds) {}
