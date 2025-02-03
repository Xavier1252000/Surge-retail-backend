package com.surgeRetail.surgeRetail.repository;

import com.surgeRetail.surgeRetail.document.SuperAdminInfo;
import com.surgeRetail.surgeRetail.document.User;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public class PublicApiRepository {

    private final MongoTemplate mongoTemplate;

    public PublicApiRepository(MongoTemplate mongoTemplate){
        this.mongoTemplate = mongoTemplate;
    }

    public User findUserByUsernameOrEmail(String username) {
        Query query = new Query();
        Criteria criteria = new Criteria().orOperator(Criteria.where("username").is(username), Criteria.where("emailId").is(username));

        return mongoTemplate.findOne(query.addCriteria(criteria), User.class);
    }

    public User findUserByEmailID(String emailId) {
        Query query = new Query();
        Criteria emailCriteria = Criteria.where("emailId").is(emailId);
        return mongoTemplate.findOne(query.addCriteria(emailCriteria), User.class);
    }

    public User findUserByUsername(String emailId) {
        Query query = new Query();
        Criteria usernameCriteria = Criteria.where("username").is(emailId);
        return mongoTemplate.findOne(query.addCriteria(usernameCriteria), User.class);
    }

    public User findUserByMobileNo(String mobileNo){
        Query query = new Query();
        Criteria mobileNoCriteria = Criteria.where("mobileNo").is(mobileNo);
        return mongoTemplate.findOne(query.addCriteria(mobileNoCriteria), User.class);
    }

    public boolean userExistByUsername(String username) {
        Query query = new Query();
        Criteria usernameCriteria = Criteria.where("username").is(username);
        return mongoTemplate.exists(query.addCriteria(usernameCriteria), User.class);
    }

    public boolean userExistByEmailId(String emailId) {
        Query query = new Query();
        Criteria emailCriteria = Criteria.where("emailId").is(emailId);
        return mongoTemplate.exists(query.addCriteria(emailCriteria), User.class);
    }

    public boolean userExistByMobileNo(String mobileNo) {
        Query query = new Query();
        Criteria mobileNoCriteria = Criteria.where("mobileNo").is(mobileNo);
        return mongoTemplate.exists(query.addCriteria(mobileNoCriteria), User.class);
    }

    public User findSuperAdmin() {
        Query query = new Query();
        Criteria criteria = Criteria.where("roles").is(User.USER_ROLE_SUPER_ADMIN);
        return mongoTemplate.findOne(query.addCriteria(criteria), User.class);

    }

    public boolean anySuperAdminExists() {
        Query query = new Query();
        Criteria criteria = Criteria.where("roles").is(User.USER_ROLE_SUPER_ADMIN);
        return mongoTemplate.exists(query.addCriteria(criteria), User.class);
    }

    public User save(User user) {
        return mongoTemplate.save(user);
    }

    public void saveSuperAdminInfo(SuperAdminInfo superAdminInfo) {
        mongoTemplate.save(superAdminInfo);
    }
}
