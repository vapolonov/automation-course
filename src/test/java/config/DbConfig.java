package config;

import org.aeonbits.owner.Config;

@Config.Sources({"classpath:dbconfig.properties"})
public interface DbConfig extends Config {
  @Key("db.url")
  @DefaultValue("jdbc:postgresql://localhost:5432/test_db")
  String getDbUrl();

  @Key("db.user")
  @DefaultValue("admin")
  String getDbUser();

  @Key("db.password")
  @DefaultValue("secret")
  String getDbPass();
}
