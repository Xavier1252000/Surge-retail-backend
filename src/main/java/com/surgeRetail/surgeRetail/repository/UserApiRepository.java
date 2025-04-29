package com.surgeRetail.surgeRetail.repository;

import com.surgeRetail.surgeRetail.document.master.Roles;
import com.surgeRetail.surgeRetail.document.userAndRoles.ClientDetails;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public class UserApiRepository {
    private final MongoTemplate mongoTemplate;

    public UserApiRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }


    public Boolean existRoleByRoleName(Set<String> roles) {
        for (String s:roles){
            Query query = new Query();
            query.addCriteria(Criteria.where("role").is(s));
            if (!mongoTemplate.exists(query, Roles.class)) {
                System.out.println(false);
                return false;
            }
        }
        return true;
    }

    public ClientDetails saveClient(ClientDetails client) {
        return mongoTemplate.save(client);
    }

    public ClientDetails saveClientDetails(ClientDetails cd) {
        return mongoTemplate.save(cd);
    }

    public ClientDetails getClientWithHighestNumId() {
        Query query = new Query();
        query.with(Sort.by(Sort.Direction.DESC, "numericId"));
        query.limit(1);
        return mongoTemplate.findOne(query, ClientDetails.class);
    }

    public ClientDetails findClientByUserId(String userId) {
        Query query = new Query();
        Criteria criteria = Criteria.where("userId").is(userId);
        query.addCriteria(criteria);
        return mongoTemplate.findOne(query, ClientDetails.class);
    }

    public ClientDetails getClientByUserId(String userId) {
        return mongoTemplate.findOne(new Query(Criteria.where("userId").is(userId)), ClientDetails.class);
    }
}
