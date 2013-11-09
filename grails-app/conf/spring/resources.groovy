import eccount.config.AbstractConfManager

// Place your Spring DSL code here
beans = {

    confManager(AbstractConfManager){ bean->
        hBaseQualifier="" //qualifier used for hBase tables

        qualifier="" //qualifier used for index in ES

        hBaseTables=["Customer","Transaction"]
    }

}
