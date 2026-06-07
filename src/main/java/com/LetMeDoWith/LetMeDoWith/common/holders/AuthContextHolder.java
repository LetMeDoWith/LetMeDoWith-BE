package com.LetMeDoWith.LetMeDoWith.common.holders;

public class AuthContextHolder {

    private static final ThreadLocal<String> memberIdHolder = new ThreadLocal<>();

    public static String getMemberId() {
        return memberIdHolder.get();
    }

    public static void setMemberId(String memberId) {
        memberIdHolder.set(memberId);
    }

    public static void clearMemberIdHolder() {
        memberIdHolder.remove();
    }
}
