package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.AnswerDao;
import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AnswerBusinessService {

  @Autowired UserDao userDao;

  @Autowired AnswerDao answerDao;

  @Autowired QuestionDao questionDao;

  @Transactional(propagation = Propagation.REQUIRED)
  public AnswerEntity createAnswer(String questionUuid, AnswerEntity answerEntity, String token)
      throws AuthorizationFailedException, InvalidQuestionException {

    UserAuthEntity userAuth = userDao.getUserAuthToken(token);
    if (userAuth == null) {
      throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
    }

    if (userAuth.getLogoutAt() != null && userAuth.getLogoutAt().isAfter(userAuth.getLoginAt())) {
      throw new AuthorizationFailedException(
          "ATHR-002", "User is signed out.Sign in first to post an answer");
    }

    QuestionEntity questionEntity = questionDao.getQuestionByUuid(questionUuid);
    if (questionEntity == null) {
      throw new InvalidQuestionException("QUES-001", "The question entered is invalid");
    }

    answerEntity.setDate(LocalDateTime.now());
    answerEntity.setQuestion(questionEntity);
    answerEntity.setUser(userAuth.getUser());
    return answerDao.createAnswer(answerEntity);
  }

  @Transactional(propagation = Propagation.REQUIRED)
  public AnswerEntity editAnswer(String answerUuid, String content, String token)
      throws AuthorizationFailedException, AnswerNotFoundException {

    UserAuthEntity userAuth = userDao.getUserAuthToken(token);
    if (userAuth == null) {
      throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
    }

    if (userAuth.getLogoutAt() != null && userAuth.getLogoutAt().isAfter(userAuth.getLoginAt())) {
      throw new AuthorizationFailedException(
          "ATHR-002", "User is signed out.Sign in first to edit the answer");
    }

    AnswerEntity answerEntity = answerDao.getAnswerByUuid(answerUuid);

    if (answerEntity == null) {
      throw new AnswerNotFoundException("ANS-001", "Entered answer uuid does not exist");
    }

    if (!answerEntity.getUser().getUuid().equals(userAuth.getUser().getUuid())) {
      throw new AuthorizationFailedException(
          "ATHR-003", "Only the answer owner can edit the answer");
    }
    answerEntity.setAnswer(content);
    answerEntity.setDate(LocalDateTime.now());
    return answerDao.editAnswer(answerEntity);
  }

  public void deleteAnswer(final String answerUuid, final String authorizationToken)
      throws AuthorizationFailedException, AnswerNotFoundException {
    UserAuthEntity userAuthEntity = userDao.getUserAuthToken(authorizationToken);

    if (userAuthEntity == null) {
      throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
    }

    if (userAuthEntity.getLogoutAt() != null) {
      throw new AuthorizationFailedException(
          "ATHR-002", "User is signed out.Sign in first to delete an answer");
    }

    if (answerDao.getAnswerByUuid(answerUuid) == null) {
      throw new AnswerNotFoundException("ANS-001", "Entered answer uuid does not exist");
    }

    if (userAuthEntity.getUser().getRole().equals("admin")) {
      answerDao.deleteAnswer(answerUuid);
    } else if (userAuthEntity
        .getUser()
        .getId()
        .equals(answerDao.getAnswerByUuid(answerUuid).getUser().getId())) {
      answerDao.deleteAnswer(answerUuid);
    } else {
      throw new AuthorizationFailedException(
          "ATHR-003", "Only the answer owner or admin can delete the answer");
    }
  }

  public List<AnswerEntity> getAllAnswersToQuestion(
      final String questionID, final String authorizationToken)
      throws AuthorizationFailedException, InvalidQuestionException {
    UserAuthEntity userAuthEntity = userDao.getUserAuthToken(authorizationToken);

    if (userAuthEntity == null) {
      throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
    }

    if (userAuthEntity.getLogoutAt() != null) {
      throw new AuthorizationFailedException(
          "ATHR-002", "User is signed out.Sign in first to get the answers");
    }

    if (questionDao.getQuestionByUuid(questionID) == null) {
      throw new InvalidQuestionException(
          "QUES-001", "The question with entered uuid whose details are to be seen does not exist");
    }
    return answerDao.getAllAnswersToQuestion(questionID);
  }
}
