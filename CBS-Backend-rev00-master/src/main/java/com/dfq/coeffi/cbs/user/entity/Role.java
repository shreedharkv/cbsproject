/**
 *
 */
package com.dfq.coeffi.cbs.user.entity;
/**
 * @author H Kapil Kumar
 */

import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;

@Setter
@Getter
@Entity
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column
    private long id;

    @Column
    private String name;

    @Column
    private String groupCode;

    @Column
    private Boolean active;

    Role() {
    }

    public Role(long id, String name) {
        super();
        this.id = id;
        this.name = name;
        this.groupCode = groupCode;
        this.active = active;
    }
}