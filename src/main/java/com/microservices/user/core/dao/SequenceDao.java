package com.microservices.user.core.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.microservices.user.core.dao.exceptions.DaoException;
import com.microservices.user.core.dao.exceptions.ErrorCodeEnum;


public class SequenceDao {

    protected static final Logger logger = LoggerFactory.getLogger(SequenceDao.class);

    public enum Error implements ErrorCodeEnum {
        KEY_IS_NULL("key is null"),
        ERROR_CREATE_KEY("Error create key in sequence_table");

        private final String text;

        private Error(final String text) {
            this.text = text;
        }

        @Override
        public String code() {
            return this.name();
        }

        @Override
        public String toString() {
            return text;
        }
    }

    public MongoOperations mongoOperations;
    private String key;
    private SequenceId seqId;

    public SequenceDao(MongoOperations mongoOperations, String key) {
        if(key == null){
            throw DaoException.create(logger, Error.KEY_IS_NULL);
        }
        this.mongoOperations = mongoOperations;
        this.key = key;
        initValue();
    }

    public void initValue(){

        this.seqId = this.mongoOperations.findById(this.key, SequenceId.class);

        if(this.seqId == null) {
            try {
                if (!this.mongoOperations.collectionExists("sequence_table")) {
                    this.mongoOperations.createCollection("sequence_table");
                }

                this.seqId = new SequenceId();
                this.seqId.setId(this.key);
                this.seqId.setSeq(0);

                this.mongoOperations.insert(this.seqId);
            } catch (Exception e) {
                throw DaoException.create(logger, Error.ERROR_CREATE_KEY);
            }
        }
    }

    public long getNextSequenceId()  {

        Query query = new Query(Criteria.where("_id").is(this.key));
        Update update = new Update();
        update.inc("seq", 1);

        FindAndModifyOptions options = new FindAndModifyOptions();
        options.returnNew(true);

        this.seqId = this.mongoOperations.findAndModify(query, update, options, SequenceId.class);
        return this.seqId.getSeq();
    }

}
