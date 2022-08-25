package com.kdt.lecture.domain.parent;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.IdClass;

@Entity
//@IdClass(ParentId.class)
@Getter
@Setter
public class Parent {

    @EmbeddedId
    private ParentId id;
}
