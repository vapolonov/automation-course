package config;

import lombok.Builder;
import org.aeonbits.owner.Config;

@Config.Sources({"classpath:config-${env}.properties"})
public interface EnvConfig extends Config {
  @Key("baseUrl")
  String getBaseUrl();

  @Key("username")
  String getUsername();

  @Key("password")
  String getPassword();

  @Key("timeout")
  int getTimeout();
}
