package config;

import org.aeonbits.owner.ConfigFactory;

public class DataBase {
  public static final DbConfig db = ConfigFactory.create(DbConfig.class, System.getProperties());
}
