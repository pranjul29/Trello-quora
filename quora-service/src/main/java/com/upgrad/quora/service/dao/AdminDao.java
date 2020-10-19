package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

@Repository
public class AdminDao {

  @PersistenceContext private EntityManager entityManager;

  @Transactional
  public void userDelete(final String userUuid) throws UserNotFoundException {
    try {
      UserEntity userEntity =
          entityManager
              .createNamedQuery("userByUuid", UserEntity.class)
              .setParameter("uuid", userUuid)
              .getSingleResult();
      entityManager.remove(userEntity);
    } catch (NoResultException nre) {
      throw new UserNotFoundException(
          "USR-001", "User with entered uuid to be deleted does not exist");
    }
  }

  public UserAuthEntity getUserAuthToken(final String accessToken) {
    try {
      return entityManager
          .createNamedQuery("userAuthTokenByAccessToken", UserAuthEntity.class)
          .setParameter("accessToken", accessToken)
          .getSingleResult();
    } catch (NoResultException nre) {
      return null;
    }
  }
}
