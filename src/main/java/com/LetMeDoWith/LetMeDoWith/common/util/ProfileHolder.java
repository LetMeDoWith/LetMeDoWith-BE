package com.LetMeDoWith.LetMeDoWith.common.util;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class ProfileHolder {

    public static String activeProfile;

    public ProfileHolder(Environment env) {
        activeProfile = env.getActiveProfiles().length > 0 ? env.getActiveProfiles()[0] : "default";
    }
}
