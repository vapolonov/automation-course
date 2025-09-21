package config;

import org.aeonbits.owner.ConfigFactory;

public class Env {
  public static final EnvConfig env = ConfigFactory.create(EnvConfig.class, System.getProperties());
}
