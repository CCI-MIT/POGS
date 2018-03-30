package edu.mit.cci.pogs.config;

import edu.mit.cci.pogs.model.dao.UserDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.AuthUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

@Service
public class AuthUserDetailsService implements UserDetailsService {

    private UserDao userDao;

    @Autowired
    public AuthUserDetailsService(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return new AuthUserDetails(userDao.get(username));
    }

    public static class AuthUserDetails implements UserDetails {

        private final AuthUser authUser;
        transient private Set<GrantedAuthority> authorities;

        public AuthUserDetails(AuthUser authUser) {
            this.authUser = authUser;
        }

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            if (authorities == null) {
                authorities = Collections.unmodifiableSet(getGrantedAuthoritiesForUser(authUser));
            }
            return null;
        }

        private Set<GrantedAuthority> getGrantedAuthoritiesForUser(AuthUser authUser) {
            // Ensure array iteration order is predictable (as per
            // UserDetails.getAuthorities() contract and SEC-717)
            SortedSet<GrantedAuthority> sortedAuthorities = new TreeSet<>(new AuthorityComparator());
            sortedAuthorities.add(new SimpleGrantedAuthority("ROLE_MEMBER"));
            return sortedAuthorities;
        }

        @Override
        public String getPassword() {
            return authUser.getPassword();
        }

        @Override
        public String getUsername() {
            return authUser.getEmailAddress();
        }

        @Override
        public boolean isAccountNonExpired() {
            return true;
        }

        @Override
        public boolean isAccountNonLocked() {
            return true;
        }

        @Override
        public boolean isCredentialsNonExpired() {
            return true;
        }

        @Override
        public boolean isEnabled() {
            return true;
        }
    }

    static class AuthorityComparator implements Comparator<GrantedAuthority>, Serializable {

        private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

        @Override
        public int compare(GrantedAuthority g1, GrantedAuthority g2) {
            // Neither should ever be null as each entry is checked before adding it to
            // the set.
            // If the authority is null, it is a custom authority and should precede
            // others.
            if (g2.getAuthority() == null) {
                return -1;
            }

            if (g1.getAuthority() == null) {
                return 1;
            }

            return g1.getAuthority().compareTo(g2.getAuthority());
        }
    }
}
