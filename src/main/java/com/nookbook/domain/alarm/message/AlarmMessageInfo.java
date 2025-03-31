package com.nookbook.domain.alarm.message;

import java.util.Map;

public record AlarmMessageInfo(
        AlarmMessageTemplate template,
        Map<String, String> args
) {}