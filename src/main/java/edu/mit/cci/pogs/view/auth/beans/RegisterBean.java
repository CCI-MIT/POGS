package edu.mit.cci.pogs.view.auth.beans;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Objects;

public class RegisterBean {

    private String emailAddress;
    private String firstName;
    private String lastName;
    private String password;

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RegisterBean)) {
            return false;
        }
        RegisterBean that = (RegisterBean) o;
        return Objects.equals(getEmailAddress(), that.getEmailAddress()) && Objects
                .equals(getFirstName(), that.getFirstName()) && Objects
                .equals(getLastName(), that.getLastName()) && Objects
                .equals(getPassword(), that.getPassword());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getEmailAddress(), getFirstName(), getLastName(), getPassword());
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("emailAddress", emailAddress)
                                        .append("firstName", firstName)
                                        .append("lastName", lastName)
                                        .append("password", password).toString();
    }
}
