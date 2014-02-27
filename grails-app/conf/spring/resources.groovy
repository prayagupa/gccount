import eccount.config.AbstractConfManager

// Place your Spring DSL code here
beans = {

    confManager(AbstractConfManager){ bean->

        qualifier="" //qualifier used for index in ES

    }

}
