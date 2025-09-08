package config;

import org.aeonbits.owner.Config;

@Config.Sources({"classpath:config-${env}.properties"})
public interface StatusConfig extends Config {
  @Key("baseUrl")
  String getBaseUrl();

  @Key("username")
  String getUsername();

  @Key("password")
  String getPassword();

  @Key("timeout")
  int getTimeout();
}
