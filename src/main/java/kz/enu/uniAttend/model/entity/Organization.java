package kz.enu.uniAttend.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class Organization {

    @Id
    @Column(name = "bin")
    private String bin;

    @Column(name = "organization_name")
    private String organizationName;

    @Column(name = "email")
    private String email;

    @Column(name = "owner_name")
    private String ownerName;

    @Column(name = "phone_number")
    private String phoneNumber;
    @Column(name = "website_link")
    private String websiteLink;
    @Column(name = "address")
    private String address;
}
