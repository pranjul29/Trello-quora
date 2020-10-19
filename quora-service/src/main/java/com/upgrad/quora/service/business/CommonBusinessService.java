package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.CommonDao;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommonBusinessService {

  @Autowired private CommonDao commonDao;

  public UserEntity getUserProfile(final String userUuid, final String authorizationToken)
      throws UserNotFoundException, AuthorizationFailedException {
    UserAuthEntity userAuthEntity = commonDao.getUserAuthToken(authorizationToken);
    if (userAuthEntity == null) {
      throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
    }
    if (userAuthEntity.getLogoutAt() != null) {
      throw new AuthorizationFailedException(
          "ATHR-002", "User is signed out.Sign in first to get user details");
    }
    UserEntity userEntity = commonDao.getUserProfile(userUuid);
    if (userEntity == null) {
      throw new UserNotFoundException("USR-001", "User with entered uuid does not exist");
    }
    return userEntity;
  }
}
