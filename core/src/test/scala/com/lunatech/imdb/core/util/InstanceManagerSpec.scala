package com.lunatech.imdb.core.util

import com.lunatech.imdb.core.CoreTestServiceT

class InstanceManagerSpec extends CoreTestServiceT {

  object test extends InstanceManager[Int]{
    def init = {
      setInstance(2)
    }
  }

  "The Instance manager " must {
    "throw an error when its instance is set more than once" in {
     test.init //first time
     try{
       test.init //second time -> should throw an exception
     }catch {
       case ex : Exception => assert(ex.getMessage == "Instance already set")
     }
    }
    "throw an error no instance is set" in {
      try{
        test.getInstance
      }catch {
        case ex : Exception => assert(ex.getMessage == "Instance not set")
      }

    }
  }

}
