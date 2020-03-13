package com.churchclerk.demoapi;

import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DemoResourceSpec implements Specification<DemoEntity> {

    private Demo criteria = null;

    /**
     * @param criteria
     */
    public DemoResourceSpec(Demo criteria) {
        this.criteria = criteria;
    }

    @Override
    public Predicate toPredicate(Root<DemoEntity> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<Predicate>();

        addPredicate(criteriaBuilder, root, "id", criteria.getId(), predicates);
        addPredicate(criteriaBuilder, root, "active", criteria.isActive(), predicates);

        if (predicates.isEmpty()) {
            return null;
        }

        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }

    private void addPredicate(CriteriaBuilder criteriaBuilder, Root<DemoEntity> root, String field, String value, List<Predicate> predicates) {
        Predicate predicate = null;

        if (value != null) {
            if (value.trim().isEmpty()) {
                predicate = criteriaBuilder.isEmpty(root.get(field));
            } else if (value.contains("%")) {
                predicate = criteriaBuilder.like(root.get(field), value);
            } else {
                predicate = criteriaBuilder.equal(root.get(field), value);
            }
        }

        if (predicate != null) {
            predicates.add(predicate);
        }
    }

    private void addPredicate(CriteriaBuilder criteriaBuilder, Root<DemoEntity> root, String field, Boolean value, List<Predicate> predicates) {
        Predicate predicate = null;

        if (value != null) {
            predicate = criteriaBuilder.equal(root.get(field), value);
        }

        if (predicate != null) {
            predicates.add(predicate);
        }
    }

    private void addPredicate(CriteriaBuilder criteriaBuilder, Root<DemoEntity> root, String field, UUID value, List<Predicate> predicates) {
        Predicate predicate = null;

        if (value != null) {
            predicate = criteriaBuilder.equal(root.get(field), value.toString());
        }

        if (predicate != null) {
            predicates.add(predicate);
        }
    }
}
