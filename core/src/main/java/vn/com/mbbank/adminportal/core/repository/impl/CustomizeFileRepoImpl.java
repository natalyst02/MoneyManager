package vn.com.mbbank.adminportal.core.repository.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import vn.com.mbbank.adminportal.core.model.entity.FileEntity;
import vn.com.mbbank.adminportal.core.model.request.FileInput;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Repository
@RequiredArgsConstructor
@Log4j2
public class CustomizeFileRepoImpl {
    private final EntityManager entityManager;

    public List<FileEntity> getUserFile(FileInput input, String username, PageRequest of) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<FileEntity> cq = cb.createQuery(FileEntity.class);
        Root<FileEntity> root = cq.from(FileEntity.class);

        List<Predicate> predicates = new ArrayList<>();

        if (input.getFileName() != null && !input.getFileName().isEmpty()) {
            predicates.add(cb.like(root.get("fileName"), "%" + input.getFileName() + "%"));
        }

        if (username != null && !username.isEmpty()) {
                predicates.add(cb.equal(root.get("userName"), username));
        }

        if (input.getFromDate() != null && !input.getFromDate().isEmpty()) {
            LocalDate fromDate = LocalDate.parse(input.getFromDate());
            predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), Date.from(fromDate.atStartOfDay(ZoneId.systemDefault()).toInstant())));
        }
        if (input.getToDate() != null && !input.getToDate().isEmpty()) {
            LocalDate toDate = LocalDate.parse(input.getToDate());
            predicates.add(cb.lessThan(root.get("createdAt"), Date.from(toDate.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant())));
        }

        cq.where(predicates.toArray(new Predicate[0]));
        return entityManager.createQuery(cq)
                .setFirstResult(of.getPageNumber() * of.getPageSize())
                .setMaxResults(of.getPageSize())
                .getResultList();

    }



    public long countUserFile(FileInput input, String userName) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<FileEntity> root = cq.from(FileEntity.class);

        List<Predicate> predicates = new ArrayList<>();

        if (input.getFileName() != null && !input.getFileName().isEmpty()) {
            predicates.add(cb.like(root.get("fileName"), "%" + input.getFileName() + "%"));
        }
        if (input.getFromDate() != null && !input.getFromDate().isEmpty()) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), java.sql.Date.valueOf(input.getFromDate())));
        }
        if (input.getToDate() != null && !input.getToDate().isEmpty()) {
            predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), java.sql.Date.valueOf(input.getToDate())));
        }

        cq.select(cb.count(root)).where(predicates.toArray(new Predicate[0]));
        return entityManager.createQuery(cq).getSingleResult();
    }
}
