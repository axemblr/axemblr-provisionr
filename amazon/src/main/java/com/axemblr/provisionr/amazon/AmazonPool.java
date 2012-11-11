package com.axemblr.provisionr.amazon;

import com.axemblr.provisionr.api.Pool;

public class AmazonPool implements Pool {

  public void init() {
    System.out.println("Test **********************************************");
  }

  @Override
  public void provision() {
    System.out.println("Provision - ");
  }
}
