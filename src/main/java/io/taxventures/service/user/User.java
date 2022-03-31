package io.taxventures.service.user;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Document(collection = "user")
public class User implements UserDetails {

    @Id final String id;
    @Indexed(unique=true) final String email;
    final String password;
    final UserRole userRole;
    final Boolean isAdmin;
    final Boolean locked;
    final Boolean enabled;

    public User(String id,
                String email,
                String password,
                UserRole userRole,
                Boolean isAdmin,
                Boolean locked,
                Boolean enabled) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.userRole = userRole;
        this.isAdmin = isAdmin;
        this.locked = locked;
        this.enabled = enabled;
    }

    public User(String id,
                String email,
                String password) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.userRole = UserRole.USER;
        this.isAdmin = false;
        this.locked = false;
        this.enabled = true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !locked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (!id.equals(user.id)) return false;
        if (!email.equals(user.email)) return false;
        if (!password.equals(user.password)) return false;
        if (userRole != user.userRole) return false;
        if (!isAdmin.equals(user.isAdmin)) return false;
        if (!locked.equals(user.locked)) return false;
        return enabled.equals(user.enabled);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + email.hashCode();
        result = 31 * result + password.hashCode();
        result = 31 * result + userRole.hashCode();
        result = 31 * result + isAdmin.hashCode();
        result = 31 * result + locked.hashCode();
        result = 31 * result + enabled.hashCode();
        return result;
    }
}
