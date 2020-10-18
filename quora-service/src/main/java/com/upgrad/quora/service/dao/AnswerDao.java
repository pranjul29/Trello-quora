package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.AnswerEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Repository
public class AnswerDao {

    @PersistenceContext
    private EntityManager entityManager;

    public AnswerEntity createAnswer(AnswerEntity answerEntity) {

        entityManager.persist(answerEntity);
        return answerEntity;
    }

    public AnswerEntity getAnswerByUuid(String uuid) {

        try {
            return entityManager.createNamedQuery("getAnswerByUuid", AnswerEntity.class).setParameter("uuid", uuid).getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }

    public AnswerEntity editAnswer(AnswerEntity answerEntity) {
        return entityManager.merge(answerEntity);
    }
}
