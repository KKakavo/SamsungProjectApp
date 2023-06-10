package com.samsung.samsungproject.feature.leaderboard.presentation;

public class LeaderboardUtils {
    public static String format(long number){
        String[] chars = String.valueOf(number).split("");
        StringBuilder builder = new StringBuilder();
        for(int i = chars.length - 1; i >= 0; i--){
            if((chars.length - i - 1) % 3 == 0)
                builder.append(" ");
            builder.append(chars[i]);
        }
        return builder.reverse().toString();
    }
}
