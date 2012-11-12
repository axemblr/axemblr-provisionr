package com.axemblr.provisionr.client;

import com.axemblr.provisionr.api.Pool;

public class ProvisionrClient {

  private Pool poolService = null;

  public void startup() {
    System.out.println("Inside startup");
    poolService.provision();
  }

  public Pool getPoolService() {
    return poolService;
  }

  public void setPoolService(Pool poolService) {
    this.poolService = poolService;
  }
}
