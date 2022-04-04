package io.taxventures.service.user;

public record UserPrincipal(String userId, String username, boolean isAdmin){}