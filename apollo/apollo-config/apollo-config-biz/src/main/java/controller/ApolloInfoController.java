package controller;

import framework.apollo.Apollo;
import framework.foundation.Foundation;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/apollo")
public class ApolloInfoController {

  @RequestMapping("app")
  public String getApp() {
    return Foundation.app().toString();
  }

  @RequestMapping("net")
  public String getNet() {
    return Foundation.net().toString();
  }

  @RequestMapping("server")
  public String getServer() {
    return Foundation.server().toString();
  }

  @RequestMapping("version")
  public String getVersion() {
    return Apollo.VERSION;
  }
}