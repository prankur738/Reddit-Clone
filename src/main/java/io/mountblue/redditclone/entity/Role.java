package io.mountblue.redditclone.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.GrantedAuthority;


@Entity
@Getter @Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "roles")
public class Role implements GrantedAuthority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Integer id;

    @Column(name = "role")
    String role;

    public Role(String role) {
        this.role = role;
    }

    @Override
    public String getAuthority() {
        return this.role;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;

        if (other instanceof Role) {
            Role otherRole = (Role) other;
            return this.role.equals(otherRole.getAuthority());
        }

        return false;
    }

    @Override
    public int hashCode() {
        return this.role.hashCode();
    }
}
