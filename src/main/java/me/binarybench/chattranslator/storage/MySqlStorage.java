package me.binarybench.chattranslator.storage;

import me.binarybench.chattranslator.api.LangStorage;

import javax.sql.DataSource;
import java.sql.*;
import java.util.UUID;

/**
 * Created by Bench on 5/17/2016.
 */
public class MySqlStorage implements LangStorage {

    //TODO thread pooling

    private static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS `player_lang` (`uuid` VARCHAR(36) NOT NULL, `lang` VARCHAR(5) NOT NULL, PRIMARY KEY (`uuid`));";
    private static final String QUERY = "SELECT * FROM `player_lang` WHERE uuid=?;";
    private static final String UPDATE = "INSERT INTO `player_lang` (uuid, lang) values (?, ?) ON DUPLICATE KEY UPDATE lang=values(lang);";

    private DataSource dataSource;

    private String url;
    private String user;
    private String pass;

    public MySqlStorage(String url, String user, String pass)
    {
        this.url = url;
        this.user = user;
        this.pass = pass;
        createTable();
    }

    public void createTable()
    {
        Connection con = null;
        try
        {
            con = getConnection();
            PreparedStatement sql = con.prepareStatement(CREATE_TABLE);
            sql.executeUpdate();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (con != null)
                try
                {
                    con.close();
                } catch (SQLException e)
                {
                    e.printStackTrace();
                }
        }
    }

    public void setLang(UUID playersUUID, String lang)
    {
        Connection con = null;
        try
        {
            con = getConnection();

            PreparedStatement sql = con.prepareStatement(UPDATE);

            sql.setString(1, playersUUID.toString());
            sql.setString(2, lang);
            sql.executeUpdate();

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (con != null)
                try
                {
                    con.close();
                } catch (SQLException e)
                {
                    e.printStackTrace();
                }
        }

    }

    public String getLang(UUID playersUUID)
    {
        Connection con = null;
        ResultSet resultSet = null;
        try
        {
            con = getConnection();

            PreparedStatement sql = con.prepareStatement(QUERY);
            sql.setString(1, playersUUID.toString());
            resultSet = sql.executeQuery();

            if (resultSet.next())
                return resultSet.getString("lang");
            return null;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (resultSet != null)
                try
                {
                    resultSet.close();
                } catch (SQLException e)
                {
                    e.printStackTrace();
                }
            if (con != null)
                try
                {
                    con.close();
                } catch (SQLException e)
                {
                    e.printStackTrace();
                }

        }

        return null;
    }

    public Connection getConnection() throws SQLException
    {
        return DriverManager.getConnection(url, user, pass);
    }
}
