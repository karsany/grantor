package hu.karsany.grantor;

/**
 * Created by fkarsany on 2015.04.21..
 */
public class ObjectGrant {
    private String tableName;
    private String grantSql;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getGrantSql() {
        return grantSql;
    }

    public void setGrantSql(String grantSql) {
        this.grantSql = grantSql;
    }

    public ObjectGrant(String tableName, String grantSql) {
        this.tableName = tableName;
        this.grantSql = grantSql;
    }
}
