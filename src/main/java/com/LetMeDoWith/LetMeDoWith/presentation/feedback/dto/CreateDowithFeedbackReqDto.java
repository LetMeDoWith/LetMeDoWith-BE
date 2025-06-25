package com.LetMeDoWith.LetMeDoWith.presentation.feedback.dto;

public record CreateDowithFeedbackReqDto(
    Long dowithTaskId,
    String senderId,
    Long taskFeedbackTemplateId) {
    // This record class is used to encapsulate the request data for creating a feedback
    // It includes the dowithTaskId, senderId, and taskFeedbackTemplateId
    
}