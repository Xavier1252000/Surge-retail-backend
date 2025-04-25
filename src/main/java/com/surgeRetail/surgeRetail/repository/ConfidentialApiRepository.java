package com.surgeRetail.surgeRetail.repository;

import com.surgeRetail.surgeRetail.document.master.RoleMaster;
import com.surgeRetail.surgeRetail.document.store.Store;
import com.surgeRetail.surgeRetail.document.userAndRoles.ClientDetails;
import com.surgeRetail.surgeRetail.document.userAndRoles.User;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.time.Instant;
import java.util.List;

@Repository
public class ConfidentialApiRepository {

    private final MongoTemplate mongoTemplate;


    public ConfidentialApiRepository(MongoTemplate mongoTemplate){
        this.mongoTemplate = mongoTemplate;
    }

    public List<Store> findAllStores() {
        return mongoTemplate.findAll(Store.class);
    }

    public List<Store> getStoresByClientId(String clientId) {
        Query query = new Query();
        Criteria criteria = Criteria.where("clientId").is(clientId).and("active").is(true);
        query.addCriteria(criteria);
        return mongoTemplate.find(query, Store.class);
    }

    public List<Store> getStoreByStoreAdminId(String storeAdminId) {
        return mongoTemplate.find(new Query(Criteria.where("storeAdminId").is(storeAdminId)), Store.class);
    }

    public RoleMaster saveRoleMaster(RoleMaster roleMaster) {
        return mongoTemplate.save(roleMaster);
    }

    public List<User> getAllUsers(Integer index, Integer itemPerIndex, List<String> userIds, List<String> roles, Boolean active, Instant fromDate, Instant toDate) {
        Query query = new Query();
        Criteria criteria = new Criteria();
        if (!CollectionUtils.isEmpty(userIds)){
            criteria = criteria.and("id").in(userIds);
        }
        if (!CollectionUtils.isEmpty(roles)){
            criteria = criteria.and("roles").in(roles);
        }
        if (active != null){
            criteria = criteria.and("active").is(active);
        }

        if (fromDate != null && toDate != null){
            criteria = criteria.and("createdOn").gte(fromDate).lte(toDate);
        }

        if (index != null && itemPerIndex != null) {
            Pageable pageable = PageRequest.of(index, itemPerIndex);
            query.with(pageable);
        }

        query.addCriteria(criteria);
        return mongoTemplate.find(query, User.class);
    }

    public ClientDetails saveClientDetails(ClientDetails cd) {
        return mongoTemplate.save(cd);
    }

    public User findUserRegAsClientByUserId(String userId) {
        Query query = new Query();
        Criteria criteria = Criteria.where("id").is(userId).and("roles").in(User.USER_ROLE_CLIENT);
        query.addCriteria(criteria);
        return mongoTemplate.findOne(query, User.class);
    }
}
