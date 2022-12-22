package com.churchclerk.demoapi;

import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
public class DemoResourceSpec implements Specification<DemoEntity> {

    private Demo criteria = null;

    @Override
    public Predicate toPredicate(Root<DemoEntity> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<Predicate>();

        addPredicate(criteriaBuilder, root, "id", criteria.getId(), predicates);
        addPredicate(criteriaBuilder, root, "active", criteria.isActive(), predicates);
        addPredicate(criteriaBuilder, root, "testData", criteria.getTestData(), predicates);

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
