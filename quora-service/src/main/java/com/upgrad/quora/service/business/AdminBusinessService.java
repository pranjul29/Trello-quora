package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.AdminDao;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminBusinessService {

  @Autowired private AdminDao adminDao;

  public void userDelete(final String userUuid, final String authorizationToken)
      throws UserNotFoundException, AuthorizationFailedException {
    UserAuthEntity userAuthEntity = adminDao.getUserAuthToken(authorizationToken);
    if (userAuthEntity == null) {
      throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
    }
    String role = userAuthEntity.getUser().getRole();
    if (role.equals("admin")) {
      if (userAuthEntity.getLogoutAt() != null) {
        throw new AuthorizationFailedException("ATHR-002", "User is signed out");
      }
      adminDao.userDelete(userUuid);
    } else {
      throw new AuthorizationFailedException(
          "ATHR-003", "Unauthorized Access, Entered user is not an admin");
    }
  }
}
