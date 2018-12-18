package com.microservices.user.core.dao;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import com.microservices.user.core.interfaces.FilterEnum;
import com.microservices.user.core.interfaces.QueryFilter;

public abstract class HibernateCriteriaQueryFilter implements QueryFilter {

    private String    name;

    private FieldType type;

    public abstract Criterion build(Object value);

    @Override
    public String getName() {
        return name;
    }

    @Override
    public FieldType getFieldType() {
        return type;
    }

    public HibernateCriteriaQueryFilter(String name) {
        this.name = name;
    }

    public HibernateCriteriaQueryFilter(FilterEnum filter) {
        this.name = filter.toString();
    }

    public static Criterion buildIntFilter(String fieldName, Object value) {
        try {
            String strValue = value.toString();
            Integer intValue = Integer.parseInt(strValue);
            return Restrictions.eq(fieldName, intValue);
        }
        catch (NumberFormatException ex) {
            return null;
        }
    }
    
    public static Criterion buildNumericFilter(String fieldName, Object value) {
        try {
            String strValue = value.toString();
            BigDecimal numericValue = new BigDecimal(strValue);
            return Restrictions.eq(fieldName, numericValue);
        }
        catch (NumberFormatException ex) {
            return null;
        }
    }

    public static Criterion buildLongFilter(String fieldName, Object value) {
        try {
            String strValue = value.toString();
            Long intValue = Long.parseLong(strValue);
            return Restrictions.eq(fieldName, intValue);
        }
        catch (NumberFormatException ex) {
            return null;
        }
    }

    public static Disjunction or(final List<String> fields, final Object value, Class<?> klass) {
        Disjunction or = Restrictions.or();
        for (String fieldName : fields) {
            Field fieldType = DataGlue.getField(klass, fieldName);
            if (fieldType == null) {
                continue;
            }
            if (fieldType.getType().isAssignableFrom(String.class)) {
                String strValue = value.toString();
                or.add(Restrictions.ilike(fieldName, strValue, MatchMode.ANYWHERE));
            }
            if (fieldType.getType().isAssignableFrom(Integer.class)) {
                Criterion cr = buildIntFilter(fieldName, value);
                if (cr != null) {
                    or.add(cr);
                }
            }
        }
        return or;
    }

    public static HibernateCriteriaQueryFilter ilike(FilterEnum filter, final String expression) {
        return ilike(filter.toString(), expression);
    }
    
    public static HibernateCriteriaQueryFilter ilike(String name, final String expression) {
        return new HibernateCriteriaQueryFilter(name) {
            
            @Override
            public Criterion build(Object value) {
                return Restrictions.ilike(expression, value.toString(), MatchMode.ANYWHERE);
            }
        };
    }
    
    public static HibernateCriteriaQueryFilter eq(FilterEnum filter, final String expression, final FieldType type) {
        return eq(filter.toString(), expression, type);
    }

    public static HibernateCriteriaQueryFilter eq(final String name, final String expression, final FieldType type) {

        return new HibernateCriteriaQueryFilter(name) {
            @Override
            public Criterion build(Object value) {
                return Restrictions.eq(expression, FieldType.convertType(value, type));
            }

        };
    }

    public static HibernateCriteriaQueryFilter ne(FilterEnum filter, final String expression, final FieldType type) {
        return ne(filter.toString(), expression, type);
    }

    public static HibernateCriteriaQueryFilter ne(final String name, final String expression, final FieldType type) {

        return new HibernateCriteriaQueryFilter(name) {
            @Override
            public Criterion build(Object value) {
                return Restrictions.ne(expression, FieldType.convertType(value, type));
            }

        };
    }

    public static HibernateCriteriaQueryFilter ge(FilterEnum filter, final String expression, final FieldType type) {
        return ge(filter.toString(), expression, type);
    }

    public static HibernateCriteriaQueryFilter ge(final String name, final String expression, final FieldType type) {
        return new HibernateCriteriaQueryFilter(name) {
            @Override
            public Criterion build(Object value) {
                return Restrictions.ge(expression, FieldType.convertType(value, type));
            }

        };
    }

    public static HibernateCriteriaQueryFilter gt(FilterEnum filter, final String expression, final FieldType type) {
        return gt(filter.toString(), expression, type);
    }

    public static HibernateCriteriaQueryFilter gt(final String name, final String expression, final FieldType type) {
        return new HibernateCriteriaQueryFilter(name) {
            @Override
            public Criterion build(Object value) {
                return Restrictions.gt(expression, FieldType.convertType(value, type));
            }

        };
    }

    public static HibernateCriteriaQueryFilter lt(FilterEnum filter, final String expression, final FieldType type) {
        return lt(filter.toString(), expression, type);
    }

    public static HibernateCriteriaQueryFilter le(FilterEnum filter, final String expression, final FieldType type) {
        return le(filter.toString(), expression, type);
    }

    public static HibernateCriteriaQueryFilter le(final String name, final String expression, final FieldType type) {
        return new HibernateCriteriaQueryFilter(name) {
            @Override
            public Criterion build(Object value) {
                return Restrictions.le(expression, FieldType.convertType(value, type));
            }
        };
    }

    public static HibernateCriteriaQueryFilter lt(final String name, final String expression, final FieldType type) {
        return new HibernateCriteriaQueryFilter(name) {
            @Override
            public Criterion build(Object value) {
                return Restrictions.lt(expression, FieldType.convertType(value, type));
            }
        };
    }

    public static HibernateCriteriaQueryFilter search(FilterEnum filter, List<String> fields, Class<?> klass) {
        return search(filter.toString(), fields, klass);
    }

    public static HibernateCriteriaQueryFilter search(final String name,
                                                      final List<String> fields,
                                                      final Class<?> klass) {
        return new HibernateCriteriaQueryFilter(name) {

            @Override
            public Criterion build(Object value) {
                return or(fields, value, klass);
            }

        };
    }

    public static HibernateCriteriaQueryFilter in(FilterEnum filter, String expression, final FieldType type) {
        return in(filter.toString(), expression, type);
    }

    public static HibernateCriteriaQueryFilter in(final String name, final String expression, final FieldType type) {
        return new HibernateCriteriaQueryFilter(name) {

            @Override
            public Criterion build(Object value) {
                return Restrictions.in(expression, FieldType.convertList(name, value, type));
            }
        };
    }

    public static HibernateCriteriaQueryFilter fromCriterion(FilterEnum filter, Criterion cr) {
        return fromCriterion(filter.toString(), cr);
    }

    public static HibernateCriteriaQueryFilter fromCriterion(final String name, final Criterion cr) {
        return new HibernateCriteriaQueryFilter(name) {
            @Override
            public Criterion build(Object value) {
                return cr;
            }
        };
    }
    
    public static HibernateCriteriaQueryFilter active() {
        return eq("isActive", "isActive", FieldType.BOOL);
    }
}
