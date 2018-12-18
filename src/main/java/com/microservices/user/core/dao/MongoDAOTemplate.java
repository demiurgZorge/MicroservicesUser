package com.microservices.user.core.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.microservices.user.core.interfaces.PagingState;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.CommandResult;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class MongoDAOTemplate<T> {
	
	@Autowired
	protected MongoOperations mongoOperations;

	protected Class<T> entityType;
	
	protected String idPropertyName;
	
	protected MongoQueryBuilder queryBuilder;
	
	//protected MongoStringQueryBuilder queryStringBuilder;

	private SequenceDao sequenceDao;

	public MongoDAOTemplate(Class<T> entityType, String idField) {
		this.entityType = entityType;
		this.idPropertyName = idField;
		this.queryBuilder = new MongoQueryBuilder();
		//this.queryStringBuilder = new MongoStringQueryBuilder();
	}
	
	public String getIdField() {
		return idPropertyName;
	}

	@PostConstruct
	public void init(){
		this.sequenceDao = new SequenceDao(mongoOperations, entityType.getSimpleName());
	}

	public Long getNextSequenceId() {
		return sequenceDao.getNextSequenceId();
	}
	
	public <K> Map<K,T> toMap(List<T> list, String keyField) {
	    return DataGlue.toMap(list, keyField);
	}
	
    public <K> Map<K,T> toMap(List<T> list) {
        return DataGlue.toMap(list, idPropertyName);
    }
	
	protected static List<String> objectToStr(Object[] list) {
		List<String> result = new ArrayList<String>();
		for(Object o : list) {
			String v = "";
			if (o instanceof String) {
				v = "'" + o.toString() + "'";
			}
			else {
				v = o.toString();
			}
			result.add(v);
		}
		return result;
	}
	
	protected static <K extends Object> List<K> convertDBList(MongoOperations mongo, Class<K> klass, List<DBObject> dbList) {
		List<K> result = new ArrayList<K>();
		if (dbList != null) {
			for(DBObject obj : dbList) {
				K t =  mongo.getConverter().read(klass, obj);
				result.add(t);
			}
		}
		return result;
	}
	
	public <K> List<K> getIdList(List<T> list) {
		return DataGlue.getFiledFromList(list, entityType, idPropertyName);
	}
	
	@SuppressWarnings("unchecked")
	protected List<T> readListResult(CommandResult listResult) {
		return convertDBList((List<DBObject>)listResult.get("result"));
	}
	
	protected List<T> convertDBList(List<DBObject> dbList) {
		return MongoDAOTemplate.convertDBList(mongoOperations, entityType, dbList);
	}
	
	@SuppressWarnings("unchecked")
	protected List<DBObject> aggregate(String pipelineStr) {
		BasicDBList pipeline = (BasicDBList)com.mongodb.util.JSON.parse(pipelineStr);
		String colName = mongoOperations.getCollectionName(entityType);
		BasicDBObject aggregation = new BasicDBObject("aggregate",colName).append("pipeline",pipeline);
		CommandResult commandResult = mongoOperations.executeCommand(aggregation);
		return (List<DBObject>) commandResult.get("result");
	}
	
	protected List<DBObject> aggregate(String match, String group, String project) {
		String pipelineStr = "[ " + match + ", " + group + ", " + project + " ]";
		return aggregate(pipelineStr);
	}
	
	protected List<DBObject> aggregate(String[] pipeline) {
		String pipelineStr = "[" + StringUtils.join(pipeline, ",") + "]";
		return aggregate(pipelineStr);
	}
	
	public <K> List<T> getByList(String field, List<K> list) {	
		Query query = new Query();
		query.addCriteria(Criteria.where(field).in(list));
		return mongoOperations.find(query, entityType);
	}
	
    public <K> List<T> getByList(String field, Set<K> list) {  
        Query query = new Query();
        query.addCriteria(Criteria.where(field).in(list));
        return mongoOperations.find(query, entityType);
    }
	
	public <K> List<T> getByIdList(List<K> list) {	
		return getByIdList(list, null);
	}
	
    public <K> List<T> getByIdList(List<K> list, PagingState paging) {  
        Query query = new Query();
        query.addCriteria(Criteria.where(idPropertyName).in(list));
        queryBuilder.addPaging(query, paging);
        return mongoOperations.find(query, entityType);
    }
	
    public <K> List<T> getByIdList(Set<K> list) {  
        Query query = new Query();
        query.addCriteria(Criteria.where(idPropertyName).in(list));
        return mongoOperations.find(query, entityType);
    }
	
    public List<T> query(QuerySpecification spec) {
        Query query = new Query();
        queryBuilder.buildCriteria(spec, query);
        return mongoOperations.find(query, entityType);
    }
    
    public long getRecordCount(final QuerySpecification spec) {
        Query query = new Query();
        queryBuilder.buildCriteria(spec, query);
        return mongoOperations.count(query, entityType);
    }
    
	public List<T> query(Query query) {
		return mongoOperations.find(query, entityType);
	}
	
    public T queryFirst(Query query) {
        List<T> list = query(query);
        return (list.size() > 0) ? list.get(0) : null;
    }
	
	protected DBCursor queryDbCursor(Query query) {
	    String collectionName = mongoOperations.getCollectionName(entityType);
	    DBCollection collection = mongoOperations.getCollection(collectionName);
	    return collection.find(query.getQueryObject());
	}
	
	protected List<DBObject> queryDbObject(Query query) {
	    return queryDbCursor(query).toArray();
	}
		
	protected String buildMatch(String field, String value) {
		return String.format("{ $match: { %s : %s }}", field, value);
	}
	
	
	protected String buildMatch(String field, Object[] list) {
		String[] params = objectToStr(list).toArray(new String[0]);  
		String args = StringUtils.join(params, ",");
		String inClause = String.format("{ $in: [ %s ] }", args);
		return buildMatch(field, inClause);	
	}
	
	protected String buildMatchRegex(String field, String regex) {
		return buildMatch(field, String.format("{$regex: '%s'}", regex));
	}
	
	protected String buildGroupByLast(String field, String[] groupFields) {
		StringBuilder fields = new StringBuilder();
		for(String f: groupFields) {
			String groupFieldPart = String.format("%s: { $last: '$%s' }", f, f);
			fields.append(groupFieldPart);
			fields.append(',');
		}
		fields.delete(fields.length()-1, fields.length());
		
		String group = String.format(
				"{ $group: { _id: '$%s', __id: { $last: '$_id' }, %s }}", 
				field, fields.toString());
		return group;
	}
	
	protected String buildProjection(String idField, String[] projectionFields) {
		StringBuilder fields = new StringBuilder();
		for(String f: projectionFields) {
			fields.append(String.format("%s: 1", f));
			fields.append(',');
		}
		fields.delete(fields.length()-1, fields.length());
		
		return String.format(
				"{ $project: { %s: '$_id', _id: '$__id', %s }}",
				idField, fields.toString());
	}
	
	public List<T> getByIdListLastAdded(String field, Integer[] list, String[] groupFields) {
		return convertDBList(
			aggregate(
				buildMatch(field, list), 
				buildGroupByLast(field, groupFields),
				buildProjection(field, groupFields)
		));
	}
	
	public List<T> getAll() {
		return mongoOperations.findAll(entityType);
	}
	
	public <K> T getById(K id) {
		Query query = new Query();
		query.addCriteria(Criteria.where(idPropertyName).is(id));
		List<T> list = mongoOperations.find(query, entityType);
		return (list.size() > 0) ? list.get(0) : null;
	}
	
	public void saveOrUpdate(T entity) {
		mongoOperations.save(entity);
	}
	
	public void remove(T entity) {
		mongoOperations.remove(entity);
	}
}
