package hu.karsany.grantor;

import hu.karsany.grantor.util.jdbc.JdbcTemplate;
import hu.karsany.grantor.util.jdbc.RowMapper;
import oracle.jdbc.pool.OracleDataSource;

import javax.sql.DataSource;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) throws SQLException, IOException {
        if (args.length != 3) {
            System.out.println("Usage: grantor.jar <url> <user> <password>");
            System.exit(0);
        }

        String URL = args[0];
        String userName = args[1];
        String password = args[2];

        DataSource ods = getDataSource(URL, userName, password);
        List<ObjectGrant> tableNameList = getGrants(ods);
        processGrants(tableNameList);

    }

    private static OracleDataSource getDataSource(String URL, String userName, String password) throws SQLException {
        OracleDataSource ods = new OracleDataSource();
        ods.setURL(URL);
        ods.setUser(userName);
        ods.setPassword(password);
        return ods;
    }

    private static void processGrants(List<ObjectGrant> tableNameList) throws IOException {
        String currentFile = "";
        Writer writer = null;
        for (ObjectGrant og : tableNameList) {
            if (!currentFile.equals(og.getTableName())) {
                if (writer != null) {
                    writer.flush();
                    writer.close();
                }
                currentFile = og.getTableName();
                writer = new FileWriter(currentFile.toUpperCase() + ".sql");
            }

            writer.write(og.getGrantSql() + "\r\n");
        }
        if (writer != null) {
            writer.flush();
            writer.close();
        }
    }

    private static List<ObjectGrant> getGrants(DataSource ods) {
        return new JdbcTemplate(ods).query("SELECT T.TABLE_NAME,\n"
                + "       'GRANT ' || LISTAGG(PRIVILEGE, ', ') WITHIN GROUP(ORDER BY 1) || ' ON ' || TABLE_NAME || ' TO ' || GRANTEE ||CASE\n"
                + "         WHEN GRANTABLE =\n"
                + "              'YES' THEN\n"
                + "          ' WITH GRANT OPTION'\n"
                + "         ELSE\n"
                + "          NULL\n"
                + "       END || ';' GRANT_SQL\n"
                + "  FROM USER_TAB_PRIVS_MADE T\n"
                + " GROUP BY TABLE_NAME,\n"
                + "          GRANTEE,\n"
                + "          GRANTABLE\n"
                + " ORDER BY TABLE_NAME", new RowMapper<ObjectGrant>() {
            @Override
            public ObjectGrant mapRow(ResultSet resultSet, int i) throws SQLException {
                return new ObjectGrant(resultSet.getString("TABLE_NAME"), resultSet.getString("GRANT_SQL"));
            }
        });
    }
}
